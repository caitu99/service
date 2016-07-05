package com.caitu99.service.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

public class ApiResult<T> {
	

	private int code;
	private String message = "";
	private T data;
	
	public ApiResult() {

    }

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public ApiResult(int code, T data) {
		this.code = code;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public void set(int code,String message){
		this.setCode(code);
		this.setMessage(message);
	}
	
	public void set(int code,String message,T data){
		this.setCode(code);
		this.setMessage(message);
		this.setData(data);
	}
	
	public String toJSONString(int code,String message){
		this.set(code, message);
		return this.toString();
	}
	
	public String toJSONString(int code,String message,T data){
		this.set(code, message,data);
		return this.toString();
	}
	
	@SuppressWarnings("rawtypes")
	public String toJSONString(int code,String message,T data,Class c,String...fields){
		this.set(code, message,data);
		SimplePropertyPreFilter filter = new SimplePropertyPreFilter(c,fields);
		return this.toString(filter);
	}
	
	public String toJSONString(int code,String message,T data,SimplePropertyPreFilter... filter){
		this.set(code, message,data);
		return this.toString(filter);
	}

	@Override
	public String toString() {
        return JSON.toJSONString(this);
    }

	public String toString(SimplePropertyPreFilter filter) {
        return JSON.toJSONString(this,filter);
    }

	public String toString(SimplePropertyPreFilter... filter) {
        return JSON.toJSONString(this,filter);
    }
	
	public static String outSucceed(Integer code,String message){
		JSONObject json = new JSONObject();
		json.put("code", code);
		json.put("message", message);
		
		return json.toJSONString();
	}
}
