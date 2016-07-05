/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.lianlianpay.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.lianlianpay.config.PartnerConfig;
import com.caitu99.service.lianlianpay.config.ServerURLConfig;
import com.caitu99.service.lianlianpay.conn.HttpRequestSimple;
import com.caitu99.service.lianlianpay.service.LianlianQueryService;
import com.caitu99.service.lianlianpay.utils.LLPayUtil;
import com.caitu99.service.transaction.controller.vo.AccountResult;
import com.caitu99.service.transaction.domain.Order;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.utils.XStringUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: LianlianQueryServiceImpl 
 * @author ws
 * @date 2016年6月14日 下午2:54:38 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class LianlianQueryServiceImpl implements LianlianQueryService {
	
	private static final Logger logger = LoggerFactory.getLogger(LianlianQueryServiceImpl.class);
	
	@Autowired
	private OrderService orderService;
	@Autowired
	private AccountService accountService;
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.lianlianpay.service.LianlianQueryService#getCardBin(java.lang.String)
	 */
	@Override
	public String getCardBin(String cardNo) {
		JSONObject reqObj = new JSONObject();
        reqObj.put("oid_partner", PartnerConfig.OID_PARTNER);
        reqObj.put("card_no", cardNo);
        reqObj.put("sign_type", PartnerConfig.SIGN_TYPE);
        String sign = LLPayUtil.addSign(reqObj, PartnerConfig.TRADER_PRI_KEY,
                PartnerConfig.MD5_KEY);
        reqObj.put("sign", sign);
        String reqJSON = reqObj.toString();
        logger.info("银行卡卡bin信息查询请求报文[" + reqJSON + "]");
        String resJSON = HttpRequestSimple.getInstance().postSendHttp(
                ServerURLConfig.QUERY_BANKCARD_URL, reqJSON);
        logger.info("银行卡卡bin信息查询响应报文[" + resJSON + "]");
        return resJSON;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.lianlianpay.service.LianlianQueryService#refund(java.lang.String, java.lang.String, java.lang.Double, java.lang.String, java.lang.String)
	 */
	@Override
	public String refund(String noRefund, String dtRefund, Double moneyRefund,
			String oidPaybill, String notifyUrl) {
		
		ApiResult result = new ApiResult();
		
		JSONObject reqObj = new JSONObject();
        reqObj.put("oid_partner", PartnerConfig.OID_PARTNER);
        reqObj.put("sign_type", PartnerConfig.SIGN_TYPE);
        reqObj.put("no_refund", noRefund);
        reqObj.put("dt_refund", dtRefund);
        reqObj.put("money_refund", moneyRefund);
        reqObj.put("oid_paybill", oidPaybill);
        reqObj.put("notify_url", notifyUrl);
        String sign = LLPayUtil.addSign(reqObj, PartnerConfig.TRADER_PRI_KEY,
                PartnerConfig.MD5_KEY);
        reqObj.put("sign", sign);
        String reqJSON = reqObj.toString();
        logger.info("退款请求报文请求报文[" + reqJSON + "]");
        String resJSON = HttpRequestSimple.getInstance().postSendHttp(
                ServerURLConfig.REFUND_URL, reqJSON);
        logger.info("退款请求报文响应报文[" + resJSON + "]");
        
        return resJSON;
	}
	
}
