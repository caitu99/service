package com.caitu99.service.life.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.life.domain.Recharge;

public interface RechargeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Recharge record);

    int insertSelective(Recharge record);

    Recharge selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Recharge record);

    int updateByPrimaryKey(Recharge record);
    
    List<Recharge> selectGiftByUserIdAndGiftType(Map<String, Object> map);

    List <Recharge>selectbyuserid(Long userid);

}