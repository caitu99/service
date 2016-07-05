/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.domain.ExchangeRule;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.ExchangeRuleService;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.VersionUtil;
import com.caitu99.service.utils.XStringUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MailJobService 
 * @author ws
 * @date 2015年12月10日 下午5:44:01 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class PresentTubiService{
	
	private final static Logger logger = LoggerFactory.getLogger(PresentTubiService.class);


	@Autowired
	protected ExchangeRuleService exchangeRuleService;
	
	@Autowired
	protected TransactionRecordService transactionRecordService;
	
	@Autowired
	protected AccountDetailService accountDetailService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private CardTypeService cardTypeService;
	@Autowired
	public PushMessageService pushMessageService;
	
	@Autowired
	private AppConfig appConfig;
	/**
	 * 
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: presentTubiDo 
	 * @param userid
	 * @param manualId
	 * @param oldIntegral
	 * @param newIntegral
	 * @return	addIntegral	addTubi	
	 * @date 2016年5月13日 下午2:43:07  
	 * @author ws
	 * @param version 
	 */
	public Map<String,Object> presentTubiDo(Long userId, Long cardTypeId, Integer oldIntegral, Integer newIntegral, Map<String,Object> resData, String version){

		Long versionL = VersionUtil.getVersionLong(version);
        Long version3 = 3000000L;
        if(version3.compareTo(versionL) > 0){
			return null;
		}
		
		Integer addIntegral = newIntegral > oldIntegral ? newIntegral - oldIntegral : 0;
		
		CardType cardType = cardTypeService.selectByPrimaryKey(cardTypeId);
		ExchangeRule exRule = exchangeRuleService.findByCardTypeName(cardType.getName());

		Map<String,Integer> addInfo = new HashMap<String, Integer>();
		addInfo.put("addIntegral", addIntegral);
		
		if(!appConfig.tubiCardTypeIds.contains(","+cardTypeId+",")){//不支持双倍积分
			//计算财币  向上取整
			Double addTubi = Math.ceil(addIntegral * (null == exRule.getScale() ? 1 : exRule.getScale()));
			addInfo.put("addTubi", addTubi.intValue());
			
			if(0 != addIntegral){
				TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
				transactionRecordDto.setOrderNo("");
				transactionRecordDto.setChannel(4);//赠送
				transactionRecordDto.setComment("双倍积分赠送");
				transactionRecordDto.setInfo("活动");//
				transactionRecordDto.setSource(2);//活动
				transactionRecordDto.setTotal(0L);
				transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNoWithRandom(
						"HD", String.valueOf(userId)));
				transactionRecordDto.setTubi(addTubi.longValue());
				transactionRecordDto.setType(5);//累积
				transactionRecordDto.setUserId(userId);
				// 添加交易记录
				Long recordId = transactionRecordService.saveTransaction(transactionRecordDto);
				// 添加交易明细
				accountDetailService.saveAccountDetailTubi(recordId, transactionRecordDto,
						3);//3  入币
				
				Account account = accountService.selectByUserId(userId);
				// 更新账户
				accountService.updateAccount(account, transactionRecordDto, 1L);
				

				this.pushMessageApp(userId, String.valueOf(addTubi.intValue()));
			}
		}

		resData.put(cardTypeId.toString(), addInfo);
		
		
		return resData;
	}
	
	/**
	 * 新积分赠送途币消息推送
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: pushMessageApp 
	 * @param userId
	 * @param cardTypeName
	 * @param addIntegral
	 * @param addTubi
	 * @date 2016年5月19日 上午9:13:09  
	 * @author ws
	 */
	public void pushMessageApp(Long userId,String addTubi){
		try {
			String description = Configuration.getProperty(
					"push.add.tubi.app.description", null);
			String title = Configuration.getProperty(
					"push.add.tubi.app.title", null);
			RedSpot redPot = RedSpot.MESSAGE_CENTER;

			
			Message message = new Message();
			message.setIsPush(true);
			message.setIsSMS(false);
			message.setIsYellow(false);
			message.setTitle(title);
			message.setPushInfo(String.format(description, addTubi));
			logger.info("新增消息通知：userId:{},message:{}", userId,
					JSON.toJSONString(message));
			pushMessageService.pushMessage(redPot, userId,
					message);
		} catch (Exception e) {
			logger.error("积分变动推送消息异常：{}", e);
		}
	}
	
	
}
