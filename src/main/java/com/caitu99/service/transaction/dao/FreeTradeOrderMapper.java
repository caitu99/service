package com.caitu99.service.transaction.dao;

import com.caitu99.service.transaction.domain.FreeTradeOrder;

public interface FreeTradeOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FreeTradeOrder record);

    int insertSelective(FreeTradeOrder record);

    FreeTradeOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FreeTradeOrder record);

    int updateByPrimaryKey(FreeTradeOrder record);
}