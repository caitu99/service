/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.transaction.dao.OrderAddressMapper;
import com.caitu99.service.transaction.domain.OrderAddress;
import com.caitu99.service.transaction.service.OrderAddressService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: OrderAddressServiceImpl 
 * @author ws
 * @date 2016年1月21日 下午4:02:40 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class OrderAddressServiceImpl implements OrderAddressService {

	@Autowired
	OrderAddressMapper orderAddressMapper;
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.service.OrderAddressService#insert(com.caitu99.service.transaction.domain.OrderAddress)
	 */
	@Override
	public void insert(OrderAddress orderAddress) {
		orderAddressMapper.insert(orderAddress);
	}

}
