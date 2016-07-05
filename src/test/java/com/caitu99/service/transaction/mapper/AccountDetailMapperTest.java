/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.mapper;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.transaction.dao.AccountDetailMapper;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AccountDetailMapperTest 
 * @author ws
 * @date 2015年12月2日 上午10:23:41 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class AccountDetailMapperTest extends AbstractJunit{

	@Autowired
	AccountDetailMapper mapper;
	
	@Test
	public void testcountTotalIntegralByUserId() {
		try{
			Long userId = 1L;
			Long result = mapper.countTotalIntegralByUserId(userId );
			System.out.println(result);
		}catch(Exception ex){
			fail();
			ex.printStackTrace();
		}
	}

}
