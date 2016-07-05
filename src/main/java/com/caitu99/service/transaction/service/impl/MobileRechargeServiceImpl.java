/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.constants.SysConstants;
import com.caitu99.service.exception.RechargeException;
import com.caitu99.service.life.domain.PhoneRechargeRecord;
import com.caitu99.service.transaction.controller.vo.CheckResult;
import com.caitu99.service.transaction.controller.vo.RechargeResult;
import com.caitu99.service.transaction.dao.AccountDetailMapper;
import com.caitu99.service.transaction.dao.AccountMapper;
import com.caitu99.service.transaction.dao.MobileRechargeRecordMapper;
import com.caitu99.service.transaction.dao.TransactionRecordMapper;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.Order;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.MobileRechargeService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.calculate.CalculateUtils;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.string.StrUtil;

@Service
public class MobileRechargeServiceImpl implements MobileRechargeService {

	private static final Logger logger = LoggerFactory
			.getLogger(MobileRechargeServiceImpl.class);

	// 默认编码集

	@Autowired
	private MobileRechargeRecordMapper mobileRechargeRecordMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private TransactionRecordMapper transactionRecordMapper;
	@Autowired
	private AccountDetailMapper accountDetailMapper;
	@Autowired
	private AccountMapper accountMapper;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private TransactionRecordService transactionRecordService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private OrderService orderService;

