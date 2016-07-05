/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.user.service;

import com.caitu99.service.user.domain.Address;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AddressService 
 * @author ws
 * @date 2016年1月21日 下午3:30:08 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface AddressService {

	/**
	 * 获取用户默认收货地址	
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUserId 
	 * @param userId
	 * @return
	 * @date 2016年1月21日 下午3:31:51  
	 * @author ws
	*/
	Address selectByUserId(Long userId);

	/**
	 * 新增收货地址
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: insert 
	 * @param addressNew
	 * @date 2016年1月21日 下午4:40:53  
	 * @author ws
	*/
	void insert(Address addressNew);

	/**
	 * 更新收货地址
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: updateByPrimaryKey 
	 * @param address
	 * @date 2016年1月21日 下午4:42:11  
	 * @author ws
	*/
	void updateByPrimaryKey(Address address);

}
