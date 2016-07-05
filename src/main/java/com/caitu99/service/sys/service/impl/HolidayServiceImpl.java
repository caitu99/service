package com.caitu99.service.sys.service.impl;

import com.caitu99.service.sys.dao.HolidayMapper;
import com.caitu99.service.sys.domain.Holiday;
import com.caitu99.service.sys.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HolidayServiceImpl implements HolidayService {
	@Autowired
	private HolidayMapper holidayMapper;

	@Override
	public int deleteByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		return holidayMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(Holiday record) {
		// TODO Auto-generated method stub
		return holidayMapper.insert(record);
	}

	@Override
	public int insertSelective(Holiday record) {
		// TODO Auto-generated method stub
		return holidayMapper.insertSelective(record);
	}

	@Override
	public Holiday selectByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		return holidayMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(Holiday record) {
		// TODO Auto-generated method stub
		return holidayMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(Holiday record) {
		// TODO Auto-generated method stub
		return holidayMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<Holiday> selectBystatus(Long status) {
		// TODO Auto-generated method stub
		return holidayMapper.selectBystatus(status);
	}

}
