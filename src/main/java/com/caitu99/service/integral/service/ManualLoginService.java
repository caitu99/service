package com.caitu99.service.integral.service;

import java.util.List;

import com.caitu99.service.exception.ManualLoginException;
import com.caitu99.service.integral.domain.ManualLogin;

public interface ManualLoginService {
	
	/**
	 * 根据用户ID,积分账户ID查询用户登录记录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findListByUserIdManualId 
	 * @param userId		用户ID
	 * @param manualId		积分账户ID
	 * @return
	 * @date 2015年11月11日 上午9:30:28  
	 * @author xiongbin
	 */
	List<ManualLogin> findListByUserIdManualId(Long userId,Long manualId) throws ManualLoginException;
	
	/**
	 * 新增
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: insert 
	 * @param record
	 * @throws ManualLoginException
	 * @date 2015年11月11日 下午7:52:58  
	 * @author xiongbin
	 */
	void insert(ManualLogin record) throws ManualLoginException;
	
	/**
	 * 修改
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: updateByPrimaryKeySelective 
	 * @param record
	 * @date 2015年11月13日 下午2:01:03  
	 * @author xiongbin
	 */
	void updateByPrimaryKeySelective(ManualLogin record);

	/**
	 * 查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getBySelective 
	 * @param record
	 * @return
	 * @date 2015年11月13日 下午2:01:17  
	 * @author xiongbin
	 */
    ManualLogin getBySelective(ManualLogin record);
    
    /**
     * 新增或修改
     * @Description: (方法职责详细描述,可空)  
     * @Title: insertORupdate 
     * @param record
     * @throws ManualLoginException
     * @date 2015年11月18日 下午3:18:01  
     * @author xiongbin
     */
    void insertORupdate(ManualLogin record) throws ManualLoginException;
    
    /**
     * 查询列表
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectPageList 
     * @param start			当前页起始行
     * @param pageSize		每页多少行
     * @return
     * @date 2015年12月18日 下午4:20:18  
     * @author xiongbin
     */
    List<ManualLogin> selectPageList(Integer start,Integer pageSize);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectAccountForUpdate 
	 * @return
	 * @date 2015年12月18日 上午11:48:53  
	 * @author ws
	*/
	List<ManualLogin> selectAccountForUpdate();
	
	/**
	 * 根据userId查询用户记录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUserId 
	 * @param userId
	 * @return
	 * @date 2015年12月21日 下午3:59:45  
	 * @author xiongbin
	 */
	List<ManualLogin> selectByUserId(Long userId);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUserIdForBatch 
	 * @param userId
	 * @return
	 * @date 2016年2月24日 下午2:23:32  
	 * @author ws
	*/
	List<ManualLogin> selectByUserIdForBatch(Long userId);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findByUserIdManualId 
	 * @param userId
	 * @param manualId
	 * @param account
	 * @return
	 * @date 2016年2月25日 下午12:11:02  
	 * @author ws
	*/
	List<ManualLogin> findByUserIdManualId(Long userId, Long manualId,
			String account);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findByUserIdManualId 
	 * @param userid
	 * @param manualId
	 * @return
	 * @date 2016年3月16日 下午12:10:00  
	 * @author ws
	*/
	List<ManualLogin> findByUserIdManualId(Long userid, Long manualId);

}
