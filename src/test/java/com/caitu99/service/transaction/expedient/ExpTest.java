/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.expedient;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AbstractJunit;
import com.caitu99.service.expedient.domain.ExpData;
import com.caitu99.service.expedient.provider.AddExp;
import com.caitu99.service.expedient.provider.rule.AddExpByIntegral;
import com.caitu99.service.expedient.provider.rule.AddExpByIshop;
import com.caitu99.service.expedient.provider.rule.AddExpByRealization;
import com.caitu99.service.expedient.provider.rule.AddExpByShare;
import com.caitu99.service.expedient.provider.rule.AddExpBySign;
import com.caitu99.service.utils.ApiResultCode;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ExpTest 
 * @author fangjunxiao
 * @date 2016年6月1日 下午6:01:02 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ExpTest extends AbstractJunit{


	@Autowired
	private AddExp addExp;
	
	@Autowired
	private AddExpByIntegral addExpByIntegral;
	
	@Autowired
	private AddExpBySign addExpBySign;
	
	@Autowired
	private AddExpByIshop addExpByIshop;
	
	@Autowired
	private AddExpByRealization addExpByRealization;
	
	@Autowired
	private AddExpByShare addExpByShare;
	
	
	@Test
	public void aa(){
		
		
		Long userId = 111L;
		Long manualId = 2L;
		String loginAccount = "4895920201109690";
		
		Map<String,Object> resData = new HashMap<String, Object>();
		resData.put("userId", String.valueOf(userId));
		resData.put("manualId", String.valueOf(manualId));
		resData.put("loginAccount", loginAccount);
		
		
		JSONObject resultJson = new JSONObject();
		resultJson.put("code", ApiResultCode.SUCCEED);
		resultJson.put("message", "登录成功");
		resultJson.put("data", resData);
		
		String result =  resultJson.toJSONString();
		
		
		ExpData expdata = new ExpData();
		expdata.setUserId(userId);
		expdata.setIntegralResult(result);
		addExp.addExp(expdata, addExpByIntegral);
		
	}
	
	
	@Test
	public void bb(){
		
		
		Long userId = 111L;
		
		ExpData expdata = new ExpData();
		expdata.setUserId(userId);
		expdata.setContinuousTime(5L);
		addExp.addExp(expdata, addExpBySign);
		
	}
	
	
	@Test
	public void cc(){
		
		
		Long userId = 111L;
		Long inegral = 7000L;
		
		ExpData expdata = new ExpData();
		expdata.setInegral(inegral);
		expdata.setUserId(userId);
		addExp.addExp(expdata, addExpByIshop);
		
	}
	
	
	@Test
	public void dd(){
		Long userId = 111L;
		Long cash = 60000L;
		
		ExpData expdata = new ExpData();
		expdata.setUserId(userId);
		expdata.setCash(cash);
		addExp.addExp(expdata, addExpByRealization);
		
	}
	
	@Test
	public void ee(){
		Long userId = 111L;
		
		ExpData expdata = new ExpData();
		expdata.setUserId(userId);
		addExp.addExp(expdata, addExpByShare);
	}
	
	
}
