/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.free.service;

import java.util.List;

import com.caitu99.service.free.domain.FreeTradePlatform;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: FreeTradePlatformService 
 * @author Hongbo Peng
 * @date 2016年1月20日 上午10:02:25 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface FreeTradePlatformService {

	List<FreeTradePlatform> selectList();
	
	/**
	 * 根据版本号查询列表
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: listByVersion 
	 * @param version	版本号
	 * @return
	 * @date 2016年2月19日 上午9:40:08  
	 * @author xiongbin
	 */
	List<FreeTradePlatform> listByVersion(String version);
	
    FreeTradePlatform selectByPrimaryKey(Long id);
}
