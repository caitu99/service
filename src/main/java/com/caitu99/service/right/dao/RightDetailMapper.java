package com.caitu99.service.right.dao;

import com.caitu99.service.right.domain.RightDetail;

public interface RightDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RightDetail record);

    int insertSelective(RightDetail record);

    RightDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RightDetail record);

    int updateByPrimaryKey(RightDetail record);
}