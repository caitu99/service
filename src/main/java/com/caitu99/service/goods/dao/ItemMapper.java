package com.caitu99.service.goods.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.goods.domain.Item;

public interface ItemMapper {
	
    int deleteByPrimaryKey(Long itemId);

    int insert(Item record);

    Item selectByPrimaryKey(Long itemId);

    int update(Item record);

    Integer selectPageCount(Map<String, Object> map);
    
    List<Item> selectPageList(Map<String, Object> map);
    
    Item findItemByskuId(Long skuId);

    List<Item> selectByInventory();

}