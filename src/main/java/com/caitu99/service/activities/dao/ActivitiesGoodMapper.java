package com.caitu99.service.activities.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.activities.domain.ActivitiesGood;
import com.caitu99.service.activities.dto.ActivitiesGoodDto;

public interface ActivitiesGoodMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ActivitiesGood record);

    int insertSelective(ActivitiesGood record);

    ActivitiesGood selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ActivitiesGood record);

    int updateByPrimaryKey(ActivitiesGood record);
    
    
    List<ActivitiesGoodDto> findBySort(Map<String,Object> map);
    
    List<ActivitiesGood> findAllByItemId(Map<String,Object> map);
}