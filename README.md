# minio-spring-boot-starter

> 对 `minio-client` SDK开发包进行二次封装，借助Spring Boot Starter 思想，通过简单的配置，实现开箱即用

### 运行环境

> * Spring Boot版本：``2.6.7`
> 
> * JDK版本：`11`
> 
> * Minio Client版本：`8.2.1`
> - Guava版本：`31.0.1-jre`

### 使用说明

> * 将项目打包后，引入如下依赖
> 
> ```xml
> <dependency>
>     <groupId>pers.avc.minio</groupId>
>     <artifactId>minio-spring-boot-starter</artifactId>
>     <version>0.0.1-SNAPSHOT</version>
> </dependency>
> 
>  <!-- 因minio-client中需要用到该包 -->
> <dependency>
>     <groupId>com.google.guava</groupId>
>     <artifactId>guava</artifactId>
>     <version>31.0.1-jre</version>
> </dependency>
> ```
> 
> * 添加配置文件
>   
>   ![](https://raw.githubusercontent.com/AmVilCresx/picBed/master/20220511153233.png)
>   
>   ```yaml
>   minio:
>     # 高版本中， API端口和Console端口是不同的， 这里的9000是API端口
>     url: http://127.0.0.1:9000
>     access-key: minioadmin
>     secret-key: minioadmin
>   ```

### 补充说明

> 该starter对常用的做了二次封装，对于一些特殊的场景，可通过  `MinioObjectOperator.getClient()` 方法获取 `MinioClient` 对象来实现。
