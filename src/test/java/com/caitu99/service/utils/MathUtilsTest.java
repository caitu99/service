/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.utils;

import org.junit.Test;


/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: StringUtilsTest 
 * @author ws
 * @date 2015年12月11日 下午6:21:31 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class MathUtilsTest {

	@Test
	public void testRound(){
		
		Double d = Math.ceil(12 * 0.2);
		
		System.out.println(d.longValue());
		System.out.println(Math.ceil(1999 * 0.02));
		
	}

}
