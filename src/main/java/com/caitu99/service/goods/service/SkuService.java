package com.caitu99.service.goods.service;

import java.util.List;

import com.caitu99.service.goods.domain.Sku;

public interface SkuService {
	
	/**
	 * 根据商品查询SKU
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByPrimaryKey 
	 * @param itemId		商品ID
	 * @return
	 * @date 2015年11月24日 下午8:09:50  
	 * @author xiongbin
	 */
	List<Sku> findSkuByItemId(Long itemId);
}
