package com.caitu99.service.merchant.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.merchant.dao.PersonnelManageMapper;
import com.caitu99.service.merchant.domain.PersonnelManage;
import com.caitu99.service.merchant.domain.ProxyRelation;
import com.caitu99.service.merchant.service.PersonnelManageService;
import com.caitu99.service.merchant.service.ProxyRelationService;
import com.caitu99.service.merchant.service.ProxyTransactionService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.string.StrUtil;

@Service
public class PersonnelManageServiceImpl implements PersonnelManageService {

	private static final Logger logger = LoggerFactory.getLogger(PersonnelManageServiceImpl.class);
	
	@Autowired
	private PersonnelManageMapper personnelManageMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private ProxyRelationService proxyRelationService;
	@Autowired
	private ProxyTransactionService proxyTransactionService;

	@Override
	public Pagination<PersonnelManage> findPageItem(PersonnelManage personnelManage,Pagination<PersonnelManage> pagination) {
		if(null==personnelManage || null==pagination){
			return pagination;
		}
		personnelManage.setStatus(1);
		Map<String,Object> map = new HashMap<String,Object>(3);
		map.put("personnelManage", personnelManage);
		map.put("start", pagination.getStart());
		map.put("pageSize", pagination.getPageSize());
		
		Integer count = personnelManageMapper.selectPageCount(map);
		List<PersonnelManage> list = personnelManageMapper.selectPageList(map);
		
		for(PersonnelManage p : list){
			p.setRate(p.getRate() + "%");
		}
		
		pagination.setDatas(list);
		pagination.setTotalRow(count);
		
		return pagination;
	}

	@Override
	public PersonnelManage selectByPrimaryKey(Long id) {
		return personnelManageMapper.selectByPrimaryKey(id);
	}

	@Override
	public void insert(PersonnelManage personnelManage) {
		Date now = new Date();
		personnelManage.setGmtCreate(now);
		personnelManage.setGmtModify(now);
		personnelManage.setStatus(1);
		
		personnelManageMapper.insertSelective(personnelManage);
	}

	@Override
	public void update(PersonnelManage personnelManage) {
		Date now = new Date();
		personnelManage.setGmtModify(now);
		
		personnelManageMapper.updateByPrimaryKeySelective(personnelManage);
	}

	@Override
	@Transactional
	public String create(PersonnelManage personnelManage) {
		ApiResult<String> result = new ApiResult<String>();

		ProxyRelation proxyRelationAdmin = proxyRelationService.selectByEmpUserId(personnelManage.getAdminUserId());
		if(null != proxyRelationAdmin && !proxyRelationAdmin.getType().equals(1)){
			return result.toJSONString(-1, "你没有权限添加人员");
		}
		
		User user = new User();
		user.setMobile(personnelManage.getLoginAccount());
		user.setType(3);
		user = userService.selectBySelective(user);
		if(null != user){
			return result.toJSONString(-1, "你创建的登录账号已存在,请重新输入");
		}
		
		user = userService.register(personnelManage.getLoginAccount());
		user.setCity(personnelManage.getCity());
		user.setProvince(personnelManage.getProvince());
		user.setContacts(personnelManage.getContacts());
		user.setType(3);
		user.setPassword(StrUtil.toMD5("123456"));
		user.setNick(personnelManage.getName());
		userService.updateByPrimaryKeySelective(user);
		
		ProxyRelation proxyRelation = new ProxyRelation();
		proxyRelation.setUserId(personnelManage.getAdminUserId());
		proxyRelation.setEmpUserId(user.getId());
		proxyRelation.setRate(personnelManage.getRate());
		proxyRelation.setType(personnelManage.getType());
		proxyRelationService.insert(proxyRelation);
		
		personnelManage.setProxyRelationId(proxyRelation.getId());
		personnelManage.setUserId(user.getId());
		this.insert(personnelManage);
		
		return result.toJSONString(0, "");
	}
	
	@Override
	@Transactional
	public String modify(PersonnelManage personnelManage) {
		ApiResult<String> result = new ApiResult<String>();

		PersonnelManage personnelManageUpdate = this.selectByPrimaryKey(personnelManage.getId());
		if(null == personnelManageUpdate){
			logger.error("代理关系不存在,id:{}",personnelManage.getId());
			return result.toJSONString(-1, "代理关系不存在");
		}
		
		User user = new User();
		user.setMobile(personnelManage.getLoginAccount());
		user.setType(3);
		user = userService.selectBySelective(user);
		if(null != user && !personnelManageUpdate.getLoginAccount().equals(personnelManage.getLoginAccount())){
			return result.toJSONString(-1, "你创建的登录账号已存在,请重新输入");
		}
		
		user = new User();
		user.setId(personnelManageUpdate.getUserId());
		user.setType(3);
		user = userService.selectBySelective(user);
		if(null == user){
			throw new UserNotFoundException(-1, "用户不存在");
		}
		
		this.update(personnelManage);
		
		user.setMobile(personnelManage.getLoginAccount());
		user.setCity(personnelManage.getCity());
		user.setProvince(personnelManage.getProvince());
		user.setContacts(personnelManage.getContacts());
		user.setNick(personnelManage.getName());
		userService.updateByPrimaryKeySelective(user);
		
		ProxyRelation proxyRelation = proxyRelationService.selectByEmpUserId(user.getId());
		if(null == proxyRelation){
			logger.error("代理关系不存在,userid:{},empUserId:{}",personnelManage.getAdminUserId(),user.getId());
			throw new ApiException(-1, "系统异常,请稍后再试");
		}
		
		proxyRelation.setRate(personnelManage.getRate());
		proxyRelation.setType(personnelManage.getType());
		proxyRelationService.update(proxyRelation);
		
		return result.toJSONString(0, "");
	}
	
	@Override
	@Transactional
	public String detele(PersonnelManage personnelManage) {
		ApiResult<String> result = new ApiResult<String>();
		
		PersonnelManage personnelManageDetele = this.selectByPrimaryKey(personnelManage.getId());
		if(null == personnelManageDetele){
			return result.toJSONString(-1, "代理关系不存在");
		}
		
		if(!personnelManageDetele.getAdminUserId().equals(personnelManage.getAdminUserId())){
			return result.toJSONString(-1, "您非此用户的管理员,不可删除");
		}
		
		//判断该用户是否存在未结算积分
		boolean flag = proxyTransactionService.checkSettle(personnelManageDetele.getUserId());
		if(!flag){
			return result.toJSONString(-1, "该用户存在未结算积分，无法删除");
		}
		
		personnelManageDetele.setStatus(-1);
		this.update(personnelManageDetele);
		
		return result.toJSONString(0, "");
	}
}
