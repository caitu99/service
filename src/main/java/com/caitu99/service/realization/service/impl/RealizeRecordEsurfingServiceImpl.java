package com.caitu99.service.realization.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.realization.dao.RealizeRecordEsurfingMapper;
import com.caitu99.service.realization.domain.RealizeRecordEsurfing;
import com.caitu99.service.realization.service.RealizeRecordEsurfingService;


@Service
public class RealizeRecordEsurfingServiceImpl implements RealizeRecordEsurfingService {

	private final static Logger logger = LoggerFactory.getLogger(RealizeRecordEsurfingServiceImpl.class);
	
	@Autowired
	private RealizeRecordEsurfingMapper realizeRecordEsurfingMapper;
	
	@Autowired
	private RedisOperate redis;

	@Override
	public void insert(RealizeRecordEsurfing record) {
		Date now = new Date();
		record.setGmtCreate(now);
		record.setGmtModify(now);
		record.setStatus(1);
		realizeRecordEsurfingMapper.insert(record);
	}

	@Override
	public RealizeRecordEsurfing selectByPrimaryKey(Long id) {
		return realizeRecordEsurfingMapper.selectByPrimaryKey(id);
	}

	@Override
	public void update(RealizeRecordEsurfing record) {
		record.setGmtModify(new Date());
		realizeRecordEsurfingMapper.update(record);
	}

	@Override
	public List<RealizeRecordEsurfing> selectPageList(RealizeRecordEsurfing record) {
		Map<String,Object> map = new HashMap<String,Object>(1);
		map.put("realizeRecordEsurfing", record);
		map.put("isToday", 1);
		return realizeRecordEsurfingMapper.selectPageList(map);
	}

	@Override
	public Long selectUserBuyIntegralCount(Long userid,String loginAccount,Long itemId){
		RealizeRecordEsurfing record = new RealizeRecordEsurfing();
		record.setItemId(itemId);
		record.setUserId(userid);
		record.setLoginAccount(loginAccount);
		
		List<RealizeRecordEsurfing> list = this.selectPageList(record);
		if(null==list || list.size()<1){
			return 0L;
		}
		
		Long sum = 0L;
		for(RealizeRecordEsurfing realizeRecordEsurfing : list){
			Long integral = realizeRecordEsurfing.getIntegral();
			sum += integral;
		}
		
		return sum;
	}
}
