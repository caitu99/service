package com.caitu99.service.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class Configuration {
	private static Properties props = new Properties();
	static {
		try {
			props.load(Configuration.class.getClassLoader()
					.getResourceAsStream("properties/app.properties"));
			props.load(Configuration.class.getClassLoader()
					.getResourceAsStream("properties/qiniu.properties"));
			props.load(Configuration.class.getClassLoader()
					.getResourceAsStream("properties/union.properties"));
			props.load(Configuration.class.getClassLoader()
					.getResourceAsStream("properties/cl.properties"));
			props.load(new InputStreamReader(Configuration.class.getClassLoader()
					.getResourceAsStream("properties/push.properties"),"UTF-8"));
			props.load(Configuration.class.getClassLoader()
					.getResourceAsStream("properties/sxf.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key, String defaultValue) {
		String value = props.getProperty(key);
		if (StringUtils.isEmpty(value))
			return defaultValue;
		return value;
	}
}
