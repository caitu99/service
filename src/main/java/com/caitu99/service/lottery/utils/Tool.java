package com.caitu99.service.lottery.utils;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;


public class Tool {

		//UTF－8编码方法
		public static String UTF8String (String param){
		   //String param1 = param;
		   String utf8Str = "";
		   try {
			   utf8Str = java.net.URLEncoder.encode(StringUtils.isBlank(param)?"":param, "UTF-8");
		   } catch (UnsupportedEncodingException e) {
			   e.printStackTrace();
		   }
		   return utf8Str;
	   }
	   //UTF－8解码方法
	   public static String UTF8Decoder (String param){
		   String utf8Str = "";
		   try {
			   if(!StringUtils.isBlank(param)){
				   utf8Str = java.net.URLDecoder.decode(param, "UTF-8");
			   }
		   } catch (UnsupportedEncodingException e) {
			   e.printStackTrace();
		   }
		   return utf8Str;
	   }
	
	
}
