/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ThreadTest2 
 * @author ws
 * @date 2016年2月23日 下午6:33:54 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ThreadTest2 {

	public static void main(String[] args) throws InterruptedException {

		List<Integer> reList = new ArrayList<Integer>();
		CyclicBarrier barrier = new CyclicBarrier(2,new Runnable() { 
			@Override
			public void run() {
				for(int i=0;i<5;i++){
					int num = i;
					Thread t = new Thread() {
						@Override
						public void run() {
							int a;
							//线程在后台执行你想执行的东西
							a = doSomething(num);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							reList.add(a);
							System.out.println("列表大小："+reList.size());
						}
					};
					t.start();
				}
				
				
				for (Integer re : reList) {
					System.out.println("over:"+re);
				}
			}
		});
	}
	

	private static int doSomething(int num){
		System.out.println("输入数值"+num);
		return num+10;
	}
}
