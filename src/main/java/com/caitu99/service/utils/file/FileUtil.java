package com.caitu99.service.utils.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caitu99.service.constants.SysConstants;

/**
 * 文件处理Util
 * 
 * @Description: (文件处理的工具类) 
 * @ClassName: FileUtil 
 * @author ws
 * @date 2015年11月2日 下午2:43:39 
 * @Copyright (c) 2015-2020 by caitu99
 */
public class FileUtil {
	

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	/**
	 * 使用BufferedReader读取txt文件
	 * 	
	 * @Description: (使用BufferedReader读取txt文件)  
	 * @Title: readTxtFile 
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @date 2015年11月2日 下午2:51:34  
	 * @author ws
	 */
	public static String readTxtFile(String filePath) throws IOException {
    	InputStreamReader read = null;
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) { 
            	FileInputStream inputStream = new FileInputStream(file);
                read = new InputStreamReader(inputStream ,SysConstants.DEFAULT_CHATSET);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    return lineTxt;
                }
                read.close();
            } else {
                logger.warn("找不到指定文件：{}", filePath);
            }
        } catch (Exception e) {
        	/*throw*/ 
            logger.error("读取文件错误：{}", filePath, e);
        } finally{
        	if(null != read)
				read.close();
        }
        return null;
    }
}
