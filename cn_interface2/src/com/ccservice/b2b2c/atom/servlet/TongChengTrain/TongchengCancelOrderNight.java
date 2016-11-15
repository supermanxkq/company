package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.InterfaceTimeRuleUtil;

/**
 * 
 * @author wzc
 * @version 创建时间：2015年8月4日 下午9:14:23
 */
public class TongchengCancelOrderNight {
    public String operate(JSONObject json, int random) {
        JSONObject retobj = new JSONObject();
        //同程订单号
        String orderid = json.containsKey("orderid") ? json.getString("orderid") : "";
        //存在空值
        if (ElongHotelInterfaceUtil.StringIsNull(orderid)) {
            retobj.put("success", false);
            retobj.put("code", "107");
            retobj.put("msg", "业务参数缺失");
            return retobj.toJSONString();
        }
        //表示不再T_TRAINORDERMSG表里，可能在trainorder表里
        boolean isTrainOrder = false;
        //表示曾经在T_TRAINORDERMSG表里，应该在trainorder表里
        boolean isrealTrainorder = false;
        if (InterfaceTimeRuleUtil.isNight()) {
            String sql = "select C_STATE from T_TRAINORDERMSG  with(nolock) where C_KEY='" + orderid + "'";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list != null && list.size() > 0) {
                Map map = (Map) list.get(0);
                long state = Long.valueOf(map.get("C_STATE").toString());
                String updatesql = "";
                if (state == 1) {//未处理
                    updatesql = "update T_TRAINORDERMSG set C_STATE=4  where C_KEY='" + orderid + "'";
                    Server.getInstance().getSystemService().findMapResultBySql(updatesql, null);
                    retobj.put("success", true);
                    retobj.put("code", "600");
                    retobj.put("msg", "夜间单取消成功");
                }
                else if (state == 3) {//已处理
                    retobj.put("success", false);
                    retobj.put("code", "601");
                    retobj.put("msg", "夜间单已经开始下单");
                    isTrainOrder = true;
                    isrealTrainorder = true;
                }
                else if (state == 2) {//处理中
                    retobj.put("success", false);
                    retobj.put("code", "601");
                    retobj.put("msg", "夜间单已经开始下单");
                    isTrainOrder = true;
                    isrealTrainorder = true;
                }
                else if (state == 4) {//已取消
                    retobj.put("success", true);
                    retobj.put("code", "600");
                    retobj.put("msg", "夜间单取消成功");
                }
                else {
                    retobj.put("success", false);
                    retobj.put("code", "999");
                    retobj.put("msg", "其他");
                }
            }
            else {
                isTrainOrder = true;
                retobj.put("success", false);
                retobj.put("code", "602");
                retobj.put("msg", "夜间单订单不存在");
            }
            if (isTrainOrder) {
                String sql1 = "select * from T_TRAINORDER  with(nolock)  where C_QUNARORDERNUMBER ='" + orderid + "'";
                List list1 = Server.getInstance().getSystemService().findMapResultBySql(sql1, null);
                Map map = (Map) list1.get(0);
                long pkid = Long.valueOf(map.get("ID").toString());//得到订单ID
                String sql11 = " exec [sp_T_TRAINORDERACQUIRING_selectbyorderid] @orderid=" + pkid;
                List list11 = Server.getInstance().getSystemService().findMapResultBySql(sql11, null);
                if (list != null && list11.size() > 0) {
                    String sqlfuhe = "exec [sp_trainordercancelnightorder] @interfaceOrderNumber='" + orderid + "'";
                    int num = Server.getInstance().getSystemService().excuteAdvertisementBySql(sqlfuhe);
                    if (num == 1) {
                        retobj.put("success", true);
                        retobj.put("code", "600");
                        retobj.put("msg", "夜间单取消成功");
                    }
                    else {
                        retobj.put("success", false);
                        retobj.put("code", "999");
                        retobj.put("msg", "其他");
                    }
                }
                else if (isrealTrainorder) {
                    retobj.put("success", false);
                    retobj.put("code", "999");
                    retobj.put("msg", "其他");
                }
                else {
                    retobj.put("success", false);
                    retobj.put("code", "602");
                    retobj.put("msg", "夜间单订单不存在");
                }
            }
            else {
                retobj.put("success", false);
                retobj.put("code", "603");
                retobj.put("msg", "非法时间无法取消");
            }
        }
        //TODO 后续逻辑以及文档补充交由王占朝书写
        return retobj.toJSONString();
    }

}
