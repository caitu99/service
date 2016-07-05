package com.caitu99.service.user.service;

import java.util.List;

import com.caitu99.service.user.domain.UserInvitation;


public interface UserInvitationService {

	/**
	 * 兑换邀请码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: receiveInvitation 
	 * @param invitedUserId			被邀请人用户ID
	 * @param invitationCode		邀请码
	 * @date 2016年1月5日 下午12:00:47  
	 * @author xiongbin
	 */
	String receiveInvitation(Long invitedUserId,String invitationCode);
	
	/**
	 * 查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectBySelective 
	 * @param userInvitation
	 * @return
	 * @date 2016年1月5日 下午2:29:45  
	 * @author xiongbin
	 */
	List<UserInvitation> selectBySelective(UserInvitation userInvitation);
}
