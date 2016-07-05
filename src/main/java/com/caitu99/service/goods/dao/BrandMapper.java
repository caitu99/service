package com.caitu99.service.goods.dao;

import com.caitu99.service.goods.domain.Brand;

public interface BrandMapper {
	
    int deleteByPrimaryKey(Long brandId);

    int insert(Brand record);

    Brand selectByPrimaryKey(Long brandId);

    int update(Brand record);
}