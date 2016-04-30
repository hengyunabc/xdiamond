package io.github.xdiamond.client.spring;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;

/**
 *
 * 把 properties对象加到spring的 {@link org.springframework.core.env.PropertySources}
 * 里。
 *
 * @author hengyunabc
 *
 * @see org.springframework.core.env.PropertySources
 * @see org.springframework.core.env.ConfigurableEnvironment
 */
public class PropertySourcesAdderBean
		implements InitializingBean, ApplicationContextAware, PriorityOrdered, BeanFactoryPostProcessor {

	private String name = "xdiamond";

	private Properties properties;

	private ApplicationContext applicationContext;

	public PropertySourcesAdderBean() {

	}

	public void afterPropertiesSet() throws Exception {
		PropertiesPropertySource propertySource = new PropertiesPropertySource(name, this.properties);

		Environment environment = applicationContext.getEnvironment();
		if (environment instanceof ConfigurableEnvironment) {
			ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) applicationContext
					.getEnvironment();
			configurableEnvironment.getPropertySources().addLast(propertySource);
		}
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}