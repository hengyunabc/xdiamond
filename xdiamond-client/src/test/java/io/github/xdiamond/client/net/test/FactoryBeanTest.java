package io.github.xdiamond.client.net.test;

import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;

public class FactoryBeanTest implements FactoryBean<Properties> {

	Properties properties = new Properties();

	@Override
	public Properties getObject() throws Exception {
		return properties;
	}

	@Override
	public Class<?> getObjectType() {
		return Properties.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
