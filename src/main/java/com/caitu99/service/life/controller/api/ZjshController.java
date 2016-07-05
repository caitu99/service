package com.caitu99.service.life.controller.api;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.caitu99.service.base.BaseController;
import com.caitu99.service.life.domain.Activation;
import com.caitu99.service.life.service.ActivationService;
import com.caitu99.service.utils.date.DateUtil;
import com.caitu99.service.utils.file.FileUtil;

/**
 * 助家生活Controller
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ZjshController 
 * @author ws
 * @date 2015年11月2日 下午2:40:12 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Controller
@RequestMapping("/api/life/zjsh")
public class ZjshController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ZjshController.class);

    @Autowired
    private ActivationService activationService;
    
    /**
     * 批量导入激活码
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: add 
     * @param filePath
     * @throws ParseException
     * @throws IOException
     * @date 2015年11月5日 上午11:42:31  
     * @author ws
     */
    @RequestMapping("/add/1.0")
    public void add(String filePath) throws ParseException, IOException {
        String fileText = FileUtil.readTxtFile(filePath);
        if (null != fileText) {
            Date date = DateUtil.getSimpleDateFormat(DateUtil.DEFAULT_DATE);
            String[] vations = fileText.split(",");
            for (String vation : vations) {
            	if (StringUtils.isBlank(vation)) {
            		continue;
				}
                Activation activation = new Activation();
                activation.setActivation(vation.trim());
                activation.setValid(date);
                activation.setStatus(Activation.ACTIVATION_STATE_NOMAL);
                activation.setType(1);//TODO 什么类型
                activationService.insertSelective(activation);
			}
        }else{
        	logger.warn("找不到指定文件：{}", filePath);
        }
    }
/*
    public String readTxtFile(String filePath) throws IOException {
    	InputStreamReader read = null;
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) { 
                read = new InputStreamReader(new FileInputStream(file),
                		Constants.DEFAULT_CHATSET);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    return lineTxt;
                }
                read.close();
            } else {
                logger.warn("助家生活-找不到指定文件：{}", filePath);
            }
        } catch (Exception e) {
            logger.error("助家生活-读取文件错误：{}", filePath, e);
        } finally{
        	if(null != read)
				read.close();
        }
        return null;
    }*/

}
