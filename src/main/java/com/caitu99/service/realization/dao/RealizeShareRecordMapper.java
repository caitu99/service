package com.caitu99.service.realization.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.realization.domain.RealizeShareRecord;

public interface RealizeShareRecordMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(RealizeShareRecord record);

    int insertSelective(RealizeShareRecord record);

    RealizeShareRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RealizeShareRecord record);

    int updateByPrimaryKey(RealizeShareRecord record);
    
    List<RealizeShareRecord> selectPageList(Map<String,Object> map);
    
    RealizeShareRecord selectFirstRecord(Map<String,Object> map);
    
    List<RealizeShareRecord> selectByUserId(Map<String, Object> map);
    
    Integer selectCountByUserId(Map<String, Object> map);
}