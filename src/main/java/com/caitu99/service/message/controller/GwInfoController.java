package com.caitu99.service.message.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.message.domain.GwInfo;
import com.caitu99.service.message.domain.GwInfoClassify;
import com.caitu99.service.message.service.GwInfoClassifyService;
import com.caitu99.service.message.service.GwInfoService;

@Controller
@RequestMapping("/api/info/")
public class GwInfoController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(GwInfoController.class);
	
	private static final String[] GW_INFO_CLASSIFY_LIST_FILLTER = {"id","name"};
	private static final String[] GW_INFO_LIST_FILLTER = {"id","title","gmtPublish","author","digest","images"};
	private static final String[] GW_INFO_FILLTER = {"id","title","author","classId","images","digest","body","gmtPublish"};

	@Autowired
	private GwInfoService gwInfoService;
	@Autowired
	private GwInfoClassifyService gwInfoClassifyService;
	
	/**
	 * 查询分类列表
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: upload 
	 * @return
	 * @date 2016年4月28日 上午11:19:13  
	 * @author xiongbin
	 */
	@RequestMapping(value="classify/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String classifyList() {
		ApiResult<List<GwInfoClassify>> result = new ApiResult<List<GwInfoClassify>>();
		List<GwInfoClassify> list = gwInfoClassifyService.selectPageList();
		return result.toJSONString(0,"success",list,GwInfoClassify.class,GW_INFO_CLASSIFY_LIST_FILLTER);
	}
	
	/**
	 * 新增官网资讯
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: add 
	 * @param gwInfo
	 * @return
	 * @date 2016年4月28日 下午12:07:15  
	 * @author xiongbin
	 */
	@RequestMapping(value="gw/add/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String add(GwInfo gwInfo) {
		ApiResult<String> result = new ApiResult<String>();
		gwInfoService.insert(gwInfo);
		return result.toJSONString(0,"success");
	}
	
	/**
	 * 官网资讯列表
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: list 
	 * @param gwInfo
	 * @param pagination
	 * @return
	 * @date 2016年4月28日 下午2:39:00  
	 * @author xiongbin
	 */
	@RequestMapping(value="gw/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String list(GwInfo gwInfo,Pagination<GwInfo> pagination) {
		ApiResult<Pagination<GwInfo>> result = new ApiResult<Pagination<GwInfo>>();
		pagination = gwInfoService.findPageItem(gwInfo, pagination);
		return result.toJSONString(0,"success",pagination,GwInfo.class,GW_INFO_LIST_FILLTER);
	}
	
	/**
	 * 查询资讯数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: select 
	 * @param id	主键
	 * @return
	 * @date 2016年4月28日 下午3:47:34  
	 * @author xiongbin
	 */
	@RequestMapping(value="gw/select/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String select(Long id) {
		ApiResult<GwInfo> result = new ApiResult<GwInfo>();
		GwInfo gwInfo = gwInfoService.selectByPrimaryKey(id);
		return result.toJSONString(0,"success",gwInfo,GwInfo.class,GW_INFO_FILLTER);
	}
	
	/**
	 * 修改官网资讯
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: add 
	 * @param gwInfo
	 * @return
	 * @date 2016年4月28日 下午12:07:15  
	 * @author xiongbin
	 */
	@RequestMapping(value="gw/update/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String update(GwInfo gwInfo) {
		ApiResult<String> result = new ApiResult<String>();
		gwInfoService.update(gwInfo);
		return result.toJSONString(0,"success");
	}
}
