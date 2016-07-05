package com.caitu99.service.transaction.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.exception.AccountException;
import com.caitu99.service.exception.OrderException;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.goods.domain.ReceiveStock;
import com.caitu99.service.goods.service.ReceiveStockService;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.sys.domain.Config;
import com.caitu99.service.sys.service.ConfigService;
import com.caitu99.service.transaction.controller.vo.AccountResult;
import com.caitu99.service.transaction.dao.AccountDetailMapper;
import com.caitu99.service.transaction.dao.AccountMapper;
import com.caitu99.service.transaction.dao.OrderMapper;
import com.caitu99.service.transaction.dao.TransactionRecordMapper;
import com.caitu99.service.transaction.dao.UserCouponMapper;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.Order;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.domain.UserCoupon;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.MobileRechargeService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.user.dao.UserMapper;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserAuth;
import com.caitu99.service.user.service.UserAuthService;
import com.caitu99.service.user.service.UserBankCardService;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.XStringUtil;

/**
 * 
 * @Description: (类职责详细描述,可空)
 * @ClassName: AccountServiceImpl
 * @author lhj
 * @date 2015年12月1日 下午8:06:38
 * @Copyright (c) 2015-2020 by caitu99
 */
@Service
public class AccountServiceImpl implements AccountService {

	private final static Logger logger = LoggerFactory
			.getLogger(AccountServiceImpl.class);

	@Autowired
	private TransactionRecordMapper transactionRecordMapper;
	@Autowired
	private AccountDetailMapper accountDetailMapper;
	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private ReceiveStockService receiveStockService;

	@Autowired
	private UserCouponMapper userCouponMapper;
	@Autowired
	private TransactionRecordService transactionRecordService;
	@Autowired
	private AccountDetailService accountDetailService;
	@Autowired
	private PushMessageService pushMessageService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private UserBankCardService userBankCardService;
	
	@Autowired
	MobileRechargeService mobileRechargeService;
	
	@Autowired
	private UserAuthService userAuthService;

	interface TradeType {
		Long INCREASE = 1L;
		Long REDUCE = -1L;
	}

