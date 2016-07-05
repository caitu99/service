package com.caitu99.service.utils.unionpay;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.http.HttpResult;
import com.caitu99.service.utils.http.WappedHttpClient;
@SuppressWarnings("rawtypes")
public class UnionOpens {
	
	private final static Logger logger = LoggerFactory.getLogger(UnionOpens.class);

	// 接口地址
    private final String bankUrl = Configuration.getProperty("union.bank.url", null);
    // 密钥
    private final String secretKey = Configuration.getProperty("union.secret.key", null);
    // 商户id
    private final String merId = Configuration.getProperty("union.merid", null);
    
    /*
     * 获得银行卡信息
     */
    public Map getCardInfo(String cardNo) throws Exception {

        WappedHttpClient httpClient = new WappedHttpClient(false);
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> mapFormParams = new HashMap<String, String>();

        mapFormParams.put("merId", merId);
        mapFormParams.put("cardNo", cardNo);
        mapFormParams.put("timestamp", System.currentTimeMillis() + "");
        mapFormParams.put(
                "sign",
                SignHelper.MD5LowerCase(SignHelper.sortParamsToSign(mapFormParams) + secretKey));

        headers.put("Content-Type", "form");

        HttpResult result = httpClient.postMethodWithForm(bankUrl
                + "bind/cardInfo.do", headers, mapFormParams, "utf-8");
        return (Map) JSON.parse(result.getResponseContent());
    }

    /*
     * 发送验证码
     */
    public Map sendCode(String mobile) throws Exception {

        WappedHttpClient httpClient = new WappedHttpClient(false);
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> mapFormParams = new HashMap<String, String>();

        mapFormParams.put("merId", merId);
        mapFormParams.put("mobile", mobile);
        mapFormParams.put("timestamp", System.currentTimeMillis() + "");
        mapFormParams.put("tranDateTime",
                XStringUtil.dateToString(new Date(), "yyyyMMddHHmmss"));

        mapFormParams.put(
                "sign",
                SignHelper.MD5LowerCase(SignHelper
                        .sortParamsToSign(mapFormParams) + secretKey));

        headers.put("Content-Type", "form");

        HttpResult result = httpClient.postMethodWithForm(bankUrl
                + "bind/sendCode.do", headers, mapFormParams, "utf-8");
        int status = result.getStatusCode();
        if(200 != status){
        	logger.error("connection to union failed! status code is {}",status);
        	throw new Exception( "网络繁忙，请重试");
        }
        return (Map) JSON.parse(result.getResponseContent());
    }

    /*
     * 绑定银行卡
     */
    public Map bindBankCard(String orderNo, String cardNo, String accName,
                            String accId, String mobile, String code) throws Exception {

        WappedHttpClient httpClient = new WappedHttpClient(false);
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> mapFormParams = new HashMap<String, String>();

        mapFormParams.put("merId", merId);
        mapFormParams.put("orderNo", orderNo);
        mapFormParams.put("cardNo", cardNo);
        mapFormParams.put("accName", accName);
        mapFormParams.put("accId", accId);
        mapFormParams.put("mobile", mobile);
        mapFormParams.put("code", code);
        mapFormParams.put("timestamp", System.currentTimeMillis() + "");
        mapFormParams.put("tranDateTime",
                XStringUtil.dateToString(new Date(), "yyyyMMddHHmmss"));
        mapFormParams.put(
                "sign",
                SignHelper.MD5LowerCase(SignHelper
                        .sortParamsToSign(mapFormParams) + secretKey));

        headers.put("Content-Type", "form");

        HttpResult result = httpClient.postMethodWithForm(bankUrl
                + "bind/cardBindByMC.do", headers, mapFormParams, "utf-8");
        int status = result.getStatusCode();
        if(200 != status){
        	logger.error("connection to union failed! status code is {}",status);
        	throw new Exception( "网络繁忙，请重试");
        }
        return (Map) JSON.parse(result.getResponseContent());
    }

