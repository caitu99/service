package com.caitu99.service.ishop.service;


import com.caitu99.service.ishop.domain.UserThirdIntegralShopRecord;

public interface UserThirdIntegralShopRecordService {

	void insert(Long userId,String loginAccount,String password,String phone,Integer type);
	
	void update(UserThirdIntegralShopRecord userThirdIntegralShopRecord);

	/**
	 * 查询用户最新使用
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUserIdNewest 
	 * @param userId	用户ID
	 * @param type		商城类型
	 * @return
	 * @date 2016年1月29日 下午3:56:51  
	 * @author xiongbin
	 */
    UserThirdIntegralShopRecord selectByUserIdNewest(Long userId,Integer type);
    
    /**
     * 根据用户ID,登录名,商城类型查询
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectWhether 
     * @param userId			用户ID
     * @param loginAccount		登录名
     * @param type				商城类型
     * @return
     * @date 2016年1月29日 下午3:58:08  
     * @author xiongbin
     */
    UserThirdIntegralShopRecord selectWhether(Long userId,String loginAccount,Integer type);
    
    /**
	 * 设置缓存
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: updateRedis 
	 * @param userThirdIntegralShopRecord
	 * @date 2016年2月3日 下午5:37:50  
	 * @author xiongbin
	 */
    void updateRedis(UserThirdIntegralShopRecord userThirdIntegralShopRecord);
}
