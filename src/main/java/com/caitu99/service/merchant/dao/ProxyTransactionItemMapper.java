package com.caitu99.service.merchant.dao;

import java.util.List;

import com.caitu99.service.merchant.domain.ProxyTransactionItem;

public interface ProxyTransactionItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProxyTransactionItem record);

    int insertSelective(ProxyTransactionItem record);

    ProxyTransactionItem selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProxyTransactionItem record);

    int updateByPrimaryKey(ProxyTransactionItem record);

	List<ProxyTransactionItem> getBySettleId(Long proxyTransactionId);
}