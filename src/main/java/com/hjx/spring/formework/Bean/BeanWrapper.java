package com.hjx.spring.formework.Bean;

import com.hjx.spring.formework.core.FactoryBean;

/**
 * @Author: hjx
 * @Date: 2019/6/23 13:54
 * @Version 1.0
 */
public class BeanWrapper extends FactoryBean {

    //这里会用到观察者模式
    //支持事件响应，会有一个监听
    private BeanPostProcessor postProcessor;

    private Object wrapperInstance;
    //原生的 通过反射new 出来
    private Object originalInstance;

    public BeanWrapper(Object instant) {
        this.wrapperInstance = instant;
        this.originalInstance = instant;
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public BeanPostProcessor getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(BeanPostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }

    /**
     * 返回代理以后的Class（$proxy0之类的）
     * @return
     */
    public Class<?> getWrapperClass(){
        return this.wrapperInstance.getClass();
    }

}
