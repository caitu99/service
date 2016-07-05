/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.goods.service;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.exception.ItemException;
import com.caitu99.service.goods.dto.TemplateDto;


/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: Group 
 * @author fangjunxiao
 * @date 2015年12月31日 上午9:51:10 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface GroupService {

	
	
	
	
	String findPageGroup(TemplateDto temp,Pagination<TemplateDto> pagination) throws ItemException; 
}
