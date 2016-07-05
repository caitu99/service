/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.free.service;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.exception.FreeTradeException;
import com.caitu99.service.free.domain.FreeTrade;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: FreeTradeService 
 * @author Hongbo Peng
 * @date 2016年1月20日 下午3:40:19 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface FreeTradeService {

	/**
	 * @Description: (分页查询)  
	 * @Title: selectPage 
	 * @param pagination
	 * @return
	 * @throws FreeTradeException
	 * @date 2016年1月20日 下午3:44:44  
	 * @author Hongbo Peng
	 */
	Pagination<FreeTrade> selectPage(Pagination<FreeTrade> pagination,FreeTrade freeTrade) throws FreeTradeException;

	/**
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByPrimaryKey 
	 * @param freetradeid
	 * @return
	 * @date 2016年2月4日 下午12:11:38  
	 * @author xiongbin
	*/
	FreeTrade selectByPrimaryKey(Long freetradeid);
}
