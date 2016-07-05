package com.caitu99.service.goods.dao;

import com.caitu99.service.goods.domain.TypeItemRelation;

public interface TypeItemRelationMapper {
    int insert(TypeItemRelation record);

    int insertSelective(TypeItemRelation record);
}