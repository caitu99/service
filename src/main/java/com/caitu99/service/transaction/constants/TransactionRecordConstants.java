/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.constants;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: TransactionRecordConstants 
 * @author ws
 * @date 2015年12月1日 下午7:44:17 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface TransactionRecordConstants {
	
	/*-1：失败 0：冻结 1：处理中 2：成功*/
	public static final int STATUS_FAIL = -1;
	public static final int STATUS_FREEZE = 0;
	public static final int STATUS_DOING = 1;
	public static final int STATUS_SUCCESS = 2;
	
	
	
}
