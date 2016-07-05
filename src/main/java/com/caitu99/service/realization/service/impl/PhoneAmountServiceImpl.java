/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.realization.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.AppConfig;
import com.caitu99.service.realization.dao.PhoneAmountMapper;
import com.caitu99.service.realization.domain.PhoneAmount;
import com.caitu99.service.realization.service.PhoneAmountService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: PhoneAmountServiceImpl 
 * @author ws
 * @date 2016年4月13日 下午4:33:44 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class PhoneAmountServiceImpl implements PhoneAmountService {
	@Autowired
	PhoneAmountMapper phoneAmountMapper;
    @Autowired
    private AppConfig appConfig;
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.realization.service.PhoneAmountService#getPhoneAmountList()
	 */
	@Override
	public List<PhoneAmount> getPhoneAmountList() {

        Integer scale = appConfig.phoneRechargeScale;
        List<PhoneAmount> amountList = phoneAmountMapper.selectAll();
        for (PhoneAmount phoneAmount : amountList) {
        	phoneAmount.setScale(scale);
		}
		return amountList;
	}
	
}
