package com.caitu99.service.user.service.impl;

import com.caitu99.service.user.dao.XUserCardMapper;
import com.caitu99.service.user.domain.XUserCard;
import com.caitu99.service.user.service.XUserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XUserCardServiceImpl implements XUserCardService {

	@Autowired
	private XUserCardMapper xUserCardMapper;

	@Override
	public Long insertOrUpdate(XUserCard xUserCard) {
		List<XUserCard> xUserCards = xUserCardMapper.selectByCardno(xUserCard
				.getCardNo());
		if (xUserCards.size() >= 1) {
			xUserCardMapper.updateByPrimaryKeySelective(xUserCard);
		} else {
			xUserCardMapper.insert(xUserCard);
		}
		return xUserCard.getId();
	}

	@Override
	public int updateByPrimaryKeySelective(XUserCard record) {
		return xUserCardMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(XUserCard record) {
		return xUserCardMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<XUserCard> selectByCardno(String cardNo) {
		return xUserCardMapper.selectByCardno(cardNo);
	}
}
