package io.github.xdiamond.example;

public class MemcachedConfig {
  String host;
  int port;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public String toString() {
    return "MemcachedConfig [host=" + host + ", port=" + port + "]";
  }
}
