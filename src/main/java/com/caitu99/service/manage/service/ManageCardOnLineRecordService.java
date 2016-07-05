package com.caitu99.service.manage.service;

import com.caitu99.service.user.domain.UserAuth;


public interface ManageCardOnLineRecordService {
	
	/**
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveOnLine 
	 * @param userAuth			用户认证对象
	 * @param bankCardId		银行卡ID
	 * @date 2015年12月28日 下午7:23:13  
	 * @author xiongbin
	 */
	void insert(UserAuth userAuth,Long bankCardId);
}
