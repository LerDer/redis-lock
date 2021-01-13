# redis-lock
This is a Java Project that can prevent repeat http request. With Annotation and AOP,  it is very easy and no invade.


### 使用方式
要配置`Redis`

```java
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.password=
spring.redis.database=0
```

加入依赖

```xml
<dependency>
    <groupId>top.lww0511</groupId>
    <artifactId>redis-lock</artifactId>
    <version>1.0.3</version>
</dependency>
```

在方法上添加注解 `Lock`

```java
@Lock(value = 10, distributed = false, hard = true)
@GetMapping(value = "/hi", name = "log")
public HttpResult hello() {
    return HttpResult.success("Hello");
}
```

-  `@Lock`：默认的，表示分布式，锁定时间3秒，非强制模式
- `@Lock(value = 10, distributed = false, hard = true)`：表示不是分布式的，锁定时间10秒，且是强制模式。

其实不是特殊需求，一般一个`@Lock`就够了。

### 效果

#### 非分布式绑定了`IP`，并且是强制模式，虽然执行完了，方法还是不能访问的。

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/32fa21de1d0040beae5d72b51dda36f1~tplv-k3u1fbpfcp-zoom-1.image)

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/280e3b58dc724779a2808943b9b4af83~tplv-k3u1fbpfcp-zoom-1.image)

#### 当只有一个`@Lock`时

没有绑定`IP`，且虽然锁定时间3秒，但是方法执行完毕，自动释放了。
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/81abb37158be4aedbe4b4015d2f6efb9~tplv-k3u1fbpfcp-zoom-1.image)

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b57fb2af56bc4227a13e96f080b2e531~tplv-k3u1fbpfcp-zoom-1.image)

### 最后

其中还有一些工具类，全局异常拦截器等等，还有`META-INF/spring.factories`。（全局异常拦截器已删除，自己配置更灵活）


这个已经发布到`Maven`中心仓库了，所以直接添加依赖就可以使用了。不需要自己打包发布到私服。

- 坐标


```xml
<!-- https://mvnrepository.com/artifact/top.lww0511/redis-lock -->
<dependency>
    <groupId>top.lww0511</groupId>
    <artifactId>redis-lock</artifactId>
    <version>1.0.3</version>
</dependency>
```


欢迎大家关注我的公众号，共同学习，一起进步。加油🤣

搜索：**南诏Blog** 
