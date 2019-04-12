package com.hl.coreApi;

import com.google.common.collect.Maps;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * HistoryService 是activiti的查询历史信息的类。在一个流程执行完成后，这个对象为我们提供查询历史信息。
 */
public class HistoryServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(HistoryServiceTest.class);
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_history.cfg.xml");

    @Test
	@Deployment(resources = {"com/hl/my-process.bpmn20.xml"})
    public void testHistory () {
        HistoryService historyService = activitiRule.getHistoryService();
        ProcessInstanceBuilder processInstanceBuilder = activitiRule.getRuntimeService().createProcessInstanceBuilder();

        Map<String,Object> variables = Maps.newHashMap();
        variables.put("key0", "value0");
        variables.put("key1", "value1");
        variables.put("key2", "value2");

        Map<String,Object> transientVariables = Maps.newHashMap();
        transientVariables.put("tkey1", "tvalue1");
        ProcessInstance processInstance = processInstanceBuilder.processDefinitionKey("my-process")
                .variables(variables)
                .transientVariables(transientVariables)  //瞬时变量不会存在历史记录
                .start();

        activitiRule.getRuntimeService().setVariable(processInstance.getId(), "key1", "value1_");
        Task task = activitiRule.getTaskService()
                .createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();

//        activitiRule.getTaskService().complete(task.getId(), );
        Map<String, String> properties = Maps.newHashMap();
        properties.put("fkey1", "fvalue1");
        properties.put("key2", "value2_");
        activitiRule.getFormService().submitTaskFormData(task.getId(), properties);

        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().listPage(0, 100);
        for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
            logger.info("historicProcessInstance={}", historicProcessInstance);
        }

        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery().listPage(0, 100);
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            logger.info("historicActivityInstance={}", historicActivityInstance);
        }

        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().listPage(0, 100);
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            logger.info("historicTaskInstance={}", ToStringBuilder.reflectionToString(historicTaskInstance,ToStringStyle.JSON_STYLE ));
        }

        List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery().listPage(0, 100);
        for (HistoricVariableInstance historicVariableInstance : historicVariableInstances) {
            logger.info("historicVariableInstance={}", historicVariableInstance);
        }

        List<HistoricDetail> historicDetails = historyService.createHistoricDetailQuery().listPage(0, 100);
        for (HistoricDetail historicDetail : historicDetails) {
            logger.info("historicDetail={}", historicDetail);
        }

        ProcessInstanceHistoryLog processInstanceHistoryLog = historyService.createProcessInstanceHistoryLogQuery(processInstance.getId())
                .includeVariables()
                .includeFormProperties()
                .includeComments()
                .includeTasks()
                .includeActivities()
                .includeVariableUpdates().singleResult();

        List<HistoricData> historicDatas = processInstanceHistoryLog.getHistoricData();
        for (HistoricData historicData : historicDatas) {
            logger.info("historicData={}", historicData);
        }

        historyService.deleteHistoricProcessInstance(processInstance.getId());
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
        logger.info("historicProcessInstance={}", historicProcessInstance);

    }
}