	/**
	 * 充财币
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "AccountException" })
	@Override
	public AccountResult add(TransactionRecordDto transactionRecordDto) {
		// 初始化
		AccountResult accountResult;
		accountResult = this.validate(transactionRecordDto);
		accountResult.setSuccess(false);
		// 账户验证
		Account account = accountMapper.selectByUserId(transactionRecordDto
				.getUserId());
		if (null == account) {
			accountResult.setCode(3001);
			accountResult.setResult("账户不存在");
			return accountResult;
		}

		boolean isOrderNoNull = StringUtils.isEmpty(transactionRecordDto
				.getOrderNo()) ? true : false;
		boolean isIntegralNull = transactionRecordDto.getTotal() == null ? true
				: false;
		if (isOrderNoNull && isIntegralNull) {
			accountResult.setCode(3109);
			accountResult.setResult("订单号或财币数不能为空");
			return accountResult;
		}
		// 订单验证
		Order order = null;
		if (!isOrderNoNull) {
			order = orderMapper.selectByPrimaryKey(transactionRecordDto
					.getOrderNo());
			if (null == order) {
				accountResult.setCode(3003);
				accountResult.setResult("订单不存在");
				return accountResult;
			}
		}
		try {
			// 添加交易记录
			Long recordId = this.saveTransaction(transactionRecordDto, order,
					0L);
			// 添加交易明细
			this.saveAccountDetail(recordId, transactionRecordDto,
					TradeType.INCREASE, order);
			// 更新账户
			this.updateAccount(account, transactionRecordDto,
					TradeType.INCREASE, order);
			// 更新账单
			if (!isOrderNoNull) {
				this.updateOrder(transactionRecordDto);
			}
		} catch (Exception e) {
			logger.error("交易失败:{}", e);
			throw new AccountException(3102, e.getMessage());
		}
		// 数据返回
		accountResult.setCode(3101);
		accountResult.setSuccess(true);
		accountResult.setResult("交易成功");
		return accountResult;
	}

	/**
	 * 扣财币
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "AccountException" })
	@Override
	public AccountResult pay(TransactionRecordDto transactionRecordDto) {

		// 初始化
		AccountResult accountResult;
		accountResult = this.validate(transactionRecordDto);
		if (!accountResult.isSuccess())
			return accountResult;
		// 账户验证
		accountResult.setSuccess(false);
		Account account = accountMapper.selectByUserId(transactionRecordDto
				.getUserId());
		//锁账号
		account = accountMapper.selectByPrimaryKeyForUpdate(account.getId());
		
		if (null == account) {
			accountResult.setCode(3001);
			accountResult.setResult("账户不存在");
			return accountResult;
		}
		boolean isOrderNoNull = StringUtils.isEmpty(transactionRecordDto
				.getOrderNo()) ? true : false;
		boolean isIntegralNull = transactionRecordDto.getTotal() == null ? true
				: false;
		if (isOrderNoNull && isIntegralNull) {
			accountResult.setCode(3109);
			accountResult.setResult("订单号或价格不能为空");
			return accountResult;
		}
		// 订单验证
		Order order = null;
		if (!isOrderNoNull) {
			order = orderMapper.selectByPrimaryKey(transactionRecordDto
					.getOrderNo());
			if (null == order) {
				accountResult.setCode(3003);
				accountResult.setResult("订单不存在");
				return accountResult;
			}
			// 状态：0.生成/待付款，1.已付款，10，完成交易 20.超时关闭，30.删除
			if (1 == order.getStatus()) {
				accountResult.setCode(3004);
				accountResult.setResult("该订单已付款");
				return accountResult;
			} else if (10 == order.getStatus()) {
				accountResult.setCode(3005);
				accountResult.setResult("已完成交易");
				return accountResult;
			} else if (20 == order.getStatus()) {
				accountResult.setCode(3006);
				accountResult.setResult("该订单已超时关闭");
				return accountResult;
			} else if (30 == order.getStatus()) {
				accountResult.setCode(3007);
				accountResult.setResult("该订单已删除");
				return accountResult;
			}
			transactionRecordDto.setTotal(order.getPrice());
			transactionRecordDto.setTubi(order.getTubi());
			transactionRecordDto.setRmb(order.getRmb());
		}
		if (account.getAvailableIntegral().compareTo(
				isOrderNoNull ? transactionRecordDto.getTotal() : order
						.getPrice()) == -1) {
			accountResult.setCode(3002);
			accountResult.setResult("您的财币不足");
			return accountResult;
		}
		try {
			// 使用券积分支付的部分
			Long userCouponPay = 0L;
			if (1 == transactionRecordDto.getType().intValue()) {
				userCouponPay = consumeUserCoupon(transactionRecordDto);
			}

			// 添加交易记录
			Long recordId = this.saveTransaction(transactionRecordDto, order,
					userCouponPay);
			// 添加交易明细
			this.saveAccountDetail(recordId, transactionRecordDto,
					TradeType.REDUCE, order);
			// 更新账户
			this.updateAccount(account, transactionRecordDto, TradeType.REDUCE,
					order);
			// 更新账单
			if (!isOrderNoNull) {
				this.updateOrder(transactionRecordDto);
				this.updateReceiveStock(transactionRecordDto.getUserId(),
						transactionRecordDto.getOrderNo());
			}
		} catch (Exception e) {
			logger.error("支付失败:{}", e);
			throw new AccountException(3102, e.getMessage());
		}

		accountResult.setCode(3101);
		accountResult.setSuccess(true);
		accountResult.setResult("支付成功");
		return accountResult;
	}
	
	/**
	 * 扣财币
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "AccountException" })
	@Override
	public AccountResult payNew(TransactionRecordDto transactionRecordDto) {

		// 初始化
		AccountResult accountResult;
		accountResult = this.validate(transactionRecordDto);
		if (!accountResult.isSuccess())
			return accountResult;
		// 账户验证
		accountResult.setSuccess(false);
		Account account = accountMapper.selectByUserId(transactionRecordDto
				.getUserId());
		//锁账号
		account = accountMapper.selectByPrimaryKeyForUpdate(account.getId());
		
		if (null == account) {
			accountResult.setCode(3001);
			accountResult.setResult("账户不存在");
			return accountResult;
		}
		boolean isOrderNoNull = StringUtils.isEmpty(transactionRecordDto
				.getOrderNo()) ? true : false;
		boolean isIntegralNull = transactionRecordDto.getTotal() == null ? true
				: false;
		if (isOrderNoNull && isIntegralNull) {
			accountResult.setCode(3109);
			accountResult.setResult("订单号或价格不能为空");
			return accountResult;
		}
		// 订单验证
		Order order = null;
		if (!isOrderNoNull) {
			order = orderMapper.selectByPrimaryKey(transactionRecordDto
					.getOrderNo());
			if (null == order) {
				accountResult.setCode(3003);
				accountResult.setResult("订单不存在");
				return accountResult;
			}
			// 状态：0.生成/待付款，1.已付款，10，完成交易 20.超时关闭，30.删除
			if (1 == order.getStatus()) {
				accountResult.setCode(3004);
				accountResult.setResult("该订单已付款");
				return accountResult;
			} else if (10 == order.getStatus()) {
				accountResult.setCode(3005);
				accountResult.setResult("已完成交易");
				return accountResult;
			} else if (20 == order.getStatus()) {
				accountResult.setCode(3006);
				accountResult.setResult("该订单已超时关闭");
				return accountResult;
			} else if (30 == order.getStatus()) {
				accountResult.setCode(3007);
				accountResult.setResult("该订单已删除");
				return accountResult;
			}
			transactionRecordDto.setTotal(order.getCaibi());
			transactionRecordDto.setTubi(order.getTubi());
			transactionRecordDto.setRmb(order.getRmb());
		}
		if (account.getAvailableIntegral().compareTo(
				isOrderNoNull ? transactionRecordDto.getTotal() : order
						.getPrice()) == -1) {
			accountResult.setCode(3002);
			accountResult.setResult("您的财币不足");
			return accountResult;
		}if (account.getTubi().compareTo(
				isOrderNoNull ? 0 : order
						.getTubi()) == -1) {
			accountResult.setCode(3002);
			accountResult.setResult("您的途币不足");
			return accountResult;
		}
		try {
			// 添加交易记录
			Long recordId = this.saveTransaction(transactionRecordDto, order,
					0L);
			// 添加交易明细
			this.saveAccountDetail(recordId, transactionRecordDto,
					TradeType.REDUCE, order);
			// 更新账户
			this.updateAccountNew(account, transactionRecordDto, TradeType.REDUCE,
					order);
			// 更新账单
			this.updateOrder(transactionRecordDto);
			this.updateReceiveStock(transactionRecordDto.getUserId(),
					transactionRecordDto.getOrderNo());
		} catch (Exception e) {
			logger.error("支付失败:{}", e);
			throw new AccountException(3102, e.getMessage());
		}

		accountResult.setCode(3101);
		accountResult.setSuccess(true);
		accountResult.setResult("支付成功");
		return accountResult;
	}
	
	private Long saveTransaction(TransactionRecordDto transactionRecordDto,
			Order order, Long userCouponPay) {
		TransactionRecord transactionRecord = new TransactionRecord();
		transactionRecord.setChannel(transactionRecordDto.getChannel());
		transactionRecord.setComment(transactionRecordDto.getComment());
		transactionRecord.setCreateTime(new Date());
		transactionRecord.setInfo(transactionRecordDto.getInfo());
		transactionRecord.setOrderNo(transactionRecordDto.getOrderNo());
		transactionRecord.setPayType(1);// 财币
		transactionRecord.setPicUrl(transactionRecordDto.getPicUrl());
		transactionRecord.setStatus(2);// 成功
		transactionRecord.setSuccessTime(new Date());
		Long total = 0L;
		
		if(null != transactionRecordDto.getTotal()){
			if(order == null){
				total = transactionRecordDto.getTotal();
			}else{
				total = order.getPrice();
			}
		}
	
		
		transactionRecord.setTotal(total);
		transactionRecord.setTubi(transactionRecordDto.getTubi() == null? 0:transactionRecordDto.getTubi());
		transactionRecord.setTransactionNumber(transactionRecordDto
				.getTransactionNumber());
		transactionRecord.setType(transactionRecordDto.getType());
		transactionRecord.setUpdateTime(new Date());
		transactionRecord.setUserId(transactionRecordDto.getUserId());
		transactionRecord.setSource(transactionRecordDto.getSource());
		transactionRecord.setCouponIntegral(userCouponPay);
		// 添加交易记录
		transactionRecordMapper.insert(transactionRecord);
		return transactionRecord.getId();
	}
	


	/**
	 * 
	 * 
	 * @Description: (添加账户明细)
	 * @Title: saveAccountDetail
	 * @param userid
	 * @param recordId
	 * @param integral
	 * @date 2015年12月2日 下午12:14:53
	 * @author lhj
	 */
	private void saveAccountDetail(Long recordId,
			TransactionRecordDto transactionRecordDto, Long tradeType,
			Order order) {
		
		if(null != transactionRecordDto.getTotal() && transactionRecordDto.getTotal() != 0){
			AccountDetail accountDetail = new AccountDetail();
			accountDetail.setGmtCreate(new Date());
			accountDetail.setGmtModify(new Date());
			accountDetail.setIntegralChange(order == null ? transactionRecordDto
					.getTotal() : order.getPrice());
			accountDetail.setMemo("");
			accountDetail.setRecordId(recordId);
			accountDetail.setType(tradeType > 0 ? 1 : 2);
			accountDetail.setUserId(transactionRecordDto.getUserId());
			accountDetailMapper.insert(accountDetail);
			
		}
		
		if(null != transactionRecordDto.getTubi() && transactionRecordDto.getTubi() != 0){
			AccountDetail accountTubi = new AccountDetail();
			accountTubi.setGmtCreate(new Date());
			accountTubi.setGmtModify(new Date());
			accountTubi.setIntegralChange(transactionRecordDto.getTubi());
			accountTubi.setMemo("");
			accountTubi.setRecordId(recordId);
			accountTubi.setType(tradeType > 0 ? 3 : 4);
			accountTubi.setUserId(transactionRecordDto.getUserId());
			accountDetailMapper.insert(accountTubi);
		
		}
		
		
	}

