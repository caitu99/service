/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.user.service;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.realization.domain.PhoneAmount;
import com.caitu99.service.realization.service.PhoneAmountService;
import com.caitu99.service.user.domain.Address;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AddressServiceTest 
 * @author ws
 * @date 2016年1月21日 下午4:56:22 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class PhoneAmountServiceTest extends AbstractJunit{
	
	@Autowired
	PhoneAmountService phoneAmountService;
	/**
	 * Test method for {@link com.caitu99.service.user.service.impl.AddressServiceImpl#selectByUserId(java.lang.Long)}.
	 */
	@Test
	public void testSelectByUserId() {
		List<PhoneAmount> amountList = phoneAmountService.getPhoneAmountList();
		
		for (PhoneAmount phoneAmount : amountList) {
			System.out.println("金额："+phoneAmount.getName()+",比例："+phoneAmount.getScale());
		}
		
	}

}
