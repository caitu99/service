package com.caitu99.service.expedient.dao;

import com.caitu99.service.expedient.domain.Expedient;

public interface ExpedientMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Expedient record);

    int insertSelective(Expedient record);

    Expedient selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Expedient record);

    int updateByPrimaryKey(Expedient record);
    
    Expedient getExpedientByuserId(Long userId);
}