	/**
	 * 
	 * 
	 * @Description: (更新账户信息)
	 * @Title: updateAccount
	 * @param account
	 * @param integral
	 * @date 2015年12月2日 下午12:17:36
	 * @author lhj
	 */
	private void updateAccount(Account account,
			TransactionRecordDto transactionRecordDto, Long tradeType,
			Order order) {
		
		if(null != transactionRecordDto.getTotal()){
			account.setAvailableIntegral(account.getAvailableIntegral()
					+ (order == null ? transactionRecordDto.getTotal() : order.getPrice()) * tradeType);
			account.setGmtModify(new Date());
			account.setTotalIntegral(account.getTotalIntegral()
					+ (order == null ? transactionRecordDto.getTotal() : order.getPrice()) * tradeType);
		}
		
		if(null != transactionRecordDto.getTubi()){
			account.setTubi(account.getTubi() + transactionRecordDto.getTubi());//送途币
		}
		
		accountMapper.updateByPrimaryKeySelective(account);
	}

	private void updateAccountNew(Account account,
			TransactionRecordDto transactionRecordDto, Long tradeType,
			Order order) {
		
		if(null != transactionRecordDto.getTotal()){
			account.setAvailableIntegral(account.getAvailableIntegral()
					+ (order == null ? transactionRecordDto.getTotal() : order.getCaibi()) * tradeType);
			account.setGmtModify(new Date());
			account.setTotalIntegral(account.getTotalIntegral()
					+ (order == null ? transactionRecordDto.getTotal() : order.getCaibi()) * tradeType);
		}
		
		if(null != transactionRecordDto.getTubi()){
			account.setTubi(account.getTubi() + transactionRecordDto.getTubi() * tradeType);
		}
		
		accountMapper.updateByPrimaryKeySelective(account);
	}
	
	private void updateOrder(TransactionRecordDto transactionRecordDto) {
		Order order = orderMapper.selectByPrimaryKey(transactionRecordDto
				.getOrderNo());
		order.setPayStatus(2);// 支付成功
		order.setPayTime(new Date());
		order.setPayType(1);// 财币
		order.setStatus(1);// 已付款
		order.setUpdateTime(new Date());
		orderMapper.updateByPrimaryKeySelective(order);
	}

	private void updateReceiveStock(Long userId, String orderNo) {
		List<ReceiveStock> list = receiveStockService
				.selectReceiveStockByOrderNoAndUserId(userId, orderNo);
		Date date = new Date();
		for (ReceiveStock receiveStock : list) {
			receiveStock.setStatus(2);
			receiveStock.setReceiveTime(date);
			receiveStockService.updateBySelective(receiveStock);
		}
	}

	private AccountResult validate(TransactionRecordDto transactionRecordDto) {
		AccountResult accountResult = new AccountResult();
		accountResult.setSuccess(false);
		// 渠道验证
		if (null == transactionRecordDto.getChannel()) {
			accountResult.setCode(3103);
			accountResult.setResult("渠道不能为空");
			return accountResult;
		}
		// // 订单号验证
		// if (StringUtils.isEmpty(transactionRecordDto.getOrderNo())) {
		// accountResult.setCode(3104);
		// accountResult.setResult("订单号不能为空");
		// return accountResult;
		// }
		// 流水号验证
		if (StringUtils.isEmpty(transactionRecordDto.getTransactionNumber())) {
			accountResult.setCode(3106);
			accountResult.setResult("流水号不能为空");
			return accountResult;
		}
		// 类型验证
		if (null == transactionRecordDto.getType()) {
			accountResult.setCode(3107);
			accountResult.setResult("类型不能为空");
			return accountResult;
		}
		// userid验证
		if (null == transactionRecordDto.getUserId()) {
			accountResult.setCode(3108);
			accountResult.setResult("用户id不能为空");
			return accountResult;
		}
		accountResult.setSuccess(true);
		return accountResult;
	}

