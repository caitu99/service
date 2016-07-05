package com.caitu99.service.realization.service;

import java.util.List;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.realization.domain.RealizeShareRecord;


public interface RealizeShareRecordService {
	
	/**
	 * 新增
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: insert 
	 * @param userid				用户ID
	 * @param realizeShareId		积分变现分享ID
	 * @param money					财币
	 * @param platform				分享平台
	 * @param type					1.财币券2.途币券
	 * @date 2016年4月12日 上午11:18:07  
	 * @author xiongbin
	 */
	void insert(Long userid, Long realizeShareId,Long money,Integer platform,Integer type);
	
	/**
	 * 查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectPageList 
	 * @param realizeShareRecord
	 * @return
	 * @date 2016年4月12日 上午10:00:21  
	 * @author xiongbin
	 */
	List<RealizeShareRecord> selectPageList(RealizeShareRecord realizeShareRecord);
	
	/**
	 * 查询是否已分享过
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: isExist 
	 * @param userId					用户ID
	 * @param realizeShareId			积分变现分享记录ID
	 * @param platform					分享平台
	 * @return
	 * @date 2016年4月12日 上午10:01:43  
	 * @author xiongbin
	 */
	Long isExist(Long userId,Long realizeShareId,Integer platform);
	
	/**
	 * 是否是首次领取分享
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: isFirst 
	 * @param userId	用户ID
	 * @return
	 * @date 2016年4月12日 下午3:38:13  
	 * @author xiongbin
	 */
	boolean isFirst(Long userId);
	
	/**
	 * 获取用户最早并且没有过期的分享红包
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectFirstRecord 
	 * @param userId	用户ID
	 * @return
	 * @date 2016年4月12日 下午5:14:42  
	 * @author xiongbin
	 */
	RealizeShareRecord selectFirstRecord(Long userId,Integer type);

	/**
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: update 
	 * @param realizeShareRecord
	 * @date 2016年4月13日 上午10:20:36  
	 * @author xiongbin
	*/
	void update(RealizeShareRecord realizeShareRecord);
	
	/**
	 * 根据积分变现记录ID查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByRealizeRecordId 
	 * @param realizeRecordId	积分变现记录ID
	 * @return
	 * @date 2016年4月13日 上午10:28:25  
	 * @author xiongbin
	 */
	List<RealizeShareRecord> selectByRealizeRecordId(Long realizeRecordId);
	
	/**
	 * 根据用户ID查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByUserId 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年4月14日   
	 * @author liuzs
	 */
	Pagination<RealizeShareRecord> selectByUserId(Long userid, Pagination<RealizeShareRecord> pagination);

	/**
	 * 查询用户已使用的礼券
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectHasUsedByUserId 
	 * @param userId	用户ID
	 * @return
	 * @date 2016年4月18日 下午4:03:26  
	 * @author xiongbin
	*/
	List<RealizeShareRecord> selectHasUsedByUserId(Long userId);
}
