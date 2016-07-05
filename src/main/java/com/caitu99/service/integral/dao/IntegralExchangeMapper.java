package com.caitu99.service.integral.dao;

import java.util.List;

import com.caitu99.service.integral.domain.IntegralExchange;
import com.caitu99.platform.dao.base.func.IEntityDAO;

public interface IntegralExchangeMapper extends IEntityDAO<IntegralExchange, IntegralExchange> {
	int deleteByPrimaryKey(Long id);

	int insert(IntegralExchange record);

	int insertSelective(IntegralExchange record);

	IntegralExchange selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(IntegralExchange record);

	int updateByPrimaryKey(IntegralExchange record);

	List<IntegralExchange> selectAll();

	List<IntegralExchange> selectByUserId(Long userId);
}