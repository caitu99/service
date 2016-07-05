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
import com.caitu99.service.integral.controller.spider.ManualQueryCMB;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.CardType;
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
public class AutoFindCMB extends AutoFindImageAbstract{

	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");
	
	@Autowired
	private ManualQueryCMB manualQueryCMB;
	@Autowired
	private UserCardManualService userCardManualService;
	@Autowired
	CardTypeService cardTypeService;

	private String MAIL_ERR_MSG = "【手动查询自动更新异常】：userId:{},manalId:{},account:{},errMsg:{}";
	private String MAIL_WARN_MSG = "【手动查询自动更新警告】：userId:{},manalId:{},account:{},warnMsg:{}";
	private String MAIL_INFO_MSG = "【手动查询自动更新信息】：userId:{},manalId:{},account:{},infoMsg:{}";
	
	public String login(Long userId,String loginAccount,String password,Integer count){
		return super.login(manualQueryCMB, userId, loginAccount, password, count, "招行");
	}

	@Override
	public String checkResult(String jsonString,ManualQueryAbstract manualQuery, 
										Long userId, String loginAccount,String password, Integer count, String log) {
		
		String reslut = super.checkResult(jsonString, manualQuery, userId, loginAccount, password, count, log);
		
		if(StringUtils.isBlank(reslut)){
			logger.warn("【手动查询自动发现失败】:" + "错误信息未捕获到");
			return null;
		}
			
		return reslut;
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
		
		String resultJson = super.loginForUpdate(manualQueryCMB, userId, loginAccount, password);
		
		JSONObject json = JSON.parseObject(resultJson);
		//resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
		if(ApiResultCode.IMAGECODE_ERROR == json.getInteger("code")){//验证码错误，再试一次
			resultJson = super.loginForUpdate(manualQueryCMB, userId, loginAccount, password);
		}
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

	public Map<String,Object> saveData(Long userId, String loginAccount, JSONObject json,String version) {
		String jsonString = json.getString("data");
		JSONObject jsonObject = JSON.parseObject(jsonString);
		
		Integer integral = jsonObject.getInteger("integral");
		String userName = jsonObject.getString("name");
		String cardNo = jsonObject.getString("account");
		
		Date now = new Date();
		
		Map<String,Object> resData = new HashMap<String, Object>();
		resData.put("userId", String.valueOf(userId));
		
		/**
		 * 记录用户积分数据 
		 */
//		UserCardManual CMBManual = userCardManualService.getByUserIdCardTypeId(userId,UserCardManual.CMB);
		UserCardManual cMBManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.CMB,null,userName,loginAccount);
		
		if(null == cMBManual){
			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.CMB, 0, integral, resData, version);
			
			cMBManual = new UserCardManual();
			cMBManual.setIntegral(integral);
			cMBManual.setUserName(userName);
			cMBManual.setCardNo(cardNo);
			cMBManual.setGmtModify(now);
			cMBManual.setGmtCreate(now);
			cMBManual.setUserId(userId);
			cMBManual.setLoginAccount(loginAccount);
			cMBManual.setCardTypeId(UserCardManual.CMB);
			cMBManual.setType(UserCardManual.TYPE_AUTO_FIND_IMPORT);
		}else{
			//积分变更消息推送
			if(null != cMBManual.getCardTypeId() 
					&& null != cMBManual.getIntegral()
					&& null != integral){
				pushIntegralChangeMessage(userId, cMBManual.getCardTypeId(), integral, integral-cMBManual.getIntegral());
			}

			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.CMB, cMBManual.getIntegral(), integral, resData, version);
			
			cMBManual.setIntegral(integral);
			cMBManual.setUserName(userName);
			cMBManual.setCardNo(cardNo);
			cMBManual.setLoginAccount(loginAccount);
			cMBManual.setGmtModify(now);
		}
		
		userCardManualService.insertORupdate(cMBManual);
		
		return resData;
		
	}
	
	public Integer saveDataAutoFind(Long userId, String loginAccount,JSONObject json) {
		String jsonString = json.getString("data");
		JSONObject jsonObject = JSON.parseObject(jsonString);
		
		Integer integral = jsonObject.getInteger("integral");
		String userName = jsonObject.getString("name");
		String cardNo = jsonObject.getString("account");
		
		Date now = new Date();
		
		/**
		 * 记录用户积分数据 
		 */
//		UserCardManual CMBManual = userCardManualService.getByUserIdCardTypeId(userId,UserCardManual.CMB);
		UserCardManual CMBManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.CMB,null,userName,loginAccount);
		
		if(null == CMBManual){
			CMBManual = new UserCardManual();
			CMBManual.setIntegral(integral);
			CMBManual.setUserName(userName);
			CMBManual.setCardNo(cardNo);
			CMBManual.setGmtModify(now);
			CMBManual.setGmtCreate(now);
			CMBManual.setUserId(userId);
			CMBManual.setLoginAccount(loginAccount);
			CMBManual.setCardTypeId(UserCardManual.CMB);
			CMBManual.setType(UserCardManual.TYPE_AUTO_FIND_IMPORT);
			
			userCardManualService.insert(CMBManual);
		}else{
			logger.info("【手动查询自动发现】:" + "userId:" + userId + ",loginAccount:" + loginAccount + ",招行积分数据已存在.");
		}
		
		CardType cardType = cardTypeService.selectByPrimaryKey(CMBManual.getCardTypeId());
		return cardType.getTypeId();
	}

}
