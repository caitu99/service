/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.expedient.domain.ExpData;
import com.caitu99.service.expedient.provider.AddExp;
import com.caitu99.service.expedient.provider.rule.AddExpByIntegral;
import com.caitu99.service.integral.controller.vo.UnionPayOrder;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: TransactionRecordService 
 * @author ws
 * @date 2016年1月6日 下午2:05:08 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class TransactionRecordServiceTest extends AbstractJunit{
	
	@Autowired
	private AddExp addExp;
	
	@Autowired
	private OrderService orderSevice;
	
	
	@Test
	public void aa(){
		Long userId = 395L;
		Long amountId = 4L;
		String moblie = "13588313856";
		String no = orderSevice.phoneRechargeOrderByJf(userId, amountId, moblie);
		System.out.println(no);
	}

}
