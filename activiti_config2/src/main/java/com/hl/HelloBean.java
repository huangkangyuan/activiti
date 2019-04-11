package com.hl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloBean {

    private Logger logger = LoggerFactory.getLogger(HelloBean.class);
    public void sayHello(){
        logger.info("say Hello");
    }
}
