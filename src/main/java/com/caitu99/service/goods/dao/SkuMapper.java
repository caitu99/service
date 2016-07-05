package com.caitu99.service.goods.dao;

import java.util.List;

import com.caitu99.service.goods.domain.Sku;

public interface SkuMapper {
	
    int deleteByPrimaryKey(Long skuId);

    int insert(Sku record);

    Sku selectByPrimaryKey(Long skuId);

    int update(Sku record);
    
    List<Sku> findSkuByItemId(Long itemId);
    
    int updateByPrimaryKeySelective(Sku record);

    int updateByPrimaryKey(Sku record);
    
    Long findAllByItemIdAndVersion(Sku record);
}