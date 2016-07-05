package com.caitu99.service.ishop.esurfing.controller.api.spider;

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
 * 天翼积分商城
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IntegralShopEsurfing 
 * @author xiongbin
 * @date 2016年2月16日 上午11:19:50 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Component
public class IntegralShopEsurfing {
	
	private final static Logger logger = LoggerFactory.getLogger(IntegralShopEsurfing.class);
	
	@Autowired
	private AppConfig appConfig;
	
	/**
	 * 获取天翼积分商城登录图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCode 
	 * @param userId
	 * @return
	 * @date 2016年2月16日 上午11:21:56  
	 * @author xiongbin
	 */
	public String getImageCode(Long userId){
		try {
			String url = appConfig.spiderUrl + "/tianyishop/imgcode/get/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userId.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("获取天翼积分商城登录图片验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("获取天翼积分商城登录图片验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 天翼积分商城效验图片验证码,并发送登录短信
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: loginSend 
	 * @param userid		用户ID
	 * @param phoneNo		手机号
	 * @param imageCode		图片验证码
	 * @return
	 * @date 2016年2月16日 上午11:26:37  
	 * @author xiongbin
	 */
	public String loginSend(Long userid,String phone,String imageCode){
		try {
			String url = appConfig.spiderUrl + "/tianyishop/imgcode/check/1.0";
			Map<String,String> paramMap = new HashMap<String,String>(3);
			paramMap.put("userid", userid.toString());
			paramMap.put("phoneNo", phone);
			paramMap.put("imgCode", imageCode);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("天翼积分商城登录返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("天翼积分商城登录失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	public String login(Long userid,String phone,String msCode){
		try {
			String url = appConfig.spiderUrl + "/tianyishop/login/1.0";
			Map<String,String> paramMap = new HashMap<String,String>(3);
			paramMap.put("userid", userid.toString());
			paramMap.put("phoneNo", phone);
			paramMap.put("msCode", msCode);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("天翼积分商城登录返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("天翼积分商城登录失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 天翼积分商城生成订单,并发送短信验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: orderGenerate 
	 * @param userid		用户ID
	 * @param paramMap		传递参数
	 * @return
	 * @date 2016年2月16日 上午11:31:39  
	 * @author xiongbin
	 */
	public String orderGenerate(Long userid,Map<String,String> paramMap){
		try {
			String url = appConfig.spiderUrl + "/tianyishop/order/1.0";
			paramMap.put("userid", userid.toString());
			//固定为2
			paramMap.put("SystemType", "2");
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("天翼积分商城生成订单返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("天翼积分商城生成订单失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 天翼积分商城支付
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pay 
	 * @param userid		用户ID
	 * @param phoneCode		短信验证码
	 * @return
	 * @date 2016年2月16日 上午11:34:52  
	 * @author xiongbin
	 */
	public String pay(Long userid,String phoneCode){
		try {
			String url = appConfig.spiderUrl + "/tianyishop/pay/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("rndCode", phoneCode);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("天翼积分商城支付返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("天翼积分商城支付失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 天翼积分商城支付发送验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: paySMS 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年2月18日 上午10:21:50  
	 * @author xiongbin
	 */
	public String paySMS(Long userid){
		try {
			String url = appConfig.spiderUrl + "/tianyishop/paymsg/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userid.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("天翼积分商城支付发送验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("天翼积分商城支付发送验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 天翼积分商城获取券码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getCode 
	 * @param userid		用户ID
	 * @return
	 * @date 2016年2月26日 上午11:04:42  
	 * @author xiongbin
	 */
	public String getCode(Long userid){
		try {
			String url = appConfig.spiderUrl + "/tianyishop/getcode/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userid.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("天翼积分商城获取券码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("天翼积分商城获取券码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
}
