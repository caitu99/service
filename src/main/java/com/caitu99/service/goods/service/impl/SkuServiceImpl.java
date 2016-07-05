package com.caitu99.service.goods.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.goods.dao.SkuMapper;
import com.caitu99.service.goods.domain.Sku;
import com.caitu99.service.goods.service.SkuService;

/** 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: SkuServiceImpl 
 * @author xiongbin
 * @date 2015年11月24日 下午4:18:49 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class SkuServiceImpl implements SkuService {

	private final static Logger logger = LoggerFactory.getLogger(SkuServiceImpl.class);
	
	@Autowired
	private SkuMapper skuMapper;

	@Override
	public List<Sku> findSkuByItemId(Long itemId) {
		List<Sku> list = new ArrayList<Sku>();
		
		try {
			if(null == itemId){
				return list;
			}
			
			list = skuMapper.findSkuByItemId(itemId);
			
			return list;
		} catch (Exception e) {
			logger.error("根据商品ID查询SKU失败:" + e.getMessage(),e);
			return list;
		}
	}

}
