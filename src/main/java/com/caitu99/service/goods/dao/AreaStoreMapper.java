package com.caitu99.service.goods.dao;

import com.caitu99.service.goods.domain.AreaStore;

import java.util.List;
import java.util.Map;

public interface AreaStoreMapper {
	
    int deleteByPrimaryKey(Long areaStoreId);

    int insert(AreaStore record);

    AreaStore selectByPrimaryKey(Long areaStoreId);

    int update(AreaStore record);

    List<AreaStore> selectByBrandId(Long brandId);

    List<AreaStore> selectByBrandIdPageList(Map map);

    Integer selectCntByBrandIdPageList(Map map);
}