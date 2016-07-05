/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.lottery.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caitu99.service.lottery.vo.LotteryOrderVo;
import com.caitu99.service.utils.encryption.md5.MD5Util;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: checkSignMD5 
 * @author fangjunxiao
 * @date 2016年5月11日 下午12:04:29 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class CheckSignMD5 {
	
	
	public static boolean checkNotice(LotteryOrderVo lotteryOrderVo,String appSecret){
		
		Map<String, String> signMap = new HashMap<String, String>();
		signMap.put("appKey", lotteryOrderVo.getAppKey());
		signMap.put("bizId", lotteryOrderVo.getBizId());
		signMap.put("resultStatus", lotteryOrderVo.getResultStatus());
		signMap.put("noticeMessage", lotteryOrderVo.getNoticeMessage());
		signMap.put("lotteryOrderId", lotteryOrderVo.getLotteryOrderId());
		signMap.put("timestamp", lotteryOrderVo.getTimestamp());
		signMap.put("appSecret", appSecret);
		//signMap.put("sign", lotteryOrderVo.getSign());
		
		 System.out.println(lotteryOrderVo.getSign());
		
		
	     Collection<String> keyset= signMap.keySet();  
	     List<String> list = new ArrayList<String>(keyset);  
	       
	     //对key键值按字典升序排序  
	     Collections.sort(list);  
	       
	     StringBuilder sb = new StringBuilder();
	     
	     for (int i = 0; i < list.size(); i++) {  
	         sb.append(signMap.get(list.get(i)));
	     }  
		
		String signString=MD5Util.getInstance().encryptionMD5Around(sb.toString(), "");
	
		System.out.println("=================================");
		System.out.println(signString);
		
		if(lotteryOrderVo.getSign().equals(signString)){
			return true;
		}
		return false;
		
	}
	
	
	public static boolean checkOrder(LotteryOrderVo lotteryOrderVo,String appSecret){
		
		Map<String, String> signMap = new HashMap<String, String>();
		signMap.put("fuserid", lotteryOrderVo.getFuserid());
		signMap.put("appKey", lotteryOrderVo.getAppKey());
		signMap.put("points", lotteryOrderVo.getPoints());
		signMap.put("timestamp", lotteryOrderVo.getTimestamp());
		signMap.put("description", lotteryOrderVo.getDescription());
		signMap.put("lotteryOrderId", lotteryOrderVo.getLotteryOrderId());
		signMap.put("product", lotteryOrderVo.getProduct());
		signMap.put("marketPrice", lotteryOrderVo.getMarketPrice());
		signMap.put("realPrice", lotteryOrderVo.getRealPrice());
		signMap.put("ifReview", lotteryOrderVo.getIfReview());
		signMap.put("params", lotteryOrderVo.getParams().toString());
		signMap.put("appSecret", appSecret);
		//signMap.put("sign", lotteryOrderVo.getSign());
		System.out.println(lotteryOrderVo.getSign());
		
	     Collection<String> keyset= signMap.keySet();  
	     List<String> list = new ArrayList<String>(keyset);  
	       
	     //对key键值按字典升序排序  
	     Collections.sort(list);  
	       
	     StringBuilder sb = new StringBuilder();
	     
	     for (int i = 0; i < list.size(); i++) {  
	         sb.append(signMap.get(list.get(i)));
	     }  
		
		String signString=MD5Util.getInstance().encryptionMD5Around(sb.toString(), "");
	
		System.out.println("=================================");
		System.out.println(signString);
		
		if(lotteryOrderVo.getSign().equals(signString)){
			return true;
		}
		return false;
	}
	
}
