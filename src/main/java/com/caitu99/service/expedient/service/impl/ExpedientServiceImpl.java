/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.expedient.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.exception.ExpedientException;
import com.caitu99.service.expedient.dao.ExpRecordMapper;
import com.caitu99.service.expedient.dao.ExpedientMapper;
import com.caitu99.service.expedient.dao.VipMapper;
import com.caitu99.service.expedient.domain.ExpRecord;
import com.caitu99.service.expedient.domain.Expedient;
import com.caitu99.service.expedient.domain.Vip;
import com.caitu99.service.expedient.service.ExpedientService;
import com.caitu99.service.user.dao.UserMapper;
import com.caitu99.service.user.domain.User;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ExpedientServiceImpl 
 * @author fangjunxiao
 * @date 2016年6月1日 上午10:00:30 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class ExpedientServiceImpl implements ExpedientService{
	
	@Autowired
	private ExpedientMapper expedientDao;
	
	@Autowired
	private VipMapper vipDao;
	 
	@Autowired
	private UserMapper userDao;
	
	@Autowired
	private ExpRecordMapper expRecordDao;

	@Override
	public Expedient getExpedientByUserId(Long userId)
			throws ExpedientException {
		
		Expedient expedient = expedientDao.getExpedientByuserId(userId);
		
		if(null == expedient){
			
			User user = userDao.selectByPrimaryKey(userId);
			
			Date date = new Date();
			
			//初始等级
			Vip vip = vipDao.getVipByLev(0);
			
			Expedient newExpt = new Expedient();
			newExpt.setExp(0L);
			newExpt.setLev(vip.getLev());
			newExpt.setMobile(user.getMobile());
			newExpt.setName(vip.getName());
			newExpt.setStatus(1);
			newExpt.setUserId(user.getId());
			newExpt.setCreateTime(date);
			newExpt.setUpdateTime(date);
			expedientDao.insertSelective(newExpt);
			
			return newExpt;
		}
		
		return expedient;
	}

	@Override
	public void addExpRecord(Long userId, Long exp, Integer source, String note)
			throws ExpedientException {
		
		Date date = new Date();
		ExpRecord expRecord = new ExpRecord();
		expRecord.setUserId(userId);
		expRecord.setSource(source);
		expRecord.setExp(exp);
		expRecord.setNote(note);
		expRecord.setCreateTime(date);
		expRecordDao.insertSelective(expRecord);
		
	}

}
