package com.caitu99.service.user.dao;


import com.caitu99.service.user.domain.ImportMailError;
import com.caitu99.platform.dao.base.func.IEntityDAO;

public interface ImportMailErrorMapper extends IEntityDAO<ImportMailError, ImportMailError> {
    int deleteByPrimaryKey(Long id);

    int insert(ImportMailError record);

    int insertSelective(ImportMailError record);

    ImportMailError selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ImportMailError record);

    int updateByPrimaryKeyWithBLOBs(ImportMailError record);

    int updateByPrimaryKey(ImportMailError record);
}