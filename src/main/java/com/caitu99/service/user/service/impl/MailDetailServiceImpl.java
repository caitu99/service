package com.caitu99.service.user.service.impl;

import com.caitu99.service.user.dao.MailDetailMapper;
import com.caitu99.service.user.domain.MailDetail;
import com.caitu99.service.user.controller.vo.MailDetailVo;
import com.caitu99.service.user.service.MailDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailDetailServiceImpl implements MailDetailService {
	@Autowired
	private MailDetailMapper mailDetailMapper;
	@Override
	public void saveMails(MailDetail mailDetail) {
		if(mailDetail == null) {
			return;
		}
		mailDetailMapper.insert(mailDetail);
	}
	@Override
	public int insertExt(MailDetailVo record) {
		return mailDetailMapper.insertExt(record);
	}
	@Override
	public List<MailDetail> list() {
		return mailDetailMapper.list();
	}
	@Override
	public int insertWithCardId(MailDetailVo record) {
		return mailDetailMapper.insertWithCardId(record);
	}
}
