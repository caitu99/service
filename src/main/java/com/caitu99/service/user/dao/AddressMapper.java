package com.caitu99.service.user.dao;

import com.caitu99.service.user.domain.Address;

public interface AddressMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Address record);

    int insertSelective(Address record);

    Address selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Address record);

    int updateByPrimaryKey(Address record);

    /**
     * 通过用户Id获取用户默认收货地址
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectByUserId 
     * @param userId
     * @return
     * @date 2016年1月21日 下午3:51:31  
     * @author ws
     */
	Address selectByUserId(Long userId);
}