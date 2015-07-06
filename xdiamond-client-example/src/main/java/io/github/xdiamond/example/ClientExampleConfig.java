package io.github.xdiamond.example;


public class ClientExampleConfig {

  String memcachedAddress;

  String zookeeperAddress;

  public String getMemcachedAddress() {
    return memcachedAddress;
  }

  public void setMemcachedAddress(String memcachedAddress) {
    this.memcachedAddress = memcachedAddress;
  }

  public String getZookeeperAddress() {
    return zookeeperAddress;
  }

  public void setZookeeperAddress(String zookeeperAddress) {
    this.zookeeperAddress = zookeeperAddress;
  }


  @Override
  public String toString() {
    return "ClientExampleConfig [memcachedAddress=" + memcachedAddress + ", zookeeperAddress="
        + zookeeperAddress + "]";
  }

}
