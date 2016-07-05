/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.right.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.caitu99.service.right.domain.MyRights;
import com.caitu99.service.right.domain.RightCode;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MyRightsService 
 * @author ws
 * @date 2016年5月11日 下午2:39:34 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface MyRightsService {

	/**
	 * 机场贵宾厅权益id
	 */
	static final Long RIGHTS_ID_AIR = 1L;
	
	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectMyRights 
	 * @param userid
	 * @return
	 * @date 2016年5月11日 下午2:47:55  
	 * @author ws
	*/
	List<MyRights> selectMyRights(Long userid);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectRightDetail 
	 * @param id
	 * @return
	 * @date 2016年5月11日 下午3:08:16  
	 * @author ws
	 * @param userid 
	*/
	Map<String,String> selectRightDetail(Long id, Long userid);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: useMyRights 
	 * @param id
	 * @date 2016年5月11日 下午3:44:41  
	 * @author ws
	 * @param userid 
	 * @return 
	*/
	MyRights useMyRights(Long id, Long userid);
	
	/**
	 * 新增我的权益
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: addMyRights 
	 * @param userid 用户id
	 * @param rightId 权益id
	 * @param code 权益二维码串
	 * @param coupon 权益标识号
	 * @param disabledDate 权益失效日期
	 * @date 2016年5月13日 上午11:04:08  
	 * @author ws
	 */
	void addMyRights(Long userid,Long rightId,String code,String coupon,Date disabledDate);

	/**
	 * 获取可使用的权益券
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getRightCode 
	 * @param rightId
	 * @return
	 * @date 2016年5月13日 下午12:17:59  
	 * @author ws
	 */
	RightCode getRightCode(Long rightId);

	/**
	 * 冻结权益
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: freezeRightCode 
	 * @param rightId
	 * @return
	 * @date 2016年5月24日 下午4:25:07  
	 * @author xiongbin
	*/
	RightCode freezeRightCode(Long rightId);
	
	RightCode selectByPrimaryKey(Long id);
}
