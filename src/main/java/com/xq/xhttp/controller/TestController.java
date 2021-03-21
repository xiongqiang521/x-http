package com.xq.xhttp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private HttpRestApi httpRestApi;

    @RequestMapping("/")
    public ResponseEntity<?> test() {
        Map<String, String> data = new HashMap<>();
        data.put("username", "aaa");
        data.put("password", "aaa");
        ResponseEntity<String> demo = httpRestApi.demo("123", null, data, "token");

        System.out.println(httpRestApi.demo2("321", data, null));
        System.out.println(httpRestApi.demo3("321", data, null));
        return demo;
    }

    @RequestMapping("/api/{id}")
    public String response2(@PathVariable String id, String username, String password) {
        System.out.println(username);
        System.out.println(password);
        return "get ok";
    }

}

