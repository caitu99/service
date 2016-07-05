package com.caitu99.service.transaction.service;

import com.caitu99.service.transaction.controller.vo.AccountResult;

/**
 * 银联智慧（代充代付）
 * 
 * @Description: (类职责详细描述,可空)
 * @ClassName: UnionPaySmartService
 * @author Hongbo Peng
 * @date 2016年5月26日 上午11:05:11
 * @Copyright (c) 2015-2020 by caitu99
 */
public interface UnionPaySmartService {


	/**
	 * @Description: (银联充值发起代付请求，金额由银联账户转到用户账户，如果是代付就提现到用户银行卡)  
	 * @Title: daifu 
	 * @param userId		收款用户编号
	 * @param unionId		银联编号
	 * @param point			充值金额（分）
	 * @param unionNo		银联订单号
	 * @param tNo			流水号
	 * @param clientId		发起请求的客户端Id
	 * @param userAccName	用户姓名
	 * @param userAccNo		用户银行卡号
	 * @param model			方式：30.代充，31.代付
	 * @return
	 * @date 2016年5月26日 上午11:16:46  
	 * @author Hongbo Peng
	 */
	AccountResult rechargeDirect(Long userId, Long unionId, Long point,
			String unionNo, String tNo, Long clientId,String userAccName,String userAccNo,
			Integer model);
	
	/**
	 * @Description: (查询提现结果并处理业务)  
	 * @Title: queryAndHandle 
	 * @param transactioNumber
	 * @date 2016年5月27日 下午12:16:44  
	 * @author Hongbo Peng
	 */
	String queryAndHandle(String transactioNumber);
	
}
