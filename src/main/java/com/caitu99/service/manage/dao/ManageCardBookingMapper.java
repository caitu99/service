package com.caitu99.service.manage.dao;

import com.caitu99.service.manage.domain.ManageCardBooking;

public interface ManageCardBookingMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ManageCardBooking record);

    int insertSelective(ManageCardBooking record);

    ManageCardBooking selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ManageCardBooking record);

    int updateByPrimaryKey(ManageCardBooking record);
}