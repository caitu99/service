package com.caitu99.service.user.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.integral.domain.ManualResult;
import com.caitu99.service.mail.controller.vo.UserCardTypeVo;
import com.caitu99.service.user.dao.UserCardMapper;
import com.caitu99.service.user.domain.UserCard;
import com.caitu99.service.user.service.UserCardService;

@Service
public class UserCardServiceImpl implements UserCardService {
	@Autowired
	private UserCardMapper userCardMapper;

	@Autowired
	private RedisOperate redis;

	@Transactional(rollbackFor = { RuntimeException.class })
	public int insert(UserCard record) {
		return userCardMapper.insertSelective(record);
	}

	@Override
	public List<UserCard> selectByUserCard(UserCard userCard) {
		return userCardMapper.selectByUserCard(userCard);
	}

	@Override
	public List<UserCard> selectByUserCard2(UserCard userCard) {
		return userCardMapper.selectByUserCard2(userCard);
	}

	@Override
	public List<UserCard> selectByUserCard2(UserCard userCard, String typeIds) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("userCard", userCard);
		map.put("typeIds", typeIds);
		return userCardMapper.selectByUserCard2(map);
	}

	@Override
	public Long selectIdByUserCard(UserCard userCard) {
		return userCardMapper.selectIdByUserCard(userCard);
	}

	@Override
	public int updateByUserCard(UserCard record) {
		return userCardMapper.updateByUserCard(record);
	}

	@Override
	public List<UserCard> queryIntegral(Long userid) {

		String key = String.format(RedisKey.USER_USER_CARD_LIST_BY_USER_ID_KEY,
				userid);
		String content = redis.getStringByKey(key);

		// get from redis
		if (!StringUtils.isEmpty(content)) {
			return JSON.parseArray(content, UserCard.class);
		}

		// get from db
		List<UserCard> list = userCardMapper.queryIntegral(userid);
		if (list != null) {
			redis.set(key, JSON.toJSONString(list));
		}

		return list;
	}

	@Override
	public List<UserCard> queryCardByUserIdAndMail(UserCard userCard) {
		return userCardMapper.queryCardByUserIdAndMail(userCard);
	}

	@Override
	public Long total(Long userid) {
		return userCardMapper.total(userid);
	}

	@Override
	public List<UserCard> selectByConditions(UserCard userCard) {
		return userCardMapper.selectByConditions(userCard);
	}

	@Override
	public List<UserCard> selectByConditionsExt(UserCard userCard) {
		return userCardMapper.selectByConditionsExt(userCard);
	}

	@Override
	public int updateByPrimaryKey(UserCard record) {
		return userCardMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public UserCard selectByPrimaryKey(Long id) {
		return userCardMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<UserCard> selectEffectiveIntegralUser(Integer dayNum) {
		return userCardMapper.selectEffectiveIntegralUser(dayNum);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.caitu99.service.user.service.UserCardService#selectUserCardForJob
	 * (java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<UserCard> selectUserCardForJob(Long userId, Long cardTypeId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("cardTypeId", cardTypeId);
		return userCardMapper.selectUserCardForJob(paramMap);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.caitu99.service.user.service.UserCardService#getAllUserDistinct()
	 */
	@Override
	public List<UserCardTypeVo> getAllUserDistinct() {

		return userCardMapper.getAllUserDistinct();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.caitu99.service.user.service.UserCardService#queryUserCardForJob(
	 * java.lang.Integer)
	 */
	@Override
	public List<UserCard> queryUserCardForJob(Integer day3) {

		return userCardMapper.queryUserCardForJob(day3);
	}

	@Override
	public void updateGmtModifyByPrimaryKey(Long id, Date now) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("id", id);
		map.put("gmtModify", now);

		userCardMapper.updateGmtModifyByPrimaryKey(map);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.caitu99.service.user.service.UserCardService#selectByAttrs(com.caitu99
	 * .service.user.domain.UserCard)
	 */
	@Override
	public List<UserCard> selectByAttrs(UserCard userCard) {
		// TODO Auto-generated method stub
		return userCardMapper.selectByAttrs(userCard);
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.user.service.UserCardService#getByUserManualInfo(java.util.Map)
	 */
	@Override
	public List<ManualResult> getByUserManualInfo(Map map) {
		List<ManualResult> result = userCardMapper.getByUserManualInfo(map);
		return result;
	}
}
