package com.hl.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.delegate.ActivityBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class MyActivityBehavior implements ActivityBehavior, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(MyActivityBehavior.class);


    @Override
    public void execute(DelegateExecution execution) {
        logger.info("run my activity behavior" );
    }
}
