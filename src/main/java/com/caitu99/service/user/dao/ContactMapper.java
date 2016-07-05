package com.caitu99.service.user.dao;

import com.caitu99.service.user.domain.Contact;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface ContactMapper extends IEntityDAO<Contact, Contact> {
    int insert(Contact record);

    int insertSelective(Contact record);
    
    int selectCount(Contact record);
    
    List<Contact> selectByMobile1(String mobile);
    List<Contact> selectByMobile2(String mobile);
    
    int updateByPrimaryKeySelective(Contact record);
}