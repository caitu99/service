package com.caitu99.service.sys.dao;


import com.caitu99.service.sys.domain.Holiday;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface HolidayMapper extends IEntityDAO<Holiday, Holiday> {
    int deleteByPrimaryKey(Long id);

    int insert(Holiday record);

    int insertSelective(Holiday record);

    Holiday selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Holiday record);

    int updateByPrimaryKey(Holiday record);
    
    List<Holiday>selectBystatus(Long status);
}