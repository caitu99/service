/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.service.CardTypeService;

/** 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: CardType 
 * @author xiongbin
 * @date 2016年1月26日 下午3:11:19 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class CardTypeTest extends AbstractJunit {

	@Autowired
	private CardTypeService cardTypeService;
	
	@Test
	public void test(){
		List<CardType> list = cardTypeService.selectAll();
		
		for(CardType cardType : list){
			System.out.println(cardType.getId() + ":" + cardType.getUrl());
		}
	}

}
