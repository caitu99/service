/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.goods.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.exception.ItemException;
import com.caitu99.service.goods.controller.api.ItemController;
import com.caitu99.service.goods.dao.GoodPropMapper;
import com.caitu99.service.goods.domain.GoodProp;
import com.caitu99.service.goods.service.GoodPropService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: GoodPropServiceImpl 
 * @author fangjunxiao
 * @date 2016年1月5日 上午10:25:44 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class GoodPropServiceImpl implements GoodPropService{

	private final static Logger logger = LoggerFactory.getLogger(ItemController.class);
	
	@Autowired
	private GoodPropMapper goodPropDao;
	
	
	@Override
	public Map<String,List<GoodProp>> findPropByItemId(Long itemId) throws ItemException{
		try {
			
			GoodProp querygp = new GoodProp();
			querygp.setItemId(itemId);
			querygp.setUseType(1);
			List<GoodProp> propList = goodPropDao.findPropByItemId(querygp);
			List<GoodProp> groupList  = goodPropDao.findGroupByItemId(itemId);
			Map<String,List<GoodProp>> map = new HashMap<String, List<GoodProp>>();
			for (GoodProp s : groupList) {
				List<GoodProp> propMap = new ArrayList<GoodProp>();
				for (GoodProp gp : propList) {
					if(gp.getGroupList().equals(s.getGroupList())){
						propMap.add(gp);
					}
				}
				map.put(s.getName(), propMap);
			}
			return map;
		} catch (Exception e) {
			logger.error("商品属性查询失败:" + e.getMessage());
			throw new ItemException(-1, "商品属性查询失败");
		}
	}


	@Override
	public List<GoodProp> findPropByItemId(Long itemId, Integer userType)
			throws ItemException {
		try {
			GoodProp querygp = new GoodProp();
			querygp.setItemId(itemId);
			querygp.setUseType(userType);
			List<GoodProp> propList = goodPropDao.findPropByItemId(querygp);
			return propList;
		} catch (Exception e) {
			logger.error("商品属性查询失败:" + e.getMessage());
			throw new ItemException(-1, "商品属性查询失败");
		}
	}

}
