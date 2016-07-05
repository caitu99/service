package com.caitu99.service.file.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.AppConfig;
import com.caitu99.service.file.dao.AttachFileMapper;
import com.caitu99.service.file.domain.AttachFile;
import com.caitu99.service.file.qiniu.QiniuFileUtil;
import com.caitu99.service.file.service.AttachFileService;
import com.caitu99.service.utils.SpringContext;
import com.qiniu.storage.model.FileInfo;

/** 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AttachFileServiceImpl 
 * @author xiongbin
 * @date 2015年11月24日 下午4:18:49 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class AttachFileServiceImpl implements AttachFileService {

	private final static Logger logger = LoggerFactory.getLogger(AttachFileServiceImpl.class);
	
	@Autowired
	private AttachFileMapper attachFileMapper;

	@Override
	public List<AttachFile> finlPageList(AttachFile attachFile) {
		List<AttachFile> list = new ArrayList<AttachFile>();
		
		try {
			if(null == attachFile){
				return list;
			}
			
			attachFile.setStatus(AttachFile.STATUS_NORMAL);
			list = attachFileMapper.selectPageList(attachFile);
			
			return list;
		} catch (Exception e) {
			logger.error("查询图片出错:" + e.getMessage(),e);
			return list;
		}
	}
	
	@Override
	public List<AttachFile> finlPageListByItemId(Long ItemId) {
		List<AttachFile> list = new ArrayList<AttachFile>();
		
		try {
			String url = SpringContext.getBean(AppConfig.class).fileUrl;
			String imageBig = SpringContext.getBean(AppConfig.class).imageFileBig;
			
			if(null == ItemId){
				return list;
			}
			
			AttachFile attachFile = new AttachFile();
			attachFile.setTableName("t_item");
			attachFile.setTableId(ItemId);
			attachFile.setCode("GOOD");
			
			list = finlPageList(attachFile);
			
			for(AttachFile a : list){
				String path = a.getPath();
				if(StringUtils.isNotBlank(path)){
					a.setPath(url + path + imageBig);
				}
			}
			
			return list;
		} catch (Exception e) {
			logger.error("查询图片出错:" + e.getMessage(),e);
			return list;
		}
	}
	
	@Override
	public String backUpImage(String path) {
		StringBuilder builder = new StringBuilder();
		try {
			int max=9999;
	        int min=1000;
	        Random random = new Random();
	        int s = random.nextInt(max)%(max-min+1) + min;
			String fileName = path.substring(path.lastIndexOf("."));
			builder.append("BACKUP/")
				.append(new Date().getTime())
				.append("/")
				.append(s)
				.append(fileName);
			QiniuFileUtil util = new QiniuFileUtil();
			String subPath = path.substring(1);
			FileInfo fileInfo = util.getFileInfo(subPath);
			if(null == fileInfo){
				logger.error("image is not exists, path:{}",path);
				return null;
			}
			util.copy(subPath, builder.toString());
			return "/" + builder.toString();
		} catch (Exception e) {
			logger.error("back up image error {},{}",path,e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
