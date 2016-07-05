package com.caitu99.service.integral.service;

import java.util.List;
import java.util.Map;

import com.caitu99.service.exception.ManualException;
import com.caitu99.service.integral.domain.Manual;

public interface ManualService {
	/**
	 * 	主键查询对象
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByPrimaryKey 
	 * @param id
	 * @return
	 * @date 2015年11月12日 下午7:27:35  
	 * @author lawrence
	 */
	 Manual selectByPrimaryKey(Long id);
	
	/**
	 * 查询积分账户列表
	 * @return
	 */
	List<Manual> list();
	
	/**
	 * 查询积分账户列表,字母排序
	 * @return
	 */
	Map<String,List<Manual>> finlListToSort();
	
	/**
	 * 生成登录页面参数
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: generatelogin 
	 * @param userId		用户ID
	 * @param manualId		积分账户ID
	 * @return
	 * @date 2015年11月11日 上午10:22:46  
	 * @author xiongbin
	 */
	Map<String,Object> generatelogin(Long userId,Long manualId);
	Map<String,Object> generatelogin2(Long userId,Long manualId) throws ManualException;

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: listByVersion 
	 * @param version
	 * @return
	 * @date 2015年12月11日 下午5:55:46  
	 * @author ws
	*/
	List<Manual> listByVersion(String version);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: generateloginWithAccount 
	 * @param userid
	 * @param manualId
	 * @param account
	 * @return
	 * @date 2016年2月25日 上午11:35:15  
	 * @author ws
	*/
	Map<String, Object> generateloginWithAccount(Long userid, Long manualId,
			String account);
}
