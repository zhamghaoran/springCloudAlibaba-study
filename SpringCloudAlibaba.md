# Spring Cloud Alibaba

中文文档:https://github.com/alibaba/spring-cloud-alibaba/blob/2022.x/README-zh.md

版本对应关系

- 2022.x分支对应的是 Spring Cloud 2022 与 Spring Boot 3.0.x，最低支持 JDK 17。
- 2021.x 分支对应的是 Spring Cloud 2021 与 Spring Boot 2.6.x，最低支持 JDK 1.8。
- 2020.0 分支对应的是 Spring Cloud 2020 与 Spring Boot 2.4.x，最低支持 JDK 1.8。
- 2.2.x 分支对应的是 Spring Cloud Hoxton 与 Spring Boot 2.2.x，最低支持 JDK 1.8。
- greenwich 分支对应的是 Spring Cloud Greenwich 与 Spring Boot 2.1.x，最低支持 JDK 1.8。
- finchley 分支对应的是 Spring Cloud Finchley 与 Spring Boot 2.0.x，最低支持 JDK 1.8。
- 1.x 分支对应的是 Spring Cloud Edgware 与 Spring Boot 1.x，最低支持 JDK 1.7。

Spring Cloud 使用 Maven 来构建，最快的使用方式是将本项目 clone 到本地，然后执行以下命令：

```shell
./mvnw install
```

执行完毕后，项目将被安装到本地 Maven 仓库。



## 准备工作

### 1.安装Nacos

下载链接：https://github.com/alibaba/nacos/releases

解压后进入bin目录

```shell
startup.cmd -m standalone
```

成功运行 ：

```shell
"nacos is starting with standalone"

         ,--.
       ,--.'|
   ,--,:  : |                                           Nacos 2.2.0
,`--.'`|  ' :                       ,---.               Running in stand alone mode, All function modules
|   :  :  | |                      '   ,'\   .--.--.    Port: 8848
:   |   \ | :  ,--.--.     ,---.  /   /   | /  /    '   Pid: 14988
|   : '  '; | /       \   /     \.   ; ,. :|  :  /`./   Console: http://192.168.123.232:8848/nacos/index.html
'   ' ;.    ;.--.  .-. | /    / ''   | |: :|  :  ;_
|   | | \   | \__\/: . ..    ' / '   | .; : \  \    `.      https://nacos.io
'   : |  ; .' ," .--.; |'   ; :__|   :    |  `----.   \
|   | '`--'  /  /  ,.  |'   | '.'|\   \  /  /  /`--'  /
'   : |     ;  :   .'   \   :    : `----'  '--'.     /
;   |.'     |  ,     .-./\   \  /            `--'---'
'---'        `--`---'     `----'

2023-02-23 13:15:42,888 INFO Tomcat initialized with port(s): 8848 (http)

```

进入：http://localhost:8848/nacos

初次登录账号密码都是nacos

### 2.创建pom文件

```xml
 <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>2022.0.0.0-RC1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>3.0.2</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>2022.0.1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
    	</dependencies>
    </dependencyManagement>
```

## Nacos注册和服务调用

application.yml配置文件：

```yaml
server:
  port: 8001   # 服务端口号
spring:
  application:
    name: service-provider   # 服务名称
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848   # nacos地址
management:   
  endpoints:
    web:
      exposure:
        include: '*'

```

主启动类：

```java
package com.zhr.alibabanacosdiscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient     // 开启服务发现客户端
public class AlibabaNacosDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlibabaNacosDiscoveryApplication.class, args);
    }

}

```

#### RestTemplate 服务调用

```java
@Configuration
public class config {
    @Bean
    @LoadBalanced   // 开启负载均衡
    public RestTemplate template() {
        return new RestTemplate();
    }
}

```

```java
 @GetMapping("/consumer/feign")
    public String echo() {
       return template.getForObject(serverURL + "/nacos/discovery",String.class);
    }

```

#### feign 服务调用



ConsumerService：

```java
@FeignClient(value = "service-provider")   // 根据微服务的名称来进行接口调用
@Service
public interface ConsumerService {

    @GetMapping("/nacos/discovery")    // 设置对应的接口
    String consume();
}
```



调用：

```java
 @GetMapping("/consumer/open")
    public String ech() {
        return consumerService.consume();   // 消费者直接调用接口中的方法即可
    }
```

### Nacos持久化

新建数据库nacos_devtest 

执行nacos目录下的conf中的sql文件创建相关的表

修改conf中的application.properties 文件

新增：

```properties
db.num=1
db.url.0=jdbc:mysql://localhost:3306/nacos_devtest?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
db.user=root
db.password=root
```

重启nacos即可将信息持久化到数据库。



## Nacos配置中心

文档: https://github.com/alibaba/spring-cloud-alibaba/wiki/Nacos-config

pom:

```xml
 <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```



bootstrap.yml:

```yaml
spring:
  application:
    name: nacos-config
  cloud:
    nacos:
      server-addr: 127.0.0.1:8848
      config:
        file-extension: yaml
        namespace: ea2d9881-f20e-46a9-9a00-a749b27f39ae
        shared-configs[0]:
            data-id: com.zhr.config.yaml
            refresh: true
```

application.yml:

```yaml
server:
  port: 8004
#spring:
#  profiles:
#    active: dev
```

- nacos端口在80时，也需要在address中写明端口： localhost:80

- 在未显示声明data-id类型时，默认的是properties，但是在后面的extension-config和shared-config中自动义data-id时，需要声明配置文件类型（尽管是properties）

- 配置文件的默认名称和spring.application.name一致，若不一致，下文介绍了声明方法

- 可以通过spring.cloud.nacos.config.refresh.enabled=false来关闭自动更新配置文件

- 自定义namespace,默认的namespace是`Public ` ，我们可以在Nacos客户端获取到自定义namepsace的MD5值来指定namesapce，`spring.cloud.nacos.config.namespace=ea2d9881-f20e-46a9-9a00-a749b27f39ae ` 

- 自定义Group ,默认的GROUP为`DEFAULT_GROUP`,我们可以通过`spring.cloud.nacos.config.group=groupname`

- 通过extension-config来自定义扩展data-id:

- ```properties
  Data Id 既不在默认的组，也支持动态刷新
  spring.cloud.nacos.config.extension-configs[2].data-id=ext-config-common03.properties
  spring.cloud.nacos.config.extension-configs[2].group=REFRESH_GROUP
  spring.cloud.nacos.config.extension-configs[2].refresh=true
  ```

- 配置的优先级：内部规则自动生成的data-id   >  `extension-config` > `shared-config`

- 获取配置：@Value("${user.name}") 注解即可获取到相对应的配置信息
- 动态刷新通过@RefreshScope 注解即可实现配置的动态刷新











