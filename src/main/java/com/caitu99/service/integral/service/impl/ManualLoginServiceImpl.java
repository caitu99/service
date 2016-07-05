package com.caitu99.service.integral.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.exception.ManualLoginException;
import com.caitu99.service.integral.dao.ManualLoginMapper;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.exception.AssertUtil;

@Service
public class ManualLoginServiceImpl implements ManualLoginService {
	
	private final static Logger logger = LoggerFactory.getLogger(ManualLoginServiceImpl.class);

	@Autowired
	private ManualLoginMapper manualLoginMapper;

	@Override
	public List<ManualLogin> findListByUserIdManualId(Long userId, Long manualId) throws ManualLoginException{
		try {
			AssertUtil.notNull(userId, "用户ID不能为空");
			AssertUtil.notNull(manualId, "积分账户ID不能为空");
			
			Map<String,Object> map = new HashMap<String,Object>(2);
			map.put("userId", userId);
			map.put("manualId", manualId);
			List<ManualLogin> list = manualLoginMapper.findListByUserIdManualId(map);
			return list;
		} catch (Exception e) {
			logger.error("查询用户登录记录失败:" + e.getMessage(),e);
			throw new ManualLoginException(ApiResultCode.QUERY_MANUAL_LOGIN_ERROR,e.getMessage());
		}
	}

	@Override
	public void insert(ManualLogin record) throws ManualLoginException {
		try {
			AssertUtil.notNull(record, "对象不能为空");
			
			Date now = new Date();
			record.setGmtCreate(now);
			record.setGmtModify(now);
			
			manualLoginMapper.insert(record);
		} catch (Exception e) {
			logger.error("新增手动账户登录记录失败:" + e.getMessage(),e);
			throw new ManualLoginException(ApiResultCode.INSERT_MANUAL_LOGIN_ERROR,e.getMessage());
		}
	}

	@Override
	public void updateByPrimaryKeySelective(ManualLogin record) {
		try {
			AssertUtil.notNull(record, "对象不能为空");
			AssertUtil.notNull(record.getId(), "对象ID不能为空");
			
			Date now = new Date();
			record.setGmtModify(now);
			
			if(null == record.getStatus()){//20151218 add by chencheng status字段给予默认值
				record.setStatus(1);
			}
			
			manualLoginMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			logger.error("修改手动账户登录记录失败:" + e.getMessage(),e);
			throw new ManualLoginException(ApiResultCode.UPDATE_MANUAL_LOGIN_ERROR,e.getMessage());
		}
	}

	@Override
	public ManualLogin getBySelective(ManualLogin record) {
		try {
			AssertUtil.notNull(record, "对象不能为空");
			
			return manualLoginMapper.getBySelective(record);
		} catch (Exception e) {
			logger.error("查询手动账户登录记录失败:" + e.getMessage(),e);
			throw new ManualLoginException(ApiResultCode.QUERY_MANUAL_LOGIN_ERROR,e.getMessage());
		}
	}
	
	@Override
	public void insertORupdate(ManualLogin record) throws ManualLoginException {
		try {
			AssertUtil.notNull(record, "对象不能为空");
			
			if(null == record.getId()){
				manualLoginMapper.insert(record);
			}else{
				record.setGmtCreate(null);
				if(null == record.getStatus()){//20151218 add by chencheng status字段给予默认值
					record.setStatus(1);
				}
				manualLoginMapper.updateByPrimaryKeySelective(record);
			}
		} catch (Exception e) {
			logger.error("新增或修改手动账户登录记录失败:" + e.getMessage(),e);
			throw new ManualLoginException(ApiResultCode.INSERT_MANUAL_LOGIN_ERROR,e.getMessage());
		}
	}

	@Override
	public List<ManualLogin> selectPageList(Integer start, Integer pageSize) {
		try {
			if(null == start || null == pageSize){
				logger.debug("查询列表失败:参数start或pageSize不能为空");
				return new ArrayList<ManualLogin>();
			}
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("start", start);
			map.put("pageSize", pageSize);
			
			return manualLoginMapper.selectPageList(map);
		} catch (Exception e) {
			logger.error("查询列表失败:" + e.getMessage(),e);
			return new ArrayList<ManualLogin>();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.integral.service.ManualLoginService#selectAccountForUpdate()
	 */
	@Override
	public List<ManualLogin> selectAccountForUpdate() {
		
		return manualLoginMapper.selectAccountForUpdate();
	}

	@Override
	public List<ManualLogin> selectByUserId(Long userId) {
		try {
			if(null == userId){
				logger.debug("根据userId查询用户记录失败:参数userId不能为空");
				return new ArrayList<ManualLogin>();
			}
			
			return manualLoginMapper.selectByUserId(userId);
		} catch (Exception e) {
			logger.error("根据userId查询用户记录失败:" + e.getMessage(),e);
			return new ArrayList<ManualLogin>();
		}
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.integral.service.ManualLoginService#selectByUserIdForBatch(java.lang.Long)
	 */
	@Override
	public List<ManualLogin> selectByUserIdForBatch(Long userId) {
		return manualLoginMapper.selectByUserIdForBatch(userId);
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.integral.service.ManualLoginService#findByUserIdManualId(java.lang.Long, java.lang.Long, java.lang.String)
	 */
	@Override
	public List<ManualLogin> findByUserIdManualId(Long userId, Long manualId,
			String account) {
		Map<String,Object> map = new HashMap<String,Object>(2);
		map.put("userId", userId);
		map.put("manualId", manualId);
		map.put("account", account);
		List<ManualLogin> list = manualLoginMapper.findByUserIdManualId(map);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.integral.service.ManualLoginService#findByUserIdManualId(java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<ManualLogin> findByUserIdManualId(Long userid, Long manualId) {
		Map<String,Object> map = new HashMap<String,Object>(2);
		map.put("userId", userid);
		map.put("manualId", manualId);
		List<ManualLogin> list = manualLoginMapper.findByUserIdManualId2(map);
		return list;
	}
}
