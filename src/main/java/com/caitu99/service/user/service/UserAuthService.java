package com.caitu99.service.user.service;

import java.util.List;

import com.caitu99.service.user.domain.UserAuth;

public interface UserAuthService {

	// 添加用户身份信息
	int insert(UserAuth userAuth);

	UserAuth selectByUserId(Long userId);

	// 根据身份证号获取用户列表
	List<UserAuth> selectByAccId(String accId);

	// 支付密码根据用户id判断实名认证
	UserAuth findByUserId(Long userId);
}
