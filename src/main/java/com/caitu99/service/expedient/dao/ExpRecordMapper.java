package com.caitu99.service.expedient.dao;

import java.util.Map;

import com.caitu99.service.expedient.domain.ExpRecord;
import com.caitu99.service.expedient.dto.ExpRecordDto;

public interface ExpRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ExpRecord record);

    int insertSelective(ExpRecord record);

    ExpRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ExpRecord record);

    int updateByPrimaryKey(ExpRecord record);
    
    int countCardByCreateTime(Map<String,Object> map);
    
    
    Long findAllExpByToDay(Map<String,Object> map);
    
    ExpRecordDto countByuserIdAndSource(Map<String,Object> map);
}