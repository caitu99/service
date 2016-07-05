package com.caitu99.service.activities.dao;

import java.util.List;

import com.caitu99.service.activities.domain.ActivitiesTime;

public interface ActivitiesTimeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ActivitiesTime record);

    int insertSelective(ActivitiesTime record);

    ActivitiesTime selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ActivitiesTime record);

    int updateByPrimaryKey(ActivitiesTime record);
    
    List<ActivitiesTime> findByItemId(Long outId);
}