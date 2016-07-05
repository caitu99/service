/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.service;

import com.caitu99.service.transaction.domain.OrderAddress;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: OrderAddressService 
 * @author ws
 * @date 2016年1月21日 下午4:02:25 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface OrderAddressService {

	/**
	 * 	新增订单收货地址
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: insert 
	 * @param orderAddress
	 * @date 2016年1月21日 下午4:03:54  
	 * @author ws
	*/
	void insert(OrderAddress orderAddress);

}
