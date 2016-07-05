/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.exception;
/**
 * 接口开关关闭异常类
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UserNotFoundException 
 * @author lawrence
 * @date 2015年11月2日 下午2:51:06 
 * @Copyright (c) 2015-2020 by caitu99
 */
public class ApiClosedException extends ApiException {
	
	private static final long serialVersionUID = 1537901235563361521L;

	public ApiClosedException(){
		super(-1, "接口开关关闭异常");
	}
	
	public ApiClosedException(int code, String description) {
		super(code, description);
	}
	
	public ApiClosedException(int code, String description, String data) {
		super(code, description, data);
	}



}
