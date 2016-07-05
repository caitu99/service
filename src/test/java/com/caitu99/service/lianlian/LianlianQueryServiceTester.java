/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.lianlian;

import org.junit.Test;

import com.caitu99.service.lianlianpay.service.LianlianQueryService;
import com.caitu99.service.lianlianpay.service.impl.LianlianQueryServiceImpl;
import com.caitu99.service.lianlianpay.utils.LLPayUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: LianlianQueryServiceTester 
 * @author ws
 * @date 2016年6月16日 上午9:50:12 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class LianlianQueryServiceTester {

	
	
	/**
	 * Test method for {@link com.caitu99.service.lianlianpay.service.impl.LianlianQueryServiceImpl#refund(java.lang.String, java.lang.String, java.lang.Double, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testRefund() {
		
		LianlianQueryService service = new LianlianQueryServiceImpl();

		String backUrl = "";
		backUrl = "http://hongbo1989.eicp.net:13030" + "/public/lianlian/refund/backresponse/1.0";
		
		service.refund("test2016062202refund", LLPayUtil.getCurrentDateTimeStr(), 0.01, "2016062233465186", backUrl);
		
	}
	

	/**
	 * Test method for {@link com.caitu99.service.lianlianpay.service.impl.LianlianQueryServiceImpl#refund(java.lang.String, java.lang.String, java.lang.Double, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetCardbin() {
		
		LianlianQueryService service = new LianlianQueryServiceImpl();

		String backUrl = "";
		backUrl = "http://hongbo1989.eicp.net:13030" + "/public/lianlian/refund/backresponse/1.0";
		
		String result = service.getCardBin("6226095711169859");
		service.refund("test2016062002refund", LLPayUtil.getCurrentDateTimeStr(), 0.01, "2016062026296752", backUrl);
		
		System.out.println(result);
		
	}

}
