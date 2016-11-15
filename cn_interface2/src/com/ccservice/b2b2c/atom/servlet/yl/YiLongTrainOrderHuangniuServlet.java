package com.ccservice.b2b2c.atom.servlet.yl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;

/**
 * 艺龙 先支付模式 3.13 黄牛模式推单接口
 * 
 * @time 2015年12月8日 上午11:36:31
 * @author chendong
 */
public class YiLongTrainOrderHuangniuServlet extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    String key = "";

    Map<String, InterfaceAccount> interfaceAccountMap = new HashMap<String, InterfaceAccount>();

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=utf-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        int r1 = new Random().nextInt(10000000);
        JSONObject resultJson = new JSONObject();
        String result = "";
        PrintWriter out = null;
        //              merchantId  供应商id   是   String  是   分配给艺龙的id
        //            timeStamp   推送时间戳   是   String  是   发送请求的时间戳
        //            orderId 订单号 是   String  是   下单订单号
        //            paramJson   json串   是   String  是   下单json串
        //            sign    签名  是   String  否   签名
        String merchantId = req.getParameter("merchantId");
        String timeStamp = req.getParameter("timeStamp");
        String orderId = req.getParameter("orderId");
        String paramJson = req.getParameter("paramJson");
        String sign = req.getParameter("sign");
        WriteLog.write("YiLongTrainOrderHuangniuServlet", r1 + ":merchantId:" + merchantId + ":timeStamp:" + timeStamp
                + ":orderId:" + orderId + ":sign:" + sign + ":paramJson:" + paramJson);
        InterfaceAccount interfaceAccount = interfaceAccountMap.get(merchantId);
        if (interfaceAccount == null) {
            interfaceAccount = getInterfaceAccountByLoginname(merchantId);
            if (interfaceAccount != null && interfaceAccount.getKeystr() != null
                    && interfaceAccount.getInterfacetype() != null) {
                interfaceAccountMap.put(merchantId, interfaceAccount);
            }
        }
        WriteLog.write("YiLongTrainOrderHuangniuServlet", r1 + ":merchantId:" + merchantId + ":timeStamp:" + timeStamp
                + ":orderId:" + orderId + ":sign:" + sign + ":paramJson:" + paramJson);
        String localSign = "merchantId=" + merchantId + "&timeStamp=" + timeStamp + "&orderId=" + orderId
                + "&paramJson=" + paramJson;
        this.key = interfaceAccount.getKeystr();
        localSign = getSignMethod(localSign) + this.key;
        WriteLog.write("YiLongTrainOrderHuangniuServlet", "传值：" + localSign + "获取key" + this.key);
        try {
            localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
        }
        catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        WriteLog.write("YiLongTrainOrderHuangniuServlet", "相比较：" + localSign + ":==:" + sign);
        if (localSign.equals(sign)) {
            WriteLog.write("YiLongTrainOrderHuangniuServlet", "有没有进来");
            YiLongTrainOrderMethod yiLongTrainOrderMethod = new YiLongTrainOrderMethod();
            result = yiLongTrainOrderMethod.createYlTrainOrder(paramJson, TrainInterfaceMethod.YILONG2, merchantId);
        }
        else {

            resultJson.put("retcode", "403");
            resultJson.put("retdesc", "签名校验失败");
            result = resultJson.toJSONString();
        }
        WriteLog.write("YiLongTrainOrderHuangniuServlet", r1 + ":result:" + result + ":orderId" + orderId);
        try {
            out = res.getWriter();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (out != null) {
                out.print(result);
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 根据用户名获取到这个用户的key
     * 
     * @param loginname
     * @return
     * @time 2015年1月14日 下午7:01:07
     * @author fiend
     */
    @SuppressWarnings("unchecked")
    private InterfaceAccount getInterfaceAccountByLoginname(String loginname) {
        WriteLog.write("YiLongTrainOrderServlet", "loginname:" + loginname);
        List<InterfaceAccount> list_interfaceAccount = new ArrayList<InterfaceAccount>();
        InterfaceAccount interfaceAccount = new InterfaceAccount();
        try {
            list_interfaceAccount = Server.getInstance().getMemberService()
                    .findAllInterfaceAccount("where C_USERNAME = '" + loginname + "'", null, -1, 0);
        }
        catch (Exception e) {
        }
        WriteLog.write("YiLongTrainOrderServlet", "list_interfaceAccount:" + list_interfaceAccount.size());
        if (list_interfaceAccount.size() > 0) {
            interfaceAccount = list_interfaceAccount.get(0);
        }
        else {
            interfaceAccount.setUsername(loginname);
            interfaceAccount.setKeystr(this.key);
            interfaceAccount.setInterfacetype(TrainInterfaceMethod.YILONG2);
        }
        return interfaceAccount;
    }

    /**
     * 
     * 
     * @param json
     * @return
     * @time 2015年12月10日 上午11:47:17
     * @author Mr.Wang
     */
    private static String getSignMethod(String sign) {
        if (!ElongHotelInterfaceUtil.StringIsNull(sign)) {
            String[] signParam = sign.split("&");
            sign = ElongHotelInterfaceUtil.sort(signParam);
            return sign;
        }
        return "";
    }
}
