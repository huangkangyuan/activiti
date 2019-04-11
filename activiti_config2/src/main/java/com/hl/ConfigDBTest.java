package com.hl;

import org.activiti.engine.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigDBTest {

    private static Logger logger = LoggerFactory.getLogger(ConfigDBTest.class);

    public static void main(String[] args) {

        ProcessEngineConfiguration processEngineConfiguration =
                ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.druid.cfg.xml");

        logger.info("processEngineConfiguration={}",processEngineConfiguration);
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        logger.info("processEngine={}",processEngine.getName());
        processEngine.close();
    }


}
