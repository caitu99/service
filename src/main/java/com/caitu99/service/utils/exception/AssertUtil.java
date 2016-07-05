package com.caitu99.service.utils.exception;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.caitu99.service.utils.exception.enums.CommonEnum;
import com.caitu99.service.utils.exception.enums.ExceptionObject;
import com.caitu99.service.utils.exception.errorcode.ErrMsg;

/**
 * 断言异常工具类
 * 
 * @ClassName: AssertUtil
 * @Description: (这里用一句话描述这个类的作用)
 * @author lys
 * @date 2015年3月10日 上午9:46:00
 */
public class AssertUtil extends Assert {

	/**
	 * 状态为假，为真抛出异常
	 * 
	 * @Title: notTrue
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param notTrue
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param viewInfo
	 *            页面信息
	 * @throws RuntimeException
	 * @date 2015年3月10日 上午9:46:11
	 * @author lys
	 */
	public static void notTrue(boolean notTrue, String logInfo, String viewInfo)
			throws RuntimeException {
		if (notTrue) {
			throw createException(logInfo, viewInfo);
		}
	}

	/**
	 * 
	 * @Title: notTrue
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param notTrue
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param viewInfo
	 *            页面信息
	 * @param params
	 *            填充信息
	 * @throws RuntimeException
	 * @date 2015年3月10日 上午10:46:57
	 * @author lys
	 */
	public static void notTrue(boolean notTrue, String logInfo,
			String viewInfo, Object... params) throws RuntimeException {
		if (notTrue) {
			throw createException(logInfo, viewInfo, params);
		}
	}

	/**
	 * 状态为假，为真抛出异常
	 * 
	 * @Title: notTrue
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param notTrue
	 *            判定对象
	 * @param viewInfo
	 *            后台信息
	 * @throws RuntimeException
	 * @date 2015年3月10日 上午9:45:06
	 * @author lys
	 */
	public static void notTrue(boolean notTrue, String logInfo)
			throws RuntimeException {
		if (notTrue) {
			throw createException(logInfo);
		}
	}

	/**
	 * 状态为假，为真抛出异常
	 * 
	 * @Title: notTrue
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param notTrue
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param params
	 *            填充信息
	 * @throws RuntimeException
	 * @date 2015年3月10日 上午10:47:35
	 * @author lys
	 */
	public static void notTrue(boolean notTrue, String logInfo,
			Object... params) throws RuntimeException {
		if (notTrue) {
			throw createException(logInfo, params);
		}
	}
	
	/**
	 * 状态为假，为真抛出异常
	 * 
	 * @Title notTrue
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param notTrue
	 *            判定对象
	 * @param errMsg
	 * 
	 * @param params
	 * 			  填充信息
	 * @throws RuntimeException
	 * @Date 2015.10.20
	 * @author jushuang
	 */
	//TODO
	public static void notTrue(boolean notTrue, ErrMsg errMsg, Object... params)
			throws RuntimeException {
		notTrue(notTrue, errMsg.getErrCd(), errMsg.getErrorMesage(), params);
	}

	/**
	 * 状态为真，为假抛出异常
	 * 
	 * @Title: isTrue
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param isTrue
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param viewInfo
	 *            页面信息
	 * @throws RuntimeException
	 * @date 2015年3月10日 上午9:43:41
	 * @author lys
	 */
	public static void isTrue(boolean isTrue, String logInfo, String viewInfo)
			throws RuntimeException {
		if (!isTrue) {
			throw createException(logInfo, viewInfo);
		}
	}

	/**
	 * 状态为真，为假抛出异常
	 * 
	 * @Title: isTrue
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param isTrue
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @throws RuntimeException
	 * @date 2015年3月10日 上午9:42:18
	 * @author lys
	 */
	public static void isTrue(boolean isTrue, String logInfo)
			throws RuntimeException {
		if (!isTrue) {
			throw createException(logInfo);
		}
	}

	/**
	 * 状态为真，为假抛出异常
	 * 
	 * @Title: isTrue
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param isTrue
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param param
	 * @throws RuntimeException
	 * @date 2015年3月11日 下午4:03:29
	 * @author lys
	 */
	public static void isTrue(boolean isTrue, String logInfo, Object... param)
			throws RuntimeException {
		if (!isTrue) {
			throw createException(logInfo, param);
		}
	}

