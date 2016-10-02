package io.github.xdiamond.client.spring;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import io.github.xdiamond.client.XDiamondConfig;
import io.github.xdiamond.client.annotation.AllKeyListener;
import io.github.xdiamond.client.annotation.OneKeyListener;
import io.github.xdiamond.client.event.ObjectListenerMethodInvokeWrapper;

/**
 * 支持${value:default}方式的配置； 支持在locations参数里，配置properties； 优先从System.getPropeties()里加载配置；
 *
 * @author hengyunabc
 *
 */
public class XDiamondConfigFactoryBean implements ApplicationContextAware, PriorityOrdered, BeanFactoryPostProcessor,
    InitializingBean, FactoryBean<XDiamondConfig>, BeanPostProcessor {
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

  // 启动时，是否打印获取到的配置信息
  String bPrintConfigWhenBoot;
  // 获取到配置，是否同步到System Properties里
  String bSyncToSystemProperties;

  // 指数退避的方式增加
  String bBackOffRetryInterval;
  // 失败重试的次数
  String maxRetryTimes;
  // 失败重试的时间间隔
  String retryIntervalSeconds;
  // 最大的重试时间间隔
  String maxRetryIntervalSeconds;

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

    if (!StringUtils.isEmpty(bPrintConfigWhenBoot)) {
      bPrintConfigWhenBoot = helper.replacePlaceholders(bPrintConfigWhenBoot, properties);
      xDiamondConfig.setbPrintConfigWhenBoot(Boolean.parseBoolean(bPrintConfigWhenBoot));
    }

    if (!StringUtils.isEmpty(bSyncToSystemProperties)) {
      bSyncToSystemProperties = helper.replacePlaceholders(bSyncToSystemProperties, properties);
      xDiamondConfig.setbSyncToSystemProperties(Boolean.parseBoolean(bSyncToSystemProperties));
    }

    if (!StringUtils.isEmpty(bBackOffRetryInterval)) {
      bBackOffRetryInterval = helper.replacePlaceholders(bBackOffRetryInterval, properties);
      xDiamondConfig.setbBackOffRetryInterval(Boolean.parseBoolean(bBackOffRetryInterval));
    }

    if (!StringUtils.isEmpty(maxRetryTimes)) {
      maxRetryTimes = helper.replacePlaceholders(maxRetryTimes, properties);
      xDiamondConfig.setMaxRetryTimes(Integer.parseInt(maxRetryTimes));
    }

    if (!StringUtils.isEmpty(retryIntervalSeconds)) {
      retryIntervalSeconds = helper.replacePlaceholders(retryIntervalSeconds, properties);
      xDiamondConfig.setRetryIntervalSeconds(Integer.parseInt(retryIntervalSeconds));
    }

    if (!StringUtils.isEmpty(maxRetryIntervalSeconds)) {
      maxRetryIntervalSeconds = helper.replacePlaceholders(maxRetryIntervalSeconds, properties);
      xDiamondConfig.setMaxRetryIntervalSeconds(Integer.parseInt(maxRetryIntervalSeconds));
    }

    xDiamondConfig.init();
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

  public String getbPrintConfigWhenBoot() {
    return bPrintConfigWhenBoot;
  }

  public void setbPrintConfigWhenBoot(String bPrintConfigWhenBoot) {
    this.bPrintConfigWhenBoot = bPrintConfigWhenBoot;
  }

  public String getbSyncToSystemProperties() {
    return bSyncToSystemProperties;
  }

  public void setbSyncToSystemProperties(String bSyncToSystemProperties) {
    this.bSyncToSystemProperties = bSyncToSystemProperties;
  }

  public String getbBackOffRetryInterval() {
    return bBackOffRetryInterval;
  }

  public void setbBackOffRetryInterval(String bBackOffRetryInterval) {
    this.bBackOffRetryInterval = bBackOffRetryInterval;
  }

  public String getMaxRetryTimes() {
    return maxRetryTimes;
  }

  public void setMaxRetryTimes(String maxRetryTimes) {
    this.maxRetryTimes = maxRetryTimes;
  }

  public String getRetryIntervalSeconds() {
    return retryIntervalSeconds;
  }

  public void setRetryIntervalSeconds(String retryIntervalSeconds) {
    this.retryIntervalSeconds = retryIntervalSeconds;
  }

  public String getMaxRetryIntervalSeconds() {
    return maxRetryIntervalSeconds;
  }

  public void setMaxRetryIntervalSeconds(String maxRetryIntervalSeconds) {
    this.maxRetryIntervalSeconds = maxRetryIntervalSeconds;
  }

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bScanListenerAnnotation) {
			Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
			if (methods != null) {
				for (Method method : methods) {
					OneKeyListener oneKeyListener = AnnotationUtils.findAnnotation(method, OneKeyListener.class);
					if (oneKeyListener != null) {
						logger.debug("XDaimond add Annotation Method Listener, class:" + bean.getClass().getName()
								+ ", method:" + method.getName());
						ObjectListenerMethodInvokeWrapper wrapper = new ObjectListenerMethodInvokeWrapper();
						wrapper.setxDiamondConfig(xDiamondConfig);
						wrapper.setListenerClassName(io.github.xdiamond.client.event.OneKeyListener.class.getName());
						wrapper.setKey(oneKeyListener.key());
						wrapper.setTargetObject(bean);
						wrapper.setTargetMethod(method.getName());
						wrapper.init();
					}

					AllKeyListener allKeyListener = AnnotationUtils.findAnnotation(method, AllKeyListener.class);
					if (allKeyListener != null) {
						logger.debug("XDaimond add Annotation Method Listener, class:" + bean.getClass().getName()
								+ ", method:" + method.getName());
						ObjectListenerMethodInvokeWrapper wrapper = new ObjectListenerMethodInvokeWrapper();
						wrapper.setxDiamondConfig(xDiamondConfig);
						wrapper.setListenerClassName(io.github.xdiamond.client.event.AllKeyListener.class.getName());
						wrapper.setTargetObject(bean);
						wrapper.setTargetMethod(method.getName());
						wrapper.init();
					}
				}
			}
		}

		return bean;
	}
}
