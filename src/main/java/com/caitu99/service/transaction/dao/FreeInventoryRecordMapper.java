package com.caitu99.service.transaction.dao;

import java.util.List;

import com.caitu99.service.transaction.domain.FreeInventoryRecord;

public interface FreeInventoryRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(FreeInventoryRecord record);

    FreeInventoryRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FreeInventoryRecord record);
    
    Integer findFreeInventoryRecordByTime(FreeInventoryRecord record);
    
    List<FreeInventoryRecord> findRecordByOrderNo(String orderNo);
    
    void deleteByOrderNo(String orderNo);
    
}