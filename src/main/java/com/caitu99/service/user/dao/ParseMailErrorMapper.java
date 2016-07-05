package com.caitu99.service.user.dao;

import com.caitu99.service.user.domain.ParseMailError;
import com.caitu99.platform.dao.base.func.IEntityDAO;

public interface ParseMailErrorMapper extends IEntityDAO<ParseMailError, ParseMailError> {
    int deleteByPrimaryKey(Long id);

    int insert(ParseMailError record);
    
    int insertSelective(ParseMailError record);

    ParseMailError selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ParseMailError record);

    int updateByPrimaryKeyWithBLOBs(ParseMailError record);

    int updateByPrimaryKey(ParseMailError record);
}