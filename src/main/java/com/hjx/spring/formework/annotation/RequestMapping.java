package com.hjx.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * @Author: hjx
 * @Date: 2019/6/18 0:15
 * @Version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE,ElementType.METHOD})
public @interface RequestMapping {
    String value() default "";
}
