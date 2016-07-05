package com.caitu99.service.expedient.dao;

import com.caitu99.service.expedient.domain.Vip;

public interface VipMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Vip record);

    int insertSelective(Vip record);

    Vip selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Vip record);

    int updateByPrimaryKey(Vip record);
    
    Vip getVipByLev(Integer lev);
    
    Vip upLevByExp(Long exp);
}