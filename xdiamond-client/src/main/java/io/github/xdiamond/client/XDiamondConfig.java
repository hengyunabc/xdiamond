package io.github.xdiamond.client;

import io.github.xdiamond.client.event.AllKeyListener;
import io.github.xdiamond.client.event.ConfigEvent;
import io.github.xdiamond.client.event.EventType;
import io.github.xdiamond.client.event.OneKeyListener;
import io.github.xdiamond.client.net.XDiamondClient;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.xdiamond.common.ResolvedConfigVO;
import io.xdiamond.common.util.ThreadFactoryBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * @author hengyunabc
 * 
 */
public class XDiamondConfig {
  static private final Logger logger = LoggerFactory.getLogger(XDiamondConfig.class);

  Timer timer = new Timer("xdiamond-timer-getconfig", true);

  ExecutorService listenerExecutorService = Executors
      .newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(
          "xdiamond-listener-thread-%d").build());

  Map<String, List<OneKeyListener>> oneKeyListenerMap =
      new ConcurrentHashMap<String, List<OneKeyListener>>();
  List<AllKeyListener> allKeyListenerList = new CopyOnWriteArrayList<AllKeyListener>();

  // default config path is: /home/username/.xidamond
  String localConfigPath = System.getProperty("user.home") + File.separator + ".xdiamond";

  // volatile Map<String, ConfigVO> configMap = new HashMap<>();
  volatile Map<String, ResolvedConfigVO> resolvedConfigVOMap = new HashMap<>();

  String groupId;
  String artifactId;
  String version;
  String profile;
  String secretKey;

  String serverHost;
  int serverPort = 5678;

  // 启动时，是否打印获取到的配置信息
  boolean bPrintConfigWhenBoot = true;
  // 获取到配置，是否同步到System Properties里
  boolean bSyncToSystemProperties = false;

  XDiamondClient xDiamondClient;

  public XDiamondConfig() {}

  public XDiamondConfig(String serverHost, int serverPort, String groupId, String artifactId,
      String version, String profile, String secretKey) {
    super();
    this.serverHost = serverHost;
    this.serverPort = serverPort;
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.profile = profile;
    this.secretKey = secretKey;
  }

  public void init() {
    boolean bShouldLoadLocalConfig = true;
    xDiamondClient = new XDiamondClient(serverHost, serverPort);
    // 首先尝试连接服务器
    ChannelFuture future = xDiamondClient.init();
    try {
      // 如果连接服务器成功，则尝试获得配置
      boolean await = future.await(10, TimeUnit.SECONDS);
      if (await && future.isSuccess()) {
        Future<List<ResolvedConfigVO>> configFuture =
            xDiamondClient.getConfigs(groupId, artifactId, version, profile, secretKey);
        List<ResolvedConfigVO> resolvedConfigs = configFuture.get(10, TimeUnit.SECONDS);
        if (configFuture.isSuccess()) {
          loadConfig(resolvedConfigs);
          logger.info("load config from xdiamond server success.");
          bShouldLoadLocalConfig = false;

          if (bPrintConfigWhenBoot) {
            System.out.println(ResolvedConfigVO.toUTF8PropertiesString(resolvedConfigs, true));
          }
        }
      }
      if(!future.isSuccess()){
        logger.error("can not load xdiamond config from server!", future.cause());
      }
    } catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
      logger.error("load xdiamond config from server error!", e);
    }
    // 如果没有从服务器加载到配置，则从本地的备份读取
    if (bShouldLoadLocalConfig) {
      try {
        List<ResolvedConfigVO> resolvedConfigVOList = loadLocalConfig();
        this.resolvedConfigVOMap = ResolvedConfigVO.listToMap(resolvedConfigVOList);
        logger.info("load xdiamond config from localConfigPath:" + localConfigPath);

        if (bPrintConfigWhenBoot) {
          System.out.println(ResolvedConfigVO.toUTF8PropertiesString(resolvedConfigVOList, true));
        }
      } catch (IOException e) {
        throw new RuntimeException("load xdiamond localConfig error!", e);
      }
    }

    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        Future<List<ResolvedConfigVO>> future =
            xDiamondClient.getConfigs(groupId, artifactId, version, profile, secretKey);
        try {
          List<ResolvedConfigVO> resolvedConfigVOs = future.get(10, TimeUnit.SECONDS);
          loadConfig(resolvedConfigVOs);
        } catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
          logger.error("timer to get xdiamond config error!", e);
        }
      }
    }, 10 * 1000, 10 * 1000);
  }

  public void destory() {
    xDiamondClient.destory();
    timer.cancel();
  }

  public synchronized void loadConfig(List<ResolvedConfigVO> resolvedConfigVOs) throws IOException {
    // 先保存到本地
    saveLocalConfig(ResolvedConfigVO.toJSONString(resolvedConfigVOs));

    // 是否要设置到系统的Properties里
    if (bSyncToSystemProperties) {
      for (ResolvedConfigVO resolvedConfigVO : resolvedConfigVOs) {
        System.setProperty(resolvedConfigVO.getConfig().getKey(), resolvedConfigVO.getConfig()
            .getValue());
      }
    }

    // 再通知Listener
    Map<String, ResolvedConfigVO> oldResolvedConfigVOMap = this.resolvedConfigVOMap;
    this.resolvedConfigVOMap = ResolvedConfigVO.listToMap(resolvedConfigVOs);
    for (ResolvedConfigVO resolvedConfigVO : this.resolvedConfigVOMap.values()) {
      String key = resolvedConfigVO.getConfig().getKey();
      String value = resolvedConfigVO.getConfig().getValue();
      ResolvedConfigVO oldResolvedConfigVO = oldResolvedConfigVOMap.get(key);

      if (oldResolvedConfigVO == null) {
        // add event
        notifyListener(new ConfigEvent(key, value, null, EventType.ADD));
      } else if (!value.equals(oldResolvedConfigVO.getConfig().getValue())) {
        // update event
        notifyListener(new ConfigEvent(key, value, oldResolvedConfigVO.getConfig().getValue(),
            EventType.UPDATE));
      }

      // here, must delete
      oldResolvedConfigVOMap.remove(key);
    }

    // delete event
    for (ResolvedConfigVO resolvedConfigVO : oldResolvedConfigVOMap.values()) {
      notifyListener(new ConfigEvent(resolvedConfigVO.getConfig().getKey(), null, resolvedConfigVO
          .getConfig().getValue(), EventType.DELETE));
    }
  }

  private void notifyListener(final ConfigEvent event) {
    List<OneKeyListener> list = oneKeyListenerMap.get(event.getKey());
    if (list != null) {
      for (final OneKeyListener listener : list) {
        listenerExecutorService.submit(new Runnable() {
          @Override
          public void run() {
            try {
              listener.onConfigEvent(event);
            } catch (Throwable t) {
              logger.error("XDiamond Listener error!", t);
            }
          }
        });
      }
    }
    for (final AllKeyListener listener : allKeyListenerList) {
      listenerExecutorService.submit(new Runnable() {
        @Override
        public void run() {
          try {
            listener.onConfigEvent(event);
          } catch (Throwable t) {
            logger.error("XDiamond Listener error!", t);
          }
        }
      });
    }
  }

  private List<ResolvedConfigVO> loadLocalConfig() throws IOException {
    // TODO 改为用'|'来分隔？这样人眼容易查找
    String dir =
        localConfigPath + File.separator + groupId + File.separator + artifactId + File.separator
            + version + File.separator + profile;
    String filePath = dir + File.separator + "config.json";
    File file = new File(filePath);
    if (file.exists()) {
      FileInputStream fis = null;
      try {
        fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        String jsonConfigString = new String(data, "UTF-8");
        List<ResolvedConfigVO> resolvedConfigVOs =
            JSON.parseArray(jsonConfigString, ResolvedConfigVO.class);
        return resolvedConfigVOs;
      } finally {
        if (fis != null) {
          try {
            fis.close();
          } catch (IOException e) {
            throw new RuntimeException("close file error!" + filePath, e);
          }
        }
      }
    } else {
      logger.warn("can not found local xdiamond config file! filePath:" + filePath);
      return Collections.emptyList();
    }
  }

  private void saveLocalConfig(String jsonConfigString) throws IOException {
    String dirPath =
        localConfigPath + File.separator + groupId + File.separator + artifactId + File.separator
            + version + File.separator + profile;
    String configFilePath = dirPath + File.separator + "config.json";

    File dir = new File(dirPath);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    // 先保存到临时文件，再用renameTo 原子性地改名
    File tempFile = File.createTempFile("config", ".tmp", dir);

    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(tempFile);
      fos.write(jsonConfigString.getBytes("UTF-8"));
      fos.flush();
      tempFile.renameTo(new File(configFilePath));
    } finally {
      if (fos != null) {
        fos.close();
      }
    }
  }

  public synchronized void addOneKeyListener(String key, OneKeyListener listener) {
    List<OneKeyListener> list = oneKeyListenerMap.get(key);
    if (list == null) {
      List<OneKeyListener> newList = new CopyOnWriteArrayList<OneKeyListener>();
      newList.add(listener);
      oneKeyListenerMap.put(key, newList);
    }
  }

  public void addAllKeyListener(AllKeyListener listener) {
    allKeyListenerList.add(listener);
  }

  public String getProperty(String key) {
    ResolvedConfigVO resolvedConfigVO = resolvedConfigVOMap.get(key);
    if (resolvedConfigVO != null) {
      return resolvedConfigVO.getConfig().getValue();
    }
    return null;
  }

  public Properties getProperties() {
    Properties properties = new Properties();
    for (ResolvedConfigVO resolvedConfigVO : resolvedConfigVOMap.values()) {
      properties
          .put(resolvedConfigVO.getConfig().getKey(), resolvedConfigVO.getConfig().getValue());
    }
    return properties;
  }

  public String getServerHost() {
    return serverHost;
  }

  public void setServerHost(String serverHost) {
    this.serverHost = serverHost;
  }

  public int getServerPort() {
    return serverPort;
  }

  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }

  public String getLocalConfigPath() {
    return localConfigPath;
  }

  public void setLocalConfigPath(String localConfigPath) {
    this.localConfigPath = localConfigPath;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getProfile() {
    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }
}
