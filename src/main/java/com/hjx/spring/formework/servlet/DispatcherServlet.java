package com.hjx.spring.formework.servlet;


import com.hjx.spring.formework.annotation.Autowried;
import com.hjx.spring.formework.annotation.Controller;
import com.hjx.spring.formework.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: hjx
 * @Date: 2019/6/15 23:34
 * @Version 1.0
 */
public class DispatcherServlet extends HttpServlet {
    /**
     * j加载配置文件
     */
    private Properties contextConfig;
    /**
     * ioc
     */
    private Map<String,Object> beanMap = new ConcurrentHashMap<String, Object>();

    /**
     * 存储bean名字 以便去 ioc 中查找
     */
    private List<String> classNames = new ArrayList<String>();




    @Override
    public void init(ServletConfig  config) throws ServletException {

        //开始初始化 进程
        //定位 定位配置文件的位置
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //加载 解析 加载配置文件 解析配置文件
        doScanner( contextConfig.getProperty("scanPackage"));
        //注册  讲解析完的 javaBean 注册到 ioc 中
        doRegistry();
        //自动依赖注入 在spring中是通过调用getBean方法才触发依赖注入
        doAutowired();


    }

    private void doAutowired() {
        if(beanMap.isEmpty()) return;
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if(!field.isAnnotationPresent(Autowried.class)) continue;
                Autowried autowried = field.getAnnotation(Autowried.class);
                String beanName = autowried.value().trim();
                //如果autowired 没有设置 value 的话 那么 默认按照 类型注入
                if("".equalsIgnoreCase(beanName)){
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);

                try {
                    //给 entry.getValue 当前对象的 某个field 赋值 赋 beanMap.get(beanName)
                    field.set(entry.getValue(),beanMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }


            }
        }

    }

    private void doRegistry() {
        if(classNames.isEmpty()) return;
        try{
            for(String className : classNames){
                //利用反射拿到字节码
                Class<?> clazz = Class.forName(className);
                //在sping中是利用多个子方法来处理的 parseArr() parseMap()
                if(clazz.isAnnotationPresent(Controller.class)){
                    String beanName = LowerFirstCase(clazz.getSimpleName());
                    //spring 这里不会直接放一个 instance 而是 BeanDefinition TODO 这里的BeanDefinition 是什么？？
                    beanMap.put(beanName,clazz.newInstance());
                }else if(clazz.isAnnotationPresent(Service.class)){
                    Service service = clazz.getAnnotation(Service.class);
                    //默认使用类名首字母小写注入
                    //如果自定义了beanName，那么优先使用自定义的 beanName

                    //在spring中会分别调用不同的方法 autowiredByName autowiredByType
                    String beanName = service.value();
                    if("".equalsIgnoreCase(beanName.trim())){
                        beanName = LowerFirstCase(clazz.getSimpleName());
                    }

                    Object instance = clazz.newInstance();
                    beanMap.put(beanName,instance);

                    //如果是一个接口
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (int i = 0; i < interfaces.length; i++) {
                        Class<?> anInterface = interfaces[i];
                        beanMap.put(anInterface.getName(),instance);
                    }

                }else{
                    continue;
                }
            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doScanner(String packageName) {
        //将配置文件中的 包路径转化为 文件路径
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replace("\\.","/"));
        System.out.println("url.getFile()====>>>>"+url.getFile());
        File classDir = new File(url.getFile());
        File[] files = classDir.listFiles();
        //递归的方式去获取文件 然后将非文件夹 的文件 筛选出来，加入到 classNames中
        for (File file : files) {
            if(file.isDirectory()){
                doScanner(packageName + "." +  file.getName());
            }else{
                classNames.add(packageName+"."+file.getName().replace(".class",""));
            }
        }

    }

    private void doLoadConfig(String location) {
        //在spring中是通过 Reader 去查找和定位的

        InputStream in = this.getClass().getClassLoader().getResourceAsStream(location.replace("classPath",""));
        try {
            //加载配置文件，供第二步去扫描解析
            contextConfig.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != in){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("=========>>>>>>><<<<<<<<==============");
    }


    /**
     * 首字母小写
     * @return
     */
    private String LowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
