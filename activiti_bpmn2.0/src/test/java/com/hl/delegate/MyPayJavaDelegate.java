package com.hl.delegate;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;

public class MyPayJavaDelegate implements JavaDelegate, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(MyPayJavaDelegate.class);


    @Override
    public void execute(DelegateExecution execution) {
        //只会获取到在callActivity中配置的变量
        logger.info("execute variables={}", execution.getVariables());

        logger.info("run my javaDelegate {}",this);
        Object errorFlag = execution.getVariable("errorFlag");
        execution.getParent().setVariableLocal("key2", "value2");

        execution.setVariable("key1", "value1");
        execution.setVariable("key3", "value3");

        if (Objects.equals(errorFlag, true)){
            throw new BpmnError("bpmnError");
        }
    }
}