	/**
	 * 状态为真，为假抛出异常
	 * 
	 * @Title: isTrue
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param isTrue
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param param
	 * @throws RuntimeException
	 * @date 2015年3月11日 下午4:03:29
	 * @author lys
	 */
	public static void isTrue(boolean isTrue, String logInfo, String viewInfo,
			Object... param) throws RuntimeException {
		if (!isTrue) {
			throw createException(logInfo, viewInfo, param);
		}
	}

	/**
	 * 状态为真，为假抛出异常
	 * @Title: isTrue
	 * @Description: (这里用一句话描述这个方法的作用)
	 * 
	 * @param isTrue
	 * 			判定对象
	 * @param errMsg
	 * 			后台信息
	 * @param params
	 * @throws RuntimeException
	 * @date 2015.10.20
	 * @author jushuang
	 */
	//TODO
	public static void isTrue(boolean isTrue, ErrMsg errMsg, Object... params)
			throws RuntimeException {
		isTrue(isTrue, errMsg.getErrCd(), errMsg.getErrorMesage(), params);
	}
	
	/**
	 * 集合必须不为空，为空则抛出指定异常
	 * 
	 * @Title: notEmpty
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param collection
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @throws RuntimeException
	 * @date 2015年3月10日 上午9:41:43
	 * @author lys
	 */
//	public static <E> void notEmpty(Collection<E> collection, String logInfo) throws RuntimeException {
//		if (CollectionUtils.isEmpty(collection)) {
//			throw createException(logInfo);
//		}
//	}

	/**
	 * 对象必须不为空，为空则抛出指定异常
	 * 
	 * @Title: notEmpty
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param collection
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param viewInfo
	 *            页面信息
	 * @throws RuntimeException
	 * @date 2015年3月10日 上午9:40:52
	 * @author lys
	 */
	public static <E> void notEmpty(Collection<E> collection, String logInfo,
			String viewInfo) throws RuntimeException {
		if (CollectionUtils.isEmpty(collection)) {
			throw createException(logInfo, viewInfo);
		}
	}

	/**
	 * 对象必须不为空，为空则抛出指定异常
	 * 
	 * @Title: notEmpty
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param collection
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param params
	 *            填充信息
	 * @throws RuntimeException
	 * @date 2015年3月11日 下午5:46:00
	 * @author lys
	 */
	public static <E> void notEmpty(Collection<E> collection, String logInfo,
			Object... params) throws RuntimeException {
		if (CollectionUtils.isEmpty(collection)) {
			throw createException(logInfo, params);
		}
	}

	/**
	 * 对象必须不为空，为空则抛出指定异常
	 * 
	 * @Title: notEmpty
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param collection
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param viewInfo
	 *            页面信息
	 * @param params
	 *            填充信息
	 * @throws RuntimeException
	 * @date 2015年3月31日 上午11:04:41
	 * @author lys
	 */
	public static <E> void notEmpty(Collection<E> collection, String logInfo,
			String viewInfo, Object... params) throws RuntimeException {
		if (CollectionUtils.isEmpty(collection)) {
			throw createException(logInfo, params);
		}
	}
	
	/**
	 * 对象必须不为空，为空则抛出指定异常
	 * 
	 * @Title: notEmpty
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param collection
	 * 				判定对象
	 * @param errMsg
	 * 				后台信息
	 * @param params
	 * 				填充信息
	 * @throws RuntimeException
	 * @date 2015.10.20 
	 * @author jushuang
	 */
	//TODO
	public static <E> void notEmpty(Collection<E> collection, ErrMsg errMsg, Object... params)
			throws RuntimeException {
		notEmpty(collection, errMsg.getErrCd(), errMsg.getErrorMesage(), params);
	}
	
	/**
	 * 集合必须不为空，为空则抛出指定异常
	 * 
	 * @Title: notEmpty
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param map
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @throws RuntimeException
	 * @date 2015年3月10日 上午9:39:14
	 * @author lys
	 */
	public static void notEmpty(Map<?, ?> map, String logInfo)
			throws RuntimeException {
		if (CollectionUtils.isEmpty(map)) {
			throw createException(logInfo);
		}
	}

	/**
	 * 集合必须为空，不空则抛出指定异常
	 * 
	 * @Title: isEmpty
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param collection
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @throws RuntimeException
	 * @date 2015年3月10日 上午9:41:43
	 * @author lys
	 */
	public static <E> void isEmpty(Collection<E> collection, String logInfo)
			throws RuntimeException {
		if (!CollectionUtils.isEmpty(collection)) {
			throw createException(logInfo);
		}
	}

