package com.caitu99.service.user.controller.api;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.exception.UserNotFoundException;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Controller
@RequestMapping("/api/user/photo")
public class PhotoController extends BaseController {

    public static final String URL = "http://static.caitu99.com/img/headimg/";
    @Autowired
    private UserService userService;
    @Autowired
    private AppConfig appConfig;

    @RequestMapping(method = RequestMethod.POST, value = "/save/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String saveImg(String imgStr, Long userid) {
        ApiResult<String> result = new ApiResult<>();
        User loginUser = userService.getById(userid);
        if (loginUser == null) throw new UserNotFoundException(2037, "用户不存在");
        if (StringUtils.isBlank(imgStr)) throw new ApiException(2106, "图片字符串不能为空");
        byte[] imgBytes = Base64.getDecoder().decode(imgStr);
        try {
            String rootPath = appConfig.uploadPath + "/img/headimg";
            File diskPathFile = new File(rootPath);
            if (!diskPathFile.exists()) {
                diskPathFile.mkdirs();
            }
            String filename = UUID.randomUUID().toString() + ".jpg";
            File dest = new File(diskPathFile, filename);
            FileOutputStream fos = new FileOutputStream(dest);
            fos.write(imgBytes);
            fos.close();
            User user = new User();
            user.setId(userid);
            String saveUrl = filename;
            user.setImgurl(saveUrl);
            userService.updateByPrimaryKeySelective(user);
            result.setCode(0);
            result.setData(URL + saveUrl);//返回可访问的地址给前端
            result.setMessage("用户头像保存成功");
            return JSON.toJSONString(result);
        } catch (IOException e) {
            result.setCode(2304);
            result.setMessage("用户头像保存失败");
            return JSON.toJSONString(result);
        }
    }

}
