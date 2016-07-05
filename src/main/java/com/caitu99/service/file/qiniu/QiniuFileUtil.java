/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.file.qiniu;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caitu99.service.file.service.impl.AttachFileServiceImpl;
import com.caitu99.service.utils.Configuration;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

/** 
 * 
 * @Description: (七牛图片操作类) 
 * @ClassName: QiniuFileUtil 
 * @author Hongbo Peng
 * @date 2015年11月25日 下午8:11:07 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class QiniuFileUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(AttachFileServiceImpl.class);
	
	private static final String ACCESS_KEY = "qiniu.access.key";
	private static final String SECRET_KEY = "qiniu.secret.key";
	private static final String BUCKET = Configuration.getProperty("qiniu.bucket", null);
	
	private Auth auth = null;
	private BucketManager bucketManager = null;
	
	private Auth getAuth() throws Exception{
		String accessKey = Configuration.getProperty(ACCESS_KEY, null);
		if(StringUtils.isEmpty(accessKey)){
			logger.error("params qiniu.access.key is not to configure");
			throw new Exception("params qiniu.access.key is not to configure");
		}
		String secretKey = Configuration.getProperty(SECRET_KEY, null);
		if(StringUtils.isEmpty(secretKey)){
			logger.error("params qiniu.secret.key is not to configure");
			throw new Exception("params qiniu.secret.key is not to configure");
		}
		
		if(null == this.auth){
			auth = Auth.create(accessKey, secretKey);
		}
		return auth;
	}
	
	private BucketManager getBucketManager() throws Exception{
		if(null == this.bucketManager){
			bucketManager = new BucketManager(getAuth());
		}
		return bucketManager;
	}
	/**
	 * @Description: (上传图片)  
	 * @Title: upload 
	 * @param file
	 * @param key
	 * @return
	 * @throws Exception
	 * @date 2015年11月25日 下午8:48:49  
	 * @author Hongbo Peng
	 */
	public QiniuUploadRet upload(File file,String key) throws Exception{
	    try {
	    	UploadManager uploadManager = new UploadManager();
	        Response res = uploadManager.put(file, key, getUpToken());
	        QiniuUploadRet ret = res.jsonToObject(QiniuUploadRet.class);
	        logger.info(res.toString());
	        return ret;
	    } catch (QiniuException e) {
	        Response r = e.response;
	        // 请求失败时简单状态信息
	        logger.error(r.toString());
	        throw new Exception(r.toString());
	    }
	}
	
	/**
	 * 上传图片
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: upload 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 * @date 2016年4月26日 下午5:20:44  
	 * @author xiongbin
	 */
	public QiniuUploadRet upload(byte[] data,String key) throws Exception{
	    try {
	    	UploadManager uploadManager = new UploadManager();
	        Response res = uploadManager.put(data, key, getUpToken());
	        QiniuUploadRet ret = res.jsonToObject(QiniuUploadRet.class);
	        logger.info(res.toString());
	        return ret;
	    } catch (QiniuException e) {
	        Response r = e.response;
	        // 请求失败时简单状态信息
	        logger.error(r.toString());
	        throw new Exception(r.toString());
	    }
	}
	
	/**
	 * @Description: (备份图片)  
	 * @Title: copy 
	 * @param key
	 * @param tagKey
	 * @throws Exception
	 * @date 2015年11月25日 下午8:49:09  
	 * @author Hongbo Peng
	 */
	public void copy(String key,String tagKey) throws Exception{
		getBucketManager().copy(BUCKET, key, BUCKET, tagKey);
	}
	
	/**
	 * @Description: (删除图片)  
	 * @Title: remove 
	 * @param key
	 * @throws Exception
	 * @date 2015年11月25日 下午8:49:33  
	 * @author Hongbo Peng
	 */
	public void remove(String key) throws Exception{
		getBucketManager().delete(BUCKET, key);
	}
	
	/**
	 * @Description: (查看文件属性)  
	 * @Title: getFileInfo 
	 * @param key
	 * @return
	 * @throws Exception
	 * @date 2015年11月25日 下午8:49:55  
	 * @author Hongbo Peng
	 */
	public FileInfo getFileInfo(String key) throws Exception{
		return getBucketManager().stat(BUCKET, key);
	}
	
	/**
	 * @Description: (获取上传凭证)  
	 * @Title: getUpToken 
	 * @return
	 * @throws Exception
	 * @date 2015年11月25日 下午8:50:16  
	 * @author Hongbo Peng
	 */
	private String getUpToken() throws Exception{
	    return getAuth().uploadToken(BUCKET, null, 3600, new StringMap()
	            .putNotEmpty("returnBody", "{\"key\": $(key), \"hash\": $(etag), \"width\": $(imageInfo.width), \"height\": $(imageInfo.height)}"));
	}
	
}