	/**
	 * 充财币From银联
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "AccountException" })
	@Override
	public AccountResult addFromUnionPay(Long userid, Long unionid,
			Long integral, String orderNo, String tNo, Long clientId) {
		// 初始化
		AccountResult accountResult = new AccountResult();
		accountResult.setSuccess(false);
		// 用户账户验证
		Account userAccount = accountMapper.selectByUserId(userid);
		if (null == userAccount) {
			accountResult.setCode(3001);
			accountResult.setResult("用户账户不存在");
			return accountResult;
		}
		// 企业账户验证
		Account unionAccount = accountMapper.selectByUserId(unionid);
		if (null == unionAccount) {
			accountResult.setCode(3001);
			accountResult.setResult("银联账户不存在");
			return accountResult;
		}
		if (unionAccount.getAvailableIntegral().compareTo(integral) == -1) {
			accountResult.setCode(3002);
			accountResult.setResult("账户财币不足");
			return accountResult;
		}
		try {
			// 添加用户交易记录
			TransactionRecord userRecord = new TransactionRecord();
			userRecord.setChannel(1);
			userRecord.setComment("");
			userRecord.setCreateTime(new Date());
			userRecord.setInfo("银联");
			userRecord.setOrderNo(orderNo);
			userRecord.setPayType(1);// 财币
			userRecord.setPicUrl("");
			userRecord.setStatus(0);// 冻结
			userRecord.setSuccessTime(new Date());
			userRecord.setFreezeTime(new Date());
			userRecord.setTotal(integral);
			userRecord.setTransactionNumber(tNo);
			userRecord.setType(5);//累积
			userRecord.setUpdateTime(new Date());
			userRecord.setUserId(userid);
			userRecord.setCompanyId(clientId);
			transactionRecordMapper.insert(userRecord);
			// 添加企业交易记录
			TransactionRecord unionRecord = new TransactionRecord();
			unionRecord.setChannel(1);
			unionRecord.setComment("");
			unionRecord.setCreateTime(new Date());
			unionRecord.setInfo("送出给用户");
			unionRecord.setOrderNo(orderNo);
			unionRecord.setPayType(1);// 财币
			unionRecord.setPicUrl("");
			unionRecord.setStatus(0);// 冻结
			unionRecord.setSuccessTime(new Date());
			unionRecord.setFreezeTime(new Date());
			unionRecord.setTotal(integral);//统一为正，通过type标识扣费
			unionRecord.setTransactionNumber(tNo);
			unionRecord.setType(1);//企业是消费
			unionRecord.setUpdateTime(new Date());
			unionRecord.setUserId(unionid);
			unionRecord.setCompanyId(clientId);
			transactionRecordMapper.insert(unionRecord);

		} catch (Exception e) {
			logger.error("交易失败:{}", e);
			throw new AccountException(3102, e.getMessage());
		}
		// 数据返回
		accountResult.setCode(3101);
		accountResult.setSuccess(true);
		accountResult.setResult("交易成功");
		return accountResult;
	}

	@Override
	public Account selectByUserId(Long userId) {
		return accountMapper.selectByUserId(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.caitu99.service.transaction.service.AccountService#updateAccountByUserId
	 * (java.lang.Long)
	 */
	@Override
	public void updateAccountByUserId(Account record) {
		accountMapper.updateIntegralByUserId(record);
	}

