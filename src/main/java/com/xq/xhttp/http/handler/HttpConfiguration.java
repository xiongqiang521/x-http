package com.xq.xhttp.http.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpConfiguration {

    @Bean
    public HttpAnnotationHandler helloService() {
        return new HttpAnnotationHandler();
    }
}
