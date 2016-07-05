package com.caitu99.service.user.service.impl;

import com.caitu99.service.user.dao.ParseMailErrorMapper;
import com.caitu99.service.user.domain.ParseMailError;
import com.caitu99.service.user.service.ParseMailErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParseMailErrorServiceImpl implements ParseMailErrorService {
	
	@Autowired
	private ParseMailErrorMapper parseMailErrorMapper;
	@Override
	public void saveMailError(ParseMailError mailError) {
		parseMailErrorMapper.insert(mailError);
	}
}
