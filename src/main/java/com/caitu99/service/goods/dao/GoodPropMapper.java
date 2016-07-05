package com.caitu99.service.goods.dao;

import java.util.List;

import com.caitu99.service.goods.domain.GoodProp;

public interface GoodPropMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GoodProp record);

    int insertSelective(GoodProp record);

    GoodProp selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoodProp record);

    int updateByPrimaryKey(GoodProp record);
    
    
    List<GoodProp> findPropByItemId(GoodProp record);
    
    List<GoodProp> findGroupByItemId(Long itemId);
}