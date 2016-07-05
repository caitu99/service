package com.caitu99.service.file.dao;

import java.util.List;

import com.caitu99.service.file.domain.AttachFile;

public interface AttachFileMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(AttachFile record);

    AttachFile selectByPrimaryKey(Long id);

    int update(AttachFile record);

    List<AttachFile> selectPageList(AttachFile attachFile);
}