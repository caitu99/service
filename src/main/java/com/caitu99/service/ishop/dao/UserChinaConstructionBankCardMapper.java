package com.caitu99.service.ishop.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.ishop.domain.UserChinaConstructionBankCard;

public interface UserChinaConstructionBankCardMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserChinaConstructionBankCard record);

    UserChinaConstructionBankCard selectByPrimaryKey(Long id);

    int update(UserChinaConstructionBankCard record);

    List<UserChinaConstructionBankCard> selectPageListByUserId(Map<String,Object> map);
    
    UserChinaConstructionBankCard selectWhether(UserChinaConstructionBankCard userChinaConstructionBankCard);
}