	// @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName =
	// { "AccountException" })
	@Override
	public void initAccount() {
		List<User> allUsers = userMapper.all();
		for (User user : allUsers) {
			Account account = new Account();
			account.setAvailableIntegral(user.getIntegral() == null ? 0L : user
					.getIntegral());
			account.setFreezeIntegral(0L);
			account.setGmtCreate(new Date());
			account.setGmtModify(new Date());
			account.setTotalIntegral(user.getIntegral() == null ? 0L : user
					.getIntegral());
			account.setUserId(user.getId());
			accountMapper.insert(account);

			AccountDetail accountDetail = new AccountDetail();
			accountDetail.setGmtCreate(new Date());
			accountDetail.setGmtModify(new Date());
			accountDetail.setIntegralChange(user.getIntegral() == null ? 0L
					: user.getIntegral());
			accountDetail.setMemo("迁移");
			accountDetail.setRecordId(-1L);// 财币迁移
			accountDetail.setType(1);
			accountDetail.setUserId(user.getId());
			accountDetailMapper.insert(accountDetail);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.caitu99.service.transaction.service.AccountService#isEnough(java.
	 * lang.Long, java.lang.Long)
	 */
	@Override
	public boolean isEnough(Long userid, Long integral) {
		Account account = accountMapper.selectByUserId(userid);
		if (account != null && account.getAvailableIntegral() >= integral)
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.caitu99.service.transaction.service.AccountService#addAccount(java
	 * .lang.Long)
	 */
	@Override
	public void addNewAccount(Long userId) {

		Date dateNow = new Date();

		Account record = new Account();
		record.setUserId(userId);
		record.setAvailableIntegral(0L);
		record.setFreezeIntegral(0L);
		record.setTotalIntegral(0L);
		record.setTubi(0L);
		record.setGmtCreate(dateNow);
		record.setGmtModify(dateNow);
		accountMapper.insert(record);
	}

	/**
	 * @Description: (使用券积分)
	 * @Title: consumeUserCoupon
	 * @param transactionRecordDto
	 * @param pay
	 * @return
	 * @date 2015年12月7日 下午4:45:26
	 * @author Hongbo Peng
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	private Long consumeUserCoupon(TransactionRecordDto transactionRecordDto)
			throws Exception {
		Order order = orderMapper.selectByPrimaryKey(transactionRecordDto
				.getOrderNo());
		if(null == order){
			return 0L;
		}
		// 使用兑换券里的积分
		UserCoupon queryUserCoupon = new UserCoupon();
		queryUserCoupon.setUserId(transactionRecordDto.getUserId());
		queryUserCoupon.setStatus(1);// 查询状态是1的券
		List<UserCoupon> userCoupons = userCouponMapper
				.selectByUserId(queryUserCoupon);
		Map<Long, Long> map = getUserCouponMap(userCoupons, order.getPayPrice());
		Long couponPay = 0L;
		for (Long key : map.keySet()) {
			couponPay += map.get(key);
			UserCoupon coupon = userCouponMapper.selectByPrimaryKey(key);
			// 修改用户券的使用情况
			UserCoupon editUserCoupon = new UserCoupon();
			editUserCoupon.setId(key);
			editUserCoupon.setUseIntegral(map.get(key));
			Long availableIntegral = coupon.getAvailableIntegral()
					- map.get(key);
			editUserCoupon.setAvailableIntegral(availableIntegral);
			if (0 == availableIntegral.longValue()) {
				editUserCoupon.setStatus(2);
			}
			editUserCoupon.setUserId(transactionRecordDto.getUserId());
			editUserCoupon.setUpdateTime(new Date());
			userCouponMapper.updateByPrimaryKeySelective(editUserCoupon);
		}
		return couponPay;
	}

	/**
	 * @Description: (先消费最早过期的券)
	 * @Title: getUserCouponMap
	 * @param userCoupons
	 * @param pay
	 * @return
	 * @date 2015年12月7日 下午4:53:44
	 * @author Hongbo Peng
	 */
	private Map<Long, Long> getUserCouponMap(List<UserCoupon> userCoupons,
			Long pay) {
		Map<Long, Long> map = new HashMap<Long, Long>();
		long toPay = pay;
		for (UserCoupon userCoupon : userCoupons) {
			map.put(userCoupon.getId(), toPay);
			if (toPay <= userCoupon.getAvailableIntegral().longValue()) {
				return map;
			} else {
				toPay -= userCoupon.getAvailableIntegral();
			}
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.service.AccountService#updateAccount(com.caitu99.service.transaction.domain.Account, com.caitu99.service.transaction.dto.TransactionRecordDto, java.lang.Long)
	 */
	@Override
	public void updateAccount(Account account,
			TransactionRecordDto transactionRecordDto, Long reduce) {
		account.setAvailableIntegral(account.getAvailableIntegral()
				+ (transactionRecordDto.getTotal() * reduce));
		account.setGmtModify(new Date());
		account.setTotalIntegral(account.getTotalIntegral()
				+ (transactionRecordDto.getTotal() * reduce));
		account.setTubi(account.getTubi()
				+ (transactionRecordDto.getTubi() * reduce));
		accountMapper.updateByPrimaryKeySelective(account);
	}

	@Override
	public Account selectByPrimaryKeyForUpdate(Long id) {
		return accountMapper.selectByPrimaryKeyForUpdate(id);
	}

	@Override
	@Transactional
	public void sdkPay(Long enterpriseUserId, User user, Account account,Long payTubi, String payPassword, boolean isPay) {
		TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
		transactionRecordDto.setOrderNo("");
		transactionRecordDto.setChannel(3);
		transactionRecordDto.setComment("sdk支付途币,企业ID:" + enterpriseUserId);
		transactionRecordDto.setInfo("fen生活");//
		transactionRecordDto.setSource(24);//活动
		transactionRecordDto.setTotal(0L);
		transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNoWithRandom("SDK", String.valueOf(user.getId())));
		transactionRecordDto.setTubi(payTubi);
		transactionRecordDto.setType(1);
		transactionRecordDto.setUserId(user.getId());
		transactionRecordDto.setRmb(0L);
		//添加交易记录
		Long recordId = transactionRecordService.saveTransaction(transactionRecordDto);
		//添加交易明细
		//途币
		accountDetailService.saveAccountDetailTubi(recordId, transactionRecordDto,4);
		
		//更新账户
		this.updateAccount(account, transactionRecordDto, -1L);
		
		if(!isPay){
			userMapper.updateByPrimaryKeySelective(user);
		}
	}

	@Override
	@Transactional
	public void giveTubi(Long userId, Long tubi) {
		Account account = this.selectByUserId(userId);
		if(null == account){
			logger.error("用户ID：{},没有Account表数据",userId);
			throw new AccountException(-1, "数据出现异常,请稍后再试");
		}
		
		TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
		transactionRecordDto.setOrderNo("");
		transactionRecordDto.setChannel(4);
		transactionRecordDto.setComment("积分商城购买成功赠送途币");
		transactionRecordDto.setInfo("活动");//
		transactionRecordDto.setSource(3);//活动
		transactionRecordDto.setTotal(0L);
		transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNoWithRandom("ZS", String.valueOf(userId)));
		transactionRecordDto.setTubi(tubi);
		transactionRecordDto.setType(6);
		transactionRecordDto.setUserId(userId);
		transactionRecordDto.setRmb(0L);
		//添加交易记录
		Long recordId = transactionRecordService.saveTransaction(transactionRecordDto);
		//添加交易明细
		//途币
		accountDetailService.saveAccountDetailTubi(recordId, transactionRecordDto,4);
		
		//更新账户
		this.updateAccount(account, transactionRecordDto, 1L);
		
		try {
			Config config = configService.selectByKey("push_switch");
			if(null == config){
				return;
			}else if(config.getValue().equals("0")){
				return;
			}
			
			String description =  Configuration.getProperty("push.buy.success.give.tubi.content", null);
			String title =  Configuration.getProperty("push.buy.success.give.tubi.title", null);
			Message message = new Message();
			message.setIsPush(true);
			message.setIsSMS(false);
			message.setIsYellow(false);
			message.setTitle(title);
			message.setPushInfo(String.format(description,tubi));
			logger.info("新增消息通知：userId:{},message:{}",userId,JSON.toJSONString(message));
			pushMessageService.saveMessage(RedSpot.MESSAGE_CENTER, userId, message);
		} catch (Exception e) {
			logger.error("积分商城购买成功赠送途币,push消息推送失败",e);
		}
	}
	
	

	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.service.AccountService#pay2(com.caitu99.service.transaction.dto.TransactionRecordDto)
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "AccountException" })
	@Override
	public AccountResult pay2(TransactionRecordDto transactionRecordDto) {

		// 初始化
		AccountResult accountResult;
		accountResult = this.validate(transactionRecordDto);
		if (!accountResult.isSuccess())
			return accountResult;
		// 账户验证
		accountResult.setSuccess(false);
		Account account = accountMapper.selectByUserId(transactionRecordDto
				.getUserId());
		//锁账号
		account = accountMapper.selectByPrimaryKeyForUpdate(account.getId());
		
		if (null == account) {
			accountResult.setCode(3001);
			accountResult.setResult("账户不存在");
			return accountResult;
		}
		boolean isOrderNoNull = StringUtils.isEmpty(transactionRecordDto
				.getOrderNo()) ? true : false;
		if (isOrderNoNull) {
			accountResult.setCode(3109);
			accountResult.setResult("订单号不能为空");
			return accountResult;
		}
		// 订单验证
		Order order = orderMapper.selectByPrimaryKey(transactionRecordDto
				.getOrderNo());
		if (null == order) {
			accountResult.setCode(3003);
			accountResult.setResult("订单不存在");
			return accountResult;
		}
		// 状态：0.生成/待付款，1.已付款，10，完成交易 20.超时关闭，30.删除
		if (1 == order.getStatus()) {
			accountResult.setCode(3004);
			accountResult.setResult("该订单已付款");
			return accountResult;
		} else if (10 == order.getStatus()) {
			accountResult.setCode(3005);
			accountResult.setResult("已完成交易");
			return accountResult;
		} else if (20 == order.getStatus()) {
			accountResult.setCode(3006);
			accountResult.setResult("该订单已超时关闭");
			return accountResult;
		} else if (30 == order.getStatus()) {
			accountResult.setCode(3007);
			accountResult.setResult("该订单已删除");
			return accountResult;
		}
		transactionRecordDto.setTotal(order.getCaibi());
		transactionRecordDto.setTubi(order.getTubi());
		transactionRecordDto.setRmb(order.getRmb());
		
		try {
			if(0 == order.getStatus()){//待支付

				if (account.getAvailableIntegral().compareTo(
						isOrderNoNull ? transactionRecordDto.getTotal() : order
								.getCaibi()) == -1) {
					accountResult.setCode(3002);
					accountResult.setResult("您的财币不足");
					return accountResult;
				}if (account.getTubi().compareTo(
						isOrderNoNull ? transactionRecordDto.getTubi() : order
								.getTubi()) == -1) {
					accountResult.setCode(3002);
					accountResult.setResult("您的途币不足");
					return accountResult;
				}
				
				// 添加交易记录
				Long recordId = this.saveTransactionPaying(transactionRecordDto, order);
				// 添加交易明细
				this.saveAccountDetail(recordId, transactionRecordDto,
						TradeType.REDUCE,null);
				// 更新账户
				this.updateAccount(account, transactionRecordDto, TradeType.REDUCE);
				// 更新账单为冻结
				this.updateOrderToPaying(transactionRecordDto);
			}
		} catch (Exception e) {
			logger.error("支付失败:{}", e);
			throw new AccountException(3102, e.getMessage());
		}

		accountResult.setCode(3101);
		accountResult.setSuccess(true);
		accountResult.setResult("支付成功");
		return accountResult;
	}

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: updateOrderToPaying 
	 * @param transactionRecordDto
	 * @date 2016年6月13日 下午3:02:31  
	 * @author ws
	*/
	private void updateOrderToPaying(TransactionRecordDto transactionRecordDto) {
		Date now = new Date();
		Order order = orderMapper.selectByPrimaryKey(transactionRecordDto.getOrderNo());
		order.setPayStatus(Order.PAY_STATUS_ING);// 支付中
		order.setPayTime(now);
		order.setStatus(Order.STATUS_ING);// 处理中
		order.setUpdateTime(now);
		orderMapper.updateByPrimaryKeySelective(order);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void paySuccessDo(Long userId, String orderNo, String oidPaybill){
		
		Date now = new Date();
		
		//update TransactionRecord success
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("userId", userId);
		queryMap.put("orderNo", orderNo);
		TransactionRecord record = transactionRecordMapper.selectByUserIdAndOrderNo(queryMap );
		if(null == record || record.getStatus().equals(2)){
			logger.info("订单编号：{}，交易不存在或已关闭",orderNo);
			return;
		}
		record.setStatus(2);// 成功
		record.setSuccessTime(now);
		record.setUpdateTime(now);
		transactionRecordMapper.updateByPrimaryKeySelective(record);
		
		if(null == userId){
			userId = record.getUserId();
		}

		//保存支付卡号信息
		String bankInfo = record.getComment();
		JSONObject bankObj = JSON.parseObject(bankInfo);
		//连连   2-存储卡   3-信用卡
		Integer cardType;//0:储蓄卡;1:信用卡
		if(bankObj.getString("cardType").equals("2")){
			cardType = 0;
		}else if(bankObj.getString("cardType").equals("3")){
			cardType = 1;
		}else{
			cardType = 2;
		}
		userBankCardService.addPayBankCard(userId, bankObj.getString("idName")
				, bankObj.getString("idNo"), bankObj.getString("cardNo")
				, cardType, bankObj.getString("bankName"));
		
		
		//update Order success
		Order order = orderMapper.selectByPrimaryKey(orderNo);
		
		if(order.getStatus() == Order.STATUS_TIMEOUT){
			//需重新扣财币、途币
			TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
			transactionRecordDto.setChannel(3);
			transactionRecordDto.setComment(JSON.toJSONString(bankInfo));
			transactionRecordDto.setInfo("fen生活");
			transactionRecordDto.setOrderNo(order.getOrderNo());
			transactionRecordDto.setPicUrl("");
			transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNo(
					"SP", String.valueOf(userId)));
			transactionRecordDto.setType(1);
			transactionRecordDto.setUserId(userId);
			transactionRecordDto.setTotal(order.getCaibi());
			transactionRecordDto.setTubi(order.getTubi());
			transactionRecordDto.setRmb(order.getRmb());
			// 添加交易明细
			this.saveAccountDetail(record.getId(), transactionRecordDto,
					TradeType.REDUCE,null);
			Account account = accountMapper.selectByUserId(transactionRecordDto
					.getUserId());
			//锁账号
			account = accountMapper.selectByPrimaryKeyForUpdate(account.getId());
			// 更新账户
			this.updateAccount(account, transactionRecordDto, TradeType.REDUCE);
		}
		
		order.setPayStatus(Order.PAY_STATUS_FINISH);// 支付成功
		order.setPayTime(now);
		order.setStatus(Order.STATUS_FINISH);// 已付款
		order.setOutNo(oidPaybill);
		order.setUpdateTime(now);
		
		if(order.getType() == 60){
			order.setDisplay(1);//话费订单展示
			
			//充值话费
			mobileRechargeService.rechargeByOrder(userId, orderNo);
		}
		
		orderMapper.updateByPrimaryKeySelective(order);
		if(order.getType() == 1){//商城
			//扣库存
			this.updateReceiveStock(userId,orderNo);
		}
		
		//推送消息
		try {
			String description =  Configuration.getProperty("push.my.gift.certificate", null);
			String title =  Configuration.getProperty("push.my.gift.certificate.title", null);
			Message message = new Message();
			message.setIsPush(true);
			message.setIsSMS(false);
			message.setIsYellow(false);
			message.setTitle(title);
			message.setPushInfo(String.format(description,order.getName()));
			logger.info("新增消息通知：userId:{},message:{}",userId,JSON.toJSONString(message));
			pushMessageService.saveMessage(RedSpot.MESSAGE_CENTER, userId, message);
		} catch (Exception e) {
			logger.error("订单支付完成获得礼券推送消息失败:{}",e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void payFailureDo(Long userId, String orderNo){
		
		Date now = new Date();
		
		//update TransactionRecord fail
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("userId", userId);
		queryMap.put("orderNo", orderNo);
		TransactionRecord record = transactionRecordMapper.selectByUserIdAndOrderNo(queryMap );
		if(null == record || record.getStatus().equals(2)){
			logger.info("订单编号：{}，交易不存在或已关闭",orderNo);
			return;
		}
		
		record.setStatus(-1);// 失败
		record.setSuccessTime(now);
		record.setUpdateTime(now);
		transactionRecordMapper.updateByPrimaryKeySelective(record);
		
		if(null == userId){
			userId = record.getUserId();
		}
		
		//order timeout
		Order order = orderService.queryOrder(orderNo);
		orderService.timeOut2(userId, orderNo);
		
		
		// 冻结返还
		TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
		transactionRecordDto.setChannel(3);
		transactionRecordDto.setComment("用户消费冻结返还");
		transactionRecordDto.setInfo("fen生活");
		transactionRecordDto.setOrderNo(orderNo);
		transactionRecordDto.setPicUrl("");
		transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNo(
				"CAITU99SHOP", String.valueOf(userId)));
		transactionRecordDto.setType(1);
		transactionRecordDto.setUserId(userId);

		transactionRecordDto.setTotal(order.getCaibi());
		transactionRecordDto.setTubi(order.getTubi());
		transactionRecordDto.setRmb(order.getRmb());

		//rollBack for AccountDetail
		// 添加交易明细
		this.saveAccountDetail(record.getId(), transactionRecordDto,
				TradeType.INCREASE, null);
		
		//rollBack for Account
		Account account = accountMapper.selectByUserId(userId);
		//锁账号
		account = accountMapper.selectByPrimaryKeyForUpdate(account.getId());
		// 更新账户
		this.updateAccount(account, transactionRecordDto, TradeType.INCREASE);
	}
	
	
	
	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveTransaction2 
	 * @param transactionRecordDto
	 * @param order
	 * @return
	 * @date 2016年5月26日 上午11:43:49  
	 * @author ws
	*/
	private Long saveTransactionPaying(TransactionRecordDto transactionRecordDto,
			Order order) {
		TransactionRecord transactionRecord = new TransactionRecord();
		transactionRecord.setChannel(transactionRecordDto.getChannel());
		transactionRecord.setComment(transactionRecordDto.getComment());
		transactionRecord.setCreateTime(new Date());
		transactionRecord.setInfo(transactionRecordDto.getInfo());
		transactionRecord.setOrderNo(transactionRecordDto.getOrderNo());
		transactionRecord.setPayType(3);// 财币+途币
		transactionRecord.setPicUrl(transactionRecordDto.getPicUrl());
		transactionRecord.setStatus(1);// 处理中
		transactionRecord.setSuccessTime(new Date());
		
		transactionRecord.setTotal(transactionRecordDto.getTotal() == null? order.getCaibi():transactionRecordDto.getTotal());
		transactionRecord.setTubi(transactionRecordDto.getTubi() == null? order.getTubi():transactionRecordDto.getTubi());
		transactionRecord.setRmb(transactionRecordDto.getRmb() == null? order.getRmb():transactionRecordDto.getRmb());
		transactionRecord.setTransactionNumber(transactionRecordDto
				.getTransactionNumber());
		transactionRecord.setType(transactionRecordDto.getType());
		transactionRecord.setUpdateTime(new Date());
		transactionRecord.setUserId(transactionRecordDto.getUserId());
		transactionRecord.setSource(transactionRecordDto.getSource());
		// 添加交易记录
		transactionRecordMapper.insert(transactionRecord);
		return transactionRecord.getId();
	}

	
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "AccountException" })
	@Override
	public AccountResult pay3(TransactionRecordDto transactionRecordDto) {

		// 初始化
		AccountResult accountResult;
		accountResult = this.validate(transactionRecordDto);
		if (!accountResult.isSuccess())
			return accountResult;
		// 账户验证
		accountResult.setSuccess(false);
		Account account = accountMapper.selectByUserId(transactionRecordDto
				.getUserId());
		//锁账号
		account = accountMapper.selectByPrimaryKeyForUpdate(account.getId());
		
		if (null == account) {
			accountResult.setCode(3001);
			accountResult.setResult("账户不存在");
			return accountResult;
		}
		boolean isOrderNoNull = StringUtils.isEmpty(transactionRecordDto
				.getOrderNo()) ? true : false;
		if (isOrderNoNull) {
			accountResult.setCode(3109);
			accountResult.setResult("订单号不能为空");
			return accountResult;
		}
		// 订单验证
		Order order = orderMapper.selectByPrimaryKey(transactionRecordDto
				.getOrderNo());
		if (null == order) {
			accountResult.setCode(3003);
			accountResult.setResult("订单不存在");
			return accountResult;
		}
		// 状态：0.生成/待付款，1.已付款，10，完成交易 20.超时关闭，30.删除
		//实名认证状态: 70.待支付, 71.支付中,72.完成,73.失败,74.已退款
		if (1 == order.getStatus()) {
			accountResult.setCode(3004);
			accountResult.setResult("该订单已付款");
			return accountResult;
		} else if (2 == order.getStatus()) {
			accountResult.setCode(3005);
			accountResult.setResult("订单处理中");
			return accountResult;
		} else if (10 == order.getStatus()) {
			accountResult.setCode(3005);
			accountResult.setResult("已完成交易");
			return accountResult;
		} else if (20 == order.getStatus()) {
			accountResult.setCode(3006);
			accountResult.setResult("该订单已超时关闭");
			return accountResult;
		} else if (30 == order.getStatus()) {
			accountResult.setCode(3007);
			accountResult.setResult("该订单已删除");
			return accountResult;
		}
		transactionRecordDto.setTotal(order.getCaibi());
		transactionRecordDto.setTubi(order.getTubi());
		transactionRecordDto.setRmb(order.getRmb());
		
		if (account.getAvailableIntegral().compareTo(
				isOrderNoNull ? transactionRecordDto.getTotal() : order
						.getCaibi()) == -1) {
			accountResult.setCode(3002);
			accountResult.setResult("您的财币不足");
			return accountResult;
		}if (account.getTubi().compareTo(
				isOrderNoNull ? transactionRecordDto.getTubi() : order
						.getTubi()) == -1) {
			accountResult.setCode(3002);
			accountResult.setResult("您的途币不足");
			return accountResult;
		}
		
		try {

			// 添加交易记录
			Long recordId = this.saveTransactionPaying(transactionRecordDto, order);
			// 添加交易明细
			this.saveAccountDetail(recordId, transactionRecordDto,
					TradeType.REDUCE,null);
			//修改订单状态
			orderService.updateAuthenticationOrder(transactionRecordDto.getOrderNo(), 71,null);
		} catch (Exception e) {
			logger.error("支付失败:{}", e);
			throw new AccountException(3102, e.getMessage());
		}

		accountResult.setCode(3101);
		accountResult.setSuccess(true);
		accountResult.setResult("支付成功");
		return accountResult;
	}
	
	@Override
	@Transactional
	public void authenticationSuccessDo(Long userId, String orderNo,String oidPaybill){
		Date now = new Date();
		
		User user = userMapper.selectByPrimaryKey(userId);
		if(null == user){
			throw new UserNotFoundException(-1, "用户不存在");
		}

		Order order = orderService.queryOrder(orderNo);
		if(null == order){
			logger.error("订单不存在:{}",orderNo);
			throw new OrderException(-1, "订单不存在");
		}
		
		//update TransactionRecord success
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("userId", userId);
		queryMap.put("orderNo", orderNo);
		queryMap.put("status", "1");//支付中
		TransactionRecord record = transactionRecordMapper.selectByUserIdAndOrderNo(queryMap );
		if(null == record){
			logger.info("订单编号：{}，交易已关闭",orderNo);
			return;
		}
		
		record.setStatus(2);// 成功
		record.setSuccessTime(now);
		record.setUpdateTime(now);
		transactionRecordMapper.updateByPrimaryKeySelective(record);
		
		//修改订单状态
		orderService.updateAuthenticationOrder(orderNo, 72,oidPaybill);

		//保存支付卡号信息
		String bankInfo = record.getComment();
		JSONObject bankObj = JSON.parseObject(bankInfo);
		//连连   2-存储卡   3-信用卡
		Integer cardType;//0:储蓄卡;1:信用卡
		if(bankObj.getString("cardType").equals("2")){
			cardType = 0;
		}else if(bankObj.getString("cardType").equals("3")){
			cardType = 1;
		}else{
			cardType = 2;
		}
		userBankCardService.addPayBankCard(userId, bankObj.getString("idName")
				, bankObj.getString("idNo"), bankObj.getString("cardNo")
				, cardType, bankObj.getString("bankName"));
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "AccountException" })
	@Override
	public AccountResult pay4(TransactionRecordDto transactionRecordDto) {

		// 初始化
		AccountResult accountResult;
		accountResult = this.validate(transactionRecordDto);
		if (!accountResult.isSuccess())
			return accountResult;
		// 账户验证
		accountResult.setSuccess(false);
		Account account = accountMapper.selectByUserId(transactionRecordDto
				.getUserId());
		//锁账号
		account = accountMapper.selectByPrimaryKeyForUpdate(account.getId());
		
		if (null == account) {
			accountResult.setCode(3001);
			accountResult.setResult("账户不存在");
			return accountResult;
		}
		boolean isOrderNoNull = StringUtils.isEmpty(transactionRecordDto
				.getOrderNo()) ? true : false;
		if (isOrderNoNull) {
			accountResult.setCode(3109);
			accountResult.setResult("订单号不能为空");
			return accountResult;
		}
		// 订单验证
		Order order = orderMapper.selectByPrimaryKey(transactionRecordDto
				.getOrderNo());
		if (null == order) {
			accountResult.setCode(3003);
			accountResult.setResult("订单不存在");
			return accountResult;
		}
		// 状态：0.生成/待付款，1.已付款，10，完成交易 20.超时关闭，30.删除
		//实名认证状态: 70.待支付, 71.支付中,72.完成,73.失败,74.已退款
		if (1 == order.getStatus()) {
			accountResult.setCode(3004);
			accountResult.setResult("该订单已付款");
			return accountResult;
		} else if (2 == order.getStatus()) {
			accountResult.setCode(3005);
			accountResult.setResult("订单处理中");
			return accountResult;
		} else if (10 == order.getStatus()) {
			accountResult.setCode(3005);
			accountResult.setResult("已完成交易");
			return accountResult;
		} else if (20 == order.getStatus()) {
			accountResult.setCode(3006);
			accountResult.setResult("该订单已超时关闭");
			return accountResult;
		} else if (30 == order.getStatus()) {
			accountResult.setCode(3007);
			accountResult.setResult("该订单已删除");
			return accountResult;
		}
		transactionRecordDto.setTotal(order.getCaibi());
		transactionRecordDto.setTubi(order.getTubi());
		transactionRecordDto.setRmb(order.getRmb());
		
		if (account.getAvailableIntegral().compareTo(
				isOrderNoNull ? transactionRecordDto.getTotal() : order
						.getCaibi()) == -1) {
			accountResult.setCode(3002);
			accountResult.setResult("您的财币不足");
			return accountResult;
		}if (account.getTubi().compareTo(
				isOrderNoNull ? transactionRecordDto.getTubi() : order
						.getTubi()) == -1) {
			accountResult.setCode(3002);
			accountResult.setResult("您的途币不足");
			return accountResult;
		}
		
		try {

			// 添加交易记录
			Long recordId = this.saveTransactionPaying(transactionRecordDto, order);
			// 添加交易明细
			this.saveAccountDetail(recordId, transactionRecordDto,
					TradeType.REDUCE,null);
			//修改订单状态
			orderService.updateAuthenticationOrder(transactionRecordDto.getOrderNo(), 74,null);
		} catch (Exception e) {
			logger.error("支付失败:{}", e);
			throw new AccountException(3102, e.getMessage());
		}

		accountResult.setCode(3101);
		accountResult.setSuccess(true);
		accountResult.setResult("支付成功");
		return accountResult;
	}
	
	@Override
	@Transactional
	public void authenticationRefundDo(Long userId, String orderNo){
		Date now = new Date();

		Order order = orderService.queryOrder(orderNo);
		if(null == order){
			logger.error("订单不存在:{}",orderNo);
			throw new OrderException(-1, "订单不存在");
		}
		
		//update TransactionRecord success
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("userId", userId);
		queryMap.put("orderNo", orderNo);
		queryMap.put("status", "2");//支付中
		TransactionRecord record = transactionRecordMapper.selectByUserIdAndOrderNo(queryMap );
		if(null == record){
			logger.info("订单编号：{}，交易已关闭",orderNo);
			return;
		}
		
		record.setInfo("已退款");
		record.setSuccessTime(now);
		record.setUpdateTime(now);
		transactionRecordMapper.updateByPrimaryKeySelective(record);
		
		//修改订单状态
		orderService.updateAuthenticationOrder(orderNo, 72,null);
	}
}
