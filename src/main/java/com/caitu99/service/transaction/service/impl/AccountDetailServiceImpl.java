/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.transaction.dao.AccountDetailMapper;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.CusTransactionRecord;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountDetailService;

/**
 * 
 * @Description: (类职责详细描述,可空)
 * @ClassName: AccountDetailServiceImpl
 * @author ws
 * @date 2015年12月1日 下午8:05:29
 * @Copyright (c) 2015-2020 by caitu99
 */
@Service
public class AccountDetailServiceImpl implements AccountDetailService {

	@Autowired
	AccountDetailMapper accountDetailMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.caitu99.service.transaction.service.AccountDetailService#
	 * insertAccountDetail(com.caitu99.service.transaction.domain.AccountDetail)
	 */
	@Override
	public void insertAccountDetail(AccountDetail accountDetail) {

		accountDetailMapper.insert(accountDetail);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.caitu99.service.transaction.service.AccountDetailService#
	 * countTotalIntegralByUserId(java.lang.Long)
	 */
	@Override
	public Long countTotalIntegralByUserId(Long userId) {

		return accountDetailMapper.countTotalIntegralByUserId(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.caitu99.service.transaction.service.AccountDetailService#
	 * selectByUserIdAndTime(java.lang.Long, java.util.Date, java.util.Date)
	 */
	@Override
	public Pagination<AccountDetail> selectByUserIdAndTime(Long userId, Date begin,
			Date end, Pagination<AccountDetail> pagination) {
		
		Map map = new HashMap();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		map.put("userId", userId);
		map.put("begin", sdf.format(begin));
		map.put("end", sdf.format(end));
		if( pagination != null){
			map.put("start", pagination.getStart());
			map.put("pageSize", pagination.getPageSize());
		}else{
			pagination = new Pagination<AccountDetail>();
		}
		List<AccountDetail> list = accountDetailMapper.selectByUserIdAndTime(map);
		Integer count = accountDetailMapper.selectCountByUserId(map);
		
		pagination.setDatas(list);
		pagination.setTotalRow(count);

		return pagination;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.service.AccountDetailService#insert(com.caitu99.service.transaction.domain.AccountDetail)
	 */
	@Override
	public void insert(AccountDetail accountDetail) {
		accountDetailMapper.insert(accountDetail);
	}
	
	public void saveAccountDetail(Long recordId,
			TransactionRecordDto transactionRecordDto, Long tradeType) {
		AccountDetail accountDetail = new AccountDetail();
		accountDetail.setGmtCreate(new Date());
		accountDetail.setGmtModify(new Date());
		accountDetail.setIntegralChange(transactionRecordDto.getTotal());
		accountDetail.setMemo("");
		accountDetail.setRecordId(recordId);
		accountDetail.setType(tradeType.longValue() > 0 ? 1 : 2);
		accountDetail.setUserId(transactionRecordDto.getUserId());
		accountDetailMapper.insert(accountDetail);
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.service.AccountDetailService#saveAccountDetailTubi(java.lang.Long, com.caitu99.service.transaction.dto.TransactionRecordDto, int)
	 */
	@Override
	public void saveAccountDetailTubi(Long recordId,
			TransactionRecordDto transactionRecordDto, int type) {
		AccountDetail accountDetail = new AccountDetail();
		accountDetail.setGmtCreate(new Date());
		accountDetail.setGmtModify(new Date());
		accountDetail.setIntegralChange(transactionRecordDto.getTubi());
		accountDetail.setMemo("");
		accountDetail.setRecordId(recordId);
		accountDetail.setType(type);
		accountDetail.setUserId(transactionRecordDto.getUserId());
		accountDetailMapper.insert(accountDetail);
	}

}
