package com.caitu99.service.manage.service;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.manage.domain.ManageCardDropInRecord;
import com.caitu99.service.user.domain.UserAuth;


public interface ManageCardDropInRecordService {
	
	/**
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: insert 
	 * @param userAuth					用户认证信息
	 * @param manageCardDropInRecord	上门办卡信息
	 * @date 2015年12月28日 下午9:10:51  
	 * @author xiongbin
	 */
	void insert(UserAuth userAuth,ManageCardDropInRecord manageCardDropInRecord);
	
	/**
	 * 分页查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findPageRecord 
	 * @param manageCardDropInRecord
	 * @param pagination
	 * @return
	 * @date 2015年12月29日 上午10:10:02  
	 * @author xiongbin
	 */
	Pagination<ManageCardDropInRecord> findPageRecord(ManageCardDropInRecord manageCardDropInRecord,Pagination<ManageCardDropInRecord> pagination);
}
