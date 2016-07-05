package com.caitu99.service.sys.service;


import com.caitu99.service.sys.domain.Notice;
import com.caitu99.service.sys.domain.Page;

import java.util.List;

public interface NoticeService {

	int insert(Notice record);

	int updateByPrimaryKey(Notice record);

	// 获取所有意见
	List<Notice> listAll(Page page);

	// 获取数据总数
	int countNum();

	// 假删除
	int fDelete(Long id);

	// app接口
	List<Notice> select(Long userid);

}
