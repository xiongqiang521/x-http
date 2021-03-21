package com.xq.xhttp.http.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpMethod;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpApiPath {
    @AliasFor("path")
    String value() default "";

    @AliasFor("value")
    String path() default "";

    HttpMethod method() default HttpMethod.GET;

}
