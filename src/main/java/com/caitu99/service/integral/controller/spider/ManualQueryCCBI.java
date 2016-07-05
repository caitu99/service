/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.spider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.ManualQueryAdaptorException;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.json.JsonResult;

/** 
 * 
 * @Description:中信
 * @ClassName: ManualQueryCCBI 
 * @author fangjunxiao
 * @date 2016年3月11日 下午12:15:04 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Component
public class ManualQueryCCBI extends ManualQueryAbstract{

    private final static Logger logger = LoggerFactory.getLogger(ManualQueryCCBI.class);
	

	@Autowired
	private RedisOperate redis;
	
	
	
	private String getImgCode(Long userid){
			ApiResult<String> result = new ApiResult<String>();
			String name = "中信银行";
		try {
			
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/ishop/ccb/vcode/get/1.0";
			
			if(null == userid){
				return result.toJSONString(-1, "参数userId不能为空");
			}
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userid.toString());
			
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			//1001
			String code = JsonResult.getResult(jsonString,"code");
			if("1001".equals(code)){

				String imageCode = JsonResult.getResult(jsonString, "data");
				
				if(StringUtils.isBlank(imageCode)){
					return result.toJSONString(-1, "获取" + name + "图片验证码失败");
				}
				
				return result.toJSONString(0, "", imageCode);
			}
			
			return result.toJSONString(-1, "获取" + name + "图片验证码失败");

		} catch (Exception e) {
			logger.info("获取" + name + "图片验证码失败:" + e.getMessage(),e);
			return result.toJSONString(-1, "获取" + name + "图片验证码失败:" + e.getMessage());
		}
	}
	
	
	private void saveuser(Long userId,Long manualId,String loginAccount,String password,Integer integral,String name, Map<String,Object> resData, String version){
		Date now = new Date();
		
         String cardNo = loginAccount.substring(loginAccount.length() - 4,loginAccount.length());

		
		/**
		 * 记录用户积分数据 
		 */
		UserCardManual ccbiManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.CCBI_INTEGRAL,null,null,loginAccount);
		
		
		if(null == ccbiManual){
			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.CCBI_INTEGRAL, 0, integral, resData, version);
			
			ccbiManual = new UserCardManual();
			ccbiManual.setIntegral(integral);
			ccbiManual.setUserName(name);
			ccbiManual.setCardNo(cardNo);
			ccbiManual.setGmtModify(now);
			ccbiManual.setGmtCreate(now);
			ccbiManual.setUserId(userId);
			ccbiManual.setLoginAccount(loginAccount);
			ccbiManual.setCardTypeId(UserCardManual.CCBI_INTEGRAL);
		}else{
			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.CCBI_INTEGRAL, ccbiManual.getIntegral(), integral, resData, version);
			
			ccbiManual.setIntegral(integral);
			ccbiManual.setCardNo(cardNo);
			ccbiManual.setUserName(name);
			ccbiManual.setLoginAccount(loginAccount);
			ccbiManual.setGmtModify(now);
			ccbiManual.setStatus(1);
		}
		
		userCardManualService.insertORupdate(ccbiManual);
		
		/**
		 * 记录用户登录数据
		 */
		ManualLogin manualLogin = new ManualLogin();
		manualLogin.setLoginAccount(loginAccount);
		manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
		manualLogin.setUserId(userId);
		manualLogin.setManualId(manualId);
		manualLogin.setType(ManualLogin.TYPE_IDENTITY_CARD);
		manualLogin.setPassword(password);
		ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
		if(oldManualLogin == null){
			manualLoginService.insert(manualLogin);
		}else{
			ManualLogin editmanualLogin = new ManualLogin();
			editmanualLogin.setId(oldManualLogin.getId());
			editmanualLogin.setStatus(1);
			editmanualLogin.setPassword(password);
			editmanualLogin.setGmtModify(now);;
			manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
		}
		
		//删除用户登录记录key
		delUserRecordRedisKey(userId, manualId);
		
	}
	

	@Override
	public String login(Long userId, String loginAccount, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public String save(Long userId, Long manualId, String loginAccount,
			String password, String result,String version) {
		
		return null;
	}
	
	public String checkCode(Long userId,String phoneCode,Long manualId, String version){
		try {
			
			String loginAccountKey = String.format(RedisKey.CCBI_ACCOUNT_KEY, userId);
			String loginAccount = redis.getStringByKey(loginAccountKey);

			String passwordKey = String.format(RedisKey.CCBI_PASSWORD_KEY, userId);
			String password = redis.getStringByKey(passwordKey);

			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", loginAccount);
			
			
			ApiResult<Object> resultJSON = new ApiResult<Object>();
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/ishop/ccb/check/1.0";
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userId.toString());
			paramMap.put("smscode", phoneCode);
			paramMap.put("querytype", "1");
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			if(StringUtils.isBlank(result)){
				//1103, "获取积分失败"
				//0  成功
				return resultJSON.toJSONString(1103, "获取积分失败");
			}
			
			String codeString = JsonResult.getResult(result, "code");
			if("0".equals(codeString)){
				result = JsonResult.getResult(result, "data");
				
				JSONObject reslutJson = JSON.parseObject(result);
				String jf = (String) reslutJson.get("jf");
				String name = (String) reslutJson.get("name");
				Integer integral = Integer.parseInt(jf);
				
				this.saveuser(userId, manualId, loginAccount,password, integral,name, resData, version);
				return resultJSON.toJSONString(ApiResultCode.SUCCEED, "登录成功",resData);
			}else if("1099".equals(codeString)){
				return resultJSON.toJSONString(1099, "短信验证码错误");
			}else{
				return resultJSON.toJSONString(1103, "获取积分失败");
			}

		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("中信银行查询积分失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.CMCC_LOGIN_ERROR,e.getMessage());
		}
		
		
	}
	
	public String login(Long userId, String loginAccount, String password,
			String imageCode,Long manualId, String version) {
		try {
			ApiResult<Object> resultJSON = new ApiResult<Object>();
			boolean errcode = true;

			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", loginAccount);
			
			
			if(StringUtils.isBlank(imageCode)){
				//自动识别验证码
				imageCode = this.checkImg(userId);
				boolean flag = JsonResult.checkResult(imageCode, 0);
				if(flag){
					imageCode = JsonResult.getResult(imageCode, "data");
				}else{
					return imageCode;
				}
			}else{
				errcode = false;
			}
		
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/ishop/ccb/loginjf/1.0";
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userId.toString());
			paramMap.put("account", loginAccount);
			paramMap.put("password", password);
			paramMap.put("vcode", imageCode);
			paramMap.put("querytype", "1");
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			//1097 短信验证码发送成功
			//0 获取积分成功
			
			String code = JsonResult.getResult(result, "code");
			
			if("0".equals(code)){
				result = JsonResult.getResult(result, "data");
				
				JSONObject reslutJson = JSON.parseObject(result);
				String jf = (String) reslutJson.get("jf");
				String name = (String) reslutJson.get("name");
				Integer integral = Integer.parseInt(jf);
				
				this.saveuser(userId, manualId, loginAccount,password, integral,name, resData, version);
				return resultJSON.toJSONString(ApiResultCode.SUCCEED, "登录成功",resData);
			}else if("1097".equals(code)){
				
				String loginAccountKey = String.format(RedisKey.CCBI_ACCOUNT_KEY, userId);
				redis.set(loginAccountKey, loginAccount);

				String passwordKey = String.format(RedisKey.CCBI_PASSWORD_KEY, userId);
				redis.set(passwordKey, password);
				
				return resultJSON.toJSONString(2428,"短信验证码发送成功");
			}else if("1095".equals(code)){
				return resultJSON.toJSONString(1095, "证件号或密码错误");
			}else if("1094".equals(code)){
				if(errcode){
					return resultJSON.toJSONString(1095, "证件号或密码错误");
				}else{
					return resultJSON.toJSONString(1094, "图片验证码错误");
				}
			}else{
				logger.error("中信银行查询积分失败:" +result);
				return resultJSON.toJSONString(2448,"服务器繁忙");
			}
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录中信银行失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.CMCC_LOGIN_ERROR,e.getMessage());
		}
	}
	

	private String checkImg(Long userId){
		ApiResult<String> resultJSON = new ApiResult<String>();
		String reslut = this.getImgCode(userId);
		boolean flag = JsonResult.checkResult(reslut, 0);
		if(flag){
			String imageString = JsonResult.getResult(reslut, "data");
			String image = crackImageCode(imageString);
			if(StringUtils.isBlank(image)){
				imageString = JsonResult.getResult(reslut, "data");
				JSONObject reslutJson = new JSONObject();
				reslutJson.put("code", ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR);
				reslutJson.put("imagecode", imageString);
				reslutJson.put("message", "自动识别图片验证码失败");
				return reslutJson.toJSONString();
			}else{	
				return resultJSON.toJSONString(0, "图片验证码获取成功",image);
			}
		}
		return resultJSON.toJSONString(-1, "图片验证码获取失败");
	}


	@Override
	public String login(Long userId, String loginAccount, String password,
			String imageCode) {
		// TODO Auto-generated method stub
		return null;
	}


}
