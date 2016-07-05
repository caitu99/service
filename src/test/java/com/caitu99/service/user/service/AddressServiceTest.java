/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.user.service;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.user.domain.Address;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AddressServiceTest 
 * @author ws
 * @date 2016年1月21日 下午4:56:22 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class AddressServiceTest extends AbstractJunit{
	
	@Autowired
	AddressService addressService;
	/**
	 * Test method for {@link com.caitu99.service.user.service.impl.AddressServiceImpl#selectByUserId(java.lang.Long)}.
	 */
	@Test
	public void testSelectByUserId() {
		Address result = addressService.selectByUserId(1L);
		System.out.println(result);
	}

	/**
	 * Test method for {@link com.caitu99.service.user.service.impl.AddressServiceImpl#insert(com.caitu99.service.user.domain.Address)}.
	 */
	@Test
	public void testInsert() {
		Date date = new Date();
		Address addressNew = new Address();
		addressNew.setArea("西湖区");
		addressNew.setCity("杭州");
		addressNew.setCreateTime(date );
		addressNew.setDetailed("天目山路网新大厦");
		addressNew.setMobile("15858284090");
		addressNew.setName("cctest");
		addressNew.setProvince("浙江");
		addressNew.setIsDefault(1);//默认收货地址
		addressNew.setSort(1);
		addressNew.setUpdateTime(date);
		addressNew.setUserId(1L);
		addressNew.setZipCode("");
		//addressService.insert(addressNew);
	}

	/**
	 * Test method for {@link com.caitu99.service.user.service.impl.AddressServiceImpl#updateByPrimaryKey(com.caitu99.service.user.domain.Address)}.
	 */
	@Test
	public void testUpdateByPrimaryKey() {
		Date date = new Date();
		Address addressNew = addressService.selectByUserId(1L);
		if(null == addressNew){
			return;
		}
		addressNew.setArea("西湖区");
		addressNew.setCity("杭州");
		addressNew.setCreateTime(date );
		addressNew.setDetailed("caitu99天目山路网新大厦");
		addressNew.setMobile("15858284090");
		addressNew.setName("cctest");
		addressNew.setProvince("浙江");
		addressNew.setUpdateTime(date);
		addressNew.setUserId(1L);
		addressNew.setZipCode("430018");
		//addressService.updateByPrimaryKey(addressNew);
	}

}
