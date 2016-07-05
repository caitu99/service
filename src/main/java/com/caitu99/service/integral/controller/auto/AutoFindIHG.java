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
import com.caitu99.service.integral.controller.spider.ManualQueryIHG;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.AutoFindRecord;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.json.JsonResult;

/**
 * IHG自动发现
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AutoFindIHG 
 * @author xiongbin
 * @date 2015年12月15日 下午2:28:17 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Component
public class AutoFindIHG extends AutoFindImageNotAbstract{

	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");
	
	@Autowired
	private ManualQueryIHG manualQueryIHG;
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
		return super.login(manualQueryIHG, userId, loginAccount, password, count, "IHG");
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
	 * @date 2015年12月18日 下午2:57:00  
	 * @author ws
	 */
	public String loginForUpdate(Long userId,String loginAccount,String password){

		if(null == userId || StringUtils.isBlank(loginAccount) || StringUtils.isBlank(password)){
			return null;
		}
		
		String resultJson = super.loginForUpdate(manualQueryIHG, userId, loginAccount, password);
		
		return resultJson;
	}
	
	@Override
	public String checkResult(String jsonString,ManualQueryAbstract manualQuery, 
										Long userId, String loginAccount,String password, Integer count, String log) {
		
		String reslut = super.checkResult(jsonString, manualQuery, userId, loginAccount, password, count, log);
		
		if(StringUtils.isBlank(reslut)){
			JSONObject json = JSON.parseObject(jsonString);
			Integer code = json.getInteger("code");
			
			JSONObject reslutJSON = new JSONObject();
			
			if(code.equals(ApiResultCode.IHG_ACCOUNT_PWD_ERROR)){
				//洲际账号或密码错误
				logger.info("【手动查询自动发现】:" + "userId：" + userId + ",IHG自动发现,尝试登陆失败." + "账号或密码错误,但账号存在");
				
//				reslutJSON.put("code", AutoFindRecord.STATUS_LOGINACCUNT_EXIST);
				reslutJSON.put("code", AutoFindRecord.STATUS_DETELE);
				reslutJSON.put("messsage", "图片验证码不正确");
				reslutJSON.put("error", ApiResultCode.IHG_ACCOUNT_PWD_ERROR);
			}else if(code.equals(ApiResultCode.DATA_LOST)){
				//数据不完整
				logger.info("【手动查询自动发现】:" + "userId：" + userId + ",IHG自动发现,尝试登陆失败." + "数据不完整");
				
				reslutJSON.put("code", AutoFindRecord.STATUS_DETELE);
				reslutJSON.put("messsage", "图片验证码不正确");
				reslutJSON.put("error", ApiResultCode.DATA_LOST);
			}else{
				logger.warn("【手动查询自动发现】:" + "错误信息未捕获到");
				return null;
			}
			
			return reslutJSON.toJSONString();
		}
			
		return reslut;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.integral.controller.auto.abs.AutoFindAbstract#saveResult(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public String saveResult(Long userId, Long manualId,String loginAccount, String password,
			String result ,String version) {
		
		//return ApiResult.outSucceed(ApiResultCode.IHG_ACCOUNT_PWD_ERROR,"用户名或密码错误");
		if(StringUtils.isBlank(result)){
			return "账号信息不全";
		}
		JSONObject json = JSON.parseObject(result);
		Boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
		
		if(!flag){
			Integer code = json.getInteger("code");
			if(ApiResultCode.IHG_ACCOUNT_PWD_ERROR.equals(code)){//账号或密码错误
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
		
		/* {
			"code": 0,
			"data": "{\"jifen\":\"0\"}",
			"message": "获取IHG积分成功"
			}
		 */


		Map<String,Object> addInfo = saveData(userId, json, version);
		addInfo.put("message", SysConstants.SUCCESS);
		
		logger.info(MAIL_INFO_MSG,userId,manualId,loginAccount,"该账户自动更新成功");
		return JSON.toJSONString(addInfo);
	}

	public Map<String,Object> saveData(Long userId, JSONObject json,String version) {
		String data = json.getString("data");
		JSONObject json2 = JSON.parseObject(data);
		Integer jifen = json2.getInteger("jifen");
		String account = json2.getString("account");
		String username = json2.getString("username");
		Date now = new Date();

		Map<String,Object> resData = new HashMap<String, Object>();
		resData.put("userId", String.valueOf(userId));
		
		/**
		 * 记录用户积分数据 
		 */
		UserCardManual iHGManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.IHG_INTEGRAL,null,username,account);
		
		if(null == iHGManual){
			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.IHG_INTEGRAL, 0, jifen, resData, version);
			
			iHGManual = new UserCardManual();
			iHGManual.setIntegral(jifen);
			iHGManual.setUserName(username);
			iHGManual.setGmtModify(now);
			iHGManual.setGmtCreate(now);
			iHGManual.setUserId(userId);
			iHGManual.setLoginAccount(account);
			iHGManual.setCardTypeId(UserCardManual.IHG_INTEGRAL);
			iHGManual.setType(UserCardManual.TYPE_AUTO_FIND_IMPORT);
		}else{
			//积分变更消息推送
			if(null != iHGManual.getCardTypeId() 
					&& null != iHGManual.getIntegral()
					&& null != jifen){
				pushIntegralChangeMessage(userId, iHGManual.getCardTypeId(), jifen, jifen-iHGManual.getIntegral());
			}

			//计算并赠送财币
			presentTubiService.presentTubiDo(userId, UserCardManual.IHG_INTEGRAL, iHGManual.getIntegral(), jifen, resData, version);
			
			iHGManual.setIntegral(jifen);
			iHGManual.setUserName(username);
			iHGManual.setLoginAccount(account);
			iHGManual.setGmtModify(now);
		}
		userCardManualService.insertORupdate(iHGManual);
		
		return resData;
	}
	
	public Integer saveDataAutoFind(Long userId, JSONObject json) {
		String data = json.getString("data");
		JSONObject json2 = JSON.parseObject(data);
		Integer jifen = json2.getInteger("jifen");
		String account = json2.getString("account");
		String username = json2.getString("username");
		Date now = new Date();
		
		/**
		 * 记录用户积分数据 
		 */
		UserCardManual IHGManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.IHG_INTEGRAL,null,username,account);
		
		if(null == IHGManual){
			IHGManual = new UserCardManual();
			IHGManual.setIntegral(jifen);
			IHGManual.setUserName(username);
			IHGManual.setGmtModify(now);
			IHGManual.setGmtCreate(now);
			IHGManual.setUserId(userId);
			IHGManual.setLoginAccount(account);
			IHGManual.setCardTypeId(UserCardManual.IHG_INTEGRAL);
			IHGManual.setType(UserCardManual.TYPE_AUTO_FIND_IMPORT);
			userCardManualService.insert(IHGManual);
		}else{
			logger.info("【手动查询自动发现】:" + "userId:" + userId + ",loginAccount:" + account + ",IHG积分数据已存在.");
		}
		
		CardType cardType = cardTypeService.selectByPrimaryKey(IHGManual.getCardTypeId());
		return cardType.getTypeId();
	}

}