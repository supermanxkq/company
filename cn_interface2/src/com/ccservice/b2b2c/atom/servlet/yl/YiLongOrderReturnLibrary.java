package com.ccservice.b2b2c.atom.servlet.yl;

import java.util.List;

import javax.servlet.http.HttpServlet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.util.db.DBHelper;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;

public class YiLongOrderReturnLibrary extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static int jf = -1;

    /**
     * 艺龙3.6订单返库
     * 2015年12月15日 15:20:36
     * yangtao
    代理商code  merchantCode    必填  由艺龙分配   
    订单号 orderIds    必填  订单号，可传一个，也可传多个用逗号分隔 直接传递如下数据，可以多个:111,233,333,444,556
    签名  sign    必填  数据签名    参照签名机制
    用途：
    1.12306挂掉，已经推送到供应商数据库的订单无法出票，用户也无法取消。
    在这种情况下，供应商调用订单返库接口，把这样的订单返回。
    给用户一个选择的余地，用户可以取消，也可以耐心等待至12306恢复，重新推送给供应商继续出票。
    注：此接口会把出票中的订单状态变为支付成功，出票表的状态从已经推送至供应商变为尚未推送。
    12306恢复后，开启推单批处理，重新推送订单时，订单状态将从支付成功->出票中，出票表的状态会变成推送至供应商。
     * */
    public String returnOrder(String result, JSONArray json, Integer state12306) {
        //substring(字符串.lastIndexOf("\\")+1)
        int count = 1;
        String sql = "";
        String orderIds = "";
        JSONObject jsonObject = null;
        // int zifu = Return(captureString(json));
        int zifu = Return(captureString(jsonReturn()));
        //  orderIds = captureString(json);
        orderIds = captureString(jsonReturn());
        String menTest = orderIds.replaceAll("\\,\\w*$", "");
        if (zifu > 1) {
            sql = "update T_TRAINORDER  set C_STATE12306=6 where C_ORDERNUMBER in(" + menTest + ") and C_ORDERSTATUS=2";
        }
        else if (zifu == 1) {
            sql = "update T_TRAINORDER  set C_STATE12306=6 where C_ORDERNUMBER =" + menTest + " and C_ORDERSTATUS=2";
        }
        else {
            System.out.println("获取值失败");
        }
        System.out.println(sql);
        for (int i = 0; i < count; i++) {
            if ("success".toUpperCase().equals(result.toUpperCase())) {
                if (zifu > 1) {
                    sql = "update T_TRAINORDER  set C_ORDERSTATUS=2 where C_ORDERNUMBER in(" + menTest
                            + ") and C_STATE12306=6";
                }
                else if (zifu == 1) {
                    sql = "update T_TRAINORDER  set C_ORDERSTATUS=2 where C_ORDERNUMBER =" + menTest
                            + " and C_STATE12306=6";
                }
                jsonObject = new JSONObject();
                jsonObject.put("C_ORDERNUMBER", menTest);
                jsonObject.put("retCode", "200");
                jsonObject.put("retDesc", "成功");

            }//mei
            else {
                if (i > 1000) {
                    count = 0;
                    System.out.println("请求过多：" + count);
                }

                count++;
            }
        }

        return jsonObject.toJSONString();
    }

    /***
    yangtao
    * 2015年12月15日 16:51:08
    * 取出JsonArray 用逗号分隔 每个值
    * */
    public static String captureString(JSONArray jsonArray) {
        JSONArray jsonArrays = jsonArray;
        JSONArray jsonAr = new JSONArray();
        int resultReturn = -1;
        String str = "";
        String strs = "";
        resultReturn = jsonArrays.size();
        for (int i = 0; i < jsonArrays.size(); i++) {
            JSONObject jsonOb = (JSONObject) jsonArrays.get(i);
            String id = (String) jsonOb.get("id");
            if (id.equals("")) {

            }
            else {

                jsonAr.add(id);
            }
        }
        /************************************************************************/
        for (int i = 0; i < jsonAr.size(); i++) {
            strs = "'" + jsonAr.get(0) + "'";
            strs += ",";
            str += strs;
        }
        String spt = str = str.substring(0, str.length() - 1);
        System.out.println("截取:" + spt);
        return spt + "," + resultReturn;
    }

    /**
     * 获取逗号最后一个值进行判断
     * 2015年12月16日 14:52:33
     * */
    public static int Return(String result) {
        jf = Integer.valueOf(result.substring(result.lastIndexOf(",") + 1));

        return jf;
    }

    /***
    yangtao
    * 2015年12月15日 16:51:08
    * 取出JsonArray 用逗号分隔 每个值
    * */
    public static String captureList(List list) {
        JSONArray jsonArrays = (JSONArray) list;
        JSONArray jsonAr = new JSONArray();
        String str = "";
        String strs = "";

        for (int i = 0; i < jsonArrays.size(); i++) {
            JSONObject jsonOb = (JSONObject) jsonArrays.get(i);
            String id = (String) jsonOb.get("id");
            if (id.equals("")) {

            }
            else {

                jsonAr.add(id);
            }
        }
        /************************************************************************/
        for (int i = 0; i < jsonAr.size(); i++) {
            strs = "'" + jsonAr.get(0) + "'";
            strs += ",";
            str += strs;
        }
        String spt = str = str.substring(0, str.length() - 1);
        System.out.println("截取:" + spt);
        return spt;
    }

    /*    *//**
             * 获取逗号最后一个值进行判断
             * 2015年12月16日 14:52:33
             * */
    /*
    public static int Return(String result) {
    jf = Integer.valueOf(result.substring(result.lastIndexOf(",") + 1));

    return jf;
    }*/

    public static void main(String[] args) {

        YiLongOrderReturnLibrary li = new YiLongOrderReturnLibrary();
        //success
        String result = "SUCCESS";
        JSONArray json = null;
        Integer state12306 = 0;
        li.returnOrder(result, json, state12306);

        /*   String jf = result.substring(result.lastIndexOf(",") + 1);
           if (jf.equals("ni")) {
               System.out.println(jf);
               System.out.println("ok");
           }
           else {
               System.out.println("no");
           }*/
    }

    /**
     * 测试
     * */
    public static JSONArray jsonReturn() {
        JSONArray jsonArray = null;
        String str = "";
        String sql = "select top 1 ID from T_TRAINORDER";
        DataTable myDt = DBHelper.GetDataTable(sql);
        List<DataRow> dataRows = myDt.GetRow();
        if (dataRows.size() > 0) {
            jsonArray = new JSONArray();
            for (int i = 0; i < dataRows.size(); i++) {
                DataRow jst = dataRows.get(i);
                String id = jst.GetColumnString("ID");
                if (id.equals("")) {

                }
                else {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", id);
                    jsonArray.add(jsonObject);
                    //  System.out.println(id);
                }
            }
        }
        return jsonArray;
    }
}
