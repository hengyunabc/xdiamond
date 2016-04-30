package io.github.xdiamond.springboot.example;

import io.github.xdiamond.example.ClientExampleAnnotationConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ComponentScan(basePackages = { "io.github.xdiamond.example", "io.github.xdiamond.springboot.example" })
@ImportResource("classpath:spring-context-springboot-example.xml")
public class DemoApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);

		ClientExampleAnnotationConfig config = context.getBean(ClientExampleAnnotationConfig.class);
		System.err.println(config);

		PrefixAnnotationConfig prefixAnnotationConfig = context.getBean(PrefixAnnotationConfig.class);

		System.err.println(prefixAnnotationConfig);

	}
}
