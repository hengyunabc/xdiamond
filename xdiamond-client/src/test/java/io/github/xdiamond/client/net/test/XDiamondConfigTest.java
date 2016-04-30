package io.github.xdiamond.client.net.test;

import io.github.xdiamond.client.XDiamondConfig;
import io.github.xdiamond.client.event.AllKeyListener;
import io.github.xdiamond.client.event.ConfigEvent;
import io.github.xdiamond.client.event.OneKeyListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

public class XDiamondConfigTest {

	@Test
	public void test() throws InterruptedException, ExecutionException, TimeoutException {
		String profile = "dev";
		String version = "sss";
		String artifactId = "sss";
		String groupId = "sss";
		String secretKey = "";

		String serverHost = "127.0.0.1";
		int port = 5678;

		XDiamondConfig config = new XDiamondConfig(serverHost, port, groupId, artifactId, version, profile, secretKey, true);
		config.init();

//		config.addOneKeyListener("test", new OneKeyListener() {
//			@Override
//			public void onConfigEvent(ConfigEvent event) {
//				System.err.println("onekey event:" + event);
//			}
//		});
//		config.addAllKeyListener(new AllKeyListener() {
//			@Override
//			public void onConfigEvent(ConfigEvent event) {
//				System.err.println("all key event:" + event);
//			}
//		});

//		TimeUnit.SECONDS.sleep(5);

		for(int i = 0; i < 100000; ++i) {
			String property = config.getProperty("test");
			System.err.println("property:" + property);
			TimeUnit.SECONDS.sleep(2);
		}


		config.destory();

		//		XDiamondClient client = new XDiamondClient();
//		client.setServerAddress("localhost");
//		client.setPort(5678);
//		client.init();
//		String profile = "pppppp";
//		String version = "vvvvv";
//		String artifactId = "testtt";
//		String groupId = "testtt";
//		String config = client.getConfig(groupId, artifactId, version, profile);
//		System.err.println("config:" + config);
	}
}
