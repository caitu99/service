/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.exception;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: OrderException 
 * @author fangjunxiao
 * @date 2015年11月25日 下午3:57:32 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class OrderException extends ApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7675382852594374107L;

	/** 
	 * @Title:  
	 * @Description:
	 * @param code
	 * @param description
	 * @param data 
	 */
	public OrderException(int code, String description, String data) {
		super(code, description, data);
		// TODO Auto-generated constructor stub
	}

	/** 
	 * @Title:  
	 * @Description:
	 * @param code
	 * @param description 
	 */
	public OrderException(int code, String description) {
		super(code, description);
		// TODO Auto-generated constructor stub
	}
	

}
