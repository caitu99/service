/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.transaction.constants.AccountDetailConstants;
import com.caitu99.service.transaction.constants.TransactionRecordConstants;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.IntegralTransferService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;

/** 
 * 财币划转
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IntegralTransferImpl 
 * @author ws
 * @date 2015年12月1日 下午4:40:52 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class IntegralTransferServiceImpl implements IntegralTransferService {
	
	private final static Logger logger = LoggerFactory
			.getLogger(IntegralTransferServiceImpl.class);
	
	@Autowired
	TransactionRecordService transactionService;
	@Autowired
	AccountDetailService accountDetailService;
	@Autowired
	AccountService accountService;
	@Autowired
	UserService userService;
	@Autowired
	AppConfig appConfig;
	@Autowired
	private RealizeService realizeService;
	@Autowired
	private PushMessageService pushMessageService;
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.company.transfer.IntegralTransfer#transferIntegral(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void transferIntegral(Long userId, String orderNo) {

		TransactionRecord userTranRecord = transactionService.getTransactionRecord(userId,orderNo);
		if(TransactionRecordConstants.STATUS_FREEZE == userTranRecord.getStatus()){
			
			transferSingleRecord(userTranRecord);
		}else{
			logger.info("无需划转，用户Id:{}",userId);
			return;
		}
		
	}
	
	@Override
	public void transferSingleRecord(TransactionRecord userTranRecord) {
		Date nowDate = new Date();
		//依据用户Id 查询企业 交易表记录
		TransactionRecord companyTranRecord = transactionService.selectByOrderNoExludeUserId(
					userTranRecord.getUserId(),userTranRecord.getOrderNo());

		if(null == companyTranRecord){
			return;
		}
		
		Account companyRecord = new Account();
		companyRecord = accountService.selectByUserId(companyTranRecord.getUserId());
		
		//企业财币阈值
		Long threshold = appConfig.companyIntegralThreshold;
		//如果企业可用财币达到某个预警值，短信通知充值财币
		if(companyRecord.getAvailableIntegral().compareTo(threshold) < 0 ){
			User companyUser = userService.selectCompanyUser(companyRecord.getUserId());
			logger.info("企业可用财币预警，企业编号为{},企业名为{}的可用财币不足{}，请尽快补充财币",companyRecord.getUserId(),companyUser.getNick(),threshold);
		}
		
		if(companyRecord.getAvailableIntegral().compareTo(userTranRecord.getTotal()) < 0){
			//企业财币不足
			logger.warn("企业财币不足。企业号：{}",companyRecord.getUserId());
			//更新用户 交易记录为失败
			userTranRecord.setStatus(TransactionRecordConstants.STATUS_FAIL);
			userTranRecord.setUpdateTime(nowDate);
			transactionService.updateByPrimaryKey(userTranRecord);
			
			//更新企业 交易记录为失败
			companyTranRecord.setStatus(TransactionRecordConstants.STATUS_FAIL);
			companyTranRecord.setUpdateTime(nowDate);
			transactionService.updateByPrimaryKey(companyTranRecord);
			return;
		}
		
		
		
		//1、更新用户 交易表状态为正常；2、插入财币明细表；3、更新总财币
		userTranRecord.setStatus(TransactionRecordConstants.STATUS_SUCCESS);
		transactionService.updateByPrimaryKey(userTranRecord);
		
		AccountDetail userAccountDetail = new AccountDetail();
		userAccountDetail.setRecordId(userTranRecord.getId());
		userAccountDetail.setGmtCreate(nowDate);
		userAccountDetail.setGmtModify(nowDate);
		userAccountDetail.setIntegralChange(userTranRecord.getTotal());
		userAccountDetail.setMemo(""+userTranRecord.getComment());
		userAccountDetail.setType(AccountDetailConstants.TYPE_IN);
		userAccountDetail.setUserId(userTranRecord.getUserId());
		accountDetailService.insertAccountDetail(userAccountDetail );
		
		//更新用户总财币
		Account userRecord = new Account();
		userRecord = accountService.selectByUserId(userTranRecord.getUserId());
		userRecord.setTotalIntegral(userRecord.getTotalIntegral()+userTranRecord.getTotal());
		userRecord.setAvailableIntegral(userRecord.getAvailableIntegral()+userTranRecord.getTotal());
		userRecord.setGmtModify(nowDate);
		accountService.updateAccountByUserId(userRecord );
		
		
		//1、更新企业 交易表状态为正常；2、插入财币明细表；3、更新总财币
		companyTranRecord.setStatus(TransactionRecordConstants.STATUS_SUCCESS);
		transactionService.updateByPrimaryKey(companyTranRecord);
		
		AccountDetail companyAccountDetail = new AccountDetail();
		companyAccountDetail.setRecordId(companyTranRecord.getId());
		companyAccountDetail.setGmtCreate(nowDate);
		companyAccountDetail.setGmtModify(nowDate);
		companyAccountDetail.setIntegralChange(-userTranRecord.getTotal());//出分  负数
		companyAccountDetail.setMemo(""+companyTranRecord.getComment());
		companyAccountDetail.setType(AccountDetailConstants.TYPE_OUT);
		companyAccountDetail.setUserId(companyTranRecord.getUserId());
		accountDetailService.insertAccountDetail(companyAccountDetail );
		
		
		companyRecord.setTotalIntegral(companyRecord.getTotalIntegral() - userTranRecord.getTotal());
		//companyRecord.setFreezeIntegral(companyRecord.getFreezeIntegral() - userTranRecord.getTotal());
		companyRecord.setAvailableIntegral(companyRecord.getAvailableIntegral() - userTranRecord.getTotal());
		companyRecord.setGmtModify(nowDate);
		accountService.updateAccountByUserId(companyRecord );
		
		logger.info("企业赠送财分激活到账，转入用户ID：{}，企业用户ID：{}",userTranRecord.getUserId(),companyTranRecord.getUserId());
	}

	@Override
    @Transactional(propagation = Propagation.REQUIRED)
	public void transferToOtherUser(Long fUserId, Long tUserId, Long total, String stall,String comment) {

        Date nowDate = new Date();
        logger.info("transfer {} from {} to {}", total, fUserId, tUserId);

        Account fAccount = accountService.selectByUserId(fUserId);
        if (fAccount.getAvailableIntegral().compareTo(total) < 0) {
            logger.info("transfer failure, insufficient integral: {} from {} to {}", total, fUserId, tUserId);
            throw new ApiException(3404, "您的财币不足");
        }

        String orderNo = UUID.randomUUID().toString().replace("-", "");

        // from
        TransactionRecord fTransactionRecord = new TransactionRecord();
        fTransactionRecord.setTransactionNumber(UUID.randomUUID().toString().replace("-", ""));
        fTransactionRecord.setOrderNo(orderNo);
        fTransactionRecord.setUserId(fUserId);
        fTransactionRecord.setInfo("转出");
        fTransactionRecord.setType(9);
        fTransactionRecord.setPayType(1);
        fTransactionRecord.setSource(3);
        fTransactionRecord.setStatus(2);
        fTransactionRecord.setTotal(total);
        fTransactionRecord.setCouponIntegral(0L);
        fTransactionRecord.setComment(StringUtils.isBlank(comment) ? "转出" : comment);
        fTransactionRecord.setSuccessTime(nowDate);
        fTransactionRecord.setChannel(4);
        fTransactionRecord.setCreateTime(nowDate);
        fTransactionRecord.setUpdateTime(nowDate);
        transactionService.insert(fTransactionRecord);

        AccountDetail fAccountDetail = new AccountDetail();
        fAccountDetail.setUserId(fUserId);
        fAccountDetail.setRecordId(fTransactionRecord.getId());
        fAccountDetail.setIntegralChange(total);
        fAccountDetail.setType(AccountDetailConstants.TYPE_OUT);
        fAccountDetail.setMemo("转账到:" + tUserId);
        fAccountDetail.setGmtCreate(nowDate);
        fAccountDetail.setGmtModify(nowDate);
        accountDetailService.insertAccountDetail(fAccountDetail);

        // to
        TransactionRecord tTransactionRecord = new TransactionRecord();
        tTransactionRecord.setTransactionNumber(UUID.randomUUID().toString().replace("-", ""));
        tTransactionRecord.setOrderNo(orderNo);
        tTransactionRecord.setUserId(tUserId);
        tTransactionRecord.setInfo("转入");
        tTransactionRecord.setType(8);
        tTransactionRecord.setPayType(1);
        tTransactionRecord.setSource(3);
        tTransactionRecord.setStatus(2);
        tTransactionRecord.setTotal(total);
        tTransactionRecord.setCouponIntegral(0L);
        tTransactionRecord.setComment("转入");
        tTransactionRecord.setSuccessTime(nowDate);
        tTransactionRecord.setChannel(4);
        tTransactionRecord.setCreateTime(nowDate);
        tTransactionRecord.setUpdateTime(nowDate);
        transactionService.insert(tTransactionRecord);

        AccountDetail tAccountDetail = new AccountDetail();
        tAccountDetail.setUserId(tUserId);
        tAccountDetail.setRecordId(tTransactionRecord.getId());
        tAccountDetail.setIntegralChange(total);
        tAccountDetail.setType(AccountDetailConstants.TYPE_IN);
		tAccountDetail.setStall(stall);
        tAccountDetail.setMemo("转账从:" + fUserId);
        tAccountDetail.setGmtCreate(nowDate);
        tAccountDetail.setGmtModify(nowDate);
        accountDetailService.insertAccountDetail(tAccountDetail);

        // update fAccount
        fAccount.setTotalIntegral(fAccount.getTotalIntegral() - total);
        fAccount.setAvailableIntegral(fAccount.getAvailableIntegral() - total);
        fAccount.setGmtModify(nowDate);
        accountService.updateAccountByUserId(fAccount);

        // update tAccount
        Account tAccount = accountService.selectByUserId(tUserId);
        tAccount.setTotalIntegral(tAccount.getTotalIntegral() + total);
        tAccount.setAvailableIntegral(tAccount.getAvailableIntegral() + total);
        tAccount.setGmtModify(nowDate);
        accountService.updateAccountByUserId(tAccount);

        logger.info("transfer {} from {} to {} success", total, fUserId, tUserId);
	}

	@Override
	@Transactional
	public void transferFreezeIntegral(Long fUserId, Long tUserId, Long total,String stall) {
		try {
			List<RealizeRecord> list = realizeService.selectRealizeDetailFirst(fUserId);
			if(null==list || list.size()<1){
				logger.info("用户{}：没有冻结财币可转让.转让非冻结财币",fUserId);
				this.transferToOtherUser(fUserId, tUserId, total, stall,null);
			}else{
				//记录用户积分变现冻结财分数量
				Long cash = 0L;
				List<RealizeRecord> realizeRecordList = new ArrayList<RealizeRecord>();
				for(RealizeRecord realizeRecord : list){
					cash += realizeRecord.getCash();
					realizeRecordList.add(realizeRecord);
					if(cash >= total){
						break;
					}
				}
				
				//冻结财分到账
				for(RealizeRecord realizeRecord : realizeRecordList){
					Long realizeRecordId = realizeRecord.getId();
					realizeService.realizeTransfer(realizeRecordId);
				}
				
				if(cash < total){
					logger.info("用户{},冻结财币不足转让,转让非冻结财币",fUserId);
				}else{
					logger.info("用户{},冻结财币足够转让,不转让非冻结财币",fUserId);
				}
				
				ApiResult<List<RealizeRecord>> comment = new ApiResult<List<RealizeRecord>>();
				SimplePropertyPreFilter filter = new SimplePropertyPreFilter(RealizeRecord.class,new String[]{"id"});
				
				this.transferToOtherUser(fUserId, tUserId, total, stall,comment.toJSONString(0,"积分变现冻结转出",realizeRecordList,filter));
			}
			
			Message message = new Message();
			message.setIsPush(true);
			message.setIsSMS(false);
			message.setIsYellow(false);
			message.setTitle("财途积分转账");
			message.setPushInfo("您有" + total + "财币到账");
			pushMessageService.pushMessage(RedSpot.MESSAGE_CENTER, tUserId, message);
		} catch (ApiException e) {
            logger.error("transfer {} from {} to {} failure", total, fUserId, tUserId, e);
            throw e;
		} catch (Exception e) {
			logger.error("用户:{}财币转让给用户:{}时出错:{}",fUserId,tUserId,e.getMessage());
			throw new ApiException(-1, "转账失败");
		}
	}
}
