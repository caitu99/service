/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mongodb.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.caitu99.service.push.model.PushMessage;


/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: QueryParamsUtils 
 * @author Hongbo Peng
 * @date 2015年12月22日 上午11:56:33 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class MongoDBUtils {

	public static Query getQuery(Object obj) throws Exception{
		Field[] filed = obj.getClass().getDeclaredFields();
		
		Criteria criteria = new Criteria();
		for (Field field : filed) {
			if("serialVersionUID".equals(field.getName())){
				continue;
			}
			String fieldName = field.getName();
			String firstLetter = fieldName.substring(0, 1).toUpperCase();      
		    String getter = "get" + firstLetter + fieldName.substring(1);      
		    Method method = obj.getClass().getMethod(getter, new Class[] {});      
		    Object value = method.invoke(obj, new Object[] {});
			if(null == value){
				continue;
			}
			criteria.and(fieldName).is(value);
		}
		Query query = new Query(criteria);
		System.out.println(query);
		return query;
	}
	
	public static Update getUpdate(Object obj) throws Exception{
		Field[] filed = obj.getClass().getDeclaredFields();
		Update update = new Update();
		for (Field field : filed) {
			if("serialVersionUID".equals(field.getName())){
				continue;
			}
			String fieldName = field.getName();
			String firstLetter = fieldName.substring(0, 1).toUpperCase();      
		    String getter = "get" + firstLetter + fieldName.substring(1);      
		    Method method = obj.getClass().getMethod(getter, new Class[] {});      
		    Object value = method.invoke(obj, new Object[] {});
			if(null == value){
				continue;
			}
			update.set(fieldName, value);
		}
		return update;
	}
	
	public static void main(String[] args) {
		PushMessage push = new PushMessage();
		push.setRedId(101);
		push.setTitle("标题");
		try {
			MongoDBUtils.getQuery( push);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
