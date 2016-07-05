package com.caitu99.service.sys.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.sys.controller.vo.VersionVO;
import com.caitu99.service.sys.domain.Version;
import com.caitu99.service.sys.service.VersionService;
import com.caitu99.service.utils.string.StrUtil;

@Controller
@RequestMapping("/api/version")
public class VersionController  extends BaseController {
	@Autowired
	private VersionService versionService;

	@RequestMapping(value="/goup/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String goup(String version,Long type) {
		ApiResult<VersionVO> result = new ApiResult<VersionVO>();
		result.setCode(0);
		Version versionlist=new Version();
		versionlist.setStatus(type.intValue());
		List<Version> versions = versionService.findbustatus(versionlist);
		
		VersionVO versionVO = new VersionVO();
		if (StrUtil.isEmpty(version)) {
			versionVO.setResult(0L);
			result.setCode(2303);
			result.setData(versionVO);
			result.setMessage("版本号不能为空");
			return JSON.toJSONString(result);
		}
		int no = -1;
		int upno = -1;
		for (Version list : versions) {
			// 拿到对应版本的NO
			if (version.equals(list.getVersion())) {
				no = list.getNo();
			}
			// 拿到必须跟新NO
			if (list.getNecessary() == 1) {
				upno = list.getNo();
			}

		}
		// 没有对应版本号
		if (no == -1) {
			versionVO.setResult(4L);
			result.setData(versionVO);
			return JSON.toJSONString(result);
		}
		versionVO.setVersion(versions.get(versions.size() - 1));
		// 当有必须跟新版本返回1
		if (upno != -1) {
			if (no < upno) {
				versionVO.setResult(1L);
				result.setData(versionVO);
				return JSON.toJSONString(result);

			}
		}
		// 最新版本时返回2
		if (no == versions.get(versions.size() - 1).getNo()) {
			versionVO.setResult(2L);
			result.setData(versionVO);
			return JSON.toJSONString(result);
		}
		// 正常版本跟新返回3
		versionVO.setResult(3L);
		result.setCode(0);
		result.setData(versionVO);
		return JSON.toJSONString(result);
	}
	
	/**
	 * 查询最新版本
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: findNewest 
	 * @return
	 * @date 2016年5月10日 下午5:48:04  
	 * @author xiongbin
	 */
	@RequestMapping(value="/newest/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String findNewest() {
		ApiResult<String> result = new ApiResult<String>();
		Version version = versionService.findNewest();
		if(null == version){
			return result.toJSONString(-1, "查询版本出现问题");
		}
		
		return result.toJSONString(0, "",version.getVersion());
	}
}
