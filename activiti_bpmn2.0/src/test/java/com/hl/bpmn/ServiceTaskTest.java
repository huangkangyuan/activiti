package com.hl.bpmn;

import com.google.common.collect.Maps;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;
import java.util.Map;

public class ServiceTaskTest {

	private static final Logger logger = LoggerFactory.getLogger(ServiceTaskTest.class);

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	//流程会结束
	@Test
	@Deployment(resources = {"com/hl/my-process-serviceTask.bpmn20.xml"})
	public void tesServiceTask() throws InterruptedException {
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

		List<HistoricActivityInstance> historicActivityInstances = activitiRule.getHistoryService().createHistoricActivityInstanceQuery()
				.orderByHistoricActivityInstanceEndTime().asc()
				.list();

		for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
			logger.info("historicActivityInstance", historicActivityInstance);

		}
	}

	//并不会结束流程 而是停止了 这是实现了JavaDelegate的区别
	@Test
	@Deployment(resources = {"com/hl/my-process-serviceTask1.bpmn20.xml"})
	public void tesServiceTask1() throws InterruptedException {
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

		List<HistoricActivityInstance> historicActivityInstances = activitiRule.getHistoryService().createHistoricActivityInstanceQuery()
				.orderByHistoricActivityInstanceEndTime().asc()
				.list();

		for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
			logger.info("historicActivityInstance={}", historicActivityInstance);
		}

		Execution execution = activitiRule.getRuntimeService()
				.createExecutionQuery().activityId("someTask")
				.singleResult();
		logger.info("execution={}", execution);

		ManagementService managementService = activitiRule.getManagementService();
		managementService.executeCommand(new Command<Object>() {
			@Override
			public Object execute(CommandContext commandContext) {
				ActivitiEngineAgenda agenda = commandContext.getAgenda();
				agenda.planTakeOutgoingSequenceFlowsOperation((ExecutionEntity) execution, false);
				return null;
			}
		});

		historicActivityInstances = activitiRule.getHistoryService().createHistoricActivityInstanceQuery()
				.orderByHistoricActivityInstanceEndTime().asc()
				.list();
		for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
			logger.info("historicActivityInstance={}", historicActivityInstance);
		}
	}

	@Test
	@Deployment(resources = {"com/hl/my-process-serviceTask2.bpmn20.xml"})
	public void tesServiceTask2() throws InterruptedException {
		Map<String, Object> variables = Maps.newHashMap();
		variables.put("desc", "The is java delegate Desc");

		ProcessInstance processInstance = activitiRule.getRuntimeService()
				.startProcessInstanceByKey("my-process",variables);

	}

}
