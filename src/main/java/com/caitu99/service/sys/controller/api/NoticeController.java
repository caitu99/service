package com.caitu99.service.sys.controller.api;

import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.sys.domain.Notice;
import com.caitu99.service.sys.service.NoticeService;

@Controller
@RequestMapping("/api/notice")
public class NoticeController  extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(ConfigController.class);

	@Autowired
	private NoticeService noticeService;

	@RequestMapping(value={"/notice/1.0","/user/notice/1.0"}, produces="application/json;charset=utf-8")
	@ResponseBody
	public String getNotice(Long userId) throws ParseException {
		//初始化
		ApiResult<List<Notice>> result = new ApiResult<>();
		//业务实现
		List<Notice> noticeList = noticeService.select(userId);
		if(noticeList.size() > 0 ) {
			result.toJSONString(0,"信息查询成功",noticeList);
		} else {
			result.toJSONString(2301,"没有查询到消息");
		}
		return JSON.toJSONString(result);
	}
}
