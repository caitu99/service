package com.caitu99.service.transaction.api;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.integral.domain.IntegralLifting;
import com.caitu99.service.integral.service.ConsumeService;
import com.caitu99.service.integral.service.IntegralLiftingService;
import com.caitu99.service.sys.domain.Config;
import com.caitu99.service.sys.service.ConfigService;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.TakeCashService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserAuth;
import com.caitu99.service.user.service.UserAuthService;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;

@Controller
@RequestMapping("/api/cash")
public class TakeCashController extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(TakeCashController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private IntegralLiftingService integralliftingService;
	@Autowired
	private UserAuthService userAuthService;
	@Autowired
	private ConsumeService consumeService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private TakeCashService takeCashService;

	// 提现
	@RequestMapping(value = "/take/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String take(Long integral, Long userId, String paypass)
			throws CryptoException {
		// 初始化
		Long caituIntegral = 100L;
		ApiResult<Boolean> result = new ApiResult<Boolean>();
		result.setCode(0);

		// 时间限制
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (hour < 5 || hour >= 22)
			throw new ApiException(2401, "每日22点-次日5点为系统结算时间，暂不支持提现，请明日再试");

		// 紧急关闭
		Config config = configService.selectByKey("Integrallifting");
		if ("false".equals(config.getValue()))
			throw new ApiException(2402, "系统维护中，提现功能暂不可用");

		if(config.getStatus().equals(0)){
			throw new ApiException(2402, "提现功能暂不可用");
		}
		
		// 限制提现金额
		if (integral < 10 * caituIntegral)
			throw new ApiException(2406, "提现金额必须大于10元");

		if (integral > 50000 * caituIntegral)
			throw new ApiException(2407, "提现金额不能大于50000元");

		User user = userService.getById(userId);

		// 校验支付密码
		if (StringUtils.isEmpty(paypass))
			throw new ApiException(2455, "支付密码不能为空");
		if (StringUtils.isEmpty(user.getPaypass()))
			throw new ApiException(2109, "请先设置支付密码");
		paypass = AESCryptoUtil.encrypt(paypass);
		if (!paypass.equals(user.getPaypass()))
			throw new ApiException(2403, "支付密码错误");

		// 校验账户
		Account account = accountService.selectByUserId(userId);
		if (null == account)
			throw new ApiException(3001, "账户不存在");

		// 校验财币
		if (null == account.getAvailableIntegral()
				|| account.getAvailableIntegral() < integral)
			throw new ApiException(2404, "您的财币不足");

		// 今日是否能提现
		IntegralLifting integralLifting = new IntegralLifting();
		integralLifting.setTime(XStringUtil.getBeginOfToday());
		Long count = integralliftingService.countIntergral(integralLifting);
		if (count == null)
			count = 0L;
		if (integral + count > 5000000 * caituIntegral)
			throw new ApiException(2408, "当日平台提现已达上限，请明日再试");

		// 校验用户绑定信息
		UserAuth userAuth = userAuthService.selectByUserId(user.getId());
		if (userAuth == null)
			throw new ApiException(2045, "您还没有绑定银行卡");

		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		// 大于10W延时付
		if (integral + count > 100000 * caituIntegral) {
			takeCashService.takeDelay(user, userAuth, integral);
			String message = "您已成功提现"
					+ df.format(((double) integral / caituIntegral - 2L))
					+ "元至"
					+ userAuth.getBankname()
					+ "(尾号"
					+ userAuth.getCardNo().substring(
							userAuth.getCardNo().length() - 4,
							userAuth.getCardNo().length()) + "),预计5个工作日到账";
			result.setMessage(message);
			result.setData(true);
			return JSON.toJSONString(result);
		}

		integralLifting.setUserid(user.getId());
		Long countUser = integralliftingService
				.countIntergralByUser(integralLifting);
		if (countUser == null)
			countUser = 0L;

		if (countUser + integral <= 500 * caituIntegral) {
			// if (false) {
			takeCashService.takeNow(user, userAuth, integral);
			String message = "您已成功提现"
					+ df.format(((double) integral / caituIntegral - 2L))
					+ "元至"
					+ userAuth.getBankname()
					+ "(尾号"
					+ userAuth.getCardNo().substring(
							userAuth.getCardNo().length() - 4,
							userAuth.getCardNo().length()) + "),预计2小时到账";
			result.setMessage(message);
			result.setData(true);
			return JSON.toJSONString(result);
		} else {
			// 延时付
			takeCashService.takeDelay(user, userAuth, integral);
			String message = "您已成功提现"
					+ df.format(((double) integral / caituIntegral - 2L))
					+ "元至"
					+ userAuth.getBankname()
					+ "(尾号"
					+ userAuth.getCardNo().substring(
							userAuth.getCardNo().length() - 4,
							userAuth.getCardNo().length()) + "),预计5个工作日到账";
			result.setMessage(message);
			result.setData(true);
			return JSON.toJSONString(result);
		}
	}
}