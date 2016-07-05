/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.utils.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.exception.AssertUtil;

/** json解析
 * @Description: (类职责详细描述,可空) 
 * @ClassName: JsonResult 
 * @author xiongbin
 * @date 2015年11月17日 上午10:32:17 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class JsonResult {

	/**
	 * 解析json,验证是否成功(默认code=0为成功)
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkResult 
	 * @param result
	 * @return
	 * @date 2015年11月17日 上午10:38:53  
	 * @author xiongbin
	 */
	public static Boolean checkResult(String jsonString){
		return checkResult(jsonString,ApiResultCode.SUCCEED);
	}
	
	/**
	 * 解析json,验证是否成功.手动传入code
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: checkResult 
	 * @param result
	 * @param code
	 * @return
	 * @date 2015年11月17日 上午10:38:53  
	 * @author xiongbin
	 */
	public static Boolean checkResult(String jsonString,Integer code){
		AssertUtil.hasLength(jsonString, "获取数据为空");
		
		Boolean flag = true;
		
		JSONObject json = JSON.parseObject(jsonString);
		if(!code.equals(json.getInteger("code"))){
			flag = false;
		}
		
		return flag;
	}
	
	/**
	 * 解析json,根据key返回数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getResult 
	 * @param jsonString		json
	 * @param key			key
	 * @return
	 * @date 2015年11月17日 上午11:01:47  
	 * @author xiongbin
	 */
	public static String getResult(String jsonString,String key){
		JSONObject json = JSON.parseObject(jsonString);
		return json.getString(key);
	}
	
	/**
	 * 解析json,根据key返回数据.若数据为空是否抛异常
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getResult 
	 * @param jsonString		json
	 * @param key				key
	 * @param flag				是否抛异常
	 * @param message			异常提示语
	 * @return
	 * @date 2015年11月17日 上午11:12:14  
	 * @author xiongbin
	 */
	public static String getResult(String jsonString,String key,Boolean flag,String message){
		String result = getResult(jsonString,key);
		if(flag){
			AssertUtil.hasLength(result, message);
		}
		
		return result;
	}
}
