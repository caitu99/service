/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.exception;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UnionException 
 * @author Hongbo Peng
 * @date 2015年12月10日 下午2:58:19 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class UnionException extends ApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8588675462824600042L;

	/** 
	 * @Title:  
	 * @Description:
	 * @param code
	 * @param description 
	 */
	public UnionException(int code, String description) {
		super(code, description);
		// TODO Auto-generated constructor stub
	}

}
