package com.caitu99.service.realization.service;

import java.util.List;

import com.caitu99.service.realization.domain.UserAddTerm;


public interface UserAddTermService {
	
	/**
	 * 查询用户积分变现添加项
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectListByUserId 
	 * @param userId	用户ID
	 * @return
	 * @date 2016年2月23日 下午3:29:22  
	 * @author xiongbin
	 */
    List<UserAddTerm> selectListByUserId(Long userId);
    
    void update(UserAddTerm record);
    
    void insert(UserAddTerm record);
    
    Long insertORupdate(UserAddTerm record);
    
    UserAddTerm selectUserAddTerm(UserAddTerm record);
    
    UserAddTerm selectByPrimaryKey(Long id);
}
