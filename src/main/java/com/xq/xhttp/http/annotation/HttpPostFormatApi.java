package com.xq.xhttp.http.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpPostFormatApi {
    @AliasFor("path")
    String value() default "";

    @AliasFor("value")
    String path() default "";

}
