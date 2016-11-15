package com.ccservice.b2b2c.atom.service12306.offlineRefund.ali;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.ben.Trainform;
import java.util.concurrent.ExecutorService;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.uniontrade.Uniontrade;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.thread.AliPushRefundPriceThread;

/**
 * 支付宝退款推送
 * @author WH
 * @time 2015年7月6日 下午6:27:59
 * @version 1.0
 */

public class AliRefundPushMethod {

    /**
     * @param reqdata 请求数据
     */
    public String operate(String reqdata) {
        //随机数
        int random = new Random().nextInt(900000) + 100000;
        //解析
        JSONObject aliObj = new JSONObject();
        try {
            aliObj = JSONObject.parseObject(reqdata);
        }
        catch (Exception e) {
            System.out.println(ElongHotelInterfaceUtil.getCurrentTime() + " " + random + " ---> " + e.getMessage());
        }
        if (aliObj == null) {
            aliObj = new JSONObject();
        }
        //结果
        JSONObject retobj = new JSONObject();
        retobj.put("success", false);
        //数据
        String busType = "";
        String allTradeNums = "";
        long orderOrChangeId = 0;
        Uniontrade unionTrade = new Uniontrade();
        //新逻辑
        if (aliObj.containsKey("refundInfo")) {
            String UID = aliObj.getString("UID");
            busType = aliObj.getString("bussType");
            orderOrChangeId = aliObj.getLongValue("orderId");
            JSONArray WNumbers = aliObj.getJSONArray("WNumbers");
            JSONArray refundInfo = aliObj.getJSONArray("refundInfo");
            JSONArray payTradeNos = aliObj.getJSONArray("payTradeNos");
            //业务参数错误
            if (orderOrChangeId <= 0 || (!"1".equals(busType) && !"2".equals(busType))) {
                retobj.put("result", "业务参数错误");
                return retobj.toString();
            }
            //业务参数缺失
            if (ElongHotelInterfaceUtil.StringIsNull(UID) && refundInfo == null || refundInfo.size() == 0
                    || payTradeNos == null || WNumbers == null || WNumbers.size() == 0) {
                retobj.put("result", "业务参数缺失");
                return retobj.toString();
            }
            //所有支付流水
            for (int i = 0; i < payTradeNos.size(); i++) {
                String payTradeNo = payTradeNos.getString(i);
                if (!ElongHotelInterfaceUtil.StringIsNull(payTradeNo)) {
                    allTradeNums += payTradeNo + "@";
                }
            }
            if (allTradeNums.endsWith("@")) {
                allTradeNums = allTradeNums.substring(0, allTradeNums.length() - 1);
            }
            //流水号为空，以W号判断获取
            if (ElongHotelInterfaceUtil.StringIsNull(allTradeNums)) {
                //重置
                allTradeNums = "";
                //所有W号
                for (int i = 0; i < WNumbers.size(); i++) {
                    //W号
                    String WNumber = WNumbers.getString(i);
                    //循环存退款数据
                    for (int j = 0; j < refundInfo.size(); j++) {
                        //JSON
                        JSONObject refundDetail = refundInfo.getJSONObject(j);
                        //判断W号
                        if (WNumber.equals(refundDetail.getString("Wnumber"))) {
                            //拼接
                            allTradeNums += refundDetail.getString("AliTradeNo") + "@";
                            //中断循环
                            break;
                        }
                    }
                }
                allTradeNums = allTradeNums.substring(0, allTradeNums.length() - 1);
            }
            //循环存退款数据
            for (int i = 0; i < refundInfo.size(); i++) {
                //JSON
                JSONObject refundDetail = refundInfo.getJSONObject(i);
                //保存数据
                Uniontrade temp = SaveRefundData(refundDetail, allTradeNums);
                //当前主推
                if (UID.equals(refundDetail.getString("UID"))) {
                    unionTrade = temp;
                }
            }
        }
        //老逻辑
        else {
            allTradeNums = aliObj.getString("AliTradeNo");
            //保存数据
            unionTrade = SaveRefundData(aliObj, allTradeNums);
        }
        unionTrade = unionTrade == null ? new Uniontrade() : unionTrade;
        //ID
        long unionId = unionTrade.getId() == null ? 0 : unionTrade.getId();
        //成功保存退款记录
        if (unionId > 0 && unionTrade.getAmount() > 0 && unionTrade.getBusstype() == 2) {
            //成功
            retobj.put("result", "保存数据成功");
            //日志
            WriteLog.write("h火车票支付宝退款", random + " ---> 请求数据: " + reqdata);
            //异步
            if (!isNight()) {
                ExecutorService pool = Executors.newFixedThreadPool(1);
                pool.execute(new AliPushRefundPriceThread(busType, allTradeNums, orderOrChangeId, random, 0, 0, false));
                pool.shutdown();
            }
        }
        else {
            if (unionId == -1) {
                retobj.put("result", "业务参数缺失");
            }
            else if (unionId == -2) {
                retobj.put("result", "退款记录已存在");
            }
            else if (unionId > 0) {
                retobj.put("result", "退款记录已保存，后续不走");
            }
            else {
                retobj.put("result", "保存退款记录失败");
            }
        }
        //标识
        retobj.put("success", unionId == -2 || unionId > 0);
        //Return
        return retobj.toString();
    }

