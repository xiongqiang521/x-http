package com.xq.xhttp.http.annotation;


import com.xq.xhttp.http.handler.HttpApiScanRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(HttpApiScanRegister.class)
public @interface HttpApiScan {

    String[] value() default {};
}
