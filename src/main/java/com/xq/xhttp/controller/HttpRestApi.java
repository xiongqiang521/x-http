package com.xq.xhttp.controller;


import com.xq.xhttp.http.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@HttpApiHost("http://localhost:8080")
public interface HttpRestApi {

    @HttpApiPath(path = "/api/{id}", method = HttpMethod.POST)
    @HttpApiHeader(key = "1", value = "1")
    @HttpApiHeader(key = "2", value = "2")
    ResponseEntity<String> demo(
            @PathVal("id") String id,
            @Param Map<String, String> param,
            @Data Object data,
            @Header("X-Auth-Type") String token
    );

    @HttpApiPath(path = "/api/{id}", method = HttpMethod.GET)
    String demo2(
            @PathVal("id") String id,
            @Param Map<String, String> param,
            @Data Object data
    );

    public default String demo3(String id, Map<String, String> param, Object data) {
        return demo2(id, param, data);
    }


}
