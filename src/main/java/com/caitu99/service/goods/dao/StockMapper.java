package com.caitu99.service.goods.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.domain.Stock;
import com.caitu99.service.goods.domain.CouponReceiveStock;

public interface StockMapper {

    int deleteByPrimaryKey(Long stockId);

    int insert(Stock record);

    Stock selectByPrimaryKey(Long stockId);

    int update(Stock record);

    Integer selectCount(Map<String, Object> map);

    List<Stock> findUnloadInventory(Map<String,Object> map);

    Integer countInventory(Map<String,Object> map);
    
    List<Stock> findInventoryBypageNum(Map<String,Object> map);

    int updateByPrimaryKeySelective(Stock stock);

    List<Stock> selectByInventory(Item itemId);
}
