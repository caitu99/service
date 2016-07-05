package com.caitu99.service.merchant.service;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.merchant.domain.PersonnelManage;

public interface PersonnelManageService {
	
	/**
	 * 分页查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findPageItem 
	 * @param personnelManage
	 * @param pagination
	 * @return
	 * @date 2016年6月20日 下午4:22:40  
	 * @author xiongbin
	 */
	Pagination<PersonnelManage> findPageItem(PersonnelManage personnelManage,Pagination<PersonnelManage> pagination);
	
	PersonnelManage selectByPrimaryKey(Long id);
	
	void insert(PersonnelManage personnelManage);
	
	void update(PersonnelManage personnelManage);
	
	String create(PersonnelManage personnelManage);
	
	String modify(PersonnelManage personnelManage);

	String detele(PersonnelManage personnelManage);
}
