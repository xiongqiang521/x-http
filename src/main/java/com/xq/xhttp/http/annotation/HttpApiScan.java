package com.xq.xhttp.http.annotation;


import com.xq.xhttp.http.handler.HttpApiScanRegister;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(HttpApiScanRegister.class)
@ComponentScan("com.xq.xhttp.http.handler")
public @interface HttpApiScan {

    String[] value() default {};
}
