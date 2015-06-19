package io.github.xdiamond.example;

import org.springframework.context.support.GenericXmlApplicationContext;

public class ClientExampleMain {
  public static void main(String[] args) throws InterruptedException {
    // 这两个变量可以通过命令行或者在tomcat的setenv.sh文件里传递进来，这里简单起见，直接设置到system properties里
    //参考spring-context-clientexample.xml 里的${}表达式配置
    System.setProperty("XDIAMOND_SERVERHOST", "192.168.66.61");
    System.setProperty("XDIAMOND_SERVERPORT", "5678");

    GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
    ctx.load("spring-context-clientexample.xml");
    ctx.refresh();

    MemcachedConfig memcachedConfig = ctx.getBean("memcachedConfig", MemcachedConfig.class);

    System.err.println(memcachedConfig);

    Thread.sleep(30 * 1000);
    System.exit(0);
  }
}
