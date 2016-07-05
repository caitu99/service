/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.merchant.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.merchant.dao.SmspayItemMapper;
import com.caitu99.service.merchant.dao.SmspayPlatformMapper;
import com.caitu99.service.merchant.dao.SmspayRecordMapper;
import com.caitu99.service.merchant.domain.SmspayItem;
import com.caitu99.service.merchant.domain.SmspayPlatform;
import com.caitu99.service.merchant.domain.SmspayRecord;
import com.caitu99.service.merchant.service.SmsPayService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: SmsPayServiceImpl 
 * @author fangjunxiao
 * @date 2016年6月21日 下午4:36:36 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class SmsPayServiceImpl implements SmsPayService{

	private static final Logger log = LoggerFactory.getLogger(SmsPayServiceImpl.class);
	
	
	@Autowired
	private SmspayPlatformMapper smspayPlatformDao;
	@Autowired
	private SmspayItemMapper smspayItemDao;
	@Autowired
	private SmspayRecordMapper smspayRecordDao;
	
	
	
	@Override
	public List<SmspayPlatform> findAll() {
		return smspayPlatformDao.findAll();
	}

	@Override
	public List<SmspayItem> findAllByPlatformId(Long platformId) {
		return smspayItemDao.findAllByPlatformId(platformId);
	}

	@Override
	public String saveSmsPayResult(SmspayRecord sr) {
		 ApiResult<String> result = new ApiResult<String>();
		try {
			 Date date = new Date();
			 sr.setCreateTime(date);
			 sr.setUpdateTime(date);
			 smspayRecordDao.insertSelective(sr);
			 return result.toJSONString(0, "Success", "");
		} catch (Exception e) {
			log.warn("短信支付失败,userid=：",sr.getUserId());
			return result.toJSONString(-1, "短信支付失败", "");
		}
	}

}
