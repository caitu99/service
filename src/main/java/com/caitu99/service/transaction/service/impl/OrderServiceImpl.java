package com.caitu99.service.transaction.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.exception.OrderException;
import com.caitu99.service.expedient.domain.ExpData;
import com.caitu99.service.expedient.provider.AddExp;
import com.caitu99.service.expedient.provider.rule.AddExpByIshop;
import com.caitu99.service.file.service.AttachFileService;
import com.caitu99.service.free.dao.FreeTradeMapper;
import com.caitu99.service.free.domain.FreeTrade;
import com.caitu99.service.goods.dao.GoodPropMapper;
import com.caitu99.service.goods.dao.ItemMapper;
import com.caitu99.service.goods.dao.OrdernoAddrMapper;
import com.caitu99.service.goods.dao.ReceiveStockMapper;
import com.caitu99.service.goods.dao.SkuMapper;
import com.caitu99.service.goods.dao.StockMapper;
import com.caitu99.service.goods.domain.GoodProp;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.domain.OrdernoAddr;
import com.caitu99.service.goods.domain.ReceiveStock;
import com.caitu99.service.goods.domain.Sku;
import com.caitu99.service.goods.domain.Stock;
import com.caitu99.service.goods.service.OrdernoAddrService;
import com.caitu99.service.life.domain.PhoneRechargeRecord;
import com.caitu99.service.mileage.dao.AirlineCompanyMapper;
import com.caitu99.service.mileage.domain.AirlineCompany;
import com.caitu99.service.mq.producer.KafkaProducer;
import com.caitu99.service.push.model.Message;
import com.caitu99.service.push.model.enums.RedSpot;
import com.caitu99.service.push.service.PushMessageService;
import com.caitu99.service.realization.dao.PhoneAmountMapper;
import com.caitu99.service.realization.dao.RealizePlatformMapper;
import com.caitu99.service.realization.domain.PhoneAmount;
import com.caitu99.service.realization.domain.RealizePlatform;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.transaction.controller.vo.AccountResult;
import com.caitu99.service.transaction.dao.FreeInventoryRecordMapper;
import com.caitu99.service.transaction.dao.FreeNoRecordMapper;
import com.caitu99.service.transaction.dao.FreeTradeOrderMapper;
import com.caitu99.service.transaction.dao.MobileRechargeRecordMapper;
import com.caitu99.service.transaction.dao.OrderAddressMapper;
import com.caitu99.service.transaction.dao.OrderItemMapper;
import com.caitu99.service.transaction.dao.OrderMapper;
import com.caitu99.service.transaction.dao.TradingSnapshotMapper;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.FreeInventoryRecord;
import com.caitu99.service.transaction.domain.FreeNoRecord;
import com.caitu99.service.transaction.domain.FreeTradeOrder;
import com.caitu99.service.transaction.domain.Order;
import com.caitu99.service.transaction.domain.OrderAddress;
import com.caitu99.service.transaction.domain.OrderItem;
import com.caitu99.service.transaction.domain.TradingSnapshot;
import com.caitu99.service.transaction.dto.OrderDto;
import com.caitu99.service.transaction.dto.OrderItemDto;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.OrderAddressService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.transaction.service.impl.AccountServiceImpl.TradeType;
import com.caitu99.service.user.dao.UserMapper;
import com.caitu99.service.user.domain.Address;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.AddressService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.calculate.CalculateUtils;
import com.caitu99.service.utils.date.DateUtil;
import com.caitu99.service.utils.date.ProccessUtil;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.json.JsonResult;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: OrderService 
 * @author tsai
 * @date 2015年11月25日 上午10:47:33 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class OrderServiceImpl implements OrderService{
	private final static Logger logger = LoggerFactory
			.getLogger(OrderService.class);
	
	@Autowired
	private SkuMapper skuDao;
	
	@Autowired
	private ItemMapper itemDao;
	
	@Autowired
	private StockMapper stockDao;
	
	@Autowired
	private ReceiveStockMapper receiveStockDao;
	
	@Autowired
	private OrderMapper orderDao;
	
	@Autowired
	private OrderItemMapper orderItemDao;
	
	@Autowired
	private UserMapper userDao;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private KafkaProducer kafkaProducer;
	
	@Autowired
	private TradingSnapshotMapper tradingSnapshotDao;

	@Autowired
	private AttachFileService attachFileService;
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountDetailService accountDetailService;

	@Autowired
	private PushMessageService pushMessageService;

    @Autowired
    private TransactionRecordService transactionRecordService;
    
    @Autowired
    private GoodPropMapper goodPropDao;
    
    @Autowired
    private FreeTradeMapper freeTrdeDao;
    
    @Autowired
    private AddressService addressService;
    
    @Autowired
    private OrderAddressService orderAddressService;
    
    @Autowired
    private FreeInventoryRecordMapper freeInventoryRecordDao;
    
    @Autowired
    private OrderAddressMapper orderAddressDao;
    
    @Autowired
    private AppConfig appconfig;
    
    @Autowired
    private FreeTradeOrderMapper freeTradeOrderDao;
    
	@Autowired
	private RedisOperate redis;
	
	@Autowired
	private FreeNoRecordMapper freeNoRecordDao;
	
	@Autowired
	private OrdernoAddrMapper ordernoAddrDao;
	
	@Autowired
	private OrdernoAddrService ordernoAddrService;
	
	@Autowired
	private RealizePlatformMapper realizationPlatformDao;

	@Autowired
	private PhoneAmountMapper phoneAmountMapper;
	@Autowired
	private MobileRechargeRecordMapper mobileRechargeRecordMapper;
	@Autowired
	private AirlineCompanyMapper airlineCompanyMapper;
	
	@Autowired
	private AddExp addExp;
	
	@Autowired
	private AddExpByIshop addExpByIshop;
	
	@Autowired
	private PhoneAmountMapper phoneAmountDao;

	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.server.OrderService#findPageItem(com.caitu99.service.transaction.domain.Order, com.caitu99.service.base.Pagination)
	 */
	@Override
	public Pagination<OrderDto> findPageOrder(Order order,
			Pagination<OrderDto> pagination) throws OrderException {
		try {
			if(null==order || null==pagination){
				return pagination;
			}
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("order", order);
			map.put("start", pagination.getStart());
			map.put("pageSize", pagination.getPageSize());
			
			Integer count = orderDao.selectPageCount(map);
			List<OrderDto> list = orderDao.selectPageList(map);
			try {
				String url = SpringContext.getBean(AppConfig.class).fileUrl;
				String imageBig = SpringContext.getBean(AppConfig.class).imageFileBig; 
				for (OrderDto od : list) {
					List<OrderItemDto> otd = orderItemDao.findAllItemByOrderNo(od.getOrderNo());
					List<String> sl = new ArrayList<String>();
					Long itemPrice = 0L;
					Integer quantity = 0;
					for (OrderItemDto orderItemDto : otd) {
						sl.add(url+orderItemDto.getImgUrl()+imageBig);
						itemPrice = orderItemDto.getPrice();
						quantity = orderItemDto.getQuantity();
					}
					od.setImgUrlList(sl);
					od.setItemPrice(itemPrice);
					od.setQuantity(quantity);
					od.setOrderTimeString(DateUtil.DateToString(od.getOrderTime()));
					od.setOutTimeString(DateUtil.DateToString(od.getTimeoutTime()));
					if(Order.STATUS_FINISH.equals(od.getStatus()) || Order.STATUS_PAY.equals(od.getStatus()) ){
						od.setPayTimeString(DateUtil.DateToString(od.getPayTime()));
					}
					od.setTypeString("");//20160125 chencheng add typeString添加默认值
					if(3 == od.getType()){
						od.setTypeString("移动商城");
					}
					if(4 == od.getType()){
						od.setTypeString("联通商城");
					}
				}
				
			} catch (Exception e) {
				logger.error("查询订单明细及交易快照失败:" + e.getMessage(),e);
			}
			
			pagination.setDatas(list);
			pagination.setTotalRow(count);
			
			return pagination;
		} catch (Exception e) {
			logger.error("订单分页查询失败:" + e.getMessage(),e);
			throw new OrderException(ApiResultCode.ORDER_PAGE_FAILE,"订单分页查询失败:" + e.getMessage());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.order.service.OrderService#addOrder(java.lang.Long,
	 * java.lang.Object)
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "OrderException" })
	public String addOrder(Long userid, Long skuId,Integer quantity) throws OrderException {
		try {
			Account account = accountService.selectByUserId(userid);
			//锁账号
			accountService.selectByPrimaryKeyForUpdate(account.getId());
			
			ApiResult<Map<String,Object>> result = new ApiResult<Map<String,Object>>();
			Sku sku = skuDao.selectByPrimaryKey(skuId);
			if(null == sku){
				return result.toJSONString(ApiResultCode.ORDER_SKU_NONE, "商品明细为空");
			}
			
			//查询库存
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("itemId", sku.getItemId());
			map.put("skuId", skuId);
			Integer inventory = stockDao.countInventory(map);
			if(quantity > inventory){
				return result.toJSONString(ApiResultCode.ORDER_INVENTORY_NONE, "这个商品太受欢迎了，2小时后再来吧");
			}
			
			Long subTotal = CalculateUtils.multiply(sku.getSalePrice(), quantity);
			
			/*
			boolean isEnough = accountService.isEnough(userid, subTotal);
			if(!isEnough){
				return result.toJSONString(-1, "您的财币不足");
			}
			*/

            // 验证消费总额
            Integer count = transactionRecordService.getLastDayConsumes(userid);
            if (count + subTotal > appConfig.maxConsumesPerDay) {
                String msg = "抱歉，您今天的累积消费财币已超过限额（" + appConfig.maxConsumesPerDay + "财币）";
                return result.toJSONString(2219, msg);
            }
			
			//生成订单号
			String no = Order.getOderNo(userid);
			Date date = new Date();
			
			//获取用户数据及商品数据
			Item item = itemDao.selectByPrimaryKey(sku.getItemId());
			String name = item.getTitle();
			
		
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderNo(no);
			orderItem.setItemId(sku.getItemId());
			orderItem.setSkuId(skuId);
			orderItem.setName(name);
			orderItem.setPrice(sku.getSalePrice());
			orderItem.setQuantity(quantity);
			orderItem.setCreateTime(date);
			orderItem.setUpdateTime(date);
			
			
			Long payCaibi = 0L;
			Long payTubi = 0L;
			Long payRmb = 0L;
			
			Long tubi = account.getTubi();
			Long privilege = sku.getSalePrice()-sku.getCostPrice();
			Long privilegeTotal = CalculateUtils.multiply(privilege, quantity);
			if(tubi > privilegeTotal){
				payTubi = privilegeTotal;
			}else{
				payTubi = tubi;
			}
			
			Long caibi = account.getAvailableIntegral();
			if(caibi > subTotal - payTubi){
				payCaibi = subTotal - payTubi;
			}else{
				payCaibi = caibi;
			}
			
			payRmb = subTotal - payCaibi - payTubi;
			
			subTotal = subTotal - privilegeTotal;
		
			//初始化订单数据
			Order order = new Order();
			order.setOrderNo(no);
			order.setUserId(userid);
			order.setName(name);
			order.setPayStatus(Order.PAY_STATUS_INIT);
			order.setPayType(Order.PAY_TYPE_MIX);
			order.setType(1);
			order.setDisplay(1);
			order.setPrice(subTotal);
			//成本价
			Long CostTotal = CalculateUtils.multiply(sku.getCostPrice(), quantity);
			order.setPayPrice(CostTotal);
			order.setOrderTime(date);
			order.setTimeoutTime(DateUtil.addMinute(date, 10));
			order.setCreateTime(date);
			order.setUpdateTime(date);
			
			order.setCaibi(payCaibi);
			order.setRmb(payRmb);
			order.setTubi(payTubi);
			
			//初始化订单状态
			order.setStatus(Order.STATUS_INIT);
			//生成订单与订单明细
			orderDao.insertSelective(order);
			orderItemDao.insertSelective(orderItem);
			
			String picUrl = attachFileService.backUpImage(item.getPicUrl());
			//生成交易快照
			TradingSnapshot ts = new TradingSnapshot();
			ts.setOrderItemId(orderItem.getId());
			ts.setTitle(item.getTitle());
			ts.setSubTitle(item.getSubTitle());
			ts.setImgUrl(picUrl);
			ts.setMarketPrice(item.getMarketPrice());
			ts.setSalePrice(sku.getSalePrice());
			ts.setVersion(sku.getVersion());
			ts.setWapUrl(item.getWapUrl());
			ts.setContent(item.getContent());
			ts.setCreateTime(date);
			tradingSnapshotDao.insertSelective(ts);
			
			//扣库存
			map.put("pageNum", quantity);
			List<Stock> stockList = stockDao.findInventoryBypageNum(map);
			if(null == stockList || quantity > stockList.size()){
				logger.error("库存异常，扣取失败");
				throw new Exception();
			}
			Stock editStock = new Stock();
			editStock.setStatus(2);
			
			ReceiveStock receiveStock = new ReceiveStock();
			receiveStock.setName(name);
			receiveStock.setUserId(userid);
			receiveStock.setRemoteId(no);
			receiveStock.setRemoteType(1);
			receiveStock.setCreateTime(date);
			receiveStock.setUpdateTime(date);
			//receiveStock.setPhone(phone);
			receiveStock.setSalePrice(sku.getSalePrice());
			receiveStock.setMarketPrice(item.getMarketPrice());
			receiveStock.setStatus(1);
			receiveStock.setReceiveTime(date);
			
			for (Stock stock : stockList) {
				editStock.setStockId(stock.getStockId());
				stockDao.update(editStock);
				receiveStock.setStockId(stock.getStockId());
				receiveStockDao.insertSelective(receiveStock);
			}
			Map<String, Object> map2 = new HashMap<>();
			map2.put("orderNo", order.getOrderNo());
			map2.put("userId", userid);
			map2.put("jobType","INTERNAL_ORDER");

			kafkaProducer.sendMessage(JSON.toJSONString(map2),appConfig.jobTopic);
			//推送消息
			try {
				String description =  Configuration.getProperty("push.order.generate", null);
				String title =  Configuration.getProperty("push.order.generate.title", null);
				Message message = new Message();
				message.setIsPush(true);
				message.setIsSMS(false);
				message.setIsYellow(false);
				message.setTitle(title);
				message.setPushInfo(String.format(description,name));
				logger.info("新增消息通知：userId:{},message:{}",userid,JSON.toJSONString(message));
				pushMessageService.saveMessage(RedSpot.MESSAGE_CENTER, userid, message);
			} catch (Exception e) {
				logger.error("订单生成推送消息发生异常:{}",e);
			}
			
			Map<String,Object> resultMap = new HashMap<String, Object>();
			resultMap.put("no", no);
			resultMap.put("source", item.getSource());
			
			resultMap.put("quantity", quantity);
			resultMap.put("marketPrice", sku.getSalePrice());
			resultMap.put("caibi", payCaibi);
			resultMap.put("tubi", payTubi);
			resultMap.put("rmb", payRmb);
			return result.toJSONString(0, "订单编号",resultMap);
		}catch (Exception e) {
			logger.error("生成订单失败:" + e.getMessage());
			throw new OrderException(ApiResultCode.SC_ORDER_ERROR,"生成订单失败",e.getMessage());
		}
	}

	
	private boolean checkStatus(Integer oldStatus,Integer newStatus){
			//0.生成/待付款，1.已付款，10，完成交易   20.超时关闭，30.删除
			switch (oldStatus) {
			case 0:
				if (Order.STATUS_PAY.equals(newStatus) || Order.STATUS_DELETE.equals(newStatus) ||Order.STATUS_TIMEOUT.equals(newStatus)){
					return true;
				}
			case 1:
				if (Order.STATUS_FINISH.equals(newStatus))
					return true;
			case 2:
				if (Order.STATUS_TIMEOUT.equals(newStatus)){
					return true;
				}
			case 20:
				if (Order.STATUS_DELETE.equals(newStatus))
					return true;
			default:
				return false;
			}
	}
	
	private boolean checkPayStatus(Integer oldStatus,Integer newStatus){
		//-2.支付取消，-1.支付失败，0.待支付，1.支付中，2支付成功
		switch (oldStatus) {
		case 0:
			if(Order.PAY_STATUS_ING.equals(newStatus) || Order.PAY_STATUS_CANCEL.equals(newStatus))
				return true;
		case 1:
			if(Order.PAY_STATUS_FINISH.equals(newStatus) || Order.PAY_STATUS_FAIL.equals(newStatus))
				return true;
		case -1:
			if(Order.PAY_STATUS_ING.equals(newStatus) || Order.PAY_STATUS_CANCEL.equals(newStatus))
				return true;
		case -2:
			if(Order.PAY_STATUS_ING.equals(newStatus))
				return true;
		default:
			return false;
		}
	}



	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.server.OrderService#cancelPay(java.lang.String)
	 */
	@Override
	public String cancelPay(String orderNo) throws OrderException {
		try {
			AssertUtil.notNull(orderNo, "订单号不能为空");
			ApiResult<Boolean> result = new ApiResult<Boolean>();
			Order order = orderDao.selectByPrimaryKey(orderNo);
			if(null == order){
				 return result.toJSONString(ApiResultCode.ORDER_QUERY_FAILE, "找不到订单");
			}
			
			Integer oldStatus = order.getPayStatus();
			Integer newStatus = Order.PAY_STATUS_CANCEL;
			
			if(checkPayStatus(oldStatus,newStatus)){
				Order editorder = new Order();
				editorder.setOrderNo(orderNo);
				editorder.setPayStatus(newStatus);
				orderDao.updateByPrimaryKeySelective(editorder);
			}
			 return result.toJSONString(0, "success");
		}catch (Exception e) {
			logger.error("取消支付失败:" + e.getMessage());
			throw new OrderException(ApiResultCode.ORDER_CANCELPAY_FAILE,e.getMessage());
		}
	}



	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.server.OrderService#timeOut(java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "OrderException" })
	public boolean timeOut(Long userid, String orderNo) throws OrderException {
		try {
			AssertUtil.notNull(orderNo, "订单号不能为空");
			Order order = orderDao.selectByPrimaryKey(orderNo);
			if(null == order){
				logger.error("找不到该订单:" + orderNo);
				return false;
			}
			Integer oldStatus = order.getStatus();
			Integer newStatus = Order.STATUS_TIMEOUT;
			
			if(checkStatus(oldStatus,newStatus)){	//判断是否可以关闭订单
				Order editorder = new Order();
				editorder.setOrderNo(orderNo);
				editorder.setStatus(newStatus);
				editorder.setUpdateTime(new Date());
				orderDao.updateByPrimaryKeySelective(editorder);		//关闭订单

				//
				Map<String, Object> map = new HashMap<>();
				map.put("user_id", userid);
				map.put("remote_id", orderNo);
				List<ReceiveStock> receiveStocks = receiveStockDao.selectReceiveStockByOrderNoAndUserId(map);//获取到被领的券的列表
				for (ReceiveStock receiveStock : receiveStocks) {
					Long stock_id = receiveStock.getStockId();
					Stock stock = stockDao.selectByPrimaryKey(stock_id);

					Integer status = receiveStock.getStatus();
					if(ReceiveStock.PRERECEIVE.equals(status)) {
						receiveStock.setStatus(ReceiveStock.GIVEBACK);
						receiveStock.setUpdateTime(new Date());
						receiveStockDao.updateByPrimaryKeySelective(receiveStock);
						if(stock != null ){
							stock.setStatus(Stock.ONSALE);
							stockDao.updateByPrimaryKeySelective(stock);
						}
					}
				}
			}
			return true;
		}catch (Exception e) {
			logger.error("关闭超时订单失败,异常：{},订单号：{}", e.getMessage(),orderNo);
			throw new OrderException(ApiResultCode.CLOSE_ORDER_ERROR,e.getMessage());
		}
	}


	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.server.OrderService#timeOut(java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "OrderException" })
	public boolean timeOut2(Long userid, String orderNo) throws OrderException {
		try {
			AssertUtil.notNull(orderNo, "订单号不能为空");
			Order order = orderDao.selectByPrimaryKey(orderNo);
			if(null == order){
				logger.error("找不到该订单:" + orderNo);
				return false;
			}
			
			if(order.getStatus().equals(Order.STATUS_PAY) 
					|| order.getStatus().equals(Order.STATUS_FINISH)){	//判断是否可以关闭订单
				logger.info("订单不需执行超时:" + orderNo);
				return false;
			}
			Order editorder = new Order();
			editorder.setOrderNo(orderNo);
			editorder.setStatus(Order.STATUS_TIMEOUT);
			editorder.setUpdateTime(new Date());
			orderDao.updateByPrimaryKeySelective(editorder);//更新订单

			Map<String, Object> map = new HashMap<>();
			map.put("user_id", userid);
			map.put("remote_id", orderNo);
			List<ReceiveStock> receiveStocks = receiveStockDao.selectReceiveStockByOrderNoAndUserId(map);//获取到被领的券的列表
			for (ReceiveStock receiveStock : receiveStocks) {
				Long stock_id = receiveStock.getStockId();
				Stock stock = stockDao.selectByPrimaryKey(stock_id);

				Integer status = receiveStock.getStatus();
				if(ReceiveStock.PRERECEIVE.equals(status)) {
					receiveStock.setStatus(ReceiveStock.GIVEBACK);
					receiveStock.setUpdateTime(new Date());
					receiveStockDao.updateByPrimaryKeySelective(receiveStock);
					if(stock != null ){
						stock.setStatus(Stock.ONSALE);
						stockDao.updateByPrimaryKeySelective(stock);
					}
				}
			}
			
			return true;
		}catch (Exception e) {
			logger.error("关闭超时订单失败,异常：{},订单号：{}", e.getMessage(),orderNo);
			throw new OrderException(ApiResultCode.CLOSE_ORDER_ERROR,e.getMessage());
		}
	}

	
	
	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.server.OrderService#delOrder(java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "OrderException" })
	public String delOrder(Long userId,String orderNo) throws OrderException {
		try {
			//修改订单状态为   已删除
			AssertUtil.hasLength(orderNo, "订单号不能为空");
			ApiResult<Boolean> result = new ApiResult<Boolean>();
			Order order = orderDao.selectByPrimaryKey(orderNo);
			if(null == order){
				 return result.toJSONString(ApiResultCode.ORDER_QUERY_FAILE, "找不到订单");
			}
			
			Integer oldStatus = order.getStatus();
			Integer newStatus = Order.STATUS_DELETE;
			
			if(checkStatus(oldStatus,newStatus)){
				Order editorder = new Order();
				editorder.setOrderNo(orderNo);
				editorder.setStatus(newStatus);
				editorder.setDeleteTime(new Date());
				orderDao.updateByPrimaryKeySelective(editorder);
				
				
				Map<String, Object> map = new HashMap<>();
				map.put("user_id", userId);
				map.put("remote_id", orderNo);
				List<ReceiveStock> receiveStocks = receiveStockDao.selectReceiveStockByOrderNoAndUserId(map);//获取到被领的券的列表
				for (ReceiveStock receiveStock : receiveStocks) {
					Long stock_id = receiveStock.getStockId();
					Stock stock = stockDao.selectByPrimaryKey(stock_id);

					Integer status = receiveStock.getStatus();
					if(ReceiveStock.PRERECEIVE.equals(status)) {
						receiveStock.setStatus(ReceiveStock.GIVEBACK);
						receiveStock.setUpdateTime(new Date());
						receiveStockDao.updateByPrimaryKeySelective(receiveStock);
						if(stock != null ){
							stock.setStatus(Stock.ONSALE);
							stockDao.updateByPrimaryKeySelective(stock);
						}
					}
				}
			}
			 return result.toJSONString(0, "success");
		}catch (Exception e) {
			logger.error("删除订单失败:" + e.getMessage());
			throw new OrderException(ApiResultCode.ORDER_DELETE_FAILE, "删除订单失败");
		}
	}
	
	

	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.server.OrderService#checkUserAndOrder(java.lang.Long, java.lang.String)
	 */
	@Override
	public boolean checkUserAndOrder(Long userid, String orderNo)
			throws OrderException {
		try {
			AssertUtil.notNull(userid,"用户ID不能为空");
			AssertUtil.hasLength(orderNo, "订单号不能为空");
			
			Order order = orderDao.selectByPrimaryKey(orderNo);
			if(null == order || null == order.getUserId()){
				return false;
			}
			if(userid.equals(order.getUserId())){
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("订单操作验证用户身份失败:" + e.getMessage());
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.server.OrderService#checkInventory(java.lang.Long, com.caitu99.service.goods.domain.Sku, java.lang.Integer)
	 */
	@Override
	public Integer queryInventory(Sku sku)
			throws OrderException {
		try {
			AssertUtil.notNull(sku, "sku不能为空");
			//查询库存
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("itemId", sku.getItemId());
			map.put("skuId", sku.getSkuId());
			return stockDao.countInventory(map);
		} catch (Exception e) {
			logger.error("库存查询失败:" + e.getMessage());
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.service.OrderService#queryOrder(java.lang.String)
	 */
	@Override
	public Order queryOrder(String orderNo) {
		return orderDao.selectByPrimaryKey(orderNo);
	}

	@Override
	public String addOrderByThird(Long userid, Long skuId, Integer quantity)
			throws OrderException {
		try {
			ApiResult<Map<String, Object>> result = new ApiResult<Map<String, Object>>();
			Sku sku = skuDao.selectByPrimaryKey(skuId);
			if(null == sku){
				return result.toJSONString(ApiResultCode.ORDER_SKU_NONE, "商品明细为空");
			}
			
			Long subTotal = CalculateUtils.multiply(sku.getSalePrice(), quantity);
		
			Date date = new Date();
			
			//获取用户数据及商品数据
			Item item = itemDao.selectByPrimaryKey(sku.getItemId());
			String name = item.getTitle();
			String headerNo = "";
			if(3==item.getSource()){
				headerNo="C";
			}else if(4==item.getSource()){
				headerNo="U";
			}else {
				headerNo="B";
			}
			
			//生成订单号    移动商城订单号  C  
			String no = Order.getOrderNoBySing(userid,headerNo);
		
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderNo(no);
			orderItem.setItemId(sku.getItemId());
			orderItem.setSkuId(skuId);
			orderItem.setName(name);
			orderItem.setPrice(sku.getSalePrice());
			orderItem.setQuantity(quantity);
			orderItem.setCreateTime(date);
			orderItem.setUpdateTime(date);
		
			//初始化订单数据
			Order order = new Order();
			order.setOrderNo(no);
			order.setUserId(userid);
			order.setName(name);
			order.setPayStatus(Order.STATUS_DELETE);
			order.setDisplay(1);
			//积分
			order.setPayType(Order.PAY_TYPE_F);
			order.setType(item.getSource());
			order.setPrice(subTotal);
			order.setPayPrice(subTotal);
			order.setOrderTime(date);
			order.setTimeoutTime(DateUtil.addMinute(date, 10));
			order.setCreateTime(date);
			order.setUpdateTime(date);
			
			//初始化订单状态
			order.setStatus(Order.STATUS_DELETE);
			//生成订单与订单明细
			orderDao.insertSelective(order);
			orderItemDao.insertSelective(orderItem);
			
			String picUrl = attachFileService.backUpImage(item.getPicUrl());
			//生成交易快照
			TradingSnapshot ts = new TradingSnapshot();
			ts.setOrderItemId(orderItem.getId());
			ts.setTitle(item.getTitle());
			ts.setSubTitle(item.getSubTitle());
			ts.setImgUrl(picUrl);
			ts.setMarketPrice(item.getMarketPrice());
			ts.setSalePrice(sku.getSalePrice());
			ts.setVersion(sku.getVersion());
			ts.setWapUrl(item.getWapUrl());
			ts.setContent(item.getContent());
			ts.setCreateTime(date);
			tradingSnapshotDao.insertSelective(ts);
			
			if(Item.SOURCE_JT.equals(item.getSource())){
				OrdernoAddr oa = new OrdernoAddr();
				oa.setOrderno(no);
				oa.setCreateTime(date);
				oa.setUpdateTime(date);
				ordernoAddrDao.insertSelective(oa);
			}
		
			Map<String,Object> resultMap = new HashMap<String, Object>();
			resultMap.put("no", no);
			resultMap.put("source", item.getSource());
			return result.toJSONString(0, "订单编号",resultMap);
		}catch (Exception e) {
			logger.error("生成订单失败:" + e.getMessage());
			throw new OrderException(ApiResultCode.SC_ORDER_ERROR,"生成订单失败",e.getMessage());
		}
	}

	@Override
	public String processOrderByThird(Integer flag,Long userid, String no, String outNo)
			throws OrderException {
		try {
			ApiResult<String> result = new ApiResult<String>();
			Order order = orderDao.selectByPrimaryKey(no);
			Date date = new Date();
			Order editOrder = new Order();
			editOrder.setOrderNo(no);
			if(1==flag){
				Long outprice = order.getPayPrice();
				editOrder.setStatus(Order.STATUS_FINISH);
				editOrder.setPayStatus(Order.PAY_STATUS_FINISH);
				editOrder.setOutPrice(outprice + "分");
				editOrder.setOutNo(outNo);
				editOrder.setPayTime(date);
				editOrder.setUpdateTime(date);
			}else{
				editOrder.setStatus(Order.STATUS_DELETE);
			}
			orderDao.updateByPrimaryKeySelective(editOrder);
			return result.toJSONString(0, "success","");
		} catch (Exception e) {
			logger.error("处理订单失败:" + e.getMessage());
			throw new OrderException(ApiResultCode.SC_ORDER_ERROR,"处理订单失败",e.getMessage());
		}
	}

	@Override
	public Map<String, Object> getToPayByThird(String orderNo)
			throws OrderException {
		try {
			Map<String,Object> map = new HashMap<String, Object>();
			Order order = orderDao.selectByPrimaryKey(orderNo);
		
			List<OrderItem> oiList = orderItemDao.listByOrderNo(orderNo);
			
			oiList.get(0).getItemId();
			
			GoodProp queryGoodProp = new GoodProp();
			queryGoodProp.setItemId(oiList.get(0).getItemId());
			queryGoodProp.setUseType(0);
			List<GoodProp> goodPropList = goodPropDao.findPropByItemId(queryGoodProp);
			
			if(9 == order.getType()){
				String productid = ordernoAddrService.getProidByItemidAndOrderno(oiList.get(0).getItemId(), orderNo);
				GoodProp gp = new GoodProp();
				gp.setName("prodid");
				gp.setValue(productid);
				goodPropList.add(gp);
				Item item = itemDao.selectByPrimaryKey(oiList.get(0).getItemId());
				GoodProp gp2 = new GoodProp();
				gp2.setName("prodname");
				gp2.setValue(item.getTitle());
				goodPropList.add(gp2);
			}
			
			map.put("price", order.getPayPrice());
			map.put("userid", order.getUserId());
			map.put("orderNo", orderNo);
			map.put("prop", goodPropList);
			//购买数量
			map.put("quantity", oiList.get(0).getQuantity());
			//单件价格
			map.put("price", oiList.get(0).getPrice());
			return map;
		} catch (Exception e) {
			logger.error("订单查询失败:" + e.getMessage());
			throw new OrderException(ApiResultCode.ORDER_QUERY_FAILE,"订单查询失败",e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see com.caitu99.service.transaction.service.OrderService#addFreeTradeOrder(java.lang.Long, java.lang.Long, java.lang.Integer, java.lang.Long)
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "OrderException" })
	@Override
	public String addFreeTradeOrder(Long userId, Long skuId, Integer quantity) {
		ApiResult<String> result = new ApiResult<String>();
		try{
			Sku sku = skuDao.selectByPrimaryKey(skuId);
			Item item = itemDao.selectByPrimaryKey(sku.getItemId());
			User user = userDao.selectByPrimaryKey(userId);
			//1.普通用户的交易，2黄牛的交易
			Integer ishn = 1;
			if(user.getIshn().intValue() == 1){//黄牛用户
				ishn = 3;
			}
			//所需总财币
			Long subTotal = CalculateUtils.multiply(item.getSalePrice(), quantity);
			
			boolean isEnough = accountService.isEnough(userId, subTotal);
			if(!isEnough){
				return result.toJSONString(-1, "您的财币不足");
			}
			/*还需要验证消费总额？
	        // 验证消费总额
	        Integer count = transactionRecordService.getLastDayConsumes(userId);
	        if (count + subTotal > appConfig.maxConsumesPerDay) {
	            String msg = "抱歉，您今天的累积消费财币已超过限额（" + appConfig.maxConsumesPerDay + "财币）";
	            return result.toJSONString(2219, msg);
	        }
			*/
			
			//添加freeTrade记录
			FreeTrade freeTrade = addFreeTrade(skuId, quantity, item, ishn);
			//用户收货地址
			Address address = addressService.selectByUserId(userId);
			//order表新增记录
			for(int i=0; i<quantity.intValue(); i++){
				saveSingleOrder(userId, quantity, sku, item, freeTrade,
						address);
			}
			
			//扣财币
			Account account = accountService.selectByUserId(userId);
			TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
			transactionRecordDto.setChannel(3);
			transactionRecordDto.setComment("购买特卖商品");
			transactionRecordDto.setInfo("fen生活");
			transactionRecordDto.setOrderNo(freeTrade.getId().toString());
			transactionRecordDto.setPicUrl("");
			transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNoWithRandom(
					"TM", String.valueOf(userId)));
			transactionRecordDto.setType(1);//消费
			transactionRecordDto.setUserId(userId);
			transactionRecordDto.setTotal(subTotal);
			transactionRecordDto.setSource(item.getSource());

			// 添加交易记录
			Long recordId = transactionRecordService.saveTransaction(transactionRecordDto);
			// 添加交易明细
			accountDetailService.saveAccountDetail(recordId, transactionRecordDto,
					TradeType.REDUCE);
			// 更新账户
			accountService.updateAccount(account, transactionRecordDto, TradeType.REDUCE);
			
			/*
			//推送消息
			try {
				String description =  Configuration.getProperty("push.order.generate", null);
				String title =  Configuration.getProperty("push.order.generate.title", null);
				Message message = new Message();
				message.setIsPush(true);
				message.setIsSMS(false);
				message.setIsYellow(false);
				message.setTitle(title);
				message.setPushInfo(String.format(description,item.getTitle()));
				logger.info("新增消息通知：userId:{},message:{}",userId,JSON.toJSONString(message));
				pushMessageService.saveMessage(RedSpot.MESSAGE_CENTER, userId, message);
			} catch (Exception e) {
				logger.error("订单生成推送消息发生异常:{}",e);
			}
			
			*/
			
			return result.toJSONString(0, "自由市场交易订单编号",freeTrade.getId().toString());
		}catch (Exception e) {
			logger.error("新增订单异常:{}",e);
			throw new OrderException(ApiResultCode.SC_ORDER_ERROR,"生成订单失败",e.getMessage());
		}
	}

	private FreeTrade addFreeTrade(Long skuId, Integer quantity, Item item,
			Integer ishn) {
		FreeTrade freeTrade = new FreeTrade();
		Date date = new Date();
		//freeTrade.setId(System.currentTimeMillis()+random.nextLong());
		freeTrade.setAvailableQuantity(quantity.longValue());
		freeTrade.setFreezeQuantity(0L);
		freeTrade.setItemId(item.getItemId());
		freeTrade.setPayPrice(item.getFreeTradePrice());//自由市场兑换所需的积分
		freeTrade.setPrice(item.getExchangePrice());//积分兑财币时，能兑多少钱（单位财币）
		freeTrade.setRemoteId(item.getSource().longValue());
		freeTrade.setSkuId(skuId);
		freeTrade.setTotalQuantity(quantity.longValue());
		freeTrade.setType(ishn);//1.普通用户的交易，3黄牛的交易
		freeTrade.setUpdateTime(date);
		freeTrade.setCreateTime(date);
		freeTrdeDao.insert(freeTrade);
		return freeTrade;
	}

	private void saveSingleOrder(Long userId, Integer quantity, Sku sku,
			Item item, FreeTrade freeTrade, Address address) {
		Order order = new Order();
		Date date = new Date();
		order.setCreateTime(date);
		//order.setDeleteTime(deleteTime);
		order.setDisplay(1);//是否展示：1.展示，2不展示
		order.setFreeTradeId(freeTrade.getId());
		//order.setGoodsCount(goodsCount);
		order.setMemo("用户使用财币购买该商品");
		order.setName(item.getTitle());
		order.setOrderNo(this.getOrderNo(userId,"TM"));
		order.setOrderTime(date);
		//order.setOutNo("");
		//order.setOutPrice(""); 
		order.setPayPrice(sku.getSalePrice());
		order.setPayStatus(2);//-2.支付取消，-1.支付失败，0.待支付，1.支付中，2支付成功
		order.setPayType(1);//付款方式    1：财币2：人民币3：积分  4：财币+人民币
		order.setPrice(sku.getSalePrice());
		order.setStatus(1);//状态：0.生成/待付款，1.已付款，10，完成交易   20.超时关闭，30.删除
		//order.setTimeoutTime(DateUtil.addMinute(date, 10));
		order.setType(1);//订单类型  1.商城订单  2.其它商家引流商品，3 移动商城，4.联通商城，5.招商银行   mark：统一为商城订单，订单列表中不显示
		order.setUpdateTime(date);
		order.setUserId(userId);
		order.setPayTime(date);
		orderDao.insert(order);
		
		OrderItem orderItem = new OrderItem();
		orderItem.setOrderNo(order.getOrderNo());
		orderItem.setItemId(sku.getItemId());
		orderItem.setSkuId(sku.getSkuId());
		orderItem.setName(item.getTitle());
		orderItem.setPrice(sku.getSalePrice());
		orderItem.setQuantity(1);//订单数量为1
		orderItem.setCreateTime(date);
		orderItem.setUpdateTime(date);
		orderItemDao.insertSelective(orderItem);
		
		String picUrl = attachFileService.backUpImage(item.getPicUrl());
		//生成交易快照
		TradingSnapshot ts = new TradingSnapshot();
		ts.setOrderItemId(orderItem.getId());
		ts.setTitle(item.getTitle());
		ts.setSubTitle(item.getSubTitle());
		ts.setImgUrl(picUrl);
		ts.setMarketPrice(item.getMarketPrice());
		ts.setSalePrice(sku.getSalePrice());
		ts.setVersion(sku.getVersion());
		ts.setWapUrl(item.getWapUrl());
		ts.setContent(item.getContent());
		ts.setCreateTime(date);
		tradingSnapshotDao.insertSelective(ts);
		
		//收货地址快照表
		OrderAddress orderAddress = new OrderAddress();
		orderAddress.setArea(address.getArea());
		orderAddress.setCity(address.getCity());
		orderAddress.setCreateTime(date);
		orderAddress.setDetailed(address.getDetailed());
		orderAddress.setMobile(address.getMobile());
		orderAddress.setName(address.getName());
		orderAddress.setOrderNo(order.getOrderNo());
		orderAddress.setProvince(address.getProvince());
		orderAddress.setUpdateTime(date);
		orderAddress.setZipCode(address.getZipCode());
		orderAddress.setIdCard(address.getIdCard());
		orderAddressService.insert(orderAddress);
	}

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getOrderNo 
	 * @param userId
	 * @return
	 * @date 2016年1月20日 下午3:43:53  
	 * @author ws
	*/
	public String getOrderNo(Long userId,String sign) {
		Random random = new Random();
		String orderNo = new StringBuffer(sign)
				.append(random.nextInt(100))
        		.append(userId)
				.append(System.currentTimeMillis())
        		.append(random.nextInt(10)).toString();
		
		return orderNo;
	}
	
	
//	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "OrderException" })
//	@Override
//	public String exchangeTradeOrder(Long userId, Integer quantity,Long freeTradeId) {
//		try {
//			ApiResult<Map<String,Object>> result = new ApiResult<Map<String,Object>>();
//			//验证库存
//			FreeTrade ft = freeTrdeDao.selectByPrimaryKey(freeTradeId);
//			if(null == ft){
//				return result.toJSONString(ApiResultCode.FREE_TRADE_NULL_ERROR, "找不到该条记录");
//			}
//			
//			Item item = itemDao.selectByPrimaryKey(ft.getItemId());
//		
//			Date date = new Date();
//			
//			FreeInventoryRecord queryRecord = new FreeInventoryRecord();
//			queryRecord.setFreeTradeId(freeTradeId);
//			queryRecord.setEndTime(date);
//			Integer newInventory = freeInventoryRecordDao.findFreeInventoryRecordByTime(queryRecord);
//			if(null == newInventory){
//				newInventory = 0;
//			}
//			
//			//剩余库存  = 对兑换数量 - 预扣库存
//			if(quantity.intValue() > ft.getAvailableQuantity().intValue() - newInventory.intValue()){
//				return result.toJSONString(ApiResultCode.ORDER_INVENTORY_NONE, "库存不足");
//			}
//			
//			//第三方商城积分
//			Long subTotal = CalculateUtils.multiply(ft.getPayPrice(), quantity);
//			
//			String dateString = appconfig.freeTardeTntegeral;
//			Date paramDate = ProccessUtil.getTime(dateString);
//			
//			//生成订单号    移动商城订单号 TH
//			String no = Order.getOrderNoBySing(userId,"TH");
//			
//			FreeInventoryRecord inRecord = new FreeInventoryRecord();
//			inRecord.setFreeTradeId(freeTradeId);
//			inRecord.setUserId(userId);
//			inRecord.setNum(quantity);
//			inRecord.setEndTime(paramDate);
//			inRecord.setCreateTime(date);
//			inRecord.setUpdateTime(date);
//			inRecord.setOrderNo(no);
//			freeInventoryRecordDao.insertSelective(inRecord);
//			
//			Sku sku = skuDao.selectByPrimaryKey(ft.getSkuId());
//			//生成 订单
//			Order order = new Order();
//		
//			order.setDisplay(2);
//			order.setFreeTradeId(freeTradeId);
//			order.setType(ft.getRemoteId().intValue());
//			order.setName(item.getTitle());
//			order.setStatus(Order.STATUS_DELETE);
//			order.setPayType(3);
//			order.setPayStatus(0);
//			order.setPayPrice(subTotal);
//			order.setPrice(subTotal);
//			order.setOrderNo(no);
//			order.setUserId(userId);
//			order.setMemo("自由交易兑换积分");
//			order.setOrderTime(date);
//			order.setCreateTime(date);
//			order.setUpdateTime(date);
//			orderDao.insertSelective(order);
//			
//			OrderItem orderItem = new OrderItem();
//			orderItem.setOrderNo(no);
//			orderItem.setItemId(ft.getItemId());
//			orderItem.setSkuId(ft.getSkuId());
//			orderItem.setName(item.getTitle());
//			orderItem.setPrice(ft.getPayPrice());
//			orderItem.setQuantity(quantity);
//			orderItem.setCreateTime(date);
//			orderItem.setUpdateTime(date);
//			orderItemDao.insertSelective(orderItem);
//
//			String picUrl = attachFileService.backUpImage(item.getPicUrl());
//			//生成交易快照
//			TradingSnapshot ts = new TradingSnapshot();
//			ts.setOrderItemId(orderItem.getId());
//			ts.setTitle(item.getTitle());
//			ts.setSubTitle(item.getSubTitle());
//			ts.setImgUrl(picUrl);
//			ts.setMarketPrice(item.getMarketPrice());
//			ts.setSalePrice(sku.getSalePrice());
//			ts.setVersion(sku.getVersion());
//			ts.setWapUrl(item.getWapUrl());
//			ts.setContent(item.getContent());
//			ts.setCreateTime(date);
//			tradingSnapshotDao.insertSelective(ts);
//			
//			
//			
//			StringBuilder redirct = new StringBuilder();
//			//中兴银行
//			if(6==ft.getRemoteId().intValue()){
//				redirct.append("/citic/citic_login.html?order_no=");
//			}else if(8==ft.getRemoteId().intValue()){//建设特卖
//				redirct.append("/ccb/login.html?order_no=");
//			}
//			redirct.append(no);
//			Map<String,Object> resultMap = new HashMap<String, Object>();
//			resultMap.put("redirect", redirct.toString());
//			return result.toJSONString(0, "success",resultMap);
//			
////			Map<String,Object> resultMap = new HashMap<String, Object>();
////			resultMap.put("no", no);
////			return result.toJSONString(0, "success",resultMap);
//			
//		} catch (Exception e) {
//			logger.error("新增订单异常:{}",e);
//			throw new OrderException(ApiResultCode.SC_ORDER_ERROR,"生成订单失败",e.getMessage());
//		}
//	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "OrderException" })
	@Override
	public String exchangeTradeOrder(Long userId, Integer quantity,Long freeTradeId) {
		try {
			//验证库存
			FreeTrade ft = freeTrdeDao.selectByPrimaryKey(freeTradeId);
			if(null == ft){
				return ApiResultCode.FREE_TRADE_NULL_ERROR.toString();
			}
			
			Item item = itemDao.selectByPrimaryKey(ft.getItemId());
		
			Date date = new Date();
			
			FreeInventoryRecord queryRecord = new FreeInventoryRecord();
			queryRecord.setFreeTradeId(freeTradeId);
			queryRecord.setEndTime(date);
			Integer newInventory = freeInventoryRecordDao.findFreeInventoryRecordByTime(queryRecord);
			if(null == newInventory){
				newInventory = 0;
			}
			
			//剩余库存  = 对兑换数量 - 预扣库存
			if(quantity.intValue() > ft.getAvailableQuantity().intValue() - newInventory.intValue()){
				return ApiResultCode.ORDER_INVENTORY_NONE.toString();
			}
			
			//第三方商城积分
			Long subTotal = CalculateUtils.multiply(ft.getPayPrice(), quantity);
			
			String dateString = appconfig.freeTardeTntegeral;
			Date paramDate = ProccessUtil.getTime(dateString);
			
			//生成订单号    移动商城订单号 TH
			String no = Order.getOrderNoBySing(userId,"TH");
			
			FreeInventoryRecord inRecord = new FreeInventoryRecord();
			inRecord.setFreeTradeId(freeTradeId);
			inRecord.setUserId(userId);
			inRecord.setNum(quantity);
			inRecord.setEndTime(paramDate);
			inRecord.setCreateTime(date);
			inRecord.setUpdateTime(date);
			inRecord.setOrderNo(no);
			freeInventoryRecordDao.insertSelective(inRecord);
			
			Sku sku = skuDao.selectByPrimaryKey(ft.getSkuId());
			//生成 订单
			Order order = new Order();
		
			order.setDisplay(2);
			order.setFreeTradeId(freeTradeId);
			order.setType(ft.getRemoteId().intValue());//对应item source 
			order.setName(item.getTitle());
			order.setStatus(Order.STATUS_DELETE);
			order.setPayType(3);
			order.setPayStatus(0);
			order.setPayPrice(subTotal);
			order.setPrice(subTotal);
			order.setOrderNo(no);
			order.setUserId(userId);
			order.setMemo("自由交易兑换积分");
			order.setOrderTime(date);
			order.setCreateTime(date);
			order.setUpdateTime(date);
			orderDao.insertSelective(order);
			
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderNo(no);
			orderItem.setItemId(ft.getItemId());
			orderItem.setSkuId(ft.getSkuId());
			orderItem.setName(item.getTitle());
			orderItem.setPrice(ft.getPayPrice());
			orderItem.setQuantity(quantity);
			orderItem.setCreateTime(date);
			orderItem.setUpdateTime(date);
			orderItemDao.insertSelective(orderItem);

			String picUrl = attachFileService.backUpImage(item.getPicUrl());
			//生成交易快照
			TradingSnapshot ts = new TradingSnapshot();
			ts.setOrderItemId(orderItem.getId());
			ts.setTitle(item.getTitle());
			ts.setSubTitle(item.getSubTitle());
			ts.setImgUrl(picUrl);
			ts.setMarketPrice(item.getMarketPrice());
			ts.setSalePrice(sku.getSalePrice());
			ts.setVersion(sku.getVersion());
			ts.setWapUrl(item.getWapUrl());
			ts.setContent(item.getContent());
			ts.setCreateTime(date);
			tradingSnapshotDao.insertSelective(ts);
			
			return no;
			
//			StringBuilder redirct = new StringBuilder();
//			//中兴银行
//			if(6==ft.getRemoteId().intValue()){
//				redirct.append("/citic/citic_login.html?order_no=");
//			}else if(8==ft.getRemoteId().intValue()){//建设特卖
//				redirct.append("/ccb/login.html?order_no=");
//			}
//			redirct.append(no);
//			Map<String,Object> resultMap = new HashMap<String, Object>();
//			resultMap.put("redirect", redirct.toString());
//			return result.toJSONString(0, "success",resultMap);
			
//			Map<String,Object> resultMap = new HashMap<String, Object>();
//			resultMap.put("no", no);
//			return result.toJSONString(0, "success",resultMap);
			
		} catch (Exception e) {
			logger.error("新增订单异常:{}",e);
			throw new OrderException(ApiResultCode.SC_ORDER_ERROR,"生成订单失败",e.getMessage());
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = { "OrderException" })
	@Override
	public String processTradeOrder(Integer flag, Long userid, String no,
			String outNo) throws OrderException {
		try {
			ApiResult<String> result = new ApiResult<String>();
			
		    String noFalg = no.substring(0, 2);
			
			if(0==flag){
				if("TH".equals(noFalg)){
				//删除预扣
				freeInventoryRecordDao.deleteByOrderNo(no);
			   }
				return result.toJSONString(0, "success","");
			}
				
			Order order = orderDao.selectByPrimaryKey(no);
			Date date = new Date();
				
				if("TH".equals(noFalg)){
				   	FreeTrade ft = freeTrdeDao.selectByPrimaryKey(order.getFreeTradeId());
					List<OrderItem> oiList = orderItemDao.listByOrderNo(no);
					//实际数量 = 可兑换数量-订单数量
					Long quantity = CalculateUtils.getDifference(Long.valueOf(ft.getAvailableQuantity()), Long.valueOf(oiList.get(0).getQuantity()));
					//更新实际库存
					this.updateQuantityByTrade(order.getFreeTradeId(),quantity,date);
					//生成订单自由交易关系
					this.generateRelation(outNo,no,date);
					
					Long inegral = CalculateUtils.multiply(ft.getPrice(), oiList.get(0).getQuantity());
					String info = this.getTransactionInfo(order.getType());
					//充财币
					this.addIntegral(userid, inegral, info); 
					//升级经验    100
					ExpData expdata = new ExpData();
					expdata.setInegral(inegral);
					expdata.setUserId(userid);
					addExp.addExp(expdata, addExpByIshop);
					
					//删除预扣
					freeInventoryRecordDao.deleteByOrderNo(no);
				  }
					
		        	//第三方支付成功 更新订单
			    	Order editOrder = new Order();
					editOrder.setOrderNo(no);
					Long outprice = order.getPayPrice();
					editOrder.setStatus(Order.STATUS_FINISH);
					editOrder.setPayStatus(Order.PAY_STATUS_FINISH);
					editOrder.setOutPrice(outprice + "分");
					editOrder.setOutNo(outNo);
					editOrder.setPayTime(date);
					editOrder.setUpdateTime(date);
					orderDao.updateByPrimaryKeySelective(editOrder);
		
			return result.toJSONString(0, "success","");
		} catch (Exception e) {
			logger.error("自由交易市场，完成交易异常:{},{},{}",flag,no,userid,e);
			throw new OrderException(ApiResultCode.ORDER_STATUS_FAILE,"修改订单失败",e.getMessage());
		} 
	}

	
	private void updateQuantityByTrade(Long freeTradeId,Long quantity,Date date){
		FreeTrade queryft = new FreeTrade();
		queryft.setId(freeTradeId);
		queryft.setAvailableQuantity(quantity);
		queryft.setUpdateTime(date);
		//更新实际库存
		freeTrdeDao.updateByPrimaryKeySelective(queryft);
	}
	
	private void generateRelation(String outNo,String no,Date date){
		Order payOrder = new Order();
		payOrder.setOutNo(outNo);
		
		FreeTradeOrder fto = new FreeTradeOrder();
		fto.setCreateTime(date);
		
		List<FreeNoRecord> fnrList = freeNoRecordDao.findNoListByOrderNo(no);
		
		List<String> noList = JSON.parseArray(fnrList.get(0).getNoList(),String.class);
			
		for (String payno : noList) {
			payOrder.setOrderNo(payno);
			orderDao.updateByPrimaryKeySelective(payOrder);
			
			fto.setGoodOrderNo(payno);
			fto.setMoneyOrderNo(no);
			fto.setOutNo(outNo);
			freeTradeOrderDao.insertSelective(fto);
		}
	}
	
	
	private String getTransactionInfo(Integer type){
		String info = "";
		switch (type) {
		case 6:
			info = "中信银行()";
			break;
		case 8:
			info = "建设银行()";
			break;
		case 10:
			info = "交通银行()";
			break;
		case 12:
			info = "招商银行()";
			break;
		case 14:
			info = "天翼()";
			break;
		default:
			logger.error("订单数据异常：type"+type);
			break;
		}
		return info;
	}
	
	
	private AccountResult addIntegral(Long userid,Long inegral,String info){ 
		TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
		transactionRecordDto.setChannel(7);
		transactionRecordDto.setComment("兑入财币");
		transactionRecordDto.setInfo(info);
		transactionRecordDto.setOrderNo("");
		transactionRecordDto.setPicUrl("");
		transactionRecordDto.setTotal(inegral);
		transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNo(
				"TH", String.valueOf(userid)));
		transactionRecordDto.setType(2);
		transactionRecordDto.setUserId(userid);
		transactionRecordDto.setSource(21);//来源于自由交易
		return accountService.add(transactionRecordDto);
		
	}

	@Override
	public Map<String,Object> getGoodByTrade(String no,Long userid) throws OrderException {
		try {
			Order order = orderDao.selectByPrimaryKey(no);
			
			if(null == order){
				throw new Exception("订单为空");
			}
			List<OrderItem> oiList = orderItemDao.listByOrderNo(no);
			if(null == oiList || 0 >= oiList.size()){
				throw new Exception("订单明细为空");
			}
			
			GoodProp querygp = new GoodProp();
			querygp.setItemId(oiList.get(0).getItemId());
			querygp.setUseType(0);
			List<GoodProp> propList = goodPropDao.findPropByItemId(querygp);
			
			OrderAddress orderAddress = new OrderAddress();
			if(Item.SOURCE_ZHONGXIN.equals(order.getType())){
				
				User user = userDao.selectByPrimaryKey(userid);
				
				if(null == user.getMobile()){
					throw new OrderException(-1,"请先填绑定手机号");
				}

				String name ="匿名";
				if(null != user.getNick()){
					name=user.getNick();
				}
			
				orderAddress.setMobile(user.getMobile());
				orderAddress.setName(name);
				
				orderAddress.setProvince("江浙省");
				orderAddress.setCity("杭州市");
				orderAddress.setArea("天目山路");
				orderAddress.setDetailed("151号丁香公寓907");
				
				orderAddress.setZipCode("000000");
				
				//orderAddress = orderAddressDao.getAddressByOrderNo(no);
			}else if(Item.SOURCE_JS.equals(order.getType()) || Item.SOURCE_TY.equals(order.getType()) 
																	|| Item.SOURCE_PAB.equals(order.getType())){
				orderAddress = orderAddressDao.getAddressByOrderNo(no);
			}else{
				orderAddress = this.getOrderAddressForTrade(no,userid,order.getFreeTradeId(),oiList.get(0).getQuantity());
			}
		
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("quantity", oiList.get(0).getQuantity());
			map.put("price", oiList.get(0).getPrice());
			map.put("propList", propList);
			map.put("orderAddress", orderAddress);  
			map.put("itemId", oiList.get(0).getItemId());
	
			return map;
		} catch(OrderException ex){
			logger.info("用户未绑定手机号码:{}, {}", userid, no, ex);
			throw new OrderException(ex.getCode(),ex.getMessage());
		}catch (Exception e) {
			logger.error("查询商品属性异常:{}, {}", userid, no, e);
			throw new OrderException(ApiResultCode.FREE_TRADE_PROP_ERROR,"查询商品属性失败",e.getMessage());
		}
	}
	
	private OrderAddress getOrderAddressForTrade(String no,Long userid,Long freeTradeId,Integer quantity){
		
		Map<String,Object> queryMap = new HashMap<String, Object>();
		queryMap.put("freeTradeId", freeTradeId);
		queryMap.put("display", 1);
		List<Order> orderList = orderDao.findOrderByOutNo(queryMap);
		OrderAddress orderAddress = orderAddressDao.getAddressByOrderNo(orderList.get(0).getOrderNo());
		StringBuilder sb = new StringBuilder();
		List<String> noList = new ArrayList<String>();
		sb.append("CT"); 
		
		for (int i = 0; i < quantity.intValue(); i++) {
			String addNo = orderList.get(i).getOrderNo();
			noList.add(addNo);
			if(i != 0){
				sb.append(",");
			}
			sb.append(addNo.substring(addNo.length()-2,addNo.length()));
		}
		
		FreeNoRecord fnr = new FreeNoRecord();
		fnr.setNoList(JSON.toJSONString(noList));
		fnr.setOrderNo(no);
		fnr.setUserId(userid);
		fnr.setCreateTime(new Date());
		freeNoRecordDao.insertSelective(fnr);
		
		orderAddress.setDetailed(orderAddress.getDetailed() + sb.toString());
		
		return orderAddress;
	}

	@Override
	@Transactional
	public String addRealizeOrder(Long userId,  Long integel, Long realizationPlatformId, RealizeRecord realizeRecord)
			throws OrderException {
		try {
			
			ApiResult<Map<String, Object>> result = new ApiResult<Map<String, Object>>();
			if(null == integel || null == realizationPlatformId){
				throw new OrderException(ApiResultCode.REALIZE_ORDER_ERROR,"变现订单生成失败");
//				return result.toJSONString(ApiResultCode.REALIZE_ORDER_NULL, "参数为空：integel");
			}
			RealizePlatform  rf = realizationPlatformDao.selectByPrimaryKey(realizationPlatformId);

			//生成 变现订单号  R
			String no = Order.getOrderNoBySing(userId,"R");
			
			String title = "";
			if(realizeRecord.getRealizeDetailId() == -1){
				String memo = realizeRecord.getMemo();
				Long amountId = JSON.parseObject(memo).getLong("amountId");
				PhoneAmount phoneAmount = phoneAmountMapper.selectByPrimaryKey(amountId);
				
				title = rf.getName() + phoneAmount.getName() + "话费充值";
				
				//添加话费充值记录，便于统计单日话费充值累积额
				PhoneRechargeRecord phoneRechargeRecord = new PhoneRechargeRecord();
				phoneRechargeRecord.setCardno(phoneAmount.getAmount().intValue());
				phoneRechargeRecord.setFlag(1);
				phoneRechargeRecord.setOrderid(no);
				phoneRechargeRecord.setPhoneno(JSON.parseObject(memo).getString("phoneNo"));
				phoneRechargeRecord.setRechargeDate(new Date());
				phoneRechargeRecord.setResult(rf.getName() + "积分充值话费");
				phoneRechargeRecord.setUserId(Long.valueOf(userId));
				mobileRechargeRecordMapper.insert(phoneRechargeRecord);
			}else{
				title = rf.getName() + "积分变现";
			}
			
			Date date = new Date();
		
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderNo(no);
			orderItem.setItemId(0L);
			orderItem.setSkuId(0L);
			orderItem.setName(title);
			orderItem.setPrice(integel);
			orderItem.setQuantity(1);
			orderItem.setCreateTime(date);
			orderItem.setUpdateTime(date);
		
			//初始化订单数据
			Order order = new Order();
			order.setOrderNo(no);
			order.setUserId(userId);
			order.setName(title);
			order.setDisplay(1);
			//分
			order.setPayType(3);
			if(realizeRecord.getRealizeDetailId() == -1){
				order.setType(51);
				order.setStatus(Order.STATUS_FINISH);
				order.setPayStatus(Order.PAY_STATUS_FINISH);
			}else{
				order.setPayStatus(1);
				order.setType(50);//变现订单
				//初始化订单状态
				order.setStatus(50);
			}
			order.setPrice(integel);
			order.setPayPrice(integel);
			
			order.setMemo(rf.getIcon());
			
			order.setOrderTime(date);
			order.setTimeoutTime(DateUtil.addMinute(date, 10));
			order.setCreateTime(date);
			order.setUpdateTime(date);
			
			//生成订单与订单明细
			orderDao.insertSelective(order);
			orderItemDao.insertSelective(orderItem);
			
			/*//生成交易快照
			TradingSnapshot ts = new TradingSnapshot();
			ts.setOrderItemId(orderItem.getId());
			ts.setTitle(title);
			ts.setSubTitle(title);
			ts.setImgUrl(rf.getIcon());
			ts.setMarketPrice(integel);
			ts.setSalePrice(integel);
			ts.setVersion("000");
			ts.setWapUrl("");
			ts.setContent("");
			ts.setCreateTime(date);
			tradingSnapshotDao.insertSelective(ts);
		*/
			Map<String,Object> resultMap = new HashMap<String, Object>();
			resultMap.put("no", no);
			return result.toJSONString(0, "订单编号",resultMap);
			
		} catch (Exception e) {
			logger.error("变现订单生成失败:",e);
			throw new OrderException(ApiResultCode.REALIZE_ORDER_ERROR,"变现订单生成失败",e.getMessage());
		}
	}
	
	
	@Override
	public String addMileageOrder(Long userId, Long realizationPlatformId, Long airlineCompanyId, RealizeRecord realizeRecord)
			throws OrderException {
		try {
			
			ApiResult<Map<String, Object>> result = new ApiResult<Map<String, Object>>();
			if(null == realizeRecord || null == realizationPlatformId){
				return result.toJSONString(ApiResultCode.REALIZE_ORDER_NULL, "参数为空：integel");
			}
			RealizePlatform  rf = realizationPlatformDao.selectByPrimaryKey(realizationPlatformId);

			//生成 变现订单号  R
			String no = Order.getOrderNoBySing(userId,"R");
			AirlineCompany airlineCompany = airlineCompanyMapper.selectByPrimaryKey(airlineCompanyId);
	        
			String title = rf.getName() + realizeRecord.getCash() + airlineCompany.getShortName() + "里程充值";//交通银行500海航里程充值
			
			Date date = new Date();
		
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderNo(no);
			orderItem.setItemId(0L);
			orderItem.setSkuId(0L);
			orderItem.setName(title);
			orderItem.setPrice(realizeRecord.getIntegral());
			orderItem.setQuantity(1);
			orderItem.setCreateTime(date);
			orderItem.setUpdateTime(date);
		
			//初始化订单数据
			Order order = new Order();
			order.setOrderNo(no);
			order.setUserId(userId);
			order.setName(title);
			order.setDisplay(1);
			//分
			order.setPayType(3);
			
			order.setType(52);//里程充值
			order.setStatus(Order.STATUS_FINISH);
			order.setPayStatus(Order.PAY_STATUS_FINISH);
			
			order.setPrice(realizeRecord.getIntegral());
			order.setPayPrice(realizeRecord.getIntegral());
			
			order.setMemo(rf.getIcon());
			order.setOrderTime(date);
			order.setCreateTime(date);
			order.setUpdateTime(date);
			//生成订单与订单明细
			orderDao.insertSelective(order);
			orderItemDao.insertSelective(orderItem);
			return no;
			
		} catch (Exception e) {
			logger.error("变现订单生成失败:",e);
			throw new OrderException(ApiResultCode.REALIZE_ORDER_ERROR,"变现订单生成失败",e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		ApiResult<Map<String, Object>> result = new ApiResult<Map<String, Object>>();
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.put("no", "123");
		System.out.println(JSON.parseObject(JsonResult.getResult(result.toJSONString(0, "订单编号",resultMap), "data"),Map.class).get("no"));
	}

	@Override
	public String finishRealizeOrder(String no, Long userId) throws OrderException {
		try {
			ApiResult<String> result = new ApiResult<String>();
			if(null == no || null == userId){
				logger.error("参数为空：userId or no;userid:{},no:{}",userId,no);
				throw new OrderException(ApiResultCode.REALIZE_ORDER_NULL, "参数为空：userId or no");
//				return result.toJSONString(ApiResultCode.REALIZE_ORDER_NULL, "参数为空：userId or no");
			}
			
			Order order = orderDao.selectByPrimaryKey(no);
			if(null == order || !userId.equals(order.getUserId())){
				logger.error("找不到该订单;userid:{},order.getUserId():{}",userId,order.getUserId());
				throw new OrderException(ApiResultCode.REALIZE_ORDER_NULL, "找不到该订单");
//				return result.toJSONString(ApiResultCode.REALIZE_ORDER_NULL, "找不到该订单");
			}
			
			Date date = new Date();
			Order editorder = new Order();
			editorder.setOrderNo(no);
			editorder.setStatus(53);//已完成
			editorder.setUpdateTime(date);
			orderDao.updateByPrimaryKeySelective(editorder);
			return result.toJSONString(0, "success","成功");
		} catch (Exception e) {
			logger.error("完成订单失败:",e);
			throw new OrderException(ApiResultCode.REALIZE_FINISH_ERROR,"变现订单完成失败",e.getMessage());

		}
	}
	
	
	
	@Override
	@Transactional
	public String addNewbieActivityOrder(Long userId,Long caibi,Long tubi,Long rmb)throws OrderException {
		try {
			logger.info("生成新手任务订单,userid:{},caibu:{},tubi:{},rmb:{}",userId,caibi,tubi,rmb);
			if(null == userId){
				throw new OrderException(ApiResultCode.NEWBIE_ACTIVITY_ORDER_ERROR,"新手任务订单生成失败,参数userId不能为空");
			}
			
			//生成 新手任务订单号 AN
			String no = Order.getOrderNoBySing(userId,"AN");
			
			String title = "新手任务userid：" + userId;
			
			Date date = new Date();
		
			if(null != caibi && caibi.intValue() > 0){
				OrderItem orderItem = new OrderItem();
				orderItem.setOrderNo(no);
				orderItem.setItemId(0L);
				orderItem.setSkuId(0L);
				orderItem.setName(title + ",财币");
				orderItem.setPrice(caibi);
				orderItem.setQuantity(1);
				orderItem.setCreateTime(date);
				orderItem.setUpdateTime(date);
				orderItemDao.insertSelective(orderItem);
			}
			if(null != tubi && tubi.intValue() > 0){
				OrderItem orderItem = new OrderItem();
				orderItem.setOrderNo(no);
				orderItem.setItemId(0L);
				orderItem.setSkuId(0L);
				orderItem.setName(title + ",途币");
				orderItem.setPrice(tubi);
				orderItem.setQuantity(1);
				orderItem.setCreateTime(date);
				orderItem.setUpdateTime(date);
				orderItemDao.insertSelective(orderItem);
			}
			if(null != rmb && rmb.intValue() > 0){
				OrderItem orderItem = new OrderItem();
				orderItem.setOrderNo(no);
				orderItem.setItemId(0L);
				orderItem.setSkuId(0L);
				orderItem.setName(title + ",人民币");
				orderItem.setPrice(rmb);
				orderItem.setQuantity(1);
				orderItem.setCreateTime(date);
				orderItem.setUpdateTime(date);
				orderItemDao.insertSelective(orderItem);
			}
		
			//初始化订单数据
			Order order = new Order();
			order.setOrderNo(no);
			order.setUserId(userId);
			order.setName(title);
			order.setDisplay(2);
			order.setPayType(5);
			order.setPayStatus(1);
			order.setType(53);//新手任务订单
			//初始化订单状态
			order.setStatus(40);
			order.setCaibi(caibi);
			order.setRmb(rmb);
			order.setTubi(tubi);
			order.setPrice(0L);
			order.setPayPrice(0L);
			order.setMemo(title);
			
			order.setOrderTime(date);
			order.setTimeoutTime(DateUtil.addMinute(date, 10));
			order.setCreateTime(date);
			order.setUpdateTime(date);
			
			//生成订单
			orderDao.insertSelective(order);
			
			return no;
		} catch (Exception e) {
			logger.error("新手任务订单生成失败:",e);
			throw new OrderException(ApiResultCode.NEWBIE_ACTIVITY_ORDER_ERROR,"新手任务订单生成失败",e.getMessage());
		}
	}
	
	@Override
	public void finishNewbieActivityOrder(String no, Long userId,boolean isSuccess) throws OrderException {
		try {
			if(null == no || null == userId){
				logger.error("参数为空：userId or no;userid:{},no:{}",userId,no);
				throw new OrderException(ApiResultCode.NEWBIE_ACTIVITY_ORDER_NULL, "参数为空：userId or no");
			}
			
			Order order = orderDao.selectByPrimaryKey(no);
			if(null == order || !userId.equals(order.getUserId())){
				logger.error("找不到该订单;userid:{},order.getUserId():{}",userId,order.getUserId());
				throw new OrderException(ApiResultCode.NEWBIE_ACTIVITY_ORDER_NULL, "找不到该订单");
			}
			
			Date date = new Date();
			Order editorder = new Order();
			editorder.setOrderNo(no);
			if(isSuccess){
				editorder.setStatus(41);//已完成
			}else{
				editorder.setStatus(42);//失败
			}
			editorder.setUpdateTime(date);
			orderDao.updateByPrimaryKeySelective(editorder);
		} catch (Exception e) {
			logger.error("完成订单失败:",e);
			throw new OrderException(ApiResultCode.NEWBIE_ACTIVITY_FINISH_ERROR,"新手任务订单完成失败",e.getMessage());
		}
	}
	
	

	@Override
	public String phoneRechargeOrder(Long userId, Long amountId,String mobile)
			throws OrderException {
		String no = null;
		try {
		PhoneAmount pa = phoneAmountDao.selectByPrimaryKey(amountId);
		Long total = CalculateUtils.multiply(pa.getAmount(), 100);
		Long tubi = CalculateUtils.percentByLong(total, pa.getDiscount());
		Account account = accountService.selectByUserId(userId);
		Long userTubi = account.getTubi();
		if(null == userTubi){
			userTubi = 0L; 
		}
		Long userCaibi = account.getTotalIntegral();
		if(null == userCaibi){
			userCaibi = 0L;
		}
		if(userTubi < tubi){
			tubi = userTubi;
		}
		
		Long caibi = CalculateUtils.getDifference(total, tubi);
		
		if(userCaibi < caibi){
			caibi = userCaibi;
		}
		
		Long rmb = total - tubi - caibi;
		
		
		//有人民币
		Integer payType = 2;
		if(null == rmb || 0 == rmb){
			//无人民币
			payType = 1;
		}
		
		
	    no = Order.getOrderNoBySing(userId, "P");
		Date date = new Date();
		
		String title = pa.getName() + "话费";
		
		OrderItem orderItem = new OrderItem();
		orderItem.setOrderNo(no);
		orderItem.setItemId(-1L);
		orderItem.setSkuId(-1L);
		orderItem.setName(title);
		orderItem.setPrice(total);
		orderItem.setQuantity(1);
		orderItem.setCreateTime(date);
		orderItem.setUpdateTime(date);
		
		
		//初始化订单数据
		Order order = new Order();
		order.setOrderNo(no);
		order.setUserId(userId);
		order.setName(title);
		order.setPayStatus(Order.PAY_STATUS_INIT);
		order.setPayType(payType);
		order.setType(60);
		order.setDisplay(0);
		order.setPrice(total);
		//成本价
		order.setPayPrice(total);
		order.setOrderTime(date);
		order.setTimeoutTime(DateUtil.addMinute(date, 10));
		order.setCreateTime(date);
		order.setUpdateTime(date);
		
		order.setCaibi(caibi);
		order.setTubi(tubi);
		order.setRmb(rmb);
		
		order.setMemo(mobile);
		//初始化订单状态
		order.setStatus(Order.STATUS_INIT);
		//生成订单与订单明细
		orderDao.insertSelective(order);
		orderItemDao.insertSelective(orderItem);
		
		
		String picUrl = "/GOODS/12345/1234567/1.png";
		
		//生成交易快照
		TradingSnapshot ts = new TradingSnapshot();
		ts.setOrderItemId(orderItem.getId());
		ts.setTitle(title);
		ts.setSubTitle(title);
		ts.setImgUrl(picUrl);
		ts.setMarketPrice(total);
		ts.setSalePrice(total);
		ts.setVersion("0");
		ts.setWapUrl("");
		ts.setContent("");
		ts.setCreateTime(date);
		tradingSnapshotDao.insertSelective(ts);
		
		} catch (Exception e) {
			logger.debug("话费充值生成订单失败！" + e.getMessage());
			return null;
		}
		
		return no;
	}

	@Override
	public String phoneRechargeOrderByJf(Long userId, Long amountId,String mobile)
			throws OrderException {
		String no = null;
		try {
		PhoneAmount pa = phoneAmountDao.selectByPrimaryKey(amountId);
		Long total = CalculateUtils.multiply(pa.getAmount(), 100);
		
	    no = Order.getOrderNoBySing(userId, "P");
	    
		String title = pa.getName() + "话费";
		Date date = new Date();
		
		OrderItem orderItem = new OrderItem();
		orderItem.setOrderNo(no);
		orderItem.setItemId(-1L);
		orderItem.setSkuId(-1L);
		orderItem.setName(title);
		orderItem.setPrice(total);
		orderItem.setQuantity(1);
		orderItem.setCreateTime(date);
		orderItem.setUpdateTime(date);
		
	
		//初始化订单数据
		Order order = new Order();
		order.setOrderNo(no);
		order.setUserId(userId);
		order.setName(title);
		order.setPayStatus(Order.PAY_STATUS_FINISH);
		order.setPayType(3);//积分变现
		order.setType(60);
		order.setDisplay(1);
		order.setPrice(total);
		//成本价
		order.setPayPrice(total);
		order.setOrderTime(date);
		order.setTimeoutTime(date);
		order.setCreateTime(date);
		order.setUpdateTime(date);
		order.setPayTime(date);
		order.setMemo(mobile);
		
		//初始化订单状态
		order.setStatus(Order.STATUS_FINISH);
		//生成订单与订单明细
		orderDao.insertSelective(order);
		orderItemDao.insertSelective(orderItem);
		
		
		String picUrl = "/GOODS/12345/1234567/1.png";
		
		//生成交易快照
		TradingSnapshot ts = new TradingSnapshot();
		ts.setOrderItemId(orderItem.getId());
		ts.setTitle(title);
		ts.setSubTitle(title);
		ts.setImgUrl(picUrl);
		ts.setMarketPrice(total);
		ts.setSalePrice(total);
		ts.setVersion("0");
		ts.setWapUrl("");
		ts.setContent("");
		ts.setCreateTime(date);
		tradingSnapshotDao.insertSelective(ts);
		
		} catch (Exception e) {
			logger.debug("话费充值生成订单失败！" + e.getMessage());
			return null;
		}
		
		return no;
	}
	
	@Override
	@Transactional
	public String addAuthenticationOrder(Long userId)throws OrderException {
		try {
			logger.info("生成实名认证订单,userid:{}",userId);
			if(null == userId){
				throw new OrderException(-1,"实名认证订单生成失败,参数userId不能为空");
			}
			
			//生成 实名认证订单号 AN
			String no = Order.getOrderNoBySing(userId,"AU");
			
			String title = "实名认证";
			
			Date date = new Date();
		
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderNo(no);
			orderItem.setItemId(0L);
			orderItem.setSkuId(0L);
			orderItem.setName(title + ",1分钱");
			orderItem.setPrice(1L);
			orderItem.setQuantity(1);
			orderItem.setCreateTime(date);
			orderItem.setUpdateTime(date);
			orderItemDao.insertSelective(orderItem);
		
			//初始化订单数据
			Order order = new Order();
			order.setOrderNo(no);
			order.setUserId(userId);
			order.setName(title);
			order.setDisplay(2);
			order.setPayType(2);
			order.setPayStatus(0);
			order.setType(54);//实名认证订单
			//初始化订单状态
			order.setStatus(70);
			order.setCaibi(0L);
			order.setRmb(1L);
			order.setTubi(0L);
			order.setPrice(0L);
			order.setPayPrice(0L);
			order.setMemo(title);
			
			order.setOrderTime(date);
			order.setTimeoutTime(DateUtil.addMinute(date, 10));
			order.setCreateTime(date);
			order.setUpdateTime(date);
			
			//生成订单
			orderDao.insertSelective(order);
			
			return no;
		} catch (Exception e) {
			logger.error("实名认证订单生成失败:",e);
			throw new OrderException(-1,"实名认证订单生成失败",e.getMessage());
		}
	}
	
	@Override
	public void updateAuthenticationOrder(String no,Integer status,String oidPaybill) throws OrderException {
		try {
			if(StringUtils.isBlank(no) || null == status){
				logger.error("参数为空：status or no;no:{}",status,no);
				throw new OrderException(-1, "参数为空：status or no");
			}
			
			Order order = orderDao.selectByPrimaryKey(no);
			if(null == order){
				logger.error("找不到该订单;no:{}",no);
				throw new OrderException(-1, "找不到该订单");
			}else if(!order.getType().equals(54)){
				logger.error("非实名认证订单;no:{}",no);
				throw new OrderException(-1, "非实名认证订单");
			}
			
			Date date = new Date();
			order.setStatus(status);
			order.setUpdateTime(date);
			if(StringUtils.isNotBlank(oidPaybill)){
				order.setOutNo(oidPaybill);
			}
			orderDao.updateByPrimaryKeySelective(order);
		} catch (Exception e) {
			logger.error("完成订单失败:",e);
			throw new OrderException(-1,"实名认证订单完成失败",e.getMessage());
		}
	}

	@Override
	public Order selectOrderByOutNo(String outNo) {
		return orderDao.selectOrderByOutNo(outNo);
	}
}
