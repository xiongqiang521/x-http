package com.xq.xhttp.http.handler;

import com.xq.xhttp.http.execption.XHttpException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HttpBuild {
    private final static RestTemplate restTemplate = HttpPoolConfiguration.restTemplate();
    private final List<String> pathParams = new ArrayList<>();
    private final Map<String, String> pathParamMap = new HashMap<>();
    private final HttpHeaders headers = new HttpHeaders();
    private String url = "http://localhost:8080/";
    private String path = "/";
    private Object data = null;
    private Map<String, String> param = new HashMap<>();
    private HttpMethod method = HttpMethod.GET;

    private HttpBuild() {

    }

    public static HttpBuild build() {
        return new HttpBuild();
    }

    private static String makePath(
            String path,
            Map<String, String> param,
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
        if (param == null) {
            return path;
        }
        StringBuffer sb = new StringBuffer(path);
        sb.append("?");
        param.forEach((key, val) -> {
            sb.append(key).append("=").append(val).append("&");
        });
        return sb.toString();
    }

    /**
     * 实现StringBuilder的replaceAll
     *
     * @param stb
     * @param oldStr 被替换的字符串
     * @param newStr 替换oldStr
     * @return
     */
    public static StringBuffer replaceAll(StringBuffer stb, String oldStr, String newStr) {
        if (stb == null || oldStr == null || newStr == null || stb.length() == 0 || oldStr.length() == 0)
            return stb;
        int index = stb.indexOf(oldStr);
        if (index > -1 && !oldStr.equals(newStr)) {
            int lastIndex = 0;
            while (index > -1) {
                stb.replace(index, index + oldStr.length(), newStr);
                lastIndex = index + newStr.length();
                index = stb.indexOf(oldStr, lastIndex);
            }
        }
        return stb;
    }

    public HttpBuild param(Map<String, String> param) {
        this.param = param;
        return this;
    }

    public HttpBuild host(String host) {
        this.url = host;
        return this;
    }

    public HttpBuild data(Object data) {
        this.data = data;
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

    public HttpBuild method(HttpMethod method) {
        this.method = method;
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
        HttpEntity<?> httpEntity = new HttpEntity<>(this.data, this.headers);
        String path = makePath(this.path, this.param, this.pathParams, this.pathParamMap);
        URI uri = null;
        try {
            uri = new URI(this.url + path);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new XHttpException(String.format("uri创建错误。host -> %s, path -> %s", this.url, path));
        }
        ResponseEntity<T> exchange = restTemplate.exchange(uri, this.method, httpEntity, clazz);
        return ResponseEntity.status(exchange.getStatusCode()).body(exchange.getBody());
    }


}
