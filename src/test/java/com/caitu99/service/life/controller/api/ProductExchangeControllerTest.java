/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.life.controller.api;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ProductExchangeControllerTest 
 * @author ws
 * @date 2015年11月3日 上午11:54:18 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ProductExchangeControllerTest extends AbstractJunit{

	@Autowired
	ProductExchangeController ctrl;
	
	
	/**
	 * Test method for {@link com.caitu99.service.life.controller.api.ProductExchangeController#check(java.lang.Long, java.lang.Long)}.
	 */
	@Test
	public void testCheck() {
		/*Long userId = 0L;
		Long productId = 0L;
		try {
			//用户不存在情况
			String result = ctrl.check(userId, productId);
			
		} catch (UserNotFoundException e) {
			Assert.assertTrue(true);
		}
		
		userId=9L;//9积分
		try {
			//商品不存在情况
			String result = ctrl.check(userId, productId);
			StringUtils.contains("2205", result);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
		
		productId = 1L;//1积分
		try {
			//判断用户财币足够
			String result = ctrl.check(userId, productId);
			StringUtils.contains("2202", result);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
		
		userId = 162L;//0积分
		try {
			//判断用户财币足够
			String result = ctrl.check(userId, productId);
			StringUtils.contains("2202", result);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
		
		productId = 3L;
		userId = 9L;
		try {
			//激活码不足
			String result = ctrl.check(userId, productId);
			StringUtils.contains("2202", result);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}*/
		
	}
/*
	*//**
	 * Test method for {@link com.caitu99.service.life.controller.api.ProductExchangeController#exchange(java.lang.Long, java.lang.Long, java.lang.String)}.
	 *//*
	@Test
	public void testExchange() {
		fail("Not yet implemented");
	}

	*//**
	 * Test method for {@link com.caitu99.service.life.controller.api.ProductExchangeController#history(java.lang.Long)}.
	 *//*
	@Test
	public void testHistory() {
		fail("Not yet implemented");
	}

	*//**
	 * Test method for {@link com.caitu99.service.life.controller.api.ProductExchangeController#checkEdaixi(java.lang.Long, java.lang.Long)}.
	 *//*
	@Test
	public void testCheckEdaixi() {
		fail("Not yet implemented");
	}

	*//**
	 * Test method for {@link com.caitu99.service.life.controller.api.ProductExchangeController#exchangeEdaixi(java.lang.Long, java.lang.Long, java.lang.String)}.
	 *//*
	@Test
	public void testExchangeEdaixi() {
		fail("Not yet implemented");
	}*/

}
