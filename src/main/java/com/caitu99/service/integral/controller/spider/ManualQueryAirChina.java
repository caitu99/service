package com.caitu99.service.integral.controller.spider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.ManualQueryAdaptorException;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.integral.domain.UserCardManualItem;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.date.DateUtil;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.string.StrUtil;

@Component
public class ManualQueryAirChina extends ManualQueryAbstract{
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryAirChina.class);
	
	@Override
	public String getImageCode(Long userid){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/airchina/imgcode/1.0";
		super.setUrl(url);
		super.setName("中国国航");
		super.setSucceedCode(ApiResultCode.SUCCEED);
		super.setFailureCode(ApiResultCode.GET_AIRCHINA_IMAGE_CODE_ERROR);
		
		return super.getImageCode(userid);
	}
	
	@Override
	public String login(Long userid,String loginAccount,String password,String imageCode){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/airchina/login/1.0";
		super.setName("中国国航");
		Map<String,String> param = new HashMap<String,String>();
		param.put("userid", userid.toString());
		param.put("account", loginAccount);
		param.put("password", password);
		param.put("yzm", imageCode);
		
		String type = "NUMBER";
		if(StrUtil.isPhone(loginAccount)){
			//登录名为手机号
			type = "MOBILE";
		}
		param.put("type", type);
		
		return super.login(userid, url, param, ApiResultCode.SUCCEED);
	}

	@Override
	public String login(Long userId, String loginAccount, String password) {
		return null;
	}

	@Override
	public String save(Long userId, Long manualId, String loginAccount,String password, String result,String version) {
		try {
			JSONObject json = JSON.parseObject(result);
//		{
//		    "data": "{\"thisInvalid\":\"0\",\"nextInvalid\":\"0\",\"name\":\"刘存显\",\"available\":\"54736\",\"account\":\"000577370850\"}",
//		    "message": "0"
//		}
			
			String message = json.getString("message");
			if(message==null || !message.equals("0")){
				return result;
			}
			
			String jsonString  = json.getString("data");
			AssertUtil.hasLength(jsonString, "登录成功,但返回数据出错.data为空");
			
			JSONObject jsonObject = JSON.parseObject(jsonString);
			Integer expirationIntegral = jsonObject.getInteger("thisInvalid");		//本月过期里程
			Integer nextExpirationIntegral = jsonObject.getInteger("nextInvalid");		//下月过期里程
			String userName =  jsonObject.getString("name");					//用户名
			Integer integral = jsonObject.getInteger("available");			//可用积分
			String cardNo = jsonObject.getString("account");				//卡号
			
			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", loginAccount);
			
			/**
			 * 记录用户积分数据 
			 */
//			UserCardManual airChinaManual = userCardManualService.getByUserIdCardTypeId(userId,UserCardManual.AIRCHINA_INTEGRAL);
			Long userCardManualId = null;
			//判断此账号是否有数据
			UserCardManual airChinaManualAccount = userCardManualService.getUserCardManualSelective(userId, UserCardManual.AIRCHINA_INTEGRAL, null, null, loginAccount);
			if(airChinaManualAccount != null){
				//计算并赠送财币
				presentTubiService.presentTubiDo(userId, UserCardManual.AIRCHINA_INTEGRAL, airChinaManualAccount.getIntegral(), integral, resData, version);
				
				airChinaManualAccount.updateIntegral(integral,cardNo,expirationIntegral,nextExpirationIntegral,userName,new Date());
				airChinaManualAccount.setLoginAccount(loginAccount);
				airChinaManualAccount.setStatus(1);
				userCardManualService.updateByPrimaryKeySelective(airChinaManualAccount);
				userCardManualId = airChinaManualAccount.getId();
				
			}else{
				UserCardManual airChinaManual = userCardManualService.getUserCardManualSelective(userId, UserCardManual.AIRCHINA_INTEGRAL, cardNo, null, null);
				if(airChinaManual != null){
					//计算并赠送财币
					presentTubiService.presentTubiDo(userId, UserCardManual.AIRCHINA_INTEGRAL, airChinaManual.getIntegral(), integral, resData, version);
					
					airChinaManual.updateIntegral(integral,cardNo,expirationIntegral,nextExpirationIntegral,userName,new Date());
					airChinaManual.setLoginAccount(loginAccount);
					airChinaManual.setStatus(1);
					userCardManualService.updateByPrimaryKeySelective(airChinaManual);
				}else{
					//计算并赠送财币
					presentTubiService.presentTubiDo(userId, UserCardManual.AIRCHINA_INTEGRAL, 0, integral, resData, version);
					
					airChinaManual = new UserCardManual(integral,cardNo,expirationIntegral,nextExpirationIntegral,userName,userId,UserCardManual.AIRCHINA_INTEGRAL);
					airChinaManual.setLoginAccount(loginAccount);
					userCardManualService.insert(airChinaManual);
				}
				userCardManualId = airChinaManual.getId();
			}
			
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
			
			/**
			 * 记录用户登录数据
			 */
			ManualLogin manualLogin = new ManualLogin();
			manualLogin.setLoginAccount(loginAccount);
			manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
//			manualLogin.setPassword(password);
			manualLogin.setUserId(userId);
			manualLogin.setManualId(manualId);
			
			if(StrUtil.isPhone(loginAccount)){
				manualLogin.setType(ManualLogin.TYPE_PHONE);
			}else{
				manualLogin.setType(ManualLogin.TYPE_CARD_NO);
			}

			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			if(oldManualLogin == null){
				manualLogin.setPassword(password);
				manualLoginService.insert(manualLogin);
			}else{
				oldManualLogin.setStatus(1);
				oldManualLogin.setPassword(password);
				manualLoginService.updateByPrimaryKeySelective(oldManualLogin);
			}
			
			//删除用户登录记录key
			delUserRecordRedisKey(userId, manualId);
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", 0);
			resultJson.put("message", "登录成功");
			resultJson.put("data", resData);
			return resultJson.toJSONString();
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录国航失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.AIRCHINA_LOGIN_ERROR,e.getMessage());
		}
	}
}
