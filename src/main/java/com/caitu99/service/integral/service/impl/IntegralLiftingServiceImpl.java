package com.caitu99.service.integral.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.integral.controller.vo.IntegralLiftingVo;
import com.caitu99.service.integral.dao.IntegralLiftingMapper;
import com.caitu99.service.integral.domain.IntegralLifting;
import com.caitu99.service.integral.service.IntegralLiftingService;
import com.caitu99.service.sys.domain.Page;
import com.caitu99.service.user.dao.UserMapper;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.utils.XStringUtil;

@Service
public class IntegralLiftingServiceImpl implements IntegralLiftingService {

	@Autowired
	private RedisOperate redis;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private IntegralLiftingMapper integralliftingMapper;

	@Override
	public String payNow(User user, Long integral) {
		User users = new User();
		users.setId(user.getId());
		users.setIntegral(user.getIntegral() - integral.intValue());
		IntegralLifting integrallifting = new IntegralLifting();
		String orderno = XStringUtil.getOrder();
		integrallifting.setIntegral(integral.intValue());
		integrallifting.setUserid(user.getId());
		integrallifting.setTime(new Date());
		integrallifting.setStatus(1);
		integrallifting.setOrderno(orderno);
		userMapper.updateIntegral(users);
		String key = String.format(RedisKey.USER_USER_BY_ID_KEY, user.getId());
		redis.del(key);
		int result = integralliftingMapper.insert(integrallifting);
		return orderno;
	}

	@Override
	public String paydelay(User user, Long intergral) {
		IntegralLifting integrallifting = new IntegralLifting();
		String orderno = XStringUtil.getOrder();
		integrallifting.setIntegral(intergral.intValue());
		integrallifting.setUserid(user.getId());
		integrallifting.setTime(new Date());
		integrallifting.setStatus(2);
		integrallifting.setOrderno(orderno);
		String key = String.format(RedisKey.USER_USER_BY_ID_KEY, user.getId());
		redis.del(key);
		int result = integralliftingMapper.insertSelective(integrallifting);
		return orderno;
	}

	@Override
	public Long countIntergral(IntegralLifting record) {
		return integralliftingMapper.countintergral(record);
	}

	@Override
	public int updateByPrimaryKeySelective(IntegralLifting record) {
		return integralliftingMapper.updateByPrimaryKeySelectivebyorder(record);
	}

	@Override
	public Boolean refundintegral(User user) {
		User users = new User();
		users.setId(user.getId());
		users.setIntegral(user.getIntegral());
		try {
			userMapper.updateIntegral(users);
			String key = String.format(RedisKey.USER_USER_BY_ID_KEY,
					user.getId());
			redis.del(key);
			return true;
		} catch (Exception e) {

		}
		return false;
	}

	@Override
	public Long countIntergralByUser(IntegralLifting record) {
		// TODO Auto-generated method stub
		return integralliftingMapper.countintergralbyuser(record);
	}

	@Override
	public List<IntegralLiftingVo> listSucess(Page page) {
		return integralliftingMapper.listSucess(page);
	}

	@Override
	public int countNum() {

		return integralliftingMapper.countNum();
	}

	@Override
	public List<IntegralLiftingVo> listAll(Page page) {
		return integralliftingMapper.listAll(page);
	}
}
