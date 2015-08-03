
## 工作原理

* 每个项目有groupId，artifactId，version，然后在不同的环境里对应不同的profile，比如：test, dev, product。

* 应用在启动时，通过网络连接到xdiamond配置中心，获取到最新的配置。如果没有获取到，从本地备份读取最后拉取的配置。

* 在Spring初始化时，把配置转为Properties，应用可以通过````${}````表达式或者````@Value````来获取配置。

* 如果配置有更新，可以通过Listener来通知应用。

每个项目都有一个base的profile，所有的profile都会继承base的配置。在base可以放一些公共的配置，比如某个服务的端口。

## 客户端使用快速例子
* 增加maven依赖

**注意：xdiamond-client里依赖的Spring版本是3.x，如果有使用spring 4.x，可以把spring的依赖exclude掉。**

```xml
		<dependency>
			<groupId>io.github.xdiamond</groupId>
			<artifactId>xdiamond-client</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
```

* 配置xdiamond只需要配置两个bean：

```xml
	<bean id="xDiamondConfig" class="io.github.xdiamond.client.spring.XDiamondConfigFactoryBean">
		<property name="serverHost" value="${xdiamond.server.host:192.168.66.61}" />
		<property name="serverPort" value="5678" />
		<property name="groupId" value="io.github.xdiamond" />
		<property name="artifactId" value="xdiamond-client-example" />
		<property name="version" value="0.0.1-SNAPSHOT" />
		<property name="profile" value="${xdiamond.project.profile:dev}" />
		<property name="secretKey" value="${xdiamond.project.secretkey:123456}"></property>
	</bean>

	<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
		<property name="properties">
			<bean class="java.util.Properties"
				factory-bean="xDiamondConfig" factory-method="getProperties">
			</bean>
		</property>
	</bean>
```
1. 通过````XDiamondConfigFactoryBean````初始化一个````XDiamondConfig````
2. 通过````PropertyPlaceholderConfigurer````使用上````XDiamondConfig````获取到的配置
3. 通过${}表达式把配置值注入到bean里

```xml
	<bean id="clientExampleConfig" class="io.github.xdiamond.example.ClientExampleConfig">
		<property name="memcachedAddress" value="${memcached.serverlist}"></property>
		<property name="zookeeperAddress" value="${zookeeper.address}"></property>
	</bean>
```
完整 的示例代码在xdiamond-client-example里。

### 临时修改本地配置的方法
在开发过程中，可能会需要临时修改本地的配置，下面给出两种参考的配置方式：

* 使用````propertiesArray````和````util:properties````

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
	xmlns:util="http://www.springframework.org/schema/util"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
		http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/util 
        http://www.springframework.org/schema/util/spring-util.xsd">

	<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
		<property name="propertiesArray">
			<util:list>
			<!-- 对于本地临时要修改的配置，可以像下面这样配置，注意不要把util:properties的内容提交到代码库里 -->
<!-- 				<util:properties> -->
<!-- 					<prop key="myTempConfigKey">tempConfigValue</prop> -->
<!-- 				</util:properties> -->
				<bean class="java.util.Properties" factory-bean="xDiamondConfig"
					factory-method="getProperties">
				</bean>
			</util:list>
		</property>
	</bean>
```
* 在本地配置一个````local.properties````

在resources目录下增加一个````local.properties````文件，然后这样配置：
```xml
	<bean
		class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
		<!-- 对于本地临时要修改的配置，可以放在local.properties文件里，注意不要把内容提交到代码库里，保持local.properties文件内容为空 -->
	<!-- <property name="location" value="classpath:local.properties" /> -->
		<property name="properties">
			<bean id="xDiamondProperties" class="java.util.Properties"
				factory-bean="xDiamondConfig" factory-method="getProperties">
			</bean>
		</property>
```

### 配置在本地缓存的目录
xdiamond client会把最后拉取的配置缓存到本地的````${usr.home}/.xdiamond````目录下，如果有需要，可以到下面去查看相应的配置文件。

如果应用在启动时，连接xdiamond server失败，那么也会先到这个目录下加载最后缓存的配置。

### 在测试环境使用xdiamond的配置
xdiamond本质是提供了一个properties对象，只要把profile改为dev即可。参考````xdiamond-client-example````里的test case。

### 线上环境的Tomcat的setenv.sh的配置
以上面的配置为例，假定xdiamond的服务器是````product.xdiamond.com````，则在````tomcat/bin/setenv.sh````里配置：
```bash
JAVA_OPTS="$JAVA_OPTS -Dxdiamond.server.host=product.xdiamond.com -Dxdiamond.project.profile=product -Dxdiamond.project.secretkey=b8ylj4r0OcBMgdNU"
```

### 高级配置

#### 配置Listener，侦听配置修改事件
如果是````@Service````, ````@Component````这样的bean，可以直接在一个函数上用````@OneKeyListener````或者````@AllKeyListener````来获取到最新的配置值。例如：

```java
@Service
public class ListenerExampleService {

  @OneKeyListener(key = "testOneKeyListener")
  public void testOneKeyListener(ConfigEvent event) {
    System.err.println("ListenerExampleService, testOneKeyListener, event :" + event);
  }

