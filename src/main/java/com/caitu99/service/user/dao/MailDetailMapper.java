package com.caitu99.service.user.dao;

import com.caitu99.service.user.domain.MailDetail;
import com.caitu99.service.user.controller.vo.MailDetailVo;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface MailDetailMapper extends IEntityDAO<MailDetail, MailDetail> {
    int deleteByPrimaryKey(Long id);

    int insert(MailDetail record);
    
    int insertWithCardId(MailDetailVo record);
    
    int insertExt(MailDetailVo record);
    
    int insertSelective(MailDetail record);

    MailDetail selectByPrimaryKey(Long id);
    List<MailDetail> list();

    int updateByPrimaryKeySelective(MailDetail record);

    int updateByPrimaryKeyWithBLOBs(MailDetail record);

    int updateByPrimaryKey(MailDetail record);
}