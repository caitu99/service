package com.caitu99.service.sys.service.impl;

import com.caitu99.service.sys.dao.VersionMapper;
import com.caitu99.service.sys.domain.Version;
import com.caitu99.service.sys.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VersionServiceImpl implements VersionService {
	@Autowired
	private VersionMapper versionMapper;

	@Override
	public int deleteByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		return versionMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(Version record) {
		// TODO Auto-generated method stub
		return versionMapper.insert(record);
	}

	@Override
	public int insertSelective(Version record) {
		// TODO Auto-generated method stub
		return versionMapper.insertSelective(record);
	}

	@Override
	public Version selectByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		return versionMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(Version record) {
		// TODO Auto-generated method stub
		return versionMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(Version record) {
		// TODO Auto-generated method stub
		return versionMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<Version> findall() {
		// TODO Auto-generated method stub
		return versionMapper.findall();
	}

	@Override
	public List<Version> findbustatus(Version record) {
		// TODO Auto-generated method stub
		return versionMapper.findbustatus(record);
	}

	@Override
	public Version findNewest() {
		return versionMapper.findNewest();
	}
}
