/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.api;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IintegralActivator 
 * @author ws
 * @date 2015年12月1日 下午12:18:26 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface IintegralActivator {
	
	/**
	 * 用户首次登录，激活财币
	 * <P>事务处理
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: activateUserIntegral 
	 * @param userId
	 * @date 2015年12月1日 下午12:23:57  
	 * @author ws
	 */
	public void activateUserIntegral(Long userId);
}
