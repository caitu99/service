package com.caitu99.service.user.service.impl;

import com.caitu99.service.user.dao.ImportMailErrorMapper;
import com.caitu99.service.user.domain.ImportMailError;
import com.caitu99.service.user.service.ImportMailErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImportMailErrorServiceImpl implements ImportMailErrorService {
	@Autowired
	private ImportMailErrorMapper importMailErrorMapper;

	@Override
	public int insert(ImportMailError record) {
		return importMailErrorMapper.insert(record);
	}
	
}
