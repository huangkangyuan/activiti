package com.hl.coreApi;

import com.google.common.collect.Maps;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * RuntimeService是activiti的流程执行服务类。可以从这个服务类中获取很多关于流程执行相关的信息。
 */
public class RuntimeServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(RuntimeServiceTest.class);
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"com/hl/my-process.bpmn20.xml"})
    public void testStartProcessByKey() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        //通过Key,每次启动都是采用最新的版本
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process", variables);
        logger.info("proccessInstance={}", processInstance);
    }

    @Test
    @Deployment(resources = {"com/hl/my-process.bpmn20.xml"})
    public void testStartProcessById() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessDefinition processDefinition = activitiRule.getRepositoryService().createProcessDefinitionQuery().singleResult();

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), variables);
        logger.info("proccessInstance={}", processInstance);
    }

    @Test
    @Deployment(resources = {"com/hl/my-process.bpmn20.xml"})
    public void testStartProcessInstanceBuilder() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        ProcessInstance processInstance = processInstanceBuilder.businessKey("businessKey001")
                .processDefinitionKey("my-process")
                .variables(variables)
                .start();
        //通过Key,每次启动都是采用最新的版本
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process",variables);
        logger.info("proccessInstance={}", processInstance);
    }

    @Test
    @Deployment(resources = {"com/hl/my-process.bpmn20.xml"})
    public void testVariables() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        variables.put("key2", "value2");
        //通过Key,每次启动都是采用最新的版本
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process", variables);
        logger.info("proccessInstance={}", processInstance);

        runtimeService.setVariable(processInstance.getId(), "key3","value3");
        runtimeService.setVariable(processInstance.getId(), "key2","value2_");
        Map<String, Object> map = runtimeService.getVariables(processInstance.getId());
        logger.info("variables={}", map);
    }

    @Test
    @Deployment(resources = {"com/hl/my-process.bpmn20.xml"})
    public void testProcessInstanceQuery() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");
        ProcessInstance processInstance1 = runtimeService.createProcessInstanceQuery()
                                                        .processInstanceId(processInstance.getId())
                                                        .singleResult();
        logger.info("processInstance1={}", processInstance1);
    }

    @Test
    @Deployment(resources = {"com/hl/my-process.bpmn20.xml"})
    public void testExecutionQuery() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");
        List<Execution> executionList = runtimeService.createExecutionQuery()
                                                        .processInstanceId(processInstance.getId())
                                                       .listPage(0, 100);
        for (Execution execution : executionList) {
            logger.info("execution={}", execution);
        }
    }

    @Test
    @Deployment(resources = {"com/hl/my-process-trigger.bpmn20.xml"})
    public void testReceiveTask(){
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        runtimeService.startProcessInstanceByKey("my-process");
        Execution execution = runtimeService.createExecutionQuery()
                .activityId("someTask")
                .singleResult();
        logger.info("execution={}", execution);

        runtimeService.trigger(execution.getId());

        execution = runtimeService.createExecutionQuery()
                .activityId("someTask")
                .singleResult();
        logger.info("execution={}", execution);
    }

    @Test
    @Deployment(resources = {"com/hl/my-process-signal.bpmn20.xml"})
    public void testSignalEventReceived(){
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        runtimeService.startProcessInstanceByKey("my-process");

        Execution execution = runtimeService.createExecutionQuery()
                                .signalEventSubscriptionName("my-signal")
                                .singleResult();
        logger.info("execution={}", execution);

        runtimeService.signalEventReceived("my-signal");


        execution = runtimeService.createExecutionQuery()
                .signalEventSubscriptionName("my-signal")
                .singleResult();
        logger.info("execution={}", execution);
    }

    @Test
    @Deployment(resources = {"com/hl/my-process-message.bpmn20.xml"})
    public void testMessageEventReceived(){
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        runtimeService.startProcessInstanceByKey("my-process");

        Execution execution = runtimeService.createExecutionQuery()
                .messageEventSubscriptionName("my-message")
                .singleResult();
        logger.info("execution={}", execution);

        runtimeService.messageEventReceived("my-message",execution.getId());

        execution = runtimeService.createExecutionQuery()
                .messageEventSubscriptionName("my-message")
                .singleResult();
        logger.info("execution={}", execution);
    }

    @Test
    @Deployment(resources = {"com/hl/my-process-message-receive.bpmn20.xml"})
    public void testMessageReceive(){
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        ProcessInstance processInstance =
                //基于message启动的话会复杂一些 还需要再流程启动文件添加相应的配置
                runtimeService.startProcessInstanceByMessage("my-message");
        logger.info("processInstance={}", processInstance);
    }
}
