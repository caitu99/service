package com.caitu99.service.transaction.sxf;

public interface Sxf {

	String daifu(String accName, String accNo, Long amount,String orderNo,String tno) throws Exception;
	
	String query(String orderNo) throws Exception;
}