	interface ResultKey {
		String ERROR_CODE = "error_code";
		String RESULT = "result";
		String REASON = "reason";
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "RechargeException" })
	public RechargeResult checkAndRecharge(Long userId, String mobile,
			String cardNum,User user,String payPass) {
		RechargeResult rechargeResult = new RechargeResult();
		CheckResult checkResult = new CheckResult();
		try {
			logger.info("进入充值");
			// 获取账户信息
			Account account = accountService.selectByUserId(user.getId());
			account = accountService.selectByPrimaryKeyForUpdate(account.getId());
			logger.info("开始锁定");
			if (null == account) {
				logger.error("账户不存在");
				rechargeResult.setSuccess(false);
				rechargeResult.setResult("账户不存在");
				rechargeResult.setCode(3001);
				return rechargeResult;
			}

			// 验证密码
			if (!AESCryptoUtil.decrypt(user.getPaypass()).equals(payPass)) {
				rechargeResult.setSuccess(false);
				rechargeResult.setResult("支付密码错误");
				rechargeResult.setCode(2201);
				return rechargeResult;
			}
			
			// 验证用户财币
	        Integer scale = appConfig.phoneRechargeScale;
			BigDecimal needCardNum = new BigDecimal(cardNum)
					.multiply(new BigDecimal(100))
					.multiply(new BigDecimal(scale).divide(new BigDecimal(100)));
			if (null == account.getAvailableIntegral()
					|| new BigDecimal(account.getAvailableIntegral())
							.compareTo(needCardNum) == -1) {
				rechargeResult.setSuccess(false);
				rechargeResult.setResult("用户财币不足");
				rechargeResult.setCode(2202);
				return rechargeResult;
			}
			// 验证消费总额
	        Integer count = transactionRecordService.getLastDayConsumes(userId);
	        if (count > appConfig.maxConsumesPerDay) {
	            String msg = "抱歉，您今天的累积消费财币已超过限额（" + appConfig.maxConsumesPerDay + "财币）";
	            rechargeResult.setSuccess(false);
				rechargeResult.setResult(msg);
				rechargeResult.setCode(2219);
				return rechargeResult;
	        }
	        
	        // 验证当日话费充值总额
	        Long totalAmount = mobileRechargeRecordMapper.sameDayPhoneRechargeAmount(userId);
	        if(totalAmount == null){
	        	totalAmount = 0L;
	        }
	        if (totalAmount + Long.valueOf(cardNum)  > appConfig.phoneRechargeAmountLimit) {
	            String msg = "抱歉，您今天的累计充值话费额超过了限额（" + appConfig.phoneRechargeAmountLimit + "元）";
	            rechargeResult.setSuccess(false);
				rechargeResult.setResult(msg);
				rechargeResult.setCode(2219);
				return rechargeResult;
	        }
			
			// checked
	        checkResult = this.check(mobile, cardNum);
			if (checkResult.isSuccess()) {//
				// charge
				rechargeResult = this.recharge(userId, mobile, cardNum);
				return rechargeResult;
			}
	        
			logger.info("充值完毕");
			rechargeResult.setSuccess(false);
			rechargeResult.setResult(checkResult.getResult());
			return rechargeResult;
		} catch (Exception e) {
			logger.warn("手机话费充值失败：{}", e);
			throw new RechargeException(-1, "系统繁忙");
		}
	}

	/**
	 * 调用第三方接口充值话费
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: check
	 * @param mobile
	 *            手机号
	 * @param cardNum
	 *            充值金额
	 * @return
	 * @date 2015年11月2日 下午5:37:15
	 * @author lawrence
	 */
	private RechargeResult recharge(Long userId, String mobile, String cardNum) {
		RechargeResult rechargeResult = new RechargeResult();
		try {
			// 参数组装
			String orderId = this.getRechargeOrderId(userId);
			String sign = this.sign(mobile, cardNum, orderId);
			String url = this.joinChargeUrl(mobile, cardNum, orderId, sign);
			logger.debug("[第三方手机直充接口] orderId：{}, signKey：{}, url:{}", orderId,
					sign, url);
			// 获得请求JSON字符串

			 String httpJsonResult = "";
			 if(appConfig.isDevMode){
				 httpJsonResult = "{\"error_code\":\"0\",\"result\":\"success\"}";
			}else{
				httpJsonResult = httpURLGet(url,
						SysConstants.DEFAULT_CHATSET);
			}
			JSONObject jsonObject = JSON.parseObject(httpJsonResult);// 转化为JSON类
			String resultCode = jsonObject.getString(ResultKey.ERROR_CODE);// 得到错误码
			logger.info("调用第三方检测手机号码是否能充值接口成功,手机号：{},充值额度：{}，第三方返回数据:{}",
					mobile, cardNum, httpJsonResult);

			boolean isSuccess = "0".equals(resultCode) ? true : false;
			PhoneRechargeRecord phoneRechargeRecord = new PhoneRechargeRecord();
			phoneRechargeRecord.setCardno(Integer.valueOf(cardNum));
			phoneRechargeRecord.setFlag(isSuccess ? 1 : 0);
			phoneRechargeRecord.setOrderid(orderId);
			phoneRechargeRecord.setPhoneno(mobile);
			phoneRechargeRecord.setRechargeDate(new Date());
			phoneRechargeRecord.setResult(httpJsonResult);
			phoneRechargeRecord.setUserId(Long.valueOf(userId));
			mobileRechargeRecordMapper.insert(phoneRechargeRecord);
			if (isSuccess) {
		        Integer scale = appConfig.phoneRechargeScale;
				BigDecimal needCardNum = new BigDecimal(cardNum)
						.multiply(new BigDecimal(100))
						.multiply(new BigDecimal(scale).divide(new BigDecimal(100)));
				// userService.updateUserIntegral(Long.parseLong(userId),
				// needCardNum.multiply(new BigDecimal(-1)).intValue());
				// 更新用户账户信息
				this.updateAccount(needCardNum.longValue(),
						userId, orderId);
			}
			rechargeResult.setResult(httpJsonResult);// 使用返回json结果
			rechargeResult.setOrderid(orderId);// 无论成功或失败，都记录下orderId
			rechargeResult.setOrderid(orderId);
			rechargeResult.setSuccess(isSuccess);
		} catch (Exception e) {
			// rechargeResult.setSuccess(false);
			// rechargeResult.setResult("调用第三方手机直充接口失败");
			logger.error("调用第三方手机直充接口发生异常,手机号：{},充值额度：{}", mobile, cardNum);
			throw new RechargeException(2203, e.getMessage());
		}
		return rechargeResult;
	}

	private void updateAccount(Long integral, Long userId, String orderNo) {
		// 保存交易记录
		TransactionRecord transactionRecord = new TransactionRecord();
		transactionRecord.setChannel(2);// 聚合数据
		transactionRecord.setComment("");
		transactionRecord.setCreateTime(new Date());
		transactionRecord.setUpdateTime(new Date());
		transactionRecord.setInfo("fen生活");
		transactionRecord.setOrderNo(orderNo);
		transactionRecord.setPayType(1);// 财币
		transactionRecord.setPicUrl("");
		transactionRecord.setStatus(2);// 成功
		transactionRecord.setSuccessTime(new Date());
		transactionRecord.setTotal(integral);
		transactionRecord.setTransactionNumber(orderNo);
		transactionRecord.setType(1);// 充值
		transactionRecord.setUserId(userId);
		transactionRecordMapper.insert(transactionRecord);

		// 保存账户明细
		AccountDetail accountDetail = new AccountDetail();
		accountDetail.setGmtCreate(new Date());
		accountDetail.setGmtModify(new Date());
		accountDetail.setIntegralChange(integral);
		accountDetail.setMemo("手机充值");
		accountDetail.setRecordId(transactionRecord.getId());
		accountDetail.setType(2);// 出分
		accountDetail.setUserId(userId);
		accountDetailMapper.insert(accountDetail);

		// 更新账户信息
		Account account = accountMapper.selectByUserId(userId);
		BigDecimal totalIntegral = new BigDecimal(account.getTotalIntegral())
		.subtract(new BigDecimal(integral));
		BigDecimal availableIntegral = new BigDecimal(account.getAvailableIntegral())
		.subtract(new BigDecimal(integral));
		account.setAvailableIntegral(availableIntegral.longValue());
		account.setTotalIntegral(totalIntegral.longValue());
		account.setUserId(userId);
		account.setGmtModify(new Date());
		accountMapper.updateByPrimaryKey(account);
	}

	/**
	 * 调用第三方接口检测手机号码是否能充值
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: check
	 * @param mobile
	 * @param cardNum
	 * @return
	 * @date 2015年11月2日 下午5:37:15
	 * @author lawrence
	 */
	private CheckResult check(String mobile, String cardNum) {
		CheckResult checkResult = new CheckResult();
		try {
			String url = this.joinCheckUrl(mobile, cardNum);
			logger.debug("[第三方手机直充检测接口] mobile：{}, cardNum：{}, url:{}", mobile,
					cardNum, url);

			// 请求
			String jsonResult = httpURLGet(url, SysConstants.DEFAULT_CHATSET);// 得到JSON字符串
			JSONObject object = JSON.parseObject(jsonResult);// 转化为JSON类
			String resultCode = object.getString(ResultKey.ERROR_CODE);// 得到错误码
			String result = object.getString("reason");
			checkResult.setResult(result);
			logger.info("调用第三方检测手机号码是否能充值接口成功,手机号：{},充值额度：{}，第三方返回数据:{}",
					mobile, cardNum, jsonResult);

			// 错误码判断
			boolean isSuccess = "0".equals(resultCode) ? true : false;
			checkResult.setSuccess(isSuccess);
		} catch (Exception e) {
			checkResult.setSuccess(false);
			checkResult.setResult("调用第三方检测手机号码是否能充值接口失败");
			logger.error("调用第三方检测手机号码是否能充值接口失败,手机号：{},充值额度：{}", mobile, cardNum);
		}
		return checkResult;

	}

	/**
	 * 通过HttpURLConnection发起请求
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: httpURLGet
	 * @param _url
	 * @param charset
	 * @return
	 * @throws IOException
	 * @date 2015年11月2日 下午4:34:57
	 * @author lawrence
	 */
	private String httpURLGet(String _url, String charset) throws IOException {
		BufferedReader reader = null;
		HttpURLConnection connection = null;
		InputStream inputStream = null;
		try {
			StringBuffer buffer = new StringBuffer();
			String userAgent = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.3; WOW64; Trident/7.0; LCJB)";// 模拟浏览器
			URL url = new URL(_url);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestProperty("User-agent", userAgent);
			connection.connect();
			inputStream = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream,
					charset));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				buffer.append(strRead);
				buffer.append("\r\n");
			}
			return buffer.toString();

		} finally {
			if (null != reader) {
				reader.close();
			}
			if (null != connection) {
				connection.disconnect();
			}
			if (null != inputStream) {
				inputStream.close();
			}
		}
	}

	/**
	 * 获取订单号
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: getRechargeOrderId
	 * @param userId
	 * @return
	 * @date 2015年11月2日 下午3:53:31
	 * @author lawrence
	 */
	private String getRechargeOrderId(Long userId) {
		return new StringBuilder().append(new Date().getTime()).append("_")
				.append(userId).toString();
	}

	/**
	 * 获取加密参数
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: sign
	 * @param mobile
	 * @param cardNum
	 * @param orderId
	 * @return
	 * @date 2015年11月2日 下午3:53:56
	 * @author lawrence
	 */
	private String sign(String mobile, String cardNum, String orderId) {
		return StrUtil
				.toMD5(new StringBuilder()
						.append(SpringContext.getBean(AppConfig.class).rechargePhoneOpenId)
						.append(SpringContext.getBean(AppConfig.class).rechargePhoneKey)
						.append(mobile).append(cardNum).append(orderId)
						.toString());
	}

	// public static void main(String[] args) {
	//
	// System.out.println(StrUtil.toMD5(new StringBuilder()
	// .append("JHed686fae7c560bad22ad3d43154ecdf0")
	// .append("ed41d996322e961fc43763ff8b444d18")
	// .append("15700114735")
	// .append(5)
	// .append("test101111111")
	// .toString()));
	// }
	/**
	 * 获取请求Url
	 * 
	 * @Description: (
	 * 
	 *               <pre>
	 * Example:
	 *  http://op.juhe.cn/ofpay/mobile/onlineorder?key=*********************
	 *  &phoneno=131****2365&cardnum=100&orderid=123456&sign=*****
	 * </pre>
	 * 
	 *               )
	 * @Title: joinChargeUrl
	 * @param mobile
	 * @param cardNum
	 * @param orderId
	 * @param sign
	 * @return
	 * @date 2015年11月2日 下午3:54:11
	 * @author lawrence
	 */
	private String joinChargeUrl(String mobile, String cardNum, String orderId,
			String sign) {
		return new StringBuilder(
				SpringContext.getBean(AppConfig.class).rechargePhoneOrderUrl)
				.append("?phoneno=")
				.append(mobile)
				.append("&cardnum=")
				.append(cardNum)
				.append("&orderid=")
				.append(orderId)
				.append("&sign=")
				.append(sign)
				.append("&key=")
				.append(SpringContext.getBean(AppConfig.class).rechargePhoneKey)
				.toString();
	}

	/**
	 * 手机充值号码验证 url拼接
	 * 
	 * @Description: (
	 * 
	 *               <pre>
	 * Example:
	 * 请求地址：http://op.juhe.cn/ofpay/mobile/telcheck
	 * 请求参数：phoneno=*&cardnum=10&key=KEY
	 * 请求方式：GET
	 * </pre>
	 * 
	 *               )
	 * @Title: joinCheckUrl
	 * @param mobile
	 * @param cardNum
	 * @return
	 * @date 2015年11月2日 下午5:29:48
	 * @author lawrence
	 */
	private String joinCheckUrl(String mobile, String cardNum) {
		return new StringBuilder(
				SpringContext.getBean(AppConfig.class).rechargePhoneCheckedUrl)
				.append("?phoneno=")
				.append(mobile)
				.append("&cardnum=")
				.append(cardNum)
				.append("&key=")
				.append(SpringContext.getBean(AppConfig.class).rechargePhoneKey)
				.toString();
	}

	@Override
	public boolean rechargeByOrder(Long userId, String orderNo) {
		RechargeResult rechargeResult = new RechargeResult();
		CheckResult checkResult = new CheckResult();
		try {
			logger.info("进入充值");
			
			Order order = orderService.queryOrder(orderNo);
			String mobile = order.getMemo().trim();
			String cardNum = CalculateUtils.converPennytoYuan(order.getPrice());
			
			// checked
	        checkResult = this.check(mobile, cardNum);
			if (checkResult.isSuccess()) {//
				// charge
				rechargeResult = this.recharge(userId, mobile, cardNum);
				return false;
			}
	        
			logger.info("充值完毕");
			rechargeResult.setSuccess(false);
			rechargeResult.setResult(checkResult.getResult());
			return true;
		} catch (Exception e) {
			logger.warn("手机话费充值失败：userId = {},{}",userId,e);
			return false;
		}
	}

}
