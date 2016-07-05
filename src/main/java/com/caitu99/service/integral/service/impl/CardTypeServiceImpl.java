package com.caitu99.service.integral.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.integral.dao.CardTypeMapper;
import com.caitu99.service.integral.domain.Bank;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.service.BankService;
import com.caitu99.service.integral.service.CardTypeService;

@Service
public class CardTypeServiceImpl implements CardTypeService {
	
	private static final Logger logger = LoggerFactory.getLogger(CardTypeServiceImpl.class);

	@Autowired
	private CardTypeMapper cardTypeMapper;
	
	@Autowired
	private RedisOperate redis;
	
	@Autowired
	private BankService bankService;
	
	@Autowired
	private AppConfig appConfig;

	/*
	 * 获取所有卡片
	 */
	@Override
	public List<CardType> listAll() {
		// TODO Auto-generated method stub
		return cardTypeMapper.listAll();
	}

	@Override
	public int add(CardType cardType) {
		// TODO Auto-generated method stub
		return cardTypeMapper.insert(cardType);
	}

	@Override
	public int modify(CardType cardType) {
		// TODO Auto-generated method stub
		return cardTypeMapper.updateByPrimaryKey(cardType);
	}

	public CardType selectByName(String name) {
		return cardTypeMapper.selectByName(name);
	}

	@Override
	public List<CardType> selectAll() {
		// TODO Auto-generated method stub
		return cardTypeMapper.selectAll();
	}

	@Override
	public CardType selectByPrimaryKey(Long id) {
		String key = String.format(RedisKey.CARD_TYPE_BY_ID, id);
		String content = redis.getStringByKey(key);
		if(StringUtils.isNotBlank(content)){
			return JSONObject.parseObject(content, CardType.class);
		}
		CardType cardType = cardTypeMapper.selectByPrimaryKey(id);
		if(cardType != null){
			redis.set(key, JSON.toJSONString(cardType));
		}
		
		return cardType;
	}

	@Override
	public CardType selectByManualId(Long manualId) {
		return cardTypeMapper.selectByManualId(manualId);
	}

	@Override
	public CardType selectByPlatformId(Long platformId) {
		return cardTypeMapper.selectByPlatformId(platformId);
	}

	@Override
	public List<CardType> selectAllToSort(){
		return cardTypeMapper.selectAllToSort();
	}
	
	@Override
	public String selectCardTypeList() {
		ApiResult<JSONArray> result = new ApiResult<JSONArray>();
		JSONArray list = new JSONArray();
		
		String key = RedisKey.CARD_TYPE_BLANK_LIST;
		try {
			String content = redis.getStringByKey(key);
			if(StringUtils.isNotBlank(content)){
				list = JSON.parseArray(content);
				return result.toJSONString(0, "success", list);
			}
		} catch (Exception e) {
			logger.error("从redis中取出空白首页的卡片类型列表时出错：" + e.getMessage(),e);
		}
		
		List<CardType> cardTypeList = this.selectAllToSort();
		boolean hasZhaoShang = false;
		for(CardType cardType : cardTypeList){
			JSONObject json = new JSONObject();
			//名称
			if(cardType.getName().contains("招商")){
				if(hasZhaoShang){
					continue;
				}
				json.put("name", "招商银行");
				hasZhaoShang = true;
			}else{
				json.put("name", cardType.getName());
			}
			//图标
			json.put("icon", "");
			Bank bank = bankService.selectByName(cardType.getBelongTo());
			if(bank != null){
				json.put("icon", appConfig.staticUrl + bank.getPicUrl());
			}
			//类型
			Long manualId = cardType.getManualId();
			if(!manualId.equals(-1L)){
				json.put("importType", "1");
			}else{
				json.put("importType", "0");
			}
			json.put("manualId", manualId);
			//排序字母
			json.put("sort", cardType.getSort());
			
			//多倍
			if(cardType.getName().contains("移动") || cardType.getName().contains("联通")
					|| cardType.getName().contains("电信") || cardType.getName().contains("天翼")
					|| cardType.getName().contains("建设") || cardType.getName().contains("交通")
					|| cardType.getName().contains("中信") || cardType.getName().contains("平安")){
				json.put("isDouble", "0");
			}else{
				json.put("isDouble", "1");
			}
			
			list.add(json);
		}
		
		try {
			if(null!=list && list.size()>0){
				redis.set(key, list.toJSONString());
			}
		} catch (Exception e) {
			logger.error("将空白首页的卡片类型列表放入redis中出错：" + e.getMessage(),e);
		}
		
		return result.toJSONString(0, "success", list);
	}
}
