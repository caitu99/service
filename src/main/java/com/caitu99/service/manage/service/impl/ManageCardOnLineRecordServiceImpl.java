package com.caitu99.service.manage.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.manage.dao.ManageCardOnLineRecordMapper;
import com.caitu99.service.manage.domain.ManageCardOnLineRecord;
import com.caitu99.service.manage.service.ManageCardOnLineRecordService;
import com.caitu99.service.user.domain.UserAuth;


@Service
public class ManageCardOnLineRecordServiceImpl implements ManageCardOnLineRecordService {

	private final static Logger logger = LoggerFactory.getLogger(ManageCardOnLineRecordServiceImpl.class);
	
	@Autowired
	private ManageCardOnLineRecordMapper manageCardOnLineRecordMapper;

	@Override
	public void insert(UserAuth userAuth, Long bankCardId) {
		Date now = new Date();
		
		ManageCardOnLineRecord record = new ManageCardOnLineRecord();
		record.setBankCardId(bankCardId);
		record.setGmtCreate(now);
		record.setGmtModify(now);
		record.setIdentityCard(userAuth.getAccId());
		record.setName(userAuth.getAccName());
		record.setPhone(userAuth.getMobile());
		record.setUserId(userAuth.getUserId());
		record.setStatus(ManageCardOnLineRecord.STATUS_NORMAL);
		
		manageCardOnLineRecordMapper.insertSelective(record);
	}
}
