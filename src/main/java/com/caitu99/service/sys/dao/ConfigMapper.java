package com.caitu99.service.sys.dao;

import com.caitu99.service.sys.domain.Config;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface ConfigMapper extends IEntityDAO<Config, Config> {
	int deleteByPrimaryKey(Long id);

	int insert(Config record);

	int insertSelective(Config record);

	Config selectByPrimaryKey(Long id);

	List<Config> getAll();

	int updateByPrimaryKeySelective(Config record);

	int updateByPrimaryKey(Config record);

	Config selectByKey(String key);
}