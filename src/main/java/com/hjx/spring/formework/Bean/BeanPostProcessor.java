package com.hjx.spring.formework.Bean;

/**
 *
 * 用于做事件监听
 *
 * @Author: hjx
 * @Date: 2019/6/30 15:19
 * @Version 1.0
 */
public class BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName)   {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)   {
        return bean;
    }

}
