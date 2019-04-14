package com.hl.bpmn;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TimerEventTest {

	private static final Logger logger = LoggerFactory.getLogger(TimerEventTest.class);

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	@Deployment(resources = {"com/hl/my-process-timer-bound.bpmn20.xml"})
	public void test() throws InterruptedException {
		ProcessInstance processInstance = activitiRule.getRuntimeService()
							.startProcessInstanceByKey("my-process");

		List<Task> taskList = activitiRule.getTaskService().createTaskQuery().listPage(0, 100);
		for (Task task : taskList) {
			logger.info("task={}", task.getName());
		}
		logger.info("taskList.size={}", taskList.size());

		Thread.sleep(1000*15);

		taskList = activitiRule.getTaskService().createTaskQuery().listPage(0, 100);
		for (Task task : taskList) {
			logger.info("task.name={}", task.getName());
		}
		logger.info("taskList.size={}", taskList.size());

	}
}
