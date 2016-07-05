/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.mapper;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.transaction.dao.AccountMapper;
import com.caitu99.service.transaction.domain.Account;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AccountMapperTest 
 * @author ws
 * @date 2015年12月2日 上午10:25:42 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class AccountMapperTest extends AbstractJunit{

	@Autowired
	AccountMapper mapper;
	
	@Test
	public void testselectByUserId() {

		try{
			Long userId = 1L;
			Account result = mapper.selectByUserId(userId );
			//Account result = mapper.selectByPrimaryKey(userId );
			System.out.println(result);
		}catch(Exception ex){
			fail();
			throw ex;
		}
	}

	@Test
	public void testupdateIntegralByUserId() {

		try{
			Account record = new Account();
			record.setAvailableIntegral(100L);
			//record.setFreezeIntegral(0L);
			record.setGmtModify(new Date());
			//record.setId(1L);
			record.setTotalIntegral(100L);
			record.setUserId(1L);
			int result = mapper.updateIntegralByUserId(record);
			//Account result = mapper.selectByPrimaryKey(userId );
			System.out.println(result);
		}catch(Exception ex){
			fail();
			throw ex;
		}
	}
}
