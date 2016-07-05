/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.user.dao.AddressMapper;
import com.caitu99.service.user.domain.Address;
import com.caitu99.service.user.service.AddressService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AddressServiceImpl 
 * @author ws
 * @date 2016年1月21日 下午3:30:28 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	AddressMapper addressMapper;
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.free.service.AddressService#selectByUserId(java.lang.Long)
	 */
	@Override
	public Address selectByUserId(Long userId) {
		return addressMapper.selectByUserId(userId);
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.user.service.AddressService#insert(com.caitu99.service.user.domain.Address)
	 */
	@Override
	public void insert(Address addressNew) {
		addressMapper.insert(addressNew);
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.user.service.AddressService#updateByPrimaryKey(com.caitu99.service.user.domain.Address)
	 */
	@Override
	public void updateByPrimaryKey(Address address) {
		addressMapper.updateByPrimaryKey(address);
	}
	
}
