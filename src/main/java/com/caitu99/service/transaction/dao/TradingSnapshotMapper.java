package com.caitu99.service.transaction.dao;

import com.caitu99.service.transaction.domain.TradingSnapshot;

public interface TradingSnapshotMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TradingSnapshot record);

    int insertSelective(TradingSnapshot record);

    TradingSnapshot selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TradingSnapshot record);

    int updateByPrimaryKeyWithBLOBs(TradingSnapshot record);

    int updateByPrimaryKey(TradingSnapshot record);
}