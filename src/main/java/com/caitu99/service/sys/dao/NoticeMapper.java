package com.caitu99.service.sys.dao;


import com.caitu99.service.sys.domain.Notice;
import com.caitu99.service.sys.domain.Page;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface NoticeMapper extends IEntityDAO<Notice, Notice> {
	int deleteByPrimaryKey(Long id);

	int insert(Notice record);

	int insertSelective(Notice record);

	Notice selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Notice record);

	int updateByPrimaryKey(Notice record);

	List<Notice> listAll(Page page);

	int countNum();

	int fDelete(Long id);
	
	//app接口
	List<Notice> select(Long userId);
}