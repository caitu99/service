/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.utils.unionPay;

import org.junit.Test;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.utils.unionpay.UnionPay;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UnionPayTest 
 * @author ws
 * @date 2015年12月30日 下午12:28:29 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class UnionPayTest extends AbstractJunit{

	
	@Test
	public void test() {
	
		UnionPay unionPay = new UnionPay();
		unionPay.queryOrder("20151230123505", "20151230123505");
		
	}

}
