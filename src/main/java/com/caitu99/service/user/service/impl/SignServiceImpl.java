package com.caitu99.service.user.service.impl;

import com.caitu99.service.expedient.domain.ExpData;
import com.caitu99.service.expedient.provider.AddExp;
import com.caitu99.service.expedient.provider.rule.AddExpBySign;
import com.caitu99.service.transaction.dao.AccountDetailMapper;
import com.caitu99.service.transaction.dao.AccountMapper;
import com.caitu99.service.transaction.dao.TransactionRecordMapper;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.user.dao.SignMapper;
import com.caitu99.service.user.domain.Sign;
import com.caitu99.service.user.service.SignService;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.caitu99.service.utils.XStringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SignServiceImpl implements SignService {

	@Autowired
	private SignMapper signMapper;
	@Autowired
	private AccountDetailMapper accountDetailMapper;
	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private TransactionRecordMapper transactionRecordMapper;
	
	@Autowired
	private AddExp addExp;
	
	@Autowired
	private AddExpBySign addExpBySign;
	
	@Override
	public Sign getSign(long userId){
		Sign sign = signMapper.selectByUserId(userId);
		
		if( sign == null ){  //从未签到过的情况
			sign = new Sign();
			sign.setContinuous_time(0L);
			sign.setSignGiftIntegral(1L);
			sign.setSignToday(0);
		}else{
			if(signMapper.selectCount(userId)>0){ //今天签到过
				Long integralChange = getGiftIntegralbyContinuousTime(sign.getContinuous_time());
				sign.setSignGiftIntegral(integralChange);
				sign.setSignToday(1);
			}else{  //今天未签到
				DateFormat df = DateFormat.getDateInstance(); //日期格式，精确到日
				Date date = new Date();
				Calendar cal_sign = Calendar.getInstance();
				
				cal_sign.setTime(sign.getSign_date());
				cal_sign.add(Calendar.DATE, 1);    //加上一天
				if( !df.format(cal_sign.getTime()).equals(df.format(date)) ){ //昨天没有签，不是连续的情况
					sign.setContinuous_time(0L);
				}
				Long integralChange = getGiftIntegralbyContinuousTime(sign.getContinuous_time() + 1);
				sign.setSignGiftIntegral(integralChange);
				sign.setSignGiftIntegral(1L);
				sign.setSignToday(0);
			}
		}
			
		return sign;
	}

	@Override
	@Transactional(rollbackFor = { RuntimeException.class})
	public Sign signEvery(long userId) {

		if(signMapper.selectCount(userId)>0) {
			return null; //今天已经签到过
		}
		
		Date date = new Date();
		Calendar cal_sign = Calendar.getInstance();
		DateFormat df = DateFormat.getDateInstance(); //日期格式，精确到日

		Sign sign = signMapper.selectByUserId(userId);

		if( sign == null ){   //该用户从未签到过
			sign = new Sign();
			sign.setContinuous_time(1L);
			sign.setSign_date(date);
			sign.setUserId(userId);
			signMapper.insert(sign);
		}else{
			cal_sign.setTime(sign.getSign_date());
			cal_sign.add(Calendar.DATE, 1);    //加上一天
			if( df.format(cal_sign.getTime()).equals( df.format(date) ) ){ 
				sign.setContinuous_time(sign.getContinuous_time() + 1);
			}else{
				sign.setContinuous_time(1L);
			}
			sign.setSign_date(date);
			signMapper.updateRecord(sign);
		}
		
		return sign;
	}
	
	@Override
	@Transactional(rollbackFor = { RuntimeException.class})
	public Sign signGiftIntegral(Sign sign) {
		
		Long integralChange = getGiftIntegralbyContinuousTime(sign.getContinuous_time());
		Date date = new Date();
		//1.记录财币详细记录
		AccountDetail accountDetail = new AccountDetail();
		accountDetail.setUserId(sign.getUserId());
		accountDetail.setRecordId(-1L);
		accountDetail.setIntegralChange(integralChange);
		accountDetail.setType(1); //财币入分
		accountDetail.setMemo("签到送途币");
		accountDetail.setGmtCreate(date);
		accountDetail.setGmtModify(date);
		accountDetailMapper.insertSelective(accountDetail);
		
		//2.给财币账户充值
		Account accountInDB = accountMapper.selectByUserId(sign.getUserId());
		Account account = new Account();
		if( accountInDB == null){
			account.setUserId(sign.getUserId());
			account.setTotalIntegral(integralChange);
			account.setAvailableIntegral(integralChange);
			account.setFreezeIntegral(0L);
			account.setTubi(0L);
			account.setGmtModify(date);
			account.setGmtCreate(date);
			accountMapper.insert(account);
		}else{
			account.setId(accountInDB.getId());
			account.setUserId(sign.getUserId());
			account.setTotalIntegral(accountInDB.getTotalIntegral()+integralChange);
			account.setAvailableIntegral(accountInDB.getAvailableIntegral()+integralChange);
//			account.setTubi(accountInDB.getTubi()+integralChange);
			account.setGmtModify(date);
			accountMapper.updateByPrimaryKeySelective(account);
		}

		//3.交易记录
		TransactionRecord transactionRecord = new TransactionRecord();
		transactionRecord.setChannel(4);   //4.赠送
		transactionRecord.setComment("签到送"+integralChange+"财币");
		transactionRecord.setCreateTime(new Date());
		transactionRecord.setInfo("活动");
		transactionRecord.setOrderNo("");
		transactionRecord.setPayType(1);// 1财币
		transactionRecord.setPicUrl("");
		transactionRecord.setStatus(2);// 成功
		transactionRecord.setSuccessTime(date);
		transactionRecord.setTotal(integralChange);
		transactionRecord.setRmb(0L);
		transactionRecord.setTubi(0L);
		transactionRecord.setTransactionNumber(XStringUtil
				.createSerialNo("ZS", String.valueOf(sign.getUserId())));
		transactionRecord.setType(6);
		transactionRecord.setUpdateTime(date);
		transactionRecord.setUserId(sign.getUserId());
		transactionRecord.setSource(3);
		transactionRecord.setCouponIntegral(0L);
		int a = transactionRecordMapper.insert(transactionRecord);

		sign.setSignGiftIntegral(integralChange);
		return sign;
	}
	
	@Override
	@Transactional(rollbackFor = { RuntimeException.class})
	public Sign signGiftTubi(Sign sign) {
		
		Long integralChange = getGiftIntegralbyContinuousTime(sign.getContinuous_time());
		
		//每日签到获得1经验，连续签到5天获得5经验。
		ExpData expData = new ExpData();
		expData.setUserId(sign.getUserId());
		expData.setContinuousTime(sign.getContinuous_time());
		addExp.addExp(expData, addExpBySign);
		
		Date date = new Date();
		//1.记录财币详细记录
		AccountDetail accountDetail = new AccountDetail();
		accountDetail.setUserId(sign.getUserId());
		accountDetail.setRecordId(-1L);
		accountDetail.setIntegralChange(integralChange);
		accountDetail.setType(3); //途币入分
		accountDetail.setMemo("签到送途币");
		accountDetail.setGmtCreate(date);
		accountDetail.setGmtModify(date);
		accountDetailMapper.insertSelective(accountDetail);
		
		//2.给财币账户充值
		Account accountInDB = accountMapper.selectByUserId(sign.getUserId());
		Account account = new Account();
		if( accountInDB == null){
			account.setUserId(sign.getUserId());
			account.setTotalIntegral(0l);
			account.setAvailableIntegral(0l);
			account.setFreezeIntegral(0L);
			account.setTubi(integralChange);
			account.setGmtModify(date);
			account.setGmtCreate(date);
			accountMapper.insert(account);
		}else{
			account.setId(accountInDB.getId());
			account.setUserId(sign.getUserId());
//			account.setTotalIntegral(accountInDB.getTotalIntegral()+integralChange);
//			account.setAvailableIntegral(accountInDB.getAvailableIntegral()+integralChange);
			account.setTubi(accountInDB.getTubi()+integralChange);
			account.setGmtModify(date);
			accountMapper.updateByPrimaryKeySelective(account);
		}

		//3.交易记录
		TransactionRecord transactionRecord = new TransactionRecord();
		transactionRecord.setChannel(4);   //4.赠送
		transactionRecord.setComment("签到送"+integralChange+"途币");
		transactionRecord.setCreateTime(new Date());
		transactionRecord.setInfo("活动");
		transactionRecord.setOrderNo("");
		transactionRecord.setPayType(1);// 1财币
		transactionRecord.setPicUrl("");
		transactionRecord.setStatus(2);// 成功
		transactionRecord.setSuccessTime(date);
		transactionRecord.setTotal(0L);
		transactionRecord.setRmb(0L);
		transactionRecord.setTubi(integralChange);
		transactionRecord.setTransactionNumber(XStringUtil
				.createSerialNo("ZS", String.valueOf(sign.getUserId())));
		transactionRecord.setType(6);
		transactionRecord.setUpdateTime(date);
		transactionRecord.setUserId(sign.getUserId());
		transactionRecord.setSource(3);
		transactionRecord.setCouponIntegral(0L);
		int a = transactionRecordMapper.insert(transactionRecord);

		sign.setSignGiftIntegral(integralChange);
		return sign;
	}
	
	private Long getGiftIntegralbyContinuousTime(Long continuousTime){
		Long integralChange = 0l;
		if( continuousTime >= 0 && continuousTime <= 7){
			integralChange = 1L;
		}else if( continuousTime >= 8 && continuousTime <= 14){
			integralChange = 10L;
		}else if( continuousTime >= 15 && continuousTime <= 21){
			integralChange = 20L;
		}else if( continuousTime >= 22 ){
			integralChange = 30L;
		}
		return integralChange;
	}
}


