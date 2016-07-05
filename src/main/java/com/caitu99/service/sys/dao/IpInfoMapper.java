package com.caitu99.service.sys.dao;


import com.caitu99.service.sys.domain.IpInfo;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface IpInfoMapper extends IEntityDAO<IpInfo, IpInfo> {
    int deleteByPrimaryKey(Long id);

    int insert(IpInfo record);

    int insertSelective(IpInfo record);

    IpInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(IpInfo record);

    int updateByPrimaryKey(IpInfo record);
    
    List<IpInfo> listByStatus(String status);
    
}