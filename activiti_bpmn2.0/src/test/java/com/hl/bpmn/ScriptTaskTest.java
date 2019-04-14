package com.hl.bpmn;

import com.google.common.collect.Maps;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
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

public class ScriptTaskTest {

	private static final Logger logger = LoggerFactory.getLogger(ScriptTaskTest.class);

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	@Deployment(resources = {"com/hl/my-process-scriptTask.bpmn20.xml"})
	public void tesScriptTask() throws InterruptedException {
		ProcessInstance processInstance = activitiRule.getRuntimeService()
				.startProcessInstanceByKey("my-process");

		HistoryService historyService = activitiRule.getHistoryService();
		List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
				.processInstanceId(processInstance.getId())
				.orderByVariableName()
				.asc()
				.listPage(0, 100);

		for (HistoricVariableInstance historicVariableInstance : historicVariableInstances) {
			logger.info("variable={}", historicVariableInstance);
		}
		logger.info("variable.size={}", historicVariableInstances.size());
	}


	@Test
	@Deployment(resources = {"com/hl/my-process-scriptTask2.bpmn20.xml"})
	public void tesScriptTask2() throws InterruptedException {


		Map<String, Object> variables = Maps.newHashMap();
		variables.put("key1", 3);
		variables.put("key2", 5);
		ProcessInstance processInstance = activitiRule.getRuntimeService()
				.startProcessInstanceByKey("my-process",variables);

		HistoryService historyService = activitiRule.getHistoryService();
		List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
				.processInstanceId(processInstance.getId())
				.orderByVariableName()
				.asc()
				.listPage(0, 100);

		for (HistoricVariableInstance historicVariableInstance : historicVariableInstances) {
			logger.info("variable={}", historicVariableInstance);
		}
		logger.info("variable.size={}", historicVariableInstances.size());
	}

	@Test
	@Deployment(resources = {"com/hl/my-process-scriptTask3.bpmn20.xml"})
	public void tesScriptTask3() throws InterruptedException {


		Map<String, Object> variables = Maps.newHashMap();
		variables.put("key1", 3);
		variables.put("key2", 5);
		ProcessInstance processInstance = activitiRule.getRuntimeService()
				.startProcessInstanceByKey("my-process",variables);

		HistoryService historyService = activitiRule.getHistoryService();
		List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
				.processInstanceId(processInstance.getId())
				.orderByVariableName()
				.asc()
				.listPage(0, 100);

		for (HistoricVariableInstance historicVariableInstance : historicVariableInstances) {
			logger.info("variable={}", historicVariableInstance);
		}
		logger.info("variable.size={}", historicVariableInstances.size());
	}

	@Test
	public void test() throws ScriptException {
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		ScriptEngine scriptEngine= scriptEngineManager.getEngineByName("juel");

		Object eval = scriptEngine.eval("${1+2}");
		logger.info("eval={}", eval);
	}

}
