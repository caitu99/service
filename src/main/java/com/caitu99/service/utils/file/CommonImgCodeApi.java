/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.utils.file;

import org.apache.commons.lang.StringUtils;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: CommonImgCodeApi 
 * @author ws
 * @date 2015年12月19日 下午1:54:50 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class CommonImgCodeApi {
	
	/**
	 * 统一的图片验证码识别
	 * 优先使用ShowApi识别
	 * 否则使用JuHeApi识别
	 * 
	 * null if fail
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: recognizeImgCodeFromStr 
	 * @param base64EncodedStr
	 * @return null if fail
	 * @date 2015年12月19日 下午1:56:44  
	 * @author ws
	 */
	public static String recognizeImgCodeFromStr(String base64EncodedStr){
		String imgCode = ShowImgApi.recognizeImgCodeFromStr(base64EncodedStr);
		if(StringUtils.isBlank(imgCode)){
			imgCode = JuHeImgApi.recognizeImgCodeFromStr(base64EncodedStr);
		}
		return imgCode;
	}
	
}
