package com.caitu99.service.right.dao;

import com.caitu99.service.right.domain.RightCode;

public interface RightCodeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RightCode record);

    int insertSelective(RightCode record);

    RightCode selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RightCode record);

    int updateByPrimaryKey(RightCode record);

	RightCode getNoUsedCode(Long rightId);
}