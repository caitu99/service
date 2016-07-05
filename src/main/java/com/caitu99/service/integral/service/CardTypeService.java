package com.caitu99.service.integral.service;

import java.util.List;

import com.caitu99.service.integral.domain.CardType;

public interface CardTypeService {

	List<CardType> listAll();

	int add(CardType cardType);

	CardType selectByName(String name);

	int modify(CardType cardType);
	
	List<CardType> selectAll();
	
	CardType selectByPrimaryKey(Long id);
	
	CardType selectByManualId(Long manualId);
	
	CardType selectByPlatformId(Long platformId);
	
	String selectCardTypeList();
	
	List<CardType> selectAllToSort();
}
