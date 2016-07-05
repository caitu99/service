package com.caitu99.service.user.service;

import java.util.List;
import java.util.Map;

import com.caitu99.service.user.domain.UserBankCard;

public interface UserBankCardService {

	List<UserBankCard> selectByUserId(Long userId,Integer form);
	
	Long saveUserBankCard(UserBankCard userBankCard);
	
	UserBankCard selectById(Long id);
	
	void delUserBankCard(Long id);

	//修改默认银行卡
	void updateByUseIdAndCardNo(UserBankCard userBankCard);
	
	/**
	 * 解绑
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: unboundUserBankCard 
	 * @param userid
	 * @param ids
	 * @date 2016年6月8日 下午6:11:09  
	 * @author xiongbin
	 */
	void unboundUserBankCard(Long userid,String[] ids);
	
	/**
	 * 保存支付银行卡
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: addPayBankCard 
	 * @param userid		用户ID
	 * @param accName		持卡人
	 * @param accId			身份证
	 * @param cardNo		银行卡
	 * @param cardType		卡片类型(0:储蓄卡;1:信用卡)
	 * @param bankName		银行名称
	 * @date 2016年6月13日 下午4:16:00  
	 * @author xiongbin
	 */
	void addPayBankCard(Long userid,String accName,String accId,String cardNo,Integer cardType,String bankName);
	
    List<UserBankCard> selectByUserBackCard(UserBankCard userBankCard);
}
