package com.smallfatcat.ioc;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * @author zsz
 * 自定义ClassPathXmlApplicationContext 实现  ApplicationContext接口
 * 实现一个IOC容器
 * 1:根据需求编写XML配置文件，配置需要创建的bean
 * 2:编写程序读取XML文件，获取bean相关信息，类属性，id
 * 3:根据第二步获取的信息，结合反射动态创建对象，同时完成属性的赋值
 * 4：将创建好的bean存入Map集合。设置key-value映射，key就是bean的id，value就是bean对象
 * 5:提供方法从Map中通过id获取对应的value
 * @date 2022/9/3
 */
public class MyClassPathXmlApplicationContext implements ApplicationContext {

    private Map<String,Object> iocMap;

    public MyClassPathXmlApplicationContext(String path) {
        iocMap = new HashMap<String, Object>();
        //解析xml----需要导入dom4j相关依赖
        parseXml(path);

    }
    public void parseXml(String path){
        SAXReader saxReader = new SAXReader();
        try {
            //将xml配置文件转化成一个Document对象
            //Document指整个文档对象
            Document document = saxReader.read("src/main/resources/" + path);
            //通过getRootElement方法获取xml里面beans
            //所以Element对象就是表示xml里面的元素beans
            Element rootElement = document.getRootElement();
            //通过迭代一层层获取信息
            Iterator<Element> iterator = rootElement.elementIterator();
            while (iterator.hasNext()){
                Element bean = iterator.next();
                String beanId = bean.attributeValue("id");
                String beanName = bean.attributeValue("class");
                //通过反射动态创建对象
                Class<?> aClass = Class.forName(beanName);
                //获取无参构造方法
                Constructor<?> constructor = aClass.getConstructor();
                //创建一个对象
                Object object = constructor.newInstance();
                //给属性赋值
                //继续迭代获取信息
                Iterator<Element> iterator1 = bean.elementIterator();
                while (iterator1.hasNext()){
                    Element property = iterator1.next();
                    String propertyName = property.attributeValue("name");
                    String propertyValue = property.attributeValue("value");
                    //通过反射获取set方法
                    //username-setUsername password-setPassword
                    String methodName = "set" + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
                    //获取属性
                    Field field = aClass.getDeclaredField(propertyName);
                    Method method = aClass.getMethod(methodName,field.getType());
                    //根据数据的属性进行类型转换--把propertyValue转换成对应的数据类型
                    Object value = propertyValue;
                    switch (field.getType().getName()){
                        case "java.lang.Integer":
                            value = Integer.parseInt(propertyValue);
                            break;
                        case "java.lang.Double":
                            value = Double.parseDouble(propertyValue);
                    }
                    //调用方法
                    method.invoke(object,value);
                }
                //存入iocMap中
                iocMap.put(beanId,object);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getApplicationName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public long getStartupDate() {
        return 0;
    }

    @Override
    public ApplicationContext getParent() {
        return null;
    }

    @Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return null;
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return null;
    }

    @Override
    public boolean containsLocalBean(String s) {
        return false;
    }

    @Override
    public boolean containsBeanDefinition(String s) {
        return false;
    }

    @Override
    public int getBeanDefinitionCount() {
        return 0;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return new String[0];
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType resolvableType) {
        return new String[0];
    }

    @Override
    public String[] getBeanNamesForType(Class<?> aClass) {
        return new String[0];
    }

    @Override
    public String[] getBeanNamesForType(Class<?> aClass, boolean b, boolean b1) {
        return new String[0];
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> aClass) throws BeansException {
        return null;
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> aClass, boolean b, boolean b1) throws BeansException {
        return null;
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> aClass) {
        return new String[0];
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> aClass) throws BeansException {
        return null;
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String s, Class<A> aClass) throws NoSuchBeanDefinitionException {
        return null;
    }

    @Override
    //获取bean的方法
    public Object getBean(String s) throws BeansException {
        return iocMap.get(s);
    }

    @Override
    public <T> T getBean(String s, Class<T> aClass) throws BeansException {
        return null;
    }

    @Override
    public Object getBean(String s, Object... objects) throws BeansException {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> aClass) throws BeansException {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> aClass, Object... objects) throws BeansException {
        return null;
    }

    @Override
    public boolean containsBean(String s) {
        return false;
    }

    @Override
    public boolean isSingleton(String s) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public boolean isPrototype(String s) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public boolean isTypeMatch(String s, ResolvableType resolvableType) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public boolean isTypeMatch(String s, Class<?> aClass) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public Class<?> getType(String s) throws NoSuchBeanDefinitionException {
        return null;
    }

    @Override
    public String[] getAliases(String s) {
        return new String[0];
    }

    @Override
    public void publishEvent(Object o) {

    }

    @Override
    public String getMessage(String s, Object[] objects, String s1, Locale locale) {
        return null;
    }

    @Override
    public String getMessage(String s, Object[] objects, Locale locale) throws NoSuchMessageException {
        return null;
    }

    @Override
    public String getMessage(MessageSourceResolvable messageSourceResolvable, Locale locale) throws NoSuchMessageException {
        return null;
    }

    @Override
    public Environment getEnvironment() {
        return null;
    }

    @Override
    public Resource[] getResources(String s) throws IOException {
        return new Resource[0];
    }

    @Override
    public Resource getResource(String s) {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }
}