  @AllKeyListener
  public void testAllKeyListener(ConfigEvent event) {
    System.err.println("ListenerExampleService, testAllKeyListener, event :" + event);
  }
}
```
在````ConfigEvent````里，可以获取到一个key是新增/修改/删除的信息。用户可以自行处理。

如果是在spring xml里配置的bean，则需要在bean上增加一个````@EnableConfigListener````的注解，以便于spring ````context:component-scan ````能扫描到这个类。



具体的例子代码在````xdiamond-client-example````里。

#### 把配置同步到system properties
````bSyncToSystemProperties````可以把配置同步到System Properties里，默认值是false。这样可以实现在*.properties文件里引用xdiamond里的配置。比如在一个test.properties文件里：
```
testKey=xxx${keyFromXdiamond}yyy
```
```xml
	<bean id="xDiamondConfig"
		class="io.github.xdiamond.client.spring.XDiamondConfigFactoryBean">
        ...
		<property name="bSyncToSystemProperties" value="true"></property>
	</bean>
```
**注意，如果是一个Tomcat里跑多个war，小心打开这个配置**。参考： http://wiki.apache.org/tomcat/HowTo#Can_I_set_Java_system_properties_differently_for_each_webapp.3F
#### 设置连接重试的次数和时间
默认是会不断尝试重连服务器。在打印日志时已经做了优化，通常不需要改变设置。
```xml
	<bean id="xDiamondConfig"
		class="io.github.xdiamond.client.spring.XDiamondConfigFactoryBean">
        ...
		<!-- 以指数退避方式计算重连时间间隔 -->
		<property name="bBackOffRetryInterval" value="true"></property>
		<property name="retryIntervalSeconds" value="5"></property>
		<property name="maxRetryIntervalSeconds" value="120"></property>
		<!-- 默认会无限重试 -->
		<property name="maxRetryTimes" value=""></property>
	</bean>
```

## 旧的client jar迁移过程
假定client jar提供了一个spring xml配置文件，如````resources/clientDefault/clientjar-spring-context.xml````，下游使用这个jar包的项目都会import这个xml文件。

在这个xml文件里会有client自己内部用到的````${}````变量，那么只需要在xdiamond server上增加相应的project，然后通过下游的项目在xdiamond server上增加对这个client jar的依赖即可。

client jar只需要保证自己client jar里的xml的变量都配置到xdiamond server上即可。

**client jar里不需要配置xdiamond client！client jar对xdiamond实际上无感知的！**

**注意：每一个war/应用，都只有一个xdiamond client，通常不需要配置多个**

## 使用某个client/公共组件的方法

1. 在xdiamond server的界面上，为自己项目增加client jar的"Dependencies"
2. 在spring配置里import client jar提供的spring xml配置文件

很简单地通过依赖关系可以获取到client jar的配置，完全不用关心client内部所用的key/value。

## 项目的属性
项目里有两个需要配置的属性：
* bPublic   是否允许其它人查看到这个Project
* bAllowDependency  是否允许下游项目依赖这个Project

## 权限相关
* Group里的用户有access等级，分别为：owner, master, developer, reporter, guest。
只有owner/master的用户才可以查看修改product环境下的配置。

* 项目的profile都有access级别。

* 项目都有一个ownerGroup，据用户在ownerGroup里的access，则可以访问相应级别的profile。


##  项目的依赖关系
有一些公共配置，或者client jar包的配置是要被依赖的。比如前端的memcached集群的地址。
为了避免前端的项目都有一份memcached地址的配置，则可以把memcached的配置抽取为一个公共的配置，下游的项目通过依赖关系获取到memcached的配置。

### 同名的profile的依赖传递关系
profile可以看做是环境，同样环境的配置，会通过依赖关系传递。

比如client jar里有一个````memcached.server````的key。

在dev环境value是````localhost:11211````，在product环境是````192.168.90.145:11211````，那么下游的应用只要在xdiamond server上依赖了这个client jar，在相应的环境里，就会自动拿到相应的配置（当然，通常不用关心client jar里的配置是什么，client jar自己要保证在不同环境的value是正确的）。

## 客户端获取配置的安全性保证
每个profile可以配置secretKey，只有匹配才可以获取到project的配置。线上环境的tomcat在setenv.sh里通过参数把secretkey传递给应用。

在test, dev等环境，可以不用配置secretkey。

## xdiamond配置的本质
对于使用者，xdiamond提供的是一个Properties对象。用户可以结合Spring等来使用。

### 和Spring profile的关系
xdiamond只提供了一个properties对象，可以在不同的Spring profile里使用这个properties。两者是独立的。

#### 结合spring profile配置
可以在不同的spring profile，加载对应的xdiamond profile。具体的配置参考example里的````SpringProfileMain````，````spring-profile-example.xml````。


## RESTful API
对于非Java的客户端，可以通过http方式获取到配置。
```
path: /clientapi/config
paras:
groupId
artifactId
version
profile
secretKey
format   properties/utf8properties/json
```
例如：

http://localhost:8081/clientapi/config?groupId=test&artifactId=test&version=test&profile=base&secretKey=123456&format=properties

## 本地开发环境
* git clone 代码
* 运行xdiamond server：

```bash
cd xdiamond-server
mvn tomcat7:run -DskipTests
```
然后访问 http://localhost:8080/xdiamond-server ，用admin/admin, standard登录

* 执行client例子代码：

```bash
cd xdiamond-client-example/
mvn exec:java -Dexec.mainClass="io.github.xdiamond.example.ClientExampleMain"
```
默认是获取product环境的配置，如果想获取dev环境的配置，则可以执行：
```bash
mvn exec:exec -Dexec.executable="java" -Dexec.args="-Dxdiamond.project.profile=dev -classpath %classpath io.github.xdiamond.example.ClientExampleMain"
```