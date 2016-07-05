/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.exception;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IntegralRechargeException 
 * @author Hongbo Peng
 * @date 2015年12月10日 下午4:50:32 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class IntegralRechargeException extends ApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6996345146012451483L;

	/** 
	 * @Title:  
	 * @Description:
	 * @param code
	 * @param description 
	 */
	public IntegralRechargeException(int code, String description) {
		super(code, description);
		// TODO Auto-generated constructor stub
	}

}
