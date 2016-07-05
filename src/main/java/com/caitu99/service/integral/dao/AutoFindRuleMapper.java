package com.caitu99.service.integral.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.integral.domain.AutoFindRule;

public interface AutoFindRuleMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(AutoFindRule record);

    int insertSelective(AutoFindRule record);

    AutoFindRule selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AutoFindRule record);

    int updateByPrimaryKey(AutoFindRule record);

    AutoFindRule selectBySelective(Map<String,Object> map);

    List<AutoFindRule> list(Map<String,Object> map);
}