package com.caitu99.service.integral.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.integral.dao.AutoFindRecordMapper;
import com.caitu99.service.integral.domain.AutoFindRecord;
import com.caitu99.service.integral.service.AutoFindRecordService;

@Service
public class AutoFindRecordServiceImpl implements AutoFindRecordService {

	@Autowired
	private AutoFindRecordMapper autoFindRecordMapper;

	@Override
	public AutoFindRecord getBySelective(AutoFindRecord autoFindRecord) {
		//autoFindRecord.setStatus(AutoFindRecord.STATUS_NORMAL);

		Map<String,Object> map = new HashMap<String,Object>(1);
		map.put("autoFindRecord", autoFindRecord);
		return autoFindRecordMapper.getBySelective(map);
	}

	@Override
	public void insert(AutoFindRecord autoFindRecord) {
		autoFindRecordMapper.insert(autoFindRecord);
	}

	@Override
	public List<AutoFindRecord> selectPageList(AutoFindRecord autoFindRecord) {
		autoFindRecord.setStatus(AutoFindRecord.STATUS_NORMAL);
		
		Map<String,Object> map = new HashMap<String,Object>(1);
		map.put("autoFindRecord", autoFindRecord);
		
		return autoFindRecordMapper.selectPageList(map);
	}

	@Override
	public void insertORupdate(AutoFindRecord autoFindRecord) {
		if(null == autoFindRecord.getId()){
			autoFindRecordMapper.insert(autoFindRecord);
		}else{
			autoFindRecordMapper.update(autoFindRecord);
		}
	}
}
