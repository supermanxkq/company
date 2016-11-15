package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.ben.Trainform;

/**
 * 查询订单状态
 * @author 路平
 *创建时间 2014年12月9日 下午5:31:08
 */
public class TongChengTrainQueryStatus {

    public static String trainorderstatus(JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        Trainform trainform = new Trainform();
        List<Trainorder> trainorder = new ArrayList<Trainorder>();
        String orderid = jsonObject.getString("orderid");
        boolean success = false;
        String Orderstatus = "";
        String msg = "查询订单成功";
        String description = "";
        String code = "100";
        try {

            if (orderid != null && !"".equals(orderid)) {
                trainform.setQunarordernumber(orderid);
                trainorder = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
                if (trainorder.size() == 0) {
                    code = "402";
                    success = false;
                    msg = "订单不存在";
                    Orderstatus = "402";
                    description = "订单不存在";
                }
                else {
                    Orderstatus = "1";
                    description = trainorder.get(0).getOrderstatusstr();
                    success = true;
                }
            }
            result.put("success", success);
            result.put("code", code);
            result.put("orderid", orderid);
            result.put("status", Orderstatus);
            result.put("description", description);
            result.put("msg", msg);
        }
        catch (Exception e) {
            result.put("success", false);
            result.put("status", Orderstatus);
            result.put("code", 601);
            result.put("description", description);
            result.put("msg", "查询订单状态失败");
            e.printStackTrace();
        }
        return result.toString();

    }
}
