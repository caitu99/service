/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.spider;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.exception.ManualQueryAdaptorException;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.json.JsonResult;

/** 
 * 浦发银行积分查询
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ManualQueryPuFaBank 
 * @author ws
 * @date 2016年4月6日 下午2:56:37 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Component
public class ManualQueryPuFaBank extends ManualQueryAbstract{
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryPuFaBank.class);

	@Override
	public String getImageCode(Long userid){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/pufabank/img/1.0";
		super.setUrl(url);
		super.setName("浦发银行");
		super.setSucceedCode(ApiResultCode.NEED_INOUT_IMAGECODE);
		super.setFailureCode(ApiResultCode.BOTAOHUI_GET_IMAGECODE_ERROR);
		
		return super.getImageCode(userid);
	}
	
	@Override
	public String login(Long userId, String loginAccount, String password,String imageCode) {
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/pufabank/login/1.0";
		super.setName("浦发银行");
		Map<String,String> param = new HashMap<String,String>();
		param.put("userid", userId.toString());
		param.put("account", loginAccount);
		param.put("password", password);
		param.put("vcode", imageCode);

		String result = super.login(userId, url, param, ApiResultCode.CMCC_SEND_PHONECODE_SUCCEED);
		
		//将铂涛会的登录账号码放入redis
		String key = String.format(RedisKey.PUFABANK_MANUAL_USER, userId);
		Map<String,String> loginParam = new HashMap<String, String>();
		loginParam.put("loginAccount", loginAccount);
		loginParam.put("password", password);
		redis.set(key, JSON.toJSONString(loginParam));
		
		return result;
	}

	@Override
	public String login(Long userId, String loginAccount, String password) {
		return null;
	}

	@Override
	public String save(Long userId, Long manualId, String loginAccount,String password, String result, String version) {
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", ApiResultCode.FOLLOWUP_VERIFY_PHONE);
			resultJson.put("message", "请输入短信验证码");
			
			return resultJson.toJSONString();
		}
		return result;
	}
	
	/**
	 * 登录，获得积分
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getPhoneCode 
	 * @param userid		用户ID
	 * @param phone			手机号码
	 * @date 2015年11月20日 下午12:23:04  
	 * @author chenhl
	 */
	public String login(Long userid,String phoneCode){
		try {
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/pufabank/verify/1.0";
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("msmCode", phoneCode);
			
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			logger.info("登录浦发银行返回数据:" + jsonString);
			
			return jsonString;
		}catch (Exception e) {
			logger.error("登录浦发银行失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.SYSTEM_BUSY,"系统繁忙");
		}
		
		
	}
}
