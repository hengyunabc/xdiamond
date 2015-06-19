package io.github.xdiamond.client.net.test;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test.xml")
public class SpringTest {

	@Autowired
	@Qualifier("xDiamondProperties2")
	Properties properties;
	@Test
	public void test() {
		
		Object object = properties.get("test");
		System.err.println(object);
	}
}
