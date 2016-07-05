package com.caitu99.service.ishop.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.ishop.domain.UserChinaConstructionBank;

public interface UserChinaConstructionBankMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(UserChinaConstructionBank record);

    UserChinaConstructionBank selectByPrimaryKey(Long id);

    int update(UserChinaConstructionBank record);

    List<UserChinaConstructionBank> selectPageListByUserId(Map<String,Object> map);
    
    UserChinaConstructionBank selectWhether(UserChinaConstructionBank userChinaConstructionBank);
}