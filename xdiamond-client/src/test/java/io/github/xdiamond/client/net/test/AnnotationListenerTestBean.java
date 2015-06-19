package io.github.xdiamond.client.net.test;

import io.github.xdiamond.client.annotation.OneKeyListener;
import io.github.xdiamond.client.event.ConfigEvent;

import org.springframework.stereotype.Service;

@Service
public class AnnotationListenerTestBean {
	
	@OneKeyListener(key = "testAnnotation")
	public void testOneKeyListener(ConfigEvent event){
		System.err.println("event :" + event);
	}

}
