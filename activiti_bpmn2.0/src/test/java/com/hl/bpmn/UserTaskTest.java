package com.hl.bpmn;

import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserTaskTest {

	private static final Logger logger = LoggerFactory.getLogger(UserTaskTest.class);

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	@Deployment(resources = {"com/hl/my-process-userTask.bpmn20.xml"})
	public void testTask() throws InterruptedException {
		ProcessInstance processInstance = activitiRule.getRuntimeService()
				.startProcessInstanceByKey("my-process");

		TaskService taskService = activitiRule.getTaskService();
		Task task = taskService.createTaskQuery().taskCandidateUser("user1").singleResult();
		logger.info("user1 task={}", task);

		task = taskService.createTaskQuery().taskCandidateUser("user1").singleResult();
		logger.info("user2 task={}", task);

		task = taskService.createTaskQuery().taskCandidateGroup("group1").singleResult();
		logger.info("group1 task={}", task);

		//指定了办理人后  候选人user1或者用户组都会失效
		taskService.claim(task.getId(), "user2");  //这个会做校验 推荐
		logger.info("claim task={}", task);
//		taskService.setAssignee(task.getId(),"user2");

		task = taskService.createTaskQuery().taskCandidateOrAssigned("user1").singleResult();
		logger.info("taskCandidateOrAssigned user1 task={}", task);

		task = taskService.createTaskQuery().taskCandidateOrAssigned("user2").singleResult();
		logger.info("taskCandidateOrAssigned user2 task={}", task);
	}

	@Test
	@Deployment(resources = {"com/hl/my-process-userTaskListener.bpmn20.xml"})
	public void testuserTaskListener() throws InterruptedException {
		ProcessInstance processInstance = activitiRule.getRuntimeService()
				.startProcessInstanceByKey("my-process");

		TaskService taskService = activitiRule.getTaskService();
		Task task = taskService.createTaskQuery().taskCandidateUser("user1").singleResult();
		logger.info("user1 task={}", task);

		task = taskService.createTaskQuery().taskCandidateUser("user1").singleResult();
		logger.info("user2 task={}", task);

		task = taskService.createTaskQuery().taskCandidateGroup("group1").singleResult();
		logger.info("group1 task={}", task);

		//指定了办理人后  候选人user1或者用户组都会失效
		taskService.claim(task.getId(), "user2");  //这个会做校验 推荐
		logger.info("claim task={}", task);
//		taskService.setAssignee(task.getId(),"user2");

		task = taskService.createTaskQuery().taskCandidateOrAssigned("user1").singleResult();
		logger.info("taskCandidateOrAssigned user1 task={}", task);

		task = taskService.createTaskQuery().taskCandidateOrAssigned("user2").singleResult();
		logger.info("taskCandidateOrAssigned user2 task={}", task);

		taskService.complete(task.getId());
	}
}
