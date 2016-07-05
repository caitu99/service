/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.user.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.integral.controller.auto.AutoFindCU;
import com.caitu99.service.integral.domain.ExchangeRule;
import com.caitu99.service.integral.domain.ManualResult;
import com.caitu99.service.integral.service.ExchangeRuleService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UserCardServiceTest 
 * @author ws
 * @date 2016年2月24日 上午11:17:41 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class UserCardServiceTest extends AbstractJunit{
	@Autowired
	UserCardService userCardService;
	/**
	 * Test method for {@link com.caitu99.service.user.service.impl.UserCardServiceImpl#getByUserManualInfo(java.util.Map)}.
	 */
	@Test
	public void testGetByUserManualInfo() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", 262);
		map.put("manualId", 2);
		map.put("loginAccount", "412726199311032976");
		List<ManualResult> result = userCardService.getByUserManualInfo(map );
		//System.out.println(result.getUserName());
	}
	@Autowired
	AutoFindCU autoFindCU;
	@Test
	public void test1(){
		autoFindCU.pushIntegralChangeMessage(262L, 12L, 144, 10);
	}

	
	@Autowired
	ExchangeRuleService exchangeRuleService;
	
	@Test
	public void testEx(){
		ExchangeRule res = exchangeRuleService.findByCardTypeName("浦发银行");
		System.out.println(res.getCardTypeId()+":"+res.getScale());
	}
	
}
