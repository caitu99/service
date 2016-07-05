/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.life.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.life.controller.api.PhoneRechargeController;
import com.caitu99.service.life.dao.RechargeMapper;
import com.caitu99.service.life.domain.Recharge;
import com.caitu99.service.life.service.RechargeService;
import com.caitu99.service.sys.dao.NoticeMapper;
import com.caitu99.service.sys.domain.Notice;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.XStringUtil;

/**
 * @author lawrence
 * @Description: (类职责详细描述, 可空)
 * @ClassName: RechargeServiceImpl
 * @date 2015年11月10日 下午3:09:09
 * @Copyright (c) 2015-2020 by caitu99
 */
@Service
public class RechargeServiceImpl implements RechargeService {

	private static final Logger logger = LoggerFactory
			.getLogger(RechargeServiceImpl.class);

	@Autowired
	private RechargeMapper rechargeMapper;
	@Autowired
	private NoticeMapper noticeMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private AccountService accountService;

	@Override
	public Map gift(Long userId, Integer giftType, String version, Long type) {
		// 1、参数验证
		Map map = new HashMap<>();

		if (null == userId || null == giftType) {
			// 参数为空，直接返回不影响业务操作
			logger.warn("param userId or giftType can not be null");
			map.put("code", 2217);
			map.put("message", "参数为空");
			return map;
		}
		// 2、业务验证
		if (1 > giftType.intValue() || giftType.intValue() > 2) {
			// 业务类型不正确，直接返回不影响业务操作

			map.put("code", 2216);
			map.put("message", "业务类型不正确");

			return map;
		}
		User user = userService.getById(userId);
		// 用户不存在，直接返回不影响业务操作
		if (null == user) {
			map.put("code", 2037);
			map.put("message", "用户名不存在");

			return map;
		}

		// 3、查询充值记录，判断已经赠送
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("userId", userId);
		maps.put("giftType", giftType);
		List<Recharge> dbRecharge = rechargeMapper
				.selectGiftByUserIdAndGiftType(maps);
		if (null != dbRecharge && !dbRecharge.isEmpty()) {
			// 已赠送，直接返回不影响业务操作
			map.put("code", 2218);
			map.put("message", "已经赠送过财币");

			return map;
		}
		// 4、赠送业务持久化
		// 4.2 更新用户赠送的财币
		final Long giftAmount = 100L;
		final Date date = new Date();

		Integer status = 0;
		try {
			// userService.updateUserIntegral(userId, giftAmount.intValue());
			TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
			transactionRecordDto.setChannel(4);
			transactionRecordDto.setComment("首次导入账单赠送"+giftAmount+"财币");
			transactionRecordDto.setOrderNo("");
			transactionRecordDto.setPicUrl("");
			transactionRecordDto.setSource(3);// 赠送
			transactionRecordDto.setTotal(giftAmount.longValue());
			transactionRecordDto.setTransactionNumber(XStringUtil
					.createSerialNo("ZS", String.valueOf(userId)));
			transactionRecordDto.setType(6);
			transactionRecordDto.setInfo("活动");
			transactionRecordDto.setUserId(userId);
			accountService.add(transactionRecordDto);
			status = 1;
		} catch (Exception e) {

		}

		// 4.1 新增记录
		rechargeMapper.insert(new Recharge(userId, giftType, giftAmount, date,
				status));

		// 5、发送通知业务，非必须业务，目前使用线程异步，后续使用消息对列

		Notice notice = new Notice();
		notice.setUserid(userId);
		notice.setContent("恭喜，首次添加账户的" + giftAmount + "财币奖励已到账");
		Date datenew = new Date();
		notice.setStartTime(datenew);
		date.setTime(date.getTime() + 60 * 60 * 1000);
		notice.setEndTime(date);// (TODO)
		notice.setVersion(version);
		notice.setType(type);
		notice.setStatus(1);
		noticeMapper.insert(notice);
		map.put("code", 0);
		map.put("message", "恭喜，首次添加账户的" + giftAmount + "财币奖励已到账");
		map.put("giftIntegral",giftAmount);
		return map;
	}

	@Override
	public List<Recharge> findstatus(Long userid) {

		return rechargeMapper.selectbyuserid(userid);
	}

}
