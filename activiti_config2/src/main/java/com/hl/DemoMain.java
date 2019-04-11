package com.hl;

import com.google.common.collect.Maps;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DemoMain {

    private static Logger logger = LoggerFactory.getLogger(DemoMain.class);

    public static void main(String[] args) throws ParseException {
        ProcessEngine processEngine = getProcessEngine();

        ProcessDefinition processDefinition = getProcessDefinition(processEngine);

        ProcessInstance processInstance = getProcessInstance(processEngine, processDefinition);

        processTask(processEngine, processInstance);
        logger.info("任务结束");
    }

    private static void processTask(ProcessEngine processEngine, ProcessInstance processInstance) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        while(processInstance!=null&&!processInstance.isEnded()){
            TaskService taskService = processEngine.getTaskService();
            List<Task> list = taskService.createTaskQuery().list();
            logger.info("待处理任务数量 {}", list.size());
            for (Task task : list) {
                logger.info("待处理任务 {}", task.getName());
                Map<String, Object> map = getMap(processEngine, scanner, task);
                taskService.complete(task.getId(), map);
                processInstance = processEngine.getRuntimeService()
                                               .createProcessInstanceQuery()
                                               .singleResult();
            }
        }
        scanner.close();
    }

    private static HashMap<String, Object> getMap(ProcessEngine processEngine, Scanner scanner, Task task) throws ParseException {
        FormService formService = processEngine.getFormService();
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        List<FormProperty> formProperties = taskFormData.getFormProperties();
        HashMap<String, Object> map = Maps.newHashMap();
        for (FormProperty formProperty : formProperties) {
            String line = null;
            if(StringFormType.class.isInstance(formProperty.getType())){
                logger.info("请输入 {} ？",formProperty.getName());
                line = scanner.nextLine();
                map.put(formProperty.getId(), line);
            }else if (DateFormType.class.isInstance(formProperty.getType())){
                logger.info("请输入 {} ？  【格式为(yyyy-MM-dd)】",formProperty.getName());
                line = scanner.nextLine();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(line);
                map.put(formProperty.getId(), date);
            }else{
                logger.info("类型暂时不支持{}",formProperty.getName());
            }
            logger.info("您输入的内容是【{}】",line);
        }
        return map;
    }

    private static ProcessInstance getProcessInstance(ProcessEngine processEngine, ProcessDefinition processDefinition) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        logger.info("启动流程 {}", processInstance.getProcessDefinitionKey() );
        return processInstance;
    }

    private static ProcessDefinition getProcessDefinition(ProcessEngine processEngine) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addClasspathResource("second_approve.bpmn20.xml");
        Deployment deployment = builder.deploy();
        String deploymentName = deployment.getName();
        String deploymentId = deployment.getId();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().
                deploymentId(deploymentId).
                singleResult();

        logger.info("流程定义文件 {}, 流程ID {}", processDefinition.getName(),processDefinition.getId());
        return processDefinition;
    }

    private static ProcessEngine getProcessEngine() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        ProcessEngine processEngine = cfg.buildProcessEngine();
        String name = processEngine.getName();
        String version = ProcessEngine.VERSION;
        logger.info("流程引擎名称 {},版本 {}",name,version);
        return processEngine;
    }

}
