package com.caitu99.service.sys.service.impl;

import com.caitu99.service.sys.controller.api.ConfigController;
import com.caitu99.service.sys.dao.SuggestMapper;
import com.caitu99.service.sys.domain.Page;
import com.caitu99.service.sys.domain.Suggest;
import com.caitu99.service.sys.service.SuggestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuggestServiceImpl implements SuggestService {

	@Autowired
	private SuggestMapper suggestMapper;
	
	@Override
	public void insert(Suggest record) {
		try {
			suggestMapper.insert(record);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Suggest> listAll(Page page) {
		return suggestMapper.list(page);
	}

	@Override
	public int countNum() {
		return suggestMapper.countNum();
	}

	@Override
	public int fDelete(Long id) {
		
		return suggestMapper.fDelete(id);
	}
	


	

	
}
