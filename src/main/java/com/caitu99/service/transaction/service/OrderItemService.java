package com.caitu99.service.transaction.service;

import java.util.List;

import com.caitu99.service.transaction.domain.OrderItem;

/**
 * 
 * @Description: (类职责详细描述,可空)
 * @ClassName: OrderService
 * @author tsai
 * @date 2015年11月25日 上午10:48:17
 * @Copyright (c) 2015-2020 by caitu99
 */
public interface OrderItemService {

	List<OrderItem> listByOrderNo(String orderNo);

}
