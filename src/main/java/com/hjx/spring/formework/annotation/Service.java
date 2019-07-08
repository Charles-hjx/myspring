package com.hjx.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * @Author: hjx
 * @Date: 2019/6/18 0:22
 * @Version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";

}
