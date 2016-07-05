package com.caitu99.service.user.service;


import com.caitu99.service.user.domain.XUserCard;

import java.util.List;

public interface XUserCardService {

	List<XUserCard> selectByCardno(String cardNo);

	Long insertOrUpdate(XUserCard xUserCard);

	int updateByPrimaryKeySelective(XUserCard record);

	int updateByPrimaryKey(XUserCard record);

}
