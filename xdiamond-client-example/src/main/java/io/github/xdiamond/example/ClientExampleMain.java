package io.github.xdiamond.example;

import org.springframework.context.support.GenericXmlApplicationContext;

public class ClientExampleMain {
  public static void main(String[] args) throws InterruptedException {
    // 这些变量可以通过命令行或者在tomcat的setenv.sh文件里传递进来，这里简单起见，直接设置到system properties里
    // 参考spring-context-clientexample.xml 里的${}表达式配置
    if (System.getProperty("xdiamond.server.host") == null) {
      System.setProperty("xdiamond.server.host", "localhost");
    }
    if (System.getProperty("xdiamond.server.port") == null) {
      System.setProperty("xdiamond.server.port", "5678");
    }
    if (System.getProperty("xdiamond.project.profile") == null) {
      System.setProperty("xdiamond.project.profile", "product");
    }
    if (System.getProperty("xdiamond.project.secretkey") == null) {
      System.setProperty("xdiamond.project.secretkey", "b8ylj4r0OcBMgdNU");
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

    Thread.sleep(300 * 1000);
  }
}
