package io.github.xdiamond.example.listener;

import io.github.xdiamond.client.annotation.AllKeyListener;
import io.github.xdiamond.client.annotation.EnableConfigListener;
import io.github.xdiamond.client.annotation.OneKeyListener;
import io.github.xdiamond.client.event.ConfigEvent;

/**
 * 这个bean是在xml文件里配置的，需要加上 @EnableConfigListener 的注解，否则 Listener注解不能生效
 * 
 * @author hengyunabc
 *
 */
@EnableConfigListener
public class ListenerXmlBean {

  @OneKeyListener(key = "testOneKeyListener")
  public void testOneKeyListener(ConfigEvent event) {
    System.err.println("ListenerXmlBean, testOneKeyListener, event :" + event);
  }

  @AllKeyListener
  public void testAllKeyListener(ConfigEvent event) {
    System.err.println("ListenerXmlBean, testAllKeyListener, event :" + event);
  }

}
