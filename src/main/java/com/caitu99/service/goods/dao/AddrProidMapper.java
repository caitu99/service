package com.caitu99.service.goods.dao;

import com.caitu99.service.goods.domain.AddrProid;

import java.util.List;

public interface AddrProidMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AddrProid record);

    int insertSelective(AddrProid record);

    AddrProid selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AddrProid record);

    int updateByPrimaryKey(AddrProid record);

    List<String> getAddressByItemId(Long itemid);

    List<String> getProidByOrdernoItemid(Long itemid,String orderno);
}