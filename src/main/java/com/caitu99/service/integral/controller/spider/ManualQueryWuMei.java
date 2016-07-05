/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.spider;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.exception.ManualQueryAdaptorException;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.json.JsonResult;
import com.caitu99.service.utils.string.StrUtil;

/**
 * 物美
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ManualQueryWuMei 
 * @author ws
 * @date 2015年12月12日 上午10:08:24 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Component
public class ManualQueryWuMei extends ManualQueryAbstract{
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryWuMei.class);

	@Autowired
	private RedisOperate redis;
	

	@Override
	public String getImageCode(Long userid){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/wumart/imgcode/1.0";
		super.setUrl(url);
		super.setName("物美");
		super.setSucceedCode(1045);
		super.setFailureCode(ApiResultCode.WUMEI_GET_IMAGECODE_ERROR);
		
		return super.getImageCode(userid);
	}
	
	/**
	 * 登录
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userId
	 * @param phone
	 * @param password
	 * @return
	 * @date 2015年12月12日 上午10:42:08  
	 * @author ws
	 * @param imageCode 
	 * @param provinceType 
	*/
	public String login(Long userId, String phone, String password, String imageCode, String provinceType) {
		try{
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/wumart/login/1.0";
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userId.toString());
			paramMap.put("account", phone);
			paramMap.put("password", password);
			paramMap.put("yzm", imageCode);
			paramMap.put("province", provinceType);
			
			if(!StrUtil.isPhone(phone)){//不是手机，直接报错
				return ApiResult.outSucceed(ApiResultCode.PHONE_NO_ERROR, "手机号码格式错误，请确认手机号");
			}
			
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			logger.info("物美返回数据:" + jsonString);
			
			Boolean flag = JsonResult.checkResult(jsonString,ApiResultCode.SUCCEED);
			if(!flag){
				JSONObject json = JSON.parseObject(jsonString);
				Integer code = json.getInteger("code");
				
				if(code.equals(2005)){// 验证码错误
					String image = getImageCode(userId);
					image = analysisImageCode(image);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.WUMEI_IMGCODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else if(code.equals(1045)){// 重置密码
					
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.WUMEI_IMGCODE_INPUT);
					resultJson.put("message", "请输入图片验证码");
					resultJson.put("imagecode", json.getString("data"));
					return resultJson.toJSONString();
				}else{// 其他
					return ApiResult.outSucceed(ApiResultCode.WUMEI_LOGIN_ERROR, "登录失败，系统繁忙");
				}
			}
			return jsonString;
		}catch (Exception e) {
			logger.error("登录物美失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.WUMEI_LOGIN_ERROR,e.getMessage());
		}
	}

	/**
	 * 验证图片验证码
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkImgCode 
	 * @param userId
	 * @param imageCode
	 * @return
	 * @date 2015年12月12日 下午2:02:42  
	 * @author ws
	*/
	public String checkImgCode(Long userId, String imageCode) {
		try{
			String url = SpringContext.getBean(AppConfig.class).spiderUrl 
						+ "/spider/wumart/check/1.0";
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userId.toString());
			paramMap.put("yzm", imageCode);
			
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			logger.info("物美返回数据:" + jsonString);
			
			Boolean flag = JsonResult.checkResult(jsonString,ApiResultCode.SUCCEED);
			if(!flag){
				JSONObject json = JSON.parseObject(jsonString);
				Integer code = json.getInteger("code");
				if(code.equals(2005)){
					String image = getImageCodeFinal(userId);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.WUMEI_IMGCODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else {
					return ApiResult.outSucceed(ApiResultCode.WUMEI_LOGIN_ERROR, "登录失败，系统繁忙");
				}
			}
			return jsonString;
		}catch (Exception e) {
			logger.error("物美图片验证码验证失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.WUMEI_LOGIN_ERROR,e.getMessage());
		}
	}

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkPhoneCode 
	 * @param userId
	 * @param phoneCode
	 * @return
	 * @date 2015年12月12日 下午2:15:18  
	 * @author ws
	*/
	public String checkPhoneCode(Long userId, String phoneCode) {
		try{
			String url = SpringContext.getBean(AppConfig.class).spiderUrl 
						+ "/spider/wumart/modify/1.0";
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userId.toString());
			paramMap.put("vcodes", phoneCode);
			
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			logger.info("物美返回数据:" + jsonString);
			
			Boolean flag = JsonResult.checkResult(jsonString,ApiResultCode.SUCCEED);
			JSONObject json = JSON.parseObject(jsonString);
			if(!flag){
				Integer code = json.getInteger("code");
				if(code.equals(2007)){
					return ApiResult.outSucceed(ApiResultCode.WUMEI_LOGIN_ERROR, "短信验证码错误");
				}else if(code.equals(1045)){// 修改成功，获取登录验证码
					
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.WUMEI_IMGCODE_INPUT);
					resultJson.put("message", "请输入图片验证码");
					resultJson.put("imagecode", json.getString("data"));
					return resultJson.toJSONString();
				}else {
					return ApiResult.outSucceed(ApiResultCode.WUMEI_LOGIN_ERROR, "登录失败，系统繁忙 ");
				}
			}
			
			return jsonString;
		}catch (Exception e) {
			logger.error("物美短信验证码验证失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.WUMEI_LOGIN_ERROR,e.getMessage());
		}
	}

	/**
	 * 重置密码后的登录
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginSecond 
	 * @param userId
	 * @param imageCode
	 * @return
	 * @date 2015年12月12日 下午6:14:39  
	 * @author ws
	*/
	public String loginSecond(Long userId, String imageCode) {
		try{
			String url = SpringContext.getBean(AppConfig.class).spiderUrl 
						+ "/spider/wumart/loginsecond/1.0";
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userId.toString());
			paramMap.put("yzm", imageCode);
			
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			logger.info("物美返回数据:" + jsonString);
			
			Boolean flag = JsonResult.checkResult(jsonString,ApiResultCode.SUCCEED);
			if(!flag){
				
				JSONObject json = JSON.parseObject(jsonString);
				Integer code = json.getInteger("code");
				if(code.equals(2005)){// 验证码错误
					String image = getImageCodeFinal(userId);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.WUMEI_IMGCODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else {
					return ApiResult.outSucceed(ApiResultCode.WUMEI_LOGIN_ERROR, "登录失败，系统繁忙");
				}
			}
			return jsonString;
		}catch (Exception e) {
			logger.error("登录物美失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.WUMEI_LOGIN_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 物美  图片验证码获取
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCode 
	 * @param userid
	 * @return
	 * @throws ManualQueryAdaptorException
	 * @date 2015年12月12日 上午10:17:19  
	 * @author ws
	 */
	public String getImageCodeFinal(Long userid) throws ManualQueryAdaptorException {
		try {
			AssertUtil.notNull(userid, "用户ID不能为空");
			
			String url = SpringContext.getBean(AppConfig.class).spiderUrl 
						+ "/spider/wumart/imgcodefinal/1.0";
		
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userid.toString());
			
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			logger.info("物美图片验证码返回数据:" + jsonString);
			
			Boolean flag = JsonResult.checkResult(jsonString,1045);
			if(!flag){
				AssertUtil.isTrue(false, "获取物美图片验证码失败");
			}
			
			String imageCode = JsonResult.getResult(jsonString, "data", true, "图片验证码为空");
			
			return imageCode;
		} catch (Exception e) {
			logger.error("获取物美图片验证码失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.WUMEI_GET_IMAGECODE_ERROR,e.getMessage());
		}
	}

	@Override
	public String login(Long userId, String loginAccount, String password,String imageCode) {
		return null;
	}

	@Override
	public String login(Long userId, String loginAccount, String password) {
		return null;
	}

	@Override
	public String save(Long userId, Long manualId, String loginAccount,String password, String result, String version) {
		return null;
	}
}
