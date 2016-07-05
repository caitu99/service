/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.realization.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.realization.dao.PhoneAmountMapper;
import com.caitu99.service.realization.dao.PhoneRealizeDetailMapper;
import com.caitu99.service.realization.domain.PhoneAmount;
import com.caitu99.service.realization.domain.PhoneRealizeDetail;
import com.caitu99.service.realization.dto.PhoneDetailDto;
import com.caitu99.service.realization.service.PhoneRealizeDetailService;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.user.domain.UserBankCard;
import com.caitu99.service.user.service.UserBankCardService;
import com.caitu99.service.utils.calculate.CalculateUtils;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: PhoneRealizeDetailServiceImpl 
 * @author ws
 * @date 2016年4月13日 下午5:51:41 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class PhoneRealizeDetailServiceImpl implements PhoneRealizeDetailService {

	@Autowired
	PhoneRealizeDetailMapper phoneRealizeDetailMapper;
	
	@Autowired
	private PhoneAmountMapper phoneAmountDao;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private UserBankCardService userBankCardService;
	
	@Autowired
	private AppConfig appConfig;
	
	private final static Logger log = LoggerFactory
			.getLogger(OrderService.class);
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.realization.service.PhoneRealizeDetailService#selectBy(java.lang.Long, java.lang.Long)
	 */
	@Override
	public PhoneRealizeDetail selectBy(Long platformId, Long amountId) {

		Map<String,Long> queryMap = new HashMap<String, Long>();
		queryMap.put("amountId", amountId);
		queryMap.put("platformId", platformId);
		return phoneRealizeDetailMapper.selectBy(queryMap);
	}

	@Override
	public Map<String,String> queryAccountByPrice(Long userid, Long amountId) {
		Map<String,String> map = new HashMap<String, String>();
		try {
			PhoneDetailDto pdd = new PhoneDetailDto();
			
			PhoneAmount pa = phoneAmountDao.selectByPrimaryKey(amountId);
			Long total = CalculateUtils.multiply(pa.getAmount(), 100);
			Long tubi = pa.getDiscount();
			Account account = accountService.selectByUserId(userid);
			Long userTubi = account.getTubi();
			if(null == userTubi){
				userTubi = 0L; 
			}
			Long userCaibi = account.getTotalIntegral();
			if(null == userCaibi){
				userCaibi = 0L;
			}
			if(userTubi < tubi){
				tubi = userTubi;
			}
			
			Long caibi = CalculateUtils.getDifference(total, tubi);
			
			if(userCaibi < caibi){
				caibi = userCaibi;
			}
			
			Long rmb = total - tubi - caibi;
			
			pdd.setTubi(tubi);
			pdd.setRmb(rmb);
			pdd.setCaibi(caibi);
			pdd.setName(pa.getName());
			pdd.setAmountId(amountId);
			
			String phoneDetail = JSON.toJSONString(pdd);
			
			//查询表中是否有这个用户的数据
			List<UserBankCard> list = userBankCardService.selectByUserId(userid,1);
			
			for(UserBankCard ubc : list){
				if(StringUtils.isNotBlank(ubc.getPicUrl())){
					ubc.setPicUrl(appConfig.staticUrl + ubc.getPicUrl());
				}
			}
			
			String userBankCard = JSON.toJSONString(list);
			
			map.put("phoneDetail", phoneDetail);
			map.put("userBankCard", userBankCard);
			
		} catch (Exception e) {
			log.debug("method queryAccountByPrice is wrong!" + e.getMessage());
			map.put("phoneDetail", "");
			map.put("phoneDetail", "");
		}
		
		return map;
	}

}
