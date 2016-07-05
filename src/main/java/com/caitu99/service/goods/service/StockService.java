package com.caitu99.service.goods.service;


import com.caitu99.service.base.Pagination;
import com.caitu99.service.goods.domain.CouponReceiveStock;

public interface StockService {
	
	/**
	 * 根据商品ID和SKU ID查询库存
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectCount 
	 * @param itemId		商品ID
	 * @param skuId			sku ID
	 * @return
	 * @date 2015年11月25日 下午4:55:11  
	 * @author xiongbin
	 */
	Integer selectCount(Long itemId,Long skuId);
}
