/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mail.controller.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.mail.controller.util.MailDateUtil;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.user.domain.UserCard;
import com.caitu99.service.user.service.UserCardService;
import com.caitu99.service.utils.date.TimeUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MailJobRegisterService 
 * @author ws
 * @date 2015年12月16日 下午8:13:24 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class MailJobRegisterService {
	
	private static final Logger logger = LoggerFactory
			.getLogger(MailJobRegisterService.class);
	
	@Autowired
	UserCardService userCardService;
	@Autowired
	CardTypeService cardTypeService;
	@Autowired
	AppConfig appConfig;
	@Autowired
	KafkaProducer kafkaProducer;
	
	private String MAIL_ERR_MSG = "【邮箱自动更新任务注册失败】：userId:{},account:{},errMsg:{}";
	private String MAIL_INFO_MSG = "【邮箱自动更新成功】：userId:{},account:{},mounth:{},infoMsg:{}";
	
	/*public void registerMailJobBatch(){
		
		List<UserCardTypeVo> users = new ArrayList<UserCardTypeVo>();
		users = userCardService.getAllUserDistinct();
		for (UserCardTypeVo userCardType : users) {
			List<UserCard> userCardList = userCardService.selectUserCardForJob(userCardType.getUserId(),userCardType.getCardTypeId());
			registerJob(userCardList,false);
		}
	}*/
	
	
	/**
	 * 注册定时任务
	 * 对于账单日后3天为更新成功的，注册至后6天更新
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: registerJob 
	 * @param userCard
	 * @date 2015年12月16日 上午11:38:56  
	 * @author ws
	 */
	public void registerJob(List<UserCard> userCardList) {
		Set<Integer> billDays = new HashSet<Integer>();
		List<UserCard> messageUserCard = new ArrayList<UserCard>();
		for (UserCard userCard : userCardList) {
			Long userId = userCard.getUserId();
			Long cardTypeId = userCard.getCardTypeId();
			Date billMounthDate = userCard.getBillMonth();
			Integer billDay = userCard.getBillDay();
			if(null == billDay){
				billDay = 15;//没有账单日，则默认15号
			}
			if(billDays.contains(billDay)){//同一账单日，生成一个定时任务
				continue;
			}else{
				billDays.add(billDay);
			}
			
			Calendar nowCal = Calendar.getInstance();
			int thisMounth = nowCal.get(Calendar.MONTH) + 1;
			Integer thisDay = nowCal.get(Calendar.DAY_OF_MONTH);
			nowCal.setTime(billMounthDate);
			int billMounth = nowCal.get(Calendar.MONTH) + 1;
			
			if(thisMounth == billMounth){
				//本月账单已更新,注册下个月账单日后3天
				logger.info(MAIL_INFO_MSG,userId,cardTypeId,billMounth,"本月账单已更新");
			}else{
				int dateSub = thisDay.intValue() - billDay;
				if(dateSub == 3){
					//注册账单日后6天
					Date jobTime = MailDateUtil.getDateFromBillDay(billMounthDate,billDay.intValue()+6);
					String message = "";
					Map<String,Object> jsonParam = new HashMap<String,Object>();
					jsonParam.put("userId", userCard.getUserId());
					jsonParam.put("cardTypeId", userCard.getCardTypeId());
					jsonParam.put("dateLong", TimeUtil.getIntegerDate(jobTime));
					jsonParam.put("jobType", "MAIL_UPDATE_JOB");
					message = JSON.toJSONString(jsonParam);
					//加入账单日后6天自动更新计划中
					kafkaProducer.sendMessage(message , appConfig.jobTopic);
					logger.info("新增邮箱自动更新计划：{}",message);
				}else if(dateSub > 3){
					//通知用户本月账单未获取
					messageUserCard.add(userCard);
				}
			}
		}
		//本月后6天未更新成功合并提示
		if(!CollectionUtils.isEmpty(messageUserCard)){//不为空   提示发现新账户
			Set<Long> cardTypeSet = new HashSet<Long>();
			StringBuffer cardTypeStr = new StringBuffer("");
			Long userId = null;
			for (UserCard userCard : messageUserCard) {
				Long carType = userCard.getCardTypeId();
				if(cardTypeSet.contains(carType)){
					continue;
				}else{
					userId = userCard.getUserId();
					CardType cardType = cardTypeService.selectByPrimaryKey(carType);
					cardTypeStr.append("、").append(cardType.getName());
				}
			}
			//TODO 此处需将提示内容加入到配置中
			//统一推
			String title = "本月账单未更新通知";
			String description = "您的"+cardTypeStr.toString().replaceFirst("、", "")+"本月账单未更新";
			String payload = description;
			kafkaProducer.sendMessage(title, description, payload, userId);
		}
	}
	
}
