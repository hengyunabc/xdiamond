package io.github.xdiamond.client;

import io.github.xdiamond.client.event.AllKeyListener;
import io.github.xdiamond.client.event.ConfigEvent;
import io.github.xdiamond.client.event.EventType;
import io.github.xdiamond.client.event.OneKeyListener;
import io.github.xdiamond.client.net.XDiamondClient;
import io.github.xdiamond.common.ResolvedConfigVO;
import io.github.xdiamond.common.util.ThreadFactoryBuilder;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
          "xdiamond-listener-thread-%d").setDaemon(true).build());

  Map<String, List<OneKeyListener>> oneKeyListenerMap =
      new ConcurrentHashMap<String, List<OneKeyListener>>();
  List<AllKeyListener> allKeyListenerList = new CopyOnWriteArrayList<AllKeyListener>();

  // default config path is: /home/username/.xidamond
  String localConfigPath = System.getProperty("user.home") + File.separator + ".xdiamond";

  volatile Map<String, ResolvedConfigVO> resolvedConfigVOMap = new HashMap<>();

  String groupId;
  String artifactId;
  String version;
  String profile;
  String secretKey;

  String serverHost;
  int serverPort = 5678;

  // 是否daemon线程
  boolean daemon = true;

  // 启动时，是否打印获取到的配置信息
  boolean bPrintConfigWhenBoot = true;
  // 获取到配置，是否同步到System Properties里
  boolean bSyncToSystemProperties = false;

  // 指数退避的方式增加
  boolean bBackOffRetryInterval = true;
  // 失败重试的次数
  int maxRetryTimes = Integer.MAX_VALUE;
  // 失败重试的时间间隔
  int retryIntervalSeconds = 5;
  // 最大的重试时间间隔
  int maxRetryIntervalSeconds = 2 * 60;

  // 当保存配置失败时，重试的最多次数
  int maxTrySaveTimes = 20;
  int trySaveIntervalMs = 100;

  XDiamondClient xDiamondClient;

  public XDiamondConfig() {}

  public XDiamondConfig(String serverHost, int serverPort, String groupId, String artifactId,
      String version, String profile, String secretKey) {
    this(serverHost, serverPort, groupId, artifactId, version, profile, secretKey, true);
  }

  public XDiamondConfig(String serverHost, int serverPort, String groupId, String artifactId,
	      String version, String profile, String secretKey, boolean daemon) {
	    super();
	    this.serverHost = serverHost;
	    this.serverPort = serverPort;
	    this.groupId = groupId;
	    this.artifactId = artifactId;
	    this.version = version;
	    this.profile = profile;
	    this.secretKey = secretKey;
	    this.daemon = daemon;
	  }

  public void init() {
    boolean bShouldLoadLocalConfig = true;
    xDiamondClient =
        new XDiamondClient(this, serverHost, serverPort, bBackOffRetryInterval, maxRetryTimes,
            retryIntervalSeconds, maxRetryIntervalSeconds, daemon);
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
          logger.info("load config from xdiamond server success. " + toProjectInfoString());
          bShouldLoadLocalConfig = false;

          if (bPrintConfigWhenBoot) {
            System.out.println(ResolvedConfigVO.toUTF8PropertiesString(resolvedConfigs, true));
          }
        }
      }
      if (!future.isSuccess()) {
        logger.error("can not load xdiamond config from server! " + toProjectInfoString(),
            future.cause());
      }
    } catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
      logger.error("load xdiamond config from server error! " + toProjectInfoString(), e);
    }
    // 如果没有从服务器加载到配置，则从本地的备份读取
    if (bShouldLoadLocalConfig) {
      try {
        List<ResolvedConfigVO> resolvedConfigVOList = loadLocalConfig();
        this.resolvedConfigVOMap = ResolvedConfigVO.listToMap(resolvedConfigVOList);
        logger.info("load xdiamond config " + toProjectInfoString() + " from localConfigPath:"
            + localConfigPath);

        if (bPrintConfigWhenBoot) {
          System.out.println(ResolvedConfigVO.toUTF8PropertiesString(resolvedConfigVOList, true));
        }
      } catch (IOException e) {
        throw new RuntimeException("load xdiamond localConfig error! " + toProjectInfoString(), e);
      }
    }

    timer.schedule(new GetConfigTask(this), 30 * 1000, 30 * 1000);
  }

  /**
   * only call by XDiamondClient，服务器通知Client配置有更新
   *
   * @return
   */
  public void notifyConfigChanged() {
    timer.schedule(new GetConfigTask(this), 0);
  }

  static class GetConfigTask extends TimerTask {
    XDiamondConfig xDiamondConfig;

    public GetConfigTask(XDiamondConfig xDiamondConfig) {
      this.xDiamondConfig = xDiamondConfig;
    }

    @Override
    public void run() {
      Future<List<ResolvedConfigVO>> future =
          xDiamondConfig.xDiamondClient.getConfigs(xDiamondConfig.groupId,
              xDiamondConfig.artifactId, xDiamondConfig.version, xDiamondConfig.profile,
              xDiamondConfig.secretKey);
      try {
        List<ResolvedConfigVO> resolvedConfigVOs = future.get(10, TimeUnit.SECONDS);
        xDiamondConfig.loadConfig(resolvedConfigVOs);
      } catch (ConnectException e) {
        // 对于连接错误，这里不打印错误信息，因为会在重试任务里打印
        return;
      } catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
        if (e instanceof ExecutionException) {
          if (e.getCause() instanceof ConnectException) {
            return;
          }
        }
        logger.error("timer to get xdiamond config error!", e);
      }
    }
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
      // 要先close再move，否则在windows下会提示文件被占用
      fos.close();

		int trySaveTimes = 0;
		while (true) {
			try {
				Files.move(tempFile.toPath(), new File(configFilePath).toPath(),
						StandardCopyOption.REPLACE_EXISTING);
				logger.debug("save xdiamond config file success. path:" + new File(configFilePath).getAbsolutePath());
				break;
			} catch (IOException e) {
				trySaveTimes++;
				logger.error("save xdiamond config file error! trySaveTimes:{}, path:{}", trySaveTimes, configFilePath, e);
				if (trySaveTimes > this.maxTrySaveTimes) {
					throw e;
				}

				try {
					Thread.sleep(this.trySaveIntervalMs);
				} catch (InterruptedException e1) {
					// ignore
				}
			}
		}

    } finally {
      if (fos != null) {
        fos.close();
        if(tempFile.exists()) {
        	tempFile.delete();
        }
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

  /**
   * 删除所有的OneKeyListener
   */
  public synchronized void clearOneKeyListener() {
    oneKeyListenerMap.clear();
  }

  public synchronized void addAllKeyListener(AllKeyListener listener) {
    allKeyListenerList.add(listener);
  }

  /**
   * 删除所有的AllKeyListener
   */
  public synchronized void clearAllKeyListener() {
    allKeyListenerList.clear();
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

  public boolean isbPrintConfigWhenBoot() {
    return bPrintConfigWhenBoot;
  }

  public void setbPrintConfigWhenBoot(boolean bPrintConfigWhenBoot) {
    this.bPrintConfigWhenBoot = bPrintConfigWhenBoot;
  }

  public boolean isbSyncToSystemProperties() {
    return bSyncToSystemProperties;
  }

  public void setbSyncToSystemProperties(boolean bSyncToSystemProperties) {
    this.bSyncToSystemProperties = bSyncToSystemProperties;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  public boolean isbBackOffRetryInterval() {
    return bBackOffRetryInterval;
  }

  public void setbBackOffRetryInterval(boolean bBackOffRetryInterval) {
    this.bBackOffRetryInterval = bBackOffRetryInterval;
  }

  public int getRetryIntervalSeconds() {
    return retryIntervalSeconds;
  }

  public void setRetryIntervalSeconds(int retryIntervalSeconds) {
    this.retryIntervalSeconds = retryIntervalSeconds;
  }

  public int getMaxRetryTimes() {
    return maxRetryTimes;
  }

  public void setMaxRetryTimes(int maxRetryTimes) {
    this.maxRetryTimes = maxRetryTimes;
  }

  public int getMaxRetryIntervalSeconds() {
    return maxRetryIntervalSeconds;
  }

  public void setMaxRetryIntervalSeconds(int maxRetryIntervalSeconds) {
    this.maxRetryIntervalSeconds = maxRetryIntervalSeconds;
  }

	public int getMaxTrySaveTimes() {
		return maxTrySaveTimes;
	}

	public void setMaxTrySaveTimes(int maxTrySaveTimes) {
		this.maxTrySaveTimes = maxTrySaveTimes;
	}

	public int getTrySaveIntervalMs() {
		return trySaveIntervalMs;
	}

	public void setTrySaveIntervalMs(int trySaveIntervalMs) {
		this.trySaveIntervalMs = trySaveIntervalMs;
	}

  private String toProjectInfoString() {
    return this.groupId + "|" + artifactId + "|" + version + "|" + profile;
  }
}
