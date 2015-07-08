package io.github.xdiamond.example.test;

import io.github.xdiamond.example.ClientExampleConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-context-test.xml")
public class ClientExampleTest {

  @Autowired
  ClientExampleConfig clientExampleConfig;

  @Test
  public void test() {
    System.err.println(clientExampleConfig);
    Assert.assertEquals(clientExampleConfig.getMemcachedAddress(), "localhost:11211");
  }

}
