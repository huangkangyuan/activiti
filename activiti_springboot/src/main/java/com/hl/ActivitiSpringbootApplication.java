package com.hl;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//springboot2.0需要排除掉这个类 因为Springboot集成activiti时的版本兼容的还是1.x
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ActivitiSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActivitiSpringbootApplication.class, args);
    }

}
