package com.caitu99.service.user.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.user.dao.UserInvitationMapper;
import com.caitu99.service.user.dao.UserMapper;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserInvitation;
import com.caitu99.service.user.service.UserInvitationService;
import com.caitu99.service.user.service.UserService;

@Service
public class UserInvitationServiceImpl implements UserInvitationService {
	
	private final static Logger logger = LoggerFactory.getLogger(UserInvitationServiceImpl.class);
	
	@Autowired
	private UserInvitationMapper userInvitationMapper;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserMapper userMapper;

	@Override
	public String receiveInvitation(Long invitedUserId, String invitationCode) {
		ApiResult<Boolean> apiResult = new ApiResult<Boolean>();
		
		if(null == invitedUserId){
			return apiResult.toJSONString(-1, "userid can not empty");
		}
		if(StringUtils.isBlank(invitationCode)){
			return apiResult.toJSONString(-1, "invitationCode can not empty");
		}
		if(invitationCode.length() != 6){
			return apiResult.toJSONString(-1, "请输入正确的邀请码");
		}
		
		logger.info("兑换邀请码,被邀请用户：{},邀请码：{}",invitedUserId,invitationCode);
		
		try {
			User user = userMapper.selectByPrimaryKey(invitedUserId);
			if(null == user){
				throw new UserNotFoundException(-1, "被邀请人不存在");
			}
			
			Date gmtCreate = user.getGmtCreate();
			if(null == gmtCreate){
				logger.warn("用户ID：" + invitedUserId + "创建时间为空,不可被邀请");
				return apiResult.toJSONString(-1, "非常抱歉,您不是新用户");
			}
			
			Date now = new Date();
			Integer receiveDay = 2;
			if(now.getTime() - gmtCreate.getTime() > receiveDay*24*60*60*1000){
				logger.info("用户ID：" + invitedUserId + "注册时间已大于2天,不可被邀请");
				return apiResult.toJSONString(-1, "非常抱歉,您不是新用户");
			}
			
			User invitationUser = new User();
			invitationUser.setInvitationCode(invitationCode);
			invitationUser = userService.selectBySelective(invitationUser);
			if(invitationUser == null){
				return apiResult.toJSONString(-1, "邀请码不存在");
			}
			
			Long invitationUserId = invitationUser.getId();
			
			if(invitationUserId.equals(invitedUserId)){
				return apiResult.toJSONString(-1, "自己不能邀请自己");
			}
			
			UserInvitation userInvitation = new UserInvitation();
			userInvitation.setInvitedUserId(invitedUserId);
			
			List<UserInvitation> list = this.selectBySelective(userInvitation);
			if(null != list && list.size() > 0){
				return apiResult.toJSONString(-1, "您已经被邀请过了");
			}

			userInvitation.setInvitationUserId(invitationUserId);
			userInvitation.setGmtCreate(now);
			userInvitation.setGmtModify(now);
			userInvitation.setStatus(UserInvitation.STATUS_NORMAL);
			userInvitationMapper.insert(userInvitation);
			
			return apiResult.toJSONString(0, "兑换成功");
		} catch (Exception e) {
			logger.error("兑换邀请码失败:" + e.getMessage(),e);
			return apiResult.toJSONString(-1, "兑换邀请码失败:" + e.getMessage());
		}
	}

	@Override
	public List<UserInvitation> selectBySelective(UserInvitation userInvitation) {
		Map<String,Object> map = new HashMap<String,Object>(1);
		map.put("userInvitation", userInvitation);
		
		return userInvitationMapper.selectBySelective(map);
	}
}
