/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.user.api;

import com.caitu99.service.user.domain.User;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IUserService 
 * @author ws
 * @date 2015年12月1日 下午3:45:54 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface IUserServiceApi {
	/**
	 * 通过手机号新增新用户
	 * 并返回注册后的用户id
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: addUser 
	 * @param phoneNo
	 * @return userId
	 * @date 2015年12月1日 下午4:06:49  
	 * @author ws
	 */
	public User addUser(String phoneNo);
}
