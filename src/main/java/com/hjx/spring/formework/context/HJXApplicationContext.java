package com.hjx.spring.formework.context;

import com.hjx.spring.formework.Bean.BeanDefinition;
import com.hjx.spring.formework.Bean.BeanPostProcessor;
import com.hjx.spring.formework.Bean.BeanWrapper;
import com.hjx.spring.formework.annotation.Autowried;
import com.hjx.spring.formework.annotation.Controller;
import com.hjx.spring.formework.annotation.Service;
import com.hjx.spring.formework.context.support.BeanDefinitionReader;
import com.hjx.spring.formework.core.BeanFactory;

import java.io.File;
import java.io.FileDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 该类的作用相当于ClassPathXmlApplicationContext
 *
 * @Author: hjx
 * @Date: 2019/6/23 13:40
 * @Version 1.0
 */
public class HJXApplicationContext implements BeanFactory {


    private String [] configLocations;

    private BeanDefinitionReader reader;

    private Map<String,BeanDefinition> beanDefinitionMap = new HashMap<String, BeanDefinition>();

    //用来保证注册式单例的容器
    private Map<String,Object> beanCacheMap = new HashMap<String,Object>();
    //存储所有的被代理过的对象
    private Map<String,BeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, BeanWrapper>();

    public HJXApplicationContext(String ... locations) {
        this.configLocations = locations;
        this.refresh();
    }

    public void refresh(){

        //定位
        reader = new BeanDefinitionReader(configLocations);
        //加载 解析
        List<String> beanDefinitions = reader.loadDefinitions();
        //注册
        doRegistry(beanDefinitions);
        //依赖注入(在spring中 依赖注入是 lazy的 不在这里执行。依赖注入 是从 getBean方法开始的
        // 这里演示的是当lazy == false的时候 自动调用 getBean() 方法)
        doAutorited();
    }

    /**
     * 开始执行 自动化的依赖注入
     */
    private void doAutorited() {

        for(Map.Entry<String,BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()){
        }

    }

    /**
     * 真正开始注入
     * @param beanName bean 的名字
     * @param instance 实例
     */
    public void populateBean(String beanName,Object instance){
        Class<?> clazz = instance.getClass();

        // 当 这两个注解都没有的时候 就不注入了。（只演示这两个注解）
        if(!(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class))){
            return;
        }

        //获取所有的属性
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if(!field.isAnnotationPresent(Autowried.class)){
                continue;
            }
            Autowried autowried = field.getAnnotation(Autowried.class);
            String autowriedBeanName = autowried.value().trim();
            if("".equalsIgnoreCase(autowriedBeanName)){
                autowriedBeanName = field.getType().getName();
            }

            field.setAccessible(true);

            try {
                field.set(instance,this.beanWrapperMap.get(autowriedBeanName).getClass());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }


    }

    /**
     * 真正的将 beanDefinitions 注册到ioc容器中(这里的ioc容器命名为beanDefinitionMap)
     * @param beanDefinitions
     */
    private void doRegistry(List<String> beanDefinitions) {

        //beanName 有三种情况 （就是 map 中的key）
        //1.默认是类名首字母小写
        //2.自定义名字
        //3.接口名字

        try {
            for(String className:beanDefinitions){
                Class<?> beanClass = Class.forName(className);
                //如果是 接口 是不能实力化的 用他的 实现类来实例化
                if(beanClass.isInterface()) continue;
                BeanDefinition beanDefinition = reader.registerBean(className);
                if(beanDefinition !=null){
                    beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
                }

                //容器中 再保存一份 beanDefinition 只是key 不同
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    Class<?> anInterface = interfaces[i];
                    //如果是多个实现类（即 key（ket就是接口名）相同，实现类不同 ） 只能覆盖 （spring 会抛出异常），
                    //这个时候 解决的方法就是自定义 beanName（key）
                    this.beanDefinitionMap.put(anInterface.getName(),beanDefinition);
                }

                //容器初始化完毕 （这里暂时不考虑 自定义beanName的情况）

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    /**
     * 这里 是 通过读取beanDefinition中的信息 再通过反射信息 创建一个实力并返回
     * spring 的做法是 ：spring 不会把原始的 对象放出去，会用一个BeanWrapper 来进行一次装饰
     * 装饰器模式：
     * 1，保留原来的oop关系
     * 2. 我需要对他进行扩展 增强（即 为以后的 AOP 的实现打基础）
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        String beanClassName = beanDefinition.getBeanClassName();

        //生成通知事件
        BeanPostProcessor beanPostProcessor = new BeanPostProcessor();



        try{
            Object instance = instantionBean(beanDefinition);
            if(instance == null){
                return null;
            }
            //在实例初始化以前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance,beanName);

            BeanWrapper beanWrapper = new BeanWrapper(instance);
            beanWrapper.setPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(beanName,beanWrapper);

            //实例初始化以后 调用一次
            beanPostProcessor.postProcessAfterInitialization(instance,beanName);

            //通过这样的 调用 就相当于 留出了可操作的空间？？
            return this.beanWrapperMap.get(beanName).getWrapperInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 返回一个实例bean
     * @param beanDefinition
     * @return
     */
    private Object instantionBean(BeanDefinition beanDefinition){
        String beanClassName = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            if(beanCacheMap.containsKey(beanClassName)){
                instance = beanCacheMap.get(beanClassName);
            }else{
                Class<?> clazz = Class.forName(beanClassName);
                instance = clazz.newInstance();
                beanCacheMap.put(beanClassName,instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return instance;

    }
}
