package com.caitu99.service.utils.exception;

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.utils.exception.enums.ExceptionObject;


/**
 * 基础异常
 * @ClassName: BaseException 
 * @Description: (这里用一句话描述这个类的作用) 
 * @author lys
 * @date 2015年3月3日 下午4:40:03
 */
public class BaseException extends RuntimeException {

	private static final long serialVersionUID = 7090283990625759498L;
	
	private ExceptionObject exObj;

	public BaseException() {
		super();
	}
	
	public BaseException(String message) {
		super(message);
	}
	
	public BaseException(Throwable cause) {
		super(getCaseMessage(cause));
		if (cause instanceof BaseException) {
			this.exObj = ((BaseException) cause).exObj;
		}
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
		if (cause instanceof BaseException) {
			this.exObj = ((BaseException) cause).exObj;
		}
	}
	
	public BaseException(ExceptionObject exObj) {
		super(exObj.getLogInfo());
		this.exObj = exObj;
	}
	
	public String getErrCode(){
		if (null == exObj) {
			return null;
		}
		return exObj.getErrCode();
	}
	
	public String getExternalMsg() {
		if (null != exObj && null != exObj.getViewInfo()) {
			return exObj.getViewInfo();
		}
		return "System error!!!";
	}
	
	private static String getCaseMessage(Throwable cause) {
		if (cause instanceof BaseException) {
			ExceptionObject exObj2 = ((BaseException) cause).exObj;
			if (null != exObj2) {
				return exObj2.getLogInfo();
			}
			return cause.getMessage();
		} else {
			return cause.getMessage();
		}
	}
	
	public String getApiMessage(){
		JSONObject json = new JSONObject();
		json.put("code", exObj.getCode());
		json.put("message", exObj.getLogInfo());
		return json.toJSONString();
	}
}
