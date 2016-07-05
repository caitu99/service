/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.service;

import java.util.Date;
import java.util.List;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.CusTransactionRecord;
import com.caitu99.service.transaction.dto.TransactionRecordDto;

/**
 * 
 * @Description: (类职责详细描述,可空)
 * @ClassName: AccountDetailService
 * @author ws
 * @date 2015年12月1日 下午8:05:06
 * @Copyright (c) 2015-2020 by caitu99
 */
public interface AccountDetailService {

	public void insertAccountDetail(AccountDetail accountDetail);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: countTotalIntegralByUserId
	 * @param userId
	 * @return
	 * @date 2015年12月1日 下午8:37:13
	 * @author ws
	 */
	public Long countTotalIntegralByUserId(Long userId);

	Pagination<AccountDetail> selectByUserIdAndTime(Long userId, Date begin, Date end ,Pagination<AccountDetail> pagination);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: insert 
	 * @param accountDetail
	 * @date 2016年1月21日 下午2:47:22  
	 * @author ws
	*/
	public void insert(AccountDetail accountDetail);
	
	/**
	 * 新增账户明细
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveAccountDetail 
	 * @param recordId
	 * @param transactionRecordDto
	 * @param tradeType
	 * @date 2016年1月21日 下午4:18:13  
	 * @author ws
	 */
	public void saveAccountDetail(Long recordId,
			TransactionRecordDto transactionRecordDto, Long tradeType);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveAccountDetailTubi 
	 * @param recordId
	 * @param transactionRecordDto
	 * @param type
	 * @date 2016年5月14日 下午2:12:25  
	 * @author ws
	*/
	public void saveAccountDetailTubi(Long recordId,
			TransactionRecordDto transactionRecordDto, int type);
}
