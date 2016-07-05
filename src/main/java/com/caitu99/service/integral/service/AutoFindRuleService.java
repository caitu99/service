package com.caitu99.service.integral.service;

import java.util.List;

import com.caitu99.service.integral.domain.AutoFindRule;

public interface AutoFindRuleService {

	/**
	 * 根据积分账户查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByManualId 
	 * @param manualId
	 * @return
	 * @date 2015年12月14日 下午12:09:25  
	 * @author xiongbin
	 */
    AutoFindRule selectByManualId(Long manualId);
    
    /**
     * 查询自动发现规则
     * @Description: (方法职责详细描述,可空)  
     * @Title: list 
     * @return
     * @date 2015年12月14日 下午2:56:06  
     * @author xiongbin
     */
    List<AutoFindRule> list();
}
