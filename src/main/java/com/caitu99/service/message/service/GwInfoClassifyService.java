package com.caitu99.service.message.service;

import java.util.List;

import com.caitu99.service.message.domain.GwInfoClassify;


public interface GwInfoClassifyService {
	
	/**
	 * 查询所有分类数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: selectPageList 
	 * @return
	 * @date 2016年4月28日 上午11:14:55  
	 * @author xiongbin
	 */
    List<GwInfoClassify> selectPageList();
}
