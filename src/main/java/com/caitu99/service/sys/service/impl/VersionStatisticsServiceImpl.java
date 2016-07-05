package com.caitu99.service.sys.service.impl;

import com.caitu99.service.sys.dao.VersionStatisticsMapper;
import com.caitu99.service.sys.domain.VersionStatistics;
import com.caitu99.service.sys.service.VersionStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VersionStatisticsServiceImpl implements VersionStatisticsService {

	@Autowired
	private VersionStatisticsMapper versionStatisticsMapper;
	
	@Override
	public int insert(VersionStatistics record) {
		return versionStatisticsMapper.insert(record);
	}

}
