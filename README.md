
## 工作原理

* 每个项目有groupId，artifactId，version，然后在不同的环境里对应不同的profile，比如：test, dev, product。

* 应用在启动时，通过网络连接到xdiamond配置中心，获取到最新的配置。如果没有获取到，从本地备份读取最后拉取的配置。

* 在Spring初始化时，把配置转为Properties，应用可以通过````${}````表达式或者````@Value````来获取配置。

* 如果配置有更新，可以通过Listener来通知应用。

每个项目都有一个base的profile，所有的profile都会继承base的配置。在base可以放一些公共的配置，比如某个服务的端口。

## 快速例子
配置xdiamond只需要配置两个bean：
```xml
	<bean id="xDiamondConfig" class="io.github.xdiamond.client.spring.XDiamondConfigFactoryBean">
		<property name="serverHost" value="${XDIAMOND_SERVERHOST:192.168.66.61}" />
		<property name="serverPort" value="${XDIAMOND_SERVERPORT:5678}" />
		<property name="groupId" value="io.github.xdiamond" />
		<property name="artifactId" value="xdiamond-client-example" />
		<property name="version" value="0.0.1-SNAPSHOT" />
		<property name="profile" value="dev" />
		<property name="secretKey" value="123456"></property>
	</bean>

	<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
		<property name="ignoreResourceNotFound" value="true" />
		<property name="ignoreUnresolvablePlaceholders" value="true" />
		<property name="properties">
			<bean id="xDiamondProperties" class="java.util.Properties"
				factory-bean="xDiamondConfig" factory-method="getProperties">
			</bean>
		</property>
	</bean>
```
1. 通过````XDiamondConfigFactoryBean````初始化一个````XDiamondConfig````
2. 通过````PropertyPlaceholderConfigurer````使用上````XDiamondConfig````获取到的配置
3. 通过${}表达式把配置值注入到bean里

```xml
	<bean id="memcachedConfig" class="io.github.xdiamond.example.MemcachedConfig">
		<property name="host" value="${memcached.host}"></property>
		<property name="port" value="${memcached.port}"></property>
	</bean>
```
完整 的示例代码在xdiamond-client-example里。


## 项目的属性
项目里有两个需要解析的属性：
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

## 客户端获取配置的安全性保证
每个profile可以配置secretKey，只有匹配才可以获取到project的配置。

## xdiamond配置的本质
对于使用者，xdiamond提供的是一个Properties对象。用户可以结合Spring等来使用。

### 和Spring profile的关系
xdiamond只提供了一个properties对象，可以在不同的Spring profile里使用这个properties。两者是独立的。

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

