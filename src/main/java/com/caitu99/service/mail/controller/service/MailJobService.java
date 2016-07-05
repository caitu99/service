/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mail.controller.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.mail.controller.api.MailImportController;
import com.caitu99.service.mail.controller.util.MailDateUtil;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.user.domain.UserCard;
import com.caitu99.service.user.domain.UserMail;
import com.caitu99.service.user.service.UserCardService;
import com.caitu99.service.user.service.UserMailService;
import com.caitu99.service.utils.Configuration;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MailJobService 
 * @author ws
 * @date 2015年12月10日 下午5:44:01 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class MailJobService{

	private static final Logger logger = LoggerFactory
			.getLogger(MailJobService.class);
	
	@Autowired
	MailImportController mailImportController;
	@Autowired
	UserCardService userCardService;
	@Autowired
	UserMailService userMailService;
	@Autowired
	MailParseService mailParseService;
	@Autowired
	CardTypeService cardTypeService;
	@Autowired
	MailJobRegisterService mailJobRegisterService;
	@Autowired
	KafkaProducer kafkaProducer;
	@Autowired
	PushMessageService pushMessageService;
	
	private String MAIL_ERR_MSG = "【邮箱爬取失败】：userId:{},account:{},errMsg:{}";
	private String CARD_ERR_MSG = "【邮箱自动更新失败】：userId:{},cardTypeId:{},errMsg:{}";
	private String MAIL_INFO_MSG = "【邮箱爬取提示信息】：userId:{},account:{},errMsg:{}";
	
	public void parseMail(String userId,String cardTypeId){
		logger.info("邮箱自动更新开始");
		
		Calendar now = Calendar.getInstance();
		//后3天
    	now.add(Calendar.DAY_OF_MONTH, -3);
    	//logger.info("【邮箱自动更新】3天前{}",now.get(Calendar.DAY_OF_MONTH));
    	//获取当日需要做更新的用户卡信息
    	List<UserCard> userCardList = userCardService.queryUserCardForJob(now.get(Calendar.DAY_OF_MONTH));
    	for (UserCard userCard : userCardList) {
    		parseSingleUserAndCard(userCard.getUserId(), userCard.getCardTypeId());
		}
    	
    	//后六天
    	now.add(Calendar.DAY_OF_MONTH, -3);
    	//logger.info("【邮箱自动更新】6天前{}",now.get(Calendar.DAY_OF_MONTH));
    	//获取当日需要做更新的用户卡信息
    	userCardList = userCardService.queryUserCardForJob(now.get(Calendar.DAY_OF_MONTH));
    	for (UserCard userCard : userCardList) {
    		parseSingleUserAndCard(userCard.getUserId(), userCard.getCardTypeId());
		}
    	//后6天未成功，消息推送
		doMessageReport(userCardList);
		
		logger.info("邮箱自动更新完成");
		
	}


	/**
	 * 按用户和账单类别进行更新
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: parseSingleUserAndCard 
	 * @param userIdStr
	 * @param cardTypeIdStr
	 * @date 2015年12月17日 下午4:31:31  
	 * @author ws
	 */
	private void parseSingleUserAndCard(Long userId, Long cardTypeId) {
		if(null == userId || null == cardTypeId){
			logger.warn(MAIL_ERR_MSG,userId,cardTypeId,"用户Id或cardTypeId为空");
			return;
		}
		
		//获取原生列表，用于判断是否发现新账单
		UserCard userCardParam = new UserCard();
		userCardParam.setUserId(userId);
		List<UserCard> oldUserCardList = userCardService.selectByConditions(userCardParam );
		
		//前面加判断，如果卡本月账单已更新，则不再爬取
		List<UserCard> userCardOldList = userCardService.selectUserCardForJob(userId,cardTypeId);
		if(null == userCardOldList || 0 == userCardOldList.size()){
			logger.warn(CARD_ERR_MSG,userId,cardTypeId,"用户card信息不存在");
			return;
		}
		boolean isAllNew = checkIsAllNew(userCardOldList);
		if(isAllNew){//全部更新了，就不再更新
			return;
		}
		
		//查找需要抓取的邮箱账号密码信息
		List<UserMail> userMailList = userMailService.selectByUserIdForJob(userId);
		for (UserMail userMail : userMailList) {
			try {
				//单个邮箱抓取
				mailParseService.parseSigleMail(userMail);
			} catch (Exception e) {
				logger.error(MAIL_ERR_MSG,userId,userMail.getEmail(),e);
			}
		}
		
		//抓取完后,注册下个任务执行时间
		//判断某卡是否已更新，如果更新了，则注册下个账单日的后3天。否则注册当前账单日的后6天
		List<UserCard> newUserCardList = userCardService.selectByConditions(userCardParam );
		checkNewCard(oldUserCardList,newUserCardList);
		
		checkIntegerUpdate(oldUserCardList,newUserCardList);
		
		List<UserCard> userCardList = userCardService.selectUserCardForJob(userId,cardTypeId);
		if(null == userCardList){
			logger.warn(CARD_ERR_MSG,userId,cardTypeId,"用户card信息不存在");
			return;
		}
	}
	
	/**
	 * 账单日后6日未更新成功时，消息推送
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: doMessageReport 
	 * @param userCardList
	 * @date 2015年12月19日 下午2:16:16  
	 * @author ws
	 */
	private void doMessageReport(List<UserCard> userCardList){

		List<UserCard> messageUserCard = new ArrayList<UserCard>();
		for (UserCard userCard : userCardList) {
			int billMounth = MailDateUtil.getMounthFromDate(userCard.getBillMonth());
			int thisMounth = MailDateUtil.getMounthFromDate(null);
			if(billMounth != thisMounth){
				messageUserCard.add(userCard);
			}
		}
		
		//合并推送
		//本月后6天未更新成功合并提示
		if(!CollectionUtils.isEmpty(messageUserCard)){//不为空 
			Long userId = null;
			userId = userCardList.get(0).getUserId();
			StringBuffer cardTypeStr = getUserCardsNames(userCardList);
			//统一推
			try {
				String description =  Configuration.getProperty("push.mail.not.update.description", null);
				String title =  Configuration.getProperty("push.mail.not.update.title", null);
				Message message = new Message();
				message.setIsPush(true);
				message.setIsSMS(false);
				message.setIsYellow(false);
				message.setTitle(title);
				message.setPushInfo(String.format(description,cardTypeStr.toString()));
				logger.info("新增消息通知：userId:{},message:{}",userId,JSON.toJSONString(message));
				pushMessageService.pushMessage(RedSpot.MESSAGE_CENTER, userId, message);
			} catch (Exception e) {
				logger.error("邮箱账单本月账户未更新消息推送异常：{}",e);
			}
			
		}
	}

	/**
	 * 判断是否已全部更新了
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkIsAllNew 
	 * @param userCardOldList
	 * @return
	 * @date 2015年12月17日 下午5:16:39  
	 * @author ws
	 */
	private boolean checkIsAllNew(List<UserCard> userCardOldList) {
		boolean isAllNew = true;
		for (UserCard userCard : userCardOldList) {
			int billMounth = MailDateUtil.getMounthFromDate(userCard.getBillMonth());
			int thisMounth = MailDateUtil.getMounthFromDate(null);
			if(billMounth != thisMounth){
				isAllNew = false;
			}
		}
		return isAllNew;
	}

	
	/**
	 * 新账户发现
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkNewCard 
	 * @param oldUserCardList
	 * @param newUserCardList
	 * @date 2015年12月11日 下午3:30:16  
	 * @author ws
	*/
	private void checkNewCard(List<UserCard> oldUserCardList,
			List<UserCard> newUserCardList) {
		List<UserCard> newCardList = new ArrayList<UserCard>();
		for (UserCard newUserCard : newUserCardList) {
			boolean isNew = true;
			for (UserCard oldUserCard : oldUserCardList) {
				if(newUserCard.getCardTypeId().equals(oldUserCard.getCardTypeId())){
					if(StringUtils.isNotBlank(newUserCard.getCardNo()) && StringUtils.isNotBlank(oldUserCard.getCardNo())){
						if(newUserCard.getCardNo().equals(oldUserCard.getCardNo())){
							isNew = false;
						}
					}else if (StringUtils.isBlank(newUserCard.getCardNo()) && StringUtils.isBlank(oldUserCard.getCardNo())){
						isNew = false;
					}
				}
			}
			if(isNew){//在old中不存在，说明发现新卡，放到newCardList中
				newCardList.add(newUserCard);
			}
		}
		
		pushNewCardFinderMessage(newCardList);
		
	}


	/**
	 * 推送发现新账单	
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pushNewCardFinderMessage 
	 * @param newCardList
	 * @date 2015年12月25日 下午3:16:59  
	 * @author Hongbo Peng
	*/
	private void pushNewCardFinderMessage(List<UserCard> newCardList) {
		if(!CollectionUtils.isEmpty(newCardList)){//不为空   提示发现新账户
			Long userId = null;
			userId = newCardList.get(0).getUserId();
			StringBuffer cardTypeStr = getUserCardsNames(newCardList);
			//统一推
			try {
				String description =  Configuration.getProperty("push.mail.card.find.description", null);
				String title =  Configuration.getProperty("push.mail.card.find.title", null);
				Message message = new Message();
				message.setIsPush(true);
				message.setIsSMS(false);
				message.setIsYellow(false);
				message.setTitle(title);
				message.setPushInfo(String.format(description,cardTypeStr.toString()));
				logger.info("新增消息通知：userId:{},message:{}",userId,JSON.toJSONString(message));
				pushMessageService.pushMessage(RedSpot.MESSAGE_CENTER, userId, message);
			} catch (Exception e) {
				logger.error("积分变动推送消息异常：{}",e);
			}
		}
	}
	
	/**
	 * 积分更新
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkNewCard 
	 * @param oldUserCardList
	 * @param newUserCardList
	 * @date 2015年12月11日 下午3:30:16  
	 * @author ws
	*/
	private void checkIntegerUpdate(List<UserCard> oldUserCardList,
			List<UserCard> newUserCardList) {
		Map<Long,Integer> changeMap = new HashMap<Long,Integer>();
		List<UserCard> newCardList = new ArrayList<UserCard>();
		for (UserCard newUserCard : newUserCardList) {
			for (UserCard oldUserCard : oldUserCardList) {
				if(newUserCard.getCardTypeId().equals(oldUserCard.getCardTypeId())){
					//卡号不为空的情况
					if(StringUtils.isNotBlank(newUserCard.getCardNo()) && StringUtils.isNotBlank(oldUserCard.getCardNo())){
						if(newUserCard.getCardNo().equals(oldUserCard.getCardNo())){
							if(!newUserCard.getIntegralBalance().equals(oldUserCard.getIntegralBalance())){
								newCardList.add(newUserCard);
								changeMap.put(newUserCard.getId(), newUserCard.getIntegralBalance() - oldUserCard.getIntegralBalance());
							}
						}
					//卡号为空的情况
					}else if (StringUtils.isBlank(newUserCard.getCardNo()) && StringUtils.isBlank(oldUserCard.getCardNo())){
						if(!newUserCard.getIntegralBalance().equals(oldUserCard.getIntegralBalance())){
							newCardList.add(newUserCard);
							changeMap.put(newUserCard.getId(), newUserCard.getIntegralBalance() - oldUserCard.getIntegralBalance());
						}
					}
				}
				
			}
			
		}
		
		for (UserCard userCard : newCardList) {
			try {
				CardType cardType = cardTypeService.selectByPrimaryKey(userCard.getCardTypeId());
				String account = cardType.getName();
				String newIntegral = userCard.getIntegralBalance().toString();
				String changeIntegral = changeMap.get(userCard.getId()).toString();
				
				RedSpot redPot = null;
				switch (cardType.getTypeId()) {
				case 1:
					redPot = RedSpot.CREDIT_INTEGRAL;
					break;
				case 2:
					redPot = RedSpot.BUSINESS_INTEGRAL;
					break;
				case 3:
					redPot = RedSpot.SHOPING_INTEGRAL;
					break;
				default:
					break;
				}
				
				//积分变动消息推送
				String description =  Configuration.getProperty("push.auto.update.description", null);
				String title =  Configuration.getProperty("push.auto.update.title", null);
				Message message = new Message();
				message.setIsPush(true);
				message.setIsSMS(false);
				message.setIsYellow(false);
				message.setTitle(title);
				message.setPushInfo(String.format(description,account,newIntegral,changeIntegral));
				logger.info("新增消息通知：userId:{},message:{}",userCard.getUserId(),JSON.toJSONString(message));
				pushMessageService.pushMessage(redPot, userCard.getUserId(), message);
			} catch (Exception e) {
				logger.error("积分变动推送消息异常：{}",e);
			}
		}
		
		
	}

	/**
	 * get userCard's names
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getUserCardsNames 
	 * @param newCardList
	 * @return
	 * @date 2015年12月19日 下午2:30:21  
	 * @author ws
	 */
	private StringBuffer getUserCardsNames(List<UserCard> newCardList) {
		Set<Long> cardTypeSet = new HashSet<Long>();
		StringBuffer cardTypeStr = new StringBuffer("");
		for (UserCard userCard : newCardList) {
			Long carType = userCard.getCardTypeId();
			if(cardTypeSet.contains(carType)){
				continue;
			}else{
				cardTypeSet.add(carType);
				CardType cardType = cardTypeService.selectByPrimaryKey(carType);
				cardTypeStr.append("、").append(cardType.getName());
			}
		}
		return cardTypeStr;
	}
}
