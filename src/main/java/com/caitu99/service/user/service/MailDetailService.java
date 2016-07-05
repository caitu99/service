package com.caitu99.service.user.service;

import com.caitu99.service.user.domain.MailDetail;
import com.caitu99.service.user.controller.vo.MailDetailVo;

import java.util.List;

public interface MailDetailService {
	void saveMails(MailDetail mailDetail);
	int insertExt(MailDetailVo record);
	List<MailDetail> list();
	int insertWithCardId(MailDetailVo record);
}
