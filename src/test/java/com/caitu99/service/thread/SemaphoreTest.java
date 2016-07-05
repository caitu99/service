/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.thread;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.kafka.common.utils.CopyOnWriteMap;

/** 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: Test 
 * @author xiongbin
 * @date 2016年1月22日 下午3:40:47 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class SemaphoreTest {
	//线程池 
//	static final ExecutorService exec = Executors.newCachedThreadPool();
	static final ExecutorService exec = Executors.newFixedThreadPool(10);  
   
	static final Semaphore semp = new Semaphore(2,true);  
   
	static CopyOnWriteArrayList<Long> list = new CopyOnWriteArrayList<Long>();
	
	static CopyOnWriteMap<Long, CopyOnWriteArrayList<Condition>> map = new CopyOnWriteMap<Long, CopyOnWriteArrayList<Condition>>();
   
	static final ReentrantLock lock = new ReentrantLock();
	
	static Integer count = 0;
   
	public static void main(String[] args) throws Exception{  
		exe(336L);
    	exe(1L);
    	exe(336L);
    	exe(336L);
    	exe(3L);
    	exe(336L);
    	exe(336L);
    	exe(3L);
    	exe(336L);
    	exe(31L);
    	exe(336L);
    	exe(31L);
    	exe(336L);
    	exe(3L);
    	exe(31L);
    	exe(31L);//16   
        // 退出线程池 
        exec.shutdown();  
        
        //336	7
        //3		2
        //31	3
	}  
    
    public static void exe(Long x){
    	Runnable run = new Runnable() {  
    		public void run() {
    			Condition condition = lock.newCondition();
    			if(!list.contains(x)){
    				list.add(x);
    				System.out.println(Thread.currentThread() + ",直接执行:" + x);
    				init(x);
    			}else{
    				lock.lock();
        			CopyOnWriteArrayList<Condition> conditionList = map.get(x);
        			if(null == conditionList){
        				conditionList = new CopyOnWriteArrayList<Condition>();
        			}
        			System.out.println(Thread.currentThread() + "," + x + ",添加：" + conditionList.add(condition));
    				System.out.println(Thread.currentThread() + "," + x + ",等待:" + condition.toString());
    				System.out.println(Thread.currentThread() + "," + x + ",总:" + conditionList.toString());
    				map.put(x, conditionList);
    				
    				System.out.println(Thread.currentThread() + ",等待信号:" + x);
    				try {
						condition.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    				lock.unlock();
    				System.out.println(Thread.currentThread() + ",拿到信号,执行:" + x);
    				init(x);
    			}
    			
    			
    			
//    			if(null == conditionList){
//    				conditionList = new CopyOnWriteArrayList<Condition>();
//    				conditionList.add(condition);
//    				map.put(x, conditionList);
//    				System.out.println(Thread.currentThread() + ",集合为空,直接执行:" + x);
//    				init(x);
//    				conditionList.remove(condition);
//    			}else if(conditionList.size() <= 0){
//    				conditionList.add(condition);
//    				map.put(x, conditionList);
//    				System.out.println(Thread.currentThread() + ",集合数据已移除,直接执行:" + x);
//    				init(x);
//    				conditionList.remove(condition);
//    			}else{
//    				lock.lock();
//    				conditionList.add(condition);
//    				map.put(x, conditionList);
//    				System.out.println(Thread.currentThread() + ",等待信号:" + x);
//    				try {
//						condition.await();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//    				init(x);
//    				conditionList.remove(condition);
//    				lock.unlock();
//    			}
    			
    			
    			
    			
//    			if(list.contains(x)){
//	    			lock.lock();
//					try {
//						System.out.println(Thread.currentThread() + ",等待信号:" + x);
//
//				    	Condition condition = lock.newCondition();
//						CopyOnWriteArrayList<Condition> conditionList = map.get(x);
//						if(null == conditionList){
//							conditionList = new CopyOnWriteArrayList<Condition>();
//						}
//						conditionList.add(condition);
//						map.put(x, conditionList);
//						condition.await();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					System.out.println(Thread.currentThread() + ",开始执行:" + x);
//	    			init(x);
//					lock.unlock();
//    			}else{
//    				System.out.println(Thread.currentThread() + ",直接执行:" + x);
//    				lock.lock();
//    				if(!list.contains(x)){
//    					list.add(x);
//    				}
//    				lock.unlock();
//    				init(x);
//    			}
            }  
        }; 
    	exec.execute(run);
//        new Thread(run).start();
    	
//    	Runnable run2 = new Runnable() {  
//    		public void run() {
//    			lock.lock();
//    			if(!list.contains(x)){
//    				condition.signal();
//					System.out.println(Thread.currentThread() + ",拿到信号:" + x);
//    				list.add(x);
//    			}
//				lock.unlock();
//            }  
//        }; 
//        
//        new Thread(run2).start();
    }
    
    private static void init(Long x){
    	try {  
    		// 获取许可 
            semp.acquire();  
            System.out.println(Thread.currentThread() + ",Accessing: " + x);  
            Thread.sleep(3000); 
            System.out.println(Thread.currentThread() + ",success: " + x);  
            list.remove(x);
			count++;
    		System.out.println("总执行数:" + count);
            semp.release();  
    	} catch (InterruptedException e) {  
    		semp.release();  
    	}

		lock.lock();
    	CopyOnWriteArrayList<Condition> conditionList = map.get(x);
    	
    	if(null!=conditionList && conditionList.size()>0){
    		System.out.println(x + "总数:" + conditionList.size());
        	System.out.println(x + "全部:" + conditionList.toString());
        	System.out.println(x + "释放:" + conditionList.get(0));
			Condition condition = conditionList.get(0);
			conditionList.remove(condition);
			condition.signal();
    	}
		lock.unlock();
    }
}