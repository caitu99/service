package com.caitu99.service.backstage.service.impl;

import com.caitu99.service.backstage.dao.SalesmanPushMapper;
import com.caitu99.service.backstage.dao.SalesmanPushRelationMapper;
import com.caitu99.service.backstage.domain.SalesmanPush;
import com.caitu99.service.backstage.service.SalesmanPushService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liuzs on 2016/3/14.
 */
@Service
public class SalesmanPushServiceImpl implements SalesmanPushService {

    @Autowired
    SalesmanPushMapper salesmanPushMapper;
    
    @Autowired
    SalesmanPushRelationMapper salesmanPushRelationMapper;

    @Override
    public SalesmanPush selectByPhone(String phone) {
        return salesmanPushMapper.selectByPhone(phone);
    }

	@Override
	public Integer countIsManager(String phone) {
		return salesmanPushRelationMapper.countIsManager(phone);
	}
}
