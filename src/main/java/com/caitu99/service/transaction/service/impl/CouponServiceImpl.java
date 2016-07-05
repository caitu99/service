/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.exception.CouponException;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.transaction.dao.AccountDetailMapper;
import com.caitu99.service.transaction.dao.AccountMapper;
import com.caitu99.service.transaction.dao.CouponMapper;
import com.caitu99.service.transaction.dao.TransactionRecordMapper;
import com.caitu99.service.transaction.dao.UserCouponMapper;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.Coupon;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.domain.UserCoupon;
import com.caitu99.service.transaction.service.CouponService;
import com.caitu99.service.user.dao.UserMapper;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserInvitationService;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.date.DateUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: CouponServiceImpl 
 * @author Hongbo Peng
 * @date 2015年12月7日 下午12:11:10 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class CouponServiceImpl implements CouponService {
	
	private final static Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

	@Autowired
	private CouponMapper couponMapper;
	
	@Autowired
	private UserCouponMapper userCouponMapper;
	
	@Autowired
	private AccountMapper accountMapper;
	
	@Autowired
	private AccountDetailMapper accountDetailMapper;
	
	@Autowired
	private TransactionRecordMapper transactionRecordMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private KafkaProducer kafkaProducer;
	
	@Autowired
	private UserInvitationService userInvitationService;
	
	//TODO code
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "CouponException" })
	@Override
	public String receiveCoupon(Long userId, String code) {
		ApiResult<Long> apiResult = new ApiResult<Long>();
		try {
			Date date = new Date();
			if(null == userId){
				return apiResult.toJSONString(3301, "userId can not empty");
			}
			if(StringUtils.isBlank(code)){
				return apiResult.toJSONString(3301, "code can not empty");
			}
			logger.info("用户：{}来兑换券：{}",userId,code);
			
			
			//新增兑换邀请码,6位为邀请码
			if(code.length() == 6){
				return userInvitationService.receiveInvitation(userId, code);
			}
			
			//1.验证用户有没有领过券
			UserCoupon queryUserCoupon = new UserCoupon();
			queryUserCoupon.setUserId(userId);
			List<UserCoupon> isCanReceiveCoupon = userCouponMapper.selectByUserId(queryUserCoupon);
			if(null != isCanReceiveCoupon && 0 != isCanReceiveCoupon.size()){
				logger.info("该用户：{}领取过兑换券",userId);
				return apiResult.toJSONString(3302, "您已经领取过，不能再领取");
			}
			
			//2.验证用户注册时间
			User user = userMapper.selectByPrimaryKey(userId);
			if(null == user){
				logger.error("用户{}不存在",userId);
				return apiResult.toJSONString(3301, "user is not exists");
			}
			
			int receiveDay =  Integer.parseInt(Configuration.getProperty("coupon.receive.time", "5"));
			if(date.getTime() - user.getGmtCreate().getTime() > receiveDay*24*60*60*1000){
				logger.info("该用户：{}不是新用户",userId);
				return apiResult.toJSONString(3302, "该兑换码仅对新用户有效");
			}
			
			Account account = accountMapper.selectByUserId(userId);
			if(null == account){
				logger.error("该用户：{}账户为空",userId);
				return apiResult.toJSONString(3301, "user account is not exists");
			}
			
			//3.验证CODE是否有效
			Coupon coupon = couponMapper.selectByCode(code);
			if(null == coupon || coupon.getStatus().intValue() != 1){
				logger.info("兑换码{}错误",code);
				return apiResult.toJSONString(3302, "兑换码无效，请重新输入");
			}
			
			//4.修改券状态
			int overdueDay = Integer.parseInt(Configuration.getProperty("coupon.overdue.time", "30"));
			Coupon editCoupon = new Coupon();
			editCoupon.setId(coupon.getId());
			editCoupon.setUserId(userId);
			editCoupon.setStatus(2);
			editCoupon.setReceiveTime(date);
			editCoupon.setOverdueTime(DateUtil.addDay(date, overdueDay));
			editCoupon.setUpdateTime(date);
			couponMapper.updateByPrimaryKeySelective(editCoupon);
			
			//5.添加用户领取记录
			UserCoupon userCoupon = new UserCoupon();
			userCoupon.setCouponId(coupon.getId());
			userCoupon.setCode(coupon.getCode());
			userCoupon.setUserId(userId);
			userCoupon.setSendIntegral(coupon.getIntegral());
			userCoupon.setUseIntegral(0L);
			userCoupon.setAvailableIntegral(coupon.getIntegral());
			userCoupon.setStatus(1);
			userCoupon.setReceiveTime(date);
			userCoupon.setOverdueTime(DateUtil.addDay(date, overdueDay));
			userCoupon.setCreateTime(date);
			userCoupon.setUpdateTime(date);
			userCouponMapper.insertSelective(userCoupon);
			
			//6.添加券充值交易记录
			Long recordId = saveTransactionRecord(userCoupon,3,"充值券",date);
			
			//7.添加入分记录
			AccountDetail accountDetail = new AccountDetail();
			accountDetail.setUserId(userId);
			accountDetail.setRecordId(recordId);
			accountDetail.setIntegralChange(coupon.getIntegral());
			accountDetail.setType(1);
			accountDetail.setMemo("充值券");
			accountDetail.setGmtCreate(date);
			accountDetail.setGmtModify(date);
			accountDetailMapper.insertSelective(accountDetail);
			
			//8.给财币账户充值
			account = accountMapper.selectByUserId(userId);
			Account editAccount = new Account();
			editAccount.setId(account.getId());
			editAccount.setTotalIntegral(account.getTotalIntegral()+coupon.getIntegral());
			editAccount.setAvailableIntegral(account.getAvailableIntegral()+coupon.getIntegral());
			editAccount.setGmtModify(date);
			accountMapper.updateByPrimaryKeySelective(editAccount);
			//9.启动过期定时任务
			Map<String, Object> map = new HashMap<>();
			map.put("id", userCoupon.getId());
			map.put("jobType","USER_COUPON_OVERDUE"); 
			System.out.println(JSON.toJSONString(map));
			kafkaProducer.sendMessage(JSON.toJSONString(map),appConfig.jobTopic);
			return apiResult.toJSONString(0, "恭喜您获得"+coupon.getIntegral()+"财币！",coupon.getIntegral());
		} catch (Exception e) {
			logger.error("领取财币失败 {}",e);
			throw new CouponException(3301, e.getMessage());
		}
	}

	//TODO errCode
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "CouponException" })
	@Override
	public String overdueCoupon(Long id) {
		ApiResult<String> apiResult = new ApiResult<String>();
		try {
			Date date = new Date();
			//1.查询用户领券记录
			UserCoupon userCoupon = userCouponMapper.selectByPrimaryKey(id);
			if(1 != userCoupon.getStatus().intValue()){
				logger.info("用户领券记录id:{}已经过期或用完，无需执行");
				return apiResult.toJSONString(0, "当前记录已经过期或用完，无需执行");
			}
			//2.修改用户领券记录
			UserCoupon editUserCoupon= new UserCoupon();
			editUserCoupon.setId(id);
			editUserCoupon.setStatus(-1);
			editUserCoupon.setOverdueTime(date);
			editUserCoupon.setUpdateTime(date);
			userCouponMapper.updateByPrimaryKeySelective(editUserCoupon);
			
			if(0 == userCoupon.getAvailableIntegral().longValue()){
				logger.info("用户领券记录id:{}积分已经用完，无需生成交易记录");
				return apiResult.toJSONString(0, "当前记录积分已经用完，无需生成交易记录");
			}
			
			//3.生成用户消费记录（积分过期）
			Long recordId = saveTransactionRecord(userCoupon,7,"券过期",date);
			
			//4.生成出分记录
			AccountDetail accountDetail = new AccountDetail();
			accountDetail.setUserId(userCoupon.getUserId());
			accountDetail.setRecordId(recordId);
			accountDetail.setIntegralChange(userCoupon.getAvailableIntegral());
			accountDetail.setType(2);
			accountDetail.setMemo("券过期");
			accountDetail.setGmtCreate(date);
			accountDetail.setGmtModify(date);
			accountDetailMapper.insertSelective(accountDetail);
			
			//5.扣减用户过期积分
			Account account = accountMapper.selectByUserId(userCoupon.getUserId());
			Account editAccount = new Account();
			editAccount.setId(account.getId());
			editAccount.setTotalIntegral(account.getTotalIntegral()-userCoupon.getAvailableIntegral());
			editAccount.setAvailableIntegral(account.getAvailableIntegral()-userCoupon.getAvailableIntegral());
			editAccount.setGmtModify(date);
			accountMapper.updateByPrimaryKeySelective(editAccount);
			return apiResult.toJSONString(0, "券积分过期执行成功");
		} catch (Exception e) {
			logger.error("券积分过期操作失败 {}",e);
			throw new CouponException(3301, e.getMessage());
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "CouponException" })
	private Long saveTransactionRecord(UserCoupon userCoupon,Integer type,String memo,Date date){
		try {
			String head = type == 3 ? "DH" : "GQ";
			TransactionRecord transactionRecord = new TransactionRecord();
			transactionRecord.setTransactionNumber(XStringUtil.createSerialNo(head, String.valueOf(userCoupon.getUserId())));
			transactionRecord.setOrderNo(String.valueOf(userCoupon.getId()));
			transactionRecord.setUserId(userCoupon.getUserId());
			transactionRecord.setInfo(memo);
			transactionRecord.setType(type);
			transactionRecord.setPayType(1);
			transactionRecord.setStatus(2);
			transactionRecord.setTotal(userCoupon.getSendIntegral());
			transactionRecord.setChannel(5);
			transactionRecord.setCreateTime(date);
			transactionRecord.setUpdateTime(date);
			transactionRecordMapper.insertSelective(transactionRecord);
			return transactionRecord.getId();
		} catch (Exception e) {
			logger.error("save transaction record failed,{}",e);
			throw new CouponException(3301, e.getMessage());
		}
	}
}
