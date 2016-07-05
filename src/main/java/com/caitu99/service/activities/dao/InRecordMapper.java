package com.caitu99.service.activities.dao;

import java.util.Map;

import com.caitu99.service.activities.domain.InRecord;

public interface InRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(InRecord record);

    int insertSelective(InRecord record);

    InRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InRecord record);

    int updateByPrimaryKey(InRecord record);
    
    int getCountUserInRecord(Map<String,Object> map);
}