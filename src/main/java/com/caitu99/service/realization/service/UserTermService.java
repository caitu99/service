package com.caitu99.service.realization.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.realization.domain.UserTerm;


public interface UserTermService {
	
	/**
	 * 用户积分变现列表
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectRealizationList 
	 * @param userid	用户ID
	 * @param version	版本号
	 * @return
	 * @date 2016年2月23日 下午6:24:13  
	 * @author xiongbin
	 * @param isBindOnly 
	 */
	List<JSONObject> selectRealizationList(Long userid,String version, boolean isBindOnly);
	
	/**
	 * 查询绑定关系
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectUserTerm 
	 * @param userId			用户ID
	 * @param remoteId			关联主键
	 * @param remoteType		关联类型(1:账单;2:实时,3:添加)
	 * @return
	 * @date 2016年2月23日 下午5:30:10  
	 * @author xiongbin
	 */
    UserTerm selectUserTerm(Long userId,Long remoteId,Integer remoteType);
    
    /**
     * @Description: (添加或者修改绑定关系)  
     * @Title: saveOrUpdate 
     * @param userTerm
     * @date 2016年2月24日 下午3:51:19  
     * @author Hongbo Peng
     */
    Long saveOrUpdate(UserTerm userTerm);
    
    /**
     * 查询用户登录账号
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectLoginAccount 
     * @param remoteId		外部记录ID
     * @param remoteType	外部记录类型(1.账单，2.实时，3.添加)
     * @return
     * @date 2016年3月22日 上午10:28:23  
     * @author xiongbin
     */
    String selectLoginAccount(Long remoteId,Integer remoteType);
}
