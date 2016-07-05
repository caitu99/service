package com.caitu99.service.integral.controller.auto;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.constants.SysConstants;
import com.caitu99.service.integral.controller.auto.abs.AutoFindImageAbstract;
import com.caitu99.service.integral.controller.spider.ManualQueryJingDong;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.json.JsonResult;

/**
 * 京东自动发现
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AutoFindJingDong 
 * @author xiongbin
 * @date 2015年12月16日 下午5:59:29 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Component
public class AutoFindJingDong extends AutoFindImageAbstract{

	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");
	
	@Autowired
	private ManualQueryJingDong manualQueryJingDong;
	@Autowired
	private ManualLoginService manualLoginService;
	@Autowired
	private UserCardManualService userCardManualService;
	@Autowired
	CardTypeService cardTypeService;

	private String MAIL_ERR_MSG = "【手动查询自动更新异常】：userId:{},manalId:{},account:{},errMsg:{}";
	private String MAIL_WARN_MSG = "【手动查询自动更新警告】：userId:{},manalId:{},account:{},warnMsg:{}";
	private String MAIL_INFO_MSG = "【手动查询自动更新信息】：userId:{},manalId:{},account:{},infoMsg:{}";
	
	public String login(Long userId,String loginAccount,String password,Integer count){
		return super.login(manualQueryJingDong, userId, loginAccount, password, count, "京东");
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
	 * @date 2015年12月18日 下午2:57:47  
	 * @author ws
	 */
	@Deprecated
	public String loginForUpdate(Long userId,String loginAccount,String password){

		if(null == userId || StringUtils.isBlank(loginAccount) || StringUtils.isBlank(password)){
			return null;
		}
		
		String resultJson = super.loginForUpdate(manualQueryJingDong, userId, loginAccount, password);
		
		JSONObject json = JSON.parseObject(resultJson);
		//resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
		if(ApiResultCode.IMAGECODE_ERROR == json.getInteger("code")){//验证码错误，再试一次
			resultJson = super.loginForUpdate(manualQueryJingDong, userId, loginAccount, password);
		}
		return resultJson;
	}

	@Override
	public String checkResult(String jsonString,ManualQueryAbstract manualQuery, 
										Long userId, String loginAccount,String password, Integer count, String log) {
		
		String reslut = super.checkResult(jsonString, manualQuery, userId, loginAccount, password, count, log);
		
		if(StringUtils.isBlank(reslut)){
			logger.warn("【手动查询自动发现】:" + "错误信息未捕获到");
			return null;
		}
			
		return reslut;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.integral.controller.auto.abs.AutoFindAbstract#saveResult(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	@Deprecated
	public String saveResult(Long userId, Long manualId,String loginAccount, String password,
			String result, String version) {
		if(StringUtils.isBlank(result)){
			return "账号信息不全";
		}
		//return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误");
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
		
		savaData(userId, loginAccount, json);

		logger.info(MAIL_INFO_MSG,userId,manualId,loginAccount,"该账户自动更新成功");
		return SysConstants.SUCCESS;
	}

	public void savaData(Long userId, String loginAccount, JSONObject json) {
		String jsonString = json.getString("data");
		JSONObject jsonObject = JSON.parseObject(jsonString);
		
		Integer integral = jsonObject.getInteger("jindou");
		String userName = jsonObject.getString("name");
		Date now = new Date();

		UserCardManual jingDongManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.JINGDONG_INTEGRAL,null,userName,null);
		if(jingDongManual != null){
			//积分变更消息推送
			if(null != jingDongManual.getCardTypeId() 
					&& null != jingDongManual.getIntegral()
					&& null != integral){
				pushIntegralChangeMessage(userId, jingDongManual.getCardTypeId(), integral, integral-jingDongManual.getIntegral());
			}
			
			jingDongManual.setIntegral(integral);
			jingDongManual.setUserName(userName);
			jingDongManual.setGmtModify(now);
			jingDongManual.setLoginAccount(loginAccount);
			
			userCardManualService.updateByPrimaryKeySelective(jingDongManual);
		}else{
			jingDongManual = new UserCardManual(userId, UserCardManual.JINGDONG_INTEGRAL, userName, integral);
			jingDongManual.setLoginAccount(loginAccount);
			jingDongManual.setGmtCreate(now);
			jingDongManual.setGmtModify(now);
			jingDongManual.setType(UserCardManual.TYPE_AUTO_FIND_IMPORT);
			userCardManualService.insert(jingDongManual);
		}
	}

	public Integer saveDataAutoFind(Long userId, String loginAccount, JSONObject json) {
		String jsonString = json.getString("data");
		JSONObject jsonObject = JSON.parseObject(jsonString);
		
		Integer integral = jsonObject.getInteger("jindou");
		String userName = jsonObject.getString("name");
		Date now = new Date();

		UserCardManual jingDongManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.JINGDONG_INTEGRAL,null,userName,null);
		
		if(jingDongManual == null){
			jingDongManual = new UserCardManual(userId, UserCardManual.JINGDONG_INTEGRAL, userName, integral);
			jingDongManual.setLoginAccount(loginAccount);
			jingDongManual.setGmtCreate(now);
			jingDongManual.setGmtModify(now);
			jingDongManual.setType(UserCardManual.TYPE_AUTO_FIND_IMPORT);
			userCardManualService.insert(jingDongManual);
		}else{
			logger.info("【手动查询自动发现】:" + "userId:" + userId + ",loginAccount:" + loginAccount + ",京东积分数据已存在.");
		}
		
		CardType cardType = cardTypeService.selectByPrimaryKey(jingDongManual.getCardTypeId());
		return cardType.getTypeId();
	}

}
