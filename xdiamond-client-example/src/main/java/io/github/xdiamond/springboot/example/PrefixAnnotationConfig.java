package io.github.xdiamond.springboot.example;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 演示spring boot里的利用前缀来注入的特性
 *
 * @author hengyunabc
 *
 */
@Configuration
@ConfigurationProperties(prefix = "memcached")
@EnableConfigurationProperties
public class PrefixAnnotationConfig {
	String serverlist;

	public String getServerlist() {
		return serverlist;
	}

	public void setServerlist(String serverlist) {
		this.serverlist = serverlist;
	}

	@Override
	public String toString() {
		return "PrefixAnnotationConfig [serverlist=" + serverlist + "]";
	}

}