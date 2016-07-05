package com.caitu99.service.realization.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.exception.RealizeException;
import com.caitu99.service.expedient.domain.ExpData;
import com.caitu99.service.expedient.provider.AddExp;
import com.caitu99.service.expedient.provider.rule.AddExpByRealization;
import com.caitu99.service.goods.dao.GoodPropMapper;
import com.caitu99.service.goods.dao.ItemMapper;
import com.caitu99.service.goods.domain.GoodProp;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.service.ItemService;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.mileage.dao.MileageIntegralMapper;
import com.caitu99.service.mileage.dao.UserAirlineMapper;
import com.caitu99.service.mileage.domain.MileageIntegral;
import com.caitu99.service.mileage.domain.UserAirline;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.realization.controller.RealizationController;
import com.caitu99.service.realization.dao.PhoneAmountMapper;
import com.caitu99.service.realization.dao.PhoneRealizeDetailMapper;
import com.caitu99.service.realization.dao.RealizeCouponMapper;
import com.caitu99.service.realization.dao.RealizeDetailMapper;
import com.caitu99.service.realization.dao.RealizeMapper;
import com.caitu99.service.realization.dao.RealizeRecordCmMapper;
import com.caitu99.service.realization.dao.RealizeRecordMapper;
import com.caitu99.service.realization.domain.PhoneAmount;
import com.caitu99.service.realization.domain.PhoneRealizeDetail;
import com.caitu99.service.realization.domain.Realize;
import com.caitu99.service.realization.domain.RealizeCoupon;
import com.caitu99.service.realization.domain.RealizeDetail;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.domain.RealizeRecordCm;
import com.caitu99.service.realization.domain.RealizeShareRecord;
import com.caitu99.service.realization.domain.UserAddTerm;
import com.caitu99.service.realization.domain.UserTerm;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.realization.service.RealizeShareRecordService;
import com.caitu99.service.realization.service.RealizeShareService;
import com.caitu99.service.realization.service.UserAddTermService;
import com.caitu99.service.realization.service.UserTermService;
import com.caitu99.service.transaction.controller.vo.AccountResult;
import com.caitu99.service.transaction.controller.vo.RechargeResult;
import com.caitu99.service.transaction.dao.MobileRechargeRecordMapper;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.calculate.CalculateUtils;
import com.caitu99.service.utils.json.JsonResult;
@Service
public class RealizeServiceImpl implements RealizeService {
	
	private Logger logger = LoggerFactory.getLogger(RealizeServiceImpl.class);

	@Autowired
	private RealizeMapper realizeMapper;
	
	@Autowired
	private RealizeDetailMapper realizeDetailMapper;

	@Autowired
	private PhoneRealizeDetailMapper phoneRealizeDetailMapper;
	
	@Autowired
    private GoodPropMapper goodPropDao;
	
	@Autowired
	private ItemMapper itemMapper;
	
	@Autowired
	private RealizeRecordMapper realizeRecordMapper;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private KafkaProducer kafkaProducer;
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private RealizeCouponMapper realizeCouponMapper;
	
	@Autowired
	private PushMessageService pushMessageService;
	
	@Autowired
	private OrderService orderService;

	@Autowired
	private CardTypeService cardTypeService;
	
	@Autowired
	private UserAddTermService userAddTermService;
	
	@Autowired
	private UserTermService userTermService;
	
	@Autowired
	private ItemService itemService;
	
	@Autowired
	private RealizationController realizationController;
	
	@Autowired
	private RealizeRecordCmMapper realizeCmMapper;
	
	@Autowired
	private TransactionRecordService transactionRecordService;
	
	@Autowired
	private RealizeShareRecordService realizeShareRecordService;
	
	@Autowired
	private MobileRechargeRecordMapper mobileRechargeRecordMapper;
	
	@Autowired
	private PhoneAmountMapper phoneAmountMapper;
	@Autowired
	private MileageIntegralMapper mileageIntegralMapper;
	@Autowired
	private UserAirlineMapper userAirlineMapper;
	
	@Autowired
	private RealizeShareService realizeShareService;
	
	@Autowired
	private AddExp addExp;
	
	@Autowired
	private AddExpByRealization addExpByRealization;
	
	@Override
	public List<Realize> selectRealizeByPlatform(Long realizationPlatformId) {
		return realizeMapper.selectByPlatformId(realizationPlatformId);
	}


	@Override
	public Map<String, Object> getItemPayParams(Long realizeDetailId) {
		RealizeDetail realizeDetail = realizeDetailMapper.selectByPrimaryKey(realizeDetailId);
		
		GoodProp querygp = new GoodProp();
		querygp.setItemId(realizeDetail.getItemId());
		querygp.setUseType(0);
		List<GoodProp> propList = goodPropDao.findPropByItemId(querygp);
		
		Item item = itemMapper.selectByPrimaryKey(realizeDetail.getItemId());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("quantity", realizeDetail.getQuantity());
		map.put("price", item.getSalePrice());
		map.put("propList", propList);
		map.put("itemId", realizeDetail.getItemId());
		return map;
	}


