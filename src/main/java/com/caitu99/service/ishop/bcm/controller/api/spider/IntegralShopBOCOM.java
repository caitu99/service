package com.caitu99.service.ishop.bcm.controller.api.spider;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.utils.http.HttpClientUtils;

/**
 * 交通银行积分商城
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IntegralShopBOCOM 
 * @author xiongbin
 * @date 2016年2月29日 下午3:43:29 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Component
public class IntegralShopBOCOM {
	
	private final static Logger logger = LoggerFactory.getLogger(IntegralShopBOCOM.class);
	
	@Autowired
	private AppConfig appConfig;
	
	/**
	 * 登录,并下单
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: login 
	 * @param userid		用户ID
	 * @param cardNo		卡号
	 * @param password		密码
	 * @param paramMap		传递参数
	 * @return
	 * @date 2016年2月29日 下午4:06:58  
	 * @author xiongbin
	 */
	public String login(Long userid,String cardNo,String password,Map<String,String> paramMap){
		try {
			String login_url = appConfig.spiderUrl + "/api/ishop/com/login/1.0";
			String order_url = appConfig.spiderUrl + "/api/ishop/com/order/1.0";
			paramMap.put("userid", userid.toString());
			paramMap.put("account", cardNo);
			paramMap.put("password", password);
			
			String result = HttpClientUtils.getInstances().doPost(order_url, "UTF-8", paramMap);
			JSONObject json = JSON.parseObject(result);
			Integer code = json.getInteger("code");
			if( code == 1005 ){ //未登录状态
				result = HttpClientUtils.getInstances().doPost(login_url, "UTF-8", paramMap);
				json = JSON.parseObject(result);
				code = json.getInteger("code");
				if( code == 0 ){
					result = HttpClientUtils.getInstances().doPost(order_url, "UTF-8", paramMap);
				}
			}
			logger.info("交通银行积分商城登录返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("交通银行积分商城登录失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}

	/**
	 * 发送支付验证码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: paySendSMS 
	 * @param userid		用户ID
	 * @param cardYear		信用卡年份
	 * @param cardMonth		信用卡月份
	 * @return
	 * @date 2016年2月29日 下午5:41:53  
	 * @author xiongbin
	 */
	public String paySendSMS(Long userid, String cardYear, String cardMonth) {
		try {
			String url = appConfig.spiderUrl + "/api/ishop/com/sms/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("userid", String.valueOf(userid));
	        paramMap.put("cardyear", cardYear);
	        paramMap.put("cardmonth", cardMonth);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("交通银行积分商城支付发送验证码返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("交通银行积分商城支付发送验证码失败:" + e.getMessage(),e);
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
	 * @date 2016年2月29日 下午6:32:43  
	 * @author xiongbin
	 */
	public String pay(Long userid, String phoneCode) {
		try {
			String url = appConfig.spiderUrl + "/api/ishop/com/pay/1.0";
			Map<String,String> paramMap = new HashMap<String,String>();
	        paramMap.put("userid", String.valueOf(userid));
	        paramMap.put("smscode", phoneCode);
			
			String result = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			logger.info("交通积分商城支付返回数据:" + result);
			return result;
		} catch (Exception e) {
			logger.error("交通积分商城支付失败:" + e.getMessage(),e);
			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
			return result.toJSONString(-1, e.getMessage());
		}
	}
}
