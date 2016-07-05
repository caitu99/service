package com.caitu99.service.life.service.impl;

import java.util.List;

import com.caitu99.service.life.dao.ActivationMapper;
import com.caitu99.service.life.domain.Activation;
import com.caitu99.service.life.service.ActivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivationServiceImpl implements ActivationService {
    @Autowired
    private ActivationMapper activationMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return activationMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(Activation record) {
        return activationMapper.insert(record);
    }

    @Override
    public int insertSelective(Activation record) {
        return activationMapper.insertSelective(record);
    }

    @Override
    public Activation selectByPrimaryKey(Long id) {
        return activationMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(Activation record) {
        return activationMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Activation record) {
        return activationMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<Activation> selectByAll() {
        return activationMapper.selectByAll();
    }

    @Override
    public List<Activation> selectByType(Integer type) {
        return activationMapper.selectByType(type);
    }

    @Override
    public List<Activation> selectByAllData() {
        return activationMapper.selectByAllData();
    }

}
