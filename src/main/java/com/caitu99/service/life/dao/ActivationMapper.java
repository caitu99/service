package com.caitu99.service.life.dao;

import com.caitu99.service.life.domain.Activation;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface ActivationMapper extends IEntityDAO<Activation, Activation> {
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