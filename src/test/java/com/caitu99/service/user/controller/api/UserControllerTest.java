/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.user.controller.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.user.dao.UserMapper;
import com.caitu99.service.user.domain.User;

import com.caitu99.service.AbstractJunit;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UserControllerTest 
 * @author ws
 * @date 2015年11月4日 上午10:17:35 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class UserControllerTest extends AbstractJunit{
	
	@Autowired
	UserController userController;
	@Autowired
	private UserMapper userMapper;
	
	/**
	 * Test method for {@link com.caitu99.service.user.controller.api.UserController#updateUserInfo(com.caitu99.service.user.domain.User)}.
	 */
	@Test
	public void testUpdateUserInfo() {
		User user = userMapper.getById(9L);
		user.setMobile("92490910");//
		user.setCity("杭州");
		
		userController.updateUserInfo(user);
	}
	
	@Test
	public void testGenerateInvitationCode(){
		System.out.println(User.generateInvitationCode());
	}

}
