package com.caitu99.service.ishop.service;


import com.caitu99.service.ishop.domain.UserChinaConstructionBank;

@Deprecated
public interface UserChinaConstructionBankService {

	void insert(Long userId,String loginAccount,String password,String phone);
	
	void update(UserChinaConstructionBank userChinaConstructionBank);

	/**
	 * 查询用户最新使用
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUserIdNewest 
	 * @param userId
	 * @return
	 * @date 2016年1月29日 下午3:56:51  
	 * @author xiongbin
	 */
    UserChinaConstructionBank selectByUserIdNewest(Long userId);
    
    /**
     * 根据用户ID和登录名查询
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectWhether 
     * @param userId			用户ID
     * @param loginAccount		用户名
     * @return
     * @date 2016年1月29日 下午3:58:08  
     * @author xiongbin
     */
    UserChinaConstructionBank selectWhether(Long userId,String loginAccount);
    
    /**
	 * 设置缓存
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: updateRedis 
	 * @param userChinaConstructionBank
	 * @date 2016年2月3日 下午5:37:50  
	 * @author xiongbin
	 */
    void updateRedis(UserChinaConstructionBank userChinaConstructionBank);
}
