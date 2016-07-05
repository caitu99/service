package com.caitu99.service.integral.service;

import com.caitu99.service.integral.domain.IntegralRealizationSubscribe;

public interface IntegralRealizationSubscribeService {
	
	/**
	 * 根据用户Id和卡片类型Id
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUserIdCardTypeId 
	 * @param userId		用户ID
	 * @param cardTypeId	卡片类型ID
	 * @return
	 * @date 2016年1月25日 下午6:19:33  
	 * @author xiongbin
	 */
    IntegralRealizationSubscribe selectByUserIdCardTypeId(Long userId,Long cardTypeId);
    
    /**
     * 积分变现预约
     * @Description: (方法职责详细描述,可空)  
     * @Title: subscribe 
     * @param userId			用户Id
     * @param cardTypeId		卡片类型Id
     * @date 2016年1月26日 上午10:28:39  
     * @author xiongbin
     */
    void subscribe(Long userId,Long cardTypeId);
    
    /**
     * 查询平台总预约人数
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectCount 
     * @param cardTypeId		平台Id
     * @return
     * @date 2016年1月26日 上午9:34:22  
     * @author xiongbin
     */
    Integer selectCount(Long cardTypeId);
}
