package io.github.xdiamond.example.listener;

import io.github.xdiamond.client.annotation.AllKeyListener;
import io.github.xdiamond.client.annotation.OneKeyListener;
import io.github.xdiamond.client.event.ConfigEvent;

import org.springframework.stereotype.Service;

@Service
public class ListenerExampleService {

  @OneKeyListener(key = "testOneKeyListener")
  public void testOneKeyListener(ConfigEvent event) {
    System.err.println("ListenerExampleService, testOneKeyListener, event :" + event);
  }

  @AllKeyListener
  public void testAllKeyListener(ConfigEvent event) {
    System.err.println("ListenerExampleService, testAllKeyListener, event :" + event);
  }
}
