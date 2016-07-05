package com.caitu99.service.merchant.service;

import com.caitu99.service.merchant.controller.vo.UserProxyInfoVo;

import java.util.List;

import com.caitu99.service.merchant.domain.ProxyRelation;
import com.caitu99.service.user.domain.User;

public interface ProxyRelationService {
	
	void insert(ProxyRelation proxyRelation);
	
	ProxyRelation selectByEmpUserId(Long empUserId);
	
	void update(ProxyRelation proxyRelation);
	
	User selectSuperiorUserInfo(long userid);

	UserProxyInfoVo selectSelfInfo(long userid);

	List<ProxyRelation> getMyUnderling(Long userId);
}
