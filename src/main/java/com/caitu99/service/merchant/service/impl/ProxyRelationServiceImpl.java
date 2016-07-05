package com.caitu99.service.merchant.service.impl;

import java.util.Date;
import java.util.List;

import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.merchant.controller.vo.UserProxyInfoVo;
import com.caitu99.service.user.dao.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.merchant.dao.ProxyRelationMapper;
import com.caitu99.service.merchant.domain.ProxyRelation;
import com.caitu99.service.merchant.service.ProxyRelationService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;

@Service
public class ProxyRelationServiceImpl implements ProxyRelationService {

	@Autowired
	private ProxyRelationMapper proxyRelationMapper;

	@Autowired
	private UserMapper userMapper;

	@Override
	public void insert(ProxyRelation proxyRelation) {
		Date now = new Date();
		proxyRelation.setCreateTime(now);
		proxyRelation.setUpdateTime(now);
		proxyRelation.setStatus(1);
		
		proxyRelationMapper.insertSelective(proxyRelation);
	}

	@Override
	public ProxyRelation selectByEmpUserId(Long empUserId) {
		return proxyRelationMapper.selectByEmpUserId(empUserId);
	}

	@Override
	public void update(ProxyRelation proxyRelation) {
		Date now = new Date();
		proxyRelation.setUpdateTime(now);
		
		proxyRelationMapper.updateByPrimaryKeySelective(proxyRelation);
	}
	
	
	@Override
	public User selectSuperiorUserInfo(long userid) {
		ProxyRelation superior=proxyRelationMapper.selectByEmpUserId(userid);
		if (null==superior){
			return null;
		}
		User user=userMapper.selectByPrimaryKey(superior.getUserId());

		if (null==user){
			throw new ApiException(-2,"数据出现异常");
		}
		return user;
	}

	@Override
	public UserProxyInfoVo selectSelfInfo(long userid) {
		User user= userMapper.selectByPrimaryKey(userid);
		if (null==user){
			throw new UserNotFoundException(-2, "用户不存在");
		}
		UserProxyInfoVo userProxyInfoVo =new UserProxyInfoVo();
		userProxyInfoVo.setLoginAccount(user.getMobile());
		userProxyInfoVo.setCity(user.getCity());
		userProxyInfoVo.setProvince(user.getProvince());
		userProxyInfoVo.setContact(user.getContacts());
		userProxyInfoVo.setNick(user.getNick());
		userProxyInfoVo.setUserid(userid);

		ProxyRelation proxyRelation=proxyRelationMapper.selectByEmpUserId(userid);

		userProxyInfoVo.setProxyType(proxyRelation.getType());
		if (userProxyInfoVo.getProxyType()==1) userProxyInfoVo.setProxyName("代理");
		if (userProxyInfoVo.getProxyType()==2) userProxyInfoVo.setProxyName("业务员");

		return userProxyInfoVo;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.merchant.service.ProxyRelationService#getMyUnderling(java.lang.Long)
	 */
	@Override
	public List<ProxyRelation> getMyUnderling(Long userId) {
		return proxyRelationMapper.getMyUnderling(userId);
	}

	
}
