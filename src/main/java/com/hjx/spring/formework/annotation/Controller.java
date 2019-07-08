package com.hjx.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * @Author: hjx
 * @Date: 2019/6/18 0:12
 * @Version 1.0
 */
@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
