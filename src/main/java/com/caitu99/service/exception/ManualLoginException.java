package com.caitu99.service.exception;


/**
 * 手动账户登录记录异常
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ManualLoginException 
 * @author xiongbin
 * @date 2015年11月11日 上午9:35:42 
 * @Copyright (c) 2015-2020 by caitu99
 */
public class ManualLoginException extends ApiException {
	
	private static final long serialVersionUID = 1537901235563361521L;

	public ManualLoginException(int code, String description) {
		super(code, description);
	}
	
	public ManualLoginException(int code, String description, String data) {
		super(code, description, data);
	}
	
}
