package com.caitu99.service.file.dao;

import com.caitu99.service.file.domain.AttachFileOperation;

public interface AttachFileOperationMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(AttachFileOperation record);

    int insertSelective(AttachFileOperation record);

    AttachFileOperation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AttachFileOperation record);

    int updateByPrimaryKey(AttachFileOperation record);
}