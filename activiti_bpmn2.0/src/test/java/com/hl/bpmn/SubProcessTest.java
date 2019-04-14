package com.hl.bpmn;

import com.google.common.collect.Maps;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class SubProcessTest {

    private static final Logger logger = LoggerFactory.getLogger(SubProcessTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"com/hl/my-process-subProcess.bpmn20.xml"})
    public void testSubProcess() {
        ProcessInstance processInstance = activitiRule.getRuntimeService()
                .startProcessInstanceByKey("my-process");

        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        logger.info("task.name={}", task.getName());
    }


    //出现异常
    @Test
    @Deployment(resources = {"com/hl/my-process-subProcess.bpmn20.xml"})
    public void testSubProcess2() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("errorFlag", true);
        ProcessInstance processInstance = activitiRule.getRuntimeService()
                .startProcessInstanceByKey("my-process", variables);

        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        logger.info("task.name={}", task.getName());

        variables = activitiRule.getRuntimeService().getVariables(processInstance.getId());
        logger.info("variables={}",variables);
    }

    //事件子流程即使出现异常也会获取到execution中设置的变量
    @Test
    @Deployment(resources = {"com/hl/my-process-subProcess2.bpmn20.xml"})
    public void testSubProcess3() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("errorFlag", true);
        variables.put("key1", "value1");
        ProcessInstance processInstance = activitiRule.getRuntimeService()
                .startProcessInstanceByKey("my-process", variables);

        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        logger.info("task.name={}", task.getName());

        variables = activitiRule.getRuntimeService().getVariables(processInstance.getId());
        logger.info("variables={}",variables);
    }


    //调用式子流程出现异常后获取不到execution中设置的变量
    @Test
    @Deployment(resources = {"com/hl/my-process-subProcess4.bpmn20.xml",
            "com/hl/my-process-subProcess3.bpmn20.xml"})
    public void testSubProcess4() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("errorFlag", true);
        variables.put("key0", "value0");
        ProcessInstance processInstance = activitiRule.getRuntimeService()
                .startProcessInstanceByKey("my-process", variables);

        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        logger.info("task.name={}", task.getName());

        variables = activitiRule.getRuntimeService().getVariables(processInstance.getId());
        logger.info("variables={}",variables);
    }
}
