/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.auto;

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
import com.caitu99.service.constants.SysConstants;
import com.caitu99.service.integral.controller.auto.abs.AutoFindImageAbstract;
import com.caitu99.service.integral.controller.spider.ManualQueryPingAnBank;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.json.JsonResult;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AutoFindPingAn 
 * @author fangjunxiao
 * @date 2016年4月14日 上午9:57:32 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Component
public class AutoFindPingAn extends AutoFindImageAbstract{


	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");
	
	
	@Autowired
	private ManualQueryPingAnBank manualQueryPingAnBank;
	
	@Autowired
	private UserCardManualService userCardManualService;
	
	private String MAIL_ERR_MSG = "【手动查询自动更新异常】：userId:{},manalId:{},account:{},errMsg:{}";
	private String MAIL_WARN_MSG = "【手动查询自动更新警告】：userId:{},manalId:{},account:{},warnMsg:{}";
	private String MAIL_INFO_MSG = "【手动查询自动更新信息】：userId:{},manalId:{},account:{},infoMsg:{}";
	
	@Override
	public String saveResult(Long userId, Long manualId, String loginAccount,
			String password, String result,String version) {
		if(StringUtils.isBlank(result)){
			return "账号信息不全";
		}
		JSONObject json = JSON.parseObject(result);
		boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
		if(!flag){
			Integer code = json.getInteger("code");
			if(2451 == code.intValue()){//账号或密码错误
				try {
					pwdErrHandler(userId, manualId, loginAccount, password);
					logger.warn(MAIL_WARN_MSG,userId,manualId,loginAccount,"账号或密码错误");
				} catch (Exception e) {
					logger.error(MAIL_ERR_MSG,userId,manualId,loginAccount,e);
				}
				
				return "账号或密码错误";
			}else{
				//未更新成功
				logger.warn(MAIL_WARN_MSG,userId,manualId,loginAccount,"该账户自动更新失败");
				return json.getString("message");
			}
		}

		Map<String,Object> addInfo = saveData(userId, loginAccount, json, version);
		addInfo.put("message", SysConstants.SUCCESS);
		
		logger.info(MAIL_INFO_MSG,userId,manualId,loginAccount,"该账户自动更新成功");
		return JSON.toJSONString(addInfo);
	}
	
	public String loginForUpdate(Long userId, String loginAccount, String password){
		if(null == userId || StringUtils.isBlank(loginAccount) || StringUtils.isBlank(password)){
			return null;
		}
		
		String resultJson = super.loginForUpdate(manualQueryPingAnBank, userId, loginAccount, password);
		
		JSONObject json = JSON.parseObject(resultJson);
		//resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
		if(ApiResultCode.IMAGECODE_ERROR == json.getInteger("code")){//验证码错误，再试一次
			resultJson = super.loginForUpdate(manualQueryPingAnBank, userId, loginAccount, password);
		}
		return resultJson;
	}
	
	private Map<String,Object> saveData(Long userId, String loginAccount, JSONObject result,String version) {
		Date now = new Date();

		Map<String,Object> resData = new HashMap<String, Object>();
		resData.put("userId", String.valueOf(userId));
		
		String jsonString = result.getString("data");
		JSONObject jsonObject = JSON.parseObject(jsonString);
		
		
		String jf = jsonObject.getString("jf");
		String userName = jsonObject.getString("name");
		String cardNo = jsonObject.getString("cardNo");
		
	    cardNo = cardNo.substring(cardNo.length() - 4,cardNo.length());

		
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

		UserCardManual pingAnManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.PINGAN_INTEGRAL,cardNo,null,null);
		if(pingAnManual == null){
			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.PINGAN_INTEGRAL, 0, integral, resData, version);
			
			pingAnManual = new UserCardManual(userId, UserCardManual.PINGAN_INTEGRAL, userName, integral);
			pingAnManual.setLoginAccount(loginAccount);
			pingAnManual.setGmtCreate(now);
			pingAnManual.setGmtModify(now);
			pingAnManual.setCardNo(cardNo);
			pingAnManual.setType(UserCardManual.TYPE_AUTO_FIND_IMPORT);
			userCardManualService.insert(pingAnManual);
		}else{
			//积分变更消息推送
			if(null != pingAnManual.getCardTypeId() 
					&& null != pingAnManual.getIntegral()
					&& null != integral){
				pushIntegralChangeMessage(userId, pingAnManual.getCardTypeId(), integral, integral-pingAnManual.getIntegral());
			}

			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.PINGAN_INTEGRAL, pingAnManual.getIntegral(), integral, resData, version);
			
			pingAnManual.setIntegral(integral);
			pingAnManual.setUserName(userName);
			pingAnManual.setCardNo(cardNo);
			pingAnManual.setGmtModify(now);
			pingAnManual.setLoginAccount(loginAccount);
			
			userCardManualService.updateByPrimaryKeySelective(pingAnManual);
		}
		
		return resData;
		
	}
	

}
