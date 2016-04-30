package io.github.xdiamond.springprofile.example;

import org.springframework.context.support.GenericXmlApplicationContext;

import io.github.xdiamond.example.ClientExampleConfig;

/**
 * 演示利用spring profiles的特性，来切换不同环境的xdiamond client配置。
 *
 * 比如，在测试环境使用的xdiamond server和生产环境使用的xidamond server的域名不一样，则可以通过spring
 * profiles很方便地切换。
 *
 * @author hengyunabc
 *
 */
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
