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
import com.caitu99.service.integral.controller.auto.abs.AutoFindImageNotAbstract;
import com.caitu99.service.integral.controller.spider.ManualQueryCCB;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.json.JsonResult;

/**
 * 建设银行自动发现
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AutoFindCMB 
 * @author xiongbin
 * @date 2015年12月16日 下午5:59:29 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Component
public class AutoFindCCB extends AutoFindImageNotAbstract{

	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");
	
	@Autowired
	private ManualQueryCCB manualQueryCCB;
	@Autowired
	private UserCardManualService userCardManualService;
	@Autowired
	CardTypeService cardTypeService;

	private String MAIL_ERR_MSG = "【手动查询自动更新异常】：userId:{},manalId:{},account:{},errMsg:{}";
	private String MAIL_WARN_MSG = "【手动查询自动更新警告】：userId:{},manalId:{},account:{},warnMsg:{}";
	private String MAIL_INFO_MSG = "【手动查询自动更新信息】：userId:{},manalId:{},account:{},infoMsg:{}";
	
	public String login(Long userId,String loginAccount,String password,Integer count){
//		return super.login(manualQueryCCB, userId, loginAccount, password, count, "建设");
		return null;
	}

	@Override
	public String checkResult(String jsonString,ManualQueryAbstract manualQuery, 
										Long userId, String loginAccount,String password, Integer count, String log) {
		/*
		String reslut = super.checkResult(jsonString, manualQuery, userId, loginAccount, password, count, log);
		
		if(StringUtils.isBlank(reslut)){
			logger.warn("【手动查询自动发现失败】:" + "错误信息未捕获到");
			return null;
		}
			
		return reslut;*/
		return null;
	}
	
	/**
	 * 自动更新登录
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginForUpdate 
	 * @param userId
	 * @param loginAccount
	 * @param password
	 * @return
	 * @date 2015年12月18日 下午2:41:41  
	 * @author ws
	 */
	public String loginForUpdate(Long userId,String loginAccount,String password){

		if(null == userId || StringUtils.isBlank(loginAccount) || StringUtils.isBlank(password)){
			return null;
		}
		
		String resultJson = manualQueryCCB.login(userId, loginAccount, password, "");
				//super.loginForUpdate(manualQueryCCB, userId, loginAccount, password);
		
		return resultJson;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.integral.controller.auto.abs.AutoFindAbstract#saveResult(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public String saveResult(Long userId, Long manualId,String loginAccount, String password,
			String result, String version) {
		//return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误");
		if(StringUtils.isBlank(result)){
			return "账号信息不全";
		}
		JSONObject json = JSON.parseObject(result);
		boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
		if(!flag){
			Integer code = json.getInteger("code");
			if(1004 == code.intValue()){//账号或密码错误
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

	private Map<String,Object> saveData(Long userId, String loginAccount, JSONObject result, String version) {
		Date now = new Date();

		Map<String,Object> resData = new HashMap<String, Object>();
		resData.put("userId", String.valueOf(userId));
		
		JSONObject reslutJson = (JSONObject) result.get("data");
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
		
        String cardNo = loginAccount.substring(loginAccount.length() - 4,loginAccount.length());

		
		/**
		 * 记录用户积分数据 
		 */
		UserCardManual ccbiManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.CCB_INTEGRAL,null,null,loginAccount);
		
		
		if(null == ccbiManual){
			//计算并赠送财币
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
			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.CCB_INTEGRAL, ccbiManual.getIntegral(), integral, resData, version);
			
			ccbiManual.setIntegral(integral);
			ccbiManual.setCardNo(cardNo);
			ccbiManual.setUserName(name);
			ccbiManual.setLoginAccount(loginAccount);
			ccbiManual.setGmtModify(now);
			ccbiManual.setStatus(1);
		}
		
		userCardManualService.insertORupdate(ccbiManual);

		return resData;
	}
	
}
