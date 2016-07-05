package com.caitu99.service.life.dao;

import java.util.List;

import com.caitu99.service.life.domain.Product;
import com.caitu99.platform.dao.base.func.IEntityDAO;

public interface ProductMapper extends IEntityDAO<Product, Product> {
    int deleteByPrimaryKey(Long id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectAll();


}