package com.ccservice.b2b2c.atom.servlet.chongdong;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;

import sun.misc.BASE64Encoder;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.component.ticket.util.Base64Utils;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.CCSPostMethod;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class WormholeShouquan {

    public static void main(String[] args) {
        //        WormholeShouquan wormholeShouquan = new WormholeShouquan();
        //        System.out.println(wormholeShouquan.sendjson("1351777170172967"));
        //        System.out.println(wormholeShouquan.make_base_auth("hangtianhuayou.api", "Op8^Aa5%"));

    }

    /**
     * 虫洞授权接口
     * 杨荣强
     */
    public final static String userName = "hangtianhuayou.api";

    //正式
    public final static String password = "Vy2$Tw8^";

    //测试
    //    public final static String password = "Op8^Aa5%";
    //正式
    public final static String congDongAuthUrl = "https://www.wfinance.com.cn/cdi/v2/authorizations/submit";

    //测试
    //    public final static String congDongAuthUrl = "https://stage.wfinance.com.cn:10443/cdi/v2/authorizations/submit";

    public static final String CURRENCY = "CNY";

    public String sendjson(String qunarordernumber) {
        return sendjson(qunarordernumber, 0);
    }

    public String sendjson(String qunarordernumber, int insureNum) {
        String result = "";
        int r1 = new Random().nextInt(10000000);
        //        String sql = "select * from  T_trainorder a,T_TRAINPASSENGER b,T_TRAINTICKET c  where b.C_ORDERID=a.ID and c.C_TRAINPID=b.ID and  a.C_QUNARORDERNUMBER='"
        //                + ordernumber + "'";
        String sql = "EXEC [dbo].[sp_T_TRAINORDER_LinkedQuery] @qunarordernumber='" + qunarordernumber + "'";
        Map map = new HashMap<String, Object>();
        try {
            map = (Map) Server.getInstance().getSystemService().findMapResultBySql(sql, null).get(0);
        }
        catch (Exception e1) {
            WriteLog.write("虫洞授权接口_Exception", r1 + "----->sql" + sql);
            ExceptionUtil.writelogByException("虫洞授权接口_Eorror", e1);
            e1.printStackTrace();
        }
        int orderid = Integer.parseInt(map.get("C_ORDERID").toString());
        //        String accountNumber = map.get("C_SUPPLYACCOUNT") == null ? "" : map.get("C_SUPPLYACCOUNT").toString();

        Double price = Double.parseDouble(map.get("C_PRICE").toString());
        int agentid = Integer.parseInt(map.get("C_AGENTID").toString());
        int ticketcount = Integer.parseInt(map.get("C_TICKETCOUNT").toString());

        //        String sqlString = "select COUNT(*) from TrainNeedInsureInformation where OrderId=" +orderid;
        String sqlString = "EXEC [dbo].[sp_TrainNeedInsureInformation_Query] @orderid=" + orderid;
        List list = new ArrayList();
        try {
            list = Server.getInstance().getSystemService().findMapResultBySql(sqlString, null);
        }
        catch (Exception e1) {
            WriteLog.write("虫洞授权接口_Exception", r1 + "---->sqlString" + sqlString);
            ExceptionUtil.writelogByException("虫洞授权接口_Exception", e1);
            e1.printStackTrace();
        }
        String[] s = list.get(0).toString().replace("}", "").split("=");
        int rows = Integer.parseInt(s[1]);
        Double amount = price * ticketcount + rows * 20 + insureNum * 20 /*+ ticketcount * 2*/;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("accountNumber", getAccount(qunarordernumber));
            jsonObject.put("currency", CURRENCY);
            jsonObject.put("amount", amount);
        }
        catch (Exception e) {
            WriteLog.write("虫洞授权接口_Exception", r1 + "------>json异常" + jsonObject.toString());
            ExceptionUtil.writelogByException("虫洞授权接口_Exception", e);
            e.printStackTrace();
        }
        //        String sqlurlString = "select C_WormholeShouquanUrl from T_INTERFACEACCOUNT where C_AGENTID=" + @agentid;
        String sqlurlString = "EXEC [dbo].[sp_T_INTERFACEACCOUNT_Query] @agentid=" + agentid;
        Map map2 = new HashMap();
        try {
            map2 = (Map) Server.getInstance().getSystemService().findMapResultBySql(sqlurlString, null).get(0);
        }
        catch (Exception e) {
            WriteLog.write("虫洞授权接口_Exception", r1 + "------>sqlurlString" + sqlurlString);
            ExceptionUtil.writelogByException("虫洞授权接口_Exception", e);
            e.printStackTrace();
        }
        String URLString = map2.get("C_WormholeShouquanUrl") == null ? "" : map2.get("C_WormholeShouquanUrl")
                .toString();

        //        result = SendPostandGet.submitGet2(URLString, jsonObject.toString(), "UTF-8");
        try {
            WriteLog.write("虫洞授权接口", r1 + "--->" + congDongAuthUrl + "--->" + jsonObject.toJSONString());
            result = WormholeShouquan.submitHttpclient(congDongAuthUrl, jsonObject.toJSONString());
            WriteLog.write("虫洞授权接口", r1 + "--->" + result);
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String make_base_auth(String username, String password) {
        String headre = "";
        String tok = username + ":" + password;
        try {
            String tok_1 = Base64Utils.encode(tok.getBytes());
            headre = "Authorization:" + "Basic" + tok_1;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return headre;
    }

    /**
     * 
     * @time 2016年5月19日 下午1:41:14
     * @author chendong
     * @param requestBody 
     * @param requestBody2 
     * @throws IOException 
     * @throws HttpException 
     */
    public static String submitHttpclient(String congDongAuthUrl, String requestBody) throws HttpException, IOException {
        CCSPostMethod post = null;
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        post = new CCSPostMethod(congDongAuthUrl);
        String authorization = userName + ":" + password;
        authorization = "Basic " + new BASE64Encoder().encode(authorization.getBytes());
        post.setRequestBody(requestBody);
        post.addRequestHeader(new Header("Authorization", authorization));
        post.addRequestHeader("Content-Type", "application/json;charset=UTF-8");
        httpClient.executeMethod(post);
        String dataHtml = post.getResponseBodyAsString();
        return dataHtml;
    }

    /**
     * 获取虫洞账号
     * 
     * @param interfaceNumber
     * @return
     * @time 2016年6月20日 下午1:56:55
     * @author fiend
     */
    private String getAccount(String interfaceNumber) {
        String account = "";
        try {
            String sql = "exec [sp_TrainOrderIsWormhole_select2] @orderid='" + interfaceNumber + "'";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                account = map.get("WormholeAccount").toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return account;

    }
}
