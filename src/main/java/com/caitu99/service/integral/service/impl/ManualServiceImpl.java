package com.caitu99.service.integral.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.ManualException;
import com.caitu99.service.integral.controller.spider.ManualQueryAirChina;
import com.caitu99.service.integral.controller.spider.ManualQueryEsurfing;
import com.caitu99.service.integral.dao.ManualMapper;
import com.caitu99.service.integral.domain.Future;
import com.caitu99.service.integral.domain.Manual;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.FutureService;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.integral.service.ManualService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserAuth;
import com.caitu99.service.user.service.UserAuthService;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;

@Service
public class ManualServiceImpl implements ManualService {
	
	private final static Logger logger = LoggerFactory.getLogger(ManualServiceImpl.class);

	@Autowired
	private ManualMapper manualMapper;
	
	@Autowired
	private FutureService futureService;
	
	@Autowired
	private RedisOperate redis;
	
	@Autowired
	private ManualLoginService manualLoginService;
	
	@Autowired
	private ManualQueryEsurfing tianYiQueryInterface;
	
	@Autowired
	private ManualQueryAirChina manualQueryAirChina;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserAuthService userAuthService;

	@Override
	public List<Manual> list() throws ManualException{
		try {
			String key = RedisKey.INTEGRAL_MANUAL_LIST;
			String content = redis.getStringByKey(key);
			
			if(!StringUtils.isEmpty(content)){
				return JSON.parseArray(content, Manual.class);
			}
			
			List<Manual> list = manualMapper.list();
			if(list != null){
				redis.set(key, JSON.toJSONString(list));
			}
			
			return list;
		} catch (Exception e) {
			logger.error("查询积分账户列表失败:" + e.getMessage(),e);
			throw new ManualException(ApiResultCode.QUERY_MANUAL_LIST_ERROR,e.getMessage());
		}
	}
	@Override
	public Manual selectByPrimaryKey(Long id) {
		return manualMapper.selectByPrimaryKey(id);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String,List<Manual>> finlListToSort() throws ManualException{
		try {
			String key = RedisKey.INTEGRAL_MANUAL_LIST_TO_SORT;
			String content = redis.getStringByKey(key);
			
			if(!StringUtils.isEmpty(content)){
				return (Map<String, List<Manual>>) JSON.parseObject(content, Map.class);
			}
			
			List<Manual> list = this.list();
			Map<String,List<Manual>> map = new HashMap<String,List<Manual>>();
			if(list == null){
				return map;
			}
			
			for(Manual manual : list){
				String sort = manual.getSort();
				if(map.containsKey(sort)){
					map.get(sort).add(manual);
				}else{
					List<Manual> manualList = new ArrayList<Manual>();
					manualList.add(manual);
					map.put(sort, manualList);
				}
			}
			
			if(map != null){
				redis.set(key, JSON.toJSONString(map));
			}
			
			return map;
		} catch (ManualException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error("查询积分账户列表,字母排序失败:" + e.getMessage(),e);
			throw new ManualException(ApiResultCode.QUERY_MANUAL_LIST_SORE_ERROR,e.getMessage());
		}
	}
	
	@Override
	public Map<String,Object> generatelogin(Long userId,Long manualId) throws ManualException{
		try {
			/** 从redis获取登录配置--begin */
			String loginParamKey = String.format(RedisKey.INTEGRAL_MANUAL_LOGIN_PARAM_1, manualId);
			String userRecordKey = String.format(RedisKey.INTEGRAL_MANUAL_USER_LOGIN_RECORD_1,userId,manualId);
			
			List<Future> futureListAccount = futureService.findListByManualIdType(manualId,Future.TYPE_LOGIN_ACCOUNT);

			//是否支持手机登录
			boolean isPhoneFuture = false;
			//是否支持身份证登录
			boolean isIdentityFuture = false;
			
			StringBuffer loginAccountMessage = new StringBuffer();
			for(int i=0;i<futureListAccount.size();i++){
				Future future = futureListAccount.get(i);
				loginAccountMessage.append(future.getName());
				if(i+1 != futureListAccount.size()){
					loginAccountMessage.append("/");
				}

				Long futureId = future.getId();
				if(futureId.equals(4L)){
					isPhoneFuture = true;
				}else if(futureId.equals(2L)){
					isIdentityFuture = true;
				}
			}

			Map<String,Object> map = getRedisLoginParam(userId,manualId,loginParamKey,userRecordKey,isPhoneFuture,isIdentityFuture);
			
			if(map != null){
				return map;
			}
			/** 从redis获取登录配置--end */
			
			map = new HashMap<String,Object>();
			
			List<Future> futureList = futureService.findListByManualIdType(manualId,Future.TYPE_LOGIN_VIEW);
			List<Future> futureListPassword = futureService.findListByManualIdType(manualId,Future.TYPE_PASSWORD_ACCOUNT);

			Long[] names = new Long[futureList.size()];
			StringBuffer passwordMessage = new StringBuffer();
			
			for(int i=0;i<futureList.size();i++){
				Future future = futureList.get(i);
				names[i] = future.getId();
			}
			map.put("name", names);
			
			map.put("loginAccountMessage", loginAccountMessage);
			
			for(int i=0;i<futureListPassword.size();i++){
				Future future = futureListPassword.get(i);
				passwordMessage.append(future.getName());
				if(i+1 != futureListPassword.size()){
					passwordMessage.append("/");
				}
			}
			map.put("passwordMessage", passwordMessage);
			
			try {
				//登录配置保存到redis中
				redis.set(loginParamKey, JSON.toJSONString(map));
			} catch (Exception e) {
				logger.error("登录配置保存到redis中失败:" + e.getMessage(),e);
			}
			
			List<ManualLogin> manualLoginList = manualLoginService.findListByUserIdManualId(userId, manualId);
			
			try {
				//用户登录记录保存到redis中
				redis.set(userRecordKey, JSON.toJSONString(manualLoginList));
			} catch (Exception e) {
				logger.error("用户登录记录保存到redis中失败:" + e.getMessage(),e);
			}
			
			//用户信息智能填写
//			autoUserInfo(userId, isPhoneFuture, isIdentityFuture, manualLoginList);
			
			map.put("value", manualLoginList);
			
			return map;
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ManualException(ApiResultCode.GENERATE_LOGIN_CONFIG_ERROR,e.getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.integral.service.ManualService#generateloginWithAccount(java.lang.Long, java.lang.Long, java.lang.String)
	 */
	@Override
	public Map<String, Object> generateloginWithAccount(Long userId,
			Long manualId, String account) {
		try {
			/** 从redis获取登录配置--begin */
			String loginParamKey = String.format(RedisKey.INTEGRAL_MANUAL_LOGIN_PARAM_1, manualId);
			String userRecordKey = String.format(RedisKey.INTEGRAL_MANUAL_USER_LOGIN_RECORD_1,userId,manualId);
			
			List<Future> futureListAccount = futureService.findListByManualIdType(manualId,Future.TYPE_LOGIN_ACCOUNT);

			//是否支持手机登录
			boolean isPhoneFuture = false;
			//是否支持身份证登录
			boolean isIdentityFuture = false;
			
			StringBuffer loginAccountMessage = new StringBuffer();
			for(int i=0;i<futureListAccount.size();i++){
				Future future = futureListAccount.get(i);
				loginAccountMessage.append(future.getName());
				if(i+1 != futureListAccount.size()){
					loginAccountMessage.append("/");
				}

				Long futureId = future.getId();
				if(futureId.equals(4L)){
					isPhoneFuture = true;
				}else if(futureId.equals(2L)){
					isIdentityFuture = true;
				}
			}

			Map<String,Object> map = getRedisLoginParamBy(userId,manualId,loginParamKey,userRecordKey,isPhoneFuture,isIdentityFuture);
			
			if(map != null){
				System.out.println(map.get("value").toString());
				JSONArray jsonRe = JSONArray.parseArray(map.get("value").toString());
				for (int i = 0; i < jsonRe.size(); i++) {
					JSONObject jsonOb = jsonRe.getJSONObject(i);
					ManualLogin manualLogin = JSONObject.parseObject(jsonOb.toJSONString(), ManualLogin.class);
					if(manualLogin.getLoginAccount().equals(account) && StringUtils.isNotBlank(manualLogin.getPassword())){
						map.put("value", manualLogin);
						return map;
					}
				}
				
			}
			/** 从redis获取登录配置--end */
			
			map = new HashMap<String,Object>();
			
			List<Future> futureList = futureService.findListByManualIdType(manualId,Future.TYPE_LOGIN_VIEW);
			List<Future> futureListPassword = futureService.findListByManualIdType(manualId,Future.TYPE_PASSWORD_ACCOUNT);

			Long[] names = new Long[futureList.size()];
			StringBuffer passwordMessage = new StringBuffer();
			
			for(int i=0;i<futureList.size();i++){
				Future future = futureList.get(i);
				names[i] = future.getId();
			}
			map.put("name", names);
			
			map.put("loginAccountMessage", loginAccountMessage);
			
			for(int i=0;i<futureListPassword.size();i++){
				Future future = futureListPassword.get(i);
				passwordMessage.append(future.getName());
				if(i+1 != futureListPassword.size()){
					passwordMessage.append("/");
				}
			}
			map.put("passwordMessage", passwordMessage);
			
			try {
				//登录配置保存到redis中
				redis.set(loginParamKey, JSON.toJSONString(map));
			} catch (Exception e) {
				logger.error("登录配置保存到redis中失败:" + e.getMessage(),e);
			}
			
			List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userId, manualId, account);
			
			try {
				//用户登录记录保存到redis中
				redis.set(userRecordKey, JSON.toJSONString(manualLoginList));
			} catch (Exception e) {
				logger.error("用户登录记录保存到redis中失败:" + e.getMessage(),e);
			}
			
			//用户信息智能填写
			//autoUserInfo(userId, isPhoneFuture, isIdentityFuture, manualLoginList);
			
			if(manualLoginList.size() > 0){
				for (ManualLogin manualLogin : manualLoginList) {
					if(StringUtils.isNotBlank(manualLogin.getPassword())){
						map.put("value", manualLogin);
						return map;
					}
				}
				map.put("value", "");
			}else{
				map.put("value", "");
			}
			
			return map;
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ManualException(ApiResultCode.GENERATE_LOGIN_CONFIG_ERROR,e.getMessage());
		}
	}
	
	@Override
	public Map<String,Object> generatelogin2(Long userId,Long manualId) throws ManualException{
		try {
			/** 从redis获取登录配置--begin */
			String loginParamKey = String.format(RedisKey.INTEGRAL_MANUAL_LOGIN_PARAM_2, manualId);
			String userRecordKey = String.format(RedisKey.INTEGRAL_MANUAL_USER_LOGIN_RECORD_2,userId,manualId);

			Map<String,Object> map = getRedisLoginParam(userId,manualId,loginParamKey,userRecordKey,false,false);
			
			if(map != null){
				return map;
			}
			/** 从redis获取登录配置--end */
			
			map = new HashMap<String,Object>();
			
			List<Future> futureListView = futureService.findListByManualIdType(manualId,Future.TYPE_LOGIN_VIEW);
			List<Future> futureListAccount = futureService.findListByManualIdType(manualId,Future.TYPE_LOGIN_ACCOUNT);

			List<String> names = new ArrayList<String>();
			List<String> accountMessage = new ArrayList<String>();
			
			for(Future future : futureListView){
				Long id = future.getId();
				
				if(id.equals(1L) || id.equals(2L) || id.equals(3L) || id.equals(4L)){
					names.add("loginAccount");
					continue;
				}else if(id.equals(5L)){
					names.add("password");
					continue;
				}else if(id.equals(6L)){
					names.add("imageCode");
					continue;
				}else if(id.equals(7L)){
					names.add("phoneCode");
					continue;
				}
			}
			map.put("name", names);

			for(Future future : futureListAccount){
				accountMessage.add(future.getName());
			}
			map.put("accountMessage", accountMessage);
			
			//登录配置保存到redis中
			redis.set(loginParamKey, JSON.toJSONString(map));
			
			List<ManualLogin> manualLoginList = manualLoginService.findListByUserIdManualId(userId, manualId);
			
			//用户登录记录保存到redis中
			redis.set(userRecordKey, JSON.toJSONString(manualLoginList));
			
			map.put("value", manualLoginList);
			
			return map;
		} catch (ApiException e) {
			logger.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ManualException(ApiResultCode.GENERATE_LOGIN_CONFIG_ERROR,e.getMessage());
		}
	}
	
	/**
	 * 用户信息智能填写
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: autoUserInfo 
	 * @param userId				用户ID
	 * @param isPhoneFuture			是否提示手机号码
	 * @param isIdentityFuture		是否提示身份证
	 * @param manualLoginList		
	 * @date 2016年1月13日 上午10:58:57  
	 * @author xiongbin
	 */
	private void autoUserInfo(Long userId,boolean isPhoneFuture,boolean isIdentityFuture,List<ManualLogin> manualLoginList){
		if(isPhoneFuture){
			User user = userService.selectByPrimaryKey(userId);
			if(null != user){
				String phone = user.getMobile();
				if(StringUtils.isNotBlank(phone)){
					ManualLogin manualLogin = new ManualLogin();
					manualLogin.setLoginAccount(phone);
					if(!manualLoginList.contains(manualLogin)){
						manualLoginList.add(manualLogin);
					}
				}
			}
			
			UserAuth userAuth = userAuthService.findByUserId(userId);
			if(null != userAuth){
				String phone = userAuth.getMobile();
				if(StringUtils.isNotBlank(phone)){
					ManualLogin manualLogin = new ManualLogin();
					manualLogin.setLoginAccount(phone);
					if(!manualLoginList.contains(manualLogin)){
						manualLoginList.add(manualLogin);
					}
				}
			}
		}
		
		if(isIdentityFuture){
			ManualLogin manualLogin = new ManualLogin();
			
			UserAuth userAuth = userAuthService.findByUserId(userId);
			if(null != userAuth){
				String accId = userAuth.getAccId();
				if(StringUtils.isNotBlank(accId)){
					manualLogin.setLoginAccount(accId);
					if(!manualLoginList.contains(manualLogin)){
						manualLoginList.add(manualLogin);
					}
				}
			}
		}
	}
	
	/**
	 * 从redis中获取登录配置和用户登录记录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getRedisLoginParam 
	 * @param userId			用户ID
	 * @param manualId			积分账户ID
	 * @param loginParamKey		登录配置key
	 * @param userRecordKey		用户登录记录key
	 * @return
	 * @date 2015年11月19日 上午11:12:52  
	 * @author xiongbin
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Object> getRedisLoginParam(Long userId,Long manualId,String loginParamKey,
																		String userRecordKey,boolean isPhoneFuture,boolean isIdentityFuture){
		Map<String,Object> map = null;
		
		try {
			String loginParamContent = redis.getStringByKey(loginParamKey);
			String userRecordContent = redis.getStringByKey(userRecordKey);
			
			if(!StringUtils.isEmpty(loginParamContent)){
				map = (Map<String,Object>)JSON.parse(loginParamContent);
				List<ManualLogin> manualLoginList = null;
				if(!StringUtils.isEmpty(userRecordContent)){
					manualLoginList = (List<ManualLogin>)JSON.parse(userRecordContent);
				}else{
					manualLoginList = manualLoginService.findListByUserIdManualId(userId, manualId);
				}
				
				//用户信息智能填写
//				autoUserInfo(userId, isPhoneFuture, isIdentityFuture, manualLoginList);
				
				map.put("value", manualLoginList);
				return map;
			}
		} catch (Exception e) {
			logger.error("从redis取登录配置出错", e);
			return null;
		}
		
		return map;
	}
	
	/**
	 * 从redis中获取登录配置和用户登录记录
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getRedisLoginParam 
	 * @param userId			用户ID
	 * @param manualId			积分账户ID
	 * @param loginParamKey		登录配置key
	 * @param userRecordKey		用户登录记录key
	 * @return
	 * @date 2015年11月19日 上午11:12:52  
	 * @author xiongbin
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Object> getRedisLoginParamBy(Long userId,Long manualId,String loginParamKey,
																		String userRecordKey,boolean isPhoneFuture,boolean isIdentityFuture){
		Map<String,Object> map = null;
		
		try {
			String loginParamContent = redis.getStringByKey(loginParamKey);
			String userRecordContent = redis.getStringByKey(userRecordKey);
			
			if(!StringUtils.isEmpty(loginParamContent)){
				map = (Map<String,Object>)JSON.parse(loginParamContent);
				List<ManualLogin> manualLoginList = null;
				if(!StringUtils.isEmpty(userRecordContent)){
					manualLoginList = (List<ManualLogin>)JSON.parse(userRecordContent);
				}else{
					manualLoginList = manualLoginService.findListByUserIdManualId(userId, manualId);
				}
				
				//用户信息智能填写
				//autoUserInfo(userId, isPhoneFuture, isIdentityFuture, manualLoginList);
				
				map.put("value", JSON.toJSONString(manualLoginList));
				return map;
			}
		} catch (Exception e) {
			logger.error("从redis取登录配置出错", e);
			return null;
		}
		
		return map;
	}
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.integral.service.ManualService#listByVersion(java.lang.String)
	 */
	@Override
	public List<Manual> listByVersion(String version) {
		try {
			String key = RedisKey.INTEGRAL_MANUAL_LIST;
			String content = redis.getStringByKey(key);
			
			if(!StringUtils.isEmpty(content)){
				List<Manual> manualList = JSON.parseArray(content, Manual.class);
				if(StringUtils.isBlank(version)){
					return manualList;
				}else{

					//使用version过滤
					return filterByVersion(manualList,version);
				}
			}else{
				List<Manual> list = manualMapper.list();
				if(list != null){
					redis.set(key, JSON.toJSONString(list));
				}
				if(StringUtils.isBlank(version)){
					return list;
				}else{

					return filterByVersion(list,version);
				}
			}
		} catch (Exception e) {
			logger.error("查询积分账户列表失败:" + e.getMessage(),e);
			throw new ManualException(ApiResultCode.QUERY_MANUAL_LIST_ERROR,e.getMessage());
		}
	}
	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: filterByVersion 
	 * @param manualList
	 * @param version
	 * @date 2015年12月11日 下午6:13:01  
	 * @author ws
	*/
	private List<Manual> filterByVersion(List<Manual> manualList, String version) {
		List<Manual> newManualList = new ArrayList<Manual>();
		
		Long versionLong = getVersionLong(version);
		
		for (Manual manual : manualList) {
			if(manual.getVersion().compareTo(versionLong) <= 0){//20151231 chencheng mod 使用compareTo进行比较
				newManualList.add(manual);
			}
		}
		
		return newManualList;
		
	}
	
	private Long getVersionLong(String version){
		String[] versionArray = version.split("\\.");
		StringBuffer versionStr = new StringBuffer("");
		boolean isFirst = true;
		for (String ver : versionArray) {
			if(isFirst){
				versionStr.append(ver);
				isFirst = false;
				continue;
			}
			
			if(ver.length()==1){
				versionStr.append("00").append(ver);
			}else if(ver.length()==2){
				versionStr.append("0").append(ver);
			}else{
				versionStr.append(ver);
			}
		}
		
		Long versionLong = Long.parseLong(versionStr.toString());
		return versionLong;
	}
	
}
