/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.life.controller.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.BaseController;
import com.caitu99.service.life.domain.Activation;
import com.caitu99.service.life.service.ActivationService;
import com.caitu99.service.utils.date.DateUtil;

/** 
 * 
 * @Description: (凹凸租车) 
 * @ClassName: AtzucheController 
 * @author chenhl
 * @date 2015年11月9日 上午11:43:03 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Controller
@RequestMapping("/api/life/atzc")
public class AtzucheController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AtzucheController.class);

    @Autowired
    private ActivationService activationService;
    
    /**
     * 批量导入凹凸租车的激活码
     * 	
     * @Description: (批量导入凹凸租车的激活码)  
     * @Title: add 
     * @param filePath
     * @throws ParseException
     * @throws IOException
     * @date 2015年11月9日 上午11:43:03  
     * @author chenhl
     */
    
    @RequestMapping("/add/1.0")
    @ResponseBody
    public String ReadText(String filePath) throws IOException, ParseException {
		String encoding = "GBK";
		File file = new File(filePath);
		Date date = DateUtil.getSimpleDateFormat(DateUtil.DEFAULT_DATE_AOTUQUAN);
		
		Activation activation = new Activation();
        activation.setValid(date);
        activation.setStatus(Activation.ACTIVATION_STATE_NOMAL);
        activation.setType(5);//TODO 类型  5是凹凸租车
        activation.setPrice(200); //券的价值
		
		if (file.isFile() && file.exists()) { // 判断文件是否存在
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				activation.setActivation(lineTxt.trim());
				activationService.insertSelective(activation);
			}
			read.close();
			System.out.println("文档扫描完毕");
			return "添加成功";
		} else {
			logger.warn("找不到指定文件：{}", filePath);
			return "找不到指定文件：{}";
		}
	}
    
    
//    @RequestMapping("/add/1.0")
//    @ResponseBody
//    public String add(String filePath) throws ParseException, IOException {
//        String fileText = FileUtil.readTxtFile(filePath);
//        if (null != fileText) {
//            Date date = DateUtil.getSimpleDateFormat(DateUtil.DEFAULT_DATE_AOTUQUAN);
//            String[] vations = fileText.split(",");
//            for (String vation : vations) {
//            	if (StringUtils.isBlank(vation)) {
//            		continue; 
//				}
//                Activation activation = new Activation();
//                activation.setActivation(vation.trim());
//                activation.setValid(date);
//                activation.setStatus(Activation.ACTIVATION_STATE_NOMAL);
//                activation.setType(5);//TODO 类型  5是凹凸租车
//                activation.setPrice(200); //券的价值
//                activationService.insertSelective(activation);
//			}
//            return "添加成功";
//        }else{
//        	logger.warn("找不到指定文件：{}", filePath);
//        	return "找不到指定文件：{}";
//        }
//    }
    
}
