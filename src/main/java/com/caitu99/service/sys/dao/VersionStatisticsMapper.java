package com.caitu99.service.sys.dao;


import com.caitu99.service.sys.domain.VersionStatistics;
import com.caitu99.platform.dao.base.func.IEntityDAO;

public interface VersionStatisticsMapper extends IEntityDAO<VersionStatistics, VersionStatistics> {
    int deleteByPrimaryKey(Long id);

    int insert(VersionStatistics record);

    int insertSelective(VersionStatistics record);

    VersionStatistics selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(VersionStatistics record);

    int updateByPrimaryKey(VersionStatistics record);
    
    
}