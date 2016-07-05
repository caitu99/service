/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.life.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.constants.SysConstants;
import com.caitu99.service.life.controller.vo.CheckResult;
import com.caitu99.service.life.controller.vo.RechargeResult;
import com.caitu99.service.life.dao.PhoneRechargeRecordMapper;
import com.caitu99.service.life.domain.PhoneRechargeRecord;
import com.caitu99.service.life.service.PhoneRechargeService;
import com.caitu99.service.transaction.constants.AccountDetailConstants;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.string.StrUtil;

@Service
public class PhoneRechargeServiceImpl implements PhoneRechargeService{

    private static final Logger logger = LoggerFactory.getLogger(PhoneRechargeServiceImpl.class);
    
    //默认编码集
    
    @Autowired
    private PhoneRechargeRecordMapper phoneRechargeRecordMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AppConfig appConfig;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private AccountDetailService accountDetailService;
    
    @Autowired
    private TransactionRecordService transactionRecordService;

    interface ResultKey {
        String ERROR_CODE = "error_code";
        String RESULT = "result";
        String REASON = "reason";
    }
    
    @Override
    public RechargeResult checkAndRecharge(String userId, String mobile,String cardNum) {
        RechargeResult rechargeResult = new RechargeResult();
        //checked
        CheckResult checkResult = this.check(mobile, cardNum);
        if (checkResult.isSuccess()) {
        	//charge
        	rechargeResult = this.recharge(userId, mobile, cardNum);
            return rechargeResult;
		}
        rechargeResult.setSuccess(false);
        rechargeResult.setResult(checkResult.getResult());
        return rechargeResult;
    }

    /**
     * 调用第三方接口充值话费
     * @Description: (方法职责详细描述,可空)  
     * @Title: check 
     * @param mobile 手机号
     * @param cardNum 充值金额
     * @return
     * @date 2015年11月2日 下午5:37:15  
     * @author lawrence
     */
    private RechargeResult recharge(String userId, String mobile, String cardNum) {
    	 RechargeResult rechargeResult = new RechargeResult();
        try {
         	//参数组装
        	String orderId = this.getRechargeOrderId(userId);
        	String sign = this.sign(mobile, cardNum, orderId);
        	String url = this.joinChargeUrl(mobile, cardNum, orderId, sign);
        	logger.debug("[第三方手机直充接口] orderId：{}, signKey：{}, url:{}", orderId, sign, url);
        	// 获得请求JSON字符串
			String httpJsonResult = httpURLGet(url, SysConstants.DEFAULT_CHATSET);
			JSONObject jsonObject = JSON.parseObject(httpJsonResult);// 转化为JSON类
            String resultCode = jsonObject.getString(ResultKey.ERROR_CODE);// 得到错误码
            logger.info("调用第三方检测手机号码是否能充值接口成功,手机号：{},充值额度：{}，第三方返回数据:{}", mobile, cardNum, httpJsonResult);
            
            boolean isSuccess = "0".equals(resultCode)?true:false;
     		PhoneRechargeRecord phoneRechargeRecord = new PhoneRechargeRecord();
    		phoneRechargeRecord.setCardno(Integer.valueOf(cardNum));
    		phoneRechargeRecord.setFlag(rechargeResult.isSuccess()?1:0);
    		phoneRechargeRecord.setOrderid(rechargeResult.getOrderid());
    		phoneRechargeRecord.setPhoneno(mobile);
    		phoneRechargeRecord.setRechargeDate(new Date());
    		phoneRechargeRecord.setResult(rechargeResult.getResult());
    		phoneRechargeRecord.setUserId(Long.valueOf(userId));
            phoneRechargeRecordMapper.insert(phoneRechargeRecord);
            if(isSuccess){
            	//手机充值比例
            	Integer scale = appConfig.phoneRechargeScale;
//            	 BigDecimal needCardNum  = new BigDecimal(cardNum).multiply(new BigDecimal(100));
           	 	BigDecimal needCardNum  = new BigDecimal(cardNum).multiply(new BigDecimal(100))
           	 			.multiply(new BigDecimal(scale).divide(new BigDecimal(100)));
//           	 	userService.updateUserIntegral(Long.parseLong(userId), needCardNum.multiply(new BigDecimal(-1)).intValue());
           	 	
           	 	Random random = new Random();
           	 	String orderNo = "phonerecharge"+ random.nextInt(10)+System.currentTimeMillis() +random.nextInt(10)+ userId;
           	 	//新增用户明细
           	 	TransactionRecord transactionRecordDto = new TransactionRecord();
           	 	transactionRecordDto.setUserId(Long.parseLong(userId));
           	 	transactionRecordDto.setType(1);
           	 	transactionRecordDto.setOrderNo(orderNo);
        	 	transactionRecordDto.setTransactionNumber(orderNo);
           	 	transactionRecordDto.setInfo("fen生活");
           	 	transactionRecordDto.setSource(10);
           	 	transactionRecordDto.setPayType(1);
           	 	transactionRecordDto.setStatus(2);
           	 	transactionRecordDto.setTotal(needCardNum.longValue());
           	 	Date now = new Date();
           	 	transactionRecordDto.setSuccessTime(now);
        	 	transactionRecordDto.setThirdPartyNumber(rechargeResult.getOrderid());
        	 	transactionRecordDto.setCreateTime(now);
        	 	transactionRecordDto.setUpdateTime(now);
        	 	transactionRecordService.insert(transactionRecordDto);
        	 	//新增用户明细
        	 	AccountDetail userAccountDetail = new AccountDetail();
        		userAccountDetail.setRecordId(transactionRecordDto.getId());
        		userAccountDetail.setGmtCreate(now);
        		userAccountDetail.setGmtModify(now);
        		userAccountDetail.setIntegralChange(needCardNum.longValue());
        		userAccountDetail.setMemo("话费充值");
        		userAccountDetail.setType(AccountDetailConstants.TYPE_OUT);
        		userAccountDetail.setUserId(Long.parseLong(userId));
        		accountDetailService.insertAccountDetail(userAccountDetail );
        		//更新用户总财币
        		Account account = accountService.selectByUserId(Long.parseLong(userId));
        		BigDecimal totalIntegral = new BigDecimal(account.getTotalIntegral())
        		.subtract(needCardNum);
        		BigDecimal availableIntegral = new BigDecimal(account.getAvailableIntegral())
				.subtract(needCardNum);
        		account.setTotalIntegral(totalIntegral.longValue());
        		account.setAvailableIntegral(availableIntegral.longValue());
        		account.setGmtModify(now);
        		accountService.updateAccountByUserId(account);
            }
            rechargeResult.setResult(httpJsonResult);//使用返回json结果
            rechargeResult.setOrderid(orderId);//无论成功或失败，都记录下orderId
            rechargeResult.setOrderid(orderId);
            rechargeResult.setSuccess(isSuccess);
        } catch (Exception e) {
            rechargeResult.setSuccess(false);
            rechargeResult.setResult("调用第三方手机直充接口失败");
            logger.error("调用第三方手机直充接口发生异常,手机号：{},充值额度：{}", mobile, cardNum);
            
        }
        return rechargeResult;
    }

