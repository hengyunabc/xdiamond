package io.github.xdiamond.example;

import org.springframework.context.support.GenericXmlApplicationContext;

public class ClientExampleMain {
  public static void main(String[] args) throws InterruptedException {
    // 这些变量可以通过命令行或者在tomcat的setenv.sh文件里传递进来，这里简单起见，直接设置到system properties里
    // 参考spring-context-clientexample.xml 里的${}表达式配置
    if (System.getProperty("XDIAMOND_SERVERHOST") == null) {
      System.setProperty("XDIAMOND_SERVERHOST", "localhost");
    }
    if (System.getProperty("XDIAMOND_SERVERPORT") == null) {
      System.setProperty("XDIAMOND_SERVERPORT", "5678");
    }
    if (System.getProperty("XDIAMOND_PROFILE") == null) {
      System.setProperty("XDIAMOND_PROFILE", "product");
    }
    if (System.getProperty("XDIAMOND_SECRETKEY") == null) {
      System.setProperty("XDIAMOND_SECRETKEY", "b8ylj4r0OcBMgdNU");
    }


    GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
    ctx.load("spring-context-clientexample.xml");
    ctx.refresh();

    ClientExampleAnnotationConfig annotationConfig =
        ctx.getBean(ClientExampleAnnotationConfig.class);

    System.err.println(annotationConfig);

    ClientExampleConfig clientExampleConfig =
        ctx.getBean("clientExampleConfig", ClientExampleConfig.class);

    System.err.println(clientExampleConfig);

    Thread.sleep(30 * 1000);
    System.exit(0);
  }
}
