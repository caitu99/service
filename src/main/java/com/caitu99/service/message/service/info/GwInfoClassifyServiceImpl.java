package com.caitu99.service.message.service.info;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.message.dao.GwInfoClassifyMapper;
import com.caitu99.service.message.domain.GwInfoClassify;
import com.caitu99.service.message.service.GwInfoClassifyService;


@Service
public class GwInfoClassifyServiceImpl implements GwInfoClassifyService {

	private final static Logger logger = LoggerFactory.getLogger(GwInfoClassifyServiceImpl.class);

	@Autowired
	private GwInfoClassifyMapper gwInfoClassifyMapper;

	@Override
	public List<GwInfoClassify> selectPageList() {
		return gwInfoClassifyMapper.selectPageList();
	}
}
