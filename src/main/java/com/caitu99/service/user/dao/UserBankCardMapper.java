package com.caitu99.service.user.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.user.domain.UserBankCard;

public interface UserBankCardMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserBankCard record);

    int insertSelective(UserBankCard record);

    UserBankCard selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserBankCard record);

    int updateByPrimaryKey(UserBankCard record);
    
    List<UserBankCard> selectByUserId(Map<String,Object> map);

    //查询这个用户的这张银行卡时候有记录
    UserBankCard selectByUseIdAndCardNo(UserBankCard userBankCards);

    //修改银行卡为非默认
    int updateByUseIdAndCardNo(UserBankCard userBankCards);

    //修改银行卡为默认银行卡
    int updateByUseIdAndCardNo1(UserBankCard userBankCards);
    
    List<UserBankCard> selectByUserBackCard(Map<String,Object> map);

}