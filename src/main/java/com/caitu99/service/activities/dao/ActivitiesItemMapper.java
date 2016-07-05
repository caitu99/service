package com.caitu99.service.activities.dao;

import java.util.List;

import com.caitu99.service.activities.domain.ActivitiesItem;

public interface ActivitiesItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ActivitiesItem record);

    int insertSelective(ActivitiesItem record);

    ActivitiesItem selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ActivitiesItem record);

    int updateByPrimaryKey(ActivitiesItem record);
    
    
    List<ActivitiesItem> findAllByActivitiesId(Long activitiesId);
}