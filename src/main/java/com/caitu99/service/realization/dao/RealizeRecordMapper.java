package com.caitu99.service.realization.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.realization.domain.RealizeRecord;

public interface RealizeRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RealizeRecord record);

    int insertSelective(RealizeRecord record);

    RealizeRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RealizeRecord record);

    int updateByPrimaryKey(RealizeRecord record);

    RealizeRecord selectByOrderNo(String orderNo);
    
    List<RealizeRecord> selectRealizeDetailFirst(Map<String,Object> map);
}