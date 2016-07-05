package com.caitu99.service.integral.dao;

import java.util.List;

import com.caitu99.service.integral.domain.Manual;

public interface ManualMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(Manual record);

    int insertSelective(Manual record);

    Manual selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Manual record);

    int updateByPrimaryKey(Manual record);
    
    List<Manual> list();
    
    int countManual();
}