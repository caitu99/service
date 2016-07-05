/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.life;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;

import com.caitu99.service.life.domain.Product;
import com.caitu99.service.life.service.ProductService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ProductControllerTest 
 * @author lawrence
 * @date 2015年11月3日 下午1:38:14 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ProductServiceTest extends AbstractJunit {
	
	@Autowired
	private ProductService productService;

	@Test
	public void testSelectAll(){
		List<Product> list = productService.selectAll();
		Assert.assertNotNull(list);
		for (Product product : list) {
			Assert.assertNotNull(product);
			System.out.println(product.getProduct());
		}
	}

}
