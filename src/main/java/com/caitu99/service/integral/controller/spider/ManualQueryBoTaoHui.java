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
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ManualQueryBoTaoHui 
 * @author chenhl
 * @date 2015年12月3日 下午3:33:44 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Component
public class ManualQueryBoTaoHui extends ManualQueryAbstract{
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryBoTaoHui.class);

	@Override
	public String getImageCode(Long userid){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/botaohui/loginpage/1.0";
		super.setUrl(url);
		super.setName("铂涛会");
		super.setSucceedCode(ApiResultCode.NEED_INOUT_IMAGECODE);
		super.setFailureCode(ApiResultCode.BOTAOHUI_GET_IMAGECODE_ERROR);
		
		return super.getImageCode(userid);
	}
	
	@Override
	public String login(Long userId, String loginAccount, String password,String imageCode) {
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/botaohui/sendsms/1.0";
		super.setName("铂涛会");
		Map<String,String> param = new HashMap<String,String>();
		param.put("userid", userId.toString());
		param.put("account", loginAccount);
		param.put("vcode", imageCode);

		String result = super.login(userId, url, param, ApiResultCode.CMCC_SEND_PHONECODE_SUCCEED);
		
		//将铂涛会的登录账号码放入redis
		String key = String.format(RedisKey.BOTAOHUI_MANUAL_USER, userId);
		redis.set(key, loginAccount);
		
		return result;
	}

	@Override
	public String login(Long userId, String loginAccount, String password) {
		return null;
	}

	@Override
	public String save(Long userId, Long manualId, String loginAccount,String password, String result,String version) {
		boolean flag = JsonResult.checkResult(result,ApiResultCode.CMCC_SEND_PHONECODE_SUCCEED);
		if(flag){
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", ApiResultCode.FOLLOWUP_VERIFY_PHONE);
			resultJson.put("message", "验证成功,有短信验证");
			
			return resultJson.toJSONString();
		}
		return result;
	}
	
	/**
	 * 获取短信验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getPhoneCode 
	 * @param userid		用户ID
	 * @param phone			手机号码
	 * @date 2015年11月20日 下午12:23:04  
	 * @author chenhl
	 */
	public String getPhoneCode(Long userid,String phone,String imageCode){
		try {
			AssertUtil.notNull(userid, "用户ID不能为空");
			AssertUtil.hasLength(phone, "手机号码不能为空");
			
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/botaohui/sendsms/1.0";
//			String url = "http://127.0.0.1:8092/spider/api/botaohui/sendsms/1.0";

			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("account", phone);
			paramMap.put("vcode", imageCode);
			
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			logger.info("获取铂涛会短信验证码返回数据:" + jsonString);
			
			Boolean flag = JsonResult.checkResult(jsonString,1051);
			if(!flag){
				JSONObject json = JSON.parseObject(jsonString);
				Integer code = json.getInteger("code");
				if(code.equals(1071)){
					String image = getImageCode(userid);
					image = analysisImageCode(image);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else if(code.equals(1074)){
					String message = json.getString("message");
					if( message == null || "".equals(message)){ message = "请求短信验证码过于频繁，稍后再试";}
					return ApiResult.outSucceed(ApiResultCode.BOTAOHUI_PHONECODE_TWICE_ERROR,message);
				}else if(code.equals(1072)){
					return ApiResult.outSucceed(ApiResultCode.SYSTEM_BUSY,"系统繁忙，稍后再试");
				}
			}
			return jsonString;
		} catch (Exception e) {
			logger.error("获取铂涛会短信验证码失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.BOTAOHUI_GET_PHONE_CODE_ERROR,e.getMessage());
		}
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
			AssertUtil.notNull(userid, "用户ID不能为空");
			AssertUtil.hasLength(phoneCode, "短信验证码不能为空");
			
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/botaohui/login/1.0";
//			String url = "http://127.0.0.1:8092/spider/api/botaohui/login/1.0";
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("password", phoneCode);
			
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			logger.info("登录铂涛会返回数据:" + jsonString);
			
			Boolean flag = JsonResult.checkResult(jsonString,ApiResultCode.SUCCEED);
			if(!flag){
				JSONObject json = JSON.parseObject(jsonString);
				Integer code = json.getInteger("code");
				
				if(code.equals(1053)){
					return ApiResult.outSucceed(ApiResultCode.BOTAOHUI_LOGIN_ERROR,json.getString("message"));
				}
			}
			return jsonString;
		}catch (Exception e) {
			logger.error("登录铂涛会失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.CMCC_LOGIN_ERROR,e.getMessage());
		}
		
		
	}
}
