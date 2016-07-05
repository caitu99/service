package com.caitu99.service.integral.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.platform.dao.base.func.IEntityDAO;
import com.caitu99.service.integral.controller.vo.CardIntegralLastTime;
import com.caitu99.service.integral.domain.CardIntegral;

public interface CardIntegralMapper extends IEntityDAO<CardIntegral, CardIntegral> {
	int deleteByPrimaryKey(Long id);

	int deleteByCardId(Long cardId);

	int insert(CardIntegral record);

	int insertSelective(CardIntegral record);

	CardIntegral selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(CardIntegral record);

	int updateByPrimaryKey(CardIntegral record);

	List<CardIntegral> list(Long cardId);

	List<CardIntegralLastTime> selectLastTime(Long userId);

	List<CardIntegralLastTime> selectOtherTime(Long userId);
	
	List<CardIntegralLastTime> selectLastTimeNew(Map<String,Object> map);
	
	Integer selectPageCount(Map<String,Object> map);
	
	List<CardIntegralLastTime> selectLastTimePageList(Map<String,Object> map);
	
	CardIntegral selectFirstTimeByCardId(Map<String,Object> map);
}