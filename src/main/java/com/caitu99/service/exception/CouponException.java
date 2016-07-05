/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.exception;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: CouponException 
 * @author Hongbo Peng
 * @date 2015年12月7日 下午12:17:59 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class CouponException extends ApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9027083481501484343L;

	/** 
	 * @Title:  
	 * @Description:
	 * @param code
	 * @param description 
	 */
	public CouponException(int code, String description) {
		super(code, description);
		// TODO Auto-generated constructor stub
	}

}
