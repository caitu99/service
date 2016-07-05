/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.exception;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ExpedientException 
 * @author fangjunxiao
 * @date 2016年5月30日 下午4:11:43 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ExpedientException extends ApiException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6086236005052770076L;

	/** 
	 * @Title:  
	 * @Description:
	 * @param code
	 * @param description
	 * @param data 
	 */
	public ExpedientException(int code, String description, String data) {
		super(code, description, data);
		// TODO Auto-generated constructor stub
	}
	
	

	/** 
	 * @Title:  
	 * @Description:
	 * @param code
	 * @param description 
	 */
	public ExpedientException(int code, String description) {
		super(code, description);
		// TODO Auto-generated constructor stub
	}
	

}
