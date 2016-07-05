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
import com.caitu99.service.integral.controller.spider.ManualQueryAirChina;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.domain.UserCardManualItem;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.integral.service.UserCardManualItemService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.date.DateUtil;

/**
 * 国航自动发现
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AutoFindAirChina 
 * @author xiongbin
 * @date 2015年12月16日 下午6:00:03 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Component
public class AutoFindAirChina extends AutoFindImageAbstract{

	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");
	
	@Autowired
	private ManualQueryAirChina manualQueryAirChina;
	@Autowired
	private ManualLoginService manualLoginService;
	@Autowired
	private UserCardManualService userCardManualService;
	@Autowired
	private CardTypeService cardTypeService;
	@Autowired
	protected UserCardManualItemService userCardManualItemService;
	
	private String MAIL_ERR_MSG = "【手动查询自动更新异常】：userId:{},manalId:{},account:{},errMsg:{}";
	private String MAIL_WARN_MSG = "【手动查询自动更新警告】：userId:{},manalId:{},account:{},warnMsg:{}";
	private String MAIL_INFO_MSG = "【手动查询自动更新信息】：userId:{},manalId:{},account:{},infoMsg:{}";
	
	public String login(Long userId,String loginAccount,String password,Integer count){
		return super.login(manualQueryAirChina, userId, loginAccount, password, count, "国航");
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
	 * @date 2015年12月18日 下午2:56:38  
	 * @author ws
	 */
	public String loginForUpdate(Long userId,String loginAccount,String password){
		
		if(null == userId || StringUtils.isBlank(loginAccount) || StringUtils.isBlank(password)){
			return null;
		}
		
		String resultJson = super.loginForUpdate(manualQueryAirChina, userId, loginAccount, password);
		
		JSONObject json = JSON.parseObject(resultJson);
		//resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
		if(ApiResultCode.IMAGECODE_ERROR == json.getInteger("code")){//验证码错误，再试一次
			resultJson = super.loginForUpdate(manualQueryAirChina, userId, loginAccount, password);
		}
		return resultJson;
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

	/* (non-Javadoc)
	 * @see com.caitu99.service.integral.controller.auto.abs.AutoFindAbstract#saveResult(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public String saveResult(Long userId, Long manualId,String loginAccount, String password,
			String result, String version) {

		if(StringUtils.isBlank(result)){
			return "账号信息不全";
		}
		
		//ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误 ");

		JSONObject json = JSON.parseObject(result);
		
		Integer code = json.getInteger("code");
		if(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR.equals(code)){//账号或密码错误
			try {
				super.pwdErrHandler(userId, manualId, loginAccount, password);
				logger.warn(MAIL_WARN_MSG,userId,manualId,loginAccount,"账号或密码错误");
			} catch (Exception e) {
				logger.error(MAIL_ERR_MSG,userId,manualId,loginAccount,e);
			}
			
			return "账号或密码错误";
		}
		
		String message = json.getString("message");
		if(message==null || !message.equals("0")){//失败
			//未更新成功
			logger.warn(MAIL_WARN_MSG,userId,manualId,loginAccount,"该账户自动更新失败");
			return json.getString("message");
		}
		
		Map<String,Object> addInfo = saveData(userId, loginAccount, json, version);
		addInfo.put("message", SysConstants.SUCCESS);
		
		logger.info(MAIL_INFO_MSG,userId,manualId,loginAccount,"该账户自动更新成功");

		return JSON.toJSONString(addInfo);
	}

	public Map<String,Object> saveData(Long userId, String loginAccount, JSONObject json, String version) {
		String jsonString  = json.getString("data");
		
		JSONObject jsonObject = JSON.parseObject(jsonString);
		Integer expirationIntegral = jsonObject.getInteger("thisInvalid");		//本月过期里程
		Integer nextExpirationIntegral = jsonObject.getInteger("nextInvalid");		//下月过期里程
		String userName =  jsonObject.getString("name");					//用户名
		Integer integral = jsonObject.getInteger("available");			//可用积分
		String cardNo = jsonObject.getString("account");				//卡号
		
		/**
		 * 记录用户积分数据 
		 */
//			UserCardManual airChinaManual = userCardManualService.getByUserIdCardTypeId(userId,UserCardManual.AIRCHINA_INTEGRAL);
		Date now = new Date();
		
		Map<String,Object> resData = new HashMap<String, Object>();
		resData.put("userId", String.valueOf(userId));
		
		UserCardManual airChinaManual = userCardManualService.getUserCardManualSelective(userId, UserCardManual.AIRCHINA_INTEGRAL, cardNo, null, null);
		if(airChinaManual != null){
			if(null != airChinaManual.getCardTypeId() 
					&& null != airChinaManual.getIntegral()
					&& null != integral){
				pushIntegralChangeMessage(userId, airChinaManual.getCardTypeId(), integral, integral-airChinaManual.getIntegral());
			}
			

			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.AIRCHINA_INTEGRAL, airChinaManual.getIntegral(), integral, resData, version);
			
			
			airChinaManual.updateIntegral(integral,cardNo,expirationIntegral,nextExpirationIntegral,userName,new Date());
			airChinaManual.setLoginAccount(loginAccount);
			userCardManualService.updateByPrimaryKeySelective(airChinaManual);
		}else{
			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.AIRCHINA_INTEGRAL, 0, integral, resData, version);
			
			airChinaManual = new UserCardManual(integral,cardNo,expirationIntegral,nextExpirationIntegral,userName,userId,UserCardManual.AIRCHINA_INTEGRAL);
			airChinaManual.setLoginAccount(loginAccount);
			airChinaManual.setGmtCreate(now);
			airChinaManual.setGmtModify(now);
			airChinaManual.setType(UserCardManual.TYPE_AUTO_FIND_IMPORT);
			userCardManualService.insert(airChinaManual);
		}
		
		Long userCardManualId = airChinaManual.getId();
		//过期积分,保存到子表
		if(null != userCardManualId){
			if(expirationIntegral > 0){
				userCardManualItemService.deleteByUserCardManualId(userCardManualId);
				UserCardManualItem userCardManualItem = new UserCardManualItem();;
				userCardManualItem.setExpirationIntegral(expirationIntegral);
				userCardManualItem.setExpirationTime(DateUtil.nextMonthFirstDate());
				userCardManualItem.setUserCardManualId(userCardManualId);
				userCardManualItemService.insert(userCardManualItem);
			}
			if(nextExpirationIntegral > 0){
				UserCardManualItem userCardManualItemNext = new UserCardManualItem();;
				userCardManualItemNext.setExpirationIntegral(nextExpirationIntegral);
				userCardManualItemNext.setExpirationTime(DateUtil.nextMonthDate(2));
				userCardManualItemNext.setUserCardManualId(userCardManualId);
				userCardManualItemService.insert(userCardManualItemNext);
			}
		}
		return resData;
	}
	
	public Integer saveDataAutoFind(Long userId, String loginAccount, JSONObject json) {
		String jsonString  = json.getString("data");
		
		JSONObject jsonObject = JSON.parseObject(jsonString);
		Integer expirationIntegral = jsonObject.getInteger("thisInvalid");		//本月过期里程
		Integer nextExpirationIntegral = jsonObject.getInteger("nextInvalid");		//下月过期里程
		String userName =  jsonObject.getString("name");					//用户名
		Integer integral = jsonObject.getInteger("available");			//可用积分
		String cardNo = jsonObject.getString("account");				//卡号
		
		/**
		 * 记录用户积分数据 
		 */
		Date now = new Date();
		UserCardManual airChinaManual = userCardManualService.getUserCardManualSelective(userId, UserCardManual.AIRCHINA_INTEGRAL, cardNo, null, null);
		if(airChinaManual == null){
			airChinaManual = new UserCardManual(integral,cardNo,expirationIntegral,nextExpirationIntegral,userName,userId,UserCardManual.AIRCHINA_INTEGRAL);
			airChinaManual.setLoginAccount(loginAccount);
			airChinaManual.setGmtCreate(now);
			airChinaManual.setGmtModify(now);
			airChinaManual.setType(UserCardManual.TYPE_AUTO_FIND_IMPORT);
			userCardManualService.insert(airChinaManual);
			
			Long userCardManualId = airChinaManual.getId();
			//过期积分,保存到子表
			if(null != userCardManualId){
				if(expirationIntegral > 0){
					userCardManualItemService.deleteByUserCardManualId(userCardManualId);
					UserCardManualItem userCardManualItem = new UserCardManualItem();;
					userCardManualItem.setExpirationIntegral(expirationIntegral);
					userCardManualItem.setExpirationTime(DateUtil.nextMonthFirstDate());
					userCardManualItem.setUserCardManualId(userCardManualId);
					userCardManualItemService.insert(userCardManualItem);
				}
				if(nextExpirationIntegral > 0){
					UserCardManualItem userCardManualItemNext = new UserCardManualItem();;
					userCardManualItemNext.setExpirationIntegral(nextExpirationIntegral);
					userCardManualItemNext.setExpirationTime(DateUtil.nextMonthDate(2));
					userCardManualItemNext.setUserCardManualId(userCardManualId);
					userCardManualItemService.insert(userCardManualItemNext);
				}
			}
		}else{
			logger.info("【手动查询自动发现】:" + "userId:" + userId + ",loginAccount:" + loginAccount + ",国航积分数据已存在.");
		}
		
		CardType cardType = cardTypeService.selectByPrimaryKey(airChinaManual.getCardTypeId());
		return cardType.getTypeId();
	}

}
