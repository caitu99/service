package com.caitu99.service.integral.dao;

import java.util.List;

import com.caitu99.service.integral.domain.CardType;

public interface CardTypeMapper {
	int deleteByPrimaryKey(Long id);

	int insert(CardType record);

	int insertSelective(CardType record);

	CardType selectByPrimaryKey(Long id);

	CardType selectByName(String name);

	int updateByPrimaryKeySelective(CardType record);

	int updateByPrimaryKey(CardType record);

	List<CardType> listAll();

	List<CardType> selectAll();

	CardType selectByManualId(Long manualId);

	CardType selectByPlatformId(Long platformId);
	
	List<CardType> selectAllToSort();
}