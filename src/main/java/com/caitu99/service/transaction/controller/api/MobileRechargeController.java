/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.controller.api;

import java.math.BigDecimal;

import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.TransactionRecordService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.exception.ApiClosedException;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.transaction.controller.vo.RechargeResult;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.MobileRechargeService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;

/**
 * 话费充值控制层
 * 
 * @Description: (类职责详细描述,可空)
 * @ClassName: PhoneRechargeController
 * @author lawrence
 * @date 2015年11月2日 下午3:22:38
 * @Copyright (c) 2015-2020 by caitu99
 */
@Controller
@RequestMapping("/api/transaction/recharge")
public class MobileRechargeController extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(MobileRechargeController.class);

	@Autowired
	private MobileRechargeService mobileRechargeService;
	@Autowired
	private UserService userService;
    @Autowired
    private AppConfig appConfig;

	/**
	 * 话费充值
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: phoneRecharge
	 * @param userId
	 *            用户ID
	 * @param mobile
	 *            手机号
	 * @param cardNum
	 *            充值金额
	 * @param payPass
	 *            密码
	 * @return Json
	 * @throws CryptoException
	 * @date 2015年11月2日 下午3:24:56
	 * @author lawrence
	 */
	@RequestMapping(value = "/mobile/1.0")
	@ResponseBody
	public String phoneRecharge(Long userId, String mobile, String cardNum,
			String payPass) throws CryptoException {
		// 初始化
		ApiResult<Object> apiResult = new ApiResult<Object>();
		// 开关
		String swith = appConfig.rechargePhoneSwitch;
		if (StringUtils.isBlank(swith) || !"1".equals(swith.trim())) {
			logger.error("话费充值接口已关闭");
			throw new ApiClosedException(-1, "话费充值接口已关闭");
		}
		// 获取用户信息
		User user = userService.selectByPrimaryKey(userId);
		if (null == user) {
			logger.error("用户不存在");
			throw new UserNotFoundException(2037, "用户不存在");
		}
        // 验证并充值
		RechargeResult rechargeResult = mobileRechargeService.checkAndRecharge(
				userId, mobile, cardNum,user,payPass);
		if (rechargeResult.isSuccess()) {
			apiResult.set(0, "充值成功");
			logger.info("用户 {} ,充值 手机{} {} 成功.", userId, mobile, cardNum);
			return apiResult.toJSONString(0, "充值成功");
		}
		logger.warn("用户 ：{} ,充值手机号：{} 金额： {} 失败.", userId, mobile, cardNum);
		
		if(null != rechargeResult.getCode()){
			return apiResult.toJSONString(rechargeResult.getCode(), rechargeResult.getResult());
		}
		
		return apiResult.toJSONString(2203, rechargeResult.getResult());
	}
}
