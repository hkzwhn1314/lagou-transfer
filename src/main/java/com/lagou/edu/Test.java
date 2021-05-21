//package com.lagou.edu;
//
//import com.alibaba.druid.util.StringUtils;
//import com.lagou.edu.annotation.Autowired;
//import com.lagou.edu.annotation.Service;
//import com.lagou.edu.annotation.Transactional;
//import com.lagou.edu.factory.BeanFactory;
//import com.lagou.edu.factory.ProxyFactory;
//import org.reflections.Reflections;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//public class Test {
//    public static void main(String[] args) {
//        Map<String, Object> iocMap = new HashMap();
//        try {
//            // 解决ioc问题
//
//            Reflections reflections = new Reflections("com.lagou.edu");
//            Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Service.class);
//            for (Class<?> annotationClass : typesAnnotatedWith) {
//                //获取注解 可以取到内部的value
//                Service annotation = annotationClass.getAnnotation(Service.class);
//                Object bean = annotationClass.newInstance();
//                if (StringUtils.isEmpty(annotation.value())) {
//                    String[] split = annotationClass.getName().split("\\.");
//                    iocMap.put(split[split.length - 1], bean);
//                } else {
//                    iocMap.put(annotation.value(), bean);
//                }
//            }
//
//            // 解决autoWired问题
//            for (Map.Entry<String, Object> entrySet : iocMap.entrySet()) {
//                Object value = entrySet.getValue();
//                Class<?> clazz = value.getClass();
//                Field[] fields = clazz.getDeclaredFields();
//                for (Field field : fields) {
//                    if (field.isAnnotationPresent(Autowired.class) &&
//                            field.getAnnotation(Autowired.class).required()) {
//                        String[] names = field.getType().getName().split("\\.");
//                        String name = names[names.length - 1];
//                        field.setAccessible(true);
//                        field.set(value, iocMap.get(name));
//                    }
//                }
//
//                if (clazz.isAnnotationPresent(Transactional.class)) {
//                    ProxyFactory proxyFactory = (ProxyFactory) BeanFactory.getBean("proxyFactory");
//                    Class[] face = clazz.getInterfaces();
//                    if (face != null && face.length > 0) {
//                        value = proxyFactory.getJdkProxy(value);
//                    } else {
//                        value = proxyFactory.getCglibProxy(value);
//                    }
//                }
//                iocMap.put(entrySet.getKey(), value);
//            }
//
//System.out.println("");
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//    }
//}
