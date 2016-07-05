/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.api;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: CompanyToThawTest 
 * @author ws
 * @date 2015年12月2日 下午5:55:05 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class CompanyToThawTest extends AbstractJunit{

	
	@Autowired
	ICompanyToThaw companyToThaw;
	
	@Test
	public void test() throws InterruptedException {
		
		String phoneNo = "13325853121";
		String orderNo = "dddhhhhh234";
		Long integral = 100L;
		companyToThaw.addCompanyToThaw(phoneNo, orderNo, integral);
		
		while(true){
			Thread.sleep(10000L);
		}
	}

}
