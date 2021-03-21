package com.xq.xhttp.http.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(HttpApiHeaders.class)
public @interface HttpApiHeader {
    String value();

    String key();
}


