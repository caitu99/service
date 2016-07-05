package com.caitu99.service.realization.dao;

import com.caitu99.service.realization.domain.RealizeRecordCm;

public interface RealizeRecordCmMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RealizeRecordCm record);

    int insertSelective(RealizeRecordCm record);

    RealizeRecordCm selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RealizeRecordCm record);

    int updateByPrimaryKey(RealizeRecordCm record);
    
    Long getUserRealizeSUM(RealizeRecordCm recor);
    
    Long getWLTAccountSUM(String wltAccount); 
}