/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.controller.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.caitu99.service.activities.service.NewbieActivityPayService;
import com.caitu99.service.transaction.api.IntegralRechargeHandler;
import com.caitu99.service.utils.sdk.SDKConstants;
import com.caitu99.service.utils.sdk.SDKUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IntegralRechargePublicController 
 * @author ws
 * @date 2016年1月8日 下午2:54:22 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/public/integral/recharge/")
public class IntegralRechargePublicController {

	private static final Logger logger = LoggerFactory
			.getLogger(IntegralRechargePublicController.class);

	@Autowired
	IntegralRechargeHandler integralRechargeHandler;
	@Autowired
	private NewbieActivityPayService newbieActivityPayService;
	
	/**
	 * 银联处理支付成功后返回
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: backRcvResponse 
	 * @param req
	 * @param resp
	 * @throws IOException
	 * @date 2015年12月31日 上午10:55:21  
	 * @author ws
	 */
	@RequestMapping(value="unionpay/backresponse/1.0", produces="application/json;charset=utf-8")
	public void backRcvResponse(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
		logger.info("BackRcvResponse接收后台通知开始");

		req.setCharacterEncoding("ISO-8859-1");
		String encoding = req.getParameter(SDKConstants.param_encoding);
		// 获取银联通知服务器发送的后台通知参数
		Map<String, String> reqParam = SDKUtil.getAllRequestParam(req);
		//LogUtil.printRequestLog(reqParam);

		Map<String, String> valideData = null;
		if (null != reqParam && !reqParam.isEmpty()) {
			Iterator<Entry<String, String>> it = reqParam.entrySet().iterator();
			valideData = new HashMap<String, String>(reqParam.size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				value = new String(value.getBytes("ISO-8859-1"), encoding);
				valideData.put(key, value);
			}
		}

		//重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
		if (!SDKUtil.validate(valideData, encoding)) {
			logger.info("验证签名结果[失败].");
			//验签失败，需解决验签问题
			
		} else {
			logger.info("验证签名结果[成功].");
			//【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态
			String orderId =valideData.get("orderId"); //获取后台通知的数据，其他字段也可用类似方式获取
			String queryId = valideData.get("queryId");//银联查询Id
			//String respCode =valideData.get("respCode"); //获取应答码，收到后台通知了respCode的值一般是00，可以不需要根据这个应答码判断。
			
			integralRechargeHandler.paySuccessDo(orderId,queryId);
			
		}
		logger.info("BackRcvResponse接收后台通知结束");
		//返回给银联服务器http 200  状态码
		resp.getWriter().print("ok");
	}
	

	@RequestMapping(value="activities/newbie/pay/1.0", produces="application/json;charset=utf-8")
	public void activitiesNewbiePay(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		logger.info("BackRcvResponse接收后台通知开始");

		req.setCharacterEncoding("ISO-8859-1");
		String encoding = req.getParameter(SDKConstants.param_encoding);
		// 获取银联通知服务器发送的后台通知参数
		Map<String, String> reqParam = SDKUtil.getAllRequestParam(req);
		//LogUtil.printRequestLog(reqParam);

		Map<String, String> valideData = null;
		if (null != reqParam && !reqParam.isEmpty()) {
			Iterator<Entry<String, String>> it = reqParam.entrySet().iterator();
			valideData = new HashMap<String, String>(reqParam.size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				value = new String(value.getBytes("ISO-8859-1"), encoding);
				valideData.put(key, value);
			}
		}

		//重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
		if (!SDKUtil.validate(valideData, encoding)) {
			logger.info("验证签名结果[失败].");
			//验签失败，需解决验签问题
			
		} else {
			logger.info("验证签名结果[成功].");
			//【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态
			String orderId =valideData.get("orderId"); //获取后台通知的数据，其他字段也可用类似方式获取
			String queryId = valideData.get("queryId");//银联查询Id
			//String respCode =valideData.get("respCode"); //获取应答码，收到后台通知了respCode的值一般是00，可以不需要根据这个应答码判断。
			
			newbieActivityPayService.paySuccessDo(null,orderId,queryId,true);
		}
		logger.info("BackRcvResponse接收后台通知结束");
		//返回给银联服务器http 200  状态码
		resp.getWriter().print("ok");
	}
	
	
}
