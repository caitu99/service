package com.caitu99.service.realization.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.realization.domain.RealizeRecordEsurfing;

public interface RealizeRecordEsurfingMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(RealizeRecordEsurfing record);

    RealizeRecordEsurfing selectByPrimaryKey(Long id);

    int update(RealizeRecordEsurfing record);

    List<RealizeRecordEsurfing> selectPageList(Map<String,Object> map);
}