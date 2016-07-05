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
import com.caitu99.service.integral.controller.spider.ManualQueryCMCC;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.json.JsonResult;

/**
 * 移动自动发现
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AutoFindCMCC 
 * @author xiongbin
 * @date 2015年12月15日 下午2:28:17 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Component
public class AutoFindCMCC extends AutoFindImageAbstract{
	
	private final static Logger logger = LoggerFactory.getLogger(AutoFindCMCC.class);
	
	@Autowired
	private ManualQueryCMCC manualQueryCMCC;

	@Autowired
	private UserCardManualService userCardManualService;
	@Autowired
	CardTypeService cardTypeService;

	private String MAIL_ERR_MSG = "【手动查询自动更新异常】：userId:{},manalId:{},account:{},errMsg:{}";
	private String MAIL_WARN_MSG = "【手动查询自动更新警告】：userId:{},manalId:{},account:{},warnMsg:{}";
	private String MAIL_INFO_MSG = "【手动查询自动更新信息】：userId:{},manalId:{},account:{},infoMsg:{}";
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.integral.controller.auto.abs.AutoFindAbstract#login(com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract, java.lang.Long, java.lang.String, java.lang.String, java.lang.Integer, java.lang.String)
	 */
	@Override
	public String login(ManualQueryAbstract manualQuery, Long userId,
			String loginAccount, String password, Integer count, String log) {
		if(null == userId || StringUtils.isBlank(loginAccount) || StringUtils.isBlank(password)){
			return null;
		}
		
		String resultJson = super.loginForUpdate(manualQuery, userId, loginAccount, password);
		
		JSONObject json = JSON.parseObject(resultJson);
		//resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
		if(ApiResultCode.IMAGECODE_ERROR == json.getInteger("code")){//验证码错误，再试一次
			resultJson = super.loginForUpdate(manualQuery, userId, loginAccount, password);
		}
		return resultJson;
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
		
		String resultJson = super.loginForUpdate(manualQueryCMCC, userId, loginAccount, password);
		
		JSONObject json = JSON.parseObject(resultJson);
		//resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
		if(1052 == json.getInteger("code") || 2460 == json.getInteger("code")){//验证码错误，再试一次
			resultJson = super.loginForUpdate(manualQueryCMCC, userId, loginAccount, password);
		}
		return resultJson;
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
		JSONObject json = JSON.parseObject(result);
		boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
		if(!flag){
			Integer code = json.getInteger("code");
			if(2112 == code.intValue()){//账号或密码错误
				try {
					pwdErrHandler(userId, manualId, loginAccount, password);
					logger.warn(MAIL_WARN_MSG,userId,manualId,loginAccount,"账号或密码错误");
				} catch (Exception e) {
					logger.warn(MAIL_ERR_MSG,userId,manualId,loginAccount,e);
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
		return SysConstants.SUCCESS;
	}
	
	private Map<String,Object> saveData(Long userId, String phone, JSONObject json, String version) {
		
        String jsonString = json.getString("data");

        AssertUtil.hasLength(jsonString, "登录移动失败");

        JSONObject jsonObject = JSON.parseObject(jsonString);

        Integer integral = jsonObject.getInteger("integral");
        String userName = jsonObject.getString("account");

        Date now = new Date();

		Map<String,Object> resData = new HashMap<String, Object>();
		resData.put("userId", String.valueOf(userId));
		
        /**
         * 记录用户积分数据
         */
        UserCardManual csAirManual = userCardManualService.getUserCardManualSelective(userId, UserCardManual.CMCC_INTEGRAL, null, null, userName);

        if (null == csAirManual) {
        	//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.CMCC_INTEGRAL, 0, integral, resData, version);
			
            csAirManual = new UserCardManual();
            csAirManual.setIntegral(integral);
            csAirManual.setUserName(userName);
            csAirManual.setGmtModify(now);
            csAirManual.setGmtCreate(now);
            csAirManual.setUserId(userId);
            csAirManual.setLoginAccount(userName);
            csAirManual.setCardTypeId(UserCardManual.CMCC_INTEGRAL);
        } else {
			//积分变更消息推送
			if(null != csAirManual.getCardTypeId() 
					&& null != csAirManual.getIntegral()
					&& null != integral){
				pushIntegralChangeMessage(userId, csAirManual.getCardTypeId(), integral, integral-csAirManual.getIntegral());
			}

        	//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.CMCC_INTEGRAL, csAirManual.getIntegral(), integral, resData, version);
			
            csAirManual.setIntegral(integral);
            csAirManual.setUserName(userName);
            csAirManual.setLoginAccount(userName);
            csAirManual.setGmtModify(now);
            csAirManual.setStatus(1);
        }

        userCardManualService.insertORupdate(csAirManual);
        
        return resData;
	}

	
}
