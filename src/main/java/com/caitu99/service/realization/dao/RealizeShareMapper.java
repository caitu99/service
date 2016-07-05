package com.caitu99.service.realization.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.realization.domain.RealizeShare;

public interface RealizeShareMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RealizeShare record);

    int insertSelective(RealizeShare record);

    RealizeShare selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RealizeShare record);

    int updateByPrimaryKey(RealizeShare record);
    
    List<RealizeShare> selectPageList(Map<String,Object> map);
}