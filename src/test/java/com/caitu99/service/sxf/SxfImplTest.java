package com.caitu99.service.sxf;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.transaction.sxf.Sxf;

public class SxfImplTest extends AbstractJunit {

	@Autowired
	private Sxf sxf;
	
	@Test
	public void test(){
		try {
			String orderNo = "ceshi10000003";
			String flag = sxf.daifu("彭红波", "6214835889291983", 1L, orderNo, "sxf1000003");
			System.out.println(flag);
			
//			String query = sxf.query(orderNo);
//			System.out.println(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
