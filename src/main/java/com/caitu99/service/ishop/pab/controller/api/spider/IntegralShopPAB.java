package com.caitu99.service.ishop.pab.controller.api.spider;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.utils.http.HttpClientUtils;

/**
 * 平安银行积分商城
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IntegralShopPAB 
 * @author xiongbin
 * @date 2016年3月31日 上午11:56:15 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Component
public class IntegralShopPAB {
	
	private final static Logger logger = LoggerFactory.getLogger(IntegralShopPAB.class);
	
	@Autowired
	private AppConfig appConfig;
	
	/** 第三方商城 */
	
	/**
	 * 获取登录验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCodeThird 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年4月1日 下午12:15:37  
	 * @author xiongbin
	 */
	public String getImageCodeThird(Long userid) {
		try {
			String url = appConfig.spiderUrl + "/api/shop/pn/getimgcode/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
	        paramMap.put("userid", String.valueOf(userid));
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行第三方商城获取登录验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行第三方商城获取登录验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginThird 
	 * @param userid			用户ID
	 * @param loginAccount		用户账户
	 * @param password			密码
	 * @param imageCode			图片验证码
	 * @return
	 * @date 2016年4月1日 下午12:19:54  
	 * @author xiongbin
	 */
	public String loginThird(Long userid,String loginAccount,String password,String imageCode){
		try {
			String url = appConfig.spiderUrl + "/api/shop/pn/login/1.0";
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("account", loginAccount);
			paramMap.put("password", password);
			paramMap.put("imgcode", imageCode);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行第三方商城登录返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行第三方商城登录失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 下单
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: orderThird 
	 * @param userid		用户ID
	 * @param paramMap		参数集合
	 * @return
	 * @date 2016年4月6日 上午11:45:36  
	 * @author xiongbin
	 */
	public String orderThird(Long userid,Map<String,String> paramMap){
		try {
			String url = appConfig.spiderUrl + "/api/shop/pn/order/1.0";
			paramMap.put("userid", userid.toString());
			paramMap.put("repositoryId", "2");
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行第三方商城下单返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行第三方商城下单失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 发送支付短信验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: paySendThird 
	 * @param userid		用户ID
	 * @return
	 * @date 2016年4月6日 下午12:14:51  
	 * @author xiongbin
	 */
	public String paySendThird(Long userid){
		try {
			String url = appConfig.spiderUrl + "/api/shop/pn/sms/1.0";
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("userid", userid.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行第三方商城发送支付短信验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行第三方商城发送支付短信验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 支付
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: payThird 
	 * @param userid			用户ID
	 * @param smsCode			短信验证码
	 * @param payPassWord		支付密码
	 * @return
	 * @date 2016年4月6日 下午12:20:25  
	 * @author xiongbin
	 */
	public String payThird(Long userid,String phoneCode,String payPassWord){
		try {
			String url = appConfig.spiderUrl + "/api/shop/pn/check/1.0";
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("smsCode", phoneCode);
			paramMap.put("payPassWord", payPassWord);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行第三方商城支付返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行第三方商城支付失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/** 第三方商城 */
	
	
	
	
	
	/** 积分变现 */
	
	/**
	 * 获取登录验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCodeRealization 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年4月6日 上午11:19:31  
	 * @author xiongbin
	 */
	public String getImageCodeRealization(Long userid) {
		try {
			String url = appConfig.spiderUrl + "/pingan/oil/getimgcode/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
	        paramMap.put("userid", String.valueOf(userid));
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行积分变现获取登录验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行积分变现获取登录验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginRealization 
	 * @param userid			用户ID
	 * @param loginAccount		登录账号
	 * @param password			密码
	 * @param imageCode			图片验证码
	 * @return
	 * @date 2016年4月6日 下午3:33:48  
	 * @author xiongbin
	 */
	public String loginRealization(Long userid,String loginAccount,String password,String imageCode){
		try {
			String url = appConfig.spiderUrl + "/pingan/oil/login/1.0";
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("account", loginAccount);
			paramMap.put("password", password);
			paramMap.put("imgcode", imageCode);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行积分变现登录返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行积分变现登录失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 获取下单图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getOrderImageCode 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年4月6日 下午5:02:25  
	 * @author xiongbin
	 */
	public String getOrderImageCode(Long userid){
		try {
			String url = appConfig.spiderUrl + "/pingan/oil/payvcode/1.0";
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("userid", userid.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行积分变现获取下单图片验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行积分变现获取下单图片验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 下单
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: orderRealization 
	 * @param userid		用户ID
	 * @param imageCode		图片验证码
	 * @param paramMap		参数集合
	 * @return
	 * @date 2016年4月6日 下午5:17:53  
	 * @author xiongbin
	 */
	public String orderRealization(Long userid,String imageCode,Map<String,String> paramMap){
		try {
			String url = appConfig.spiderUrl + "/pingan/oil/submitpay/1.0";
			paramMap.put("userid", userid.toString());
			paramMap.put("vcode", imageCode);
			paramMap.put("phoneNum", "15394209984");
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行第三方商城下单返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行第三方商城下单失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 发送支付验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: paySendRealization 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年4月6日 下午5:11:20  
	 * @author xiongbin
	 */
	public String paySendRealization(Long userid){
		try {
			String url = appConfig.spiderUrl + "/pingan/oil/msg/1.0";
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("userid", userid.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行积分变现发送支付短信验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行积分变现发送支付短信验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 支付
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: payRealization 
	 * @param userid			用户ID
	 * @param phoneCode			短信验证码
	 * @param payPassWord		支付密码
	 * @return
	 * @date 2016年4月6日 下午5:10:02  
	 * @author xiongbin
	 */
	public String payRealization(Long userid,String phoneCode,String payPassWord){
		try {
			String url = appConfig.spiderUrl + "/pingan/oil/dopay/1.0";
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("msgCode", phoneCode);
			paramMap.put("payPwd", payPassWord);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行积分变现支付返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行积分变现支付失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}

	/** 积分变现 */
	
	/** 忘记密码 */
	
	/**
	 * 获取忘记密码图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getForgetImageCode 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年4月7日 下午12:33:16  
	 * @author xiongbin
	 */
	public String getForgetImageCode(Long userid){
		try {
			String url = appConfig.spiderUrl + "/api/passwd/pn/imgcode/1.0";
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("userid", userid.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行获取忘记密码图片验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行获取忘记密码图片验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 忘记密码验证图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: verifyForgetImageCode 
	 * @param userid			用户ID
	 * @param loginAccount		登录账号
	 * @param imageCode			图片验证码
	 * @return
	 * @date 2016年4月7日 下午2:04:05  
	 * @author xiongbin
	 */
	public String verifyForgetImageCode(Long userid,String loginAccount,String imageCode){
		try {
			String url = appConfig.spiderUrl + "/api/passwd/pn/regvcode/1.0";
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("imgcode", imageCode);
			paramMap.put("account", loginAccount);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行忘记密码验证图片验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行忘记密码验证图片验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 获取忘记密码第二张图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getForgetImageCode2 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年4月7日 下午4:10:05  
	 * @author xiongbin
	 */
	public String getForgetImageCode2(Long userid){
		try {
			String url = appConfig.spiderUrl + "/api/passwd/pn/thirvcode/1.0";
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("userid", userid.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行获取忘记密码第二张图片验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行获取忘记密码第二张图片验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 忘记密码验证发送验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: sendSmsForget 
	 * @param userid		用户ID
	 * @param imageCode		图片yanzm
	 * @return
	 * @date 2016年4月7日 下午2:05:59  
	 * @author xiongbin
	 */
	public String sendSmsForget(Long userid,String imageCode){
		try {
			String url = appConfig.spiderUrl + "/api/passwd/pn/sendcode/1.0";
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("imgcode", imageCode);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行忘记密码发送验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行忘记密码发送验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 忘记密码验证短信验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: verifySmsForget 
	 * @param userid		用户ID
	 * @param phoneCode		短信验证码
	 * @return
	 * @date 2016年4月7日 下午2:11:27  
	 * @author xiongbin
	 */
	public String verifySmsForget(Long userid,String phoneCode){
		try {
			String url = appConfig.spiderUrl + "/api/passwd/pn/checkcode/1.0";
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("code", phoneCode);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行忘记密码验证短信验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行忘记密码验证短信验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 修改密码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: updatePassword 
	 * @param userid		用户ID
	 * @param password		新密码
	 * @return
	 * @date 2016年4月7日 下午2:24:09  
	 * @author xiongbin
	 */
	public String updatePassword(Long userid,String password){
		try {
			String url = appConfig.spiderUrl + "/api/passwd/pn/update/1.0";
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("password", password);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("平安银行忘记密码修改密码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("平安银行忘记密码修改密码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/** 忘记密码 */
}
