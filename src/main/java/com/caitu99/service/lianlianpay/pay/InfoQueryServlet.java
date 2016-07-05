package com.caitu99.service.lianlianpay.pay;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.lianlianpay.config.PartnerConfig;
import com.caitu99.service.lianlianpay.config.ServerURLConfig;
import com.caitu99.service.lianlianpay.conn.HttpRequestSimple;
import com.caitu99.service.lianlianpay.utils.LLPayUtil;

/**
* 已绑定银行卡信息查询
* @author guoyx
* @date:Jun 10, 2014 3:40:39 PM
* @version :1.0
*
*/
public class InfoQueryServlet extends HttpServlet{
    private static final long   serialVersionUID    = 1L;
    private static final String QUERY_BANKCARD_LIST = "query_bankcard_list"; // 查询已绑定银行卡列表
    private static final String QUERY_CARD_BIN      = "query_card_bin";     // 查询银行卡卡bin信息

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        String operate = req.getParameter("operate");
        String resJson = "";
        if (QUERY_BANKCARD_LIST.equals(operate))
        {
            resJson = queryBankcardList(req);
        } else if (QUERY_CARD_BIN.equals(operate))
        {
            resJson = queryCardBin(req);
        } else
        {
            resJson = "{\"ret_code\":\"9999\",\"ret_msg\":\"非法交易\"}";
        }
        resp.getWriter().write(resJson);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    /**
     * 银行卡卡bin信息查询
     * @param req
     * @return
     */
    private String queryCardBin(HttpServletRequest req)
    {

        JSONObject reqObj = new JSONObject();
        reqObj.put("oid_partner", PartnerConfig.OID_PARTNER);
        reqObj.put("card_no", req.getParameter("card_no"));
        reqObj.put("sign_type", PartnerConfig.SIGN_TYPE);
        String sign = LLPayUtil.addSign(reqObj, PartnerConfig.TRADER_PRI_KEY,
                PartnerConfig.MD5_KEY);
        reqObj.put("sign", sign);
        String reqJSON = reqObj.toString();
        System.out.println("银行卡卡bin信息查询请求报文[" + reqJSON + "]");
        String resJSON = HttpRequestSimple.getInstance().postSendHttp(
                ServerURLConfig.QUERY_BANKCARD_URL, reqJSON);
        System.out.println("银行卡卡bin信息查询响应报文[" + resJSON + "]");
        return resJSON;
    }

    /**
     * 用户已绑定银行列表查询
     * @param req
     * @return
     */
    private String queryBankcardList(HttpServletRequest req)
    {
        JSONObject reqObj = new JSONObject();
        reqObj.put("oid_partner", PartnerConfig.OID_PARTNER);
        reqObj.put("user_id", req.getParameter("user_id"));
        reqObj.put("offset", "0");
        reqObj.put("sign_type", PartnerConfig.SIGN_TYPE);
        String sign = LLPayUtil.addSign(reqObj, PartnerConfig.TRADER_PRI_KEY,
                PartnerConfig.MD5_KEY);
        reqObj.put("sign", sign);
        String reqJSON = reqObj.toString();
        System.out.println("用户已绑定银行列表查询请求报文[" + reqJSON + "]");
        String resJSON = HttpRequestSimple.getInstance().postSendHttp(
                ServerURLConfig.QUERY_USER_BANKCARD_URL, reqJSON);
        System.out.println("用户已绑定银行列表查询响应报文[" + resJSON + "]");
        return resJSON;
    }

}
