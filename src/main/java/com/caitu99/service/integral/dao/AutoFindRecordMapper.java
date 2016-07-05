package com.caitu99.service.integral.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.integral.domain.AutoFindRecord;

public interface AutoFindRecordMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(AutoFindRecord record);

    AutoFindRecord selectByPrimaryKey(Long id);

    int update(AutoFindRecord record);
    
    AutoFindRecord getBySelective(Map<String,Object> map);
    
    List<AutoFindRecord> selectPageList(Map<String,Object> map);
}