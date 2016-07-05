package com.caitu99.service.integral.controller.spider;

import java.util.HashMap;
import java.util.Map;

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
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.json.JsonResult;

@Component
public class ManualQueryEsurfing extends ManualQueryAbstract{
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryEsurfing.class);
	
	@Override
	public String getImageCode(Long userid){
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/imgcode/get/1.0";
		super.setUrl(url);
		super.setName("天翼");
		super.setSucceedCode(ApiResultCode.NEED_INOUT_IMAGECODE);
		super.setFailureCode(ApiResultCode.GET_TIANYI_IMAGE_CODE_ERROR);
		
		return super.getImageCode(userid);
	}

	@Override
	public String login(Long userId, String loginAccount, String password,String imageCode) {
		String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/imgcode/check/1.0";
		super.setName("天翼");
		Map<String,String> param = new HashMap<String,String>();
		param.put("userid", userId.toString());
		param.put("phoneNo", loginAccount);
		param.put("imgCode", imageCode);

		return super.login(userId, url, param, ApiResultCode.SUCCEED);
//		String reslut = super.login(userId, url, param, ApiResultCode.SUCCEED);
//		boolean flag = JsonResult.checkResult(reslut);
//		if(flag){
//			JSONObject resultJson = new JSONObject();
//			resultJson.put("code", ApiResultCode.FOLLOWUP_VERIFY_PHONE);
//			resultJson.put("message", "验证成功,有后续验证");
//			return resultJson.toJSONString();
//		}
//		
//		return reslut;
	}

	@Override
	public String login(Long userId, String loginAccount, String password) {
		return null;
	}

	@Override
	public String save(Long userId, Long manualId, String loginAccount,String password, String result, String version) {
		boolean flag = JsonResult.checkResult(result);
		if(flag){
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", ApiResultCode.FOLLOWUP_VERIFY_PHONE);
			resultJson.put("message", "验证成功,有后续验证");
			return resultJson.toJSONString();
		}
		return result;
	}
	
	/**
	 * 验证天翼图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkTianYiImageCode 
	 * @param phoneNo		手机号
	 * @param imgCode		图片验证码
	 * @return
	 * @throws ManualQueryAdaptorException
	 * @date 2015年11月11日 下午2:51:18  
	 * @author xiongbin
	 */
	public String checkImageCode(Long userId,String phoneNo,String imgCode) throws ManualQueryAdaptorException {
		try {
			AssertUtil.hasLength(phoneNo, "手机号不能为空");
			AssertUtil.hasLength(imgCode, "图片验证码不能为空");
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userId.toString());
			paramMap.put("phoneNo", phoneNo);
			paramMap.put("imgCode", imgCode);
			
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/imgcode/check/1.0";
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			AssertUtil.hasLength(jsonString, "获取数据为空");
			
			logger.info("验证天翼图片验证码成功:" + jsonString);
			
			boolean flag = JsonResult.checkResult(jsonString,ApiResultCode.SUCCEED);
			if(!flag){
				JSONObject json = JSON.parseObject(jsonString);
				Integer code = json.getInteger("code");
				
				if(code.equals(2004)){
					String image = getImageCode(userId);
					image = analysisImageCode(image);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else if(code.equals(2005)){
					return ApiResult.outSucceed(ApiResultCode.LOGINACCOUNT_PASSWORD_ERROR, "账号或密码错误");
				}else if(code.equals(2007) || code.equals(2603) || code.equals(2049)){
					return ApiResult.outSucceed(ApiResultCode.SYSTEM_BUSY, "系统繁忙");
				}
			}
			
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", ApiResultCode.FOLLOWUP_VERIFY_PHONE);
			resultJson.put("message", "验证成功,有后续验证");
			
			return resultJson.toJSONString();
		} catch (Exception e) {
			logger.error("验证天翼图片验证码失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.CHECH_TIANYI_IMAGE_CODE_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 天翼登录,获取积分
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param phoneNo		手机号
	 * @param msCode		短信验证码
	 * @return
	 * @date 2015年11月11日 下午4:34:21  
	 * @author xiongbin
	 */
	public String login(Long userId,Long manualId,String phoneNo,String msCode){
		try {
			AssertUtil.hasLength(phoneNo, "手机号不为空");
			AssertUtil.hasLength(msCode, "短信验证码不为空");
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("phoneNo", phoneNo);
			paramMap.put("msCode", msCode);
			paramMap.put("userid", userId.toString());
			
			String url = SpringContext.getBean(AppConfig.class).spiderUrl + "/login/1.0";
			String jsonString = HttpClientUtils.getInstances().doPost(url, "UTF-8",paramMap);
			AssertUtil.hasLength(jsonString, "获取数据为空");

			logger.info("登录天翼成功:" + jsonString);
			
			boolean flag = JsonResult.checkResult(jsonString,ApiResultCode.SUCCEED);
			if(!flag){
				JSONObject json = JSON.parseObject(jsonString);
				Integer code = json.getInteger("code");
				
				if(code.equals(2004)){
					String image = getImageCode(userId);
					image = analysisImageCode(image);
					JSONObject resultJson = new JSONObject();
					resultJson.put("code", ApiResultCode.IMAGECODE_ERROR);
					resultJson.put("message", "图片验证码不正确");
					resultJson.put("imagecode", image);
					return resultJson.toJSONString();
				}else if(code.equals(2005)){
					return ApiResult.outSucceed(ApiResultCode.PHONE_CODE_ERROR, "短信验证码不正确");
				}else if(code.equals(2007) || code.equals(2603) || code.equals(2049)){
					return ApiResult.outSucceed(ApiResultCode.SYSTEM_BUSY, "系统繁忙");
				}
			}
			
			return jsonString;
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("登录天翼失败:" + e.getMessage(),e);
			throw new ManualQueryAdaptorException(ApiResultCode.TIANYI_LOGIN_ERROR,e.getMessage());
		}
	}
}
