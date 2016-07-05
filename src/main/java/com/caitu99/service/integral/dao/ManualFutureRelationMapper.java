package com.caitu99.service.integral.dao;

import com.caitu99.service.integral.domain.ManualFutureRelation;

public interface ManualFutureRelationMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(ManualFutureRelation record);

    int insertSelective(ManualFutureRelation record);

    ManualFutureRelation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ManualFutureRelation record);

    int updateByPrimaryKey(ManualFutureRelation record);
}