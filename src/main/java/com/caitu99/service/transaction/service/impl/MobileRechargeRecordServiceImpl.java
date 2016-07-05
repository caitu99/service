package com.caitu99.service.transaction.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.life.dao.PhoneRechargeRecordMapper;
import com.caitu99.service.life.domain.PhoneRechargeRecord;
import com.caitu99.service.sys.domain.Page;
import com.caitu99.service.transaction.service.MobileRechargeRecordService;

@Service
public class MobileRechargeRecordServiceImpl implements
		MobileRechargeRecordService {
	@Autowired
	private PhoneRechargeRecordMapper phoneRechargeRecordMapper;

	@Override
	public int saveRecord(PhoneRechargeRecord phoneRechargeRecord) {
		return phoneRechargeRecordMapper.insert(phoneRechargeRecord);
	}

	@Override
	public List<PhoneRechargeRecord> listAll(Page page) {
		return phoneRechargeRecordMapper.listAll(page);
	}

	@Override
	public int countNum() {
		return phoneRechargeRecordMapper.countNum();
	}
}
