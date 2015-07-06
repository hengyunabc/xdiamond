package io.github.xdiamond.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientExampleAnnotationConfig {

  @Value(value = "${memcached.serverlist}")
  String memcachedAddress;

  @Value("${zookeeper.address}")
  String zookeeperAddress;

  @Override
  public String toString() {
    return "ClientExampleAnnotationConfig [memcachedAddress=" + memcachedAddress + ", zookeeperAddress="
        + zookeeperAddress + "]";
  }

}
