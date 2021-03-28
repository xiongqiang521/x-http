package com.xq.xhttp;

import com.xq.xhttp.http.annotation.HttpApiHost;
import com.xq.xhttp.http.annotation.HttpApiScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@HttpApiScan("com.xq.xhttp.controller")
public class XHttpApplication {

    public static void main(String[] args) {
        SpringApplication.run(XHttpApplication.class, args);
    }

}
