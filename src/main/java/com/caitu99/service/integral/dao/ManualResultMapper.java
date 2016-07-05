package com.caitu99.service.integral.dao;

import java.util.Date;
import java.util.List;

import com.caitu99.service.integral.domain.ManualResult;

public interface ManualResultMapper {
    int deleteByUserId(Long userId);

    int insert(ManualResult record);

    int insertSelective(ManualResult record);

    List<ManualResult> selectByUserId(Long userId);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectDateTimeByUserId 
	 * @param userId
	 * @return
	 * @date 2016年2月26日 下午2:51:08  
	 * @author ws
	*/
	Date selectDateTimeByUserId(Long userId);

}