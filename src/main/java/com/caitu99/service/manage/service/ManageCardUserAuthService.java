package com.caitu99.service.manage.service;

import com.caitu99.service.manage.domain.ManageCardUserAuth;

public interface ManageCardUserAuthService {
	
	/**
	 * 根据用户Id查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUserId 
	 * @param userId	用户ID
	 * @return
	 * @date 2016年2月22日 下午4:04:14  
	 * @author xiongbin
	 */
	ManageCardUserAuth selectByUserId(Long userId);
	
	/**
	 * 验证用户是否认证过
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: isAuth 
	 * @param userId	用户ID
	 * @return
	 * @date 2016年2月22日 下午4:04:45  
	 * @author xiongbin
	 */
	boolean isAuth(Long userId);
	
	void insert(ManageCardUserAuth record);
}
