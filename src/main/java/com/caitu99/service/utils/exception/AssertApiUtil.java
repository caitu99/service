package com.caitu99.service.utils.exception;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.StringUtils;

import com.caitu99.service.utils.exception.enums.CommonEnum;
import com.caitu99.service.utils.exception.enums.ExceptionObject;


/**
 * Api断言异常工具类
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AssertApiUtil 
 * @author xiongbin
 * @date 2015年11月16日 上午10:15:33 
 * @Copyright (c) 2015-2020 by caitu99
 */
public class AssertApiUtil extends AssertUtil {

	public static void hasLength(String text, String errorMsg, Integer code) throws RuntimeException {
		if (StringUtils.isBlank(text)) {
//			throw createException(errorMsg, code);
			throw createException(BaseException.class, CommonEnum.COMMON_02,errorMsg, code);
		}
	}
	
	public static void notTrue(boolean notTrue, String errorMsg, Integer code) throws RuntimeException {
		if (notTrue) {
			throw createException(BaseException.class, CommonEnum.COMMON_02,errorMsg, code);
		}
	}
	
	private static RuntimeException createException(Class<? extends BaseException> clazz, ExceptionObject exObj,String errorMsg, Integer code) {
		try {
			exObj.setLogInfo(errorMsg);
			exObj.setCode(code);
			return clazz.getConstructor(ExceptionObject.class).newInstance(exObj);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
