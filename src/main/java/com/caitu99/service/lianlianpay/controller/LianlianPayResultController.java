/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.lianlianpay.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.lianlianpay.config.PartnerConfig;
import com.caitu99.service.lianlianpay.service.LianlianQueryService;
import com.caitu99.service.lianlianpay.utils.LLPayUtil;
import com.caitu99.service.lianlianpay.vo.PayDataBean;
import com.caitu99.service.lianlianpay.vo.RetBean;
import com.caitu99.service.transaction.api.IntegralRechargeHandler;
import com.caitu99.service.transaction.controller.vo.AccountResult;
import com.caitu99.service.transaction.domain.Order;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.date.DateUtil;

/**
 * 连连支付结果处理
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: LianlianPayResultController 
 * @author ws
 * @date 2016年6月13日 下午2:40:03 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Controller
@RequestMapping("/public/lianlian/")
public class LianlianPayResultController {

	private static final Logger logger = LoggerFactory
			.getLogger(LianlianPayResultController.class);

	@Autowired
	IntegralRechargeHandler integralRechargeHandler;
	
	@Autowired
	OrderService orderService;
	@Autowired
	AccountService accountService;
	@Autowired
	AppConfig appConfig;
	@Autowired
	UserService userService;
	@Autowired
	private LianlianQueryService lianlianQueryService;
	
	/**
	 * 银联处理支付成功后返回
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: backRcvResponse 
	 * @param req
	 * @param resp
	 * @throws IOException
	 * @date 2015年12月31日 上午10:55:21  
	 * @author ws
	 */
	@RequestMapping(value="pay/backresponse/1.0", produces="application/json;charset=utf-8")
	public void backRcvResponse(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
		logger.info("进入支付异步通知数据接收处理");

		resp.setCharacterEncoding("UTF-8");
        RetBean retBean = new RetBean();
        String reqStr = LLPayUtil.readReqStr(req);
        if (LLPayUtil.isnull(reqStr))
        {
            retBean.setRet_code("9999");
            retBean.setRet_msg("交易失败");
            resp.getWriter().write(JSON.toJSONString(retBean));
            resp.getWriter().flush();
            return;
        }
        logger.info("接收支付异步通知数据：【" + reqStr + "】");
        try
        {
            if (!LLPayUtil.checkSign(reqStr, PartnerConfig.YT_PUB_KEY,
                    PartnerConfig.MD5_KEY))
            {
                retBean.setRet_code("9999");
                retBean.setRet_msg("交易失败");
                resp.getWriter().write(JSON.toJSONString(retBean));
                resp.getWriter().flush();
                logger.info("支付异步通知验签失败");
                return;
            }
        } catch (Exception e)
        {
        	logger.info("异步通知报文解析异常：" + e);
            retBean.setRet_code("9999");
            retBean.setRet_msg("交易失败");
            resp.getWriter().write(JSON.toJSONString(retBean));
            resp.getWriter().flush();
            return;
        }
        // 解析异步通知对象
        PayDataBean payDataBean = JSON.parseObject(reqStr, PayDataBean.class);
        // 更新订单
        Order order = orderService.queryOrder(payDataBean.getNo_order());
        
        if(order.getType().equals(54)){
        	String backUrl = "";
    		
    		if(appConfig.isDevMode){
    			backUrl = "http://hongbo1989.eicp.net:13030" + "/public/lianlian/refund/backresponse/1.0";
    		}else{
    			backUrl = appConfig.caituUrl + "/public/lianlian/refund/backresponse/1.0";
    		}
    		
        	String refundReslut = lianlianQueryService.refund(order.getOderNo(order.getUserId()), LLPayUtil.getCurrentDateTimeStr(), 
        													order.getRmb()*0.01, payDataBean.getOid_paybill(), backUrl);
        	
        	JSONObject resObj = JSON.parseObject(refundReslut);
            if(resObj.getString("ret_code").equals("0000")){
	    		TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
	    		transactionRecordDto.setChannel(3);
	    		transactionRecordDto.setComment("实名认证退款");
	    		transactionRecordDto.setInfo("实名");
	    		transactionRecordDto.setOrderNo(order.getOrderNo());
	    		transactionRecordDto.setPicUrl("");
	    		transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNo("SP", String.valueOf(order.getUserId())));
	    		transactionRecordDto.setType(12);
	    		transactionRecordDto.setUserId(order.getUserId());
	    		//冻结支付
	    		AccountResult accountResult = accountService.pay4(transactionRecordDto);
	    		if(!accountResult.isSuccess()){
	    			logger.error("实名认证退款失败");
	    		}
            }else{
            	logger.error("实名认证退款失败,userid:{},orderNo:{},连连支付流水号:{}",
            					order.getUserId(),order.getOrderNo(),payDataBean.getOid_paybill());
            }
            
        }
        	
        accountService.paySuccessDo(order.getUserId(), order.getOrderNo(), payDataBean.getOid_paybill());

        logger.info("支付异步通知数据接收处理成功");
        retBean.setRet_code("0000");
        retBean.setRet_msg("交易成功");
        resp.getWriter().write(JSON.toJSONString(retBean));
        resp.getWriter().flush();
        
	}
	

	/**
	 * 银联退款处理成功后返回
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: backRcvResponse 
	 * @param req
	 * @param resp
	 * @throws IOException
	 * @date 2015年12月31日 上午10:55:21  
	 * @author ws
	 */
	@RequestMapping(value="refund/backresponse/1.0", produces="application/json;charset=utf-8")
	public void refundBackresponse(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException{
		logger.info("进入退款异步通知数据接收处理");

		resp.setCharacterEncoding("UTF-8");
        RetBean retBean = new RetBean();
        String reqStr = LLPayUtil.readReqStr(req);
        if (LLPayUtil.isnull(reqStr))
        {
            retBean.setRet_code("9999");
            retBean.setRet_msg("交易失败");
            resp.getWriter().write(JSON.toJSONString(retBean));
            resp.getWriter().flush();
            return;
        }
        logger.info("接收退款异步通知数据：【" + reqStr + "】");
        try
        {
            if (!LLPayUtil.checkSign(reqStr, PartnerConfig.YT_PUB_KEY,
                    PartnerConfig.MD5_KEY))
            {
                retBean.setRet_code("9999");
                retBean.setRet_msg("交易失败");
                resp.getWriter().write(JSON.toJSONString(retBean));
                resp.getWriter().flush();
                logger.info("退款异步通知验签失败");
                return;
            }
        } catch (Exception e)
        {
        	logger.info("异步通知报文解析异常：" + e);
            retBean.setRet_code("9999");
            retBean.setRet_msg("交易失败");
            resp.getWriter().write(JSON.toJSONString(retBean));
            resp.getWriter().flush();
            return;
        }
        // 解析异步通知对象
        PayDataBean payDataBean = JSON.parseObject(reqStr, PayDataBean.class);
        
        logger.info("退款异步通知数据接收处理成功");
        retBean.setRet_code("0000");
        retBean.setRet_msg("交易成功");
        resp.getWriter().write(JSON.toJSONString(retBean));
        resp.getWriter().flush();
        
	}
	

	/**
	 * 支付超时（超时设置5分钟）
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: lianlianTimeout 
	 * @param userId
	 * @param orderId
	 * @date 2016年6月14日 上午9:41:50  
	 * @author ws
	 * @throws IOException 
	 * @throws ServletException 
	 */
    @RequestMapping(value = "lianlian/result/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public void lianlianResult(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
    	try {
    		req.setCharacterEncoding("utf-8");
            resp.setCharacterEncoding("utf-8");
            String resData = req.getParameter("res_data");
            
            JSONObject resDataObj = JSON.parseObject(resData);
            String orderNo = resDataObj.getString("no_order");
        	String oidPaybill = resDataObj.getString("oid_paybill");
            
    		AccountResult accountResult = new AccountResult();
    		accountResult.setSuccess(true);
    		accountResult.setResult("success");
    		Order order = orderService.queryOrder(orderNo);
    		//实名认证通过处理
            accountService.paySuccessDo(order.getUserId(), orderNo, oidPaybill);
    		String redirct = createRedirect(order, accountResult);
    		
        	resp.sendRedirect(redirct);
		} catch (Exception e) {
			resp.sendRedirect("/citic/backToCaitu99AppHelloWorld.html");//返回首页
			logger.info("连连支付结果处理异常，非正常返回,{}",e);
		}
    }
 

	/**
	 * 支付超时（超时设置5分钟）
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: lianlianTimeout 
	 * @param userId
	 * @param orderId
	 * @date 2016年6月14日 上午9:41:50  
	 * @author ws
	 * @throws IOException 
	 * @throws ServletException 
	 */
    @RequestMapping(value = "lianlian/auth/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public void lianlianAuth(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		try {
			req.setCharacterEncoding("utf-8");
	        resp.setCharacterEncoding("utf-8");
	        String resData = req.getParameter("res_data");
	        
	        JSONObject resDataObj = JSON.parseObject(resData);
	        String orderNo = resDataObj.getString("no_order");
	        String oidPaybill = resDataObj.getString("oid_paybill");//连连支付单号
	    	
	        //TODO 支付成功处理  需添加退款请求
	        
			Order order = orderService.queryOrder(orderNo);
			User user = userService.getById(order.getUserId());
			accountService.authenticationSuccessDo(order.getUserId(), orderNo,oidPaybill);
			
			String url = "/quick_pay/autonym_success.html";
			StringBuilder redirct = new StringBuilder();
			//redirct.append(appConfig.caituUrl);
			redirct.append(appConfig.caituUrl);
			redirct.append(url)
				.append("?phone=").append(user.getMobile())
				.append("&oidPaybill=").append(oidPaybill);
	    	resp.sendRedirect(redirct.toString());
		} catch (Exception e) {
			resp.sendRedirect("/citic/backToCaitu99AppHelloWorld.html");//返回首页
			logger.info("实名认证支付结果处理异常，非正常返回,{}",e);
		}
        
    }
    
    
	private String createRedirect(Order order, AccountResult accountResult) 
			throws UnsupportedEncodingException {
		// 数据返回
		String title = getTitle(order.getType().toString(), accountResult);
		String payInfo = getPayInfo(order.getRmb(),order.getCaibi(),order.getTubi());
		String remark = getRemark(accountResult);
		String url = "/quick_pay/status_success.html";
		StringBuilder redirct = new StringBuilder();
		//redirct.append(appConfig.caituUrl);
		redirct.append(appConfig.caituUrl);
		redirct.append(url)
		.append("?orderId=").append(URLEncoder.encode(order.getOrderNo(),"UTF-8"))
		.append("&goodsName=").append(URLEncoder.encode(order.getName(),"UTF-8"))
		.append("&payTime=").append(DateUtil.DateToString(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"))
		.append("&title=").append(URLEncoder.encode(title,"UTF-8"))
		.append("&remark=").append(URLEncoder.encode(remark,"UTF-8"))
		.append("&payInfo=").append(URLEncoder.encode(payInfo,"UTF-8"));
		return redirct.toString();
	}
	

	/**
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getRemark 
	 * @param accountResult
	 * @return
	 * @date 2016年6月12日 下午3:58:48  
	 * @author ws
	*/
	private String getRemark(AccountResult accountResult) {

		if(accountResult.isSuccess()){
			return "success";
		}else{
			return accountResult.getResult();
		}
	}

	/**
	 * 获取支付结果展示标题
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getTitle 
	 * @param payType 2充值 or 1支付
	 * @param accountResult
	 * @return
	 * @date 2016年6月8日 下午4:17:09  
	 * @author ws
	 */
	private String getTitle(String payType, AccountResult accountResult) {
		String title = "";
		if(payType.equals("1")){//
			if(accountResult.isSuccess()){
				title = "恭喜，支付成功";
			}else{
				title = "抱歉，支付失败";
			}
		}else{
			if(accountResult.isSuccess()){
				title = "恭喜，充值成功";
			}else{
				title = "很遗憾，充值失败";
			}
		}
		return title;
	}
	/**
	 * 获取支付结果展示支付信息
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getPayInfo 
	 * @param rmb
	 * @param caibi
	 * @param tubi
	 * @return
	 * @date 2016年6月8日 下午4:17:23  
	 * @author ws
	 */
	private String getPayInfo(Long rmb, Long caibi, Long tubi) {
		String payInfo = "";
		if(null != rmb && rmb > 0){
			payInfo = payInfo
					+"现金："+rmb;
		}
		if(null != caibi && caibi > 0){
			payInfo = payInfo
					+"，财币："+caibi;
		}
		if(null != tubi && tubi > 0){
			payInfo = payInfo
					+"，途币："+tubi;
		}
		
		if(null == rmb || rmb == 0){
			payInfo = payInfo.substring(1);//去掉最前面的逗号
		}
		
		return payInfo;
	}


	
}

