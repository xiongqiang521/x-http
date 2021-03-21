package com.xq.xhttp.http.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpApiHeaders {
    HttpApiHeader[] value();
}


