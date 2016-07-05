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

@Component
public class ManualQueryYjf189 extends ManualQueryAbstract{
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryYjf189.class);
	
	
	@Override
	public String getImageCode(Long userid){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/yjf189/img/1.0";
		super.setUrl(url);
		super.setName("天翼");
		super.setSucceedCode(ApiResultCode.NEED_INOUT_IMAGECODE);
		super.setFailureCode(ApiResultCode.GET_TIANYI_IMAGE_CODE_ERROR);
		
		return super.getImageCode(userid);
	}
	

	@Override
	public String login(Long userid,String account,String password,String imageCode){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/yjf189/login/1.0";
		super.setName("天翼");
		Map<String,String> param = new HashMap<String,String>();
		param.put("userid", userid.toString());
		param.put("account", account);
		param.put("password", password);
		param.put("vcode", imageCode);

		return super.login(userid, url, param, ApiResultCode.SUCCEED);
	}
	
	@Override
	public String login(Long userId, String loginAccount, String password) {
		return null;
	}

	@Override
	public String save(Long userId, Long manualId, String phoneNo,String password, String result, String version) {
		try {
			JSONObject json = JSON.parseObject(result);
			if(!json.getInteger("code").equals(0)){
				return json.toJSONString();
			}
			
			String jsonString = json.getString("data");
			AssertUtil.notNull(jsonString, "从天翼获取用户数据失败");
			
			JSONObject jsonObject = JSON.parseObject(jsonString);
			AssertUtil.notNull(jsonObject, "从天翼获取用户数据失败");
			
			//电信积分
			Integer integral = jsonObject.getInteger("Integral");
			//天翼积分
			Integer voucher = jsonObject.getInteger("Voucher");	
			//用户名
			String userName = jsonObject.getString("custName");
			
			Date now = new Date();

			Map<String,Object> resData = new HashMap<String, Object>();
			resData.put("userId", String.valueOf(userId));
			resData.put("manualId", String.valueOf(manualId));
			resData.put("loginAccount", phoneNo);
			
			/**
			 * 记录用户积分数据 
			 */
			//电信积分
			UserCardManual integralManual = userCardManualService.getUserCardManualSelective(userId, UserCardManual.CT_INTEGRAL,null,null,phoneNo);
			
			if(integralManual != null){
				//赠送途币
				presentTubiService.presentTubiDo(userId, UserCardManual.CT_INTEGRAL, integralManual.getIntegral(), integral, resData, version);
				
				integralManual.setIntegral(integral);
				integralManual.setGmtModify(now);
				integralManual.setLoginAccount(phoneNo);
				integralManual.setStatus(1);
				userCardManualService.updateByPrimaryKeySelective(integralManual);
			}else{
				//赠送途币
				presentTubiService.presentTubiDo(userId, UserCardManual.CT_INTEGRAL, 0, integral, resData, version);
				
				integralManual = new UserCardManual(userId,UserCardManual.CT_INTEGRAL,userName,integral);
				integralManual.setLoginAccount(phoneNo);
				integralManual.setGmtCreate(now);
				integralManual.setGmtModify(now);
				userCardManualService.insert(integralManual);
			}
			
			//天翼积分
			UserCardManual voucherManual = userCardManualService.getUserCardManualSelective(userId,UserCardManual.ESURFING_INTEGRAL,null,null,phoneNo);
			
			if(voucherManual != null){
				voucherManual.setIntegral(voucher);
				voucherManual.setGmtModify(now);
				voucherManual.setLoginAccount(phoneNo);
				voucherManual.setStatus(1);
				userCardManualService.updateByPrimaryKeySelective(voucherManual);
			}else{
				voucherManual = new UserCardManual(userId,UserCardManual.ESURFING_INTEGRAL,userName,voucher);
				voucherManual.setLoginAccount(phoneNo);
				voucherManual.setGmtCreate(now);
				voucherManual.setGmtModify(now);
				userCardManualService.insert(voucherManual);
			}
			
			/**
			 * 记录用户登录数据
			 */
			ManualLogin manualLogin = new ManualLogin();
			manualLogin.setLoginAccount(phoneNo);
			manualLogin.setUserId(userId);
			manualLogin.setManualId(manualId);
			
			ManualLogin oldManualLogin = manualLoginService.getBySelective(manualLogin);
			manualLogin.setType(ManualLogin.TYPE_PHONE);
			manualLogin.setIsPassword(ManualLogin.IS_PASSWORD_YES);
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
			logger.error("登录天翼失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.TIANYI_LOGIN_ERROR,e.getMessage());
		}
	}
}
