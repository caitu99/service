package com.caitu99.service.sys.dao;


import com.caitu99.service.sys.domain.Version;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface VersionMapper extends IEntityDAO<Version, Version> {
	int deleteByPrimaryKey(Long id);

	int insert(Version record);

	int insertSelective(Version record);

	Version selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Version record);

	int updateByPrimaryKey(Version record);

	List<Version> findall();

	List<Version> findbustatus(Version record);

	/**
	 * 查询最新版本
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findNewest 
	 * @return
	 * @date 2016年5月10日 下午5:42:46  
	 * @author xiongbin
	 */
	Version findNewest();

}