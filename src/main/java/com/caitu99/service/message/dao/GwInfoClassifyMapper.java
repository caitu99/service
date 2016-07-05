package com.caitu99.service.message.dao;

import java.util.List;

import com.caitu99.service.message.domain.GwInfoClassify;

public interface GwInfoClassifyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GwInfoClassify record);

    int insertSelective(GwInfoClassify record);

    GwInfoClassify selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GwInfoClassify record);

    int updateByPrimaryKey(GwInfoClassify record);
    
    List<GwInfoClassify> selectPageList();
}