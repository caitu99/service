package com.caitu99.service.integral.service;

import java.util.List;

import com.caitu99.service.exception.UserCardManualException;
import com.caitu99.service.integral.controller.vo.CardIntegralLastTime;
import com.caitu99.service.integral.domain.UserCardManual;
import com.caitu99.service.user.controller.vo.IntegralVo;
import com.caitu99.service.user.controller.vo.UserCardManualVo;

public interface UserCardManualService {
	
	/**
	 * 新增
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: insert 
	 * @param record
	 * @date 2015年11月11日 下午6:14:48  
	 * @author xiongbin
	 */
	void insert(UserCardManual userCardManual) throws UserCardManualException;
	
	/**
	 * 根据用户ID和卡类型ID查询用户手动查询积分表
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getByUserIdCardTypeId 
	 * @param userId		用户ID
	 * @param manualId		积分账户ID
	 * @param cardTypeId	卡类型ID
	 * @return
	 * @throws UserCardManualException
	 * @date 2015年11月11日 下午7:29:53  
	 * @author xiongbin
	 */
    UserCardManual getByUserIdCardTypeId(Long userId,Long manualId,Long cardTypeId) throws UserCardManualException;
    
    /**
     * 修改
     * @Description: (方法职责详细描述,可空)  
     * @Title: updateByPrimaryKeySelective 
     * @param userCardManual
     * @throws UserCardManualException
     * @date 2015年11月11日 下午7:41:07  
     * @author xiongbin
     */
    void updateByPrimaryKeySelective(UserCardManual userCardManual) throws UserCardManualException;
    
    /**
     * 查询用户积分
     * @Description: (方法职责详细描述,可空)  
     * @Title: queryIntegral 
     * @param userid
     * @return
     * @throws UserCardManualException
     * @date 2015年11月12日 下午8:12:13  
     * @author xiongbin
     */
    List<UserCardManualVo> queryIntegral(Long userid) throws UserCardManualException;
    
    /**
     * 查询用户积分
     * @Description: (方法职责详细描述,可空)  
     * @Title: queryIntegralRemoveRepetition 
     * @param userid
     * @param status
     * @return
     * @throws UserCardManualException
     * @date 2015年11月13日 下午3:59:58  
     * @author xiongbin
     */
    List<UserCardManualVo> queryIntegralRemoveRepetition(Long userid,Integer status) throws UserCardManualException;
    
    /**
     * 新增或修改
     * @Description: (方法职责详细描述,可空)  
     * @Title: insertORupdate 
     * @param userCardManual
     * @throws UserCardManualException
     * @date 2015年11月18日 下午3:12:20  
     * @author xiongbin
     */
    void insertORupdate(UserCardManual userCardManual) throws UserCardManualException;
    
    /**
     * 条件查询
     * @Description: (方法职责详细描述,可空)  
     * @Title: getUserCardManualSelective 
     * @param map
     * @return
     * @date 2015年11月19日 下午3:09:27  
     * @author xiongbin
     */
    UserCardManual getUserCardManualSelective(Long userId,Long cardTypeId,String cardNo,String userName,String loginAccount);
    
    /**
     * 主键查询
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectByPrimaryKey 
     * @param id
     * @return
     * @date 2015年11月23日 下午3:22:41  
     * @author xiongbin
     */
    UserCardManual selectByPrimaryKey(Long id);

    /**
	 * 查询用户积分
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: queryIntegral 
	 * @param userid		用户ID
	 * @param status		积分状态
	 * @return
	 * @date 2015年12月22日 下午8:07:58  
	 * @author xiongbin
	 */
	List<IntegralVo> queryIntegral(Long userid, Integer status);
	
	/**
	 * 根据用户ID和积分账户ID查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUserIdManualId 
	 * @param userId
	 * @param manualId
	 * @return
	 * @date 2015年12月24日 上午11:09:48  
	 * @author xiongbin
	 */
    List<UserCardManual> selectByUserIdManualId(Long userId,Long manualId);
    
    /**
     * 首页最先过期的积分详情
     * @Description: (方法职责详细描述,可空)  
     * @Title: selectLastTimeFirst 
     * @param userId
     * @return
     * @date 2016年1月19日 上午11:53:36  
     * @author xiongbin
     */
    CardIntegralLastTime selectLastTimeFirst(Long userId);
}
