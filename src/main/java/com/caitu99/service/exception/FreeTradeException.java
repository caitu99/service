/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.exception;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: FreeTradeException 
 * @author Hongbo Peng
 * @date 2016年1月20日 上午10:39:06 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class FreeTradeException extends ApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6492654947961182121L;

	/** 
	 * @Title:  
	 * @Description:
	 * @param code
	 * @param description 
	 */
	public FreeTradeException(int code, String description) {
		super(code, description);
		// TODO Auto-generated constructor stub
	}

}
