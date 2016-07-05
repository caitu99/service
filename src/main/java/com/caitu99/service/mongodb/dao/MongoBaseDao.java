/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.mongodb.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.caitu99.service.mongodb.util.MongoDBUtils;
import com.mongodb.WriteResult;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MongoBaseDao 
 * @author ws
 * @date 2015年11月26日 上午11:36:56 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Repository("mongoBaseDao")
public class MongoBaseDao implements MongoDao{
	@Autowired 
    protected MongoTemplate mongoTemplate;  
  
    /** 
     * 根据主键id返回对象 
     *  
     * @param id 
     *            唯一标识 
     * @return T 对象 
     */  
    public <T> T findById(Class<T> entityClass, String id) {  
        return this.mongoTemplate.findById(id, entityClass);  
    }  
    
    
	@Override
	public <T> List<T> find(Object queryObject, Class<T> entityClass) throws Exception {
		if(Query.class.equals(queryObject.getClass())){
			return this.mongoTemplate.find((Query)queryObject, entityClass);
		}
		return this.mongoTemplate.find(MongoDBUtils.getQuery(queryObject), entityClass);
	}


	@Override
	public <T> List<T> findPage(int start, int pageSize, Object queryObject,
			Class<T> entityClass) throws Exception {
		Query query = MongoDBUtils.getQuery(queryObject);
		query.skip(start);
		query.limit(pageSize);
		return this.mongoTemplate.find(query, entityClass);
	}
	

	/** 
     * 根据类获取全部的对象列表 
     *  
     * @param entityClass 
     *            返回类型 
     * @return List<T> 返回对象列表 
     */  
    public <T> List<T> findAll(Class<T> entityClass) {  
        return this.mongoTemplate.findAll(entityClass);  
    }  
  
    /** 
     * 删除一个对象 
     *  
     * @param obj 
     *            要删除的Mongo对象 
     */  
    public void remove(Object obj) {  
        this.mongoTemplate.remove(obj);  
    }  
  
    /** 
     * 添加对象 
     *  
     * @param obj 
     *            要添加的Mongo对象 
     */  
    public void add(Object obj) {  
        this.mongoTemplate.insert(obj);  
  
    }  
    
    public <T> void addAll(List<T> list){
    	this.mongoTemplate.insertAll(list);
    }
    
  

	/** 
     * 修改对象 
     *  
     * @param obj 
     *            要修改的Mongo对象 
     */  
    public void saveOrUpdate(Object obj) {  
        this.mongoTemplate.save(obj);  
    }  
  
    /** 
     *  
     * @param entityClass 
     *            查询对象 
     * @param query 
     *            查询条件 
     * @return 
     * @throws Exception 
     */  
    public <T> Long count(Class<T> entityClass, Object queryObject) throws Exception {  
        return this.mongoTemplate.count(MongoDBUtils.getQuery(queryObject), entityClass);  
    }  
    
    public <T> Long count(Class<T> entityClass, Query query) throws Exception {  
        return this.mongoTemplate.count(query, entityClass);  
    }  
    
    /** 
     * 批量插入 
     * @param entityClass 对象类 
     * @param collection  要插入的对象集合 
     */  
    public <T> void addCollection(Class<T> entityClass, Collection<T> collection){  
        this.mongoTemplate.insert(collection, entityClass);  
    }  
  
    public MongoTemplate getMongoTemplate() {  
        return mongoTemplate;  
    }


	@Override
	public <T> WriteResult updateMulti(Object queryObject, Object updateObject,
			Class<T> entityClass) throws Exception {
		return mongoTemplate.updateMulti(MongoDBUtils.getQuery(queryObject), MongoDBUtils.getUpdate(updateObject), entityClass);
	}  
    
}
