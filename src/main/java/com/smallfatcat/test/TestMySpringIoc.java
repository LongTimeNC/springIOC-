package com.smallfatcat.test;

import com.smallfatcat.ioc.MyClassPathXmlApplicationContext;
import com.smallfatcat.pojo.User;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.MalformedParameterizedTypeException;

/**
 * @author zsz
 * @Description
 * @date 2022/9/3
 */
public class TestMySpringIoc {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new MyClassPathXmlApplicationContext("spring-ioc.xml");
        User user = (User)applicationContext.getBean("user");
        System.out.println(user);

    }
}
