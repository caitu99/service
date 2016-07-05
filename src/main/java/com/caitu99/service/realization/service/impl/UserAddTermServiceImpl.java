package com.caitu99.service.realization.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.realization.dao.UserAddTermMapper;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.service.UserAddTermService;


@Service
public class UserAddTermServiceImpl implements UserAddTermService {

	private final static Logger logger = LoggerFactory.getLogger(UserAddTermServiceImpl.class);

	@Autowired
	private UserAddTermMapper userAddTermMapper;

	@Override
	public List<UserAddTerm> selectListByUserId(Long userId) {
		return userAddTermMapper.selectListByUserId(userId);
	}

	@Override
	public void update(UserAddTerm record) {
		Date now = new Date();
		record.setUpdateTime(now);
		userAddTermMapper.update(record);
	}

	@Override
	public void insert(UserAddTerm record) {
		Date now = new Date();
		record.setCreateTime(now);
		record.setUpdateTime(now);
		record.setStatus(1);
		userAddTermMapper.insert(record);
	}
	
	@Override
	public Long insertORupdate(UserAddTerm record){
		UserAddTerm userAddTerm = this.selectUserAddTerm(record);
		if(null == userAddTerm){
			this.insert(record);
			return record.getId();
		}else{
			userAddTerm.setStatus(1);
			this.update(userAddTerm);
			return userAddTerm.getId();
		}
	}

	@Override
	public UserAddTerm selectUserAddTerm(UserAddTerm record) {
		Map<String,Object> map = new HashMap<String,Object>(1);
		map.put("userAddTerm", record);
		List<UserAddTerm> list = userAddTermMapper.selectUserAddTerm(map);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}

	@Override
	public UserAddTerm selectByPrimaryKey(Long id) {
		return userAddTermMapper.selectByPrimaryKey(id);
	}
}
