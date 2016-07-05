/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.merchant.service;

import java.util.List;

import com.caitu99.service.merchant.domain.ProxyTransaction;
import com.caitu99.service.merchant.domain.ProxyTransactionItem;
import com.caitu99.service.merchant.domain.SmspayRecord;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ProxyTransactionService 
 * @author ws
 * @date 2016年6月20日 下午5:56:44 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface ProxyTransactionService {

	/**
	 * 获取我的下级收分结算数据	
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: querySetteData 
	 * @param userId
	 * @param year
	 * @param week
	 * @param type	结算方式：1与下级结算，2与上级计算
	 * @date 2016年6月21日 上午10:23:46  
	 * @author ws
	 * @param empUserId 
	*/
	ProxyTransaction querySetteData(Long userId, Long empUserId, Integer year, Integer week, Integer type);

	/**
	 * 获取我的收分结算数据	
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryMySetteData 
	 * @param userId
	 * @param year
	 * @param week
	 * @param type	
	 * @return
	 * @date 2016年6月21日 下午3:03:24  
	 * @author ws
	*/
	ProxyTransaction queryMySetteData(Long userId, Integer year, Integer week,
			int type);

	/**
	 * 获取结算明细	
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getProxyTranItem 
	 * @param userId
	 * @param year
	 * @param week
	 * @param empUserId
	 * @param settleId
	 * @return
	 * @date 2016年6月21日 下午4:23:14  
	 * @author ws
	 * @throws Exception 
	*/
	List<ProxyTransactionItem> queryProxyTranItem(Long userId, Integer year,
			Integer week, Long empUserId, Long settleId) throws Exception;

	/**
	 * 获取用户交易记录	
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryProxyTranRecord 
	 * @param userId
	 * @param empUserId
	 * @param year
	 * @param week
	 * @param platformId
	 * @return
	 * @date 2016年6月21日 下午5:06:10  
	 * @author ws
	*/
	List<SmspayRecord> queryProxyTranRecord(Long userId, Long empUserId,
			Integer year, Integer week, Long platformId);

	/**
	 * 发起结算	
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: proposeSettle 
	 * @param userId
	 * @param empUserId
	 * @param year
	 * @param week
	 * @date 2016年6月21日 下午5:18:34  
	 * @author ws
	 * @return 
	 * @throws Exception 
	*/
	String proposeSettle(Long userId, Long empUserId, Integer year, Integer week) throws Exception;

	/**
	 * 确认结算	
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: confirmSettle 
	 * @param userId
	 * @param settleId
	 * @date 2016年6月21日 下午5:51:55  
	 * @author ws
	 * @return 
	*/
	String confirmSettle(Long userId, Long settleId);

	/**
	 * 	检查用户是否存在未结算分
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkSettle 
	 * @param userId
	 * @return
	 * @date 2016年6月22日 下午3:44:13  
	 * @author ws
	*/
	boolean checkSettle(Long userId);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: querySetteDataIterate 
	 * @param userId
	 * @param empUserId
	 * @param year
	 * @param week
	 * @param type
	 * @return
	 * @date 2016年6月23日 下午6:22:07  
	 * @author ws
	 * @throws Exception 
	*/
	ProxyTransaction querySetteDataIterate(Long userId, Long empUserId,
			Integer year, Integer week, int type) throws Exception;


}
