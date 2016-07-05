/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.exception;

/**
 * 
 * @Description: (类职责详细描述,可空)
 * @ClassName: ActivitiesException
 * @author fangjunxiao
 * @date 2015年12月1日 下午6:34:27
 * @Copyright (c) 2015-2020 by caitu99
 */
public class ActivitiesException extends ApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2321495791240653778L;

	/** 
	 * @Title:  
	 * @Description:
	 * @param code
	 * @param description
	 * @param data 
	 */
	public ActivitiesException(int code, String description, String data) {
		super(code, description, data);
		// TODO Auto-generated constructor stub
	}

	/** 
	 * @Title:  
	 * @Description:
	 * @param code
	 * @param description 
	 */
	public ActivitiesException(int code, String description) {
		super(code, description);
		// TODO Auto-generated constructor stub
	}

}
