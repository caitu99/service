/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.activities.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.RedisKey;
import com.caitu99.service.activities.controller.vo.ActivitiesVo;
import com.caitu99.service.activities.controller.vo.WinningVo;
import com.caitu99.service.activities.dao.ActivitiesItemMapper;
import com.caitu99.service.activities.dao.ActivitiesMapper;
import com.caitu99.service.activities.dao.InRecordMapper;
import com.caitu99.service.activities.domain.Activities;
import com.caitu99.service.activities.domain.ActivitiesItem;
import com.caitu99.service.activities.domain.InRecord;
import com.caitu99.service.activities.service.ActivitiesService;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.exception.ActivitiesException;
import com.caitu99.service.goods.dao.ItemMapper;
import com.caitu99.service.goods.dao.ReceiveStockMapper;
import com.caitu99.service.goods.dao.SkuMapper;
import com.caitu99.service.goods.dao.StockMapper;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.domain.ReceiveStock;
import com.caitu99.service.goods.domain.Sku;
import com.caitu99.service.goods.domain.Stock;
import com.caitu99.service.transaction.controller.vo.AccountResult;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.user.dao.UserMapper;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.date.DateUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ActivitiesService 
 * @author fangjunxiao
 * @date 2015年12月1日 下午6:10:04 
 * @Copyright (c) 2015-2020 by caitu99 
 */

@Service
public class ActivitiesServiceImpl implements ActivitiesService{

	private final static Logger logger = LoggerFactory
			.getLogger(OrderService.class);
	
	@Autowired
	private AppConfig appconfig;
	
	@Autowired
	private RedisOperate redis;
	
	@Autowired
	private ActivitiesMapper activitiesDao;
	
	@Autowired
	private InRecordMapper inRecordDao;
	
	@Autowired
	private ActivitiesItemMapper activitiesItemDao;
	
	@Autowired
	private StockMapper stockDao;
	
	@Autowired
	private ItemMapper itemDao;
	
	@Autowired
	private SkuMapper skuDao;
	
