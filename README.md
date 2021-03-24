## 已实现功能：

声明式调用http接口，暂时仅支持GET、POST、PUT、DELETE（后续会增加其他方法）
动态请求头
路劲支持/api/{id}变量形式入参

## 使用指导：

### 下载源码包：

github地址：https://github.com/xiongqiang521/x-http.git
gitee地址：https://gitee.com/xiongqiang521/x-http.git
### 本地安装：

maven install

### 项目中引用：

```xml
<dependency>
    <groupId>com.xq</groupId>
    <artifactId>x-http</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### SpringBoot启动类 添加注解

@

### 项目中创建接口调用远程http接口

```java

@HttpApiHost("http://localhost:8080")
public interface HttpRestApi {

    @HttpApiPath(path = "/api/{id}", method = HttpMethod.GET)
    public String demo(@Param Map<String, String> param);

    @HttpApiPath(path = "/api/{id}", method = HttpMethod.POST)
    @HttpApiHeader(key = "1", value = "1")
    @HttpApiHeader(key = "2", value = "2")
    public ResponseEntity<String> demo(
            @PathVal("id") String id,
            @Data Object data,
            @Header("X-Auth-Type") String token
    );
}
```

其中:

- @HttpApiHost为此接口下方法的远程服务地址
- @HttpApiPath中path为路劲，method请求方法，默认为get方法，暂时仅支持GET、POST、PUT、DELETE。路劲支持{id}的形式与参数中@PathVal("id")对应
- @HttpApiHeader为请求头，建议将固定的请求头信息放在此处，一个方法上可以多个此注解
- @PathVal为path路径中的{var}变量，内容非必填，不填时按参数顺序依次拼接在path中
- @Param为请求url中的参数，类型为Map<string, string>，暂不支持其他类型
- @Data为请求体，内部会json化此对象，尽量使用对象类型，不要使用json字符串。get、delete请求中此参数没用
- @Header请求头，key-value形式。与@HttpApiHeader作用类似，但此此处value为参数可变
  返回结果支持ResponseEntity<T>和对象类型

### service中调用

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HttpService {
    @Autowired
    private HttpRestApi httpRestApi;

    public String demo(String id, Object data, String token) {
        return httpRestApi.demo(id, null, data, token);
    }
}
```
