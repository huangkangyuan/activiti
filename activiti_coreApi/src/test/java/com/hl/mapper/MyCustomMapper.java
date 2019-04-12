package com.hl.mapper;

import org.activiti.engine.task.Task;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface MyCustomMapper {

    @Select("select * from ACT_RU_TASK")
    public List<Map<String,Object>> findAll();
}
