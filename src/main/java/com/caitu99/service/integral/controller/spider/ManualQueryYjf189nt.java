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
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.ManualQueryAdaptorException;
import com.caitu99.service.integral.controller.spider.abs.ManualQueryAbstract;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.json.JsonResult;

@Component
public class ManualQueryYjf189nt extends ManualQueryAbstract{
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryYjf189nt.class);
	
	
	@Override
	public String getImageCode(Long userid){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/yjf189nt/img/1.0";
		super.setUrl(url);
		super.setName("天翼");
		super.setSucceedCode(ApiResultCode.NEED_INOUT_IMAGECODE);
		super.setFailureCode(ApiResultCode.GET_TIANYI_IMAGE_CODE_ERROR);
		
		return super.getImageCode(userid);
	}
	

	@Override
	public String login(Long userid,String account,String password,String imageCode){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/yjf189nt/login/1.0";
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
			logger.error("登录天翼失败:{}",e);
			throw new ManualQueryAdaptorException(ApiResultCode.TIANYI_LOGIN_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 天翼修改密码第一步：获取图形验证码
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: resetPwdStep1 
	 * @param userId
	 * @return
	 * @date 2016年3月15日 下午4:45:02  
	 * @author ws
	 */
	public String getResetImageCode(String userId){
		ApiResult<String> result = new ApiResult<String>();
		try {
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/yjf189nt/resetimg/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userId.toString());
			
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			logger.info("天翼图片验证码返回数据:" + jsonString);
			
			Boolean flag = JsonResult.checkResult(jsonString,1001);
			if(!flag){
				return result.toJSONString(3003, "获取天翼图片验证码失败");
			}

			String imageCode = JsonResult.getResult(jsonString, "data");
			
			if(StringUtils.isBlank(imageCode)){
				return result.toJSONString(3003, "获取天翼图片验证码失败");
			}
			
			return result.toJSONString(0, "", imageCode);
		} catch (Exception e) {
			logger.info("获取天翼修改密码图片验证码失败:" + e.getMessage(),e);
			return result.toJSONString(3003, "获取天翼图片验证码失败:" + e.getMessage());
		}
	}
	
	/**
	 * 天翼修改密码第二步：验证图形验证码并发送短信验证码
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: resetPwdStep1 
	 * @param userId
	 * @return
	 * @date 2016年3月15日 下午4:45:02  
	 * @author ws
	 */
	public String resetPwdStep2(String userId,String phoneNo,String vcode){
		ApiResult<String> result = new ApiResult<String>();
		String reslut = "";
		
		if(StringUtils.isBlank(vcode)){
			reslut = getResetImageCode(userId);
			String imgData = "";
			if(JsonResult.checkResult(reslut)){
				imgData = JsonResult.getResult(reslut, "data");
				vcode = crackImageCode(imgData);
			}else{
				vcode = null;
			}
			
			if(null == vcode){
				reslut = getResetImageCode(userId);
				boolean flag = JsonResult.checkResult(reslut);
				if(flag){
					vcode = JsonResult.getResult(reslut, "data");
					JSONObject reslutJson = new JSONObject();
					reslutJson.put("code", ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR);
					reslutJson.put("imagecode", vcode);
					reslutJson.put("message", "自动识别图片验证码失败");
					logger.warn("自动识别图片验证码失败");
					return reslutJson.toJSONString();
				}
				return reslut;
			}
		}
		
		try {
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/yjf189nt/msg/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userId);
			paramMap.put("account", phoneNo);
			paramMap.put("vcode", vcode);
			
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			logger.info("重置密码第二步返回数据:" + jsonString);
			
			Boolean flag = JsonResult.checkResult(jsonString,0);
			if(!flag){
				if(jsonString.contains("验证码填写错误或已过期")){
					reslut = getResetImageCode(userId);
					boolean imgFlag = JsonResult.checkResult(reslut);
					if(imgFlag){
						vcode = JsonResult.getResult(reslut, "data");
						JSONObject reslutJson = new JSONObject();
						reslutJson.put("code", ApiResultCode.AUTO_DISCERN_IMAGECODE_ERROR);
						reslutJson.put("imagecode", vcode);
						reslutJson.put("message", "图形验证码错误");
						return reslutJson.toJSONString();
					}
				}
				return jsonString;
			}

			return result.toJSONString(0, "请输入短信验证码");
		} catch (Exception e) {
			logger.warn("短信验证码发送失败",e);
			return result.toJSONString(3003, "短信验证码发送失败" + e.getMessage());
		}
	}
	
	/**
	 * 天翼修改密码第三步：验证短信验证码，并修改密码
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: resetPwdStep1 
	 * @param userId
	 * @return
	 * @date 2016年3月15日 下午4:45:02  
	 * @author ws
	 */
	public String resetPwdStep3(String userId,String password,String msgcode){
		ApiResult<String> result = new ApiResult<String>();
		try {
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/yjf189nt/reset/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userId.toString());
			paramMap.put("password", password);
			paramMap.put("msgcode", msgcode);
			
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			
			logger.info("重置密码第三步返回数据:" + jsonString);
			
			Boolean flag = JsonResult.checkResult(jsonString,0);
			if(!flag){
				return jsonString;
			}

			return result.toJSONString(0, "修改成功");
		} catch (Exception e) {
			logger.warn("修改密码失败",e);
			return result.toJSONString(3003, "修改密码失败" + e.getMessage());
		}
	}
	
}
