package com.caitu99.service.integral.service.impl;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.integral.controller.vo.CardIntegralLastTime;
import com.caitu99.service.integral.dao.UserCardManualItemMapper;
import com.caitu99.service.integral.domain.UserCardManualItem;
import com.caitu99.service.integral.service.UserCardManualItemService;
import com.caitu99.service.user.controller.vo.IntegralVo;

@Service
public class UserCardManualItemServiceImpl implements UserCardManualItemService {
	
	private final static Logger logger = LoggerFactory.getLogger(UserCardManualItemServiceImpl.class);

	@Autowired
	private UserCardManualItemMapper userCardManualItemMapper;

	@Override
	public CardIntegralLastTime selectLastTimePageFirst(Long userId) {
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("userId", userId);
		map.put("start", 0);
		map.put("pageSize", 1);
		
		List<CardIntegralLastTime> list = userCardManualItemMapper.selectLastTimePageList(map);
		
		if(null!=list && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	@Override
	public Pagination<CardIntegralLastTime> selectLastTimePageList(Long userId,Pagination<CardIntegralLastTime> pagination) {
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("userId", userId);
		map.put("start", pagination.getStart());
		map.put("pageSize", pagination.getPageSize());
		
		Integer count = userCardManualItemMapper.selectPageCount(map);
		List<CardIntegralLastTime> list = userCardManualItemMapper.selectLastTimePageList(map);
		pagination.setTotalRow(count);
		pagination.setDatas(list);
		
		return pagination;
	}

	@Override
	public void insert(UserCardManualItem record) {
		Date now = new Date();
		record.setGmtCreate(now);
		record.setGmtModify(now);
		record.setStatus(1);
		userCardManualItemMapper.insert(record);
	}

	@Override
	public void updateByPrimaryKey(UserCardManualItem record) {
		Date now = new Date();
		record.setGmtModify(now);
		userCardManualItemMapper.updateByPrimaryKey(record);
	}
	
	@Override
	public void insertORupdate(UserCardManualItem record) {
		if(null == record.getId()){
			userCardManualItemMapper.insert(record);
		}else{
			record.setGmtCreate(null);
			userCardManualItemMapper.updateByPrimaryKeySelective(record);
		}
	}

	@Override
	public UserCardManualItem selectByUserCardManualId(Long userCardManualId) {
		return userCardManualItemMapper.selectByUserCardManualId(userCardManualId);
	}

	@Override
	public void deleteByUserCardManualId(Long userCardManualId) {
		userCardManualItemMapper.deleteByUserCardManualId(userCardManualId);
	}

	@Override
	public UserCardManualItem selectFirstTimeByCardId(Long userCardManualId) {
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("userCardManualId", userCardManualId);
		map.put("start", 0);
		map.put("pageSize", 1);
		
		return userCardManualItemMapper.selectFirstTimeByCardId(map);
	}
	
	public static void main(String[] args) {
		Map<Integer, IntegralVo> map = new HashMap<>();
		DecimalFormat df = new DecimalFormat("#.00"); 
		for (Integer key : map.keySet()) {
			IntegralVo caifen = map.get(key);
			caifen.setIntegral(0);
			double money = caifen.getMoney();
			caifen.setMoney(Double.parseDouble(df.format(money)));
		}
	}
}
