package com.hjx.spring.formework.core;

/**
 * @Author: hjx
 * @Date: 2019/6/23 13:44
 * @Version 1.0
 */
public interface BeanFactory {

    /**
     * 根据beanName 从ioc中获取一个实力bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName);
}
