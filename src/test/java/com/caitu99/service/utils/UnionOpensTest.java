package com.caitu99.service.utils;

import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AbstractJunit;
import com.caitu99.service.utils.unionpay.UnionOpens;

public class UnionOpensTest extends AbstractJunit {

	
	@Test
	public void test(){
		UnionOpens unionOpens = new UnionOpens();
		try {
//			Map map = unionOpens.paymentNoBind(1000L, "6214835889291983", "10000000008", 
//					"彭红波", "", "421122198909180015");
//			System.out.println(JSON.toJSONString(map));
			Map map = unionOpens.getCardInfo("6214835889291983");
			System.out.println(JSON.toJSONString(map));
//			Map map = unionOpens.getOrder("10000000001");
//			System.out.println(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
