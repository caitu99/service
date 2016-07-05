package com.caitu99.service.merchant.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.merchant.domain.ProxyTransactionItem;
import com.caitu99.service.merchant.domain.SmspayRecord;

public interface SmspayRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmspayRecord record);

    int insertSelective(SmspayRecord record);

    SmspayRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmspayRecord record);

    int updateByPrimaryKey(SmspayRecord record);
    
	Long collectSettleData(Map<String, Object> queryParam);

	List<ProxyTransactionItem> collectSettleDetailData(Map<String, Object> queryParam);

	List<SmspayRecord> queryRecord(Map<String, Object> queryParam);

	Long collectAll(Long userId);
}