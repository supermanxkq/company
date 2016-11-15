package com.ccservice.b2b2c.atom.servlet.listener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet2;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengConfirmChange;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 淘宝改签支付消费
 * 
 * @time 2015年4月23日 上午11:12:18
 * @author Administrator
 */
public class TaoBaoTrainOrderChangeGetMQMSGListener implements MessageListener {
    Logger logger = Logger.getLogger("淘宝改签消费队列");

    @Override
    public void onMessage(Message message) {
        int r1 = new Random().nextInt(10000000);
        try {
            String trainOrderChangeResult = ((TextMessage) message).getText();
            WriteLog.write("101_TAOBAO_CHANGE_LISTENER", r1 + ":进入改签出票队列messagetext--->" + trainOrderChangeResult);

            JSONObject jsonObject = JSONObject.parseObject(trainOrderChangeResult);
            String jsontypes;
            String confirmchangeurl = PropertyUtil.getValue("Taobao_ConfirmChangeUrl", "Train.properties");
            jsonObject.put("isneedtradeno", true);
            if (confirmchangeurl != null && !"".equals(confirmchangeurl)) {
                String jsonObjectString = jsonObject.toJSONString();
                try {
                    jsonObjectString = URLEncoder.encode(jsonObjectString, "UTF-8");
                }
                catch (UnsupportedEncodingException e1) {
                    WriteLog.write("101_TAOBAO_CHANGE_LISTENER_ERROR", r1 + ":异常");
                    ExceptionUtil.writelogByException("101_TAOBAO_CHANGE_LISTENER_ERROR", e1);
                }
                jsontypes = SendPostandGet2.doGet(confirmchangeurl + "?jsonStr=" + jsonObjectString + "&random=" + r1,
                        "UTF-8");
            }
            else {
                TongChengConfirmChange tccfc = new TongChengConfirmChange();
                jsontypes = tccfc.operate(jsonObject, r1);
            }
            JSONObject jsons = JSONObject.parseObject(jsontypes);
            String extent = "";
            WriteLog.write("101_TAOBAO_CHANGE_LISTENER", r1 + ":进入改签出票队列出票状态--->" + jsons.toString());

            if (jsons.getBooleanValue("success") == true) //回调成功
            {
            }
            else {
                WriteLog.write("101_TAOBAO_CHANGE_LISTENER", r1 + ":进入改签出票队列回调淘宝失败状态--->" + jsons.toString());
                //                String Taobao_TrainCallBack = getSysconfigString("Taobao_MealCallBack");

                String orderid = jsonObject.containsKey("orderid") ? jsonObject.getString("orderid") : "";
                long trainorderid = getOrderIdByJiekouNum(orderid);
                long changeorderid = jsonObject.containsKey("changeorderid") ? jsonObject.getLongValue("changeorderid")
                        : 0l;
                Trainorder order = Server.getInstance().getTrainService().findTrainorder(trainorderid);
                Trainorderchange trainChange = Server.getInstance().getTrainService()
                        .findTrainorcerchange(changeorderid);
                JSONObject falseJsonObject = new JSONObject();
                falseJsonObject.put("orderidme", order.getId());
                falseJsonObject.put("mainbizorderid", order.getQunarOrdernumber());
                falseJsonObject.put("applyid", trainChange.getTaobaoapplyid());
                falseJsonObject.put("errorcode", TaobaoHotelInterfaceUtil.msg2TaoBaoError(ElongHotelInterfaceUtil
                        .getJsonString(jsonObject, "msg")));

                String Taobao_TrainCallBack = PropertyUtil.getValue("Taobao_MealCallBack", "Train.properties");
                if (Taobao_TrainCallBack != null && !"".equals(Taobao_TrainCallBack)) {
                    String jsonString = falseJsonObject.toString();
                    try {
                        jsonString = URLEncoder.encode(jsonString, "UTF-8");
                    }
                    catch (UnsupportedEncodingException e) {
                        WriteLog.write("101_TAOBAO_CHANGE_LISTENER_ERROR", r1 + ":异常");
                        ExceptionUtil.writelogByException("101_TAOBAO_CHANGE_LISTENER_ERROR", e);
                    }
                    extent = SendPostandGet2.doGet(Taobao_TrainCallBack + "?json=" + jsonString + "&statue=2", "UTF-8");
                }
                else {
                    TaobaoHotelInterfaceUtil thi = new TaobaoHotelInterfaceUtil();
                    extent = thi.CommitChangOrderageOver(falseJsonObject);
                }

            }

        }
        catch (JMSException e) {
            WriteLog.write("101_TAOBAO_CHANGE_LISTENER_ERROR", r1 + ":异常");
            ExceptionUtil.writelogByException("101_TAOBAO_CHANGE_LISTENER_ERROR", e);
        }
    }

    public String getSysconfigString(String name) {
        String result = "-1";
        try {
            if (Server.getInstance().getDateHashMap().get(name) == null) {
                List<Sysconfig> sysoconfigs = Server.getInstance().getSystemService()
                        .findAllSysconfig("WHERE C_NAME='" + name + "'", "", -1, 0);
                if (sysoconfigs.size() > 0) {
                    result = sysoconfigs.get(0).getValue();
                    Server.getInstance().getDateHashMap().put(name, result);
                }
            }
            else {
                result = Server.getInstance().getDateHashMap().get(name);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 通过接口订单号查询订单ID  如果查询不到就返回0
     * @param jiekounumber
     * @return
     * @author wangchengliang
     */
    private long getOrderIdByJiekouNum(String jiekounumber) {
        long trainorderid = 0;
        try {
            String sql = "SELECT top 1 ID FROM T_TRAINORDER WITH (NOLOCK) WHERE C_QUNARORDERNUMBER='" + jiekounumber
                    + "'";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() == 1) {
                Map map = (Map) list.get(0);
                trainorderid = Long.valueOf(map.get("ID").toString());
            }
        }
        catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return trainorderid;
    }

}
