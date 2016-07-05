/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.push.service;

import java.util.List;

import com.caitu99.service.base.Pagination;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.PushMessage;
import com.caitu99.service.push.model.enums.RedSpot;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: PushMessageService 
 * @author Hongbo Peng
 * @date 2015年12月22日 下午4:12:51 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface PushMessageService {

	/**
	 * @Description: (统一消息处理，根据regid推送)  
	 * @Title: pushMessage 
	 * @param redSpot 小红点
	 * @param userId  发送的用户
	 * @param message 消息信息
	 * @date 2015年12月22日 下午4:21:43  
	 * @author Hongbo Peng
	 */
	void pushMessage(RedSpot redSpot,Long userId,Message message) throws Exception;
	
	/**
	 * @Description: (定时推送时调用)  
	 * @Title: pushMessage 
	 * @param messageId
	 * @throws Exception
	 * @date 2015年12月23日 上午10:59:48  
	 * @author Hongbo Peng
	 */
	void pushMessage(String messageId)throws Exception;
	
	/**
	 * @Description: (短信发送，用于小米推送失败时调用)  
	 * @Title: smsPush 
	 * @param messageId
	 * @throws Exception
	 * @date 2015年12月23日 上午11:30:22  
	 * @author Hongbo Peng
	 */
	void smsPush(String messageId)throws Exception;
	
	/**
	 * @Description: (批量已读消息)  
	 * @Title: readMessage 
	 * @param userId
	 * @param status
	 * @throws Exception
	 * @date 2015年12月23日 上午11:00:12  
	 * @author Hongbo Peng
	 */
	void editMessage(String[] ids,Integer status) throws Exception;
	
	/**
	 * @Description: (消息列表分页)  
	 * @Title: findPage 
	 * @param pagination
	 * @param pushMessage
	 * @return
	 * @throws Exception
	 * @date 2015年12月25日 下午4:54:21  
	 * @author Hongbo Peng
	 */
	Pagination<PushMessage> findPage(Pagination<PushMessage> pagination,PushMessage pushMessage)throws Exception;
	
	List<PushMessage> getRedSpot(Long userId) throws Exception;
	/**
	 * @Description: (将消息存到消息中心，不推送)  
	 * @Title: saveMessage 
	 * @param redSpot
	 * @param userId
	 * @param message
	 * @throws Exception
	 * @date 2016年1月4日 下午4:04:26  
	 * @author Hongbo Peng
	 */
	void saveMessage(RedSpot redSpot,Long userId,Message message) throws Exception;
	
	/**
	 * @Description: (群推消息，给所有的人推送一个消息)  
	 * @Title: sendBroadcast 
	 * @param message
	 * @param title
	 * @throws Exception
	 * @date 2016年1月4日 下午4:05:11  
	 * @author Hongbo Peng
	 */
	void sendBroadcast(String title,String message) throws Exception;
}
