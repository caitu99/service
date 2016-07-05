package com.caitu99.service.sys.service.impl;

import com.caitu99.service.sys.dao.IpInfoMapper;
import com.caitu99.service.sys.domain.IpInfo;
import com.caitu99.service.sys.service.IpInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IpInfoServiceImpl implements IpInfoService {
	@Autowired
	private IpInfoMapper ipInfoMapper;
	@Override
	public List<IpInfo> listByStatus(String status) {
		return ipInfoMapper.listByStatus(status);
	}
	@Override
	public void updateByPrimaryKeySelective(IpInfo ipInfo) {
		ipInfoMapper.updateByPrimaryKeySelective(ipInfo);
	}
	@Override
	public int insertSelective(IpInfo ipInfo) {
		return ipInfoMapper.insertSelective(ipInfo);
	}

}