	/**
	 * 对象必须为空，不为空则抛出指定异常
	 * 
	 * @Title: isEmpty
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param collection
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param viewInfo
	 *            页面信息
	 * @throws RuntimeException
	 * @date 2015年3月10日 上午9:40:52
	 * @author lys
	 */
	public static <E> void isEmpty(Collection<E> collection, String logInfo,
			String viewInfo) throws RuntimeException {
		if (!CollectionUtils.isEmpty(collection)) {
			throw createException(logInfo, viewInfo);
		}
	}

	/**
	 * 对象必须为空，不为空则抛出指定异常
	 * 
	 * @Title: isEmpty
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param collection
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param param
	 *            填充信息
	 * @throws RuntimeException
	 * @date 2015年3月11日 下午5:26:08
	 * @author lys
	 */
	public static <E> void isEmpty(Collection<E> collection, String logInfo,
			Object... param) throws RuntimeException {
		if (!CollectionUtils.isEmpty(collection)) {
			throw createException(logInfo, param);
		}
	}

	/**
	 * 对象必须为空，不为空则抛出指定异常
	 * 
	 * @Title: isEmpty
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param collection
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param viewInfo
	 *            页面信息
	 * @param param
	 *            填充信息
	 * @throws RuntimeException
	 * @date 2015年3月31日 上午11:03:22
	 * @author lys
	 */
	public static <E> void isEmpty(Collection<E> collection, String logInfo,
			String viewInfo, Object... param) throws RuntimeException {
		if (!CollectionUtils.isEmpty(collection)) {
			throw createException(logInfo, viewInfo, param);
		}
	}

	/**
	 * 对象必须为空，不为空则抛出指定异常
	 * 
	 * @Title: isEmpty
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param collection
	 *            判定对象
	 * @param errMsg
	 *            后台信息
	 * @param param
	 *            填充信息
	 * @throws RuntimeException
	 * @date 2015年10月20日 上午11:03:22
	 * @author jushuang
	 */
//	TODO
	public static <E> void isEmpty(Collection<E> collection,ErrMsg errMsg, Object... param)
			throws RuntimeException {
		isEmpty(collection, errMsg.getErrCd(), errMsg.getErrorMesage(), param);
	}
	
	/**
	 * 集合必须为空，不为空则抛出指定异常
	 * 
	 * @Title: isEmpty
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param map
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @throws RuntimeException
	 * @date 2015年3月10日 上午9:39:14
	 * @author lys
	 */
	public static void isEmpty(Map<?, ?> map, String logInfo)
			throws RuntimeException {
		if (!CollectionUtils.isEmpty(map)) {
			throw createException(logInfo);
		}
	}

	/**
	 * 对象必须不为空，为空则抛出指定异常
	 * 
	 * @Title: notEmpty
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param map
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param viewInfo
	 *            页面信息
	 * @throws RuntimeException
	 * @date 2015年3月10日 上午9:38:22
	 * @author lys
	 */
	public static void notEmpty(Map<?, ?> map, String logInfo, String viewInfo)
			throws RuntimeException {
		if (CollectionUtils.isEmpty(map)) {
			throw createException(logInfo, viewInfo);
		}
	}

	/**
	 * 对象必须不为空，为空则抛出指定异常
	 * 
	 * @Title: notNull
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param object
	 *            判定对象
	 * @param logInfo
	 *            后台日志
	 * @param viewInfo
	 *            页面信息
	 * @throws RuntimeException
	 * @date 2015年3月11日 下午3:38:40
	 * @author lys
	 */
	public static void notNull(Object object, String logInfo, String viewInfo)
			throws RuntimeException {
		if (null == object) {
			throw createException(logInfo, viewInfo);
		}
	}

	/**
	 * 对象必须不为空，为空则抛出指定异常
	 * 
	 * @Title: notNull
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param object
	 *            判定对象
	 * @param logInfo
	 *            后台日志
	 * @param viewInfo
	 *            页面信息
	 * @param param
	 *            填充信息
	 * @throws RuntimeException
	 * @date 2015年3月31日 上午11:01:16
	 * @author lys
	 */
	public static void notNull(Object object, String logInfo, String viewInfo,
			Object... params) throws RuntimeException {
		if (null == object) {
			throw createException(logInfo, viewInfo, params);
		}
	}