	@Autowired
	private ReceiveStockMapper receiveStockDao;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private UserMapper userDao;
	
	
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "ActivitiesException" })
	public String getProof(Long userId, Long activitiesId, Integer source) throws ActivitiesException {
		try {
			ActivitiesVo aVo = new ActivitiesVo();
			aVo.setUserId(userId);
			aVo.setActivitiesId(activitiesId);
			aVo.setSource(source);
			aVo.setAward(false);
			aVo.setNowTime(new Date());
			ApiResult<WinningVo> relust = new ApiResult<WinningVo>();
			WinningVo winningVo = new WinningVo();
			//活动入缓存
			Activities activities = this.getRedisActivities(aVo.getActivitiesId());
			//判断用户抽奖次数  
			Integer myTimes = this.getUserTimes(aVo.getUserId(),aVo.getActivitiesId());
			
			if(myTimes.intValue() >= activities.getTimes().intValue()){ 
				// 已使用完免费抽奖次数
				AccountResult ar = this.payIntegral(aVo);
				if(3101 != ar.getCode()){
					return relust.toJSONString(ApiResultCode.ACTIVITIES_DEDUCT_INTEGRAL_ERROR,"财币扣取失败",winningVo); 
				}
			}
			
			//抽奖算法  
			ActivitiesItem activitiesItem = this.callbackGetActivity(aVo.getActivitiesId());
			
			if(null == activitiesItem){
				//没有中奖
				this.generateInRecord(aVo);
				winningVo.setInRecordId(aVo.getInRecordId());
				winningVo.setName(appconfig.activitiesWinningTitle);
				winningVo.setType(0);
				return relust.toJSONString(0, "success",winningVo);
			}
			
			//生成中奖记录
			this.generateInRecord(aVo, activitiesItem);
			
			winningVo.setInRecordId(aVo.getInRecordId());
			winningVo.setName(activitiesItem.getName());
			winningVo.setType(activitiesItem.getType());
			
			if(ActivitiesItem.ACTIVITIES_ITEM_TYPE_ROLL.equals(activitiesItem.getType())){
				
				//获取奖品信息
				Item item = itemDao.selectByPrimaryKey(activitiesItem.getItemId());
				Sku sku = skuDao.selectByPrimaryKey(activitiesItem.getSkuId());
				aVo.setSubTitle(item.getSubTitle());
				aVo.setMarketPrice(item.getMarketPrice());
				aVo.setSalePrice(sku.getSalePrice());
				
				winningVo.setSubTitle(aVo.getSubTitle());
				winningVo.setMarketPrice(aVo.getMarketPrice());
				
				//查库存
				if(ActivitiesItem.ACTIVITIES_ITEM_TYPE_ROLL.equals(activitiesItem.getType())){
					List<Stock> stockList = this.findUnloadInventory(activitiesItem);
					if(null == stockList || 0 == stockList.size()){
						logger.info("活动抽奖库存不足:itemId=" + activitiesItem.getItemId() + ",skuId=" + activitiesItem.getSkuId());
						return relust.toJSONString(ApiResultCode.ACTIVITIES_NO_STOCK_ERROR,"系统繁忙",winningVo); 
					}
					aVo.setStockId(stockList.get(0).getStockId());
				}
				
				//扣库存
				aVo.setReceiveStockStatus(ReceiveStock.PRERECEIVE);
				this.cuttingStock(aVo,activitiesItem);
			}
			return relust.toJSONString(0, "success",winningVo);
		} catch (Exception e) {
			logger.error("抽奖失败:" + e.getMessage());
			throw new ActivitiesException(ApiResultCode.ACTIVITIES_GETPROOF_ERROR,e.getMessage());
		}
	}
	
	private ActivitiesItem callbackGetActivity(Long activityId){
		boolean flag = true;
		ActivitiesItem activitiesItem = null;
		while (flag) {
			 activitiesItem = this.luckyTwo(activityId);
			if(null == activitiesItem){
				flag = false;
				break;
			}
			//查库存
			if(ActivitiesItem.ACTIVITIES_ITEM_TYPE_ROLL.equals(activitiesItem.getType())){
				List<Stock> stockList = this.findUnloadInventory(activitiesItem);
				if(null == stockList || 0 == stockList.size()){
					logger.info("活动抽奖库存不足:itemId=" + activitiesItem.getItemId() + ",skuId=" + activitiesItem.getSkuId());
					//继续抽
					continue;
				}
			}
			flag = false;
			break;
		}
		return activitiesItem;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "ActivitiesException" })
	public String getUnautherizedProof( Long activitiesId, Integer source)
			throws ActivitiesException {
		try {
			ApiResult<WinningVo> relust = new ApiResult<WinningVo>();
			WinningVo winningVo = new WinningVo();
			
			//抽奖算法   100%中奖
			//ActivitiesItem activitiesItem = this.luckyTwo(activitiesId);
			ActivitiesItem activitiesItem = this.callbackGetActivity(activitiesId);
			if(null == activitiesItem){
				winningVo.setName(appconfig.activitiesWinningTitle);
				winningVo.setType(0);
				return relust.toJSONString(0, "success",winningVo);
			}
			
			winningVo.setActivitiesItemId(activitiesItem.getId());
			winningVo.setType(activitiesItem.getType());
			winningVo.setName(activitiesItem.getName());
			winningVo.setSource(source);
	
			return relust.toJSONString(0, "success",winningVo);
		} catch (Exception e) {
			logger.error("抽奖失败:" + e.getMessage());
			throw new ActivitiesException(ApiResultCode.ACTIVITIES_GETPROOF_ERROR,e.getMessage());
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "ActivitiesException" })
	public String saveWinning(Long userId,Long activitiesId, Integer source,Long activitiesItemId) throws ActivitiesException {
		try {
			ActivitiesVo aVo = new ActivitiesVo();
			aVo.setUserId(userId);
			aVo.setActivitiesId(activitiesId);
			aVo.setSource(source);
			aVo.setNowTime(new Date());
			aVo.setAward(true);
			
			ApiResult<WinningVo> relust = new ApiResult<WinningVo>();
			WinningVo winningVo = new WinningVo();
			
			ActivitiesItem activitiesItem = activitiesItemDao.selectByPrimaryKey(activitiesItemId);

			//活动入缓存
			Activities activities = this.getRedisActivities(aVo.getActivitiesId());
			
			//判断用户抽奖次数  
			Integer myTimes = this.getUserTimes(aVo.getUserId(),aVo.getActivitiesId());
			
			if(myTimes.intValue() >= activities.getTimes().intValue()){ 
				// 已使用完免费抽奖次数
				AccountResult ar = this.payIntegral(aVo);
				if(3101 != ar.getCode()){
					return relust.toJSONString(ApiResultCode.ACTIVITIES_DEDUCT_INTEGRAL_ERROR,"系统繁忙",winningVo); 
				}
			}
			
			//生成中奖记录
			this.generateInRecord(aVo, activitiesItem );
			
			winningVo.setName(activitiesItem.getName());
			winningVo.setType(activitiesItem.getType());
			
			if(ActivitiesItem.ACTIVITIES_ITEM_TYPE_ROLL.equals(activitiesItem.getType())){
				
				//获取商品信息
				Item item = itemDao.selectByPrimaryKey(activitiesItem.getItemId());
				Sku sku = skuDao.selectByPrimaryKey(activitiesItem.getSkuId());
				aVo.setSubTitle(item.getSubTitle());
				aVo.setMarketPrice(item.getMarketPrice());
				aVo.setSalePrice(sku.getSalePrice());
				
				//查库存
				List<Stock> stockList = this.findUnloadInventory(activitiesItem);
				if(null == stockList || 0 == stockList.size()){
					logger.error("活动抽奖库存不足:itemId=" + activitiesItem.getItemId() + ",skuId=" + activitiesItem.getSkuId());
					return relust.toJSONString(ApiResultCode.ACTIVITIES_NO_STOCK_ERROR,"库存不足",winningVo); 
				}
				aVo.setStockId(stockList.get(0).getStockId());
				//扣库存
				aVo.setReceiveStockStatus(ReceiveStock.RECEIVE);
				this.cuttingStock(aVo,activitiesItem);
				
				return relust.toJSONString(0, "success",winningVo);
			}
			
			//财币入账
			AccountResult ar = this.addIntegral(userId,activitiesItem.getIntegral());
			if(3101 != ar.getCode()){
				return relust.toJSONString(ApiResultCode.ACTIVITIES_GET_INTEGRAL_ERROR, "财币领取失败");
			}
			
			return relust.toJSONString(0, "success",winningVo);
		} catch (Exception e) {
			logger.error("抽奖失败:" + e.getMessage());
			throw new ActivitiesException(ApiResultCode.ACTIVITIES_GETPROOF_ERROR,e.getMessage());
		}
	}
	
	private void cuttingStock(ActivitiesVo aVo,ActivitiesItem activitiesItem){

		//扣库存
		Stock editStock = new Stock();
		editStock.setStockId(aVo.getStockId());
		editStock.setStatus(Stock.LUCKYDRAW);//3抽奖
		stockDao.update(editStock);
		
		ReceiveStock receiveStock = new ReceiveStock();
		receiveStock.setUserId(aVo.getUserId());
		receiveStock.setStockId(aVo.getStockId());
		receiveStock.setRemoteType(ReceiveStock.LUCKYDRAW);//3抽奖
		//活动ID
		receiveStock.setRemoteId(aVo.getInRecordId().toString());//中奖记录表ID
		receiveStock.setName(activitiesItem.getName());
		receiveStock.setCreateTime(aVo.getNowTime());
		receiveStock.setUpdateTime(aVo.getNowTime());
		receiveStock.setSalePrice(aVo.getSalePrice());
		receiveStock.setMarketPrice(aVo.getMarketPrice());
		receiveStock.setStatus(aVo.getReceiveStockStatus());
		receiveStock.setReceiveTime(aVo.getNowTime());
		
		receiveStockDao.insertSelective(receiveStock);
	}
	
	
	private AccountResult payIntegral(ActivitiesVo aVo){
		TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
		transactionRecordDto.setChannel(3);
		transactionRecordDto.setComment("");
		transactionRecordDto.setInfo("");
		transactionRecordDto.setOrderNo("");
		transactionRecordDto.setPicUrl("");
		transactionRecordDto.setTotal(appconfig.activitiesItemIntegral);
		transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNo(
				"SP", String.valueOf(aVo.getUserId())));
		transactionRecordDto.setType(1);
		transactionRecordDto.setUserId(aVo.getUserId());
		transactionRecordDto.setSource(2);//来源于活动
		return accountService.pay(transactionRecordDto);
		
	}
	
	
	public AccountResult addIntegral(Long userId,Long inegral){
		TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
		transactionRecordDto.setChannel(1);
		transactionRecordDto.setComment("活动送财币");
		transactionRecordDto.setInfo("活动");
		transactionRecordDto.setOrderNo("");
		transactionRecordDto.setPicUrl("");
		transactionRecordDto.setTotal(inegral);
		transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNo(
				"B", String.valueOf(userId)));
		transactionRecordDto.setType(5);
		transactionRecordDto.setUserId(userId);
		transactionRecordDto.setSource(2);//来源于活动
		return accountService.add(transactionRecordDto);
		
	}
	
	
	private void generateInRecord(ActivitiesVo aVo, ActivitiesItem activitiesItem){
		InRecord inRecord = new InRecord();
		inRecord.setUserId(aVo.getUserId());
		inRecord.setActivitiesId(aVo.getActivitiesId());
		inRecord.setActivitiesItemId(activitiesItem.getId());
		inRecord.setAward(aVo.getAward());
		inRecord.setItemName(activitiesItem.getName());
		inRecord.setNum(1);
		inRecord.setMarketPrice(aVo.getMarketPrice());
		inRecord.setSubTitle(aVo.getSubTitle());
		inRecord.setSource(aVo.getSource());
		inRecord.setWinning(true);
		inRecord.setCreateTime(aVo.getNowTime());
		inRecord.setUpdateTime(aVo.getNowTime());
		inRecordDao.insertSelective(inRecord);
		aVo.setInRecordId(inRecord.getId());
	}
	
	
	private void generateInRecord(ActivitiesVo aVo){
		InRecord inRecord = new InRecord();
		inRecord.setUserId(aVo.getUserId());
		inRecord.setActivitiesId(aVo.getActivitiesId());
		inRecord.setAward(false);
		inRecord.setSource(aVo.getSource());
		inRecord.setWinning(false);
		inRecord.setCreateTime(aVo.getNowTime());
		inRecord.setUpdateTime(aVo.getNowTime());
		inRecordDao.insertSelective(inRecord);
		aVo.setInRecordId(inRecord.getId());
	}
	
	/**抽奖算法   100%中奖*/
	private ActivitiesItem lucky(Long activitiesId){
		List<ActivitiesItem> aiList = activitiesItemDao.findAllByActivitiesId(activitiesId);
		Random random = new Random();
		int abc = random.nextInt(100)+1;
		Integer next = 0;
		Integer last = 0;
		for (ActivitiesItem aid : aiList) {
			last = next;
			next += aid.getProbability();
			if(abc>last && abc<=next){
				//System.out.println(aid.getName() +"===" +aid.getProbability() +"%==="+last+"-"+ next +"==="+abc);
			return aid;
			}
		}
		return aiList.get(0);
	} 
	
	/**中奖方案2：中奖率千分制,会有没有中奖可能*/
	private ActivitiesItem luckyTwo(Long activitiesId){
		List<ActivitiesItem> aiList = activitiesItemDao.findAllByActivitiesId(activitiesId);
		Random random = new Random();
		int abc = random.nextInt(1000)+1;
		Integer next = 0;
		Integer last = 0;
		for (ActivitiesItem aid : aiList) {
			last = next;
			next += aid.getProbability();
			if(abc>last && abc<=next){
				//System.out.println(aid.getName() +"===" +aid.getProbability() +"%==="+last+"-"+ next +"==="+abc);
				return aid;
			}
		}
		return null;
	}
	
	
	
	private List<Stock> findUnloadInventory(ActivitiesItem activitiesItem){
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("itemId", activitiesItem.getItemId());
		map.put("skuId", activitiesItem.getSkuId());
		return stockDao.findUnloadInventory(map);
	}


	@Override
	public String checkUserTimes(Long userId, Long activitiesId)
			throws ActivitiesException {
		try {
			ApiResult<Map<String,Object>> relust = new ApiResult<Map<String,Object>>();
			//活动入缓存
			Activities activities = this.getRedisActivities(activitiesId);
			Integer myTimes = this.getUserTimes(userId,activitiesId);
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("myTimes", myTimes);
			map.put("flag", true);
			if(myTimes.intValue() > activities.getTimes().intValue()){  //mod by chencheng < 改为   >
				// "已使用完免费抽奖次数";
				map.put("flag", false);
			}
			return relust.toJSONString(0,"scussess", map);
		} catch (Exception e) {
			logger.error("查询用户抽奖次数失败:" + e.getMessage());
			throw new ActivitiesException(ApiResultCode.ACTIVITIES_TIMES_ERROR, "查询用户抽奖次数失败");
		}
	}
	
	private Integer getUserTimes(Long userId,Long activitiesId){
		Date endTime = Calendar.getInstance().getTime();
		Date startTime = DateUtil.getZeroPoint(endTime);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("endTime", endTime);
		map.put("startTime", startTime);
		map.put("userId", userId);
		map.put("activitiesId", activitiesId);
		return inRecordDao.getCountUserInRecord(map);
	}
	
	private Activities getRedisActivities(Long activitiesId){
		String key = String.format(RedisKey.ACTIVITIES_CARDS_ID, activitiesId);
		String content = redis.getStringByKey(key);
		if(StringUtils.isNotBlank(content)){
			return JSON.parseObject(content,Activities.class);
		}
		Activities activities =  activitiesDao.selectByPrimaryKey(activitiesId);
		if(null != activities){
			Date date = new Date();
			Long seconds = activities.getEndTime().getTime() - date.getTime();
			redis.set(key,JSON.toJSONString(activities));
			redis.setExpire(key, seconds.intValue());
		}
		return activities;
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "ActivitiesException" })
	public String awardItm(Long inRecordId, Long userId)
			throws ActivitiesException {
		try {
			ApiResult<WinningVo> relust = new ApiResult<WinningVo>();
			WinningVo winningVo = new WinningVo();
			Date date = new Date();
			InRecord inRecord = inRecordDao.selectByPrimaryKey(inRecordId);
			if(true == inRecord.getAward()){
				return relust.toJSONString(-1, "已领奖，请不要重复领奖");
			}
			
			InRecord editInRecord = new InRecord();
			editInRecord.setId(inRecordId);
			editInRecord.setAward(true);
			editInRecord.setUpdateTime(date);
			//editInRecord.setPhone(phone);
			//editInRecord.setName(name);
			inRecordDao.updateByPrimaryKeySelective(editInRecord);
			
			ActivitiesItem  ai = activitiesItemDao.selectByPrimaryKey(inRecord.getActivitiesItemId());
		    winningVo.setType(ai.getType());
		    winningVo.setName(ai.getName());
			if(ActivitiesItem.ACTIVITIES_ITEM_TYPE_ROLL.equals(ai.getType())){
				ReceiveStock queryrecord = new ReceiveStock();
				queryrecord.setUserId(userId);
				queryrecord.setRemoteId(inRecord.getId().toString());
			    ReceiveStock record = receiveStockDao.getReceiveStockByRecord(queryrecord);
				
			    record.setStatus(ReceiveStock.RECEIVE);
			    record.setReceiveTime(date);
			    receiveStockDao.updateByPrimaryKeySelective(record);
			
			    return relust.toJSONString(0, "success",winningVo);
			}
			
			AccountResult ar = this.addIntegral(userId,ai.getIntegral());
			if(3101 != ar.getCode()){
				return relust.toJSONString(ApiResultCode.ACTIVITIES_GET_INTEGRAL_ERROR, "财币领取失败");
			}
			
		
			return relust.toJSONString(0, "success",winningVo);
		} catch (Exception e) {
			logger.error("领奖失败:" + e.getMessage());
			throw new ActivitiesException(ApiResultCode.ACTIVITIES_WINNING_ERROR,"领奖失败",e.getMessage());
		}
	}


	@Override
	public String checkUserIntegral(Long userId) throws ActivitiesException {
		try {
			ApiResult<Boolean> relust = new ApiResult<Boolean>();
			Long activitiesId = appconfig.activitiesId;
			Activities activities = this.getRedisActivities(activitiesId);
			Integer myTimes = this.getUserTimes(userId,activitiesId);
			Boolean tag = true;
			if(myTimes.intValue() > activities.getTimes().intValue()){  //mod by chencheng < 改为   >
				// "已使用完免费抽奖次数";
			   tag = accountService.isEnough(userId, appconfig.activitiesItemIntegral);
			}
			return relust.toJSONString(0, "success",tag);
		} catch (Exception e) {
			logger.error("验证用户财币失败：" + e.getMessage());
			throw new ActivitiesException(ApiResultCode.ACTIVITIES_CHECK_INTEGRAL_ERROR,"验证用户财币失败", e.getMessage());
		}
	}


	@Override
	public String giveupItem(Long inRecordId, Long userId)
			throws ActivitiesException {
		try {
			ApiResult<WinningVo> relust = new ApiResult<WinningVo>();
			WinningVo winningVo = new WinningVo();
			InRecord inRecord = inRecordDao.selectByPrimaryKey(inRecordId);
			if(true == inRecord.getAward()){
				return relust.toJSONString(-1, "已领奖，请不要重复领奖");
			}
			ActivitiesItem  ai = activitiesItemDao.selectByPrimaryKey(inRecord.getActivitiesItemId());
		    winningVo.setType(ai.getType());
		    winningVo.setName(ai.getName());
			if(ActivitiesItem.ACTIVITIES_ITEM_TYPE_ROLL.equals(ai.getType())){
				ReceiveStock queryrecord = new ReceiveStock();
				queryrecord.setUserId(userId);
				queryrecord.setRemoteId(inRecord.getId().toString());
			    ReceiveStock record = receiveStockDao.getReceiveStockByRecord(queryrecord);
				
			    record.setStatus(ReceiveStock.GIVEBACK);
			    record.setUpdateTime(new Date());
			    receiveStockDao.updateByPrimaryKeySelective(record);
			    
			    Stock stock = new Stock();
			    stock.setSkuId(record.getStockId());
			    stock.setStatus(Stock.NOTONSALE);
			    stockDao.updateByPrimaryKeySelective(stock);
			    
			}
			
			AccountResult ar = this.addIntegral(userId,appconfig.activitiesAddIntegeral);
			if(3101 != ar.getCode()){
				return relust.toJSONString(ApiResultCode.ACTIVITIES_GET_INTEGRAL_ERROR, "财币领取失败");
			}
		
			return relust.toJSONString(0, "success",winningVo);
		} catch (Exception e) {
			logger.error("放弃领奖失败:" + e.getMessage());
			throw new ActivitiesException(ApiResultCode.ACTIVITIES_NOT_WINNING_ERROR,"放弃领奖失败",e.getMessage());
		}
	}



	@Override
	public String getInRecord(Long userId, Long inRecordId)
			throws ActivitiesException {
		try {
			ApiResult<WinningVo> relust = new ApiResult<WinningVo>();
			WinningVo winningVo = new WinningVo();
			
			InRecord inRecord = inRecordDao.selectByPrimaryKey(inRecordId);
			if(null == inRecord ){
				return  relust.toJSONString(-1, "找不到对象");
			}
			if(!userId.equals(inRecord.getUserId())){
				return relust.toJSONString(-1, "非法访问");
			}
			
			ActivitiesItem activitiesItem = activitiesItemDao.selectByPrimaryKey(inRecord.getActivitiesItemId());
			
			winningVo.setName(inRecord.getItemName());
			winningVo.setType(activitiesItem.getType());
			winningVo.setInRecordId(inRecord.getId());
			
			if(ActivitiesItem.ACTIVITIES_ITEM_TYPE_ROLL==activitiesItem.getType()){
				Item item = itemDao.selectByPrimaryKey(activitiesItem.getItemId());
				winningVo.setMarketPrice(item.getMarketPrice());
				winningVo.setSubTitle(item.getSubTitle());
			}
		
			
			return relust.toJSONString(0, "success", winningVo);
			
		} catch (Exception e) {
			logger.error("查询中奖信息失败:" + e.getMessage());
			throw new ActivitiesException(ApiResultCode.ACTIVITIES_GET_INRECORD_ERROR,"查询中奖信息失败",e.getMessage());
		}
	}


	@Override
	public String getActivitiesItem(Long activitiesItemId)
			throws ActivitiesException {
		try {
			ApiResult<WinningVo> relust = new ApiResult<WinningVo>();
			WinningVo winningVo = new WinningVo();
			ActivitiesItem activitiesItem = activitiesItemDao.selectByPrimaryKey(activitiesItemId);
			
			winningVo.setName(activitiesItem.getName());
			winningVo.setType(activitiesItem.getType());
			winningVo.setActivitiesItemId(activitiesItem.getId());
			
			if(ActivitiesItem.ACTIVITIES_ITEM_TYPE_ROLL==activitiesItem.getType()){
				Item item = itemDao.selectByPrimaryKey(activitiesItem.getItemId());
				winningVo.setMarketPrice(item.getMarketPrice());
			}
			
			return relust.toJSONString(0, "success", winningVo);
		} catch (Exception e) {
			logger.error("查询中奖信息失败:" + e.getMessage());
			throw new ActivitiesException(ApiResultCode.ACTIVITIES_GET_INRECORD_ERROR,"查询奖品信息失败",e.getMessage());
		}
	}

	@Override
	public Activities selectByPrimaryKey(Long id) {
		return activitiesDao.selectByPrimaryKey(id);
	}

}
