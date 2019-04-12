package com.hl.coreApi;

import com.google.common.collect.Maps;
import com.hl.mapper.MyCustomMapper;
import org.activiti.engine.FormService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.management.TablePage;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.*;
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
 * ManagementService
 * Job任务管理
 * 数据库相关通用操作
 * 执行流程引擎命令(Command)
 */
public class ManagementServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ManagementServiceTest.class);
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_job.xml");

    @Test
	@Deployment(resources = {"com/hl/my-process-job.bpmn20.xml"})
    public void testJobQuery () {
        ManagementService managementService = activitiRule.getManagementService();
        List<Job> jobList = managementService.createTimerJobQuery().listPage(0, 100);
        for (Job job : jobList) {
            logger.info("job={}", job);
        }

        JobQuery jobQuery = managementService.createJobQuery();
        SuspendedJobQuery suspendedJobQuery = managementService.createSuspendedJobQuery();
        DeadLetterJobQuery deadLetterJobQuery = managementService.createDeadLetterJobQuery();//无法执行的任务
    }

    @Test
    @Deployment(resources = {"com/hl/my-process-job.bpmn20.xml"})
    public void testTablePageQuery () {
        ManagementService managementService = activitiRule.getManagementService();
        TablePage tablePage = managementService.createTablePageQuery()
                .tableName(managementService.getTableName(ProcessDefinitionEntity.class))
                .listPage(0, 100);

        List<Map<String, Object>> rows = tablePage.getRows();
        for (Map<String, Object> row : rows) {
            logger.info("row={}", row);
        }
    }

    @Test
    @Deployment(resources = {"com/hl/my-processb.bpmn20.xml"})
    public void testCustomExecuteQuery () {
        activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        ManagementService managementService = activitiRule.getManagementService();
        List<Map<String, Object>> maps = managementService.executeCustomSql(new AbstractCustomSqlExecution<MyCustomMapper, List<Map<String, Object>>>(MyCustomMapper.class) {
            @Override
            public List<Map<String, Object>> execute(MyCustomMapper myCustomMapper) {
                return myCustomMapper.findAll();
            }
        });
        for (Map<String, Object> map : maps) {
            logger.info("map={}", map);
        }
    }

    @Test
    @Deployment(resources = {"com/hl/my-process.bpmn20.xml"})
    public void testCommand () {
        activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
        ManagementService managementService = activitiRule.getManagementService();
        ProcessDefinitionEntity processDefinitionEntity = managementService.executeCommand(new Command<ProcessDefinitionEntity>() {

            @Override
            public ProcessDefinitionEntity execute(CommandContext commandContext) {
                ProcessDefinitionEntity processDefinitionEntity = commandContext.getProcessDefinitionEntityManager()
                        .findLatestProcessDefinitionByKey("my-process");
                return processDefinitionEntity;
            }
        });
        logger.info("processDefinitionEntity={}", processDefinitionEntity);
    }
}

