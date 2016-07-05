package com.caitu99.service.integral.dao;

import java.util.List;

import com.caitu99.service.integral.domain.CardIntegral;
import com.caitu99.service.integral.domain.Consume;
import com.caitu99.platform.dao.base.func.IEntityDAO;

public interface ConsumeMapper extends IEntityDAO<Consume, Consume> {
    int deleteByPrimaryKey(Long id);

    int insert(Consume record);

    int insertSelective(Consume record);

    Consume selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Consume record);

    int updateByPrimaryKey(Consume record);
    
    List<Consume> consumeAll(Long userId);
}