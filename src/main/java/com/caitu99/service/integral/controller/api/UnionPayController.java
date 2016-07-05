/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.constants.IntegralConstants;
import com.caitu99.service.integral.controller.vo.UnionPayOrder;
import com.caitu99.service.transaction.api.ICompanyToThaw;
import com.caitu99.service.transaction.api.UnionPayRechargeDirectApi;
import com.caitu99.service.transaction.controller.vo.AccountResult;
import com.caitu99.service.transaction.controller.vo.UnionPaySmartOrder;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.user.api.IUserServiceApi;
import com.caitu99.service.user.domain.User;

/**
 *
 * @Description: (类职责详细描述,可空)
 * @ClassName: UnionPayController
 * @author lhj
 * @date 2015年11月26日 上午10:46:38
 * @Copyright (c) 2015-2020 by caitu99
 */
@Controller
@RequestMapping("/api/integral/unionpay")
public class UnionPayController {

	private final static Logger logger = LoggerFactory.getLogger(UnionPayController.class);

    @Autowired
    private IUserServiceApi userServiceApi;

    @Autowired
    private ICompanyToThaw companyToThaw;

    @Autowired
    private AccountService accountService;
    
    @Autowired
    private TransactionRecordService transactionRecordService;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    UnionPayRechargeDirectApi unionPayRechargeDirectApi;

    /**
     * charge integral
     * @param serialNo serial no
     * @param userid user id
     * @param mobile mobile
     * @param integral integral
     * @return result
     */
	@RequestMapping(value = "/recharge/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String recharge(Long clientId, String serialNo, Long userid, String mobile, Long integral) {
		ApiResult<String> result = new ApiResult<>();//  20160106 chencheng mod 成功返回交易编号
        User user = userServiceApi.addUser(mobile);

        //String uuid = UUID.randomUUID().toString().replace("-", "");
        Random random = new Random();
        //String orderNo = String.format("%s%016d%016d", uuid, userid, user.getId());
        String orderNo = "union"+random.nextInt(10)+System.currentTimeMillis()
        		+random.nextInt(10)+user.getId();
        
        if (serialNo.length() > 64) {
            return result.toJSONString(2071, "serialNo长度不符", "");
        }

        if (clientId != 1001) {
            return result.toJSONString(2074, "非法客户端", "");
        }

        logger.info("begin unionpay charge, orderno: {}, union: {}, mobile: {}, integral: {}", orderNo, userid, mobile, integral);
        if(appConfig.modeUnionPay.equals(IntegralConstants.MODE_UNION_PAY_DIRECT)){
        	//直冲模式
        	AccountResult accountResult = unionPayRechargeDirectApi.rechargeDirect(user.getId(), userid, integral, orderNo, serialNo, clientId);
        	if (accountResult.getCode() != 3101) {
                return result.toJSONString(accountResult.getCode(), accountResult.getResult(), "");
            }
        }else{
        	//冻结模式
	        try {
	            AccountResult accountResult = accountService.addFromUnionPay(user.getId(), userid, integral, orderNo, serialNo, clientId);
	            if (accountResult.getCode() != 3101) {
	                return result.toJSONString(accountResult.getCode(), accountResult.getResult(), "");
	            }
	        } catch (Exception e) {
	            logger.error("union charge failure, serialNo: {}, orderNo: {}, union: {}, mobile: {}, integral: {}", serialNo, orderNo, userid, mobile, integral, e);
	            return result.toJSONString(2072, "冻结资金失败", "");
	        } finally {
	            logger.info("union charge success, serialNo: {}, orderNo: {}, union: {}, mobile: {}, integral: {}", serialNo, orderNo, userid, mobile, integral);
	        }
	
	        try {
	            companyToThaw.addCompanyToThaw(mobile, orderNo, integral);
	        } catch (Exception e) {
	            logger.error("union charge thaw failure, serialNo: {}, orderNo: {}, union: {}, mobile: {}, integral: {}", serialNo, orderNo, userid, mobile, integral, e);
	            return result.toJSONString(2073, "划拨资金失败", "");
	        }
        }
        logger.info("end unionpay charge, orderno: {}, union: {}, mobile: {}, integral: {}", orderNo, userid, mobile, integral);

        return result.toJSONString(0, "财币充值成功", serialNo);
	}


	/**
	 * 银联充值交易查询
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryRechargeOrders 
	 * @param clientId
	 * @param mobile
	 * @param serialNo
	 * @param orderNo
	 * @return
	 * @date 2016年1月6日 下午2:33:49  
	 * @author ws
	 */
	@RequestMapping(value = "/order/query/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String queryRechargeOrders(Long clientId, String mobile, String serialNo, String orderNo, Pagination<UnionPayOrder> pagination) {
		ApiResult<Object> result = new ApiResult<>();
		if (null == clientId) {
            return result.toJSONString(2075, "非法请求参数", "");
        }
		if (StringUtils.isBlank(mobile) && StringUtils.isBlank(serialNo) && StringUtils.isBlank(orderNo)) {
            return result.toJSONString(2075, "手机号码/银联流水号/订单编号 至少有一个不为空", "");
        }
		if(null == pagination || 50 < pagination.getPageSize()){
			return result.toJSONString(2075, "非法请求参数,pageSize最大值50", "");
		}
		
		pagination = transactionRecordService.queryRechargeOrdersByPage(clientId, mobile, serialNo, orderNo, pagination);
		
		return result.toJSONString(0,"success",pagination,UnionPayOrder.class);
	}
	
