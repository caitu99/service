package com.caitu99.service.realization.service;

import java.util.List;

import com.caitu99.service.realization.domain.RealizePlatform;

public interface RealizePlatformService {
	
	/**
	 * 根据版本号查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectPageList 
	 * @param version	版本号
	 * @return
	 * @date 2016年2月23日 上午11:38:36  
	 * @author xiongbin
	 */
	List<RealizePlatform> selectPageList(String version);
	
    RealizePlatform selectByPrimaryKey(Long id);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectBySupport 
	 * @param string
	 * @param support
	 * @return
	 * @date 2016年4月27日 下午3:51:27  
	 * @author ws
	*/
	List<RealizePlatform> selectBySupport(String version, String support);
}