    /*
     * 获得银行卡信息
     */
    public Map getBankInfo(String cardNo) throws Exception {

        WappedHttpClient httpClient = new WappedHttpClient(false);
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> mapFormParams = new HashMap<String, String>();

        mapFormParams.put("merId", merId);
        mapFormParams.put("cardNo", cardNo);
        mapFormParams.put("timestamp", System.currentTimeMillis() + "");
        mapFormParams.put(
                "sign",
                SignHelper.MD5LowerCase(SignHelper
                        .sortParamsToSign(mapFormParams) + secretKey));

        headers.put("Content-Type", "form");

        HttpResult result = httpClient.postMethodWithForm(bankUrl
                + "bind/cardInfo.do", headers, mapFormParams, "utf-8");
        int status = result.getStatusCode();
        if(200 != status){
        	logger.error("connection to union failed! status code is {}",status);
        	throw new Exception( "网络繁忙，请重试");
        }
        return (Map) JSON.parse(result.getResponseContent());
    }

    /*
     * 付款
     */
    public Map integrallifting(Long amount, String bindId, String orderNo)
            throws Exception {
        // 手续费
        amount = amount - 200;
        // 发请求
        WappedHttpClient httpClient = new WappedHttpClient(false);
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> mapFormParams = new HashMap<String, String>();
        mapFormParams.put("tranDateTime",
                XStringUtil.dateToString(new Date(), "yyyyMMddHHmmss"));
        mapFormParams.put("orderNo", orderNo);
        mapFormParams.put("bindId", bindId);
        mapFormParams.put("payType", "" + 2);
        mapFormParams.put("merId", merId);
        mapFormParams.put("amount", "" + amount);
        mapFormParams.put("timestamp", System.currentTimeMillis() + "");
        mapFormParams.put(
                "sign",
                SignHelper.MD5LowerCase(SignHelper
                        .sortParamsToSign(mapFormParams) + secretKey));
        headers.put("Content-Type", "form");
        HttpResult result = httpClient.postMethodWithForm(bankUrl
                + "bind/payment.do", headers, mapFormParams, "utf-8");
        int status = result.getStatusCode();
        if(200 != status){
        	logger.error("connection to union failed! status code is {}",status);
        	throw new Exception( "网络繁忙，请重试");
        }
        return (Map) JSON.parse(result.getResponseContent());
    }
    
    /**
     * @Description: (无绑定付款)  
     * @Title: paymentNoBind 
     * @param amount	交易金额，整数并以分为单位 
     * @param cardNo	银行卡号
     * @param orderNo	商户请求唯一订单编号
     * @param accName	持卡人姓名
     * @param mobile	预留手机号码
     * @param accId		持卡人身份证号码
     * @return
     * @throws Exception
     * @date 2016年3月24日 下午4:48:30  
     * @author Hongbo Peng
     */
    public Map paymentNoBind(Long amount, String cardNo, String orderNo, String accName, 
    		String mobile, String accId)
            throws Exception {
        // 手续费
        amount = amount - 200;
        // 发请求
        WappedHttpClient httpClient = new WappedHttpClient(false);
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> mapFormParams = new HashMap<String, String>();
        mapFormParams.put("merId", merId);
        mapFormParams.put("orderNo", orderNo);
        mapFormParams.put("cardNo", cardNo);
        mapFormParams.put("amount", "" + amount);
        mapFormParams.put("accName", "" + accName);
        mapFormParams.put("mobile", "" + mobile);
        mapFormParams.put("accId", "" + accId);
        mapFormParams.put("tranDateTime",
                XStringUtil.dateToString(new Date(), "yyyyMMddHHmmss"));
        mapFormParams.put("payType", "2");
        mapFormParams.put("timestamp", System.currentTimeMillis() + "");
        mapFormParams.put(
                "sign",
                SignHelper.MD5LowerCase(SignHelper
                        .sortParamsToSign(mapFormParams) + secretKey));
        headers.put("Content-Type", "form");
        HttpResult result = httpClient.postMethodWithForm(bankUrl
                + "bind/paymentNoBind.do", headers, mapFormParams, "utf-8");
        int status = result.getStatusCode();
        if(200 != status){
        	logger.error("connection to union failed! status code is {}",status);
        	throw new Exception( "网络繁忙，请重试");
        }
        return (Map) JSON.parse(result.getResponseContent());
    }

