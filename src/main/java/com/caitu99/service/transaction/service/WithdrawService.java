package com.caitu99.service.transaction.service;

import com.caitu99.service.exception.WithdrawException;

/**
 * @Description: (提现) 
 * @ClassName: WithdrawService 
 * @author Hongbo Peng
 * @date 2016年3月30日 下午2:18:31 
 * @Copyright (c) 2015-2020 by caitu99
 */
public interface WithdrawService {

	/**
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: withdraw 
	 * @param userId 用户编号
	 * @param userBankCardId 银行卡编号
	 * @param amount	提现金额
	 * @param payPass	支付密码
	 * @return
	 * @date 2016年3月30日 下午2:22:34  
	 * @author Hongbo Peng
	 */
	String withdraw(Long userId,Long userBankCardId,Long amount,String payPass) throws WithdrawException ;
	
	/**
	 * @Description: (定时查询提现订单是否完成)  
	 * @Title: queryWithdraw 
	 * @param id
	 * @return
	 * @throws WithdrawException
	 * @date 2016年3月30日 下午5:20:58  
	 * @author Hongbo Peng
	 */
	String queryWithdraw(Long id) throws WithdrawException;

	/**
	 * @Description: (是否可提现)
	 * @Title: isWithdraw
	 * @param userId
	 * @return
	 * @date 2016年4月5日
	 * @author liuzs
	 */
	String isWithdraw(Long userId);
}
