package com.caitu99.service.goods.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.goods.domain.CouponReceiveStock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.goods.dao.StockMapper;
import com.caitu99.service.goods.service.StockService;

/** 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: StockServiceImpl 
 * @author xiongbin
 * @date 2015年11月24日 下午4:18:49 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class StockServiceImpl implements StockService {

	private final static Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);
	
	@Autowired
	private StockMapper stockMapper;

	@Override
	public Integer selectCount(Long itemId, Long skuId) {
		try {
			if(null==itemId || null==skuId){
				return 0;
			}
			
			Map<String,Object> map = new HashMap<String,Object>(2);
			map.put("itemId", itemId);
			map.put("skuId", skuId);
			
			Integer count = stockMapper.selectCount(map);
			
			return count;
		} catch (Exception e) {
			logger.error("查询库存失败:" + e.getMessage(),e);
			return 0;
		}
	}
}
