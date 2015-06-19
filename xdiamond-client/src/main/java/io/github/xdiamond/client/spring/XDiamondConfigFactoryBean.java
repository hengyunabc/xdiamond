package io.github.xdiamond.client.spring;

import io.github.xdiamond.client.XDiamondConfig;
import io.github.xdiamond.client.annotation.AllKeyListener;
import io.github.xdiamond.client.annotation.OneKeyListener;
import io.github.xdiamond.client.event.ObjectListenerMethodInvokeWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * 支持${value:default}方式的配置； 支持在locations参数里，配置properties； 优先从System.getPropeties()里加载配置；
 * 
 * @author hengyunabc
 *
 */
public class XDiamondConfigFactoryBean implements ApplicationContextAware,
    ApplicationListener<ContextStartedEvent>, PriorityOrdered, BeanFactoryPostProcessor,
    InitializingBean, FactoryBean<XDiamondConfig> {
  private static final Log logger = LogFactory.getLog(XDiamondConfigFactoryBean.class);
  PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}", ":", true);

  int order = 0;
  ApplicationContext context;

  boolean bScanListenerAnnotation = true;

  List<String> locations = Collections.emptyList();

  Properties properties = new Properties();

  String groupId;

  String artifactId;
  String version;
  String profile;
  String secretKey;

  String serverHost;
  String serverPort;

  XDiamondConfig xDiamondConfig;

  public void setOrder(int order) {
    this.order = order;
  }

  @Override
  public int getOrder() {
    return order;
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {

  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.context = applicationContext;
    for (String location : locations) {
      try {
        Resource[] resources = context.getResources(location);
        if (resources != null) {
          for (Resource resource : resources) {
            InputStream inputStream = null;
            try {
              inputStream = resource.getInputStream();
              properties.load(inputStream);
            } finally {
              if (inputStream != null) {
                inputStream.close();
              }
            }

          }
        }

      } catch (IOException e) {
        logger.error("can not load resources, location:" + location, e);
      }
    }

    /**
     * merge properties form System.getProperties();
     */
    Properties sysProperties = System.getProperties();
    for (Entry<Object, Object> entry : sysProperties.entrySet()) {
      properties.put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    xDiamondConfig = new XDiamondConfig();

    if (!StringUtils.isEmpty(groupId)) {
      groupId = helper.replacePlaceholders(groupId, properties);
      xDiamondConfig.setGroupId(groupId);
    }

    if (!StringUtils.isEmpty(artifactId)) {
      artifactId = helper.replacePlaceholders(artifactId, properties);
      xDiamondConfig.setArtifactId(artifactId);
    }

    if (!StringUtils.isEmpty(profile)) {
      profile = helper.replacePlaceholders(profile, properties);
      xDiamondConfig.setProfile(profile);
    }

    if (!StringUtils.isEmpty(version)) {
      version = helper.replacePlaceholders(version, properties);
      xDiamondConfig.setVersion(version);
    }

    if (secretKey != null) {
      secretKey = helper.replacePlaceholders(secretKey, properties);
      xDiamondConfig.setSecretKey(secretKey);
    }
    
    if (!StringUtils.isEmpty(serverHost)) {
      serverHost = helper.replacePlaceholders(serverHost, properties);
      xDiamondConfig.setServerHost(serverHost);
    }

    if (!StringUtils.isEmpty(serverPort)) {
      serverPort = helper.replacePlaceholders(serverPort, properties);
      xDiamondConfig.setServerPort(Integer.parseInt(serverPort));
    }

    xDiamondConfig.init();
  }

  private void scanListenerAnnotation() throws ClassNotFoundException, NoSuchMethodException {
    logger.info("scan XDiamond Listener Annotation...");
    Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(Service.class);
    for (Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
      Object object = entry.getValue();
      Method[] methods = ReflectionUtils.getAllDeclaredMethods(object.getClass());
      if (methods != null) {
        for (Method method : methods) {
          OneKeyListener oneKeyListener =
              AnnotationUtils.findAnnotation(method, OneKeyListener.class);
          if (oneKeyListener != null) {
            logger.debug("XDaimond add Annotation Method Listener, class:"
                + object.getClass().getName() + ", method:" + method.getName());
            ObjectListenerMethodInvokeWrapper wrapper = new ObjectListenerMethodInvokeWrapper();
            wrapper.setxDiamondConfig(xDiamondConfig);
            wrapper.setListenerClassName(io.github.xdiamond.client.event.OneKeyListener.class
                .getName());
            wrapper.setKey(oneKeyListener.key());
            wrapper.setTargetObject(object);
            wrapper.setTargetMethod(method.getName());
            wrapper.init();
          }

          AllKeyListener allKeyListener =
              AnnotationUtils.findAnnotation(method, AllKeyListener.class);
          if (allKeyListener != null) {
            logger.debug("XDaimond add Annotation Method Listener, class:"
                + object.getClass().getName() + ", method:" + method.getName());
            ObjectListenerMethodInvokeWrapper wrapper = new ObjectListenerMethodInvokeWrapper();
            wrapper.setxDiamondConfig(xDiamondConfig);
            wrapper.setListenerClassName(io.github.xdiamond.client.event.AllKeyListener.class
                .getName());
            wrapper.setKey(oneKeyListener.key());
            wrapper.setTargetObject(object);
            wrapper.setTargetMethod(method.getName());
            wrapper.init();
          }
        }
      }
    }
  }

  @Override
  public XDiamondConfig getObject() throws Exception {
    return xDiamondConfig;
  }

  @Override
  public Class<?> getObjectType() {
    return XDiamondConfig.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
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

  public String getServerHost() {
    return serverHost;
  }

  public void setServerHost(String serverHost) {
    this.serverHost = serverHost;
  }

  public String getServerPort() {
    return serverPort;
  }

  public void setServerPort(String serverPort) {
    this.serverPort = serverPort;
  }

  public List<String> getLocations() {
    return locations;
  }

  public void setLocations(List<String> locations) {
    this.locations = locations;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public boolean isbScanListenerAnnotation() {
    return bScanListenerAnnotation;
  }

  public void setbScanListenerAnnotation(boolean bScanListenerAnnotation) {
    this.bScanListenerAnnotation = bScanListenerAnnotation;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  @Override
  public void onApplicationEvent(ContextStartedEvent event) {
    // scan annotation 必须要放到这里，不然会出现构造函数里传进来String不能正确地处理${}表达式
    if (bScanListenerAnnotation) {
      try {
        scanListenerAnnotation();
      } catch (ClassNotFoundException | NoSuchMethodException e) {
        throw new RuntimeException("xdiamond scan annotation error!", e);
      }
    }
  }
}
