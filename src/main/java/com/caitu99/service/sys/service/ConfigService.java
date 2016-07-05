package com.caitu99.service.sys.service;

import com.caitu99.service.sys.domain.Config;

import java.util.List;

public interface ConfigService {
	List<Config> selectAll();

	Config selectByKey(String key);
}
