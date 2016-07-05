package com.caitu99.service.sys.service;


import com.caitu99.service.sys.domain.IpInfo;

import java.util.List;

public interface IpInfoService {
	// 获得ip信息
	public List<IpInfo> listByStatus(String status);
	// 更新ip信息
	public void updateByPrimaryKeySelective(IpInfo ipInfo);
	//插入ip信息
	public int insertSelective(IpInfo ipInfo);
}