	@RequestMapping(value = "/order/query/2.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String queryUnionPaySmartOrders(Long clientId, String mobile, String serialNo, Pagination<UnionPaySmartOrder> pagination) {
		ApiResult<Object> result = new ApiResult<>();
		if (null == clientId) {
            return result.toJSONString(2075, "非法请求参数", "");
        }
		if (StringUtils.isBlank(mobile) && StringUtils.isBlank(serialNo)) {
            return result.toJSONString(2075, "手机号码/银联流水号 至少有一个不为空", "");
        }
		if(null == pagination || 50 < pagination.getPageSize()){
			return result.toJSONString(2075, "非法请求参数,pageSize最大值50", "");
		}
		
		pagination = transactionRecordService.queryUnionPaySmartOrdersByPage(clientId, mobile, serialNo, pagination);
		
		return result.toJSONString(0,"success",pagination,UnionPayOrder.class);
	}
	
	/**
	 * 
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryRechargeOrderLists 
	 * @return
	 * @date 2016年1月15日 上午11:34:55  
	 * @author ws
	 */
	@RequestMapping(value = "/order/nos/query/1.0", produces = "application/json;charset=utf-8",method=RequestMethod.POST)
	@ResponseBody
	public String queryRechargeOrderByOrderNos(Long clientId, String orderNos){
		ApiResult<Object> result = new ApiResult<>();
		if (null == clientId || StringUtils.isBlank(orderNos)) {
            return result.toJSONString(2075, "非法请求参数", "");
        }
		
		List<String> orderList = new ArrayList<String>();
		try{
			orderList = JSON.parseArray(orderNos, String.class);
			int limitSize = appConfig.maxQueryLimit;
			if(orderList.size() > limitSize){
				return result.toJSONString(2075, "订单号数量超出单次最大查询限制数"+limitSize, "");
			}
			//System.out.println(JSON.toJSONString(orderList));
		}catch(Exception e){
			return result.toJSONString(2075,"非法请求参数");
		}
		
		List<UnionPayOrder> resultList = transactionRecordService.queryByOrderNos(clientId, orderList);
		
		List<UnionPayOrder> resultFilted = new ArrayList<UnionPayOrder>();
		resultFilted.addAll(resultList);
		for (String orderNo : orderList) {
			//System.out.println(orderNo);
			boolean hasNoValue = true;
			for (UnionPayOrder unionPayOrder : resultList) {
				if(orderNo.equals(unionPayOrder.getOrderNo())){
					hasNoValue = false;
				}
			}
			if(hasNoValue){
				UnionPayOrder newOrder = new UnionPayOrder();
				newOrder.setOrderNo(orderNo);
				
				resultFilted.add(newOrder);
			}
		}
		
		return result.toJSONString(0,"success",resultFilted);
		
	}
	
	@RequestMapping(value = "/order/nos/query/2.0", produces = "application/json;charset=utf-8",method=RequestMethod.POST)
	@ResponseBody
	public String queryUnionPaySmartOrderByOrderNos(Long clientId, String orderNos){
		ApiResult<Object> result = new ApiResult<>();
		if (null == clientId || StringUtils.isBlank(orderNos)) {
            return result.toJSONString(2075, "非法请求参数", "");
        }
		
		List<String> orderList = new ArrayList<String>();
		try{
			orderList = JSON.parseArray(orderNos, String.class);
			int limitSize = appConfig.maxQueryLimit;
			if(orderList.size() > limitSize){
				return result.toJSONString(2075, "订单号数量超出单次最大查询限制数"+limitSize, "");
			}
			//System.out.println(JSON.toJSONString(orderList));
		}catch(Exception e){
			return result.toJSONString(2075,"非法请求参数");
		}
		
		List<UnionPaySmartOrder> resultList = transactionRecordService.queryUnionPaySmartOrderByOrderNos(clientId, orderList);
		
		List<UnionPaySmartOrder> resultFilted = new ArrayList<UnionPaySmartOrder>();
		resultFilted.addAll(resultList);
		for (String orderNo : orderList) {
			//System.out.println(orderNo);
			boolean hasNoValue = true;
			for (UnionPaySmartOrder unionPaySmartOrder : resultList) {
				if(orderNo.equals(unionPaySmartOrder.getOrderNo())){
					hasNoValue = false;
				}
			}
			if(hasNoValue){
				UnionPaySmartOrder newOrder = new UnionPaySmartOrder();
				newOrder.setOrderNo(orderNo);
				
				resultFilted.add(newOrder);
			}
		}
		
		return result.toJSONString(0,"success",resultFilted);
		
	}
	
}