    // 查看订单信息
    public Map getOrder(String orderNo) throws Exception {

        WappedHttpClient httpClient = new WappedHttpClient(false);
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> mapFormParams = new HashMap<String, String>();
        mapFormParams.put("merId", merId);
        mapFormParams.put("orderNo", orderNo);
        mapFormParams.put("timestamp", System.currentTimeMillis() + "");
        mapFormParams.put("tranDateTime", System.currentTimeMillis() + "1");
        mapFormParams.put(
                "sign",
                SignHelper.MD5LowerCase(SignHelper
                        .sortParamsToSign(mapFormParams) + secretKey));
        headers.put("Content-Type", "form");
        HttpResult result = httpClient.postMethodWithForm(bankUrl
                + "bind/queryOrder.do", headers, mapFormParams, "utf-8");
        int status = result.getStatusCode();
        if(200 != status){
        	logger.error("connection to union failed! status code is {}",status);
        	throw new Exception( "网络繁忙，请重试");
        }
        return (Map) JSON.parse(result.getResponseContent());
    }

    // 查询备付金
	public Map selectMoney() throws Exception {

        WappedHttpClient httpClient = new WappedHttpClient(false);
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> mapFormParams = new HashMap<String, String>();
        mapFormParams.put("merId", merId);
        mapFormParams.put("merType", "" + 1);
        mapFormParams.put("timestamp", System.currentTimeMillis() + "");
        mapFormParams.put("tranDateTime", System.currentTimeMillis() + "1");
        mapFormParams.put(
                "sign",
                SignHelper.MD5LowerCase(SignHelper
                        .sortParamsToSign(mapFormParams) + secretKey));
        headers.put("Content-Type", "form");
        HttpResult result = httpClient.postMethodWithForm(bankUrl
                + "bind/queryProv.do", headers, mapFormParams, "utf-8");
        int status = result.getStatusCode();
        if(200 != status){
        	logger.error("connection to union failed! status code is {}",status);
        	throw new Exception( "网络繁忙，请重试");
        }
        return (Map) JSON.parse(result.getResponseContent());
    }

	public Map singlePay(String orderNo,String bindId,Long amount,String tranDateTime)throws Exception{
		WappedHttpClient httpClient = new WappedHttpClient(false);
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> mapFormParams = new HashMap<String, String>();
        mapFormParams.put("merId", merId);
        mapFormParams.put("orderNo", orderNo);
        mapFormParams.put("bindId", bindId);
        mapFormParams.put("amount", String.valueOf(amount));
        mapFormParams.put("timestamp", String.valueOf(System.currentTimeMillis()));
        mapFormParams.put("tranDateTime", tranDateTime);
        mapFormParams.put(
                "sign",
                SignHelper.MD5LowerCase(SignHelper
                        .sortParamsToSign(mapFormParams) + secretKey));
        headers.put("Content-Type", "form");
        HttpResult result = httpClient.postMethodWithForm(bankUrl
                + "bind/singlePay.do", headers, mapFormParams, "utf-8");
        int status = result.getStatusCode();
        if(200 != status){
        	logger.error("connection to union failed! status code is {}",status);
        	throw new Exception( "网络繁忙，请重试");
        }
        return (Map) JSON.parse(result.getResponseContent());
	}
	
    public static void main(String[] args) {
        Map map = (Map) JSON.parse("{'name':'lhj'}");
        System.out.println(map.get("name"));
    }

}
