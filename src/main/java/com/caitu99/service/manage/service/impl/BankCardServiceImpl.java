package com.caitu99.service.manage.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.manage.dao.BankCardMapper;
import com.caitu99.service.manage.domain.BankCard;
import com.caitu99.service.manage.service.BankCardService;


@Service
public class BankCardServiceImpl implements BankCardService {

	private final static Logger logger = LoggerFactory.getLogger(BankCardServiceImpl.class);
	
	@Autowired
	private BankCardMapper bankCardMapper;
	
	@Autowired
	private RedisOperate redis;

	@Override
	public List<BankCard> selectOnLinePageList() {
		String key = RedisKey.MANAGE_BANK_ONLINE_LIST;
		String content = redis.getStringByKey(key);
		
		if(!StringUtils.isEmpty(content)){
			return JSON.parseArray(content, BankCard.class);
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("type", BankCard.TYPE_ON_LINE);
		
		List<BankCard> list = bankCardMapper.selectPageList(map);
		
		if(list != null){
			redis.set(key, JSON.toJSONString(list));
		}
		
		return list;
	}

	@Override
	public List<BankCard> selectDropInPageList() {
		String key = RedisKey.MANAGE_BANK_DROPIN_LIST;
		String content = redis.getStringByKey(key);
		
		if(!StringUtils.isEmpty(content)){
			return JSON.parseArray(content, BankCard.class);
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("type", BankCard.TYPE_DROP_IN);
		
		List<BankCard> list = bankCardMapper.selectPageList(map);
		
		if(list != null){
			redis.set(key, JSON.toJSONString(list));
		}
		
		return list;
	}

	@Override
	public BankCard selectByPrimaryKey(Long id) {
		String key = String.format(RedisKey.MANAGE_BANK_PRIMARYKEY_BANKCARDID,id);
		String content = redis.getStringByKey(key);
		
		if(!StringUtils.isEmpty(content)){
			return JSON.parseObject(content, BankCard.class);
		}
		
		BankCard bankCard = bankCardMapper.selectByPrimaryKey(id);
		if(bankCard != null){
			redis.set(key, JSON.toJSONString(bankCard));
		}
		
		return bankCard;
	}
}
