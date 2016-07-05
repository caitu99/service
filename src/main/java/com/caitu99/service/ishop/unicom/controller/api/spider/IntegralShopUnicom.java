package com.caitu99.service.ishop.unicom.controller.api.spider;

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
 * 联通积分商城
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IntegralShopUnicom 
 * @author xiongbin
 * @date 2016年3月22日 下午3:40:25 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Component
public class IntegralShopUnicom {
	
	private final static Logger logger = LoggerFactory.getLogger(IntegralShopUnicom.class);
	
	@Autowired
	private AppConfig appConfig;
	
	/**
	 * 登录初始化
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: init 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年3月22日 下午3:42:25  
	 * @author xiongbin
	 */
	public String init(Long userid){
		try {
			String url = appConfig.spiderUrl + "/api/shop/unicom/init/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userid.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("联通积分商城登录初始化返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("联通积分商城登录初始化失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 获取图片验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getImageCode 
	 * @param userid	用户id
	 * @return
	 * @date 2016年3月22日 下午3:43:56  
	 * @author xiongbin
	 */
	public String getImageCode(Long userid){
		try {
			String url = appConfig.spiderUrl + "/api/shop/unicom/getvcode/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userid.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("联通积分商城获取图片验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("联通积分商城获取图片验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 登录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userid		用户ID
	 * @param phone			手机号码
	 * @param password		服务密码
	 * @param imageCode		图片验证码
	 * @return
	 * @date 2016年3月22日 下午3:46:57  
	 * @author xiongbin
	 */
	public String login(Long userid,String phone,String password,String imageCode){
		try {
			String url = appConfig.spiderUrl + "/api/shop/unicom/login/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("account", phone);
			paramMap.put("password", password);
			paramMap.put("vcode", imageCode);
			paramMap.put("self", "false");
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("联通积分商城登录返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("联通积分商城登录失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 发送支付验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: paySendSMS 
	 * @param userid		用户ID
	 * @param paramMap		商品属性集合
	 * @return
	 * @date 2016年3月22日 下午3:52:29  
	 * @author xiongbin
	 */
	public String paySendSMS(Long userid,Map<String,String> paramMap){
		try {
			String url = appConfig.spiderUrl + "/api/shop/unicom/getsmscode/1.0";
			paramMap.put("userid", userid.toString());
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("联通积分商城发送支付验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("联通积分商城发送支付验证码失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
	
	/**
	 * 支付
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pay 
	 * @param userid		用户ID
	 * @param phoneCode		短信验证码
	 * @return
	 * @date 2016年3月22日 下午4:28:18  
	 * @author xiongbin
	 */
	public String pay(Long userid,String phoneCode){
		try {
			String url = appConfig.spiderUrl + "/api/shop/unicom/submit/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", userid.toString());
			paramMap.put("smscode", phoneCode);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("联通积分商城支付返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("联通积分商城支付失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
}
