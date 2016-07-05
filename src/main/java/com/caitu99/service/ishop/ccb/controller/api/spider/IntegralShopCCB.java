package com.caitu99.service.ishop.ccb.controller.api.spider;

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
 * 建行积分商城
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IntegralShopCCB 
 * @author xiongbin
 * @date 2016年1月29日 下午2:33:37 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Component
public class IntegralShopCCB {
	
	private final static Logger logger = LoggerFactory.getLogger(IntegralShopCCB.class);
	
	@Autowired
	private AppConfig appConfig;
	
	/**
	 * 建行积分商城注册发送验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: registerSendSMS 
	 * @param userid			用户ID
	 * @param loginAccount		用户名
	 * @param password			密码
	 * @param phone				手机号码
	 * @return
	 * @date 2016年1月28日 下午5:38:36  
	 * @author xiongbin
	 */
	public String registerSendSMS(Long userid,String loginAccount,String password,String phone){
		try {
			String url = appConfig.spiderUrl + "/api/shop/ccb/register/check/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("mobile", phone);
			paramMap.put("account", loginAccount);
			paramMap.put("password", password);
			paramMap.put("userId", userid.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("建行积分商城发送验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("建行积分商城发送验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 建行积分商城注册
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: register 
	 * @param userid		用户ID
	 * @param code			手机验证码
	 * @return
	 * @date 2016年1月29日 下午12:03:31  
	 * @author xiongbin
	 */
	public String register(Long userid,String code){
		try {
			String url = appConfig.spiderUrl + "/api/shop/ccb/register/submit/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("smsCode", code);
			paramMap.put("userId", userid.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("建行积分商城注册返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("建行积分商城注册失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 获取建行积分商城登录图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCode 
	 * @param userId		用户ID
	 * @return
	 * @date 2016年2月1日 上午11:19:59  
	 * @author xiongbin
	 */
	public String getImageCode(Long userId){
		try {
			String url = appConfig.spiderUrl + "/api/shop/ccb/getimgcode/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userId", userId.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("获取建行积分商城登录图片验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("获取建行积分商城登录图片验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userid			用户ID
	 * @param loginAccount		用户名
	 * @param password			密码
	 * @param imageCode			图片验证码
	 * @return
	 * @date 2016年2月1日 下午4:39:42  
	 * @author xiongbin
	 */
	public String login(Long userid,String loginAccount,String password,String imageCode){
		try {
			String url = appConfig.spiderUrl + "/api/shop/ccb/login/1.0";
			
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("userId", userid.toString());
			paramMap.put("vcode", imageCode);
			paramMap.put("account", loginAccount);
			paramMap.put("password", password);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("建行积分商城登录返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("建行积分商城登录失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 下单
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: order 
	 * @param userid			用户ID
	 * @param paramMap			商品属性集合
	 * @return
	 * @date 2016年3月23日 下午2:15:04  
	 * @author xiongbin
	 */
	public String order(Long userid,Map<String,String> paramMap){
		try {
			String url = appConfig.spiderUrl + "/api/shop/ccb/order/1.0";
			paramMap.put("userId", userid.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("建行积分商城下单返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("建行积分商城下单失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}

	/**
	 * 建行积分商城支付发送验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: paySendSMS 
	 * @param userid			用户ID
	 * @param cardNo			银行卡号
	 * @param reservedPhone		银行预留手机号码
	 * @return
	 * @date 2016年1月29日 下午5:59:08  
	 * @author xiongbin
	*/
	public String paySendSMS(Long userid, String cardNo, String reservedPhone) {
		try {
			String url = appConfig.spiderUrl + "/api/shop/ccb/msgsend/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userId", userid.toString());
			paramMap.put("cardNumber", cardNo);
			paramMap.put("cardMobile", reservedPhone);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("建行积分商城支付发送验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("建行积分商城支付发送验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 建行积分商城支付
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pay 
	 * @param userid		用户ID
	 * @param phoneCode		手机验证码
	 * @return
	 * @date 2016年2月1日 下午5:09:50  
	 * @author xiongbin
	 */
	public String pay(Long userid, String phoneCode) {
		try {
			String url = appConfig.spiderUrl + "/api/shop/ccb/dopay/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userId", userid.toString());
			paramMap.put("smsCode", phoneCode);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("建行积分商城支付返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("建行积分商城支付失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
}
