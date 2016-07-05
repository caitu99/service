package com.caitu99.service.transaction.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.AppConfig;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.integral.controller.vo.UnionPayOrder;
import com.caitu99.service.integral.dao.BankMapper;
import com.caitu99.service.integral.domain.Bank;
import com.caitu99.service.transaction.constants.TransactionRecordConstants;
import com.caitu99.service.transaction.controller.vo.UnionPaySmartOrder;
import com.caitu99.service.transaction.dao.TransactionRecordMapper;
import com.caitu99.service.transaction.domain.CusTransactionRecord;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.dto.EnterpriseDetailDto;
import com.caitu99.service.transaction.dto.EnterpriseDto;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.TransactionRecordService;

/**
 * Created by Lion on 2015/11/25 0025.
 */

@Service
public class TransactionRecordServiceImpl implements TransactionRecordService {
	private final static Logger logger = LoggerFactory
			.getLogger(TransactionRecordServiceImpl.class);

	@Autowired
	private TransactionRecordMapper transactionRecordMapper;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private BankMapper bankMapper;

	@Override
	public Pagination<CusTransactionRecord> selectTransactionRecoredByUserId(
			Long user_id, Pagination<CusTransactionRecord> pagination,Date start_time,Date end_time) {

		try {
			if (user_id == null || pagination == null) {
				return pagination;
			}
			Map<String, Object> map = new HashMap<>();
			map.put("user_id", user_id);
			map.put("start", pagination.getStart());
			map.put("pageSize", pagination.getPageSize());
			if(start_time != null) map.put("start_time", start_time);
			if(end_time != null) map.put("end_time", end_time);

			List<CusTransactionRecord> list = transactionRecordMapper
					.selectPageListByUserId(map);
			Integer count = transactionRecordMapper
					.selectCountByUserId(map);

			Pattern pattern = Pattern.compile("(.*?)(?=\\(\\d{0,4}\\))");
			// 添加图片前缀和图标地址,转换字符串
			for (CusTransactionRecord cusTransactionRecord : list) {
				String path = cusTransactionRecord.getPicUrl();
				if (path != null) {
					cusTransactionRecord.setPicUrl(appConfig.fileUrl + path);
				}
				Integer type = cusTransactionRecord.getType();
				String typeName = null;
				String bankName = "null";
				if (type == 1) {
					typeName = "消费";
					bankName = "消费";
					Long tubi = cusTransactionRecord.getTubi();
					
					cusTransactionRecord.setTotalstr("-"
							+ cusTransactionRecord.getTotal());
//					cusTransactionRecord.setTubistr("-" + cusTransactionRecord.getTubi());
					cusTransactionRecord.setTubistr(tubi!=null ? tubi.toString() : "0");
					cusTransactionRecord.setRmbstr("-￥" + cusTransactionRecord.getRmb());
					
					cusTransactionRecord.setInfo("fen生活"); //统一为fen生活
				} else if (type == 2) {
					typeName = "兑入";
					String info = cusTransactionRecord.getInfo();
					if (info != null) {
						Matcher matcher = pattern.matcher(info);
						if (matcher.find()) {	//根据银行名称查
							bankName = matcher.group();
							if(cusTransactionRecord.getInfo().equals(bankName+"()"))
							{
								cusTransactionRecord.setInfo(bankName);
							}
						}
						else
						{
							bankName = info;
						}
					}
					else {
						logger.error("兑入类型下，info不能为空");
					}
					cusTransactionRecord.setTotalstr("+"
							+ cusTransactionRecord.getTotal());
					cusTransactionRecord.setTubistr("+" + cusTransactionRecord.getTubi());
					cusTransactionRecord.setRmbstr("+￥" + cusTransactionRecord.getRmb());
				} else if (type == 3) {//充值类型
					//
					String info = cusTransactionRecord.getInfo();
					if (info != null) {
						Matcher matcher = pattern.matcher(info);
						if (matcher.find()) {	//根据银行名称查
							 bankName = matcher.group();
						}
						else if("银联".equals(info))
						{
							bankName = info;
						}
						else if(info.contains("充值券")) {
							bankName = "充值券";
						}
						else {
							logger.error("充值类型下，info不符合要求:{}",info);
						}
					}

					typeName = "充值";
					cusTransactionRecord.setTotalstr("+"
							+ cusTransactionRecord.getTotal());        //钱充值为财币
					
					cusTransactionRecord.setTubistr("+" + cusTransactionRecord.getTubi());
					cusTransactionRecord.setRmbstr("+￥" + cusTransactionRecord.getRmb());
				} else if (type == 4) {
					typeName = "提现";		//根据银行名称查
					String info = cusTransactionRecord.getInfo();
					if (info != null) {
						Matcher matcher = pattern.matcher(info);
						if (matcher.find()) {
							bankName = matcher.group();
						}
						else
						{
							logger.error("有新的银行名称在数据库中不存在:{}",info);
						}
					}

					cusTransactionRecord.setTotalstr("-"
							+ cusTransactionRecord.getTotal());
					cusTransactionRecord.setTubistr("-" + cusTransactionRecord.getTubi());
					cusTransactionRecord.setRmbstr("-￥" + cusTransactionRecord.getRmb());
				} else if (type == 5) {
					typeName = "累积";
					bankName = cusTransactionRecord.getInfo();
					if (bankName == null||"".equals(bankName)) {
						bankName = "活动";
					}
					cusTransactionRecord.setTotalstr("+"
							+ cusTransactionRecord.getTotal());
					cusTransactionRecord.setTubistr("+" + cusTransactionRecord.getTubi());
					cusTransactionRecord.setRmbstr("+￥" + cusTransactionRecord.getRmb());
				}
				else if(type==6)
				{		//不做单独处理
					typeName = "累积";
					bankName = "活动";
					cusTransactionRecord.setTotalstr("+"
							+ cusTransactionRecord.getTotal());
					cusTransactionRecord.setTubistr("+" + cusTransactionRecord.getTubi());
					cusTransactionRecord.setRmbstr("+￥" + cusTransactionRecord.getRmb());
				}
				else if (type == 20) {//银联支付
					bankName = "银联";
					typeName = "支付";
					cusTransactionRecord.setRmbstr("-￥" + cusTransactionRecord.getRmb() * 0.01);
				}
				else if(type == 8)		//转账
				{
					typeName="转账";
					bankName="转账";
                    cusTransactionRecord.setTotalstr("+"
                            + cusTransactionRecord.getTotal());
                    cusTransactionRecord.setTubistr("+" + cusTransactionRecord.getTubi());
                    cusTransactionRecord.setRmbstr("+￥" + cusTransactionRecord.getRmb());
				}
				else if(type == 9)		//转账
				{
					typeName="转账";
					bankName="转账";
                    cusTransactionRecord.setTotalstr("-"
                            + cusTransactionRecord.getTotal());
                    cusTransactionRecord.setTubistr("-" + cusTransactionRecord.getTubi());
                    cusTransactionRecord.setRmbstr("-￥" + cusTransactionRecord.getRmb());
				}else if(type == 10){
					//冻结
					typeName = "兑入";
					String info = cusTransactionRecord.getInfo();
					if(info != null){
						Matcher matcher = pattern.matcher(info);
						if(matcher.find()){	//根据银行名称查
							bankName = matcher.group();
							if(cusTransactionRecord.getInfo().equals(bankName+"()")){
								cusTransactionRecord.setInfo(bankName);
							}
						}else{
							bankName = info;
						}
					}else{
						logger.error("兑入类型下，info不能为空");
					}
					cusTransactionRecord.setTotalstr("+" + cusTransactionRecord.getTotal());
					cusTransactionRecord.setTubistr("+" + cusTransactionRecord.getTubi());
					cusTransactionRecord.setRmbstr("+￥" + cusTransactionRecord.getRmb());
				}
				else if(type==11)
				{		//不做单独处理
					typeName = "支付";
					bankName = "实名";
					cusTransactionRecord.setTotalstr("-"
							+ cusTransactionRecord.getTotal());
					cusTransactionRecord.setTubistr("-" + cusTransactionRecord.getTubi());
					cusTransactionRecord.setRmbstr("-￥" + cusTransactionRecord.getRmb());
				}
				else if(type==12)
				{		//不做单独处理
					typeName = "退款";
					bankName = "实名";
					cusTransactionRecord.setTotalstr("+"
							+ cusTransactionRecord.getTotal());
					cusTransactionRecord.setTubistr("+" + cusTransactionRecord.getTubi());
					cusTransactionRecord.setRmbstr("+￥" + cusTransactionRecord.getRmb());
				}
				else {
					typeName = "未知";
					logger.error("未知的交易类型：{}",type);
				}

				cusTransactionRecord.setTypestr(typeName);	//设置类型对应的字符串
				//设置图标路径
				Bank bank = bankMapper.selectByName(bankName);
				if(bank!=null) {
					cusTransactionRecord.setIconurl(appConfig.staticUrl
							+ bank.getPicUrl());
				}
				else {
					logger.error("未能查询到交易类型图标：{}",bankName);
				}


				Integer ix = cusTransactionRecord.getStatus();
				if (ix == -1) {
					cusTransactionRecord.setStatusstr("失败");
				} else if (ix == 0) {
					cusTransactionRecord.setStatusstr("冻结");
				} else if (ix == 1) {
					cusTransactionRecord.setStatusstr("处理中");
				} else if (ix == 2) {
					cusTransactionRecord.setStatusstr("成功");
				}
				else {
					logger.error("不支持的交易状态：{}",ix);
				}
			}

			pagination.setDatas(list);
			pagination.setTotalRow(count);

			return pagination;
		} catch (Exception e) {
			logger.error("分页查询交易明细失败:" + e.getMessage(), e);
			return pagination;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.caitu99.service.transaction.service.TransactionRecordService#
	 * getTransactionRecord(java.lang.String, java.lang.String)
	 */
	@Override
	public TransactionRecord getTransactionRecord(Long userId, String orderNo) {
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("userId", userId);
		queryMap.put("orderNo", orderNo);
		return transactionRecordMapper.selectByUserIdAndOrderNo(queryMap);

	}

	@Override
	public int updateByPrimaryKey(TransactionRecord record) {

		return transactionRecordMapper.updateByPrimaryKey(record);

	}
	
	@Override
	public void updateByPrimaryKeySelective(TransactionRecord record) {
		transactionRecordMapper.updateByPrimaryKeySelective(record);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.caitu99.service.transaction.service.TransactionRecordService#
	 * selectByOrderNoExludeUserId(java.lang.Long, java.lang.String)
	 */
	@Override
	public TransactionRecord selectByOrderNoExludeUserId(Long userId,
			String orderNo) {
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("userId", userId);
		queryMap.put("orderNo", orderNo);
		return transactionRecordMapper.selectByOrderNoExludeUserId(queryMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.caitu99.service.transaction.service.TransactionRecordService#
	 * countTotalFreezeByUserId(java.lang.Long)
	 */
	@Override
	public Long countTotalFreezeByUserId(Long userId) {
		return transactionRecordMapper.countTotalFreezeByUserId(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.caitu99.service.transaction.service.TransactionRecordService#
	 * selectByUserId(java.lang.Long)
	 */
	@Override
	public List<TransactionRecord> selectByUserIdFreeze(Long userId) {
		// TODO Auto-generated method stub
		return transactionRecordMapper.selectByUserIdFreeze(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.caitu99.service.transaction.service.TransactionRecordService#
	 * selectByUserIdAndTime(java.lang.Long, java.util.Date, java.util.Date)
	 */
	@Override
	public List<TransactionRecord> selectByUserIdAndTime(Long userId,
			Date begin, Date end) {
		Map map = new HashMap();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		map.put("userId", userId);
		map.put("begin", sdf.format(begin));
		map.put("end", sdf.format(end));
		return transactionRecordMapper.selectByUserIdAndTime(map);
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.service.TransactionRecordService#getTransactionRecordByOrderNo(java.lang.String)
	 */
	@Override
	public TransactionRecord getTransactionRecordByOrderNo(String orderNo) {

		return transactionRecordMapper.selectByOrderNo(orderNo);
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.service.TransactionRecordService#saveTransactionRecord(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void saveTransactionRecord(Long userId, Long integral,
			String orderId, String tn, String txnTime) {

		logger.info("新增财分交易记录，用户ID：{}，Type：3，Total：{}，TransactionNumber：{}",userId
					,integral,tn);
		
		TransactionRecord transactionRecord = new TransactionRecord();
		Date dateNow = new Date();
		transactionRecord.setChannel(3);//渠道     1 银联 2 券充值 3 银行卡充值 6财币充值
		//transactionRecord.setComment(String.format("用户userId:%s充值财币%s",userId,integral*100));
		transactionRecord.setComment(txnTime);
		//transactionRecord.setCouponIntegral(couponIntegral);
		transactionRecord.setCreateTime(dateNow);
		//transactionRecord.setFaileTime(faileTime);
		//transactionRecord.setFreezeTime(freezeTime);
		transactionRecord.setInfo("银联");
		transactionRecord.setOrderNo(orderId);
		transactionRecord.setPayType(2);//支付方式（1：财币 2：银行卡）
		//transactionRecord.setPicUrl(picUrl);
		//transactionRecord.setSource(source);
		transactionRecord.setStatus(TransactionRecordConstants.STATUS_DOING);//-1：失败 0：冻结 1：处理中 2：成功
		//transactionRecord.setSuccessTime(successTime);
		transactionRecord.setThirdPartyNumber(tn);
		transactionRecord.setTotal(integral);//正数
		transactionRecord.setTransactionNumber(tn);
		transactionRecord.setType(3);//1:消费 2：兑入3：充值4：提现5：累积6：赠送
		transactionRecord.setUpdateTime(dateNow);
		transactionRecord.setUserId(userId);
		transactionRecordMapper.insert(transactionRecord);
		
	}

	@Override
	public void payTransactionRecord(Long userId, Long rmb,String orderId, String tn, String txnTime,Long caibi,Long tubi) {
		logger.info("银联支付新增交易记录，用户ID：{}，Type：3，rmb：{}，TransactionNumber：{},rmb:{},tubi:{}",userId,rmb,tn,rmb,tubi);
		
		TransactionRecord transactionRecord = new TransactionRecord();
		Date dateNow = new Date();
		transactionRecord.setChannel(3);//渠道     1 银联 2 券充值 3 银行卡充值 6财币充值
		//transactionRecord.setComment(String.format("用户userId:%s充值财币%s",userId,integral*100));
		transactionRecord.setComment(txnTime);
		//transactionRecord.setCouponIntegral(couponIntegral);
		transactionRecord.setCreateTime(dateNow);
		//transactionRecord.setFaileTime(faileTime);
		//transactionRecord.setFreezeTime(freezeTime);
		transactionRecord.setInfo("银联");
		transactionRecord.setOrderNo(orderId);
		transactionRecord.setPayType(2);//支付方式（1：财币 2：银行卡）
		//transactionRecord.setPicUrl(picUrl);
		//transactionRecord.setSource(source);
		transactionRecord.setStatus(TransactionRecordConstants.STATUS_DOING);//-1：失败 0：冻结 1：处理中 2：成功
		//transactionRecord.setSuccessTime(successTime);
		transactionRecord.setThirdPartyNumber(tn);
		transactionRecord.setRmb(rmb);
//		transactionRecord.setTotal(caibi);
//		transactionRecord.setTubi(tubi);
		transactionRecord.setTotal(0L);
		transactionRecord.setTubi(0L);
		transactionRecord.setTransactionNumber(tn);
		transactionRecord.setType(20);//1:消费 2：兑入3：充值4：提现5：累积6：赠送,20:支付
		transactionRecord.setUpdateTime(dateNow);
		transactionRecord.setUserId(userId);
		transactionRecordMapper.insert(transactionRecord);
		
	}

	/**
	 * 
	 */
	@Override
	public List<UnionPayOrder> queryRechargeOrders(Long clientId, String mobile,
			String serialNo, String orderNo) {
		
		Map<String,Object> queryParam = new HashMap<String, Object>();
		queryParam.put("companyId", clientId);//企业编号
		queryParam.put("mobile", mobile);
		queryParam.put("serialNo", serialNo);
		queryParam.put("orderNo", orderNo);
		
		return transactionRecordMapper.selectForUnionPay(queryParam);
		
	}

	@Override
	public Integer countByUserIdAndTime(Long userId, List<Integer> types, Date begin, Date end) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userId", userId);
        queryParams.put("types", types);
        queryParams.put("begin", begin);
        queryParams.put("end", end);

        return transactionRecordMapper.countByUserIdAndTime(queryParams);
    }

    @Override
    public Integer getLastDayConsumes(Long userId) {
        List<Integer> types = new ArrayList<>();
        types.add(1);
        types.add(3);
        types.add(4);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date begin = calendar.getTime();

        Integer integer = this.countByUserIdAndTime(userId, types, begin, new Date());
        if (integer == null) {
            return  0;
        }
        return integer;
    }

	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.service.TransactionRecordService#queryRechargeOrdersByPage(java.lang.Long, java.lang.String, java.lang.String, java.lang.String, com.caitu99.service.base.Pagination)
	 */
	@Override
	public Pagination<UnionPayOrder> queryRechargeOrdersByPage(Long clientId,
			String mobile, String serialNo, String orderNo,
			Pagination<UnionPayOrder> pagination) {
		Map<String,Object> queryParam = new HashMap<String, Object>();
		queryParam.put("companyId", clientId);//企业编号
		queryParam.put("mobile", mobile);
		queryParam.put("serialNo", serialNo);
		queryParam.put("orderNo", orderNo);
		queryParam.put("start", pagination.getStart());
		queryParam.put("pageSize", pagination.getPageSize());
		
		Integer count = transactionRecordMapper.queryRechargeOrdersCount(queryParam);
		List<UnionPayOrder> list = transactionRecordMapper.queryRechargeOrdersByPage(queryParam);

		pagination.setDatas(list);
		pagination.setTotalRow(count);
		
		return pagination;
	}
	@Override
	public Pagination<UnionPaySmartOrder> queryUnionPaySmartOrdersByPage(Long clientId,
			String mobile, String unionNo,Pagination<UnionPaySmartOrder> pagination) {
		Map<String,Object> queryParam = new HashMap<String, Object>();
		queryParam.put("companyId", clientId);//企业编号
		queryParam.put("mobile", mobile);
		queryParam.put("unionNo", unionNo);
		queryParam.put("start", pagination.getStart());
		queryParam.put("pageSize", pagination.getPageSize());
		
		Integer count = transactionRecordMapper.queryUnionPaySmartOrdersCount(queryParam);
		List<UnionPaySmartOrder> list = transactionRecordMapper.queryUnionPaySmartOrdersByPage(queryParam);

		pagination.setDatas(list);
		pagination.setTotalRow(count);
		
		return pagination;
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.service.TransactionRecordService#insert(com.caitu99.service.transaction.domain.TransactionRecord)
	 */
	@Override
	public void insert(TransactionRecord record) {

		logger.info("新增财分交易记录，用户ID：{}，Type：{}，Total：{}，TransactionNumber：{}",record.getUserId()
					,record.getType(),record.getTotal(),record.getTransactionNumber());
		
		transactionRecordMapper.insert(record);
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.service.TransactionRecordService#queryByOrderNos(java.lang.Long, java.lang.String)
	 */
	@Override
	public List<UnionPayOrder> queryByOrderNos(Long clientId, List<String> orderList) {
		
		Map<String,Object> queryParam = new HashMap<String, Object>();
		queryParam.put("companyId", clientId);//企业编号
		queryParam.put("orderList", orderList);
		
		return transactionRecordMapper.queryByOrderNos(queryParam);
		
	}
	
	public List<UnionPaySmartOrder> queryUnionPaySmartOrderByOrderNos(Long clientId, List<String> orderList) {
		
		Map<String,Object> queryParam = new HashMap<String, Object>();
		queryParam.put("companyId", clientId);//企业编号
		queryParam.put("orderList", orderList);
		
		return transactionRecordMapper.queryUnionPaySmartOrderByOrderNos(queryParam);
		
	}


	public Long saveTransaction(TransactionRecordDto transactionRecordDto) {

		logger.info("新增财分交易记录，用户ID：{}，Type：{}，Total：{},TransactionNumber：{}",transactionRecordDto.getUserId()
					,transactionRecordDto.getType(),transactionRecordDto.getTotal()
					,transactionRecordDto.getTransactionNumber());
		TransactionRecord transactionRecord = new TransactionRecord();
		transactionRecord.setChannel(transactionRecordDto.getChannel());
		transactionRecord.setComment(transactionRecordDto.getComment());
		transactionRecord.setCreateTime(new Date());
		transactionRecord.setInfo(transactionRecordDto.getInfo());
		transactionRecord.setOrderNo(transactionRecordDto.getOrderNo());
		transactionRecord.setPayType(1);// 财币
		transactionRecord.setPicUrl(transactionRecordDto.getPicUrl());
		transactionRecord.setStatus(2);// 成功
		transactionRecord.setSuccessTime(new Date());
		transactionRecord.setTotal(transactionRecordDto.getTotal());
		transactionRecord.setTransactionNumber(transactionRecordDto
				.getTransactionNumber());
		transactionRecord.setType(transactionRecordDto.getType());
		transactionRecord.setUpdateTime(new Date());
		transactionRecord.setUserId(transactionRecordDto.getUserId());
		transactionRecord.setSource(transactionRecordDto.getSource());
		transactionRecord.setTubi(transactionRecordDto.getTubi());
		transactionRecord.setRmb(0l);
		//transactionRecord.setCouponIntegral(userCouponPay);
		// 添加交易记录
		transactionRecordMapper.insert(transactionRecord);
		return transactionRecord.getId();
	}

	@Override
	public EnterpriseDto queryEnterpriseTotal() {
		EnterpriseDto result = new EnterpriseDto();
		
		Map<String,Object> map = new HashMap<String, Object>();
		//总数据
		EnterpriseDto total = transactionRecordMapper.enterpriseTotalInfo(map);
		result.setCountA(total.getCountId());
		result.setSumA(total.getSumTotal());
		
		map.put("status", 2);
		//成功数据
		EnterpriseDto success = transactionRecordMapper.enterpriseTotalInfo(map);
		result.setCountB(success.getCountId());
		result.setSumB(success.getSumTotal());
		
		map.put("status", -1);
		//失败数据
		EnterpriseDto fail = transactionRecordMapper.enterpriseTotalInfo(map);
		result.setCountC(fail.getCountId());
		result.setSumC(fail.getSumTotal());
		
		map.put("status", 1);
		//处理中数据
		EnterpriseDto ing = transactionRecordMapper.enterpriseTotalInfo(map);
		result.setCountD(ing.getCountId());
		result.setSumD(ing.getSumTotal());
		
		return result;
	}

	@Override
	public Pagination<EnterpriseDetailDto> pageEnterpriseDetail(Date begin,Date end,
			Pagination<EnterpriseDetailDto> pagination) {
		Map<String,Object> map = new HashMap<String,Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		map.put("startTime", sdf.format(begin));
		map.put("endTime", sdf.format(end));
		if( pagination != null){
			map.put("start", pagination.getStart());
			map.put("pageSize", pagination.getPageSize());
		}else{
			pagination = new Pagination<EnterpriseDetailDto>();
		}
		
		List<EnterpriseDetailDto> list = transactionRecordMapper.pageEnterpriseDetail(map);
		Integer count = transactionRecordMapper.pageCountEnterpriseDetail(map);
		
		pagination.setDatas(list);
		pagination.setTotalRow(count);

		return pagination;
	}

	public TransactionRecord selectById(Long id) {
		return transactionRecordMapper.selectByPrimaryKey(id);
	}

	@Override
	public TransactionRecord selectByTransactionNumberAndType(
			String transactionNumber, Integer type) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("transactionNumber", transactionNumber);
		map.put("type", type);
		return transactionRecordMapper.selectByTransactionNumberAndType(map);
	}

	

}
