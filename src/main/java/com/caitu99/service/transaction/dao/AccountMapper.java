package com.caitu99.service.transaction.dao;

import com.caitu99.service.transaction.domain.Account;

import java.util.List;

public interface AccountMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Account record);

    int insertSelective(Account record);

    Account selectByPrimaryKey(Long id);
    
	Account selectByUserId(Long userId);

    int updateByPrimaryKeySelective(Account record);

    int updateByPrimaryKey(Account record);

	int updateIntegralByUserId(Account record);

	int updateByUser(Account account);

    //查询所有总财币不是0的用户
	Long selectAllCount();
    
    Account selectByPrimaryKeyForUpdate(Long id);
}
