package com.hl.dbEntity;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbIdentityTest {
    
    private static final Logger logger = LoggerFactory.getLogger(DbIdentityTest.class);
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

    @Test
    public void test(){
        IdentityService identityService = activitiRule.getIdentityService();

        User user1 = identityService.newUser("user1");
        user1.setFirstName("firstName");
        user1.setLastName("lastName");
        user1.setEmail("1@.com");
        user1.setPassword("user1_pwd");
        identityService.saveUser(user1);

        Group group1 = identityService.newGroup("group1");
        group1.setName("group test");
        identityService.saveGroup(group1);

        identityService.createMembership(user1.getId(), group1.getId());

        identityService.setUserInfo(user1.getId(), "age", "21");
    }
}
