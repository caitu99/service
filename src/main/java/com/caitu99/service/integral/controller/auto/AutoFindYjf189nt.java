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
import com.caitu99.service.integral.controller.spider.ManualQueryYjf189nt;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.json.JsonResult;

/**
 * 招行自动发现
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AutoFindCMB 
 * @author xiongbin
 * @date 2015年12月16日 下午5:59:29 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Component
public class AutoFindYjf189nt extends AutoFindImageAbstract{

	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");
	
	@Autowired
	private ManualQueryYjf189nt manualQueryYjf189nt;
	@Autowired
	private UserCardManualService userCardManualService;
	@Autowired
	CardTypeService cardTypeService;

	private String MAIL_ERR_MSG = "【手动查询自动更新异常】：userId:{},manalId:{},account:{},errMsg:{}";
	private String MAIL_WARN_MSG = "【手动查询自动更新警告】：userId:{},manalId:{},account:{},warnMsg:{}";
	private String MAIL_INFO_MSG = "【手动查询自动更新信息】：userId:{},manalId:{},account:{},infoMsg:{}";
	
	public String login(Long userId,String loginAccount,String password,Integer count){
//		return super.login(manualQueryYjf189nt, userId, loginAccount, password, count, "天翼");
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
		
		String resultJson = super.loginForUpdate(manualQueryYjf189nt, userId, loginAccount, password);
		
		JSONObject json = JSON.parseObject(resultJson);
		//resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
		if(ApiResultCode.IMAGECODE_ERROR == json.getInteger("code")){//验证码错误，再试一次
			resultJson = super.loginForUpdate(manualQueryYjf189nt, userId, loginAccount, password);
		}
		return resultJson;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.integral.controller.auto.abs.AutoFindAbstract#saveResult(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public String saveResult(Long userId, Long manualId,String loginAccount, String password,
			String result,String version) {
		//return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误");
		if(StringUtils.isBlank(result)){
			return "账号信息不全";
		}
		JSONObject json = JSON.parseObject(result);
		boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
		if(!flag){
			Integer code = json.getInteger("code");
			if(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR.equals(code)){//账号或密码错误
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

	private Map<String,Object> saveData(Long userId, String phone, JSONObject json,String version) {
	
		String jsonString = json.getString("data");
		JSONObject jsonObject = JSON.parseObject(jsonString);
		
		//电信积分
		Integer integral = jsonObject.getInteger("Integral");
		//天翼积分
		Integer voucher = jsonObject.getInteger("Voucher");	
		//用户名
		String userName = jsonObject.getString("custName");
		
		Date now = new Date();

		Map<String,Object> resData = new HashMap<String, Object>();
		resData.put("userId", String.valueOf(userId));
		
		/**
		 * 记录用户积分数据 
		 */
		//电信积分
		UserCardManual integralManual = userCardManualService.getUserCardManualSelective(userId, UserCardManual.CT_INTEGRAL,null,null,phone);
		
		if(integralManual != null){
			//积分变更消息推送
			if(null != integralManual.getCardTypeId() 
					&& null != integralManual.getIntegral()
					&& null != integral){
				pushIntegralChangeMessage(userId, integralManual.getCardTypeId(), integral, integral-integralManual.getIntegral());
			}

			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.CT_INTEGRAL, integralManual.getIntegral(), integral, resData, version);
			
			integralManual.setIntegral(integral);
			integralManual.setGmtModify(now);
			integralManual.setLoginAccount(phone);
			integralManual.setStatus(1);
			userCardManualService.updateByPrimaryKeySelective(integralManual);
		}else{
			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.CT_INTEGRAL, 0, integral, resData, version);
			
			integralManual = new UserCardManual(userId,UserCardManual.CT_INTEGRAL,userName,integral);
			integralManual.setLoginAccount(phone);
			integralManual.setGmtCreate(now);
			integralManual.setGmtModify(now);
			userCardManualService.insert(integralManual);
		}
		
		//天翼积分
		UserCardManual voucherManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.ESURFING_INTEGRAL,null,null,phone);
		
		if(voucherManual != null){
			voucherManual.setIntegral(voucher);
			voucherManual.setGmtModify(now);
			voucherManual.setLoginAccount(phone);
			voucherManual.setStatus(1);
			userCardManualService.updateByPrimaryKeySelective(voucherManual);

			//积分变更消息推送
			if(null != voucherManual.getCardTypeId() 
					&& null != voucherManual.getIntegral()
					&& null != integral){
				pushIntegralChangeMessage(userId, voucherManual.getCardTypeId(), voucher, voucher-voucherManual.getIntegral());
			}
		}else{
			voucherManual = new UserCardManual(userId,UserCardManual.ESURFING_INTEGRAL,userName,voucher);
			voucherManual.setLoginAccount(phone);
			voucherManual.setGmtCreate(now);
			voucherManual.setGmtModify(now);
			userCardManualService.insert(voucherManual);
		}
		
		return resData;
	}
	
}
