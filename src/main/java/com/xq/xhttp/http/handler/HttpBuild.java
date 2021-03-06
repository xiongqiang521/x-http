package com.xq.xhttp.http.handler;

import com.xq.xhttp.http.execption.Try;
import com.xq.xhttp.http.execption.XHttpException;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class HttpBuild {
    private final static RestTemplate restTemplate = HttpPoolConfiguration.restTemplate();
    private final List<String> pathParams = new ArrayList<>();
    private final Map<String, String> pathParamMap = new HashMap<>();
    private final HttpHeaders headers = new HttpHeaders();
    private String host = "http://localhost:8080";
    private String path = "/";
    private Object body = null;
    private Map<String, String> queryParam = new HashMap<>();
    private HttpMethod method = HttpMethod.GET;

    // 是否为表单提交，请提踢的类型有区别
    private boolean isForm = false;

    private HttpBuild() {

    }

    public static HttpBuild build() {
        return new HttpBuild();
    }

    private static String makePath(
            String path,
            Map<String, String> queryParam,
            List<String> pathParams,
            Map<String, String> pathParamMap) {

        path = path.replaceAll("\\s", "");
        for (Map.Entry<String, String> entry : pathParamMap.entrySet()) {
            String tmp = "{" + entry.getKey() + "}";
            if (!path.contains(tmp)) {
                throw new RuntimeException("path 路径与 @path参数不一致");
            }
            path = path.replace(tmp, entry.getValue());
        }
        // 替换@path中无value的参数
        for (String pathParam : pathParams) {
            String tmp = path.replaceFirst("\\{[a-zA-Z0-9]*\\}", pathParam);
            if (path.equals(tmp)) {
                throw new RuntimeException("path 路径与 @path参数不一致");
            }
            path = tmp;
        }
        if (queryParam == null) {
            return path;
        }

        return queryParam.entrySet().stream()
                .map(Try.throwException(
                        entry -> String.format("%s=%s", entry.getKey(), URLEncoder.encode(entry.getValue(), "UTF-8")),
                        new RuntimeException()))
                .collect(Collectors.joining("&", path + "?", ""));
    }

    public HttpBuild queryParam(Map<String, String> queryParam) {
        this.queryParam = queryParam;
        return this;
    }

    public HttpBuild host(String host) {
        this.host = host;
        return this;
    }

    public HttpBuild get() {
        this.method = HttpMethod.GET;
        return this;
    }

    public HttpBuild delete() {
        this.method = HttpMethod.DELETE;
        return this;
    }

    public HttpBuild post(Object body) {
        this.method = HttpMethod.POST;
        this.body = body;
        return this;
    }

    public HttpBuild put(Object body) {
        this.method = HttpMethod.PUT;
        this.body = body;
        return this;
    }

    /**
     * 请使用 post()    get()   put()   delete()
     *
     * @param method
     * @return
     */
    @Deprecated
    public HttpBuild method(HttpMethod method) {
        this.method = method;
        return this;
    }

    /**
     * @param body
     * @return
     */
    @Deprecated
    public HttpBuild body(Object body) {
        this.body = body;
        return this;
    }

    public HttpBuild path(String path) {
        this.path = path;
        return this;
    }

    public HttpBuild addPathParam(String key, String value) {
        this.pathParamMap.put(key, value);
        return this;
    }

    public HttpBuild addPathParam(String pathParam) {
        this.pathParams.add(pathParam);
        return this;
    }

    public HttpBuild addHeader(String name, String value) {
        this.headers.add(name, value);
        return this;
    }

    public <T> T exchangeObject(Class<T> clazz) {
        ResponseEntity<T> exchange = exchange(clazz);
        return exchange.getBody();
    }

    public <T> ResponseEntity<T> exchange(Class<T> clazz) {
        // 是否为表单提交
        if (MediaType.APPLICATION_FORM_URLENCODED.equalsTypeAndSubtype(this.headers.getContentType())
                && !(this.body instanceof MultiValueMap)) {
            throw new XHttpException("表单提交时body的类型应为MultiValueMap");
        }

        HttpEntity<?> httpEntity = new HttpEntity<>(this.body, this.headers);
        String path = makePath(this.path, this.queryParam, this.pathParams, this.pathParamMap);
        URI uri = null;
        try {
            uri = new URI(this.host + path);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new XHttpException(String.format("uri创建错误。host -> %s, path -> %s", this.host, path));
        }
        ResponseEntity<T> exchange = restTemplate.exchange(uri, this.method, httpEntity, clazz);
        return ResponseEntity.status(exchange.getStatusCode()).body(exchange.getBody());
    }


}