	/**
	 * 对象必须不为空，为空则抛出指定异常
	 * 
	 * @Title: notNull
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param object
	 *            判定对象
	 * @param logInfo
	 *            后台日志
	 * @param param
	 *            异常附加信息
	 * @throws RuntimeException
	 * @date 2015年3月11日 下午3:37:56
	 * @author lys
	 */
	public static void notNull(Object object, String logInfo, Object... param)
			throws RuntimeException {
		if (null == object) {
			throw createException(logInfo, param);
		}
	}

	/**
	 * 对象必须不为空，为空则抛出指定异常
	 * 
	 * @Title: notNull
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param object
	 *            判定对象
	 * @param logInfo
	 *            异常信息
	 * @throws Exception
	 * @date 2014年7月16日 上午11:41:47
	 */
	public static void notNull(Object object, String logInfo)
			throws RuntimeException {
		if (null == object) {
			throw createException(logInfo);
		}
	}

	/**
	 * 对象必须不为空，为空则抛出指定异常
	 * 
	 * @Title: notNull
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param object
	 *            判定对象
	 * @param params
	 *            异常附加信息
 	 * @date 2015年3月11日 下午3:37:56
	 * @author js
	 */
	//TODO
	public static void notNull(Object object, ErrMsg errMsg, Object... params)
			throws RuntimeException{
		notNull(object, errMsg.getErrCd(), errMsg.getErrorMesage(), params);
	}
	
	/**
	 * 对象必须为空，不为空则抛出指定异常
	 * 
	 * @Title: isNull
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param object
	 *            判定对象
	 * @param logInfo
	 *            异常信息
	 * @throws Exception
	 * @date 2014年7月16日 上午11:41:47
	 */
	public static void isNull(Object object, String logInfo)
			throws RuntimeException {
		if (null != object) {
			throw createException(logInfo);
		}
	}

	/**
	 * 对象必须为空，不为空则抛出指定异常
	 * 
	 * @Title: isNull
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param object
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param viewInfo
	 *            页面信息
	 * @throws Exception
	 * @date 2014年7月16日 上午11:43:20
	 */
	public static void isNull(Object object, String logInfo, String viewInfo)
			throws RuntimeException {
		if (null != object) {
			throw createException(logInfo, viewInfo);
		}
	}

	/**
	 * 对象必须为空，不为空则抛出指定异常
	 * 
	 * @Title: isNull
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param object
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param param
	 *            填充信息
	 * @throws RuntimeException
	 * @date 2015年3月11日 下午9:44:58
	 * @author lys
	 */
	public static void isNull(Object object, String logInfo, Object... param)
			throws RuntimeException {
		if (null != object) {
			throw createException(logInfo, param);
		}
	}

	/**
	 * 对象必须为空，不为空则抛出指定异常
	 * 
	 * @Title: isNull
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param object
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param viewInfo
	 *            页面信息
	 * @param param
	 *            填充信息
	 * @throws RuntimeException
	 * @date 2015年3月31日 上午10:56:37
	 * @author lys
	 */
	public static void isNull(Object object, String logInfo, String viewInfo,
			Object... param) throws RuntimeException {
		if (null != object) {
			throw createException(logInfo, viewInfo, param);
		}
	}

	/**
	 * 对象必须为空，不为空则抛出指定异常
	 * 
	 * @Title: isNull
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param object
	 *            判定对象
	 * @param errMsg
	 *            后台信息
	 * @param param
	 *            填充信息
	 * @throws RuntimeException
	 * @date 2015年10月20日 上午10:56:37
	 * @author jushuang
	 */
	//TODO
	public static void isNull(Object object, ErrMsg errMsg, Object... param)
			throws RuntimeException {
		isNull(object, errMsg.getErrCd(), errMsg.getErrorMesage(), param);
	}
	/**
	 * 字符串不为空，为空则抛出指定异常
	 * 
	 * @Title: hasLength
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param text
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @throws RuntimeException
	 * @date 2015年3月31日 上午10:58:07
	 * @author lys
	 */
	public static void hasLength(String text, String logInfo)
			throws RuntimeException {
		if (StringUtils.isBlank(text)) {
			throw createException(logInfo);
		}
	}

	/**
	 * 字符串不为空，为空则抛出指定异常
	 * 
	 * @Title: hasLength
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param text
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param viewInfo
	 *            页面信息
	 * @throws Exception
	 * @date 2014年7月16日 上午11:43:20
	 */
	public static void hasLength(String text, String logInfo, String viewInfo)
			throws RuntimeException {
		if (StringUtils.isBlank(text)) {
			throw createException(logInfo, viewInfo);
		}
	}

