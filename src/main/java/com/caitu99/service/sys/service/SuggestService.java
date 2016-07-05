package com.caitu99.service.sys.service;


import com.caitu99.service.sys.domain.Page;
import com.caitu99.service.sys.domain.Suggest;

import java.util.List;

public interface SuggestService {
	//添加意见反馈
	void insert(Suggest record);
	
	//获取所有意见
	List<Suggest> listAll(Page page);
	
	// 获取数据总数
	int countNum();
	
	//假删除
	 int fDelete(Long id);
	 
	 
}
