package com.caitu99.service.integral.controller.spider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.json.JsonResult;
import com.caitu99.service.utils.string.IdCardValidator;
import com.caitu99.service.utils.string.StrUtil;

@Component
public class ManualQueryCsAir extends ManualQueryAbstract{
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryCsAir.class);

	@Override
	public String getImageCode(Long userid){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/csair/imgcode/1.0";
		super.setUrl(url);
		super.setName("南方航空");
		super.setSucceedCode(ApiResultCode.SUCCEED);
		super.setFailureCode(ApiResultCode.CSAIR_GET_IMAGECODE_ERROR);
		
		return super.getImageCode(userid);
	}
	
	@Override
	public String login(Long userid,String loginAccount,String password,String imageCode){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/spider/csair/login/1.0";
		super.setName("南方航空");
		Map<String,String> param = new HashMap<String,String>();
		param.put("userid", userid.toString());
		param.put("account", loginAccount);
		param.put("password", password);
		param.put("yzm", imageCode);
		
		if(StrUtil.isPhone(loginAccount)){
			param.put("type", "4");
		}else if(StrUtil.isEmail(loginAccount)){
			param.put("type", "5");
		}else if(IdCardValidator.valideIdCard(loginAccount)){
			param.put("type", "2");
		}else if(loginAccount.length()==12&&StringUtils.isNumeric(loginAccount)){
			param.put("type", "1");
		}else{
			param.put("type", "3");
		}
		
		param.put("inCode", "1");
		
		return super.login(userid, url, param, ApiResultCode.SUCCEED);
	}

	@Override
	public String login(Long userId, String loginAccount, String password) {
		return null;
	}

	@Override
	public String save(Long userId, Long manualId, String loginAccount,String password, String result, String version) {
		try {
			boolean flag = JsonResult.checkResult(result,ApiResultCode.SUCCEED);
			if(!flag){
				return result;
			}
			
			JSONObject json = JSON.parseObject(result);
			String jsonString = json.getString("data");
			
			AssertUtil.hasLength(jsonString, "登录南航失败");
			
			JSONObject jsonObject = JSON.parseObject(jsonString);
			
//			{"code":0,"data":"{\"integral\":\"1,255公里\",\"name\":\"曹君跃\",\"card\":\"卡号 230002631681\"}","message":"0"}
			
			Integer integral = jsonObject.getInteger("integral");
			String userName = jsonObject.getString("name");
			String cardNo = jsonObject.getString("card");
			
			Date now = new Date();

			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", loginAccount);
			
			/**
			 * 记录用户积分数据 
			 */
			UserCardManual csAirManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.CSAIR_INTEGRAL,cardNo,null,null);
			
			if(null == csAirManual){
				presentTubiService.presentTubiDo(userId, UserCardManual.CSAIR_INTEGRAL, 0, integral, resData, version);
				
				csAirManual = new UserCardManual();
				csAirManual.setIntegral(integral);
				csAirManual.setUserName(userName);
				csAirManual.setCardNo(cardNo);
				csAirManual.setGmtModify(now);
				csAirManual.setGmtCreate(now);
				csAirManual.setUserId(userId);
				csAirManual.setLoginAccount(loginAccount);
				csAirManual.setCardTypeId(UserCardManual.CSAIR_INTEGRAL);
			}else{
				presentTubiService.presentTubiDo(userId, UserCardManual.CSAIR_INTEGRAL, csAirManual.getIntegral(), integral, resData, version);
				
				csAirManual.setIntegral(integral);
				csAirManual.setUserName(userName);
				csAirManual.setCardNo(cardNo);
				csAirManual.setLoginAccount(loginAccount);
				csAirManual.setGmtModify(now);
				csAirManual.setStatus(1);
			}
			
			userCardManualService.insertORupdate(csAirManual);
			
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
			}else if(StrUtil.isEmail(loginAccount)){
				manualLogin.setType(ManualLogin.TYPE_EMAIL);
			}else if(IdCardValidator.valideIdCard(loginAccount)){
				manualLogin.setType(ManualLogin.TYPE_IDENTITY_CARD);
			}else if(loginAccount.length()==12 && StringUtils.isNumeric(loginAccount)){
				manualLogin.setType(ManualLogin.TYPE_CARD_NO);
			}else{
				manualLogin.setType(ManualLogin.TYPE_LOGIN_ACCOUNT);
			}
			
			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			if(oldManualLogin == null){
				manualLogin.setPassword(password);
				manualLoginService.insert(manualLogin);
			}else{
				oldManualLogin.setPassword(password);
				oldManualLogin.setStatus(1);
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
			logger.error("登录南航失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.CSAIR_LOGIN_ERROR,e.getMessage());
		}
	}
}
