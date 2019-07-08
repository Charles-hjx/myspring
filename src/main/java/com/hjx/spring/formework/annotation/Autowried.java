package com.hjx.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * @Author: hjx
 * @Date: 2019/6/18 0:19
 * @Version 1.0
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowried {
    String value() default "";

}
