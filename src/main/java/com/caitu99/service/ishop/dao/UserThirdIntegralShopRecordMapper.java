package com.caitu99.service.ishop.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.ishop.domain.UserThirdIntegralShopRecord;

public interface UserThirdIntegralShopRecordMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(UserThirdIntegralShopRecord record);

    UserThirdIntegralShopRecord selectByPrimaryKey(Long id);

    int update(UserThirdIntegralShopRecord record);

    List<UserThirdIntegralShopRecord> selectPageListByUserId(Map<String,Object> map);
    
    UserThirdIntegralShopRecord selectWhether(UserThirdIntegralShopRecord userThirdIntegralShopRecord);
}