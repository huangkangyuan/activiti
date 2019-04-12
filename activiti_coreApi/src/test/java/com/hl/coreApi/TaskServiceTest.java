package com.hl.coreApi;

import com.google.common.collect.Maps;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.*;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Documented;
import java.util.List;
import java.util.Map;

/**
 * TaskService是activiti的任务服务类。可以从这个类中获取任务的信息。设置用户的权限信息
 */
public class TaskServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceTest.class);
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
	@Deployment(resources = {"com/hl/my-process-task.bpmn20.xml"})
    public void testTaskService() {
        Map<String,Object> variables = Maps.newHashMap();
        variables.put("message", "start");
        activitiRule.getRuntimeService()
                .startProcessInstanceByKey("my-process",variables);
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        logger.info("task={}", ToStringBuilder.reflectionToString(task,ToStringStyle.JSON_STYLE));
        logger.info("task_documentation={}",task.getDescription() );

        taskService.setVariable(task.getId(), "key1", "value1");
        taskService.setVariableLocal(task.getId(), "localKey1", "localValue1");

        Map<String, Object> variables1 = taskService.getVariables(task.getId());
        Map<String, Object> variablesLocal = taskService.getVariablesLocal(task.getId());

        Map<String, Object> variables2 = activitiRule.getRuntimeService().getVariables(task.getExecutionId());


        //variables1={key1=value1, localKey1=localValue1, message=start}
        //variables2={key1=value1, message=start}
        //variablesLocal={localKey1=localValue1}
        logger.info("variables1={}", variables1);
        logger.info("variables2={}", variables2);
        logger.info("variablesLocal={}", variablesLocal);

        Map<String,Object> completeVariables = Maps.newHashMap();
        completeVariables.put("completeVariables", "completeVariables");
        taskService.complete(task.getId(),completeVariables);

        Task task1 = taskService.createTaskQuery().taskId(task.getId()).singleResult();
        logger.info("task1={}", task1);
    }

    @Test
    @Deployment(resources = {"com/hl/my-process-task.bpmn20.xml"})
    public void test(){
        Map<String,Object> variables = Maps.newHashMap();
        variables.put("message", "start");
        activitiRule.getRuntimeService()
                .startProcessInstanceByKey("my-process",variables);
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        logger.info("task={}", ToStringBuilder.reflectionToString(task,ToStringStyle.JSON_STYLE));
        logger.info("task_documentation={}",task.getDescription() );

        taskService.setOwner(task.getId(), "lsh");
//        taskService.setAssignee(task.getId(), "hky");
        List<Task> taskList = taskService.createTaskQuery()
                .taskCandidateUser("hky")   //候选人
                .taskUnassigned().listPage(0, 100);

        for (Task task1 : taskList) {
            try{
                taskService.claim(task1.getId(), "hky"); //设置当前任务的办理人
            }catch (Exception e){
                logger.info("exception", e);
            }
        }

        List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(task.getId());
        for (IdentityLink identityLink : identityLinkList) {
            logger.info("identityLink = {}", identityLink);
        }

        List<Task> taskList1 = taskService.createTaskQuery().taskAssignee("hky").listPage(0, 100);
        for (Task task1 : taskList1) {
            taskService.complete(task1.getId());
        }

        taskList1 = taskService.createTaskQuery().taskAssignee("hky").listPage(0, 100);
        logger.info("是否为空={}", CollectionUtils.isEmpty(taskList1));
    }

    @Test
    @Deployment(resources = {"com/hl/my-process-task.bpmn20.xml"})
    public void testTaskAttachment(){
        Map<String,Object> variables = Maps.newHashMap();
        variables.put("message", "start");
        activitiRule.getRuntimeService()
                .startProcessInstanceByKey("my-process",variables);
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();

        taskService.createAttachment("url",
                task.getId(),
                task.getProcessInstanceId(),
                "name",
                "desc",
                "/url/test.png");

        List<Attachment> taskAttachments = taskService.getTaskAttachments(task.getId());
        for (Attachment taskAttachment : taskAttachments) {
            logger.info("taskAttachment={}", ToStringBuilder.reflectionToString(taskAttachment,ToStringStyle.JSON_STYLE));
        }
    }

    @Test
    @Deployment(resources = {"com/hl/my-process-task.bpmn20.xml"})
    public void testTaskComment(){
        Map<String,Object> variables = Maps.newHashMap();
        variables.put("message", "start");
        activitiRule.getRuntimeService()
                .startProcessInstanceByKey("my-process",variables);
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        taskService.setOwner(task.getId(), "hky");
        taskService.setAssignee(task.getId(), "hky");
        taskService.addComment(task.getId(),task.getProcessInstanceId(),"message_note 1");
        taskService.addComment(task.getId(),task.getProcessInstanceId(),"message_note 2");

        List<Comment> taskComments = taskService.getTaskComments(task.getId());
        for (Comment taskComment : taskComments) {
            logger.info("taskComment={}", ToStringBuilder.reflectionToString(taskComment,ToStringStyle.JSON_STYLE));
        }

        List<Event> taskEvents = taskService.getTaskEvents(task.getId());
        for (Event taskEvent : taskEvents) {
            logger.info("taskEvent={}", ToStringBuilder.reflectionToString(taskEvent,ToStringStyle.JSON_STYLE));
        }
    }
}
