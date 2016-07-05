/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.utils.calculate;

import java.math.BigDecimal;
import java.text.NumberFormat;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: CalculateUtils 
 * @author fangjunxiao
 * @date 2015年11月23日 下午3:44:08 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class CalculateUtils {
	
	/**
	 * 	总价=单价*数量 
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: multiply 
	 * @param a
	 * @param b
	 * @return
	 * @date 2015年11月23日 下午3:50:33  
	 * @author fangjunxiao
	 */
	public static Long multiply(Long a,Integer b){
		BigDecimal bd = new BigDecimal(a);
		BigDecimal bd2 = new BigDecimal(b);
		return bd.multiply(bd2).longValue();
	}
	
	/**
	 * 	乘法
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: multiply 
	 * @param a
	 * @param b
	 * @return
	 * @date 2016年6月1日 下午3:45:16  
	 * @author fangjunxiao
	 */
	public static Long multiply(Long a,double b){
		BigDecimal bd = new BigDecimal(a);
		BigDecimal bd2 = new BigDecimal(b);
		return bd.multiply(bd2).longValue();	
	}
	
	/**
	 * 乘法
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: multiply 
	 * @param a
	 * @param b
	 * @return
	 * @date 2016年6月1日 下午3:46:00  
	 * @author fangjunxiao
	 */
	public static Long multiply(Long a,Long b){
		BigDecimal bd = new BigDecimal(a);
		BigDecimal bd2 = new BigDecimal(b);
		return bd.multiply(bd2).longValue();	
	}
	
	
	public static Long getAdd(Long aVal, Long bVal){
        BigDecimal bd = new BigDecimal(aVal);
        BigDecimal bd2 = new BigDecimal(bVal);
        BigDecimal add = bd.add(bd2);
        return add.longValue();
    }
	
	/**
	 * 	减
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getDifference 
	 * @param oldVal
	 * @param newVal
	 * @return
	 * @date 2016年1月25日 下午5:51:01  
	 * @author fangjunxiao
	 */
    public static Long getDifference(Long oldVal, Long newVal){
        BigDecimal bd = new BigDecimal(oldVal);
        BigDecimal bd2 = new BigDecimal(newVal);
        BigDecimal difference = bd.subtract(bd2);
        return difference.longValue();
    }
	
	
	/**
	 * @Description: (除法)  
	 * @Title: divide 
	 * @param a
	 * @param b
	 * @return
	 * @date 2015年12月10日 下午5:14:22  
	 * @author Hongbo Peng
	 */
	public static Long divide(Long a,Integer b){
		BigDecimal bd = new BigDecimal(a);
		BigDecimal bd2 = new BigDecimal(b);
		return bd.divide(bd2).longValue();
	}
	
	/**
	 * 	折扣
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: percentByLong 
	 * @param a
	 * @param b
	 * @return
	 * @date 2016年6月14日 上午10:55:22  
	 * @author fangjunxiao
	 */
	public static Long percentByLong(Long a,Long b){
		BigDecimal bd = new BigDecimal(a);
		BigDecimal bd2 = new BigDecimal(b);
		return bd2.divide(new BigDecimal(100)).multiply(bd).longValue();
	}
	
	
	
	public static String percent(Integer a,Integer b){
		BigDecimal bd = new BigDecimal(a);
		BigDecimal bd2 = new BigDecimal(b);
		double percent = bd.divide(bd2,5,BigDecimal.ROUND_HALF_UP).doubleValue();
	    NumberFormat nt = NumberFormat.getPercentInstance();
	    //设置百分数精确度2即保留两位小数
	    nt.setMinimumFractionDigits(2);
	    //最后格式化并输出
	    //  System.out.println("百分数：" + nt.format(percent));
	    return nt.format(percent);
	}
	
	public static String divide(Integer a,Integer b){
		BigDecimal bd = new BigDecimal(a);
		BigDecimal bd2 = new BigDecimal(b);
		double cc = bd.divide(bd2,5,BigDecimal.ROUND_HALF_UP).doubleValue();
		return String.valueOf(cc);
		
	}
	
	
	public static Long add(Long a,Long b){
		BigDecimal bd = new BigDecimal(a);
		BigDecimal bd2 = new BigDecimal(b);
		return bd.add(bd2).longValue();
	}
	
	public static Long add(Integer a,Integer b){
		BigDecimal bd = new BigDecimal(a);
		BigDecimal bd2 = new BigDecimal(b);
		return bd.add(bd2).longValue();
	}
	
	
	
	public static boolean compareTo(Long a,Long b){
		BigDecimal bd = new BigDecimal(a);
		BigDecimal bd2 = new BigDecimal(b);
		 return bd.compareTo(bd2)!=-1?true:false;
	}
	
	
    /**
     * 单位换算【元转分】
     * 
     * @param Yuan
     * @return
     */
    public static Long converYuantoPenny(String yuan){
        BigDecimal bd = new BigDecimal(yuan);
        BigDecimal bd2 = new BigDecimal(100);
        BigDecimal cash = bd.multiply(bd2);
        return cash.longValue();
    }

    /**
     * 单位换算【分转元】
     * 
     * @param panny
     * @return
     */
    public static String converPennytoYuan(Long panny){
        BigDecimal bd = new BigDecimal(panny);
        return bd.divide(new BigDecimal(100)).toString();
    }
    
    /**
     * @Description: (计算折扣金额 向上抹)  
     * @Title: getDiscountPriceCeil 
     * @param price
     * @param discount
     * @return
     * @date 2016年5月26日 下午8:15:53  
     * @author Hongbo Peng
     */
    public static Long getDiscountPriceCeil(Long price, Integer discount){
        BigDecimal bd = new BigDecimal(price);
        BigDecimal bd2 = new BigDecimal(discount);
        BigDecimal bd3 = new BigDecimal(100);
        Double dou = bd.multiply(bd2).divide(bd3).doubleValue();
        return (long) Math.ceil(dou);
    }
    
    
    /**
     * @Description: (计算折扣金额 向下抹)  
     * @Title: getDiscountPriceDown 
     * @param price
     * @param discount
     * @return
     * @date 2016年5月26日 下午8:16:16  
     * @author Hongbo Peng
     */
	public static Long getDiscountPriceDown(Long price, Integer discount) {
        BigDecimal bd = new BigDecimal(price);
        BigDecimal bd2 = new BigDecimal(discount);
        BigDecimal bd3 = new BigDecimal(100);
        Double dou = bd.multiply(bd2).divide(bd3).doubleValue();
        return (long) Math.floor(dou);
	}
	
	
	public static void main(String[] args) {
		System.out.println(compareTo(1000L,999L));
	}
}
