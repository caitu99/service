package com.caitu99.service.user.dao;


import com.caitu99.service.user.domain.XUserCard;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface XUserCardMapper extends IEntityDAO<XUserCard, XUserCard> {
	int deleteByPrimaryKey(Integer id);

	Long insert(XUserCard record);

	int insertSelective(XUserCard record);

	XUserCard selectByPrimaryKey(Integer id);

	List<XUserCard> selectByCardno(String cardNo);

	int updateByPrimaryKeySelective(XUserCard record);

	int updateByPrimaryKey(XUserCard record);
}