    /**
     * 保存支付宝退款
     {
       "Liushuihao": "96706260612891",
       "AliTradeNo": "2015021021001004990056391808",
       "Wnumber": "W2015012903672719",
       "AliPayUserName": "hthy012@qq.com",
       "RefundAmount": ,
       "PayMoney": 112,
       "Banlance": 4519.4,
       "TimeTrade": "2015-01-30 15:47:01"
     }
     */
    @SuppressWarnings("unchecked")
    private Uniontrade SaveRefundData(JSONObject refundDetail, String payTradeNos) {
        Uniontrade trade = new Uniontrade();
        try {
            String Wnumber = refundDetail.getString("Wnumber");
            String TimeTrade = refundDetail.getString("TimeTrade");
            float PayMoney = refundDetail.getFloatValue("PayMoney");
            float Banlance = refundDetail.getFloatValue("Banlance");
            String AliTradeNo = refundDetail.getString("AliTradeNo");
            String Liushuihao = refundDetail.getString("Liushuihao");
            float RefundAmount = refundDetail.getFloatValue("RefundAmount");
            String AliPayUserName = refundDetail.getString("AliPayUserName");
            if (ElongHotelInterfaceUtil.StringIsNull(AliTradeNo) || ElongHotelInterfaceUtil.StringIsNull(TimeTrade)) {
                trade.setId(-1l);
                throw new Exception("业务参数缺失");
            }
            if (RefundAmount > 0 || PayMoney < 0) {

            }
            else {
                trade.setId(-1l);
                throw new Exception("业务参数错误");
            }
            //唯一KEY
            String key = ElongHotelInterfaceUtil.MD5(AliTradeNo + TimeTrade + Banlance + RefundAmount);
            //查询现有数据
            String sql = "SELECT TOP 1 * FROM T_UNIONTRADE with(nolock) WHERE C_KEY = '" + key + "'";
            List<Uniontrade> tradeList = Server.getInstance().getSystemService().findAllUniontradeBySql(sql, -1, 0);
            //现有数据存在
            if (tradeList != null && tradeList.size() > 0) {
                trade = tradeList.get(0);
                trade.setId(-2l);//表示后续不再走
            }
            else {
                trade.setKey(key);
                trade.setBankTransNo("");
                trade.setBalance(Banlance);
                trade.setPayTradeNos(payTradeNos);
                //支付
                if (PayMoney < 0) {
                    trade.setAmount(PayMoney);
                    trade.setBusstype(1l);
                    Trainform trainform = new Trainform();
                    trainform.setSupplytradeno(AliTradeNo);
                    List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
                    if (orders != null && orders.size() == 1) {
                        Trainorder order = orders.get(0);
                        trade.setOrderid(order.getId());
                        trade.setOrdernumber(order.getOrdernumber());
                    }
                }
                //退票
                else {
                    trade.setAmount(RefundAmount);
                    trade.setBusstype(2l);
                }
                trade.setTrandnum(AliTradeNo);
                trade.setLiushiuhao(Liushuihao);
                trade.setWnumber(Wnumber);
                trade.setPayname(AliPayUserName);
                trade.setOrdertime(new Timestamp(TimeUtil.parseStringtimeToDate(TimeTrade, 4).getTime()));
                //保存数据
                trade = Server.getInstance().getSystemService().createUniontrade(trade);
            }
        }
        catch (Exception e) {
        }
        return trade;
    }

    /**
     * 判断当前时间
     */
    private boolean isNight() {
        //晚上
        boolean isNight = false;
        //捕捉异常
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            //当前
            Date current = sdf.parse(sdf.format(new Date()));
            //00:00 - 06:00
            Date start = sdf.parse("23:55");
            Date end = sdf.parse("06:05");
            //判断
            if (current.after(start) || current.before(end)) {
                isNight = true;
            }
        }
        catch (Exception e) {
            System.out.println(ElongHotelInterfaceUtil.errormsg(e));
        }
        //返回结果
        return isNight;
    }

}