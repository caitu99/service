package com.caitu99.service.integral.dao;

import java.util.Map;

import com.caitu99.service.integral.domain.IntegralRealizationSubscribe;

public interface IntegralRealizationSubscribeMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(IntegralRealizationSubscribe record);

    int insertSelective(IntegralRealizationSubscribe record);

    IntegralRealizationSubscribe selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(IntegralRealizationSubscribe record);

    int updateByPrimaryKey(IntegralRealizationSubscribe record);
    
    IntegralRealizationSubscribe selectByUserIdCardTypeId(Map<String,Object> map);
    
    Integer selectCount(Long cardTypeId);
}