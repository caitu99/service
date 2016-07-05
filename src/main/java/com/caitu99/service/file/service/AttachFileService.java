package com.caitu99.service.file.service;

import java.util.List;

import com.caitu99.service.file.domain.AttachFile;

public interface AttachFileService {
	
	/**
	 * 根据商品ID查询图片
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: finlPageListByItemId 
	 * @param ItemId
	 * @return
	 * @date 2015年11月25日 上午10:26:54  
	 * @author xiongbin
	 */
	List<AttachFile> finlPageListByItemId(Long ItemId);
	
	/**
	 * 查询图片信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: finlPageList 
	 * @param attachFile
	 * @return
	 * @date 2015年11月25日 上午10:26:54  
	 * @author xiongbin
	 */
	List<AttachFile> finlPageList(AttachFile attachFile);
	
	/**
	 * @Description: (备份图片)  
	 * @Title: backUpImage 
	 * @param path
	 * @return
	 * @date 2015年11月26日 上午11:51:23  
	 * @author Hongbo Peng
	 */
	String backUpImage(String path);
}
