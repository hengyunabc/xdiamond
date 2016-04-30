package io.github.xdiamond.example.listener;

import io.github.xdiamond.client.annotation.AllKeyListener;
import io.github.xdiamond.client.annotation.OneKeyListener;
import io.github.xdiamond.client.event.ConfigEvent;

/**
 *
 * @author hengyunabc
 *
 */
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
