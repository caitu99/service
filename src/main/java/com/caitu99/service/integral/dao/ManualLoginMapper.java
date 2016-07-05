package com.caitu99.service.integral.dao;

import java.util.List;
import java.util.Map;

import com.caitu99.service.integral.domain.ManualLogin;

public interface ManualLoginMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(ManualLogin record);

    ManualLogin selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ManualLogin record);

    ManualLogin getBySelective(ManualLogin record);
    
    List<ManualLogin> findListByUserIdManualId(Map<String,Object> map);
    
    List<ManualLogin> selectPageList(Map<String,Object> map);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectAccountForUpdate 
	 * @return
	 * @date 2015年12月18日 上午11:49:41  
	 * @author ws
	*/
	List<ManualLogin> selectAccountForUpdate();
	
	/**
	 * 根据userId查询用户记录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUserId 
	 * @param userId
	 * @return
	 * @date 2015年12月21日 下午3:57:32  
	 * @author xiongbin
	 */
	List<ManualLogin> selectByUserId(Long userId);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUserIdForBatch 
	 * @param userId
	 * @return
	 * @date 2016年2月24日 下午2:24:03  
	 * @author ws
	*/
	List<ManualLogin> selectByUserIdForBatch(Long userId);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findByUserIdManualId 
	 * @param map
	 * @return
	 * @date 2016年2月25日 下午12:12:57  
	 * @author ws
	*/
	List<ManualLogin> findByUserIdManualId(Map<String, Object> map);

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findByUserIdManualId2 
	 * @param map
	 * @return
	 * @date 2016年3月16日 下午12:12:57  
	 * @author ws
	*/
	List<ManualLogin> findByUserIdManualId2(Map<String, Object> map);
}
