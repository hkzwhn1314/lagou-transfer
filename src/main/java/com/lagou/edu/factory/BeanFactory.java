package com.lagou.edu.factory;

import com.alibaba.druid.util.StringUtils;
import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Service;
import com.lagou.edu.annotation.Transactional;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.reflections.Reflections;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 应癫
 * <p>
 * 工厂类，生产对象（使用反射技术）
 */
public class BeanFactory {

    /**
     * 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
     * 任务二：对外提供获取实例对象的接口（根据id获取）
     */




    private static Map<String, Object> iocMap = new HashMap();

    static {
        try {
            // 解决ioc问题

            Reflections reflections = new Reflections("com.lagou.edu");
            Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Service.class);
            for (Class<?> annotationClass : typesAnnotatedWith) {
                //获取注解 可以取到内部的value
                Service annotation = annotationClass.getAnnotation(Service.class);
                Object bean = annotationClass.newInstance();
                if (StringUtils.isEmpty(annotation.value())) {
                    String[] split = annotationClass.getName().split("\\.");
                    iocMap.put(split[split.length - 1], bean);
                } else {
                    iocMap.put(annotation.value(), bean);
                }
            }

            // 解决autoWired问题
            for (Map.Entry<String, Object> entrySet : iocMap.entrySet()) {
                Object value = entrySet.getValue();
                Class<?> clazz = value.getClass();
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Autowired.class) &&
                            field.getAnnotation(Autowired.class).required()) {
                        String[] names = field.getType().getName().split("\\.");
                        String name = names[names.length - 1];
                        field.setAccessible(true);
                        field.set(value, iocMap.get(name));
                    }
                }

                if (clazz.isAnnotationPresent(Transactional.class)) {
                    ProxyFactory proxyFactory = (ProxyFactory) BeanFactory.getBean("proxyFactory");
                    Class[] face = clazz.getInterfaces();
                    if (face != null && face.length > 0) {
                        value = proxyFactory.getJdkProxy(value);
                    } else {
                        value = proxyFactory.getCglibProxy(value);
                    }
                }
                iocMap.put(entrySet.getKey(), value);
            }


        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    // 任务二：对外提供获取实例对象的接口（根据id获取）
    public static Object getBean(String id) {
        return iocMap.get(id);
    }

}
