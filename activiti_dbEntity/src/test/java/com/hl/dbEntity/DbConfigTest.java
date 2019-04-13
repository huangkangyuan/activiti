package com.hl.dbEntity;

import com.google.common.collect.Lists;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.test.Deployment;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class DbConfigTest {

    private static final Logger logger = LoggerFactory.getLogger(DbConfigTest.class);

    @Test
    @Deployment(resources = {"com/hl/my-process.bpmn20.xml"})
    public void test(){
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti-mysql.cfg.xml")
                .buildProcessEngine();
        ManagementService managementService = processEngine.getManagementService();
        //String是表名，Long是表的数据量
        Map<String, Long> tableCount = managementService.getTableCount();
        ArrayList<String> tableNames = Lists.newArrayList(tableCount.keySet());

        Collections.sort(tableNames);
        for (String tableName : tableNames) {
            logger.info("tableName={}", tableName);
        }
        logger.info("tableNames.size={}", tableNames.size());
    }

    @Test
    @Deployment(resources = {"com/hl/my-process.bpmn20.xml"})
    public void dropTable(){
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti-mysql.cfg.xml")
                .buildProcessEngine();
        ManagementService managementService = processEngine.getManagementService();
        managementService.executeCommand(new Command<Object>(){

            @Override
            public Object execute(CommandContext commandContext) {
                commandContext.getDbSqlSession().dbSchemaDrop();;
                logger.info("删除表结构");
                return null;
            }
        });
    }
}
