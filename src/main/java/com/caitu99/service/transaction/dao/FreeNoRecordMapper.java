package com.caitu99.service.transaction.dao;

import java.util.List;

import com.caitu99.service.transaction.domain.FreeNoRecord;

public interface FreeNoRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FreeNoRecord record);

    int insertSelective(FreeNoRecord record);

    FreeNoRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FreeNoRecord record);

    int updateByPrimaryKey(FreeNoRecord record);
    
    List<FreeNoRecord> findNoListByOrderNo(String orderNo);
}