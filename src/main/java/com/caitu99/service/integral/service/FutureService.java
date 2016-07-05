package com.caitu99.service.integral.service;

import java.util.List;

import com.caitu99.service.exception.FutureException;
import com.caitu99.service.integral.domain.Future;

public interface FutureService {
	
	/**
	 * 根据积分账户ID查询手动查询登录配置
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectListByManualId 
	 * @param manualId		积分账户ID
	 * @param type			配置类型
	 * @return
	 * @date 2015年11月11日 上午9:43:42  
	 * @author xiongbin
	 */
	List<Future> findListByManualIdType(Long manualId,Integer type) throws FutureException;
}
