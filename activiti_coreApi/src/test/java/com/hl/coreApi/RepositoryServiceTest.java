package com.hl.coreApi;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.*;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * RepositoryService是Activiti的仓库服务类。所谓的仓库指流程定义文档的两个文件：bpmn文件和流程图片。
 */
public class RepositoryServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryServiceTest.class);
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
//	@Deployment(resources = {"com/hl/my-process.bpmn20.xml"})
    public void testRepositoryService() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        Deployment deploy = deploymentBuilder.name("测试部署文件")
                .addClasspathResource("com/hl/my-process.bpmn20.xml")
                .deploy();

        DeploymentBuilder deploymentBuilder2 = repositoryService.createDeployment();
        Deployment deploy2 = deploymentBuilder2.name("测试部署文件2")
                .addClasspathResource("com/hl/my-process_1.bpmn20.xml")
                .deploy();

        logger.info("deploy={}", deploy);
        logger.info("deploy2={}", deploy2);

        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
        List<Deployment> deploymentList = deploymentQuery.orderByDeploymentId().asc().listPage(0, 100);
        for (Deployment deployment : deploymentList) {
            logger.info("deployment={}", deployment);
        }

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionId().asc()
                .listPage(0, 100);
        for (ProcessDefinition processDefinition : processDefinitions) {
            logger.info("processDefinition={},version={},id={},key={}", processDefinition, processDefinition.getVersion(), processDefinition.getId(), processDefinition.getKey());
        }
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = {"com/hl/my-process.bpmn20.xml"})
    public void testSuspendAndActive() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();
        logger.info("processDefinition.id={}", processDefinition.getId());

        //挂起
        repositoryService.suspendProcessDefinitionById(processDefinition.getId());

        RuntimeService runtimeService = activitiRule.getRuntimeService();

        try {
            runtimeService.startProcessInstanceById(processDefinition.getId());
            logger.info("启动成功");
        } catch (Exception e) {
            logger.info("启动失败", e);
        }

        repositoryService.activateProcessDefinitionById(processDefinition.getId());
        try {
            runtimeService.startProcessInstanceById(processDefinition.getId());
            logger.info("启动成功");
        } catch (Exception e) {
            logger.info("启动失败", e);
        }
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = {"com/hl/my-process.bpmn20.xml"})
    public void testCandidateUser() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();
        logger.info("processDefinition.id={}", processDefinition.getId());

        repositoryService.addCandidateStarterUser(processDefinition.getId(), "user");
        repositoryService.addCandidateStarterGroup(processDefinition.getId(),"groupM");

        List<IdentityLink> identityLinkList = repositoryService.getIdentityLinksForProcessDefinition(processDefinition.getId());
        for (IdentityLink identityLink : identityLinkList) {
            logger.info("identityLink={}", identityLink);
        }

        repositoryService.deleteCandidateStarterGroup(processDefinition.getId(), "groupM");
        repositoryService.deleteCandidateStarterUser(processDefinition.getId(), "user");
    }

}
