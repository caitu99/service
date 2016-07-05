package com.caitu99.service.manage.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.manage.dao.ManageCardDropInRecordMapper;
import com.caitu99.service.manage.domain.ManageCardDropInRecord;
import com.caitu99.service.manage.service.ManageCardDropInRecordService;
import com.caitu99.service.user.domain.UserAuth;


@Service
public class ManageCardDropInRecordServiceImpl implements ManageCardDropInRecordService {

	private final static Logger logger = LoggerFactory.getLogger(ManageCardDropInRecordServiceImpl.class);
	
	@Autowired
	private ManageCardDropInRecordMapper manageCardDropInRecordMapper;

	@Override
	public void insert(UserAuth userAuth,ManageCardDropInRecord manageCardDropInRecord) {
		Date now = new Date();
		manageCardDropInRecord.setName(userAuth.getAccName());
		manageCardDropInRecord.setIdentityCard(userAuth.getAccId());
		manageCardDropInRecord.setUserId(userAuth.getUserId());
		manageCardDropInRecord.setStatus(ManageCardDropInRecord.STATUS_ACCEPT_THE);
		manageCardDropInRecord.setGmtCreate(now);
		manageCardDropInRecord.setGmtModify(now);
		
		manageCardDropInRecordMapper.insertSelective(manageCardDropInRecord);
	}

	@Override
	public Pagination<ManageCardDropInRecord> findPageRecord(ManageCardDropInRecord manageCardDropInRecord,
																							Pagination<ManageCardDropInRecord> pagination) {
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("manageCardDropInRecord", manageCardDropInRecord);
		map.put("start", pagination.getStart());
		map.put("pageSize", pagination.getPageSize());
		
		Integer count = manageCardDropInRecordMapper.selectPageCount(map);
		List<ManageCardDropInRecord> list = manageCardDropInRecordMapper.selectPageList(map);
		
		pagination.setDatas(list);
		pagination.setTotalRow(count);
		
		return pagination;
	}
}
