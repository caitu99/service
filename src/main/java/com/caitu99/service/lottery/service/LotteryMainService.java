/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.lottery.service;

import java.util.Map;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.exception.LotteryException;
import com.caitu99.service.lottery.domain.LotteryOrder;
import com.caitu99.service.lottery.dto.LotteryPageDto;
import com.caitu99.service.lottery.vo.LotteryOrderVo;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: LotteryMain 
 * @author fangjunxiao
 * @date 2016年5月10日 下午5:57:52 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface LotteryMainService {
	
	/**
	 * 	获取免登陆访问URL
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: redirctLotteryUrl 
	 * @param userid
	 * @return
	 * @throws LotteryException
	 * @date 2016年5月13日 上午9:12:32  
	 * @author fangjunxiao
	 */
	String redirctLotteryUrl(Long userid) throws LotteryException;
	
	/**
	 * 	生成订单并扣分
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: createLotteryOrder 
	 * @param lotteryOrderVo
	 * @param map
	 * @return
	 * @throws LotteryException
	 * @date 2016年5月13日 上午9:13:01  
	 * @author fangjunxiao
	 */
	String createLotteryOrder(LotteryOrderVo lotteryOrderVo,Map<String,String> map) throws LotteryException;
	
	/**
	 * 检查用户帐户余额，并选择支付方案
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectAndCheckPrice 
	 * @param userid
	 * @return
	 * @throws LotteryException
	 * @date 2016年5月13日 上午9:13:28  
	 * @author fangjunxiao
	 */
	Map<String,String> selectAndCheckPrice(Long userid) throws LotteryException;
	
	/**
	 * 	是否重复订单
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkIsSame 
	 * @param outOrderId
	 * @return
	 * @throws LotteryException
	 * @date 2016年5月13日 上午9:15:06  
	 * @author fangjunxiao
	 */
	boolean checkIsSame(String outOrderId) throws LotteryException;
	
	
	/**
	 * 	修改订单状态，处理重复通知
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: changeLotteryOrderStatus 
	 * @param orderNo
	 * @return
	 * @throws LotteryException
	 * @date 2016年5月13日 上午10:00:12  
	 * @author fangjunxiao
	 */
	void changeLotteryOrderStatus(String orderNo) throws LotteryException;
	
	
	/**
	 * 出票失败，我方业务回滚
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: failLotteryOrder 
	 * @param outOrderId
	 * @return
	 * @throws LotteryException
	 * @date 2016年5月13日 上午11:59:27  
	 * @author fangjunxiao
	 */
	boolean failLotteryOrder(String outOrderId) throws LotteryException;
	
	
	/**
	 * 	彩票交易记录分页
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findPageByLottery 
	 * @param userId
	 * @param pagination
	 * @return
	 * @throws LotteryException
	 * @date 2016年5月16日 上午11:07:59  
	 * @author fangjunxiao
	 */
	Pagination<LotteryPageDto> findPageByLottery(Long userId,Pagination<LotteryPageDto> pagination) throws LotteryException; 


	LotteryOrder getLotteryOrder(String orderNo) throws LotteryException;
	
	
	LotteryOrder getLotteryOrderByOutOrderId(String outOrderId) throws LotteryException;
}
