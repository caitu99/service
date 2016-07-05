package com.caitu99.service.life.service;

import com.caitu99.service.life.domain.Activation;

import java.util.List;

public interface ActivationService {
    int deleteByPrimaryKey(Long id);

    int insert(Activation record);

    int insertSelective(Activation record);

    Activation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Activation record);

    int updateByPrimaryKey(Activation record);

    List<Activation> selectByAll();

    List<Activation> selectByType(Integer type);
    List<Activation> selectByAllData();

}