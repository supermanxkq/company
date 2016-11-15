package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.util.db.DBHelper;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;
import com.ccservice.elong.inter.PropertyUtil;

@SuppressWarnings("serial")
public class TuniuCancelServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=utf-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        JSONObject jsonObject = new JSONObject();
        try {
            out = res.getWriter();
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer buf = new StringBuffer(1024);
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }
            String param = buf.toString();
            WriteLog.write("途牛抢票_实时取消", "请求参数:" + param);
            if (!ElongHotelInterfaceUtil.StringIsNull(param)) {
                JSONObject json = JSONObject.parseObject(param);
                if (StringIsNull(json)) {
                    JSONObject signs = JSONObject.parseObject(param);
                    signs.put("sign", "");
                    String sign = SignUtil.generateSign(signs.toString(), getKey(json.getString("account")));
                    if (json.getString("sign").equals(sign)) {
                        String Url = PropertyUtil.getValue("TuniuCancelServlet_url", "Train.properties");
                        String Prm = "Data=" + TuNiuDesUtil.decrypt(json.getString("data")) + "&Account="
                                + json.getString("account") + "&Key=" + getKey(json.getString("account"));
                        for (int i = 0; i <= 5; i++) {
                            String ret = SendPostandGet.submitPost(Url, Prm, "UTF-8").toString();
                            if (ret.equalsIgnoreCase("success")) {
                                jsonObject.put("success", true);
                                jsonObject.put("returnCode", "301");
                                jsonObject.put("errorMsg", "");
                                jsonObject.put("data", "");
                                break;
                            }
                            else {
                                jsonObject.put("success", false);
                                jsonObject.put("returnCode", "231008");
                                jsonObject.put("errorMsg", "param error");
                                jsonObject.put("data", "");
                            }
                        }
                    }
                    else {
                        jsonObject.put("success", false);
                        jsonObject.put("returnCode", "231007");
                        jsonObject.put("errorMsg", "signature error");
                        jsonObject.put("data", "");
                    }
                }
                else {
                    jsonObject.put("success", false);
                    jsonObject.put("returnCode", "231008");
                    jsonObject.put("errorMsg", "param error");
                    jsonObject.put("data", "");
                }
            }
            else {
                jsonObject.put("success", false);
                jsonObject.put("returnCode", "231008");
                jsonObject.put("errorMsg", "param error");
                jsonObject.put("data", "");
            }
        }
        catch (Exception e) {
            jsonObject.put("success", false);
            jsonObject.put("returnCode", "231099");
            jsonObject.put("errorMsg", "unknown error");
            jsonObject.put("data", "");
        }
        finally {
            WriteLog.write("途牛抢票_实时取消", "回调参数:" + jsonObject.toString());
            out.write(jsonObject.toString());
            out.flush();
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    /**
     * 验证参数
     **/
    public boolean StringIsNull(JSONObject json) {
        try {
            if (json.containsKey("account") || json.containsKey("timestamp") || json.containsKey("sign")
                    || json.containsKey("data")) {
                return true;
            }
        }
        catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 根据用户名获取到这个用户的key
     **/
    private String getKey(String loginname) {
        String Key = "";
        try {
            String sql = "SELECT * FROM T_INTERFACEACCOUNT WHERE C_USERNAME='" + loginname + "'";
            DataTable datatable = DBHelper.GetDataTable(sql);
            List<DataRow> dataRows = datatable.GetRow();
            for (DataRow dataRow : dataRows) {
                Key = dataRow.GetColumnString("C_KEY");
            }
        }
        catch (Exception e) {
            Key = "";
        }
        return Key;
    }
}
