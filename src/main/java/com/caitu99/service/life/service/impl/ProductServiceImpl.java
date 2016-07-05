package com.caitu99.service.life.service.impl;

import java.util.List;

import com.caitu99.service.life.dao.ProductMapper;
import com.caitu99.service.life.domain.Product;
import com.caitu99.service.life.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductMapper productMapper;

	@Override
	public int deleteByPrimaryKey(Long id) {
		return productMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(Product record) {
		return productMapper.insert(record);
	}

	@Override
	public int insertSelective(Product record) {
		return productMapper.insertSelective(record);
	}

	@Override
	public Product selectByPrimaryKey(Long id) {
		return productMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(Product record) {
		return productMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(Product record) {
		return productMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<Product> selectAll() {
		return productMapper.selectAll();
	}

}
