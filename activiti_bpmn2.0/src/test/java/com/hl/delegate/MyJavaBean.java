package com.hl.delegate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class MyJavaBean implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(MyJavaBean.class);

    private String name;

    public String getName() {
        logger.info("run getName():{}", name);
        return name;
    }

    public MyJavaBean() {
    }

    public MyJavaBean(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void sayHello() {
        logger.info("run sayHello()");
    }
}
