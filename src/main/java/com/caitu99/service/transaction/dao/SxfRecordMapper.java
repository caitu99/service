package com.caitu99.service.transaction.dao;

import com.caitu99.service.transaction.domain.SxfRecord;

public interface SxfRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SxfRecord record);

    int insertSelective(SxfRecord record);

    SxfRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SxfRecord record);

    int updateByPrimaryKey(SxfRecord record);
    
    SxfRecord selectByTransactionNumber(String transactionNumber);
}