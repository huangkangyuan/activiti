package com.hl.bpmn;

import com.google.common.collect.Maps;
import com.hl.delegate.MyJavaBean;
import com.hl.delegate.MyJavaDelegate;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.ManagementService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:activiti-context.xml")
public class SpringServiceTaskTest {

	private static final Logger logger = LoggerFactory.getLogger(SpringServiceTaskTest.class);

	@Autowired
	@Rule
	public ActivitiRule activitiRule;

	//流程会结束
	@Test
	@Deployment(resources = {"com/hl/my-process-serviceTask3.bpmn20.xml"})
	public void tesSpringServiceTask() throws InterruptedException {
		ProcessInstance processInstance = activitiRule.getRuntimeService()
				.startProcessInstanceByKey("my-process");

		List<HistoricActivityInstance> historicActivityInstances = activitiRule.getHistoryService()
				.createHistoricActivityInstanceQuery()
				.orderByHistoricActivityInstanceEndTime().asc()
				.list();

		for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
			logger.info("historicActivityInstance={}", historicActivityInstance);
		}
	}

	//流程会结束
	@Test
	@Deployment(resources = {"com/hl/my-process-serviceTask3.bpmn20.xml"})
	public void tesSpringServiceTask2() throws InterruptedException {


		Map<String, Object> variables = Maps.newHashMap();

		//优先选择我们创建的javaDelegate对象,覆盖了spring中配置的
		MyJavaDelegate myJavaDelegate = new MyJavaDelegate();
		logger.info("myJavaDelegate={}", myJavaDelegate);

		variables.put("myJavaDelegate", myJavaDelegate);
		ProcessInstance processInstance = activitiRule.getRuntimeService()
				.startProcessInstanceByKey("my-process",variables);

		List<HistoricActivityInstance> historicActivityInstances = activitiRule.getHistoryService()
				.createHistoricActivityInstanceQuery()
				.orderByHistoricActivityInstanceEndTime().asc()
				.list();

		for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
			logger.info("historicActivityInstance={}", historicActivityInstance);
		}
	}

	//流程会结束
	@Test
	@Deployment(resources = {"com/hl/my-process-serviceTask4.bpmn20.xml"})
	public void tesSpringServiceTask3() throws InterruptedException {

		Map<String, Object> variables = Maps.newHashMap();
		MyJavaBean myJavaBean = new MyJavaBean();
		myJavaBean.setName("myJavaBean");
		variables.put("myJavaBean", myJavaBean);

		ProcessInstance processInstance = activitiRule.getRuntimeService()
				.startProcessInstanceByKey("my-process",variables);

		List<HistoricActivityInstance> historicActivityInstances = activitiRule.getHistoryService()
				.createHistoricActivityInstanceQuery()
				.orderByHistoricActivityInstanceEndTime().asc()
				.list();

		for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
			logger.info("historicActivityInstance={}", historicActivityInstance);
		}
	}
}
