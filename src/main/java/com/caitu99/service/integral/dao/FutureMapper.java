package com.caitu99.service.integral.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.integral.domain.Future;

public interface FutureMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Future record);

    int insertSelective(Future record);

    Future selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Future record);

    int updateByPrimaryKey(Future record);
    
    List<Future> findListByManualIdType(Map<String,Object> map);
}