/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities.service;

import com.caitu99.service.activities.domain.Activities;
import com.caitu99.service.exception.ActivitiesException;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ActivitiesService 
 * @author fangjunxiao
 * @date 2015年12月1日 下午6:11:11 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface ActivitiesService {

	/**
	 * 	验证用户抽奖次数
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkUserTimes 
	 * @param userId
	 * @param activitiesId
	 * @return
	 * @throws ActivitiesException
	 * @date 2015年12月5日 上午9:24:51  
	 * @author fangjunxiao
	 */
	String checkUserTimes(Long userId,Long activitiesId)throws ActivitiesException;
	
	
	/**
	 * 	验证用户财币是否足够付费抽奖
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkUserIntegral 
	 * @param userId
	 * @return
	 * @throws ActivitiesException
	 * @date 2015年12月5日 下午4:05:56  
	 * @author fangjunxiao
	 */
	String checkUserIntegral(Long userId) throws ActivitiesException;
	
	
	/**
	 * 	查询中奖记录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getInRecord 
	 * @param userId
	 * @param inRecordId
	 * @return
	 * @throws ActivitiesException
	 * @date 2015年12月8日 上午1:04:36  
	 * @author fangjunxiao
	 */
	String getInRecord(Long userId,Long inRecordId) throws ActivitiesException;
	
	
	String getActivitiesItem(Long activitiesItemId)throws ActivitiesException;
	
	
	
	/**
	 * 	已认证用户抽奖
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getProof 
	 * @param userId
	 * @param activitiesId
	 * @param source
	 * @return
	 * @date 2015年12月5日 上午9:25:59  
	 * @author fangjunxiao
	 */
	String getProof(Long userId, Long activitiesId, Integer source);
	
	
	
	/**
	 * 	已认证用户领奖
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: awardItm 
	 * @param inRecordId
	 * @param userId
	 * @param source
	 * @return
	 * @throws ActivitiesException
	 * @date 2015年12月5日 上午9:25:15  
	 * @author fangjunxiao
	 */
	String awardItm(Long inRecordId,Long userId)throws ActivitiesException;

	/**
	 * 非认证用户抽奖
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getUnautherizedProof 
	 * @param aVo
	 * @return
	 * @throws ActivitiesException
	 * @date 2015年12月4日 上午9:48:03  
	 * @author fangjunxiao
	*/
	String getUnautherizedProof(Long activitiesId, Integer source) throws ActivitiesException;

	/**
	 * 非认证用户认证后，绑定中奖信息
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveWinning 
	 * @param aVo
	 * @return
	 * @throws ActivitiesException
	 * @date 2015年12月4日 上午9:55:43  
	 * @author fangjunxiao
	*/
	String saveWinning(Long userId,Long activitiesId,Integer source,Long activitiesItemId) throws ActivitiesException;


	/**
	 * 放弃领奖
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: giveupItem 
	 * @param inRecordId
	 * @param userId
	 * @return
	 * @throws ActivitiesException
	 * @date 2015年12月5日 下午7:28:46  
	 * @author fangjunxiao
	 */
	String giveupItem(Long inRecordId, Long userId) throws ActivitiesException;
	
	Activities selectByPrimaryKey(Long id);
}
