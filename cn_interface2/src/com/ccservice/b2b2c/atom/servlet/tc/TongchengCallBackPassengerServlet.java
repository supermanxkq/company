/**
 * 
 */
package com.ccservice.b2b2c.atom.servlet.tc;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.servlet.format.json.ValueFilterTCPassengerChangeValue;
import com.tenpay.util.MD5Util;

/**
 * 4.21. 下单账号状况及常旅变更回调推送接口
 * 回调 url 中只有一个名为 backjson 的参数，参数值是完整的 json 字符串（全部小 写）。
示例：backjson= {"partnerid":"test123","reqtime":"20151001093618","sign":"fdsfds32839gdsdfthGGDE2","accounts":[{"accountname":"23aa","accountstatusid":"2","accountstatusname":"可用",
"passengers":
[{"passengersename":"张三","passportseno":"362323230950435300","passporttypeseid":"1","passporttypeseidname":"二代身份证","passengertypeid":"1","passengertypename":"成人票","operationtypeid":"1","operationtypename":"新增","operationtime":"20151001093518"}]
}]
}
正式地址
http://train.17usoft.com/trainOrder/services/accountInfoChange
测试地址
http://61.155.159.8:8081/train/services/accountInfoChange
 * @time 2015年10月14日 下午3:05:53
 * @author chendong
 */
public class TongchengCallBackPassengerServlet extends HttpServlet {
    public static void main(String[] args) {
        TongchengCallBackPassengerServlet tongchengCallBackPassenger = new TongchengCallBackPassengerServlet();
        JSONArray jsonArray = new JSONArray();
        System.out.println(tongchengCallBackPassenger.getTongchengJsonbyObj("", "", jsonArray));
    }

    /**
     * 
     */
    private static final long serialVersionUID = 4566416377891L;

    public String partnerid;

    public String key;

    public String callbackurl;

    @Override
    public void init() throws ServletException {
        super.init();
        this.partnerid = this.getInitParameter("partnerid");
        this.key = this.getInitParameter("key");
        this.callbackurl = this.getInitParameter("callbackurl");
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Long l1 = System.currentTimeMillis();
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        PrintWriter out = null;
        String backjson = req.getParameter("backjson");
        WriteLog.write("TongchengCallBackPassengerServlet", l1 + ":backjson-->" + backjson);
        String tongchengResult = "1";
        if (backjson != null && backjson.length() > 0) {
            backjson = ValueFilterTCPassengerChangeValue.getNewJSONString(backjson);
            JSONObject jsonObject = JSONObject.parseObject(backjson);
            JSONArray jsonArrayaccounts = jsonObject.getJSONArray("accounts");
            String backjsonString = getTongchengJsonbyObj(this.partnerid, this.key, jsonArrayaccounts);
            String paramContent = "backjson=" + backjsonString;

            WriteLog.write("TongchengCallBackPassengerServlet", l1 + ":paramContent:" + paramContent + "-->"
                    + this.callbackurl);
            tongchengResult = SendPostandGet.submitPost(this.callbackurl, paramContent, "utf-8").toString();
            WriteLog.write("TongchengCallBackPassengerServlet", l1 + ":" + tongchengResult);
        }
        else {
            tongchengResult = "0";
        }
        try {
            out = res.getWriter();
        }
        catch (Exception e) {
            e.printStackTrace();
            log("回调", e.fillInStackTrace());
        }
        finally {
            if (out != null) {
                out.print(tongchengResult);
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 获取同程规定的格式的json
     * 
     * @param partnerid
     * @param key
     * @param accountsjsonArray 
     * 
     * accountname  1~32    string  账户名
        accountstatusid 1~8 string  账户状态ID 与名称对应关系: 1:不可用，2:可用
        accountstatusname   1~32    string  账户状态名称
        passengers      Array   旅客列表
        [
            passengersename 1~32    string  乘客姓名
            passportseno    1~32    string  乘客证件号码
            passporttypeseid    1~8 string  证件类型 ID  与名称对应关系: 1:二代身份证，2:一代身份证，C:港澳通行证，G:台湾通 行证，B:护照
            passporttypeseidname 1~128   string  证件类型名称 
            passengertypeid 1~8 string  旅客类型 ID。 与旅客类型名称对应关系： 1:成人票，2:儿童票，3:学生票，4:残军票
            passengertypename   1~32    string  旅客类型名称
            operationtypeid 1~8 string  操作类型 ID 与名称对应关系: 1:新增，2:删除，3:修改
            operationtypename   1~32    string  操作类型名称
            operationtime   1~16    string  操作时间，格式：yyyyMMddHHmmss（非空）例：20151001093518
        ]

     * "accounts": [
    {
      "accountname": "23aa",
      "accountstatusid": "2",
      "accountstatusname": "可用",
      "passengers": [
        {
          "passengersename": "张三",
          "passportseno": "362323230950435300",
          "passporttypeseid": "1",
          "passporttypeseidname": "二代身份证",
          "passengertypeid": "1",
          "passengertypename": "成人票",
          "operationtypeid": "1",
          "operationtypename": "新增",
          "operationtime": "20151001093518"
        }
      ]
    }
    ]
     * @return
     * @time 2015年10月14日 下午3:18:29
     * @author chendong
     */
    private String getTongchengJsonbyObj(String partnerid, String key, JSONArray accountsjsonArray) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("partnerid", partnerid);
        String reqtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "";
        jsonObject.put("reqtime", reqtime);
        //        =md5(partnerid+reqtime+md5(key))，
        String sign = partnerid + reqtime + MD5Util.MD5Encode(key, "utf-8");
        sign = MD5Util.MD5Encode(sign, "utf-8");
        jsonObject.put("sign", sign);

        jsonObject.put("accounts", accountsjsonArray);
        return jsonObject.toJSONString();
    }
}
