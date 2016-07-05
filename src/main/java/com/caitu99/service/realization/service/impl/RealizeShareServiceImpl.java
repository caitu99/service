package com.caitu99.service.realization.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.exception.RealizeException;
import com.caitu99.service.realization.dao.RealizeShareMapper;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.domain.RealizeShare;
import com.caitu99.service.realization.domain.RealizeShareRecord;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.realization.service.RealizeShareRecordService;
import com.caitu99.service.realization.service.RealizeShareService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
@Service
public class RealizeShareServiceImpl implements RealizeShareService {
	
	private Logger logger = LoggerFactory.getLogger(RealizeShareServiceImpl.class);

	@Autowired
	private RealizeShareMapper realizeShareMapper;
	
	@Autowired
	private RealizeService realizeService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RealizeShareRecordService realizeShareRecordService;

	@Override
	public Long generate(Long userid,Long realizeRecordId) {
		RealizeRecord realizeRecord = realizeService.selectById(realizeRecordId);
		if(null == realizeRecord){
			logger.info("realizeRecordId:{},没有变现记录",realizeRecordId);
		}else if(realizeRecord.getStatus().intValue() != 2 && realizeRecord.getStatus().intValue() != 3){
			logger.info("realizeRecordId:{},Status:{},该变现记录未成功",realizeRecordId,realizeRecord.getStatus());
		}else if(!realizeRecord.getUserId().equals(userid)){
			logger.info("realizeRecordId:{},userid:{},该变现记录不属于该用户",realizeRecordId,userid);
		}else{
			RealizeShare realizeShare = this.isExist(userid, realizeRecordId);
			if(null != realizeShare){
				logger.info("realizeRecordId:{},userid:{},该变现记录已分享过",realizeRecordId,userid);
				return realizeShare.getId();
			}
			
			return this.insert(userid, realizeRecordId);
		}
		
		return null;
	}

	@Override
	public Long insert(Long userid, Long realizeRecordId) {
		RealizeShare record = new RealizeShare();
		Date now = new Date();
		record.setGmtCreate(now);
		record.setGmtModify(now);
		record.setRealizeRecordId(realizeRecordId);
		record.setStatus(1);
		record.setUserId(userid);
		
		realizeShareMapper.insertSelective(record);
		
		return record.getId();
	}

	@Override
	public List<RealizeShare> selectPageList(RealizeShare realizeShare) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("realizeShare", realizeShare);
		
		return realizeShareMapper.selectPageList(map);
	}

	@Override
	public RealizeShare isExist(Long userId, Long realizeRecordId) {
		RealizeShare realizeShare = new RealizeShare();
		realizeShare.setUserId(userId);
		realizeShare.setRealizeRecordId(realizeRecordId);
		realizeShare.setStatus(1);
		
		List<RealizeShare> list = this.selectPageList(realizeShare);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		
		return null;
	}

	@Override
	public Long receive(String phone, Long realizeShareId,Integer platform) {
		Long money = null;
		
		RealizeShare realizeShare = realizeShareMapper.selectByPrimaryKey(realizeShareId);
		if(null != realizeShare){
			User user = new User();
			user.setMobile(phone);
			user = userService.isExistMobile(user);
			if(null == user){
				logger.info("手机号码:{},未注册过,自动注册");
				user = userService.register(phone);
			}
			
			Long userId = user.getId();

			//判断用户是否领取过分享
			money = realizeShareRecordService.isExist(userId, realizeShareId, platform);
			if(null != money){
				logger.info("userid:{},已领取过分享,platform:{},realizeShareId:{}",userId,platform,realizeShareId);
			}else{
				boolean isFirst = realizeShareRecordService.isFirst(userId);
				
				Integer type = 2;
				Long shareRealizeRecordId = realizeShare.getRealizeRecordId();
	        	RealizeRecord shareRealizeRecord = realizeService.selectById(shareRealizeRecordId);
	        	if(null == shareRealizeRecord){
	        		throw new RealizeException(-1, "变现记录不存在");
	        	}
				String shareMemo = shareRealizeRecord.getMemo();
	        	JSONObject shareJsonMemo = JSON.parseObject(shareMemo);
	      		String shareVersion = shareJsonMemo.getString("versionLong");
	      		if(!StringUtils.isBlank(shareVersion) && "3000000".compareTo(shareVersion) <= 0){
	      			type = 2;
	      		}else{
	      			type = 1;
	      		}
				//判断用户是否登录过APP
				if((null == user.getLoginCount() || user.getLoginCount().intValue() < 1) && isFirst){
					logger.info("userid:{},首次领取分享,platform:{},realizeShareId:{}",userId,platform,realizeShareId);
					money = 500L;
					realizeShareRecordService.insert(userId, realizeShareId, money, platform,type);
				}else{
					logger.info("userid:{},领取分享,platform:{},realizeShareId:{}",userId,platform,realizeShareId);
					//用户已登录过APP
					money = RealizeShareRecord.shareAlgorithm();
					realizeShareRecordService.insert(userId, realizeShareId, money, platform,type);
				}
			}
		}else{
			logger.info("没有此分享记录,realizeShareId:{}",realizeShareId);
		}

		return money;
	}
	
	@Override
    public RealizeShare selectByPrimaryKey(Long id){
    	return realizeShareMapper.selectByPrimaryKey(id);
    }
}
