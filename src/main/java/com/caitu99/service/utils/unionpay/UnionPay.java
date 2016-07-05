/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.utils.unionpay;

import java.util.HashMap;
import java.util.Map;

import com.caitu99.service.SDKConfig;
import com.caitu99.service.utils.SpringContext;
import com.caitu99.service.utils.sdk.LogUtil;
import com.caitu99.service.utils.sdk.SDKUtil;


/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UnionPay 
 * @author ws
 * @date 2015年12月29日 下午6:08:14 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class UnionPay {
	
//	应答报文:
//		accessType=0
//		bizType=000201
//		certId=68759585097
//		encoding=UTF-8
//		merId=777290058110097
//		orderId=20151229172038
//		reqReserved=透传字段
//		respCode=00
//		respMsg=成功[0000000]
//		signMethod=01
//		signature=kpkfMS7L+1ifJ/02h/aIkMKQWDp3bNsIiUUImVsqAxg3biLBJzN85nJHSwY2KDMwY7eDaWXsrh0csHg1G+11WR4MWdkLdeGLGSeBo8kzxogXIrLyxLwLVcrXMmZM5Jb759+ondMXvgNvQYn5Wg7e++JCuGSWvCLd5KrUjsVBtuHBFLff9HjQ0nDRbIStA9/Ff0Xouz15XH9yr6tD+67J7n6E4hPI+Jin+Qv0cdWaFQlYARCHn4HaOXnMxKsOFUM7j8TfdV7kbtxtBQjoGJ47Sgw41eI3/bh087Dsojs85kPwqsK+a3KAUP3QLdy+peK34QAejRzXRQxAGIywI4P3pw==
//		tn=201512291720384396328
//		txnSubType=01
//		txnTime=20151229172038
//		txnType=01
//		version=5.0.0
	
	/**
	 * 银联支付请求
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: doPay 
	 * @param merId
	 * @param txnAmt
	 * @param orderId
	 * @param txnTime
	 * @return
	 * @date 2015年12月29日 下午6:17:10  
	 * @author ws
	 */
	public Map<String, String> doPay(String txnAmt,String orderId,String txnTime,String backUrl){
		//String tn = "";//银联返回的交易流水号
		Map<String, String> contentData = new HashMap<String, String>();

		SDKConfig sDKConfig = SpringContext.getBean("sDKConfig");
		
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		contentData.put("version", SDKUtil.version);            //版本号 全渠道默认值
		contentData.put("encoding", SDKUtil.encoding_UTF8);     //字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put("signMethod", "01");           		 	//签名方法 目前只支持01：RSA方式证书加密
		contentData.put("txnType", "01");              		 	//交易类型 01:消费
		contentData.put("txnSubType", "01");           		 	//交易子类 01：消费
		contentData.put("bizType", "000201");          		 	//填写000201
		contentData.put("channelType", "07");          		 	//渠道类型

		/***商户接入参数***/
		contentData.put("merId", sDKConfig.memId);   		 				//商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
		contentData.put("accessType", "0");            		 	//接入类型，商户接入填0 ，不需修改（0：直连商户， 1： 收单机构 2：平台商户）
		contentData.put("orderId", orderId);        	 	    //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则	
		contentData.put("txnTime", txnTime);		 		    //订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		contentData.put("accType", "01");					 	//账号类型 01：银行卡02：存折03：IC卡帐号类型(卡介质)
		
		/*
		//////////如果在控件回显卡号【需开通 接收商户共享信息】，商户号开通了商户对敏感信息加密的权限那么，需要对 卡号accNo加密使用：
		contentData.put("encryptCertId",CertUtil.getEncryptCertId());      //上送敏感信息加密域的加密证书序列号
		String accNo = SDKUtil.encryptPan("6216261000000000018", "UTF-8"); //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		contentData.put("accNo", accNo);
		//////////
		*/
		
		/////////如果在控件回显卡号【需开通 接收商户共享信息】，商户未开通敏感信息加密的权限那么不对敏感信息加密使用：
		//contentData.put("accNo", "6216261000000000018");                  //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		////////

		//代收交易的上送的卡验证要素为：姓名或者证件类型+证件号码
		Map<String,String> customerInfoMap = new HashMap<String,String>();
		customerInfoMap.put("certifTp", "01");						    //证件类型
		customerInfoMap.put("certifId", "341126197709218366");		    //证件号码
		//customerInfoMap.put("customerNm", "全渠道");					//姓名
		String customerInfoStr = SDKUtil.getCustomerInfoWithEncrypt(customerInfoMap,"6216261000000000018",SDKUtil.encoding_UTF8);				
		
		contentData.put("customerInfo", customerInfoStr);
		contentData.put("txnAmt", txnAmt);						 	//交易金额 单位为分，不能带小数点
		contentData.put("currencyCode", "156");                     //境内商户固定 156 人民币
		contentData.put("reqReserved", "透传字段");                    //商户自定义保留域，交易应答时会原样返回
		
		//后台通知地址（需设置为外网能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，【支付失败的交易银联不会发送后台通知】
		//后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
		//注意:1.需设置为外网能访问，否则收不到通知    2.http https均可  3.收单后台通知后需要10秒内返回http200或302状态码 
		//    4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200或302，那么银联会间隔一段时间再次发送。总共发送5次，银联后续间隔1、2、4、5 分钟后会再次通知。
		//    5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
		contentData.put("backUrl", backUrl);
		
		/**对请求参数进行签名并发送http post请求，接收同步应答报文**/
		Map<String, String> submitFromData = SDKUtil.signData(contentData,SDKUtil.encoding_UTF8);			 //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		
		String requestAppUrl = sDKConfig.appTransUrl;			 //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
		//如果这里通讯读超时（30秒），需发起交易状态查询交易
		Map<String, String> resmap = SDKUtil.submitUrl(submitFromData,requestAppUrl,SDKUtil.encoding_UTF8);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		
		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
		/*String respCode = resmap.get("respCode");
		if(("00").equals(respCode)){
			//成功,获取tn号
			tn = resmap.get("tn");
		}else{
			//其他应答码为失败请排查原因
		}*/
		
		return resmap;
	}

	
	public Map<String, String> queryOrder(String orderId,String txnTime){

		SDKConfig sDKConfig = SpringContext.getBean("sDKConfig");
		Map<String, String> data = new HashMap<String, String>();
		
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		data.put("version", SDKUtil.version);                 //版本号
		data.put("encoding", SDKUtil.encoding_UTF8);               //字符集编码 可以使用UTF-8,GBK两种方式
		data.put("signMethod", "01");                          //签名方法 目前只支持01-RSA方式证书加密
		data.put("txnType", "00");                             //交易类型 00-默认
		data.put("txnSubType", "00");                          //交易子类型  默认00
		data.put("bizType", "000401");                         //业务类型 代付
		
		/***商户接入参数***/
		data.put("merId", sDKConfig.memId);                  			   //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
		data.put("accessType", "0");                           //接入类型，商户接入固定填0，不需修改
		
		/***要调通交易以下字段必须修改***/
		//商户订单号  和  交易查询流水号  二选一
		data.put("orderId", orderId);                 //****商户订单号，每次发交易测试需修改为被查询的交易的订单号
		data.put("txnTime", txnTime);                 //****订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间

		/**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
		
		Map<String, String> submitFromData = SDKUtil.signData(data,SDKUtil.encoding_UTF8);			//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		
		String url = sDKConfig.singleQueryUrl;	//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.singleQueryUrl
		Map<String, String> resmap = SDKUtil.submitUrl(submitFromData, url,SDKUtil.encoding_UTF8); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		
		return resmap;
		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
		/*
		if(("00").equals(resmap.get("respCode"))){//如果查询交易成功
			String origRespCode = resmap.get("origRespCode");
			//处理被查询交易的应答码逻辑
			if(("00").equals(origRespCode) ||("A6").equals(origRespCode)){
				//A6代付交易返回，参与清算，商户应该算成功交易，根据成功的逻辑处理
				//交易成功，更新商户订单状态
				//TODO 
			}else if(("03").equals(origRespCode)||
					 ("04").equals(origRespCode)||
					 ("05").equals(origRespCode)||
					 ("01").equals(origRespCode)||
					 ("12").equals(origRespCode)||
					 ("34").equals(origRespCode)||
					 ("60").equals(origRespCode)){
				//订单处理中或交易状态未明，需稍后发起交易状态查询交易 【如果最终尚未确定交易是否成功请以对账文件为准】
				//TODO
			}else{
				//其他应答码为交易失败
				//TODO
			}
		}else if(("34").equals(resmap.get("respCode"))){
			//订单不存在，可认为交易状态未明，需要稍后发起交易状态查询，或依据对账结果为准
			
		}else{//查询交易本身失败，如应答码10/11检查查询报文是否正确
			//TODO
		}
		*/
	}
	
	/**
	 * 
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: validate 
	 * @param resultString
	 * @return
	 * @date 2016年1月11日 上午10:32:15  
	 * @author ws
	 */
	public boolean validate(String resultString){
		/**
		 * 验证签名
		 */
		Map<String, String> resData = new HashMap<String, String>();
		if (null != resultString && !"".equals(resultString)) {
			// 将返回结果转换为map
			resData = SDKUtil.convertResultStringToMap(resultString);
			if (SDKUtil.validate(resData, SDKUtil.encoding_UTF8)) {
				LogUtil.writeLog("验证签名成功,可以继续后边的逻辑处理");
				return true;
			} else {
				LogUtil.writeLog("验证签名失败,必须验签签名通过才能继续后边的逻辑...");
				return false;
			}
		}
		return false;
	}
	
	
}
