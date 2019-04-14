package com.hl.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class MyJavaDelegate implements JavaDelegate, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(MyJavaDelegate.class);

    private Expression name;
    private Expression desc;

    @Override
    public void execute(DelegateExecution execution) {
        if (name!=null){
            Object nameValue = name.getValue(execution);
            logger.info("nameValue={}", nameValue);
        }

        if (desc!=null){
            Object descValue = desc.getValue(execution);
            logger.info("descValue={}", descValue);
        }
        logger.info("run my javaDelegate {}",this);

    }
}
