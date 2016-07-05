package com.caitu99.service.goods.dao;

import java.util.List;

import com.caitu99.service.goods.domain.Group;

public interface GroupMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Group record);

    int insertSelective(Group record);

    Group selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Group record);

    int updateByPrimaryKey(Group record);
    
    
    List<Group> findGroupByTemplateId(Long templateId);
    
    
    
}