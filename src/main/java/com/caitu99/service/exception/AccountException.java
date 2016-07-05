/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.exception;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: PayException 
 * @author lhj
 * @date 2015年12月2日 下午2:33:04 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class AccountException extends ApiException {

	/** 
	 * @Title:  
	 * @Description:
	 * @param code
	 * @param description 
	 */
	public AccountException(int code, String description) {
		super(code, description);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
