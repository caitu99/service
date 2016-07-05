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
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.json.JsonResult;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ManualQueryPingAnBank 
 * @author fangjunxiao
 * @date 2016年4月13日 下午12:28:33 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Component
public class ManualQueryPingAnBank extends ManualQueryAbstract{
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryPuFaBank.class);

	@Override
	public String getImageCode(Long userid){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/xyk/pn/getimgcode/1.0";
		super.setUrl(url);
		super.setName("平安银行");
		super.setSucceedCode(ApiResultCode.NEED_INOUT_IMAGECODE);
		super.setFailureCode(ApiResultCode.BOTAOHUI_GET_IMAGECODE_ERROR);
		
		return super.getImageCode(userid);
	}
	
	@Override
	public String login(Long userId, String loginAccount, String password,String imageCode) {
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/xyk/pn/login/1.0";
		super.setName("平安银行");
		Map<String,String> param = new HashMap<String,String>();
		param.put("userid", userId.toString());
		param.put("account", loginAccount);
		param.put("password", password);
		param.put("imgcode", imageCode);

		return super.login(userId, url, param, ApiResultCode.SUCCEED);
	}

	@Override
	public String login(Long userId, String loginAccount, String password) {
		return null;
	}

	@Override
	public String save(Long userId, Long manualId, String loginAccount,String password, String result, String version) {
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			result = JsonResult.getResult(result, "data");
			
			JSONObject jsonObject = JSON.parseObject(result);
			
			String jf = jsonObject.getString("jf");
			String userName = jsonObject.getString("name");
			String cardNo = jsonObject.getString("cardNo");

		    cardNo = cardNo.substring(cardNo.length() - 4,cardNo.length());

			
			Date now = new Date();

			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", loginAccount);
			
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
			/**
			 * 记录用户积分数据 
			 */
			UserCardManual puFaBankManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.PINGAN_INTEGRAL,cardNo,userName,loginAccount);
			
			if(null == puFaBankManual){
				presentTubiService.presentTubiDo(userId, UserCardManual.PINGAN_INTEGRAL, 0, integral, resData, version);
				
				puFaBankManual = new UserCardManual();
				puFaBankManual.setIntegral(integral);
				puFaBankManual.setUserName(userName);
				puFaBankManual.setGmtModify(now);
				puFaBankManual.setGmtCreate(now);
				puFaBankManual.setUserId(userId);
				puFaBankManual.setLoginAccount(loginAccount);
				puFaBankManual.setCardNo(cardNo);
				puFaBankManual.setCardTypeId(UserCardManual.PINGAN_INTEGRAL);
			}else{
				presentTubiService.presentTubiDo(userId, UserCardManual.PINGAN_INTEGRAL, puFaBankManual.getIntegral(), integral, resData, version);
				
				puFaBankManual.setIntegral(integral);
				puFaBankManual.setUserName(userName);
				puFaBankManual.setLoginAccount(loginAccount);
				puFaBankManual.setGmtModify(now);
				puFaBankManual.setStatus(1);
				puFaBankManual.setCardNo(cardNo);
			}
			
			userCardManualService.insertORupdate(puFaBankManual);
			
			/**
			 * 记录用户登录数据
			 */
			ManualLogin manualLogin = new ManualLogin();
			manualLogin.setLoginAccount(loginAccount);
			manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
			manualLogin.setUserId(userId);
			manualLogin.setManualId(manualId);
			manualLogin.setType(ManualLogin.TYPE_IDENTITY_CARD);
			
			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			if(oldManualLogin == null){
				manualLogin.setPassword(password);
				manualLogin.setStatus(1);
				manualLoginService.insert(manualLogin);
			}else{
				ManualLogin editlogin = new ManualLogin();
				editlogin.setId(oldManualLogin.getId());
				editlogin.setPassword(password);
				editlogin.setStatus(1);
				editlogin.setGmtModify(now);
				manualLoginService.updateByPrimaryKeySelective(editlogin);
			}
			
			//删除用户登录记录key
			delUserRecordRedisKey(userId, manualId);
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", ApiResultCode.SUCCEED);
			resultJson.put("message", "登录成功");
			resultJson.put("data", resData);
			
			return resultJson.toJSONString();
			
		}
		return result;
	}
	
	
	
	
}
