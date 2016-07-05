/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.exception;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: LotteryException 
 * @author fangjunxiao
 * @date 2016年5月10日 下午6:01:27 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class LotteryException extends ApiException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7563151126242641935L;

	/** 
	 * @Title:  
	 * @Description:
	 * @param code
	 * @param description
	 * @param data 
	 */
	public LotteryException(int code, String description, String data) {
		super(code, description, data);
		// TODO Auto-generated constructor stub
	}

	
}
