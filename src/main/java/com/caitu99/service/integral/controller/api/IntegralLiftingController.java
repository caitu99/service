package com.caitu99.service.integral.controller.api;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.caitu99.service.base.BaseController;
import com.caitu99.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.integral.domain.Consume;
import com.caitu99.service.integral.domain.IntegralLifting;
import com.caitu99.service.integral.service.ConsumeService;
import com.caitu99.service.integral.service.IntegralLiftingService;
import com.caitu99.service.sys.domain.Config;
import com.caitu99.service.sys.service.ConfigService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserAuth;
import com.caitu99.service.user.service.UserAuthService;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;
import com.caitu99.service.utils.unionpay.UnionOpens;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/lifting")
public class IntegralLiftingController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(IntegralLiftingController.class);

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

	// 提现
	@RequestMapping(value="/money/take/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String take(Long integral, Long userId, String paypass) throws CryptoException {
		// 初始化
		Long caituIntegral = 100L;
		ApiResult<Boolean> result = new ApiResult<Boolean>();
		result.setCode(0);

        paypass = AESCryptoUtil.encrypt(paypass);

		// 时间限制
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (hour < 5 || hour >= 22)
            throw new ApiException(2401, "每日22点-次日5点为系统结算时间，暂不支持提现，请明日再试");

		// 紧急关闭
		Config config = configService.selectByKey("Integrallifting");
		if ("false".equals(config.getValue()))
            throw new ApiException(2402, "系统维护中，提现功能暂不可用");

		User user = userService.getById(userId);
		// 校验支付密码
		if (!paypass.equals(user.getPaypass()))
            throw new ApiException(2403, "支付密码错误");

		//校验财币
		if (null == user.getIntegral() || user.getIntegral() < integral)
            throw new ApiException(2404, "您的积分不足");

		// 限制提现金额
		if (integral < 10 * caituIntegral)
            throw new ApiException(2406, "提现金额必须大于10元");

		if (integral > 50000 * caituIntegral)
            throw new ApiException(2407, "提现金额不能大于50000元");

		// 判断延付与实时付
		IntegralLifting integralLifting = new IntegralLifting();

        // get total of today
		integralLifting.setTime(XStringUtil.getBeginOfToday());
		Long count = integralliftingService.countIntergral(integralLifting);
        if (count == null)
            count = 0L;

		if (integral + count > 5000000 * caituIntegral)
            throw new ApiException(2408, "当日平台提现已达上限，请明日再试");

        //校验用户绑定信息
        UserAuth userAuth = userAuthService.selectByUserId(user.getId());
        if (userAuth == null)
            throw new ApiException(2045, "您还没有绑定银行卡");

        Consume consume = new Consume();
        consume.setIntegral(integral.intValue());
        consume.setUserid(user.getId());
        consume.setStatus(1);
        consume.setUsetype(1L);
        consume.setRegulation(2);

        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");

        // 大于10W延时付
		if (integral + count > 100000 * caituIntegral) {
			String results = integralliftingService.paydelay(user, integral);
			consume.setUseid(results);
			consumeService.insert(consume);
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
			return JSON.toJSONString(results);
		}

        integralLifting.setUserid(user.getId());
        Long countUser = integralliftingService.countIntergralByUser(integralLifting);
        if (countUser == null)
            countUser = 0L;

		if (countUser + integral <= 500 * caituIntegral) {
			String orderno = integralliftingService.payNow(user, integral);
			// 第三方银联接口
			UnionOpens unionOpens = new UnionOpens();
			Map map = null;
			IntegralLifting ilfs = new IntegralLifting();
			try {
				map = unionOpens.integrallifting(integral, userAuth.getBindId(), orderno);
				if ("58".equals(map.get("retCode"))) {
					integralliftingService.refundintegral(user); // 退回积分
                    throw new ApiException(2401, "每日22点-次日5点为系统结算时间，暂不支持提现，请明日再试");
                }
				if (!"2".equals(map.get("orderStatus"))) {
					integralliftingService.refundintegral(user); // 退回积分
                    throw new ApiException(2409, "网络繁忙，请重试");
                }

				ilfs.setOrderstatus(Integer.valueOf((String) map.get("orderStatus")));
				ilfs.setOrderno((String) map.get("orderNo"));
				Date date = new Date();
				date.setTime(Long.valueOf((String) map.get("processDate")));
				ilfs.setProcessdate(date);
				ilfs.setRetdesc((String) map.get("retDesc"));
				ilfs.setUserid(user.getId());
				integralliftingService.updateByPrimaryKeySelective(ilfs);

				// 消费记录
				consume.setUseid(orderno);
				consumeService.insert(consume);
				String message ="您已成功提现"
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
			} catch (Exception ignored) {

			}

			// 如果发生异常
			if (map == null) {
				Map maps = null;
				try {
					maps = unionOpens.getOrder(orderno);
				} catch (Exception e) {
					logger.error("get order error:", e);
				}
                String retCode = (String) maps.get("retCode");
				if (!retCode.equals("0000")) {
					integralliftingService.refundintegral(user); // 退回积分
                    throw new ApiException(2409, "网络繁忙，请重试");
                }

				Map ordermap = (Map) maps.get("orderDetail");
				String status = (String) ordermap.get("orderStatus");
				String retDesc = (String) ordermap.get("retDesc");
				String orderNo = (String) ordermap.get("orderNo");
				String processDate = (String) ordermap.get("processDate");
				// 当银行接口正常调用
				if (status.trim().equals("2")) {
					// 按原逻辑执行
					ilfs.setOrderstatus(Integer.valueOf(status));
					ilfs.setOrderno(orderNo);
					Date date = new Date();
					date.setTime(Long.valueOf(processDate));
					ilfs.setProcessdate(date);
					ilfs.setRetdesc(retDesc);
					ilfs.setUserid(user.getId());
					integralliftingService.updateByPrimaryKeySelective(ilfs);
					// 消费记录
					consume.setUseid(orderNo);
					consumeService.insert(consume);
					result.setMessage("您已成功提现"
							+ df.format(((double) integral / caituIntegral - 2L))
							+ "元至"
							+ userAuth.getBankname()
							+ "(尾号"
							+ userAuth.getCardNo().substring(
									userAuth.getCardNo().length() - 4,
									userAuth.getCardNo().length())
							+ "),预计2小时到账");
					result.setData(true);
					return JSON.toJSONString(result);
				} else {
					integralliftingService.refundintegral(user); // 退回积分
                    throw new ApiException(2409, "网络繁忙，请重试");
				}
			}

			// 消费记录
			consume.setUseid(orderno);
			consumeService.insert(consume);
			result.setMessage("您已成功提现"
					+ df.format(((double) integral / caituIntegral - 2L))
					+ "元至"
					+ userAuth.getBankname()
					+ "(尾号"
					+ userAuth.getCardNo().substring(
							userAuth.getCardNo().length() - 4,
							userAuth.getCardNo().length()) + "),预计2小时到账");
			result.setData(true);
			return JSON.toJSONString(result);
		} else {
			// 延时付
			String results = integralliftingService.paydelay(user, integral);
			// 消费记录
			consume.setUseid(results);
			consumeService.insert(consume);
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