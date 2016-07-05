package com.caitu99.service.file.controller;

import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.file.qiniu.QiniuFileUtil;
import com.caitu99.service.file.qiniu.QiniuUploadRet;
import com.caitu99.service.message.service.GwInfoService;

@Controller
@RequestMapping("/public/upload/")
public class FileController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	private GwInfoService gwInfoService;
	@Autowired
	private AppConfig appConfig;
	
	/**
	 * 文件上传
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: upload 
	 * @param request
	 * @return
	 * @date 2016年4月26日 下午5:31:13  
	 * @author xiongbin
	 */
	@RequestMapping(value="file/single/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String upload(HttpServletRequest request) {
		ApiResult<String> result = new ApiResult<String>();
		
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		if (!multipartResolver.isMultipart(request)) {
            return result.toJSONString(-1, "上传文件异常");
		}
		
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
		Iterator<String> iter = multiRequest.getFileNames();
		
		try {
			QiniuFileUtil util = new QiniuFileUtil();
			
			while (iter.hasNext()) {
				MultipartFile file = multiRequest.getFile(iter.next());
				if (file == null) {
					continue;
				}
				
				//效验文件是否为图片
				String name = file.getOriginalFilename();
				String postfix = name.substring(name.lastIndexOf(".")+1, name.length());
				if("jpg|png|jpeg|JPG|PNG|JPEG".indexOf(postfix) == -1){
					return result.toJSONString(-1, "上传文件类型错误,只支持上传jpg|jpeg|png格式图片");
				}
				
				//文件名称
				name = System.currentTimeMillis() + "." + postfix;
				
				//调用图片服务
				QiniuUploadRet qu = util.upload(file.getBytes(), name);

				return result.toJSONString(0,"success",appConfig.fileUrl + "/" + qu.getKey());
			}
		} catch (Exception e) {
			logger.error("上传文件出错:" + e.getMessage(),e);
			return result.toJSONString(-1,"上传文件出错:" + e.getMessage());
		}

        return result.toJSONString(-1, "上传文件异常");
	}
	
	/**
	 * 上传
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: upload 
	 * @param request
	 * @param response
	 * @throws Exception
	 * @date 2016年4月28日 上午11:01:03  
	 * @author xiongbin
	 */
	@RequestMapping(value="file/multi/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public void upload(HttpServletRequest request,HttpServletResponse response) throws Exception {
		PrintWriter pw = response.getWriter();
		
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		if (!multipartResolver.isMultipart(request)) {
			pw.print("上传文件异常");
			return;
		}
		
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
		Iterator<String> iter = multiRequest.getFileNames();
		
		try {
			QiniuFileUtil util = new QiniuFileUtil();
			
			while (iter.hasNext()) {
				
				String state = "SUCCESS";
				
				MultipartFile file = multiRequest.getFile(iter.next());
				if (file == null) {
					continue;
				}
				
				//效验文件是否为图片
				String name = file.getOriginalFilename();
				String postfix = name.substring(name.lastIndexOf(".")+1, name.length());
				if("jpg|png|jpeg|JPG|PNG|JPEG".indexOf(postfix) == -1){
					state = "\\u4e0d\\u5141\\u8bb8\\u7684\\u6587\\u4ef6\\u683c\\u5f0f";
				}
				
				//文件名称
				name = System.currentTimeMillis() + "." + postfix;
				
				//调用图片服务
				QiniuUploadRet qu = util.upload(file.getBytes(), name);
				
				StringBuffer key = new StringBuffer();
				key.append("{'original':'")
					.append(name)
					.append("','url':'")
					.append(appConfig.fileUrl + "/" + qu.getKey())
					.append("','title':'")
					.append(name)
					.append("','state':'")
					.append(state)
					.append("'}");
					
				pw.print(key.toString());
			}
		} catch (Exception e) {
			logger.error("上传文件出错:" + e.getMessage(),e);
		}
	}
}
