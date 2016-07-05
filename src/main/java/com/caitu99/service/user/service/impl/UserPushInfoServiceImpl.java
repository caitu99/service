/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.user.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.user.dao.UserPushInfoMapper;
import com.caitu99.service.user.domain.UserPushInfo;
import com.caitu99.service.user.service.UserPushInfoService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UserPushInfoServiceImpl 
 * @author Hongbo Peng
 * @date 2015年12月2日 上午10:27:22 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class UserPushInfoServiceImpl implements UserPushInfoService {

	@Autowired
	private UserPushInfoMapper userPushInfoMapper;

	@Override
	public Long addOrUpdateUserPushInfo(UserPushInfo userPushInfo) {
		UserPushInfo info = userPushInfoMapper.selectByUserId(userPushInfo.getUserId());
		Date date = new Date();
		userPushInfo.setUpdateTime(date);
		if(null == info){
			userPushInfo.setCreateTime(date);
			userPushInfoMapper.insertSelective(userPushInfo);
		}else{
			userPushInfo.setId(info.getId());
			userPushInfoMapper.updateByPrimaryKeySelective(userPushInfo);
		}
		return userPushInfo.getId();
	}


}