    /**
     * 调用第三方接口检测手机号码是否能充值
     * @Description: (方法职责详细描述,可空)  
     * @Title: check 
     * @param mobile
     * @param cardNum
     * @return
     * @date 2015年11月2日 下午5:37:15  
     * @author lawrence
     */
    private CheckResult check(String mobile, String cardNum) {
        CheckResult checkResult = new CheckResult();
        try {
            String url = this.joinCheckUrl(mobile, cardNum);
            logger.debug("[第三方手机直充检测接口] mobile：{}, cardNum：{}, url:{}", mobile, cardNum, url);
            
            //请求
            String jsonResult = httpURLGet(url, SysConstants.DEFAULT_CHATSET);// 得到JSON字符串
            JSONObject object = JSON.parseObject(jsonResult);// 转化为JSON类
            String resultCode = object.getString(ResultKey.ERROR_CODE);// 得到错误码
            String result = object.getString("reason");
            checkResult.setResult(result);
            logger.info("调用第三方检测手机号码是否能充值接口成功,手机号：{},充值额度：{}，第三方返回数据:{}", mobile, cardNum, jsonResult);
            
            // 错误码判断
            boolean isSuccess = "0".equals(resultCode)?true:false;
            checkResult.setSuccess(isSuccess);
        } catch (Exception e) {
            checkResult.setSuccess(false);
            checkResult.setResult("调用第三方检测手机号码是否能充值接口失败");
            logger.error("调用第三方检测手机号码是否能充值接口失败,手机号：{},充值额度：{}", mobile, cardNum);
        }
        return checkResult;

    }