	@Override
	public List<RealizeDetail> selectRealizeDetailByRealizeId(Long realizeId) {
		return realizeDetailMapper.selectByRealizeId(realizeId);
	}


	@Override
	public Long saveRealizeRecord(Long userId, Long platformId, Long realizeDetailId,String memo) {
		if(null == userId || null == platformId || null == realizeDetailId){
			throw new RealizeException(-1, "参数不能为空");
		}
		RealizeDetail realizeDetail = realizeDetailMapper.selectByPrimaryKey(realizeDetailId);
		if(null == realizeDetail){
			throw new RealizeException(-1, "没有找到对应的兑换方案");
		}
		Realize realize = realizeMapper.selectByPrimaryKey(realizeDetail.getRealizeId());
		if(null == realize){
			throw new RealizeException(-1, "积分变现不存在");
		}
		Date date = new Date();
		RealizeRecord realizeRecord = new RealizeRecord();
		realizeRecord.setUserId(userId);
		realizeRecord.setRealizeId(realizeDetail.getRealizeId());
		realizeRecord.setPlatformId(platformId);
		realizeRecord.setRealizeDetailId(realizeDetailId);
		realizeRecord.setItemId(realizeDetail.getItemId());
		realizeRecord.setSkuId(realizeDetail.getSkuId());
		realizeRecord.setQuantity(realizeDetail.getQuantity());
		realizeRecord.setIntegral(realize.getIntegral());
		realizeRecord.setCash(realize.getCash());
		Long tubi = 0L;
		if(null != realize.getCash()){
			tubi = CalculateUtils.multiply(realize.getCash(), appConfig.tubiIntegerRate);
		}
		realizeRecord.setTubi(tubi);
		realizeRecord.setStatus(0);
		realizeRecord.setMemo(memo);
		realizeRecord.setCreateTime(date);
		realizeRecord.setUpdateTime(date);
		realizeRecordMapper.insertSelective(realizeRecord);
		return realizeRecord.getId();
	}
	
	@Override
	public Long savePhoneIntegralRecord(Long userId, Long platformId, String memo,Long amountId) {
		if(null == userId || null == platformId){
			throw new RealizeException(-1, "参数不能为空");
		}
		/*RealizeDetail realizeDetail = realizeDetailMapper.selectByPrimaryKey(realizeId);
		if(null == realizeDetail){
			throw new RealizeException(-1, "没有找到对应的兑换方案");
		}*/
		/*Realize realize = realizeMapper.selectByPrimaryKey(realizeDetail.getRealizeId());
		if(null == realize){
			throw new RealizeException(-1, "积分变现不存在");
		}*/
		Map<String,Long> queryMap = new HashMap<String, Long>();
		queryMap.put("amountId", amountId);
		queryMap.put("platformId", platformId);
		PhoneRealizeDetail detail = phoneRealizeDetailMapper.selectBy(queryMap);
		
		Date date = new Date();
		RealizeRecord realizeRecord = new RealizeRecord();
		realizeRecord.setUserId(userId);
		realizeRecord.setRealizeId(-1L);
		realizeRecord.setPlatformId(platformId);
		realizeRecord.setRealizeDetailId(-1L);
		realizeRecord.setItemId(detail.getItemId());
		realizeRecord.setSkuId(detail.getSkuId());
		realizeRecord.setQuantity(detail.getQuantity());
		realizeRecord.setIntegral(detail.getIntegral());
		realizeRecord.setCash(0L);
		realizeRecord.setTubi(0L);
		realizeRecord.setStatus(0);
		realizeRecord.setMemo(memo);
		realizeRecord.setCreateTime(date);
		realizeRecord.setUpdateTime(date);
		realizeRecordMapper.insertSelective(realizeRecord);
		return realizeRecord.getId();
	}
	
	@Override
	public void updateRealizeRecord(Long id, Long realizeDetailId) {
		if(null == id || null == realizeDetailId){
			throw new RealizeException(-1, "参数不能为空");
		}
		RealizeDetail realizeDetail = realizeDetailMapper.selectByPrimaryKey(realizeDetailId);
		if(null == realizeDetail){
			throw new RealizeException(-1, "没有找到对应的兑换方案");
		}
		RealizeRecord realizeRecord = new RealizeRecord();
		realizeRecord.setId(id);
		realizeRecord.setRealizeDetailId(realizeDetailId);
		realizeRecord.setItemId(realizeDetail.getItemId());
		realizeRecord.setSkuId(realizeDetail.getSkuId());
		realizeRecord.setQuantity(realizeDetail.getQuantity());
		realizeRecord.setUpdateTime(new Date());
		realizeRecordMapper.updateByPrimaryKeySelective(realizeRecord);
	}

