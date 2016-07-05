/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.controller.api;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.activities.service.ActivitiesGoodService;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.free.domain.FreeTrade;
import com.caitu99.service.free.service.FreeTradeService;
import com.caitu99.service.goods.domain.Item;
import com.caitu99.service.goods.service.ItemService;
import com.caitu99.service.transaction.domain.Order;
import com.caitu99.service.transaction.domain.OrderItem;
import com.caitu99.service.transaction.dto.OrderDto;
import com.caitu99.service.transaction.service.OrderItemService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;

/**
 * 
 * @Description: (类职责详细描述,可空)
 * @ClassName: OrderController
 * @author fangjunxiao
 * @date 2015年11月26日 上午10:16:39
 * @Copyright (c) 2015-2020 by caitu99
 */
@Controller
@RequestMapping("/api/transaction/order/")
public class OrderController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderItemService orderItemService;
	@Autowired
	private UserService userService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private FreeTradeService freeTradeService;
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private ActivitiesGoodService activitiesGoodService;

	/**
	 * 订单列表
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: list
	 * @param order
	 * @param pagination
	 * @return
	 * @date 2015年11月26日 下午6:47:31
	 * @author fangjunxiao
	 */
	@RequestMapping(value = "list/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String list(Order order, Pagination<OrderDto> pagination) {
		ApiResult<Pagination<OrderDto>> result = new ApiResult<Pagination<OrderDto>>();
		pagination = orderService.findPageOrder(order, pagination);
		return result.toJSONString(0, "success", pagination);
	}

	/**
	 * 删除订单
	 *  
	 * @Description: (方法职责详细描述,可空)
	 * @Title: delOrder
	 * @param userid
	 * @param orderNo
	 * @return
	 * @date 2015年11月26日 下午6:47:48
	 * @author fangjunxiao
	 */
	@RequestMapping(value = "del/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String delOrder(Long userId, String orderNo, String jsonpCallback) {
		ApiResult<Boolean> error = new ApiResult<Boolean>();
		if (null == userId || null == orderNo) {
			return error.toJSONString(-1, "参数不能为空");
		}
		if (!orderService.checkUserAndOrder(userId, orderNo)) {
			return error.toJSONString(ApiResultCode.ORDER_USERID_FAILE,
					"用户验证失败", false);
		}
		String result = orderService.delOrder(userId,orderNo);

		if (StringUtils.isBlank(jsonpCallback)) {
			return result;
		} else {
			return jsonpCallback + "(" + result + ")";
		}
	}

	/**
	 * 取消订单
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: cancelOrder
	 * @param userid
	 * @param orderNo
	 * @return
	 * @date 2015年11月26日 下午6:48:00
	 * @author fangjunxiao
	 */
	@RequestMapping(value = "cancel/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String cancelOrder(Long userid, String orderNo) {
		return orderService.cancelPay(orderNo);
	}

	/**
	 * 立即支付
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: generateOrder
	 * @param userid
	 * @param sku
	 * @param quantity
	 * @return
	 * @date 2015年11月27日 上午9:53:59
	 * @author fangjunxiao
	 */
	@RequestMapping(value = "generate/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String generateOrder(Long userId, Long skuId, Integer quantity,
			String jsonpCallback) {

		ApiResult<Boolean> error = new ApiResult<Boolean>();

		if (null == userId || null == skuId || null == quantity) {
			return error.toJSONString(-1, "参数不能为空");
		}
		
		Item item = itemService.findItemByskuId(skuId);
		String result;
		if(Item.SOURCE_MY.equals(item.getSource()) || Item.SOURCE_OTHER.equals(item.getSource())){	
			//查询是否是活动商品   是否在活动时间
			Integer code = activitiesGoodService.checkIsActivitiesGood(item.getItemId());
			if(code == 1){
				ApiResult<String> inforesult = new ApiResult<String>();
				return inforesult.toJSONString(ApiResultCode.SC_ORDER_ERROR, "活动已结束");
			}
			
			result = orderService.addOrder(userId, skuId, quantity);
		}else{
			result = orderService.addOrderByThird(userId, skuId, quantity);
		}
		logger.info("购买商品下单,用户ID：{},商品skuId:{},购买数量:{}",userId,skuId,quantity);

		if (StringUtils.isBlank(jsonpCallback)) {
			return result;
		} else {
			return jsonpCallback + "(" + result + ")";
		}

	}
	
	/**
	 * 	第三方商城订单处理
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: processOrderByThird 
	 * @param userid
	 * @param flag
	 * @param no
	 * @param outno
	 * @param jsonpCallback
	 * @return
	 * @date 2016年1月13日 下午4:28:42  
	 * @author fangjunxiao
	 */
	@RequestMapping(value = "processorder/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String processOrderByThird(Long userid, Integer flag, String no, String outno,
			String jsonpCallback) {
		ApiResult<Boolean> error = new ApiResult<Boolean>();
		if (null == userid || null == no || null == outno) {
			return error.toJSONString(-1, "参数不能为空");
		}
		String result = orderService.processOrderByThird(flag, userid, no, outno);
		if (StringUtils.isBlank(jsonpCallback)) {
			return result;
		} else {
			return jsonpCallback + "(" + result + ")";
		}
	}
	
	

	@RequestMapping(value = "closeorder/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String closeOrder(Long userId, String orderNo) {

		ApiResult<Boolean> result = new ApiResult<Boolean>();

		if (null == userId || orderNo == null) {
			return result.toJSONString(-1, "参数不能为空");
		}

		try {
			boolean res = orderService.timeOut(userId, orderNo);
			result.setData(res);
			return "success";
		} catch (Exception e) {
			result.setData(false);
			return "fail";
		}

	}

	@RequestMapping(value = "detail/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String orderDetail(String orderno) {
		// 初始化
		ApiResult<Order> apiResult = new ApiResult<Order>();
		apiResult.setCode(0);
		// 业务实现
		// 状态：0.生成/待付款，1.已付款，10，完成交易 20.超时关闭，30.删除
		Order order = orderService.queryOrder(orderno);
		if (null == order) {
			apiResult.setCode(2910);
			apiResult.setMessage("订单不存在");
			return JSON.toJSONString(apiResult);
		}
		if (1 == order.getStatus()) {
			apiResult.setCode(2921);
			apiResult.setMessage("已付款订单");
			return JSON.toJSONString(apiResult);
		} else if (10 == order.getStatus()) {
			apiResult.setCode(2922);
			apiResult.setMessage("已完成交易的订单");
			return JSON.toJSONString(apiResult);
		} else if (20 == order.getStatus()) {
			apiResult.setCode(2923);
			apiResult.setMessage("已关闭的订单");
			return JSON.toJSONString(apiResult);
		} else if (30 == order.getStatus()) {
			apiResult.setCode(2924);
			apiResult.setMessage("已删除的订单");
			return JSON.toJSONString(apiResult);
		}
		List<OrderItem> list = orderItemService.listByOrderNo(order
				.getOrderNo());
		order.setGoodsCount(list == null ? 0 : list.size());
		// 数据返回
		apiResult.setData(order);
		return JSON.toJSONString(apiResult);
	}
	
	@RequestMapping(value = "special/generate/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String generateSpecialOrder(Long userId, Long skuId, Integer quantity,String paypass,
			Integer setpass,String jsonpCallback) throws CryptoException {
		ApiResult<Boolean> error = new ApiResult<Boolean>();

		if (null == userId || null == skuId || null == quantity) {
			return error.toJSONString(-1, "userId/skuId/quantity参数不能为空");
		}
		// 支付密码验证
		if (StringUtils.isEmpty(paypass)) {
			error.setCode(2455);
			error.setMessage("支付密码不能为空");
			return JSON.toJSONString(error);
		}
		// 用户验证
		User loginUser = userService.selectByPrimaryKey(userId);
		if (1 == setpass) {// 需要设置支付密码
			loginUser.setPaypass(AESCryptoUtil.encrypt(paypass));
			userService.updateByPrimaryKeySelective(loginUser);
		} else {
			if (!AESCryptoUtil.encrypt(paypass).equals(loginUser.getPaypass())) {
				error.setCode(2032);
				error.setMessage("支付密码错误");
				return JSON.toJSONString(error);
			}
		}

		Integer ishn = 1;//默认普通用户
		if(1 == loginUser.getIshn().intValue()){//黄牛用户
			ishn = 3;
		}
		String result = orderService.addFreeTradeOrder(userId, skuId, quantity);

		if (StringUtils.isBlank(jsonpCallback)) {
			return result;
		} else {
			return jsonpCallback + "(" + result + ")";
		}
	}
	
	
	
