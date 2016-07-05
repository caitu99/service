package com.caitu99.service.integral.service;

import java.util.List;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.integral.controller.vo.CardIntegralLastTime;
import com.caitu99.service.integral.domain.CardIntegral;

public interface CardIntegralService {
	int insert(CardIntegral record);

	List<CardIntegral> list(Long userId);

	int updateByPrimaryKeySelective(CardIntegral record);

	int deleteByCardId(Long cardId);

	List<CardIntegralLastTime> selectLastTime(Long userId);

	List<CardIntegralLastTime> selectOtherTime(Long userId);
	
	/**
	 * 根据卡片ID和用户ID查询最近的积分详情
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectLastTimeNew 
	 * @param userId	用户ID
	 * @param cardId	卡片ID
	 * @return
	 * @date 2016年1月5日 下午4:31:32  
	 * @author xiongbin
	 */
	List<CardIntegralLastTime> selectLastTimeNew(Long userId,Long cardId);
	
	/**
	 * 首页最先过期的积分详情
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectLastTimeFirst 
	 * @param userId
	 * @return
	 * @date 2016年1月19日 上午11:15:55  
	 * @author xiongbin
	 */
	CardIntegralLastTime selectLastTimeFirst(Long userId);
	
	/**
	 * 分页查询最近的积分详情
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectLastTimePageList 
	 * @param userId
	 * @param pagination
	 * @return
	 * @date 2016年1月19日 上午11:10:46  
	 * @author xiongbin
	 */
	Pagination<CardIntegralLastTime> selectLastTimePageList(Long userId,Pagination<CardIntegralLastTime> pagination);
	
	/**
	 * 查询某卡片最近第一个过期的积分
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectLastTimeByCardId 
	 * @param cardId
	 * @return
	 * @date 2016年1月25日 下午4:35:39  
	 * @author xiongbin
	 */
	CardIntegral selectFirstTimeByCardId(Long cardId);
}
