package com.caitu99.service.transaction.service;

import com.caitu99.service.transaction.controller.vo.AccountResult;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.user.domain.User;

public interface AccountService {

	// 根据用户id获得账户
	Account selectByUserId(Long userId);

	// 增加财币
	AccountResult add(TransactionRecordDto transactionRecordDto);

	// 扣除财币
	AccountResult pay(TransactionRecordDto transactionRecordDto);

	// 充财币From银联
	AccountResult addFromUnionPay(Long userid, Long unionid, Long integral,
			String orderNo, String tNo, Long clientId);

	/**
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: updateAccountByUserId
	 * @param record
	 * @date 2015年12月1日 下午8:31:27
	 * @author ws
	 */
	void updateAccountByUserId(Account record);
	
	/**
	 * 判断用户财币是否够用
	 */
	boolean isEnough(Long userid,Long integral);
	
	public void initAccount();

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: addAccount 
	 * @param id
	 * @date 2015年12月4日 上午9:22:47  
	 * @author ws
	*/
	void addNewAccount(Long id);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: updateAccount 
	 * @param account
	 * @param transactionRecordDto
	 * @param reduce
	 * @date 2016年1月21日 下午5:27:54  
	 * @author ws
	*/
	void updateAccount(Account account,
			TransactionRecordDto transactionRecordDto, Long reduce);

	Account selectByPrimaryKeyForUpdate(Long id);
	
	/**
	 * sdk支付
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: sdkPay 
	 * @param enterpriseUserId
	 * @param user
	 * @param account
	 * @param isPay
	 * @date 2016年5月31日 下午5:44:02  
	 * @author xiongbin
	 */
	void sdkPay(Long enterpriseUserId,User user,Account account,Long payTubi,String payPassword,boolean isPay);
	
	/**
	 * 积分商城购买成功赠送途币
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: giveTubi 
	 * @param userId	用户Id
	 * @param tubi		赠送途币数量
	 * @date 2016年6月1日 下午2:34:15  
	 * @author xiongbin
	 */
	void giveTubi(Long userId,Long tubi);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pay2 
	 * @param transactionRecordDto
	 * @return
	 * @date 2016年6月13日 下午3:02:45  
	 * @author ws
	*/
	AccountResult pay2(TransactionRecordDto transactionRecordDto);
	
	void paySuccessDo(Long userId, String orderNo, String oidPayBill);
	
	void payFailureDo(Long userId, String orderNo);

	/**
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pay3 
	 * @param transactionRecordDto
	 * @return
	 * @date 2016年6月16日 下午4:21:47  
	 * @author xiongbin
	*/
	AccountResult pay3(TransactionRecordDto transactionRecordDto);

	/**
	 * 实名认证订单完成
	 * @Title: authenticationSuccessDo 
	 * @param userId
	 * @param orderNo
	 * @param oidPaybill	连连支付订单号
	 * @date 2016年6月16日 下午4:52:55  
	 * @author xiongbin
	*/
	void authenticationSuccessDo(Long userId, String orderNo,String oidPaybill);

	/**
	 * 实名认证订单退款  
	 * @Title: authenticationRefundDo 
	 * @param userId
	 * @param orderNo
	 * @date 2016年6月16日 下午5:22:06  
	 * @author xiongbin
	*/
	void authenticationRefundDo(Long userId, String orderNo);

	/**
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pay4 
	 * @param transactionRecordDto
	 * @return
	 * @date 2016年6月17日 下午2:55:19  
	 * @author xiongbin
	*/
	AccountResult pay4(TransactionRecordDto transactionRecordDto);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: payNew 
	 * @param transactionRecordDto
	 * @return
	 * @date 2016年6月17日 上午9:05:32  
	 * @author ws
	*/
	AccountResult payNew(TransactionRecordDto transactionRecordDto);
}
