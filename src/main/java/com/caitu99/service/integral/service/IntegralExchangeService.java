package com.caitu99.service.integral.service;

import java.util.List;

import com.caitu99.service.integral.domain.IntegralExchange;

public interface IntegralExchangeService {
	int deleteByPrimaryKey(Long id);

	int insert(IntegralExchange record);

	int insertSelective(IntegralExchange record);

	IntegralExchange selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(IntegralExchange record);

	int updateByPrimaryKey(IntegralExchange record);
	
	List<IntegralExchange> selectAll();
	
	List<IntegralExchange> selectByUserId(Long userId);
}
