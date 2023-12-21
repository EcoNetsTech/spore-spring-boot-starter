
## spore-spring-boot-starter

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![GitHub release](https://img.shields.io/github/v/release/EcoNetsTech/spore-spring-boot-starter.svg)](https://github.com/EcoNetsTech/spore-spring-boot-starterr/releases)
[![License](https://img.shields.io/badge/JDK-1.8+-4EB1BA.svg)](https://docs.oracle.com/javase/8/docs/index.html)
[![License](https://img.shields.io/badge/SpringBoot-1.5+-green.svg)](https://docs.spring.io/spring-boot/docs/2.1.5.RELEASE/reference/htmlsingle/)

**适用于retrofit的spring-boot-starter，支持快速集成和功能增强**。
1. *Spring Boot 3.x 项目，请使用spore-spring-boot-starter 3.x*。
2. *Spring Boot 1.x/2.x 项目，请使用spore-spring-boot-starter 2.x*。

> 麻烦大家能给一颗star✨，您的star是我们持续更新的动力！

github项目地址：[https://github.com/EcoNetsTech/spore-spring-boot-starter](https://github.com/EcoNetsTech/spore-spring-boot-starter)

<!--more-->

## 功能特性

- [x] [自定义OkHttpClient](#自定义OkHttpClient)
- [x] [注解式拦截器](#注解式拦截器)
- [x] [日志打印](#日志打印)
- [x] [请求重试](#请求重试)
- [x] [微服务之间的HTTP调用](#微服务之间的HTTP调用)
- [x] [全局拦截器](#全局拦截器)
- [x] [调用适配器](#调用适配器)
- [x] [数据转换器](#数据转码器)
- [x] [元注解](#元注解)
- [x] [其他功能示例](#其他功能示例)

## 快速开始

### 引入依赖

```xml
<dependency>
   <groupId>io.github.ximutech</groupId>
   <artifactId>spore-spring-boot-starter</artifactId>
   <version>2.0.6</version>
</dependency>
```

**如果启动失败，大概率是依赖冲突，烦请引入或者排除相关依赖**。

### 定义HTTP接口

**接口必须使用`@SporeClient`注解标记**！HTTP相关注解可参考官方文档：[retrofit官方文档](https://square.github.io/retrofit/)。

```java
@SporeClient(baseUrl = "${test.baseUrl}")
public interface HttpApi {

    @GET("/")
    HitokotoVO getContent();
}
```

### 注入使用

**将接口注入到其它Service中即可使用！**

```java
@Service
public class TestService {

    @Autowired
    private HttpApi httpApi;

    public void test() {
       // 使用`httpApi`发起HTTP请求
    }
}
```

**默认情况下，自动使用`SpringBoot`扫描路径进行`SporeClient`注册**。你也可以在配置类加上`@EnableSporeClients`手工指定扫描路径。

## HTTP请求相关注解

`HTTP`请求相关注解，全部使用了`Retrofit`原生注解，以下是一个简单说明：

| 注解分类|支持的注解 |
|------------|-----------|
|请求方式|`@GET` `@HEAD` `@POST` `@PUT` `@DELETE` `@OPTIONS` `@HTTP`|
|请求头|`@Header` `@HeaderMap` `@Headers`|
|Query参数|`@Query` `@QueryMap` `@QueryName`|
|path参数|`@Path`|
|form-encoded参数|`@Field` `@FieldMap` `@FormUrlEncoded`|
| 请求体 |`@Body`|
|文件上传|`@Multipart` `@Part` `@PartMap`|
|url参数|`@Url`|

> 详细信息可参考官方文档：[retrofit官方文档](https://square.github.io/retrofit/)

## 配置属性

组件支持了多个可配置的属性，用来应对不同的业务场景，具体可支持的配置属性及默认值如下：

**注意：应用只需要配置要更改的配置项**!

```yaml
retrofit:
   # 全局转换器工厂
   global-converter-factories:
      - retrofit2.converter.jackson.JacksonConverterFactory
   # 全局调用适配器工厂
   global-call-adapter-factories:
      - io.github.ximutech.spore.retrofit.adapter.BodyCallAdapterFactory

   # 全局日志打印配置
   global-log:
      # 启用日志打印
      enable: true
      # 全局日志打印级别
      log-level: info
      # 全局日志打印策略
      log-strategy: basic

   # 全局重试配置
   global-retry:
      # 是否启用全局重试
      enable: false
      # 全局重试间隔时间
      interval-ms: 100
      # 全局最大重试次数
      max-retries: 2
      # 全局重试规则
      retry-rules:
         - response_status_not_2xx
         - occur_io_exception

   # 全局超时时间配置
   global-timeout:
      # 全局读取超时时间
      read-timeout-ms: 10000
      # 全局写入超时时间
      write-timeout-ms: 10000
      # 全局连接超时时间
      connect-timeout-ms: 10000
      # 全局完整调用超时时间
      call-timeout-ms: 0
```

## 高级功能

### 超时时间配置

如果仅仅需要修改`OkHttpClient`的超时时间，可以通过`@SporeClient`相关字段修改，或者全局超时配置修改。


### 自定义OkHttpClient

如果需要修改`OkHttpClient`其它配置，可以通过自定义`OkHttpClient`来实现，步骤如下：

1. 实现`OkHttpClientRegistrar`接口，调用`OkHttpClientRegistry#register()`方法注册`OkHttpClient`。
   
   ```java
   @Component
   @Slf4j
   public class CustomOkHttpClientRegistrar implements OkHttpClientRegistrar {
        @Override
        public void register(OkHttpClientRegistry registry) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                     .addInterceptor(chain -> {
                     log.info("========== 自定义okHttpClient的拦截器 ============");
                     return chain.proceed(chain.request());
                     })
                   .build();
   
           registry.register("customOkHttpClient", okHttpClient);
       }
   }
   ```

2. 通过`@SporeClient.sourceOkHttpClient`指定当前接口要使用的`OkHttpClient`。

   ```java
   @SporeClient(baseUrl = "${test.baseUrl}", sourceOkHttpClient = "customOkHttpClient")
   public interface CustomOkHttpTestApi {

         @GET("/get")
         Result<HitokotoVO> get();
   }
   ```

> 注意：组件不会直接使用指定的`OkHttpClient`，而是基于该`OkHttpClient`创建一个新的。


### 注解式拦截器

组件提供了**注解式拦截器**，支持基于url路径匹配拦截，使用的步骤如下：

1. 继承`BasePathMatchInterceptor`
2. 使用`@Intercept`注解指定要使用的拦截器

> 如果需要使用多个拦截器，在接口上标注多个`@Intercept`注解即可。

下面以"给指定请求的url后面拼接timestamp时间戳"为例，介绍下如何使用注解式拦截器。

#### 继承`BasePathMatchInterceptor`编写拦截处理器

```java
@Component
@Scope("prototype")
public class TokenInterceptor extends BasePathMatchInterceptor {
   @Override
   protected Response doIntercept(Chain chain) throws IOException {
      System.out.println("============ 进入token拦截器 ===============");
      Request request = chain.request();

      HttpUrl newUrl = request.url().newBuilder()
              .addQueryParameter("token", UUID.randomUUID().toString())
              .build();

      Request newRequest = request.newBuilder()
              .url(newUrl)
              .build();
      return chain.proceed(newRequest);
   }
}
```

#### 接口上使用`@Intercept`进行标注

```java
@SporeClient(baseUrl = "${test.baseUrl}")
@Intercept(handler = TokenInterceptor.class, include = {"/api/**"}, exclude = "/api/test1")
public interface HttpApi {
   @GET("/get")
   Result<HitokotoVO> get();
}
```

上面的`@Intercept`配置表示：拦截`HttpApi`接口下`/api/**`路径下（排除`/api/test1`）的请求，拦截处理器使用`TokenInterceptor`。


## 全局拦截器

### 全局应用拦截器

如果我们需要对整个系统的的`HTTP`请求执行统一的拦截处理，可以实现全局拦截器`GlobalInterceptor`, 并配置成`spring Bean`。

```java
@Component
public class CustomGlobalInterceptor implements GlobalInterceptor {

   @Override
   public Response intercept(Chain chain) throws IOException {
      System.out.println("===========执行全局拦截器===========");

      Request request = chain.request();
      Request newRequest = request.newBuilder()
              .addHeader("traceId", UUID.randomUUID().toString())
              .build();

      return chain.proceed(newRequest);
   }
}
```


### 日志打印

组件支持支持全局日志打印和声明式日志打印。

#### 全局日志打印

默认情况下，全局日志打印是开启的，默认配置如下：

```yaml
retrofit:
   # 全局日志打印配置
   global-log:
      # 启用日志打印
      enable: true
      # 全局日志打印级别
      log-level: info
      # 全局日志打印策略
      log-strategy: basic
```

四种日志打印策略含义如下：

1. `NONE`：No logs.
2. `BASIC`：Logs request and response lines.
3. `HEADERS`：Logs request and response lines and their respective headers.
4. `BODY`：Logs request and response lines and their respective headers and bodies (if present).

#### 声明式日志打印

如果只需要部分请求才打印日志，可以在相关接口或者方法上使用`@SporeLogging`注解。

#### 日志打印自定义扩展

如果需要修改日志打印行为，可以继承`LoggingInterceptor`，并将其配置成`Spring bean`。

#### 聚合日志打印

如果需要将同一个请求的日志聚合在一起打印，可配置`AggregateLoggingInterceptor`。

```java
@Bean
public LoggingInterceptor loggingInterceptor(RetrofitProperties retrofitProperties){
    return new AggregateLoggingInterceptor(retrofitProperties.getGlobalLog());
}
```

### 请求重试

组件支持支持全局重试和声明式重试。

#### 全局重试

全局重试默认关闭，默认配置项如下：

```yaml
retrofit:
  # 全局重试配置
  global-retry:
     # 是否启用全局重试
     enable: false
     # 全局重试间隔时间
     interval-ms: 100
     # 全局最大重试次数
     max-retries: 2
     # 全局重试规则
     retry-rules:
        - response_status_not_2xx
        - occur_io_exception
 ```

重试规则支持三种配置：

1. `RESPONSE_STATUS_NOT_2XX`：响应状态码不是`2xx`时执行重试
2. `OCCUR_IO_EXCEPTION`：发生IO异常时执行重试
3. `OCCUR_EXCEPTION`：发生任意异常时执行重试

#### 声明式重试

如果只有一部分请求需要重试，可以在相应的接口或者方法上使用`@Retry`注解。

#### 请求重试自定义扩展

如果需要修改请求重试行为，可以继承`RetryInterceptor`，并将其配置成`Spring bean`。


### 错误解码器

在`HTTP`发生请求错误(包括发生异常或者响应数据不符合预期)的时候，错误解码器可将`HTTP`相关信息解码到自定义异常中。你可以在`@SporeClient`注解的`errorDecoder()`
指定当前接口的错误解码器，自定义错误解码器需要实现`ErrorDecoder`接口：

### 微服务之间的HTTP调用

#### 继承`ServiceInstanceChooser`

用户可以自行实现`ServiceInstanceChooser`接口，完成服务实例的选取逻辑，并将其配置成`Spring Bean`。对于`Spring Cloud`
应用，可以使用如下实现。

```java
@Service
public class SpringCloudServiceInstanceChooser implements ServiceInstanceChooser {
    
   private LoadBalancerClient loadBalancerClient;

   @Autowired
   public SpringCloudServiceInstanceChooser(LoadBalancerClient loadBalancerClient) {
      this.loadBalancerClient = loadBalancerClient;
   }
   
   @Override
   public URI choose(String serviceId) {
      ServiceInstance serviceInstance = loadBalancerClient.choose(serviceId);
      Assert.notNull(serviceInstance, "can not found service instance! serviceId=" + serviceId);
      return serviceInstance.getUri();
   }
}
```

#### 指定`serviceId`和`path`

```java

@SporeClient(serviceId = "${jy-helicarrier-api.serviceId}", path = "/m/count")
public interface ApiCountService {}
```

## 全局拦截器

### 全局应用拦截器

如果我们需要对整个系统的的`HTTP`请求执行统一的拦截处理，可以实现全局拦截器`GlobalInterceptor`, 并配置成`spring Bean`。

```java
@Component
public class SourceGlobalInterceptor implements GlobalInterceptor {

   @Autowired
   private TestService testService;

   @Override
   public Response intercept(Chain chain) throws IOException {
      Request request = chain.request();
      Request newReq = request.newBuilder()
              .addHeader("source", "test")
              .build();
      testService.test();
      return chain.proceed(newReq);
   }
}
```

### 全局网络拦截器

实现`NetworkInterceptor`接口，并配置成`spring Bean`。

## 调用适配器

`Retrofit`可以通过`CallAdapterFactory`将`Call<T>`对象适配成接口方法的返回值类型。组件扩展了一些`CallAdapterFactory`实现：

1. `BodyCallAdapterFactory`
   - 同步执行`HTTP`请求，将响应体内容适配成方法的返回值类型。
   - 任意方法返回值类型都可以使用`BodyCallAdapterFactory`，优先级最低。
2. `ResponseCallAdapterFactory`
    - 同步执行`HTTP`请求，将响应体内容适配成`Retrofit.Response<T>`返回。
    - 只有方法返回值类型为`Retrofit.Response<T>`，才可以使用`ResponseCallAdapterFactory`。
3. 响应式编程相关`CallAdapterFactory`

**`Retrofit`会根据方法返回值类型选择对应的`CallAdapterFactory`执行适配处理**，目前支持的返回值类型如下：

- `String`：将`Response Body`适配成`String`返回。
- 基础类型(`Long`/`Integer`/`Boolean`/`Float`/`Double`)：将`Response Body`适配成上述基础类型
- 任意`Java`类型： 将`Response Body`适配成对应的`Java`对象返回
- `CompletableFuture<T>`: 将`Response Body`适配成`CompletableFuture<T>`对象返回
- `Void`: 不关注返回类型可以使用`Void`
- `Response<T>`: 将`Response`适配成`Response<T>`对象返回
- `Call<T>`: 不执行适配处理，直接返回`Call<T>`对象
- `Mono<T>`: `Project Reactor`响应式返回类型
- `Single<T>`：`Rxjava`响应式返回类型（支持`Rxjava2/Rxjava3`）
- `Completable`：`Rxjava`响应式返回类型，`HTTP`请求没有响应体（支持`Rxjava2/Rxjava3`）

```java
@SporeClient(baseUrl = "${test.baseUrl}")
public interface HttpApi {

   @POST("getString")
   String getString(@Body Person person);

   @GET("person")
   Result<Person> getPerson(@Query("id") Long id);

   @GET("person")
   CompletableFuture<Result<Person>> getPersonCompletableFuture(@Query("id") Long id);

   @POST("savePerson")
   Void savePersonVoid(@Body Person person);

   @GET("person")
   Response<Result<Person>> getPersonResponse(@Query("id") Long id);

   @GET("person")
   Call<Result<Person>> getPersonCall(@Query("id") Long id);

   @GET("person")
   Mono<Result<Person>> monoPerson(@Query("id") Long id);
   
   @GET("person")
   Single<Result<Person>> singlePerson(@Query("id") Long id);
   
   @GET("ping")
   Completable ping();
}

```

可以通过继承`CallAdapter.Factory`扩展`CallAdapter`。

组件支持通过`retrofit.global-call-adapter-factories`配置全局调用适配器工厂：
```yaml
retrofit:
  # 全局转换器工厂(组件扩展的`CallAdaptorFactory`工厂已经内置，这里请勿重复配置)
  global-call-adapter-factories:
    # ...
```

针对每个Java接口，还可以通过`@SporeClient.callAdapterFactories`指定当前接口采用的`CallAdapter.Factory`。

> 建议：将`CallAdapter.Factory`配置成`Spring Bean`


### 数据转码器

`Retrofit`使用`Converter`将`@Body`注解的对象转换成`Request Body`，将`Response Body`转换成一个`Java`对象，可以选用以下几种`Converter`：

- [Gson](https://github.com/google/gson): com.squareup.Retrofit:converter-gson
- [Jackson](https://github.com/FasterXML/jackson): com.squareup.Retrofit:converter-jackson
- [Moshi](https://github.com/square/moshi/): com.squareup.Retrofit:converter-moshi
- [Protobuf](https://developers.google.com/protocol-buffers/): com.squareup.Retrofit:converter-protobuf
- [Wire](https://github.com/square/wire): com.squareup.Retrofit:converter-wire
- [Simple XML](http://simple.sourceforge.net/): com.squareup.Retrofit:converter-simplexml
- [JAXB](https://docs.oracle.com/javase/tutorial/jaxb/intro/index.html): com.squareup.retrofit2:converter-jaxb
- fastJson：com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory

组件支持通过`retrofit.global-converter-factories`配置全局`Converter.Factory`，默认的是`retrofit2.converter.jackson.JacksonConverterFactory`。

如果需要修改`Jackson`配置，自行覆盖`JacksonConverterFactory`的`bean`配置即可。

```yaml
retrofit:
   # 全局转换器工厂
   global-converter-factories:
      - retrofit2.converter.jackson.JacksonConverterFactory
```

针对每个`Java`接口，还可以通过`@SporeClient.converterFactories`指定当前接口采用的`Converter.Factory`。

> 建议：将`Converter.Factory`配置成`Spring Bean`。

### 元注解

`@SporeClient`、`@Retry`、`@SporeLogging`、`@Resilience4jDegrade`等注解支持元注解、继承以及`@AliasFor`。 

```java

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@SporeClient(baseUrl = "${test.baseUrl}")
@SporeLogging(logLevel = LogLevel.WARN)
@Retry(intervalMs = 200)
public @interface MyRetrofitClient {

   @AliasFor(annotation = RetrofitClient.class, attribute = "converterFactories")
   Class<? extends Converter.Factory>[] converterFactories() default {GsonConverterFactory.class};

   @AliasFor(annotation = Logging.class, attribute = "logStrategy")
   LogStrategy logStrategy() default LogStrategy.BODY;
}
```

## 其他功能示例

### form参数

```java
@FormUrlEncoded
@POST("token/verify")
Object tokenVerify(@Field("source") String source,@Field("signature") String signature,@Field("token") String token);


@FormUrlEncoded
@POST("message")
CompletableFuture<Object> sendMessage(@FieldMap Map<String, Object> param);
```

### 文件上传

#### 创建MultipartBody.Part

```java
// 对文件名使用URLEncoder进行编码
public ResponseEntity importTerminology(MultipartFile file){
     String fileName=URLEncoder.encode(Objects.requireNonNull(file.getOriginalFilename()),"utf-8");
     okhttp3.RequestBody requestBody=okhttp3.RequestBody.create(MediaType.parse("multipart/form-data"),file.getBytes());
     MultipartBody.Part part=MultipartBody.Part.createFormData("file",fileName,requestBody);
     apiService.upload(part);
     return ok().build();
}
```

#### `HTTP`上传接口

```java
@POST("upload")
@Multipart
Void upload(@Part MultipartBody.Part file);
```

### 文件下载

#### `HTTP`下载接口

```java
@SporeClient(baseUrl = "https://img.ljcdn.com/hc-picture/")
public interface DownloadApi {

    @GET("{fileKey}")
    Response<ResponseBody> download(@Path("fileKey") String fileKey);
}

```

#### `HTTP`下载使用

```java
@SpringBootTest(classes = RetrofitTestApplication.class)
@RunWith(SpringRunner.class)
public class DownloadTest {
    @Autowired
    DownloadApi downLoadApi;

    @Test
    public void download() throws Exception {
        String fileKey = "6302d742-ebc8-4649-95cf-62ccf57a1add";
        Response<ResponseBody> response = downLoadApi.download(fileKey);
        ResponseBody responseBody = response.body();
        // 二进制流
        InputStream is = responseBody.byteStream();

        // 具体如何处理二进制流，由业务自行控制。这里以写入文件为例
        File tempDirectory = new File("temp");
        if (!tempDirectory.exists()) {
            tempDirectory.mkdir();
        }
        File file = new File(tempDirectory, UUID.randomUUID().toString());
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        byte[] b = new byte[1024];
        int length;
        while ((length = is.read(b)) > 0) {
            fos.write(b, 0, length);
        }
        is.close();
        fos.close();
    }
}
```

### 动态URL

使用`@url`注解可实现动态URL。此时，`baseUrl`配置任意合法url即可。例如： `http://github.com/` 。运行时只会根据`@Url`地址发起请求。

> 注意：`@url`必须放在方法参数的第一个位置，另外，`@GET`、`@POST`等注解上，不需要定义端点路径。

```java
 @GET
 Map<String, Object> test3(@Url String url,@Query("name") String name);
```

### `DELETE`请求添加请求体

```java
@HTTP(method = "DELETE", path = "/user/delete", hasBody = true)
```

### `GET`请求添加请求体

`okhttp3`自身不支持`GET`请求添加请求体，源码如下：

![image](https://user-images.githubusercontent.com/30620547/108949806-0a9f7780-76a0-11eb-9eb4-326d5d546e98.png)

![image](https://user-images.githubusercontent.com/30620547/108949831-1ab75700-76a0-11eb-955c-95d324084580.png)

作者给出了具体原因，可以参考: [issue](https://github.com/square/okhttp/issues/3154)

但是，如果实在需要这么做，可以使用：`@HTTP(method = "get", path = "/user/get", hasBody = true)`，使用小写`get`绕过上述限制。
