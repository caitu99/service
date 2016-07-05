package com.caitu99.service.exception;


/**
 * 手动查询接口异常
 * @author 熊斌 2015年11月10日
 */
public class ManualQueryAdaptorException extends ApiException {
	
	private static final long serialVersionUID = 1537901235563361521L;

	public ManualQueryAdaptorException(int code, String description) {
		super(code, description);
	}
	
	public ManualQueryAdaptorException(int code, String description, String data) {
		super(code, description, data);
	}
	
}
