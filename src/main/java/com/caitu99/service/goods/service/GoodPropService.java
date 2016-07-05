/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.goods.service;

import java.util.List;
import java.util.Map;

import com.caitu99.service.exception.ItemException;
import com.caitu99.service.goods.domain.GoodProp;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: GoodPropService 
 * @author fangjunxiao
 * @date 2016年1月5日 上午10:25:30 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface GoodPropService {

	
	Map<String,List<GoodProp>> findPropByItemId(Long itemId) throws ItemException;
	
	/**
	 * 	查询商品属性  userType 0:隐藏属性，1：显示属性
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findPropByItemId 
	 * @param itemId
	 * @param userType
	 * @return
	 * @throws ItemException
	 * @date 2016年1月14日 上午10:30:14  
	 * @author fangjunxiao
	 */
	List<GoodProp> findPropByItemId(Long itemId,Integer userType) throws ItemException;
	
}
