package com.caitu99.service.manage.dao;

import com.caitu99.service.manage.domain.ManageCardOnLineRecord;

public interface ManageCardOnLineRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ManageCardOnLineRecord record);

    int insertSelective(ManageCardOnLineRecord record);

    ManageCardOnLineRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ManageCardOnLineRecord record);

    int updateByPrimaryKey(ManageCardOnLineRecord record);
}