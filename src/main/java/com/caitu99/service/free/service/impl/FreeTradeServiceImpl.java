/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.free.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.exception.FreeTradeException;
import com.caitu99.service.free.dao.FreeTradeMapper;
import com.caitu99.service.free.domain.FreeTrade;
import com.caitu99.service.free.service.FreeTradeService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: FreeTradeServiceImpl 
 * @author Hongbo Peng
 * @date 2016年1月20日 下午3:47:42 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class FreeTradeServiceImpl implements FreeTradeService {

	@Autowired
	private FreeTradeMapper freeTradeMapper;
	
	@Override
	public Pagination<FreeTrade> selectPage(Pagination<FreeTrade> pagination,
			FreeTrade freeTrade) throws FreeTradeException {
		if(null == freeTrade || null == pagination){
			return pagination;
		}
		Integer totalRow = freeTradeMapper.selectCount(freeTrade);
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("freeTrade", freeTrade);
		map.put("start", pagination.getStart());
		map.put("pageSize", pagination.getPageSize());
		List<FreeTrade> datas = freeTradeMapper.selectPage(map);
		pagination.setDatas(datas);
		pagination.setTotalRow(totalRow);
		return pagination;
	}

	@Override
	public FreeTrade selectByPrimaryKey(Long freetradeid) {
		return freeTradeMapper.selectByPrimaryKey(freetradeid);
	}

}
