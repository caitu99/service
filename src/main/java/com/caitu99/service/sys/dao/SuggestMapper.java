package com.caitu99.service.sys.dao;


import com.caitu99.service.sys.domain.Page;
import com.caitu99.service.sys.domain.Suggest;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface SuggestMapper extends IEntityDAO<Suggest, Suggest> {
    int deleteByPrimaryKey(Long id);

    void insert(Suggest record);

    int insertSelective(Suggest record);

    Suggest selectByPrimaryKey(Long id);
    List<Suggest> selectByUserId(Long userId);

    int updateByPrimaryKeySelective(Suggest record);

    int updateByPrimaryKey(Suggest record);
    
    List<Suggest> list(Page page);
    
    int countNum();

    int fDelete(Long id);
}