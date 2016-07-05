/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.service.impl;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.exception.AccountException;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.TakeCashException;
import com.caitu99.service.integral.dao.IntegralLiftingMapper;
import com.caitu99.service.integral.domain.IntegralLifting;
import com.caitu99.service.transaction.controller.vo.AccountResult;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.TakeCashService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserAuth;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.unionpay.UnionOpens;

/**
 * 
 * @Description: (类职责详细描述,可空)
 * @ClassName: TakeCashServiceImpl
 * @author lhj
 * @date 2015年12月3日 下午7:25:46
 * @Copyright (c) 2015-2020 by caitu99
 */
@Service
public class TakeCashServiceImpl implements TakeCashService {

	private final static Logger logger = LoggerFactory
			.getLogger(TakeCashServiceImpl.class);

	@Autowired
	private AccountService accountService;
	@Autowired
	private IntegralLiftingMapper integralliftingMapper;
	@Autowired
	private RedisOperate redis;

	/**
	 * 提现延时
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "TakeCashException" })
	public void takeDelay(User user, UserAuth userAuth, Long intergral) {
		try {
			IntegralLifting integrallifting = new IntegralLifting();
			String orderNo = XStringUtil.createSerialNo("TD",
					String.valueOf(user.getId()));
			integrallifting.setIntegral(intergral.intValue());
			integrallifting.setUserid(user.getId());
			integrallifting.setTime(new Date());
			integrallifting.setStatus(2);
			integrallifting.setOrderno(orderNo);
			String key = String.format(RedisKey.USER_USER_BY_ID_KEY,
					user.getId());
			redis.del(key);
			integralliftingMapper.insertSelective(integrallifting);

			TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
			transactionRecordDto.setChannel(1);
			transactionRecordDto.setComment("");
			String cardNo = "****";
			String bankName = "";
			if (!StringUtils.isEmpty(userAuth.getCardNo()))
				cardNo = userAuth.getCardNo().substring(
						userAuth.getCardNo().length() - 4);
			if (!StringUtils.isEmpty(userAuth.getBankname()))
				bankName = userAuth.getBankname();
			transactionRecordDto.setInfo(bankName + "(" + cardNo + ")");
			transactionRecordDto.setOrderNo("");
			transactionRecordDto.setPicUrl("");
			transactionRecordDto.setTotal(intergral);
			transactionRecordDto.setTransactionNumber(orderNo);
			transactionRecordDto.setType(4);
			transactionRecordDto.setUserId(user.getId());
			transactionRecordDto.setSource(4);
			accountService.pay(transactionRecordDto);
		} catch (Exception e) {
			logger.error("交易失败:{}", e);
			throw new TakeCashException(3102, e.getMessage());
		}
	}

	/**
	 * 提现实时
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "TakeCashException" })
	public void takeNow(User user, UserAuth userAuth, Long integral) {
		try {

			String orderNo = XStringUtil.getOrder();
			TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
			transactionRecordDto.setChannel(1);
			transactionRecordDto.setComment("");
			String cardNo = "****";
			String bankName = "";
			if (!StringUtils.isEmpty(userAuth.getCardNo()))
				cardNo = userAuth.getCardNo().substring(
						userAuth.getCardNo().length() - 4);
			if (!StringUtils.isEmpty(userAuth.getBankname()))
				bankName = userAuth.getBankname();
			transactionRecordDto.setInfo(bankName + "(" + cardNo + ")");
			transactionRecordDto.setOrderNo("");
			transactionRecordDto.setPicUrl("");
			transactionRecordDto.setTotal(integral);
			transactionRecordDto.setTransactionNumber(orderNo);
			transactionRecordDto.setType(4);
			transactionRecordDto.setUserId(user.getId());
			transactionRecordDto.setSource(4);
			AccountResult accountResult = accountService
					.pay(transactionRecordDto);
			if (!accountResult.isSuccess()) {
				logger.error("提现失败，更新交易记录或账户信息失败");
				throw new TakeCashException(2456, "提现失败，更新交易记录或账户信息失败");
			}
			// 第三方银联接口
			UnionOpens unionOpens = new UnionOpens();

			Map map = unionOpens.integrallifting(integral,
					userAuth.getBindId(), orderNo);
			if (null == map) {
				logger.error("提现失败，网络繁忙，请重试");
				throw new TakeCashException(2409, "网络繁忙，请重试");
			}
			if ("58".equals(map.get("retCode"))) {
				logger.error("提现失败，每日22点-次日5点为系统结算时间，暂不支持提现");
				throw new TakeCashException(2401,
						"每日22点-次日5点为系统结算时间，暂不支持提现，请明日再试");
			}
			if (!"2".equals(map.get("orderStatus"))) {
				logger.error("提现失败，网络繁忙，请重试");
				throw new TakeCashException(2409, "网络繁忙，请重试");
			}
			IntegralLifting ilfs = new IntegralLifting();
			ilfs.setOrderstatus(Integer.valueOf((String) map.get("orderStatus")));
			ilfs.setOrderno((String) map.get("orderNo"));
			Date date = new Date();
			date.setTime(Long.valueOf((String) map.get("processDate")));
			ilfs.setProcessdate(date);
			ilfs.setRetdesc((String) map.get("retDesc"));
			ilfs.setTime(new Date());
			ilfs.setStatus(1);
			ilfs.setIntegral(integral.intValue());
			ilfs.setUserid(user.getId());
			integralliftingMapper.insert(ilfs);

		} catch (Exception e) {
			logger.error("交易失败:{}", e);
			throw new TakeCashException(3102, e.getMessage());
		}
	}

}
