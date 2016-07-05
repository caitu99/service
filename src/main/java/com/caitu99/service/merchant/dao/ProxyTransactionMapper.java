package com.caitu99.service.merchant.dao;

import java.util.Map;

import com.caitu99.service.merchant.domain.ProxyTransaction;

public interface ProxyTransactionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProxyTransaction record);

    int insertSelective(ProxyTransaction record);

    ProxyTransaction selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProxyTransaction record);

    int updateByPrimaryKey(ProxyTransaction record);

	ProxyTransaction getSettleData(Map<String, Object> queryParam);

	Long collectAll(Map<String, Object> queryMap);
}