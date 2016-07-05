package com.caitu99.service.realization.dao;

import java.util.List;

import com.caitu99.service.realization.domain.PhoneAmount;

public interface PhoneAmountMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PhoneAmount record);

    int insertSelective(PhoneAmount record);

    PhoneAmount selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PhoneAmount record);

    int updateByPrimaryKey(PhoneAmount record);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectAll 
	 * @date 2016年4月13日 下午4:42:40  
	 * @author ws
	*/
	List<PhoneAmount> selectAll();
}