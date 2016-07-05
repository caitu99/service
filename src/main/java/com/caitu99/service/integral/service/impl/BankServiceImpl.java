package com.caitu99.service.integral.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.integral.dao.BankMapper;
import com.caitu99.service.integral.domain.Bank;
import com.caitu99.service.integral.service.BankService;

@Service
public class BankServiceImpl implements BankService {
	
	private final static Logger logger = LoggerFactory.getLogger(BankServiceImpl.class);

	@Autowired
	private BankMapper bankMapper;

	@Override
	public Bank selectByName(String name) {
		return bankMapper.selectByName(name);
	}
}
