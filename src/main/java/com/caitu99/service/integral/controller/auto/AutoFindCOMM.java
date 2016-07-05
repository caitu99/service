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
import com.caitu99.service.integral.controller.auto.abs.AutoFindImageNotAbstract;
import com.caitu99.service.integral.controller.spider.ManualQueryCOMM;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.json.JsonResult;

/**
 * 联通自动发现
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AutoFindCU 
 * @author xiongbin
 * @date 2015年12月15日 下午2:28:17 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Component
public class AutoFindCOMM extends AutoFindImageNotAbstract{

	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");
	
	@Autowired
	private ManualQueryCOMM manualQueryCOMM;
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
		return super.login(manualQueryCOMM, userId, loginAccount, password, count, "交通");
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
	 * @date 2015年12月18日 下午2:56:54  
	 * @author ws
	 */
	public String loginForUpdate(Long userId,String loginAccount,String password){

		if(null == userId || StringUtils.isBlank(loginAccount) || StringUtils.isBlank(password)){
			return null;
		}
		
		String jsonString = super.loginForUpdate(manualQueryCOMM, userId, loginAccount, password);
		
		Boolean flag = JsonResult.checkResult(jsonString,2111);
		if(flag){
			JSONObject reslut = JSON.parseObject(jsonString);
			reslut.put("code", ApiResultCode.SUCCEED);
			
			return reslut.toJSONString();
		}
		
		return jsonString;
	}
	
	@Override
	public String checkResult(String jsonString,ManualQueryAbstract manualQuery, 
										Long userId, String loginAccount,String password, Integer count, String log) {
		
		Boolean flag = JsonResult.checkResult(jsonString,2111);
		if(flag){
			JSONObject reslut = JSON.parseObject(jsonString);
			reslut.put("code", ApiResultCode.SUCCEED);
			
			return reslut.toJSONString();
		}

        return jsonString;
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
		
		Integer code = json.getInteger("code");
		if(5008 == code.intValue()){//账号或密码错误
			try {
				logger.warn(MAIL_WARN_MSG,userId,manualId,loginAccount,"账号或密码错误");
				pwdErrHandler(userId, manualId, loginAccount, password);
			} catch (Exception e) {
				logger.error(MAIL_ERR_MSG,userId,manualId,loginAccount,e);
			}
			
			return "账号或密码错误";
		}
		if(ApiResultCode.SUCCEED.equals(code)){
			
			Map<String,Object> addInfo = saveData(userId, loginAccount, json, version);
			addInfo.put("message", SysConstants.SUCCESS);
			
    		logger.info(MAIL_INFO_MSG,userId,manualId,loginAccount,"该账户自动更新成功");
    		return JSON.toJSONString(addInfo);
        }else{
        	logger.warn(MAIL_WARN_MSG,userId,manualId,loginAccount,"其他异常");
        	return json.getString("message");
        }

	}

	public Map<String,Object> saveData(Long userId, String loginAccount, JSONObject json, String version) {
        String jsonString = json.getString("data");

        AssertUtil.hasLength(jsonString, "查询交通银行积分失败");

        JSONObject jsonObject = JSON.parseObject(jsonString);

        Integer integral = jsonObject.getInteger("integral");
        String cardNo = jsonObject.getString("account");
        String name = jsonObject.getString("name");
        cardNo = cardNo.substring(cardNo.length() - 4,cardNo.length());

        Date now = new Date();

		Map<String,Object> resData = new HashMap<String, Object>();
		resData.put("userId", String.valueOf(userId));
		
        /**
         * 记录用户积分数据
         */
        UserCardManual COMMManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.COMM_INTEGRAL,cardNo,name,loginAccount);
        if(null == COMMManual){
			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.COMM_INTEGRAL, 0, integral, resData, version);
			
            COMMManual = new UserCardManual();
            COMMManual.setIntegral(integral);
            COMMManual.setUserName(name);
            COMMManual.setCardNo(cardNo);
            COMMManual.setGmtModify(now);
            COMMManual.setGmtCreate(now);
            COMMManual.setUserId(userId);
            COMMManual.setLoginAccount(loginAccount);
            COMMManual.setCardTypeId(UserCardManual.COMM_INTEGRAL);
        }else{
            //积分变更消息推送
			if(null != COMMManual.getCardTypeId() 
					&& null != COMMManual.getIntegral()
					&& null != integral){
				pushIntegralChangeMessage(userId, COMMManual.getCardTypeId(), integral, integral-COMMManual.getIntegral());
			}

			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.COMM_INTEGRAL, COMMManual.getIntegral(), integral, resData, version);
			
            COMMManual.setIntegral(integral);
            COMMManual.setUserName(name);
            COMMManual.setCardNo(cardNo);
            COMMManual.setLoginAccount(loginAccount);
            COMMManual.setGmtModify(now);
            COMMManual.setStatus(1);
        }
        userCardManualService.insertORupdate(COMMManual);
        
        return resData;
	}

	public Integer saveDataAutoFind(Long userId, JSONObject json) {
		String jsonString = json.getString("data");
		JSONObject jsonObject = JSON.parseObject(jsonString);
		
		Integer integral = jsonObject.getInteger("integral");
		String userName = jsonObject.getString("phone");
		
		Date now = new Date();
		
		/**
		 * 记录用户积分数据 
		 */
		UserCardManual csAirManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.CU_INTEGRAL,null,null,userName);
		
		if(null == csAirManual){
			csAirManual = new UserCardManual();
			csAirManual.setIntegral(integral);
			csAirManual.setUserName(userName);
			csAirManual.setGmtModify(now);
			csAirManual.setGmtCreate(now);
			csAirManual.setUserId(userId);
			csAirManual.setLoginAccount(userName);
			csAirManual.setCardTypeId(UserCardManual.CU_INTEGRAL);
			csAirManual.setType(UserCardManual.TYPE_AUTO_FIND_IMPORT);
			userCardManualService.insert(csAirManual);
		}else{
			logger.info("【手动查询自动发现】:" + "userId:" + userId + ",loginAccount:" + userName + ",联通积分数据已存在.");
		}
		CardType cardType = cardTypeService.selectByPrimaryKey(csAirManual.getCardTypeId());
		return cardType.getTypeId();
	}

}