	@Override
	public void updateRealizeRecordStatus(Long id,Integer status) {
		Date date = new Date();
		RealizeRecord realizeRecord = new RealizeRecord();
		realizeRecord.setId(id);
		realizeRecord.setStatus(status);
		if(3 == status.intValue()){
			realizeRecord.setTransferTime(date);
		}
		realizeRecord.setUpdateTime(date);
		realizeRecordMapper.updateByPrimaryKeySelective(realizeRecord);
	}


	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void realizeTransfer(Long realizeRecordId) {
		logger.info("变现到账处理 realizeRecordId："+realizeRecordId);
		if(null == realizeRecordId){
			throw new RealizeException(-1, "积分变现记录编号不能为空");
		}
		RealizeRecord realizeRecord = realizeRecordMapper.selectByPrimaryKey(realizeRecordId);
		if(null == realizeRecord){
			throw new RealizeException(-1, "没有找到对于的积分变现记录");
		}
		if(2 != realizeRecord.getStatus().intValue()){
			throw new RealizeException(-1, "积分变现记录状态异常");
		}
		String info = getPaltformInfo(realizeRecord.getPlatformId());
		//返现充财币
		TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
		transactionRecordDto.setChannel(8);
		transactionRecordDto.setComment("变现");
		transactionRecordDto.setInfo(info);
		transactionRecordDto.setOrderNo("");
		transactionRecordDto.setPicUrl("");
		transactionRecordDto.setTotal(realizeRecord.getCash());
		transactionRecordDto.setRmb(0L);
		
		//变现升级经验  转化后财币/100
		ExpData expdata = new ExpData();
		expdata.setUserId(realizeRecord.getUserId());
		expdata.setCash(realizeRecord.getCash());
		addExp.addExp(expdata, addExpByRealization);
		

		//获取必要数据
		String memo = realizeRecord.getMemo();
		JSONObject json = JSON.parseObject(memo);
		//版本号
		String version = json.getString("versionLong");
		//3.0.0版本以后送途币,以前版本不送
		if(!StringUtils.isBlank(version) && "3000000".compareTo(version) <= 0){
			transactionRecordDto.setTubi(realizeRecord.getTubi());//途币
		}else{
			transactionRecordDto.setTubi(0L);//途币
		}
		
		transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNo(
				"BX", String.valueOf(realizeRecord.getUserId())));
		transactionRecordDto.setType(2);
		transactionRecordDto.setUserId(realizeRecord.getUserId());
		transactionRecordDto.setSource(22);//来源于积分变现
		AccountResult result = accountService.add(transactionRecordDto);
		if(3101 == result.getCode()){
			//修改变现记录状态为已到帐
			this.updateRealizeRecordStatus(realizeRecordId, 3);
			try {
				//发送消息到消息中心
				String title = Configuration.getProperty("push.realize.title", null);
				String content = Configuration.getProperty("push.realize.content", null);
				Message message = new Message();
				message.setTitle(String.format(title, info,realizeRecord.getCash()));
				message.setPushInfo(String.format(content, info,realizeRecord.getCash()));
				pushMessageService.saveMessage(RedSpot.MESSAGE_CENTER, realizeRecord.getUserId(), message);
			} catch (Exception e) {
				logger.info("积分变现成功发送消息到消息中心失败",e);
			}
			
			//积分变现分享红包
			//查询该积分变现是否有使用过分享红包
			List<RealizeShareRecord> realizeShareRecordList = realizeShareRecordService.selectByRealizeRecordId(realizeRecordId);
			if(null == realizeShareRecordList){
				logger.info("积分变现记录:{},没有使用分享红包",realizeRecordId);
			}else{
				for(int i=0;i<realizeShareRecordList.size();i++){
					RealizeShareRecord realizeShareRecord = realizeShareRecordList.get(i);
					Long realizeShareRecordId = realizeShareRecord.getId();
					Long userId = realizeShareRecord.getUserId();
					Long money = realizeShareRecord.getMoney();
					logger.info("积分变现记录:{},使用分享红包,realizeShareRecordId:{},userId:{},财币:{}",
																realizeRecordId,realizeShareRecordId,userId,money);
					
					//返现充财币
					TransactionRecordDto transactionRecordShare = new TransactionRecordDto();
					transactionRecordShare.setChannel(9);
					transactionRecordShare.setComment("积分变现分享红包解冻");
					transactionRecordShare.setInfo("财途积分钱包");
					transactionRecordShare.setOrderNo("");
					transactionRecordShare.setRmb(0L);
					
		      		if(realizeShareRecord.getType().longValue() == 2){
						transactionRecordShare.setTubi(money);
						transactionRecordShare.setTotal(0L);
		      		}else{
						transactionRecordShare.setTubi(0L);
						transactionRecordShare.setTotal(money);
		      		}
					
					transactionRecordShare.setTransactionNumber(XStringUtil.createSerialNo("BX", String.valueOf(realizeRecord.getUserId())));
					transactionRecordShare.setType(6);
					transactionRecordShare.setUserId(userId);
					transactionRecordShare.setSource(23);//来源于积分变现
					result = accountService.add(transactionRecordShare);
					if(3101 == result.getCode()){
						//修改记录状态为已到帐
						realizeShareRecord.setStatus(3);
						realizeShareRecordService.update(realizeShareRecord);
					}
				}
			}
		}
	}

	private String getPaltformInfo(Long platformId){
		switch (platformId.intValue()) {
		case 1:
			return "建设银行";
		case 2:
			return "天翼";
		case 3:
			return "中信银行";
		case 4:
			return "交通银行";
		case 5:
			return "移动";
		case 6:
			return "联通";
		case 7:
			return "天翼";
		case 8:
			return "平安银行";
		default:
			return "";
		}
	}

	@Override
	public void realizeJob(Long realizeRecordId) {
		Map<String,String> map = new HashMap<String, String>();
		map.put("realizeRecordId", String.valueOf(realizeRecordId));
		map.put("jobType", "REALIZE_TRANSFER_JOB");//变现到账JOB
		kafkaProducer.sendMessage(JSON.toJSONString(map),appConfig.jobTopic);
	}

	@Override
	public void realizeJob(Long realizeRecordId,String shopName) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("realizeRecordId", String.valueOf(realizeRecordId));
		map.put("jobType", "REALIZE_TRANSFER_JOB");//变现到账JOB
		map.put("shopName",shopName);
		kafkaProducer.sendMessage(JSON.toJSONString(map),appConfig.jobTopic);
	}


	@Override
	public RealizeRecord selectById(Long id) {
		return realizeRecordMapper.selectByPrimaryKey(id);
	}


	@Override
	public Boolean checkCodeExists(Long platformId, String code) {
		Map<String,String> map = new HashMap<String, String>(2);
		map.put("platformId", platformId.toString());
		map.put("code", code);

		int count = realizeCouponMapper.checkExists(map);
		if(count > 0){
			return true;
		}
		return false;
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveRealizeCoupon(RealizeCoupon realizeCoupon) {
		realizeCoupon.setStatus(1);
		realizeCoupon.setCreateTime(new Date());
		realizeCouponMapper.insertSelective(realizeCoupon);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveRealizeCouponList(List<RealizeCoupon> list,String orderNo,Long userid,Long realizeRecordId,boolean isSave) {
		if(isSave){
			if(null == list || list.size() < 1){
				return;
			}

			for(RealizeCoupon realizeCoupon : list){
				boolean flag = this.checkCodeExists(realizeCoupon.getPlatformId(), realizeCoupon.getCode());
				if(flag){
					throw new RealizeException(-1, "您的券码:" + realizeCoupon.getCode() + "有重复,请检查后重填");
				}
				
				this.saveRealizeCoupon(realizeCoupon);
			}
		}
		
		RealizeRecord realizeRecord = realizeRecordMapper.selectByPrimaryKey(realizeRecordId);
		if(null == realizeRecord){
			throw new RealizeException(-1, "积分变现记录不存在");
		}
		
		orderService.finishRealizeOrder(orderNo, userid);

		this.updateRealizeRecordStatus(realizeRecordId, 2);
		
		//增加交易记录
		String info = getPaltformInfo(realizeRecord.getPlatformId());
		Date nowDate = new Date(); 
		TransactionRecord fTransactionRecord = new TransactionRecord();
        fTransactionRecord.setTransactionNumber(UUID.randomUUID().toString().replace("-", ""));
        fTransactionRecord.setOrderNo(orderNo);
        fTransactionRecord.setUserId(userid);
        fTransactionRecord.setInfo(info);
        fTransactionRecord.setType(10);
        fTransactionRecord.setPayType(1);
        fTransactionRecord.setSource(3);
        fTransactionRecord.setStatus(0);
        
        boolean isNewVersion = false;
        //获取版本号
  		String memo = realizeRecord.getMemo();
  		JSONObject jsonMemo = JSON.parseObject(memo);
  		String version = jsonMemo.getString("versionLong");
  		if(!StringUtils.isBlank(version) && "3000000".compareTo(version) <= 0){
  	        fTransactionRecord.setTubi(realizeRecord.getTubi());
  			fTransactionRecord.setTotal(realizeRecord.getCash());
  			isNewVersion = true;
  		}else{
  			fTransactionRecord.setTotal(realizeRecord.getCash());
  	        fTransactionRecord.setTubi(0L);
  		}
        
  		fTransactionRecord.setRmb(0L);
        fTransactionRecord.setCouponIntegral(0L);
        fTransactionRecord.setComment("积分变现冻结");
        fTransactionRecord.setSuccessTime(nowDate);
        fTransactionRecord.setChannel(8);
        fTransactionRecord.setCreateTime(nowDate);
        fTransactionRecord.setUpdateTime(nowDate);
        transactionRecordService.insert(fTransactionRecord);
        
        //查询用户是否有分享红包
        RealizeShareRecord realizeShareRecord = realizeShareRecordService.selectFirstRecord(userid,1);
        if(null == realizeShareRecord){
        	logger.info("用户:{},没有分享财币红包券码",userid);
        	
        	if(isNewVersion){
        		//查询用户是否有途币券
        		realizeShareRecord = realizeShareRecordService.selectFirstRecord(userid,2);
        		if(null == realizeShareRecord){
                	logger.info("用户:{},没有分享财币红包券码",userid);
        		}else{
        			operationShareCoupon(userid,realizeShareRecord,realizeRecordId,nowDate);
        		}
        	}
        }else{
        	Long money = realizeShareRecord.getMoney();
        	logger.info("用户:{},拥有分享红包券码,realizeShareRecordId:{},金额:{}途币",userid,realizeShareRecord.getId(),money);
        	
			operationShareCoupon(userid,realizeShareRecord,realizeRecordId,nowDate);
			if(isNewVersion){
        		//查询用户是否有途币券
        		realizeShareRecord = realizeShareRecordService.selectFirstRecord(userid,2);
        		if(null == realizeShareRecord){
                	logger.info("用户:{},没有分享财币红包券码",userid);
        		}else{
        			operationShareCoupon(userid,realizeShareRecord,realizeRecordId,nowDate);
        		}
        	}
        }
	}
	
	/**
	 * 操作分享红包券码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: operationShareCoupon 
	 * @param userid					用户ID
	 * @param realizeShareRecord		分享红包券码对象
	 * @param realizeRecordId			积分变现记录ID
	 * @param nowDate					修改时间
	 * @date 2016年5月26日 下午3:40:08  
	 * @author xiongbin
	 */
	@Transactional
	public void operationShareCoupon(Long userid,RealizeShareRecord realizeShareRecord,Long realizeRecordId,Date nowDate){
		Long money = realizeShareRecord.getMoney();
		
		TransactionRecord shareTransactionRecord = new TransactionRecord();
        shareTransactionRecord.setTransactionNumber(UUID.randomUUID().toString().replace("-", ""));
        shareTransactionRecord.setUserId(userid);
        shareTransactionRecord.setInfo("财途积分钱包");
        shareTransactionRecord.setOrderNo("");
        shareTransactionRecord.setType(6);
        shareTransactionRecord.setPayType(1);
        shareTransactionRecord.setSource(23);
        shareTransactionRecord.setStatus(0);
        
  		if(realizeShareRecord.getType().longValue() == 2){
  			shareTransactionRecord.setTubi(money);
  			shareTransactionRecord.setTotal(0L);
  		}else{
  			shareTransactionRecord.setTubi(0L);
  			shareTransactionRecord.setTotal(money);
  		}
        
  		shareTransactionRecord.setRmb(0L);
        shareTransactionRecord.setCouponIntegral(0L);
        shareTransactionRecord.setComment("积分变现分享红包冻结");
        shareTransactionRecord.setSuccessTime(nowDate);
        shareTransactionRecord.setChannel(9);
        shareTransactionRecord.setCreateTime(nowDate);
        shareTransactionRecord.setUpdateTime(nowDate);
        transactionRecordService.insert(shareTransactionRecord);
        //修改分享红包被使用的积分变现记录
        realizeShareRecord.setRealizeRecordId(realizeRecordId);
        realizeShareRecord.setStatus(2);
        realizeShareRecordService.update(realizeShareRecord);
	}


	@Override
	public RealizeDetail selectByLevel(Long realizeId, Long realizeDetailId) {
		Integer level = 1;
		if(null != realizeDetailId){
			RealizeDetail realizeDetail = realizeDetailMapper.selectByPrimaryKey(realizeDetailId);
			level = realizeDetail.getLevel()+1;
		}
		Map<String,String> map = new HashMap<String,String>(2);
		map.put("realizeId", realizeId.toString());
		map.put("level", level.toString());

		return realizeDetailMapper.selectByLevel(map);
	}


	@Override
	public List<RealizeCoupon> selectByRealizeRecordId(Long realizeRecordId) {
		return realizeCouponMapper.selectByRealizeRecordId(realizeRecordId);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public String realizePay(Long userid,RealizeRecord realizeRecord,UserAddTerm userAddTerm,JSONObject json,JSONObject info,
													boolean isTransfer,Long remoteId,Integer remoteType,Integer isAddUserTerm,boolean isAddCoupon){

		if(realizeRecord.getStatus().equals(2) || realizeRecord.getStatus().equals(3) || realizeRecord.getStatus().equals(10)){
			logger.error("积分变现记录状态错误.realizeRecordId:{},status:{}",realizeRecord.getId(),realizeRecord.getStatus());
			return null;
		}
		
		Long platformId = realizeRecord.getPlatformId();
		
		//1.判断是否来自添加变现卡
		if(isAddUserTerm.equals(1)){
			CardType cardType = cardTypeService.selectByPlatformId(platformId);
			userAddTerm.setCardTypeId(cardType.getId());
			userAddTerm.setUserId(userid);
			remoteId = userAddTermService.insertORupdate(userAddTerm);
			remoteType = 3;
		}
		
		if(new Integer(3).equals(remoteType) && new Integer(0).equals(isAddUserTerm)){
//		if(remoteType.equals(3) && isAddUserTerm.equals(0)){
			remoteId = userAddTermService.insertORupdate(userAddTerm);
		}
		
		//2.是否需要绑定卡片（记录用户帐号支付信息）
		UserTerm userTerm = new UserTerm();
		userTerm.setInfo(info.toJSONString());
		userTerm.setRemoteId(remoteId);
		userTerm.setRemoteType(remoteType);
		userTerm.setUserId(userid);
		Long userTermId = userTermService.saveOrUpdate(userTerm);
		
		//3.生成订单
		Long integral = realizeRecord.getIntegral();
		String orderReslut = orderService.addRealizeOrder(userid,integral,platformId,realizeRecord);
		orderReslut = JsonResult.getResult(orderReslut, "data");
		Map<String,Object> map = JSON.parseObject(orderReslut,Map.class);
		String orderNo = (String)map.get("no");

		if(realizeRecord.getRealizeDetailId() != -1){
			//将订单和变现记录关联起来
			RealizeRecord editRealizeRecord = new RealizeRecord();
			editRealizeRecord.setId(realizeRecord.getId());
			editRealizeRecord.setOrderNo(orderNo);
			realizeRecordMapper.updateByPrimaryKeySelective(editRealizeRecord);
			
			//4.1.判断返现财币类型
			Long realizeRecordId = realizeRecord.getId();
			
			Integer status = 0;
			
			if(isTransfer){
				status = 2;
				//4.2.生成变现记录
				this.updateRealizeRecordStatus(realizeRecordId, status);
				//直接到账
				this.realizeTransfer(realizeRecordId);
			}else{
				if(isAddCoupon){
					status = 1;
				}else{
					status = 2;
				}
				this.updateRealizeRecordStatus(realizeRecordId, status);
			}
		}
		
		//保存绑定账户信息到变现记录
		RealizeRecord updateRealizeRecord = new RealizeRecord();
		updateRealizeRecord.setId(realizeRecord.getId());
		updateRealizeRecord.setUserTermId(userTermId);
		this.updateRealizeRecord(updateRealizeRecord);
		
		return orderNo;
	}

	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public JSONObject convertMileage(Long userid,RealizeRecord realizeRecord,UserAddTerm userAddTerm,
			JSONObject json,JSONObject info,Long remoteId,Integer remoteType,Integer isAddUserTerm){

		Long platformId = realizeRecord.getPlatformId();
		
		//1.判断是否来自添加变现卡
		if(isAddUserTerm.equals(1)){
			CardType cardType = cardTypeService.selectByPlatformId(platformId);
			userAddTerm.setCardTypeId(cardType.getId());
			userAddTerm.setUserId(userid);
			remoteId = userAddTermService.insertORupdate(userAddTerm);
			remoteType = 3;
		}
		
		if(new Integer(3).equals(remoteType) && new Integer(0).equals(isAddUserTerm)){
			remoteId = userAddTermService.insertORupdate(userAddTerm);
		}
		
		//2.是否需要绑定卡片（记录用户帐号支付信息）
		UserTerm userTerm = new UserTerm();
		userTerm.setInfo(info.toJSONString());
		userTerm.setRemoteId(remoteId);
		userTerm.setRemoteType(remoteType);
		userTerm.setUserId(userid);
		userTermService.saveOrUpdate(userTerm);
		
		
		//新增用户航空公司绑定
		UserAirline userAir = new UserAirline();
		Long airlineCompanyId = json.getLong("flightCompanyId");
		//先将该用户已有的删除
		Map<String,Long> userAirMap = new HashMap<String, Long>();
		userAirMap.put("userid", userid);
		userAirMap.put("airlineCompanyId", airlineCompanyId);
		userAirlineMapper.deleteByUser(userAirMap);
		
		Date now = new Date();
		userAir.setAirlineCard(json.getString("memberId"));
		userAir.setAirlineCompanyId(airlineCompanyId);
		userAir.setCreateTime(now);
		userAir.setFirstName(json.getString("ebsNm1"));
		userAir.setLastName(json.getString("ebsNm2"));
		userAir.setStatus(1);
		userAir.setUpdateTime(now);
		userAir.setUserId(userid);
		//添加新的
		userAirlineMapper.insert(userAir);
		
		//3.生成订单
		String orderNo = orderService.addMileageOrder(userid,platformId,airlineCompanyId,realizeRecord);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("platformName", "交通银行");//TODO 
		jsonObject.put("orderNo", orderNo);
		return jsonObject;
	}

	

	@Override
	public Long getUserRealizeSUM(Long userid,String account, Long realizeDetailId) {
		RealizeRecordCm realizeCm = new RealizeRecordCm();
		realizeCm.setUserId(userid);
		realizeCm.setAccount(account);
		if(null != realizeDetailId){
			realizeCm.setRealizeDetailId(realizeDetailId);
		}
		return realizeCmMapper.getUserRealizeSUM(realizeCm);
	}


	@Override
	public Long getWLTAccountSUM(String wltAccount) {
		return realizeCmMapper.getWLTAccountSUM(wltAccount);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public String cmPayBusiness(Long userid, Long realizeRecordId, String account, String password){
		ApiResult<Long> apiResult = new ApiResult<Long>();
		//获取变现记录
		RealizeRecord realizeRecord = this.selectById(realizeRecordId);
		JSONObject json = JSONObject.parseObject(realizeRecord.getMemo());
		Long remoteId = json.getLong("remoteId");
		Integer remoteType = json.getInteger("remoteType");
		Integer isAddUserTerm = json.getInteger("isAddUserTerm"); 
		String wanlitongAccount = json.getString("wanlitongAccount");
		if(null == isAddUserTerm){
			return apiResult.toJSONString(-2, "数据出现异常");
		}
		UserAddTerm userAddTerm = new UserAddTerm();
		userAddTerm.setLoginAccount(account);
		
		JSONObject info = new JSONObject();
		info.put("account",account);
		info.put("password",password);
		//处理业务
		String no = this.realizePay(userid, realizeRecord, userAddTerm, json, info, true, remoteId, remoteType, isAddUserTerm,false);
		
		if(StringUtils.isBlank(no)){
			return apiResult.toJSONString(-1, "系统暂时出现异常,请稍后再试");
		}
		
		//变现记录 状态 完成
		this.updateRealizeRecordStatus(realizeRecordId, 3);
		//订单 状态 完成
		orderService.finishRealizeOrder(no, userid);
		//记录变现的额度
		RealizeRecordCm realizeCm = new RealizeRecordCm();
		realizeCm.setIntegral(realizeRecord.getIntegral());
		realizeCm.setRealizeDetailId(realizeRecord.getRealizeDetailId());
		realizeCm.setUserId(userid);
		realizeCm.setAccount(account);
		realizeCm.setWltAccount(wanlitongAccount);
		realizeCm.setCreateTime(new Date());
		realizeCmMapper.insertSelective(realizeCm);
		
		//积分变现红包
		
		return apiResult.toJSONString(0, "变现成功", realizeRecord.getCash());
	}


	@Override
	public void updateRealizeRecord(RealizeRecord record) {
		record.setUpdateTime(new Date());
		realizeRecordMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public RealizeRecord selectByOrderNo(String orderNo) {
		return realizeRecordMapper.selectByOrderNo(orderNo);
	}
	
	@Override
	public Map<String,Object> getMode3Date(String orderNo){
		RealizeRecord realizeRecord = this.selectByOrderNo(orderNo);
		if(null == realizeRecord){
			logger.warn("未查询到积分变现记录,orderNo:" + orderNo);
			return null;
		}
		
		Long itemId = realizeRecord.getItemId();
		if(null == itemId){
			logger.warn("积分变现记录中没有记录商品ID,orderNo:" + orderNo);
			return null;
		}
		
		Item item = itemMapper.selectByPrimaryKey(itemId);
		if(null == item){
			logger.warn("没有查询到商品信息,orderNo:" + orderNo);
			return null;
		}
		
		Long realizeDetailId = realizeRecord.getRealizeDetailId();
		if(null == realizeDetailId){
			logger.warn("没有积分变现明细,orderNo:" + orderNo);
			return null;
		}
		
		RealizeDetail realizeDetail = realizeDetailMapper.selectByPrimaryKey(realizeDetailId);
		if(null == realizeDetail){
			logger.warn("没有积分变现明细,orderNo:" + orderNo);
			return null;
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("itemName", item.getTitle());
		map.put("quantity", realizeRecord.getQuantity());
		map.put("realizeRecordId", realizeRecord.getId());
		map.put("mode", realizeDetail.getMode());
		map.put("platformId", realizeRecord.getPlatformId());
		
		return map;
	}

	@Override
	public List<RealizeRecord> selectRealizeDetailFirst(Long userId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userId", userId);
		map.put("status", 2);
		
		return realizeRecordMapper.selectRealizeDetailFirst(map);
	}
	
	@Override
	public Map<String,Long> selectFreezeCashByUserId(Long userId){
		Long total = 0L;
		Long tubi = 0L;
		
		//积分变现
		List<RealizeRecord> list = this.selectRealizeDetailFirst(userId);
		if(null!=list && list.size()>0){
			for(RealizeRecord realizeRecord : list){
				Long cash = realizeRecord.getCash();
				Long cashTubi = realizeRecord.getTubi() == null? 0L:realizeRecord.getTubi();
				total += cash;
				tubi += cashTubi;
			}
		}
		
		//积分变现分享红包
		List<RealizeShareRecord> realizeShareRecordList = realizeShareRecordService.selectHasUsedByUserId(userId);
		if(null!=realizeShareRecordList && realizeShareRecordList.size()>0){
			for(RealizeShareRecord realizeShareRecord : realizeShareRecordList){
				Long money = realizeShareRecord.getMoney();
				total += money;
			}
		}
		
		Map<String,Long> map = new HashMap<String, Long>();
		map.put("total", total);
		map.put("tubi", tubi);
		return map;
	}


	/* (non-Javadoc)
	 * @see com.caitu99.service.realization.service.RealizeService#getItemPayParamsByRecord(com.caitu99.service.realization.domain.RealizeRecord)
	 */
	@Override
	public Map<String, Object> getItemPayParamsByRecord(
			RealizeRecord realizeRecord) {
		GoodProp querygp = new GoodProp();
		querygp.setItemId(realizeRecord.getItemId());
		querygp.setUseType(0);
		List<GoodProp> propList = goodPropDao.findPropByItemId(querygp);
		
		Item item = itemMapper.selectByPrimaryKey(realizeRecord.getItemId());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("quantity", realizeRecord.getQuantity());
		map.put("price", item.getSalePrice());
		map.put("propList", propList);
		map.put("itemId", realizeRecord.getItemId());
		return map;
	}


	/* (non-Javadoc)
	 * @see com.caitu99.service.realization.service.RealizeService#checkPhoneLimit(java.lang.Long, java.lang.Long)
	 */
	@Override
	public RechargeResult checkPhoneLimit(Long userId, Long amountId) {
		RechargeResult rechargeResult = new RechargeResult();
		
		// 验证当日话费充值总额
        Long totalAmount = mobileRechargeRecordMapper.sameDayPhoneRechargeAmount(userId);
        if(totalAmount == null){
        	totalAmount = 0L;
        }
        PhoneAmount phoneAmount = phoneAmountMapper.selectByPrimaryKey(amountId);
		if (totalAmount + phoneAmount.getAmount()  > appConfig.phoneRechargeAmountLimit) {
            String msg = "抱歉，您今天的累计充值话费额超过了限额（" + appConfig.phoneRechargeAmountLimit + "元）";
            rechargeResult.setSuccess(false);
			rechargeResult.setResult(msg);
			return rechargeResult;
        }
		rechargeResult.setSuccess(true);
		return rechargeResult;
	}

	@Override
	public Integer selectUserNotCoupon(Long userid) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userId", userid);
		map.put("status", 1);
		
		List<RealizeRecord> list = realizeRecordMapper.selectRealizeDetailFirst(map);
		if(null == list){
			return 0;
		}
		
		return list.size();
	}


	/* (non-Javadoc)
	 * @see com.caitu99.service.realization.service.RealizeService#saveMileageConvertRecord(java.lang.Long, java.lang.Long, java.lang.String)
	 */
	@Override
	public Long saveMileageConvertRecord(Long userId, Long platformId,
			String jsonString, Long mileageId) {
		MileageIntegral detail = mileageIntegralMapper.selectByPrimaryKey(mileageId);
		
		Date date = new Date();
		RealizeRecord realizeRecord = new RealizeRecord();
		realizeRecord.setUserId(userId);
		realizeRecord.setRealizeId(-1L);
		realizeRecord.setPlatformId(platformId);
		realizeRecord.setRealizeDetailId(-1L);
		realizeRecord.setItemId(-1L);
		realizeRecord.setSkuId(-1L);
		realizeRecord.setQuantity(1);
		realizeRecord.setIntegral(detail.getIntegral());
		realizeRecord.setCash(detail.getMileage());//此处记为里程数
		realizeRecord.setTubi(0L);
		realizeRecord.setStatus(0);
		realizeRecord.setMemo(jsonString);
		realizeRecord.setCreateTime(date);
		realizeRecord.setUpdateTime(date);
		realizeRecordMapper.insertSelective(realizeRecord);
		return realizeRecord.getId();
	}

	@Override
	@Transactional
	public void ishopBinding(Long userid, Long platformId, String info,UserAddTerm userAddTerm) {
		CardType cardType = cardTypeService.selectByPlatformId(platformId);
		if(null == cardType){
			logger.error("platformId:{},不存在",platformId);
			return;
		}
		
		Date now = new Date();
		userAddTerm.setCardTypeId(cardType.getId());
		userAddTerm.setCreateTime(now);
		userAddTerm.setStatus(1);
		userAddTerm.setUpdateTime(now);
		userAddTerm.setUserId(userid);
		
		Long remoteId = userAddTermService.insertORupdate(userAddTerm);
		
		UserTerm userTerm = new UserTerm();
		userTerm.setCreateTime(now);
		userTerm.setInfo(info);
		userTerm.setRemoteId(remoteId);
		userTerm.setRemoteType(3);
		userTerm.setStatus(1);
		userTerm.setUpdateTime(now);
		userTerm.setUserId(userid);
		
		userTermService.saveOrUpdate(userTerm);
	}

	@Override
	public boolean isRealizeSuccessByUserId(Long userid) {
		boolean flag = false;
		List<RealizeRecord> list = this.selectRealizeDetailFirst(userid);
		if(null!=list && list.size()>0){
			flag = true;
        }else{
        	Map<String,Object> map = new HashMap<String,Object>();
    		map.put("userId", userid);
    		map.put("status", 3);
    		
        	list = realizeRecordMapper.selectRealizeDetailFirst(map);
    		if(null!=list && list.size()>0){
    			flag = true;
    		}else{
    			flag = false;
    		}
        }
		return flag;
	}
}
