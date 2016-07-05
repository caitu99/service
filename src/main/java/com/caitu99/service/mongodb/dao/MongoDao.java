/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mongodb.dao;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.WriteResult;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: BaseDao 
 * @author ws
 * @date 2015年11月26日 上午11:34:40 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface MongoDao {
	  
    <T> T findById(Class<T> entityClass, String id);  
    
    <T> List<T> find(Object queryObject,Class<T> entityClass)throws Exception;
    
    <T> List<T> findPage(int start,int pageSize,Object queryObject,Class<T> entityClass) throws Exception;
  
    <T> List<T> findAll(Class<T> entityClass);  
  
    void remove(Object obj);  
  
    void add(Object obj);  
    
    <T> void addAll(List<T> list);
  
    void saveOrUpdate(Object obj); 
    /**
     * @Description: (批量修改数据)  
     * @Title: updateMulti 
     * @param queryObject 修改条件对象
     * @param updateObject 修改值对象
     * @param entityClass
     * @return
     * @throws Exception
     * @date 2015年12月22日 下午3:23:58  
     * @author Hongbo Peng
     */
    <T> WriteResult updateMulti(Object queryObject,Object updateObject,Class<T> entityClass) throws Exception;
    
    <T> Long count(Class<T> entityClass, Object queryObject) throws Exception;
    
    <T> Long count(Class<T> entityClass, Query query) throws Exception;
}
