package com.caitu99.service.activities.dao;

import com.caitu99.service.activities.domain.ActivitiesStatistics;

public interface ActivitiesStatisticsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ActivitiesStatistics record);

    int insertSelective(ActivitiesStatistics record);

    ActivitiesStatistics selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ActivitiesStatistics record);

    int updateByPrimaryKey(ActivitiesStatistics record);
}