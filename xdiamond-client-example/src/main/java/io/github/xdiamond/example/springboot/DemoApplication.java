package io.github.xdiamond.example.springboot;

import io.github.xdiamond.client.XDiamondConfig;
import io.github.xdiamond.example.ClientExampleAnnotationConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ComponentScan("io.github.xdiamond.example")
@ImportResource("classpath:spring-context-clientexample.xml")
public class DemoApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);

    ClientExampleAnnotationConfig config = context.getBean(ClientExampleAnnotationConfig.class);
    System.err.println(config);
  }
}
