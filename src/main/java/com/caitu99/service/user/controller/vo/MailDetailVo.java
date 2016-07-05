package com.caitu99.service.user.controller.vo;


import com.caitu99.service.user.domain.MailDetail;

public class MailDetailVo extends MailDetail {

    private Long cardId;

	public Long getCardId() {
		return cardId;
	}

	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}
}