package io.github.xdiamond.example.springboot;

import io.github.xdiamond.client.XDiamondConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

//@Configuration
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class MyConfig {

  String groupId = "io.github.xdiamond";
  String artifactId = "xdiamond-client-example";
  String version = "0.0.1-SNAPSHOT";

  @Value("${:192.168.66.61}")
  String serverHost;

  @Value("${:5678}")
  int serverPort;
  
  @Bean
  @Profile("product")
  public XDiamondConfig XDiamondConfig() {
    // String serverHost, int serverPort, String groupId, String artifactId,
    // String version, String profile, String secretKey
    String profile = "product";
    String secretKey = null;
    XDiamondConfig xDiamondConfig =
        new XDiamondConfig(serverHost, serverPort, groupId, artifactId, version, profile, secretKey);
    xDiamondConfig.init();
    return xDiamondConfig;
  }

  @Bean
//  @Profile("dev")
  @Autowired
  public XDiamondConfig XDiamondConfig_dev(@Value("${xxx:192.168.66.61}") String serverHost) {
    // String serverHost, int serverPort, String groupId, String artifactId,
    // String version, String profile, String secretKey
    String profile = "dev";
    String secretKey = null;
    XDiamondConfig xDiamondConfig =
        new XDiamondConfig(serverHost, serverPort, groupId, artifactId, version, profile, secretKey);
    xDiamondConfig.init();
    return xDiamondConfig;
  }
  
  @Bean
  public PropertyPlaceholderConfigurer PropertyPlaceholderConfigurer(XDiamondConfig xDiamondConfig) {
    PropertyPlaceholderConfigurer propertyPlaceholderConfigurer =
        new PropertyPlaceholderConfigurer();
    propertyPlaceholderConfigurer.setProperties(xDiamondConfig.getProperties());

    return propertyPlaceholderConfigurer;
  }

}
