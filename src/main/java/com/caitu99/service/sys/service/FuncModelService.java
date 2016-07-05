/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.sys.service;

import java.util.List;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.sys.domain.Banner;
import com.caitu99.service.sys.domain.FuncModel;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: BannerService 
 * @author fangjunxiao
 * @date 2015年12月3日 上午10:39:42 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface FuncModelService {

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByModel 
	 * @param modelId
	 * @return
	 * @date 2016年5月10日 下午6:04:11  
	 * @author ws
	*/
	List<FuncModel> selectByModel(Integer modelId);
	
	
}
