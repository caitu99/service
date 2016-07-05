/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.api;

/** 
 * 企业赠送财币实时划转 或 过期解冻
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IintegralThawer 
 * @author ws
 * @date 2015年12月1日 上午10:26:59 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface ICompanyToThaw {
	
	/**
	 * 	判断用户是否是新用户
	 *  如果是，则冻结企业财币，并且加入解冻计划中
	 *  如果不是，则直接划转财币
	 *  <P>事务处理
	 * 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: addCompanyToThaw 
	 * @param phoneNo
	 * @param orderNo
	 * @param integral
	 * @date 2015年12月2日 下午4:18:31  
	 * @author ws
	*/
	void addCompanyToThaw(String phoneNo, String orderNo, Long integral);
}
