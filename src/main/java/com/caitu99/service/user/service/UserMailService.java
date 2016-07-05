package com.caitu99.service.user.service;

import com.caitu99.service.user.domain.UserMail;

import java.util.List;

public interface UserMailService {

	List<UserMail> list();

	int insert(UserMail userMail);

	UserMail selectByEmail(String email);

	int updateByPrimaryKeySelective(UserMail record);

	List<UserMail> selectByUserId(Long userId);

	UserMail selectByUserIdAndMail(UserMail userMail);

	int updateByLastUpdate(UserMail record);

	// 用户积分可兑换人民币
	Long Convertibleintegral(Long userId);

	int updateByUserIdAndMail(UserMail userMail);

	/**
	 * 获取自动更新邮箱列表
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUserIdForJob 
	 * @param userId
	 * @return
	 * @date 2015年12月16日 下午2:15:33  
	 * @author ws
	*/
	List<UserMail> selectByUserIdForJob(Long userId);
}
