package com.caitu99.service.goods.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.goods.dao.BrandMapper;
import com.caitu99.service.goods.domain.Brand;
import com.caitu99.service.goods.service.BrandService;

/** 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: BrandServiceImpl 
 * @author xiongbin
 * @date 2015年11月24日 下午4:18:49 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class BrandServiceImpl implements BrandService {

	private final static Logger logger = LoggerFactory.getLogger(BrandServiceImpl.class);
	
	@Autowired
	private BrandMapper brandMapper;

	@Override
	public Brand selectByPrimaryKey(Long brandId) {
		try {
			if(null == brandId){
				return null;
			}
			
			Brand brand = brandMapper.selectByPrimaryKey(brandId);
			
			return brand;
		} catch (Exception e) {
			logger.error("根据主键查询品牌出错:" + e.getMessage(),e);
			return null;
		}
	}
}
