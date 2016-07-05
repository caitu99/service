package com.caitu99.service.integral.service;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.integral.controller.vo.CardIntegralLastTime;
import com.caitu99.service.integral.domain.UserCardManualItem;

public interface UserCardManualItemService {
	
	/**
	 * 查询首页第一个到期积分详情
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectLastTimePageFirst 
	 * @param userId
	 * @return
	 * @date 2016年1月20日 下午7:13:40  
	 * @author xiongbin
	 */
	CardIntegralLastTime selectLastTimePageFirst(Long userId);
	
	/**
	 * 分页查询首页到期积分详情
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectLastTimePageList 
	 * @param userId
	 * @param pagination
	 * @return
	 * @date 2016年1月20日 下午7:14:09  
	 * @author xiongbin
	 */
	Pagination<CardIntegralLastTime> selectLastTimePageList(Long userId,Pagination<CardIntegralLastTime> pagination);
	
	void insert(UserCardManualItem record);

    void updateByPrimaryKey(UserCardManualItem record);
    
    void insertORupdate(UserCardManualItem record);

    /**
     * 根据用户手动查询积分ID查询用户手动查询积分明细
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectByUserCardManualId 
     * @param userCardManualId
     * @return
     * @date 2016年1月20日 下午8:26:48  
     * @author xiongbin
     */
    UserCardManualItem selectByUserCardManualId(Long userCardManualId);
    
    void deleteByUserCardManualId(Long userCardManualId);
    

	/**
	 * 查询某卡片最近第一个过期的积分
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectLastTimeByCardId 
	 * @param userCardManualId
	 * @return
	 * @date 2016年1月25日 下午4:35:39  
	 * @author xiongbin
	 */
    UserCardManualItem selectFirstTimeByCardId(Long userCardManualId);
}
