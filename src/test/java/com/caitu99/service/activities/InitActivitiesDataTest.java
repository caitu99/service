/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.activities.dao.ActivitiesItemMapper;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.goods.dao.ItemMapper;
import com.caitu99.service.goods.dao.SkuMapper;
import com.caitu99.service.goods.dao.StockMapper;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.domain.Stock;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: InitActivitiesDataTest 
 * @author fangjunxiao
 * @date 2015年12月5日 上午10:32:16 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class InitActivitiesDataTest extends AbstractJunit{
	
	
	@Autowired
	private SkuMapper skuDao;
	
	@Autowired
	private StockMapper stockDao;
	
	@Autowired
	private ActivitiesItemMapper activitiesItemDao;
	
	@Autowired
	private ItemMapper itemDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AccountService accountService;
	

	public void initUserA(){
		String phoneNo = "13588313856";
		
		User user = new User();
		user.setMobile(phoneNo);
		user.setGmtCreate(new Date());
		user.setLoginCount(0);//0次登录
		user.setType(1);//普通用户
		User findUser = userService.isExistMobile(user);
		if (null == findUser) {// 如果用户不存在则创建用户
			user.setStatus(1);
			userService.regist(user);
			
			accountService.addNewAccount(user.getId());
			
			if (null == user.getId()) {
				throw new ApiException(2016,"注册失败");
			} else {
				return ;
			}
		}else{
			user = userService.getById(findUser.getId());
			return ;
		}
	}
	
	@Test
	public void initA (){
		
		/**
		 * 10173   云南（港澳）双人游
		 * 10058  流量
		 * 10001  凹凸租车
		 * 10172 也买酒
		 */
		
		Long imeiId = 10035L;
		Integer probability = 11;
		Integer type = 1;
		
		Date date = new Date();
		Item item = itemDao.selectByPrimaryKey(imeiId);
		
/*		
		Sku sku = new Sku();
		sku.setItemId(imeiId);
		sku.setSalePrice(1500L);
		sku.setVersion("0");
		sku.setCreateTime(date);
		sku.setUpdateTime(date);
		skuDao.insert(sku);
		
		ActivitiesItem activitiesItem = new ActivitiesItem();
		activitiesItem.setActivitiesId(1L);
		activitiesItem.setCreateTime(date);
		activitiesItem.setDetails("");
		activitiesItem.setIntegral(0L);
		
		activitiesItem.setItemId(imeiId);
		activitiesItem.setLev(1);
		activitiesItem.setName(item.getTitle());
		activitiesItem.setProbability(probability);
		activitiesItem.setQuantity(10);
		activitiesItem.setSkuId(sku.getItemId());
		activitiesItem.setStatus(1);
		activitiesItem.setType(type);
		activitiesItem.setUpdateTime(date);
		
		activitiesItemDao.insertSelective(activitiesItem);*/
		
		
		Stock stock = new Stock();
		for (int i = 0; i < 30; i++) {
			stock.setCode("FDSAFD");
			stock.setCreateTime(date);
			stock.setEffectiveTime(date);
			stock.setItemId(imeiId);
			stock.setSaleTime(date);
			stock.setSkuId(10035L);
			stock.setStatus(0);
			stock.setVersion("0");
			stockDao.insert(stock);
		}

	}

}
