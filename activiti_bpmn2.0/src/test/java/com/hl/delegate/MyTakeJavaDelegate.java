package com.hl.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class MyTakeJavaDelegate implements JavaDelegate, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(MyTakeJavaDelegate.class);


    @Override
    public void execute(DelegateExecution execution) {
        logger.info("run my javaDelegate {}",this);
    }
}
