package com.caitu99.service.realization.dao;

import java.util.Map;

import com.caitu99.service.realization.domain.PhoneRealizeDetail;

public interface PhoneRealizeDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PhoneRealizeDetail record);

    int insertSelective(PhoneRealizeDetail record);

    PhoneRealizeDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PhoneRealizeDetail record);

    int updateByPrimaryKey(PhoneRealizeDetail record);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectBy 
	 * @param amountId
	 * @param platformId
	 * @return
	 * @date 2016年4月13日 下午2:35:19  
	 * @author ws
	*/
	PhoneRealizeDetail selectBy(Map<String,Long> queryMap);
}