     /**
      * 通过HttpURLConnection发起请求	
      * @Description: (方法职责详细描述,可空)  
      * @Title: httpURLGet 
      * @param _url
      * @param charset
      * @return
      * @throws IOException
      * @date 2015年11月2日 下午4:34:57  
      * @author lawrence
      */
     private String httpURLGet(String _url, String charset) throws IOException  {
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
    	   StringBuffer buffer = new StringBuffer();
           String userAgent = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.3; WOW64; Trident/7.0; LCJB)";// 模拟浏览器
           URL url = new URL(_url);
           connection = (HttpURLConnection) url.openConnection();
           connection.setRequestMethod("GET");
           connection.setReadTimeout(5000);
           connection.setConnectTimeout(5000);
           connection.setRequestProperty("User-agent", userAgent);
           connection.connect();
           inputStream = connection.getInputStream();
           reader = new BufferedReader(new InputStreamReader(inputStream, charset));
           String strRead = null;
           while ((strRead = reader.readLine()) != null) {
        	   buffer.append(strRead);
        	   buffer.append("\r\n");
           }
           return buffer.toString();
           
		}  finally{
			if (null != reader) {
				reader.close();
			}
			if (null != connection) {
				connection.disconnect();
			}
			if (null != inputStream) {
				inputStream.close();
			}
		}
    }
    
    /**
     * 	获取订单号
     * @Description: (方法职责详细描述,可空)  
     * @Title: getRechargeOrderId 
     * @param userId
     * @return
     * @date 2015年11月2日 下午3:53:31  
     * @author lawrence
     */
    private String getRechargeOrderId(String userId){
    	return new StringBuilder()
		.append(new Date().getTime())
		.append("_")
		.append(userId)
		.toString();
    }
    /**
     * 获取加密参数
     * @Description: (方法职责详细描述,可空)  
     * @Title: sign 
     * @param mobile
     * @param cardNum
     * @param orderId
     * @return
     * @date 2015年11月2日 下午3:53:56  
     * @author lawrence
     */
    private String sign(String mobile, String cardNum, String orderId){
    	return StrUtil.toMD5(new StringBuilder()
    	.append(SpringContext.getBean(AppConfig.class).rechargePhoneOpenId)
    	.append(SpringContext.getBean(AppConfig.class).rechargePhoneKey)
    	.append(mobile)
    	.append(cardNum)
    	.append(orderId)
    	.toString());
    }
    
//    public static void main(String[] args) {
//    	
//    	System.out.println(StrUtil.toMD5(new StringBuilder()
//    	.append("JHed686fae7c560bad22ad3d43154ecdf0")
//    	.append("ed41d996322e961fc43763ff8b444d18")
//    	.append("15700114735")
//    	.append(5)
//    	.append("test101111111")
//    	.toString()));
//	}
    /**
     * 获取请求Url	
     * @Description: (
     * <pre>
     * Example:
     *  http://op.juhe.cn/ofpay/mobile/onlineorder?key=*********************
     *  &phoneno=131****2365&cardnum=100&orderid=123456&sign=*****
     * </pre>
     * )  
     * @Title: joinChargeUrl 
     * @param mobile
     * @param cardNum
     * @param orderId
     * @param sign
     * @return
     * @date 2015年11月2日 下午3:54:11  
     * @author lawrence
     */
    private String joinChargeUrl(String mobile, String cardNum, String orderId,String sign){
    	return new StringBuilder(SpringContext.getBean(AppConfig.class).rechargePhoneOrderUrl)
		.append("?phoneno=").append(mobile)
		.append("&cardnum=").append(cardNum)
		.append("&orderid=").append(orderId)
		.append("&sign=").append(sign)
		.append("&key=").append(SpringContext.getBean(AppConfig.class).rechargePhoneKey)
		.toString();
    } 
    
    /**
     * 手机充值号码验证 url拼接
     * @Description: (
     * <pre>
     * Example:
	 * 请求地址：http://op.juhe.cn/ofpay/mobile/telcheck
     * 请求参数：phoneno=*&cardnum=10&key=KEY
     * 请求方式：GET
     * </pre>
     * )  
     * @Title: joinCheckUrl 
     * @param mobile
     * @param cardNum
     * @return
     * @date 2015年11月2日 下午5:29:48  
     * @author lawrence
     */
    private String joinCheckUrl(String mobile, String cardNum){
    	return new StringBuilder(SpringContext.getBean(AppConfig.class).rechargePhoneCheckedUrl)
		.append("?phoneno=").append(mobile)
		.append("&cardnum=").append(cardNum)
		.append("&key=").append(SpringContext.getBean(AppConfig.class).rechargePhoneKey)
		.toString();
    } 
    

}
