package com.caitu99.service.goods.service;

import com.caitu99.service.goods.domain.Brand;

public interface BrandService {
	
	/**
	 * 根据主键查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByPrimaryKey 
	 * @param brandId
	 * @return
	 * @date 2015年11月25日 下午12:05:01  
	 * @author xiongbin
	 */
	Brand selectByPrimaryKey(Long brandId);
}
