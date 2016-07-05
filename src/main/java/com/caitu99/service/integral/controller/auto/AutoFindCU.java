package com.caitu99.service.integral.controller.auto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.common.errors.RetriableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.constants.SysConstants;
import com.caitu99.service.integral.controller.auto.abs.AutoFindImageAbstract;
import com.caitu99.service.integral.controller.spider.ManualQueryCU;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.AutoFindRecord;
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
public class AutoFindCU extends AutoFindImageAbstract{

	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");
	
	@Autowired
	private ManualQueryCU manualQueryCU;
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
		return super.login(manualQueryCU, userId, loginAccount, password, count, "联通");
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
		
		String resultJson = super.loginForUpdate(manualQueryCU, userId, loginAccount, password);
		
		JSONObject json = JSON.parseObject(resultJson);
		//resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
		if(ApiResultCode.IMAGECODE_ERROR == json.getInteger("code")){//验证码错误，再试一次
			resultJson = super.loginForUpdate(manualQueryCU, userId, loginAccount, password);
		}
		return resultJson;
	}
	
	@Override
	public String checkResult(String jsonString,ManualQueryAbstract manualQuery, 
										Long userId, String loginAccount,String password, Integer count, String log) {
		/*
		Boolean flag = JsonResult.checkResult(jsonString,ApiResultCode.CU_LOGIN_SUCCEED);
		if(flag){
			JSONObject reslut = JSON.parseObject(jsonString);
			reslut.put("code", ApiResultCode.SUCCEED);
			
			return reslut.toJSONString();
		}
		
		JSONObject json = JSON.parseObject(jsonString);
		Integer code = json.getInteger("code");
		
		if(code.equals(ApiResultCode.FOLLOWUP_VERIFY_IMAGE)){
			//验证成功,有图片验证 
			logger.info("【手动查询自动发现】:" + "userId：" + userId + "," + log + "自动发现,尝试登陆,有图片验证 ,获取图片验证码进行登陆 ");

			Object reslut = getImageCode(json,userId,log);
			
			if(reslut instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject)reslut;
				return jsonObject.toString();
			}
			
			String imageCode = (String)reslut;
			
			return vcodeLogin(userId,loginAccount,password,count,imageCode);
		}else if(code.equals(ApiResultCode.IMAGECODE_ERROR)){
			//图片验证码不正确
			if(count < 2){
				logger.info("【手动查询自动发现失败】:" + "userId：" + userId + ",联通自动发现,尝试登陆失败." + "图片验证码不正确,尝试第二次");
				
				Object reslut = getImageCode(json,userId,log);
				
				if(reslut instanceof JSONObject) {
					JSONObject jsonObject = (JSONObject)reslut;
					return jsonObject.toString();
				}
				
				String imageCode = (String)reslut;
				
				return vcodeLogin(userId,loginAccount,password,count++,imageCode);
			}else{
				logger.info("userId：" + userId + "," + log + "自动发现,尝试登陆失败." + "图片验证码不正确,账号密码可能可以登陆");

				JSONObject reslutJSON = new JSONObject();
				reslutJSON.put("code", AutoFindRecord.STATUS_NORMAL);
				reslutJSON.put("messsage", "图片验证码不正确");
				reslutJSON.put("error", ApiResultCode.IMAGECODE_ERROR);
				
				return reslutJSON.toJSONString();
			}
		}
		
		String reslut = super.checkResult(jsonString, manualQuery, userId, loginAccount, password, count, log);
		
		if(StringUtils.isBlank(reslut)){
			logger.warn("错误信息未捕获到");
			return null;
		}
		
		JSONObject reslutJson = JSON.parseObject(reslut);
		if(reslutJson.getInteger("code").equals(AutoFindRecord.STATUS_LOGINACCUNT_EXIST)){
			reslutJson.put("code", AutoFindRecord.STATUS_DETELE);
			return reslutJson.toJSONString();
		}
			
		return reslut;*/
		return null;
	}
	
	/**
	 * 验证码登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: vcodeLogin 
	 * @param userId			用户ID
	 * @param loginAccount		账号
	 * @param password			密码
	 * @param count				图片验证码允许错误次数
	 * @param imageCode			图片验证码
	 * @return
	 * @date 2015年12月16日 上午10:56:15  
	 * @author xiongbin
	 */
	private String vcodeLogin(Long userId,String loginAccount,String password,Integer count,String imageCode){
		/*String jsonString = manualQueryCU.vcodeLogin(userId, loginAccount, password, imageCode);
		
		String reslut = checkResult(jsonString, manualQueryCU, userId, loginAccount, password, count, "联通");
		
		return reslut;*/
		return null;
	}
	
	
	private String vcodeLoginForUpdate(Long userId,String loginAccount,String password,String imageCode){
		/*String jsonString = manualQueryCU.vcodeLogin(userId, loginAccount, password, imageCode);
		
		return jsonString;*/
		return null;
	}
	
	/**
	 * 获取验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCode 
	 * @param json
	 * @param userId
	 * @param log
	 * @return
	 * @date 2015年12月17日 下午5:45:32  
	 * @author xiongbin
	 */
	private Object getImageCode(JSONObject json,Long userId,String log){
		/*JSONObject reslutJSON = new JSONObject();
		
		String jsonStr = json.getString("data");
		if(StringUtils.isBlank(jsonStr)){
			logger.info("【手动查询自动发现失败】:" + "userId：" + userId + "," + log + "自动发现,尝试登陆失败.获取图片验证码失败 ");
			
			reslutJSON.put("code", AutoFindRecord.STATUS_DETELE);
			reslutJSON.put("message", "获取图片验证码失败");
			reslutJSON.put("error", ApiResultCode.CU_GET_IMAGECODE_ERROR);
			return reslutJSON;
		}
		
		JSONObject jsonObject = JSON.parseObject(jsonStr);
		String imageCodeIO = jsonObject.getString("imgstr");
		
		if(StringUtils.isBlank(imageCodeIO)){
			logger.info("【手动查询自动发现失败】:" + "userId：" + userId + "," + log + "自动发现,尝试登陆失败.获取图片验证码失败 ");
			
			reslutJSON.put("code", AutoFindRecord.STATUS_DETELE);
			reslutJSON.put("message", "获取图片验证码失败");
			reslutJSON.put("error", ApiResultCode.CU_GET_IMAGECODE_ERROR);
			return reslutJSON;
		}

		//破解验证码
		String imageCode = crackImageCode(imageCodeIO);
		if(null == imageCode){
			logger.info("【手动查询自动发现失败】:" + "userId：" + userId + "," + log + "自动发现,尝试登陆失败." + "图片验证码破解失败");
			
			reslutJSON.put("code", AutoFindRecord.STATUS_DETELE);
			reslutJSON.put("message", "图片验证码破解失败");
			reslutJSON.put("error", ApiResultCode.IMAGECODE_ERROR);
			return reslutJSON;
		}
		
		return imageCode;*/
		return null;
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
		if(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR.equals(code)
				|| ApiResultCode.PASSWORD_ERROR.equals(code)
				|| ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR.equals(code)){//账号或密码错误
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

	public Map<String,Object> saveData(Long userId,String loginAccount, JSONObject json, String version) {
		String data = json.getString("data");
		
		AssertUtil.hasLength(data, "登录联通失败");
		
		Integer integral = json.getInteger("data");
		String userName = loginAccount;
		
		Date now = new Date();

		Map<String,Object> resData = new HashMap<String, Object>();
		resData.put("userId", String.valueOf(userId));
		
		/**
		 * 记录用户积分数据 
		 */
		UserCardManual cuManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.CU_INTEGRAL,null,null,userName);
		
		if(null == cuManual){
			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.CU_INTEGRAL, 0, integral, resData, version);
			
			cuManual = new UserCardManual();
			cuManual.setIntegral(integral);
			cuManual.setUserName(userName);
			cuManual.setGmtModify(now);
			cuManual.setGmtCreate(now);
			cuManual.setUserId(userId);
			cuManual.setLoginAccount(userName);
			cuManual.setCardTypeId(UserCardManual.CU_INTEGRAL);
			cuManual.setType(UserCardManual.TYPE_AUTO_FIND_IMPORT);
		}else{
			//积分变更消息推送
			if(null != cuManual.getCardTypeId() 
					&& null != cuManual.getIntegral()
					&& null != integral){
				pushIntegralChangeMessage(userId, cuManual.getCardTypeId(), integral, integral-cuManual.getIntegral());
			}

			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.CU_INTEGRAL, cuManual.getIntegral(), integral, resData, version);
			
			cuManual.setIntegral(integral);
			cuManual.setUserName(userName);
			cuManual.setLoginAccount(userName);
			cuManual.setGmtModify(now);
		}
		
		userCardManualService.insertORupdate(cuManual);
		
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
