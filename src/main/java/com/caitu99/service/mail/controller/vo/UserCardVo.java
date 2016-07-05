package com.caitu99.service.mail.controller.vo;

import java.util.List;

import com.caitu99.service.integral.domain.CardIntegral;
import com.caitu99.service.user.domain.UserCard;

public class UserCardVo {
	private UserCard userCard;
	private List<CardIntegral> cardIntegrals;

	public UserCard getUserCard() {
		return userCard;
	}

	public void setUserCard(UserCard userCard) {
		this.userCard = userCard;
	}

	public List<CardIntegral> getCardIntegrals() {
		return cardIntegrals;
	}

	public void setCardIntegrals(List<CardIntegral> cardIntegrals) {
		this.cardIntegrals = cardIntegrals;
	}
}
