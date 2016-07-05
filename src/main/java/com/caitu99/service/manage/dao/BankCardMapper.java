package com.caitu99.service.manage.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.manage.domain.BankCard;

public interface BankCardMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BankCard record);

    int insertSelective(BankCard record);

    BankCard selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BankCard record);

    int updateByPrimaryKey(BankCard record);
    
    List<BankCard> selectPageList(Map<String,Object> map);
}