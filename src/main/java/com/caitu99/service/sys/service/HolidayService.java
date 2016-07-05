package com.caitu99.service.sys.service;


import com.caitu99.service.sys.domain.Holiday;

import java.util.List;


public interface HolidayService {
	int deleteByPrimaryKey(Long id);

	int insert(Holiday record);

	int insertSelective(Holiday record);

	Holiday selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Holiday record);

	int updateByPrimaryKey(Holiday record);

	List<Holiday> selectBystatus(Long status);
}
