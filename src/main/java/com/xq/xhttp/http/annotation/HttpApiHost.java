package com.xq.xhttp.http.annotation;

import com.xq.xhttp.http.handler.HttpHostInterface;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpApiHost {
    @AliasFor("host")
    String value() default "";

    @AliasFor("value")
    String host() default "";

    Class<? extends HttpHostInterface> getClassType() default HttpHostInterface.DefaultHttpHostImpl.class;
}
