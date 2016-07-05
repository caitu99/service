package com.caitu99.service.user.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.user.dao.UserBankCardMapper;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserAuth;
import com.caitu99.service.user.domain.UserBankCard;
import com.caitu99.service.user.service.UserAuthService;
import com.caitu99.service.user.service.UserBankCardService;
import com.caitu99.service.user.service.UserService;

@Service
public class UserBankCardServiceImpl implements UserBankCardService{

	@Autowired
	private UserBankCardMapper userBankCardMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private UserAuthService userAuthService;
	
	@Override
	public List<UserBankCard> selectByUserId(Long userId,Integer form) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userId", userId);
		map.put("form", form);
		return userBankCardMapper.selectByUserId(map);
	}

	@Override
	public Long saveUserBankCard(UserBankCard userBankCard) {
		Date date = new Date();
		if(null == userBankCard.getCreateTime()){
			userBankCard.setCreateTime(date);
		}
		if(null == userBankCard.getUpdateTime()){
			userBankCard.setUpdateTime(date);
		}

		UserBankCard userBankCards = userBankCardMapper.selectByUseIdAndCardNo(userBankCard);
		//判断表中是否有这个用户的这张银行卡
		if(null != userBankCards){
			return userBankCards.getId();
		}
		userBankCardMapper.insertSelective(userBankCard);
		this.updateByUseIdAndCardNo(userBankCard);
		return userBankCard.getId();
	}

	@Override
	public UserBankCard selectById(Long id) {
		return userBankCardMapper.selectByPrimaryKey(id);
	}

	@Override
	public void delUserBankCard(Long id) {
		userBankCardMapper.deleteByPrimaryKey(id);
	}

	@Override
	public void updateByUseIdAndCardNo(UserBankCard userBankCard) {
		userBankCardMapper.updateByUseIdAndCardNo(userBankCard);
		userBankCardMapper.updateByUseIdAndCardNo1(userBankCard);
	}

	@Override
	@Transactional
	public void unboundUserBankCard(Long userid, String[] ids) {
		for(int i=0;i<ids.length;i++){
			Long id = Long.parseLong(ids[i]);
			UserBankCard userBankCard = this.selectById(id);
			if(null == userBankCard){
				throw new ApiException(-1, "该银行卡不存在");
			}else if(!userBankCard.getUserId().equals(userid)){
				throw new ApiException(-1, "该银行卡不属于您");
			}
			
			userBankCard.setStatus(-1);
			userBankCardMapper.updateByPrimaryKeySelective(userBankCard);
		}
	}

	@Override
	@Transactional
	public void addPayBankCard(Long userid, String accName, String accId,String cardNo, Integer cardType,String bankName) {
		Date now = new Date();
		User user = userService.selectByPrimaryKey(userid);
		if(null == user){
			throw new UserNotFoundException(-1,"用户不存在");
		}
		
		UserBankCard userBankCard = new UserBankCard();
		userBankCard.setUserId(userid);
		userBankCard.setAccId(accId);
		userBankCard.setAccName(accName);
		userBankCard.setCardNo(cardNo);
		userBankCard.setCardType(cardType);
		userBankCard.setBankName(bankName);
		userBankCard.setForm(1);
		
		List<UserBankCard> list = this.selectByUserBackCard(userBankCard);
		if(null != list && list.size() > 0){
			return;
		}

		userBankCard.setCreateTime(now);
		userBankCard.setUpdateTime(now);
		
		userBankCardMapper.insertSelective(userBankCard);
		
		UserAuth userAuth = userAuthService.selectByUserId(userid);
		if(null == userAuth){
			userAuth = new UserAuth();
			userAuth.setAccId(accId);
			userAuth.setAccName(accName);
			userAuth.setBankname(bankName);
			userAuth.setCardNo(cardNo);
			userAuth.setGmtCreate(now);
			userAuth.setGmtModify(now);
			userAuth.setUserId(userid);
			userAuthService.insert(userAuth);
		}
	}

	@Override
	public List<UserBankCard> selectByUserBackCard(UserBankCard userBankCard) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userBankCard", userBankCard);
		return userBankCardMapper.selectByUserBackCard(map);
	}

}
