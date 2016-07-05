package com.caitu99.service.integral.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.integral.dao.AutoFindRuleMapper;
import com.caitu99.service.integral.domain.AutoFindRule;
import com.caitu99.service.integral.service.AutoFindRuleService;

@Service
public class AutoFindRuleServiceImpl implements AutoFindRuleService {

	@Autowired
	private AutoFindRuleMapper autoFindRuleMapper;

	@Override
	public AutoFindRule selectByManualId(Long manualId) {
		AutoFindRule autoFindRule = new AutoFindRule();
		autoFindRule.setManualId(manualId);
//		autoFindRule.setStatus(AutoFindRule.STATUS_NORMAL);
		
		Map<String,Object> map = new HashMap<String,Object>(1);
		map.put("autoFindRule", autoFindRule);
		return autoFindRuleMapper.selectBySelective(map);
	}

	@Override
	public List<AutoFindRule> list() {
		AutoFindRule autoFindRule = new AutoFindRule();
		autoFindRule.setStatus(AutoFindRule.STATUS_NORMAL);

		Map<String,Object> map = new HashMap<String,Object>(1);
		map.put("autoFindRule", autoFindRule);
		return autoFindRuleMapper.list(map);
	}
}
