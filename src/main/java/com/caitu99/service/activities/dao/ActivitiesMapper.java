package com.caitu99.service.activities.dao;

import com.caitu99.service.activities.domain.Activities;

public interface ActivitiesMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Activities record);

    int insertSelective(Activities record);

    Activities selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Activities record);

    int updateByPrimaryKey(Activities record);
}