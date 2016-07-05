/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.transaction.dao.OrderItemMapper;
import com.caitu99.service.transaction.domain.OrderItem;
import com.caitu99.service.transaction.service.OrderItemService;

/**
 * 
 * @Description: (类职责详细描述,可空)
 * @ClassName: OrderItemServiceImpl
 * @author lhj
 * @date 2015年12月5日 上午11:09:36
 * @Copyright (c) 2015-2020 by caitu99
 */
@Service
public class OrderItemServiceImpl implements OrderItemService {

	@Autowired
	private OrderItemMapper orderItemMapper;

	/**
	 * 根据订单号获取细项
	 */
	@Override
	public List<OrderItem> listByOrderNo(String orderNo) {
		// TODO Auto-generated method stub
		return orderItemMapper.listByOrderNo(orderNo);
	}

}
