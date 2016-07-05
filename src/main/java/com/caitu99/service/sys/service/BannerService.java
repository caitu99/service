/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.sys.service;

import java.util.List;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.sys.domain.Banner;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: BannerService 
 * @author fangjunxiao
 * @date 2015年12月3日 上午10:39:42 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface BannerService {
	
	
	
	List<Banner>  getRotaryImg(Integer type); 
	 
	String findPageBanner(Banner banner,Pagination<Banner> pagination); 

}
