package com.hjx.spring.formework.context.support;

import com.hjx.spring.formework.Bean.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * 该类用于配置文件的 查找 读取 解析
 *
 * @Author: hjx
 * @Date: 2019/6/23 13:52
 * @Version 1.0
 */
public class BeanDefinitionReader {

    private Properties config = new Properties();

    private List<String> registryBeanClasses = new ArrayList<String>();

    private final String SCAN_PACKAGE = "scanPackage";

    /**
     * 构造方法中 加载配置文
     * @param locations
     */
    public BeanDefinitionReader(String ... locations) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //扫描包
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    public List<String> loadDefinitions(){
        return this.registryBeanClasses;
    }

    /**
     * 递归扫描所有 相关的 class 保存到list 中扫描包名
     * @param packageName 包名
     */
    private void doScanner(String packageName){
        String name = config.getProperty(packageName);
        name.replace("\\.","/");
        URL url = this.getClass().getResource("/"+name);
        File file  = new File(url.getPath());
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file1 = files[i];
            if(file1.isDirectory()){
                doScanner(packageName+"/"+file.getName());
            }else{
                registryBeanClasses.add(packageName+"."+file.getName().replace(".class",""));
            }
        }
    }




    /**
     * 每注册一个ClassName 就返回一个beanDefinition
     * beanDefinition 作用： 对配置信息进行包装
     * @param className
     * @return
     */
    public BeanDefinition registerBean(String className){
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName(className);
        beanDefinition.setFactoryBeanName(lowerFirstCase(className.substring(className.lastIndexOf(".")+1)));
        return beanDefinition;
    }

    /**
     * 用于将当前的 配置信息 返回出去
     * @return
     */
    public Properties getConfig(){
        return this.config;
    }

    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
