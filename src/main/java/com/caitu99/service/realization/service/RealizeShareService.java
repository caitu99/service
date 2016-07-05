package com.caitu99.service.realization.service;

import java.util.List;

import com.caitu99.service.realization.domain.RealizeShare;


public interface RealizeShareService {
	
	/**
	 * 生成分享数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: generate 
	 * @param userid				用户ID
	 * @param realizeRecordId		积分变现记录ID
	 * @return
	 * @date 2016年4月11日 下午7:05:09  
	 * @author xiongbin
	 */
	Long generate(Long userid,Long realizeRecordId);
	
	Long insert(Long userid,Long realizeRecordId);
	
	/**
	 * 查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectPageList 
	 * @param realizeShare
	 * @return
	 * @date 2016年4月12日 上午10:00:21  
	 * @author xiongbin
	 */
	List<RealizeShare> selectPageList(RealizeShare realizeShare);
	
	/**
	 * 查询是否已分享过
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: isExist 
	 * @param userId				用户ID
	 * @param realizeRecordId		积分变现记录ID
	 * @return
	 * @date 2016年4月12日 上午10:01:43  
	 * @author xiongbin
	 */
	RealizeShare isExist(Long userId,Long realizeRecordId); 
	
	/**
	 * 领取分享
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: receive 
	 * @param phone				手机号码
	 * @param realizeShareId	分享记录ID
	 * @param platform			分享平台
	 * @date 2016年4月12日 上午10:32:12  
	 * @author xiongbin
	 */
	Long receive(String phone,Long realizeShareId,Integer platform);

	/**
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectByPrimaryKey 
	 * @param id
	 * @return
	 * @date 2016年5月24日 下午8:07:42  
	 * @author xiongbin
	*/
	RealizeShare selectByPrimaryKey(Long id);
}
