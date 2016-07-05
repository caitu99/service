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
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.json.JsonResult;
import com.caitu99.service.utils.string.StrUtil;

@Component
public class ManualQueryJingDong extends ManualQueryAbstract{
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryJingDong.class);
	
	@Override
	public String getImageCode(Long userid){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/jingdong/loginpage/1.0";
		super.setUrl(url);
		super.setName("京东");
		super.setSucceedCode(ApiResultCode.NEED_INOUT_IMAGECODE);
		super.setFailureCode(ApiResultCode.JINGDONG_GET_IMAGECODE_ERROR);
		
		return super.getImageCode(userid);
	}
	
	@Override
	public String login(Long userid,String loginAccount,String password,String imageCode){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/api/jingdong/login/1.0";
		super.setName("京东");
		Map<String,String> param = new HashMap<String,String>();
		param.put("userid", userid.toString());
		param.put("account", loginAccount);
		param.put("password", password);
		param.put("vcode", imageCode);
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
			
			AssertUtil.hasLength(jsonString, "获取用户京东数据失败");
			
			JSONObject jsonObject = JSON.parseObject(jsonString);
			
			Integer integral = jsonObject.getInteger("jindou");
			String userName = jsonObject.getString("name");
			
			Date now = new Date();

			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", loginAccount);
			
			/**
			 * 记录用户积分数据 
			 */
//			UserCardManual jingDongManual = userCardManualService.getByUserIdCardTypeId(userId,UserCardManual.JINGDONG_INTEGRAL);
			//先判断账号是否登录过,兼容自动发现
			UserCardManual jingDongManualAccount = userCardManualService.getUserCardManualSelective(userId,UserCardManual.JINGDONG_INTEGRAL,null,null,loginAccount);
			if(jingDongManualAccount == null){
				UserCardManual jingDongManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.JINGDONG_INTEGRAL,null,userName,null);
				if(jingDongManual != null){
					presentTubiService.presentTubiDo(userId, UserCardManual.JINGDONG_INTEGRAL, jingDongManual.getIntegral(), integral, resData, version);
					
					jingDongManual.setIntegral(integral);
					jingDongManual.setUserName(userName);
					jingDongManual.setGmtModify(now);
					jingDongManual.setLoginAccount(loginAccount);
					jingDongManual.setStatus(1);
					
					userCardManualService.updateByPrimaryKeySelective(jingDongManual);
				}else{
					presentTubiService.presentTubiDo(userId, UserCardManual.JINGDONG_INTEGRAL, 0, integral, resData, version);
					
					jingDongManual = new UserCardManual(userId, UserCardManual.JINGDONG_INTEGRAL, userName, integral);
					jingDongManual.setLoginAccount(loginAccount);
					
					userCardManualService.insert(jingDongManual);
				}
			}else{
				presentTubiService.presentTubiDo(userId, UserCardManual.JINGDONG_INTEGRAL, jingDongManualAccount.getIntegral(), integral, resData, version);
				
				jingDongManualAccount.setIntegral(integral);
				jingDongManualAccount.setUserName(userName);
				jingDongManualAccount.setGmtModify(now);
				jingDongManualAccount.setLoginAccount(loginAccount);
				jingDongManualAccount.setStatus(1);
				
				userCardManualService.updateByPrimaryKeySelective(jingDongManualAccount);
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
			
			if(loginAccount.matches("^[1][358][0-9]{9}$")){
				manualLogin.setType(ManualLogin.TYPE_PHONE);
			}else{
				manualLogin.setType(ManualLogin.TYPE_LOGIN_ACCOUNT);
			}
			
			if(StrUtil.isPhone(loginAccount)){
				manualLogin.setType(ManualLogin.TYPE_PHONE);
			}else if(StrUtil.isEmail(loginAccount)){
				manualLogin.setType(ManualLogin.TYPE_EMAIL);
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
			logger.error("登录京东账户失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.JINGDONG_LOGIN_ERROR,e.getMessage());
		}
	}
}