//	@RequestMapping(value = "special/exchange/1.0", produces = "application/json;charset=utf-8")
//	@ResponseBody
//	public String exchangeTrade(Long userId, Integer quantity,Long freetradeid,
//			String jsonpCallback) {
//		ApiResult<Boolean> error = new ApiResult<Boolean>();
//
//		if (null == userId ||  null == quantity || null == freetradeid) {
//			return error.toJSONString(-1, "参数不能为空");
//		}
//		
//		String result = orderService.exchangeTradeOrder(userId, quantity, freetradeid);
//
//		if (StringUtils.isBlank(jsonpCallback)) {
//			return result;
//		} else {
//			return jsonpCallback + "(" + result + ")";
//		}
//	}
	
	@RequestMapping(value = "special/exchange/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String exchangeTrade(Long userId, Integer quantity,Long freetradeid,String jsonpCallback) {
		ApiResult<String> result = new ApiResult<String>();

		if (null == userId ||  null == quantity || null == freetradeid) {
			return result.toJSONString(-1, "参数不能为空");
		}
		
		String orderNo = orderService.exchangeTradeOrder(userId, quantity, freetradeid);
		
		if(orderNo.equals(ApiResultCode.FREE_TRADE_NULL_ERROR.toString())){
			return result.toJSONString(ApiResultCode.FREE_TRADE_NULL_ERROR, "找不到该条记录"); 
		}else if(orderNo.equals(ApiResultCode.ORDER_INVENTORY_NONE.toString())){
			return result.toJSONString(ApiResultCode.ORDER_INVENTORY_NONE, "库存不足");
		}

		return result.toJSONString(0, "success",orderNo);
	}
	
	@RequestMapping(value = "special/exchange/2.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String exchangeTrade2(Long userId, Integer quantity,Long freetradeid,String jsonpCallback) {
		ApiResult<String> result = new ApiResult<String>();

		if (null == userId ||  null == quantity || null == freetradeid) {
			return result.toJSONString(-1, "参数不能为空");
		}
		
		String orderNo = orderService.exchangeTradeOrder(userId, quantity, freetradeid);
		
		if(orderNo.equals(ApiResultCode.FREE_TRADE_NULL_ERROR.toString())){
			return result.toJSONString(ApiResultCode.FREE_TRADE_NULL_ERROR, "找不到该条记录"); 
		}else if(orderNo.equals(ApiResultCode.ORDER_INVENTORY_NONE.toString())){
			return result.toJSONString(ApiResultCode.ORDER_INVENTORY_NONE, "库存不足");
		}
		
		FreeTrade ft = freeTradeService.selectByPrimaryKey(freetradeid);
		
		StringBuilder redirct = new StringBuilder();
		redirct.append("transitional/transtional.html?redirect=");
		//中兴银行
		if(6==ft.getRemoteId().intValue()){
			redirct.append("/citic/citic_login.html?order_no=");
		}else if(8==ft.getRemoteId().intValue()){//建设特卖
			redirct.append("/ccb/login.html?order_no=");
		}else if(14==ft.getRemoteId().intValue()){
			redirct.append("/esurfing/login.html?order_no=");
		}
		redirct.append(orderNo);

		return result.toJSONString(0, "success",redirct.toString());
	}
	
	/**
	 * 查询订单详情
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: detail 
	 * @param orderNo	订单号
	 * @return
	 * @date 2016年6月14日 下午6:31:23  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/pay/detail/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String detail(String orderNo) {
		ApiResult<Order> result = new ApiResult<Order>();

		if (StringUtils.isBlank(orderNo)) {
			return result.toJSONString(-1, "参数orderNo不能为空");
		}
		
		Order order = orderService.queryOrder(orderNo);
		if(null == order){
			return result.toJSONString(-1, "找不到订单"); 
		}

		String[] filter = {"name","payStatus","price","orderTime","tubi","rmb","memo"};
		
		return result.toJSONString(0, "success",order,Order.class,filter);
	}
	
	@RequestMapping(value = "phone/recharge/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String phoneOrder(Long userid,Long amountId,String mobile) {
		ApiResult<String> result = new ApiResult<String>();
		String no = orderService.phoneRechargeOrder(userid , amountId, mobile);
		if(StringUtils.isBlank(no)){
			return result.toJSONString(-1, "fail");
		}
		return result.toJSONString(0, "success", no);
	}
	
	/**
	 * 生成实名认证订单
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: authentication 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年6月16日 下午3:47:05  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/authentication/generate/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String authentication(Long userid) {
		ApiResult<String> reslut = new ApiResult<String>();
		if (null == userid) {
			return reslut.toJSONString(-1, "参数userid不能为空");
		}
		
		User user = userService.selectByPrimaryKey(userid);
		if(null == user){
			throw new UserNotFoundException(-1, "用户不存在");
		}
			
		String orderNo = orderService.addAuthenticationOrder(userid);

		return reslut.toJSONString(0, "", orderNo);
	}
}
