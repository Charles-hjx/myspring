package com.hjx.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * @Author: hjx
 * @Date: 2019/6/18 0:22
 * @Version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface RequestParam {
    String value() default "";

}
