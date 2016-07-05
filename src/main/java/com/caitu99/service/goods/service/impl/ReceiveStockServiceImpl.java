/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.goods.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.goods.dao.ReceiveStockMapper;
import com.caitu99.service.goods.domain.ReceiveStock;
import com.caitu99.service.goods.service.ReceiveStockService;

/**
 * 
 * @Description: (类职责详细描述,可空)
 * @ClassName: ReceiveStockServiceImpl
 * @author lhj
 * @date 2015年12月5日 下午5:33:42
 * @Copyright (c) 2015-2020 by caitu99
 */
@Service
public class ReceiveStockServiceImpl implements ReceiveStockService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.caitu99.service.goods.service.ReceiveStockService#
	 * selectReceiveStockByOrderNoAndUserId(java.lang.Long, java.lang.String)
	 */
	@Autowired
	private ReceiveStockMapper receiveStockMapper;

	@Override
	public List<ReceiveStock> selectReceiveStockByOrderNoAndUserId(Long userId,
			String orderNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_id", userId);
		map.put("remote_id", orderNo);
		return receiveStockMapper.selectReceiveStockByOrderNoAndUserId(map);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.caitu99.service.goods.service.ReceiveStockService#updateBySelective
	 * (com.caitu99.service.goods.domain.ReceiveStock)
	 */
	@Override
	public int updateBySelective(ReceiveStock receiveStock) {
		// TODO Auto-generated method stub
		return receiveStockMapper.updateByPrimaryKeySelective(receiveStock);
	}

}
