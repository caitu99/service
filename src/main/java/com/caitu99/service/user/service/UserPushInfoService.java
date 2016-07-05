/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.user.service;

import com.caitu99.service.user.domain.UserPushInfo;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UserPushInfoService 
 * @author Hongbo Peng
 * @date 2015年12月2日 上午10:21:06 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface UserPushInfoService {

	/**
	 * @Description: (修改用户设备信息)  
	 * @Title: addOrUpdateUserPushInfo 
	 * @param userPushInfo
	 * @return
	 * @date 2015年12月2日 上午10:24:38  
	 * @author Hongbo Peng
	 */
	Long addOrUpdateUserPushInfo(UserPushInfo userPushInfo);
	
}
