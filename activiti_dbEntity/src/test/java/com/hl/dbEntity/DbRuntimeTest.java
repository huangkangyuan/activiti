package com.hl.dbEntity;

import com.google.common.collect.Maps;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DbRuntimeTest {
    
    private static final Logger logger = LoggerFactory.getLogger(DbRuntimeTest.class);
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

    @Test
    public void testRuntime(){
        activitiRule.getRepositoryService().createDeployment()
                .name("二级审批流程")
                .addClasspathResource("com/hl/second_approve.bpmn20.xml")
                .deploy();

        Map<String,Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        activitiRule.getRuntimeService()
                    .startProcessInstanceByKey("second_approve", variables);
                    
    }

}
