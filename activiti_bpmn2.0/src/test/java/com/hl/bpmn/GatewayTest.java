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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GatewayTest {

    private static final Logger logger = LoggerFactory.getLogger(GatewayTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"com/hl/my-process-exclusiveGateway1.bpmn20.xml"})
    public void testExclusiveGateway() {

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("scope", 85);
        ProcessInstance processInstance = activitiRule.getRuntimeService()
                .startProcessInstanceByKey("my-process",variables);

        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        logger.info("task.name={}", task.getName());
    }

    @Test
    @Deployment(resources = {"com/hl/my-process-parallelGateway1.bpmn20.xml"})
    public void testParallelGateway() {

        ProcessInstance processInstance = activitiRule.getRuntimeService()
                .startProcessInstanceByKey("my-process");

        List<Task> taskList = activitiRule.getTaskService().createTaskQuery()
                .processInstanceId(processInstance.getId())
                .listPage(0, 100);
        for (Task task : taskList) {
            logger.info("task.name={}", task.getName());
            activitiRule.getTaskService().complete(task.getId());
        }
        logger.info("taskList.size={}", taskList.size());

        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        logger.info("task.name={}", task.getName());

    }
}
