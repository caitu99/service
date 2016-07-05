package com.caitu99.service.goods.service;

import java.util.List;

import com.caitu99.service.goods.domain.ReceiveStock;

public interface ReceiveStockService {

	List<ReceiveStock> selectReceiveStockByOrderNoAndUserId(Long userId,
			String orderNo);

	int updateBySelective(ReceiveStock receiveStock);

}
