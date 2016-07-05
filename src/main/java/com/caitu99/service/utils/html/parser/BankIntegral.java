package com.caitu99.service.utils.html.parser;

import com.caitu99.service.utils.mail.MailIDVo;
import com.caitu99.service.utils.mail.MailSrc;

public interface BankIntegral {
	public Object getJiFenBasic(Long userId, String mail, Long cardId,
			MailSrc mailSrc, MailIDVo mailIDVo) throws Exception;
}
