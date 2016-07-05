package com.caitu99.service.ishop.service;


import com.caitu99.service.ishop.domain.UserChinaConstructionBankCard;

public interface UserChinaConstructionBankCardService {

	void insert(Long userId,String cardNo,String reservedPhone);
	
	void update(UserChinaConstructionBankCard userChinaConstructionBankCard);

	/**
	 * 查询用户最新使用
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUserIdNewest 
	 * @param userId
	 * @return
	 * @date 2016年1月29日 下午3:56:51  
	 * @author xiongbin
	 */
	UserChinaConstructionBankCard selectByUserIdNewest(Long userId);
    
    /**
     * 根据用户ID和银行卡号查询
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectWhether 
     * @param userId			用户ID
     * @param cardNo			银行卡号
     * @return
     * @date 2016年1月29日 下午3:58:08  
     * @author xiongbin
     */
	UserChinaConstructionBankCard selectWhether(Long userId,String cardNo);
	
	/**
	 * 设置缓存
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: updateRedis 
	 * @param userChinaConstructionBankCard
	 * @date 2016年2月3日 下午5:49:03  
	 * @author xiongbin
	 */
	void updateRedis(UserChinaConstructionBankCard userChinaConstructionBankCard);
}
