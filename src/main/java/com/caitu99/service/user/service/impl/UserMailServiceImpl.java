package com.caitu99.service.user.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.AppConfig;
import com.caitu99.service.user.dao.UserCardMapper;
import com.caitu99.service.user.dao.UserMailMapper;
import com.caitu99.service.user.domain.UserCard;
import com.caitu99.service.user.domain.UserMail;
import com.caitu99.service.user.service.UserMailService;
import com.caitu99.service.utils.Configuration;

@Service
public class UserMailServiceImpl implements UserMailService {

	@Autowired
	private UserMailMapper userMailMapper;
	@Autowired
	private UserCardMapper userCardMapper;
	@Autowired
	AppConfig appConfig;

	@Override
	public int insert(UserMail userMail) {
		return userMailMapper.insert(userMail);
	}

	@Override
	public UserMail selectByEmail(String email) {
		return userMailMapper.selectByEmail(email);
	}

	@Override
	public int updateByPrimaryKeySelective(UserMail record) {
		return userMailMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public List<UserMail> selectByUserId(Long userId) {
		return userMailMapper.selectByUserId(userId);
	}

	@Override
	public UserMail selectByUserIdAndMail(UserMail userMail) {
		return userMailMapper.selectByUserIdAndMail(userMail);
	}

	@Override
	public int updateByLastUpdate(UserMail record) {
		return userMailMapper.updateByLastUpdate(record);
	}

	@Override
	public Long Convertibleintegral(Long userId) {
		List<UserCard> list = userCardMapper.queryIntegral(userId);
		Long tatol = 0L;
		float scale = Float.valueOf(Configuration.getProperty("exchange.scale",
				"0.01"));
		for (UserCard userCard : list) {
			tatol = tatol
					+ Long.valueOf((long) (scale
							* (userCard.getIntegralBalance() == null ? 0
									: userCard.getIntegralBalance()) * userCard
							.getScale()));
		}
		return tatol;

	}

	@Override
	public List<UserMail> list() {
		return userMailMapper.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.caitu99.service.user.service.UserMailService#updateByUserIdAndMail
	 * (com.caitu99.service.user.domain.UserMail)
	 */
	@Override
	public int updateByUserIdAndMail(UserMail userMail) {
		return userMailMapper.delByUserIdAndMail(userMail);
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.user.service.UserMailService#selectByUserIdForJob(java.lang.Long)
	 */
	@Override
	public List<UserMail> selectByUserIdForJob(Long userId) {
		Map<String,Object> paramMap = new HashMap<String,Object>();

		String emailExclude = appConfig.emailExclude;
		String[] emailsStr = emailExclude.split(",");
		
		paramMap.put("userId", userId);
		paramMap.put("mails", emailsStr);
		return userMailMapper.selectByUserIdForJob(paramMap);
	}
}
