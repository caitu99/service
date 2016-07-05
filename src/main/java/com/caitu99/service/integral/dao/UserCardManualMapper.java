package com.caitu99.service.integral.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.user.controller.vo.UserCardManualVo;

public interface UserCardManualMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(UserCardManual record);

    UserCardManual selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserCardManual record);

    UserCardManual getByUserIdCardTypeId(Map<String,Object> map);
    
    List<UserCardManualVo> queryIntegral(Long userid);
    
    List<UserCardManualVo> queryIntegralRemoveRepetition(Map<String,Object> map);
    
    List<UserCardManual> getUserCardManualSelective(Map<String,Object> map);
    
    List<UserCardManual> selectByUserIdManualId(Map<String,Object> map);
    
    List<UserCardManualVo> selectLastTime(Long userId);

    List<UserCardManual> selectByUserIdTime(Map map);
    
    
    int countCardByGmtCreate(Map<String,Object> map);
    
    int countAccountByBank(Map<String,Object> map);
    
    int countPlatformByUser(Map<String,Object> map);
}