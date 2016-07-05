/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.lottery.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.caitu99.service.AppConfig;
import com.caitu99.service.activities.dao.ActivitiesMapper;
import com.caitu99.service.activities.domain.Activities;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.exception.LotteryException;
import com.caitu99.service.lottery.dao.LotteryOrderMapper;
import com.caitu99.service.lottery.domain.LotteryOrder;
import com.caitu99.service.lottery.dto.LotteryPageDto;
import com.caitu99.service.lottery.service.LotteryMainService;
import com.caitu99.service.lottery.vo.LotteryOrderVo;
import com.caitu99.service.transaction.dao.AccountDetailMapper;
import com.caitu99.service.transaction.dao.AccountMapper;
import com.caitu99.service.transaction.dao.TransactionRecordMapper;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.utils.calculate.CalculateUtils;
import com.caitu99.service.utils.encryption.md5.MD5Util;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: LotteryMain 
 * @author fangjunxiao
 * @date 2016年5月10日 下午5:58:13 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class LotteryMainServiceImpl implements LotteryMainService{

	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private LotteryOrderMapper lotteryOrderMapper;
	
	@Autowired
	private TransactionRecordMapper transactionRecordMapper;
	
	@Autowired
	private AccountDetailMapper accountDetailMapper;
	
	@Autowired
	private ActivitiesMapper activitiesMapper;
	
	@Autowired
	private AccountMapper accountMapper;
	
	
	private final static Logger log = LoggerFactory
			.getLogger(OrderService.class);
	
	
	
	@Override
	public String redirctLotteryUrl(Long userid) throws LotteryException{
		 //查询用户ID与积分
		 String fuserid = userid.toString();
		 String credits="200";
		 
		 String appKey = appConfig.lotteryAppKey;
		 String appSecret = appConfig.lotteryAppSecret;
		 
       Long time=new Date().getTime();
       String timestamp=time.toString();
       Map<String, String> params=new LinkedHashMap<String, String>();
       params.put("appKey", appKey);
       params.put("appSecret", appSecret);
       params.put("credits", credits);
       params.put("fuserid", fuserid);	
       params.put("timestamp", timestamp);
   
       String signString=MD5Util.getInstance().encryptionMD5Around(params, "");

       params.put("sign", signString);
       params.remove("appSecret");
	 
       return this.doGet(params);
	}
	
	private String doGet(Map<String, String> paramMap){
       
		String url = appConfig.lotteryAppUrl  + "?";
		
		if (null == paramMap || paramMap.isEmpty()) {
			return null;
		}
		StringBuffer signbuffer = new StringBuffer();
		signbuffer.append(url);
		String first =  paramMap.keySet().iterator().next();
		for (String key : paramMap.keySet()) {
			if(!key.equals(first)){
				signbuffer.append("&");
			}
			signbuffer.append(key).append("=").append(paramMap.get(key));
		}
       
	    return signbuffer.toString();    
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "LotteryException" })
	public String createLotteryOrder(LotteryOrderVo lotteryOrderVo,Map<String,String> map)
			throws LotteryException {
		try {
			//对彩票业务操作
			//生成彩票订单
			Date date = new Date();
			String orderNo = LotteryOrder.generateNo(Long.parseLong(lotteryOrderVo.getFuserid()));
			LotteryOrder lotteryOrder = new LotteryOrder();
			lotteryOrder.setOrderNo(orderNo);
			lotteryOrder.setUserId(Long.parseLong(lotteryOrderVo.getFuserid()));
			lotteryOrder.setName(lotteryOrderVo.getProduct());
			lotteryOrder.setMarketPrice(Long.parseLong(lotteryOrderVo.getMarketPrice()));
			lotteryOrder.setOutOrderId(lotteryOrderVo.getLotteryOrderId());
			lotteryOrder.setPoints(Long.parseLong(lotteryOrderVo.getPoints()));
			lotteryOrder.setRealPrice(Long.parseLong(lotteryOrderVo.getRealPrice()));
			lotteryOrder.setStatus(0);//生成
			lotteryOrder.setCreateTime(date);
			lotteryOrder.setUpdateTime(date);
			lotteryOrderMapper.insertSelective(lotteryOrder);
			
			 //生成交易记录
			TransactionRecord tsr = new TransactionRecord();
		 	String transactionNumber = "L"+ UUID.randomUUID().toString().replace("-", "");
			tsr.setTransactionNumber(transactionNumber);
			tsr.setUserId(Long.parseLong(lotteryOrderVo.getFuserid()));
			tsr.setOrderNo(orderNo);
			tsr.setInfo("彩票");
			tsr.setType(1);
			//财币+途币
			tsr.setPayType(3);
			//彩票
			tsr.setSource(30);
			//成功
			tsr.setStatus(2);
			tsr.setTotal(Long.parseLong(map.get("total")));
			tsr.setTubi(Long.parseLong(map.get("tubi")));
			tsr.setRmb(0L);
			//10彩票
			tsr.setChannel(10);
			tsr.setComment("投注");
			tsr.setCouponIntegral(0l);
			tsr.setCreateTime(date);
			tsr.setUpdateTime(date);
			transactionRecordMapper.insertSelective(tsr);
			
			//扣款  account_detail
			AccountDetail adTotal = new AccountDetail();
			adTotal.setIntegralChange(Long.parseLong(map.get("total")));
			adTotal.setMemo("彩票财币");
			adTotal.setRecordId(tsr.getId());
			adTotal.setStall("");
			adTotal.setType(1);//财币入分
			adTotal.setUserId(Long.parseLong(lotteryOrderVo.getFuserid()));
			adTotal.setGmtCreate(date);
			adTotal.setGmtModify(date);
			accountDetailMapper.insertSelective(adTotal);
			
			AccountDetail adTubi = new AccountDetail();
			adTubi.setIntegralChange(Long.parseLong(map.get("tubi")));
			adTubi.setMemo("彩票途币");
			adTubi.setRecordId(tsr.getId());
			adTubi.setStall("");
			adTubi.setType(3);//途币入分
			adTubi.setUserId(Long.parseLong(lotteryOrderVo.getFuserid()));
			adTubi.setGmtCreate(date);
			adTubi.setGmtModify(date);
			accountDetailMapper.insertSelective(adTubi);
			
			//账户扣款
			Account account = accountMapper.selectByUserId(Long.valueOf(lotteryOrderVo.getFuserid()));
			
			//扣财币，途币
			Long changeIntegral = account.getTotalIntegral();
			Long tubi = account.getTubi();
			changeIntegral = CalculateUtils.getDifference(changeIntegral, Long.parseLong(map.get("total")));
			tubi = CalculateUtils.getDifference(tubi, Long.parseLong(map.get("tubi")));
			
			Account editAccount = new Account();
			editAccount.setId(account.getId());
			editAccount.setTotalIntegral(changeIntegral);
			editAccount.setAvailableIntegral(changeIntegral);
			editAccount.setTubi(tubi);
			editAccount.setGmtModify(date);
			accountMapper.updateByPrimaryKeySelective(editAccount);
			
			return orderNo;
		} catch (Exception e) {
			log.error("生成彩票订单失败:" + e.getMessage(), e);
			return "";
		}
	
	}

	@Override
	public Map<String,String> selectAndCheckPrice(Long userid)
			throws LotteryException {
		
		Map<String,String> map = new HashMap<String, String>();
		Date date = new Date();
		Activities activities = activitiesMapper.selectByPrimaryKey(3L);//彩票
		Account account = accountMapper.selectByUserId(userid);
		if(null == account || null == account.getTubi() ){
			map.put("code", "-1");
			map.put("message", "财币或途币不足");
			return map;
		}
		if(null == activities || "-1".equals(activities.getStatus())){
			//无活动
				//方案1    195财币  + 5途币
				//验证用户积分 是否足够
			if(account.getTotalIntegral().intValue()<195 || account.getTubi().intValue()<5){
				map.put("code", "-1");
				map.put("message", "财币或途币不足");
				return map;
			}
			
			map.put("code", "1");
			map.put("message", "success");
			map.put("total", "195");
			map.put("tubi", "5");
			map.put("active", "-1");
			return map;
		}
		
		
		Long dt = date.getTime();
		Long st = activities.getStartTime().getTime();
		Long et = activities.getEndTime().getTime();
		if(dt<=et && dt>=st){
			if((date.getHours()>=12 && date.getHours()<=13) || (date.getHours()>=20 && date.getHours()<=21)){
				//方案2    100财币 + 100积分
				//验证用户积分 是否足够
				
				if(account.getTotalIntegral().intValue()<100 || account.getTubi().intValue()<100){
					if(account.getTotalIntegral().intValue()<195 || account.getTubi().intValue()<5){
						map.put("code", "-1");
						map.put("message", "财币或途币不足");
						return map;
					}
					//积分不足时选方案1
					map.put("code", "1");
					map.put("message", "success");
					map.put("total", "195");
					map.put("tubi", "5");
					map.put("active", "0");//有活动但没有参与进活动
					return map;
				}
				
				map.put("code", "1");
				map.put("message", "success");
				map.put("total", "100");
				map.put("tubi", "100");
				map.put("active", "1");
				return map;
				
			}
		}
		
		//方案 1    195财币  + 5途币
		//验证用户积分 是否足够
		if(account.getTotalIntegral().intValue()<195 || account.getTubi().intValue()<5){
			map.put("code", "-1");
			map.put("message", "财币或途币不足");
			return map;
		}
		
		map.put("code", "1");
		map.put("message", "success");
		map.put("total", "195");
		map.put("tubi", "5");
		map.put("active", "0");
		return map;
	}

	@Override
	public boolean checkIsSame(String outOrderId) throws LotteryException {
		int cou = lotteryOrderMapper.checkIsSame(outOrderId);
		if(cou >= 1){
			return true;
		}
		return false;
	}

	@Override
	public void changeLotteryOrderStatus(String orderNo)
			throws LotteryException {
		Date date = new Date();
		LotteryOrder editLotteryOrder = new LotteryOrder();
		editLotteryOrder.setOrderNo(orderNo);
		editLotteryOrder.setStatus(1);	
		editLotteryOrder.setFinishTime(date);
		editLotteryOrder.setUpdateTime(date);
		lotteryOrderMapper.updateByPrimaryKeySelective(editLotteryOrder);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "LotteryException" })
	public boolean failLotteryOrder(String outOrderId) throws LotteryException {
		try {
		LotteryOrder lo = lotteryOrderMapper.selectByOutOrderId(outOrderId);
		if(null == lo || lo.getStatus().intValue() != 0 ){
			return false;
		}
		
		Date date = new Date();
		LotteryOrder lotteryOrder = new LotteryOrder();
		lotteryOrder.setOrderNo(lo.getOrderNo());
		lotteryOrder.setStatus(2);//出票失败
		lotteryOrder.setUpdateTime(date);
		lotteryOrderMapper.updateByPrimaryKeySelective(lotteryOrder);
		
		 //交易记录
		TransactionRecord tsr = transactionRecordMapper.selectByOrderNo(lo.getOrderNo());
		
		TransactionRecord editTr = new TransactionRecord();
		editTr.setOrderNo(lo.getOrderNo());
		tsr.setStatus(-1);//失败
		tsr.setUpdateTime(date);
		transactionRecordMapper.updateByPrimaryKeySelective(tsr);
		
		
		//扣款  account_detail
		AccountDetail adTotal = new AccountDetail();
		adTotal.setIntegralChange(tsr.getTotal());
		adTotal.setMemo("彩票财币");
		adTotal.setRecordId(tsr.getId());
		adTotal.setStall("");
		adTotal.setType(2);//财币出分
		adTotal.setUserId(lo.getUserId());
		adTotal.setGmtCreate(date);
		adTotal.setGmtModify(date);
		accountDetailMapper.insertSelective(adTotal);
		
		AccountDetail adTubi = new AccountDetail();
		adTubi.setIntegralChange(tsr.getTubi());
		adTubi.setMemo("彩票途币");
		adTubi.setRecordId(tsr.getId());
		adTubi.setStall("");
		adTubi.setType(4);//途币入分
		adTubi.setUserId(lo.getUserId());
		adTubi.setGmtCreate(date);
		adTubi.setGmtModify(date);
		accountDetailMapper.insertSelective(adTubi);
		
		
		Account account = accountMapper.selectByUserId(lo.getUserId());
		
		//扣财币，途币
		Long changeIntegral = account.getTotalIntegral();
		Long tubi = account.getTubi();
		changeIntegral = CalculateUtils.add(changeIntegral, tsr.getTotal());
		tubi = CalculateUtils.add(tubi, tsr.getTubi());
		
		Account editAccount = new Account();
		editAccount.setId(account.getId());
		editAccount.setTotalIntegral(changeIntegral);
		editAccount.setAvailableIntegral(changeIntegral);
		editAccount.setTubi(tubi);
		editAccount.setGmtModify(date);
		accountMapper.updateByPrimaryKeySelective(editAccount);
		return true;
		
		} catch (Exception e) {
			log.error("出票失败返充积分失败:{},{}",e.getMessage(), e);
			return false;
		}
	}

	@Override
	public Pagination<LotteryPageDto> findPageByLottery(Long userId,Pagination<LotteryPageDto> pagination)
			throws LotteryException {
		try {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("start", pagination.getStart());
            map.put("pageSize", pagination.getPageSize());
            
            List<LotteryPageDto> list = lotteryOrderMapper.pageByLottery(map);
            Integer cnt = lotteryOrderMapper.pageCount(map);
            pagination.setDatas(list);
            pagination.setTotalRow(cnt);
            return pagination;
        }catch (Exception e){
            log.error("彩票分页查询失败：{},{}",e.getMessage(),e);
            return pagination;
        }
	}

	@Override
	public LotteryOrder getLotteryOrder(String orderNo) throws LotteryException {
		return lotteryOrderMapper.selectByPrimaryKey(orderNo);
	}

	@Override
	public LotteryOrder getLotteryOrderByOutOrderId(String outOrderId)
			throws LotteryException {
		return lotteryOrderMapper.selectByOutOrderId(outOrderId);
	}

}
