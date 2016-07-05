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
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.ApiResult;
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
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ManualQueryCCB 
 * @author fangjunxiao
 * @date 2016年3月9日 上午11:22:51 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Component
public class ManualQueryCCB extends ManualQueryAbstract{

    private final static Logger logger = LoggerFactory.getLogger(ManualQueryCCB.class);
	
	@Override
	public String getImageCode(Long userid){
		ApiResult<String> result = new ApiResult<String>();
		String name = "建设银行";
		try {
			
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/shop/ccb/xykimg/1.0";
			
			if(null == userid){
				return result.toJSONString(-1, "参数userId不能为空");
			}
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userId", userid.toString());
			
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

    
	
	private String getImgCode(Long userid){
			ApiResult<String> result = new ApiResult<String>();
			String name = "建设银行";
		try {
			
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/shop/ccb/xykimg/1.0";
			
			if(null == userid){
				return result.toJSONString(-1, "参数userId不能为空");
			}
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userId", userid.toString());
			
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

	
	
	public String login(Long userId,String bankCard,String password,Long manualId, String version) {
		try {
			ApiResult<Object> resultJSON = new ApiResult<Object>();
			
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/shop/ccb/getJf/1.0";
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userId", userId.toString());
			paramMap.put("account", bankCard);
			paramMap.put("password", password);
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			String code = JsonResult.getResult(result, "code");
			
			if("0".equals(code)){
				Map<String,Object> resData = new HashMap<String, Object>();
				resData.put("userId", String.valueOf(userId));
				resData.put("manualId", String.valueOf(manualId));
				resData.put("loginAccount", bankCard);
				
				result = JsonResult.getResult(result, "data");
				
				JSONObject reslutJson = JSON.parseObject(result);
				String jf = (String) reslutJson.get("jf");
				String name = (String) reslutJson.get("name");
				
				String c = "";
				int k = jf.indexOf(".");
				if(k == -1){
					 c = jf;
				}else{
					 c = jf.substring(0, k);
				}
				if(StringUtils.isBlank(c)){
					c = "0";
				}
				
				Integer integral = Integer.parseInt(c);
				
				
				this.saveuser(userId, manualId, bankCard,password,integral,name, resData, version);
				return resultJSON.toJSONString(ApiResultCode.SUCCEED, "登录成功",resData);
			}else if("1004".equals(code)){
				 return resultJSON.toJSONString(1004, "帐号与密码错误");
			}else if("1007".equals(code)){
				 return resultJSON.toJSONString(1007, "登录超时");
			}else if("1011".equals(code)){
				 return resultJSON.toJSONString(1011, "密码错误数次超限");
			}else{
				 return resultJSON.toJSONString(1006, "服务器繁忙");
			}
			
		}catch (Exception e) {
			logger.error("登录建设失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.CMCC_LOGIN_ERROR,"服务器繁忙");
		}
	}


	private void saveuser(Long userId,Long manualId,String loginAccount,String password,Integer integral,String name, Map<String, Object> resData, String version){
		Date now = new Date();
		
         String cardNo = loginAccount.substring(loginAccount.length() - 4,loginAccount.length());

		
		/**
		 * 记录用户积分数据 
		 */
		UserCardManual ccbiManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.CCB_INTEGRAL,null,null,loginAccount);
		
		
		if(null == ccbiManual){
			//赠送途币
			presentTubiService.presentTubiDo(userId, UserCardManual.CCB_INTEGRAL, 0, integral, resData, version);
			
			ccbiManual = new UserCardManual();
			ccbiManual.setIntegral(integral);
			ccbiManual.setUserName(name);
			ccbiManual.setCardNo(cardNo);
			ccbiManual.setGmtModify(now);
			ccbiManual.setGmtCreate(now);
			ccbiManual.setUserId(userId);
			ccbiManual.setLoginAccount(loginAccount);
			ccbiManual.setCardTypeId(UserCardManual.CCB_INTEGRAL);
		}else{
			//赠送途币
			presentTubiService.presentTubiDo(userId, UserCardManual.CCB_INTEGRAL, ccbiManual.getIntegral(), integral, resData, version);
			
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
		manualLogin.setType(ManualLogin.TYPE_CARD_NO);
		ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
		if(oldManualLogin == null){
			manualLogin.setPassword(password);
			manualLoginService.insert(manualLogin);
		}else{
			ManualLogin editmanualLogin = new ManualLogin();
			editmanualLogin.setId(oldManualLogin.getId());
			editmanualLogin.setStatus(1);
			editmanualLogin.setPassword(password);
			editmanualLogin.setGmtModify(now);;
			manualLoginService.updateByPrimaryKeySelective(editmanualLogin);
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
		// TODO Auto-generated method stub
		return null;
	}


	
	@Override
	public String login(Long userId, String bankCard, String password,
			String imgCode) {
		try {
			ApiResult<JSONObject> resultJSON = new ApiResult<JSONObject>();
			
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/shop/ccb/getJf/1.0";
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userId", userId.toString());
			paramMap.put("account", bankCard);
			paramMap.put("password", password);
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			String code = JsonResult.getResult(result, "code");
			
			if("0".equals(code)){
				
				return resultJSON.toJSONString(ApiResultCode.SUCCEED, "登录成功",JSON.parseObject(JsonResult.getResult(result, "data")));
			}else if("1004".equals(code)){
				 return resultJSON.toJSONString(1004, "帐号与密码错误");
			}else if("1007".equals(code)){
				 return resultJSON.toJSONString(1007, "登录超时");
			}else{
				 return resultJSON.toJSONString(1006, "服务器繁忙");
			}
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录建设银行失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.CMCC_LOGIN_ERROR,e.getMessage());
		}
	}

}
