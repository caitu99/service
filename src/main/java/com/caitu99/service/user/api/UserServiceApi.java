/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.user.api;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.caitu99.service.exception.ApiException;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;


/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UserServiceApi 
 * @author ws
 * @date 2015年12月1日 下午3:47:23 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class UserServiceApi implements IUserServiceApi {


	@Autowired
	private UserService userService;
	@Autowired
	private AccountService accountService;
	/* (non-Javadoc)
	 * @see com.caitu99.service.user.api.IUserServiceApi#addUser(java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public User addUser(String phoneNo) {
		
		if(null == phoneNo){
			throw new ApiException(2006,"手机号不能为空");
		}
		
		User user = new User();
		user.setMobile(phoneNo);
		user.setGmtCreate(new Date());
		user.setLoginCount(0);//0次登录
		user.setType(1);//普通用户
		User findUser = userService.isExistMobile(user);
		if (null == findUser) {// 如果用户不存在则创建用户
			user.setStatus(1);
			userService.regist(user);
			
			accountService.addNewAccount(user.getId());
			
			if (null == user.getId()) {
				throw new ApiException(2016,"注册失败");
			} else {
				return user;
			}
		}else{
			user = userService.getById(findUser.getId());
			return user;
		}
	}

}
