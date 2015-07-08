package io.github.xdiamond.springprofile.example;

import io.github.xdiamond.example.ClientExampleAnnotationConfig;
import io.github.xdiamond.example.ClientExampleConfig;

import org.springframework.context.support.GenericXmlApplicationContext;

public class SpringProfileMain {
  public static void main(String[] args) throws InterruptedException {
    // 这些变量可以通过命令行或者在tomcat的setenv.sh文件里传递进来，这里简单起见，直接设置到system properties里
    // 参考spring-context-clientexample.xml 里的${}表达式配置
    if (System.getProperty("xdiamond.project.secretkey") == null) {
      System.setProperty("spring.profiles.active", "dev");
    }

    GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
    ctx.load("spring-profile-example.xml");
    ctx.refresh();

    ClientExampleConfig clientExampleConfig =
        ctx.getBean("clientExampleConfig", ClientExampleConfig.class);

    System.err.println(clientExampleConfig);

    Thread.sleep(30 * 1000);
    System.exit(0);
  }
}
