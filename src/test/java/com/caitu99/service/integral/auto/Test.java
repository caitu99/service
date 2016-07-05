/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.auto;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/** 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: Test 
 * @author xiongbin
 * @date 2015年12月18日 下午3:57:51 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class Test {

	
	public static void main(String[] args) throws InterruptedException {
		ArrayBlockingQueue queue =  new ArrayBlockingQueue(30){};
		String str = null;
		for (int i = 0; i < 10; i++) {
			str = "msg_"+i;
			queue.put(str);
		}
		for (Object object : queue) {
			
		}
	}
	
	private static  void test(){
		
	}
}
