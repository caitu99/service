package com.caitu99.service.goods.service;

import java.util.List;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.exception.ItemException;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.dto.ItemDto;

public interface ItemService {
	
	/**
	 * 分页查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findPageItem 
	 * @param item				查询条件
	 * @param pagination		分页对象
	 * @return
	 * @throws ItemException
	 * @date 2015年11月24日 下午4:17:50  
	 * @author xiongbin
	 */
	Pagination<Item> findPageItem(ItemDto item,Pagination<Item> pagination,String[] typeId2s) throws ItemException;
	
	/**
	 * 根据主键查询商品
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByPrimaryKey 
	 * @param itemId		商品ID
	 * @return
	 * @date 2015年11月24日 下午8:09:50  
	 * @author xiongbin
	 */
	Item selectByPrimaryKey(Long itemId);
	
	
	Item findItemByskuId(Long skuId);
	
	
}
