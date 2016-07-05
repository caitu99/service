package com.caitu99.service.realization.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.AppConfig;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.realization.dao.RealizeShareRecordMapper;
import com.caitu99.service.realization.domain.RealizeShareRecord;
import com.caitu99.service.realization.service.RealizeShareRecordService;
@Service
public class RealizeShareRecordServiceImpl implements RealizeShareRecordService {
	
	private Logger logger = LoggerFactory.getLogger(RealizeShareRecordServiceImpl.class);

	@Autowired
	private RealizeShareRecordMapper realizeShareRecordMapper;
	@Autowired
	private AppConfig appConfig;
	
	@Override
	public void insert(Long userid, Long realizeShareId,Long money,Integer platform,Integer type) {
		RealizeShareRecord record = new RealizeShareRecord();
		Date now = new Date();
		record.setGmtCreate(now);
		record.setGmtModify(now);
		record.setMoney(money);
		record.setPlatform(platform);
		record.setRealizeShareId(realizeShareId);
		record.setStatus(1);
		record.setType(type);
		record.setUserId(userid);
		
		realizeShareRecordMapper.insertSelective(record);
	}

	@Override
	public List<RealizeShareRecord> selectPageList(RealizeShareRecord realizeShareRecord) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("realizeShareRecord", realizeShareRecord);
		
		return realizeShareRecordMapper.selectPageList(map);
	}

	@Override
	public Long isExist(Long userId, Long realizeShareId,Integer platform) {
		RealizeShareRecord realizeShareRecord = new RealizeShareRecord();
		realizeShareRecord.setPlatform(platform);
		realizeShareRecord.setStatus(1);
		realizeShareRecord.setUserId(userId);
		realizeShareRecord.setRealizeShareId(realizeShareId);
		
		List<RealizeShareRecord> list = this.selectPageList(realizeShareRecord);
		if(null != list && list.size() > 0){
			realizeShareRecord = list.get(0);
			return realizeShareRecord.getMoney();
		}
		
		return null;
	}

	@Override
	public boolean isFirst(Long userId) {
		RealizeShareRecord realizeShareRecord = new RealizeShareRecord();
		realizeShareRecord.setStatus(1);
		realizeShareRecord.setUserId(userId);
		
		List<RealizeShareRecord> list = this.selectPageList(realizeShareRecord);
		if(null != list && list.size() > 0){
			return false;
		}
		
		return true;
	}

	@Override
	public RealizeShareRecord selectFirstRecord(Long userId,Integer type) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userId", userId);
		map.put("expiresTime", appConfig.realizeShareExpiresTime);
		map.put("type", type);
		return realizeShareRecordMapper.selectFirstRecord(map);
	}
	
	@Override
	public void update(RealizeShareRecord realizeShareRecord){
		Date now = new Date();
		realizeShareRecord.setGmtModify(now);
		realizeShareRecordMapper.updateByPrimaryKeySelective(realizeShareRecord);
	}

	@Override
	public List<RealizeShareRecord> selectByRealizeRecordId(Long realizeRecordId) {
		RealizeShareRecord realizeShareRecord = new RealizeShareRecord();
		realizeShareRecord.setStatus(2);
		realizeShareRecord.setRealizeRecordId(realizeRecordId);
		
		List<RealizeShareRecord> list = this.selectPageList(realizeShareRecord);
		if(null != list && list.size() > 0){
			return list;
		}
		
		return null;
	}

	@Override
	public Pagination<RealizeShareRecord> selectByUserId(Long userid, Pagination<RealizeShareRecord> pagination) {
		try {
            Map<String, Object> map = new HashMap<>();
            map.put("user_id", userid);
            map.put("start", pagination.getStart());
            map.put("pageSize", pagination.getPageSize());
    		map.put("expiresTime", appConfig.realizeShareExpiresTime);
            
            List<RealizeShareRecord> list = realizeShareRecordMapper.selectByUserId(map);
            Integer cnt = realizeShareRecordMapper.selectCountByUserId(map);
            List<RealizeShareRecord> newlist = new ArrayList<>();
            for(RealizeShareRecord rsr : list){
            	if(rsr.getStatus() != 1){
            		newlist.add(rsr);
            	}else {
            		cnt--;
            	}
            }
            pagination.setDatas(newlist);
            pagination.setTotalRow(cnt);
            return pagination;
        }
        catch (Exception e)
        {
            logger.error("查询我的礼券出错"+e.getMessage(),e);
            return pagination;
        }
	}
	
	@Override
	public List<RealizeShareRecord> selectHasUsedByUserId(Long userId) {
		RealizeShareRecord realizeShareRecord = new RealizeShareRecord();
		realizeShareRecord.setUserId(userId);
		realizeShareRecord.setStatus(2);
		
		return this.selectPageList(realizeShareRecord);
	}
}