	/**
	 * 字符串不为空，为空则抛出指定异常
	 * 
	 * @Title: hasLength
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param text
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param params
	 *            填充信息
	 * @throws RuntimeException
	 * @date 2015年3月31日 上午10:59:19
	 * @author lys
	 */
	public static void hasLength(String text, String logInfo, Object... params)
			throws RuntimeException {
		if (StringUtils.isBlank(text)) {
			throw createException(logInfo, params);
		}
	}

	/**
	 * 字符串不为空，为空则抛出指定异常
	 * 
	 * @Title: hasLength
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param text
	 *            判定对象
	 * @param logInfo
	 *            后台信息
	 * @param viewInfo
	 *            页面信息
	 * @param params
	 *            填充信息
	 * @throws RuntimeException
	 * @date 2015年3月31日 上午10:59:37
	 * @author lys
	 */
	public static void hasLength(String text, String logInfo, String viewInfo,
			Object... params) throws RuntimeException {
		if (StringUtils.isBlank(text)) {
			throw createException(logInfo, viewInfo, params);
		}
	}
	
	/**
	 * 字符串不为空，为空则抛出指定异常
	 * 
	 * @Title: hasLength
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param text
	 *            判定对象
	 * @param errMsgs
	 *            后台信息
	 * @param params
	 *            填充信息
	 * @throws RuntimeException
	 * @date 2015年10月20日 上午10:59:37
	 * @author jushuang
	 */
	//TODO
	public static void hasLength(String text, ErrMsg errMsg,
			Object... params) throws RuntimeException {
		hasLength(text, errMsg.getErrCd(), errMsg.getErrorMesage(), params);
	}

	/**
	 * 异常生产者
	 * 
	 * @Title: createException
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param logInfo
	 * @return
	 * @date 2015年3月10日 上午9:32:10
	 * @author lys
	 */
	private static RuntimeException createException(String logInfo) {
		return createException(BaseException.class, CommonEnum.COMMON_01,
				logInfo, null);
	}

	/**
	 * 异常生产者
	 * 
	 * @Title: createException
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param logInfo
	 *            后台信息
	 * @param viewInfo
	 *            页面信息
	 * @return
	 * @date 2015年3月10日 上午9:32:38
	 * @author lys
	 */
	private static RuntimeException createException(String logInfo,
			String viewInfo) {
		return createException(BaseException.class, CommonEnum.COMMON_02,
				logInfo, viewInfo);
	}

	/**
	 * 异常生产者
	 * 
	 * @Title: createException
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param logInfo
	 *            后台信息
	 * @param params
	 *            填充信息
	 * @return
	 * @date 2015年3月10日 上午10:44:27
	 * @author lys
	 */
	private static RuntimeException createException(String logInfo,
			Object... params) {
		logInfo = coverMessage(logInfo, params);
		return createException(BaseException.class, CommonEnum.COMMON_01,
				logInfo, null);
	}

	/**
	 * 异常生产者
	 * 
	 * @Title: createException
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param logInfo
	 *            后台信息
	 * @param viewInfo
	 *            页面信息
	 * @param params
	 *            填充信息
	 * @return
	 * @date 2015年3月10日 上午10:44:56
	 * @author lys
	 */
	private static RuntimeException createException(String logInfo,
			String viewInfo, Object... params) {
		String content = new StringBuilder(logInfo).append("|")
				.append(viewInfo).toString();
		content = coverMessage(content, params);
		String[] arr = content.split("\\|");
		return createException(BaseException.class, CommonEnum.COMMON_01,
				arr[0], arr[1]);
	}

	/**
	 * 异常生产者
	 * 
	 * @Title: createException
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param clazz
	 * @param exObj
	 * @param logInfo
	 * @param viewInfo
	 * @return
	 * @date 2015年3月10日 上午9:30:30
	 * @author lys
	 */
	private static RuntimeException createException(
			Class<? extends BaseException> clazz, ExceptionObject exObj,
			String logInfo, String viewInfo) {
		try {
			exObj.setLogInfo(logInfo);
			exObj.setViewInfo(viewInfo);
			return clazz.getConstructor(ExceptionObject.class).newInstance(
					exObj);
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

	private static String coverMessage(String content, Object... args) {
		if (null == content || args == null || args.length <= 0) {
			return content;
		}
		for (Object obj : args) {
			content = content.replaceFirst("\\{\\}", String.valueOf(obj));
		}
		return content;
	}

}
