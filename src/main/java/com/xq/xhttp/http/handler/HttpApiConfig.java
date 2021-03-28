package com.xq.xhttp.http.handler;

import org.springframework.context.annotation.Configuration;

public class HttpApiConfig {
    private String basePackage;

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
}
