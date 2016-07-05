package com.caitu99.service.merchant.controller;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.merchant.controller.vo.UserProxyInfoVo;
import com.caitu99.service.merchant.service.ProxyRelationService;
import com.caitu99.service.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * todo
 *
 * @author wugaoda
 * @Description: 代理上下级别关系，个人代理信息接口
 * @ClassName: ProxyRelationController
 * @date 2016年06月21日 09:22
 * @Copyright (c) 2015-2020 by caitu99
 */

@Controller
@RequestMapping("/api/merchant/agent/relation")
public class ProxyRelationController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(ProxyRelationController.class);


    @Autowired
    ProxyRelationService proxyRelationService;

    /**
     *
     * @param userid 用户ID
     * @return
     */
    @RequestMapping(value = "/superior/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String superiorInfo(Long userid) {
        ApiResult<User> result = new ApiResult<>();
        if(null == userid){
            result.toJSONString(-1, "参数userid不能为空");
        }
        String[] filter = {"nick","contacts"};
        User superiorUser=proxyRelationService.selectSuperiorUserInfo(userid);
        if (null==superiorUser){
           return result.toJSONString(-3, "该用户无上级代理");
        }
        return result.toJSONString(0, "Success", superiorUser,User.class,filter);
    }


    /**
     *
     * @param userid 用户ID
     * @return
     */
    @RequestMapping(value = "/self/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String selfInfo(Long userid) {
        ApiResult<UserProxyInfoVo> result = new ApiResult<>();
        if(null == userid){
            result.toJSONString(-1, "参数userid不能为空");
        }
        UserProxyInfoVo currentUser=proxyRelationService.selectSelfInfo(userid);

        return result.toJSONString(0, "Success", currentUser);
    }

}
