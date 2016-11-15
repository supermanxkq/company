package com.ccservice.b2b2c.atom.pay.gp;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.billpay.pki.Pkipair;
import com.callback.PropertyUtil;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.PayEntryInfo;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.traderecord.Traderecord;

/**
 * GP机票接口
 * @Description: 快钱人民币支付网关接口
 * @Copyright (c) 上海快钱信息服务有限公司
 * @version 2.0
 */
/**
 * Servlet implementation class for Servlet: Billpay
 */
public class BillpayWithHoldMoney extends AirSupper {

    public static void main(String[] args) throws Exception {
        Random r = new Random();
        JSONObject obj = new JSONObject();
        for (int i = 0; i < 1; i++) {
            String msg = new BillpayWithHoldMoney("").pay(obj, 984.6d, "", 0, 0);
        }
    }

    private Orderinfo orderinfo = null;

    /**
     * 
     * @param orderid
     * @param OrderNumber
     */
    public BillpayWithHoldMoney(String OrderNumber) {
        String sql = "select Id from T_ORDERINFO with(nolock) WHERE C_ORDERNUMBER='" + OrderNumber + "'";
        List<Map> list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        long orderid = 0;
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            orderid = Long.parseLong(map.get("Id").toString());
        }
        orderinfo = Server.getInstance().getAirService().findOrderinfo(orderid);
    }

    /**
     * 
     * @param ordernumber
     * @param money 保留两位小数
     * @param goodsDesc
     * @param agentid 供应商Agentid
     * @return
     * @throws Exception
     */
    public String pay(JSONObject msgobj, double money, String goodsDesc, double ServMoney, int rand) throws Exception {
        if (orderinfo == null) {
            return "未查询到订单";
        }
        if (orderinfo.getPolicyagentid() == null || orderinfo.getPolicyagentid() <= 0) {
            return "无效订单供应商";
        }
        String ordernumber = orderinfo.getOrdernumber();
        long orderid = orderinfo.getId();
        PayEntryInfo info = findAgentInfo(orderinfo.getPolicyagentid().longValue() + "", 1);
        String merchantId = "";
        if (info == null) {
            return "快钱账户缺失";
        }
        else {
            merchantId = info.getPid();
        }
        // 字符集.固定选择值。可为空。
        // /只能选择1、2、3.
        // /1代表UTF-8; 2代表GBK; 3代表gb2312
        // /默认值为1
        String inputCharset = "1";
        //网关接口地址
        String subway = "https://www.99bill.com/msgateway/recvMsgatewayMerchantInfoAction.htm?";//分账网关  
        //        String subway = "https://sandbox.99bill.com/msgateway/recvMsgatewayMerchantInfoAction.htm?";//分账网关    
        //服务器接受支付结果的后台地址.与[pageUrl]不能同时为空。必须是绝对地址,快钱通过服务器连接的方式将交易结果发送到[bgUrl]对应的页面地址，在商户处理完成后输出的<result>为1，页面会转向到<redirecturl>对应的地址。
        String bgUrl = "";
        // 网关版本.固定值///快钱会根据版本号来调用对应的接口处理程序。///本代码版本号固定为v2.0
        String version = "v2.0";

        // 语言种类.固定选择值。///只能选择1、2、3///1代表中文；2代表英文///默认值为1
        String language = "1";

        // 签名类型.固定值///1代表MD5签名///当前版本固定为1
        String signType = "4";

        //主收款方联系方式类型，固定值 1代表Email
        String payeeContactType = "1";
        //主收款方联系方式，当payeeContactType=1 时输入Email 地址 
        String payeeContact = info.getSellemail();

        // 商户订单号 由字母、数字、或[-][_]组成
        String orderId = ordernumber;
        // 订单金额 以分为单位，必须是整型数字
        // /比方2，代表0.02元
        String orderAmount = "" + (int) (money * 100);

        String moneyShare = getRoyalty_parameters(orderid, ordernumber, orderinfo, ServMoney, money);
        WriteLog.write("GpPay代扣款", rand + ":ordernumber:" + orderinfo.getOrdernumber() + ":分润参数：" + moneyShare);
        String[] aryinfo = moneyShare.split(",");

        if (aryinfo[0].equals("0")) {
            return "支付参数不合法";
        }
        //主收款方应收额,以分为单位，必须是整型数字
        String payeeAmount = aryinfo[2];
        // 订单提交时间
        // /14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]如；20080101010101
        String orderTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        // 商品名称 可为中文或英文字符
        String productName = goodsDesc;
        // 商品数量 可为空，非空时必须为数字
        String productNum = "1";

        // 扩展字段1 在支付结束后原样返回给商户
        String ext1 = orderinfo.getPolicyagentid().longValue() + "";
        // 扩展字段2 在支付结束后原样返回给商户
        String ext2 = ordernumber + "FgGpAirWithHoldHandle";

        // 支付方式.固定选择值 只能选择00、10、11、12、13、14
        // /00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）.14：B2B支付（网关支付页面只显示B2B支付，但需要向快钱申请开通才能使用）
        String payType = "00";//默认银行卡支付
        //提交此分账请求的合作方在快钱的用户编号。
        //分账数据，可为空
        String sharingData = aryinfo[1];
        //        String sharingData = "1^2829679462@qq.com^100^0^test";
        //分账标志 ，0 代表异步分账，即不立即把款项分配给相关账户；1 代表支付成功立刻分账
        String sharingPayFlag = "1";
        // 支付人联系方式类型.固定选择值///只能选择1///1代表Email
        bgUrl = PropertyUtil.getValue("pageUrl", "GpAir.properties");
        // 生成加密签名串 请务必按照如下顺序和规则组成加密串！
        StringBuilder signMsgVal = new StringBuilder("");
        signMsgVal = appendParam(signMsgVal, "inputCharset", inputCharset);
        signMsgVal = appendParam(signMsgVal, "bgUrl", bgUrl);
        signMsgVal = appendParam(signMsgVal, "version", version);
        signMsgVal = appendParam(signMsgVal, "language", language);
        signMsgVal = appendParam(signMsgVal, "signType", signType);
        signMsgVal = appendParam(signMsgVal, "payeeContactType", payeeContactType);
        signMsgVal = appendParam(signMsgVal, "payeeContact", payeeContact);
        signMsgVal = appendParam(signMsgVal, "orderId", orderId);
        signMsgVal = appendParam(signMsgVal, "orderAmount", orderAmount);
        signMsgVal = appendParam(signMsgVal, "payeeAmount", payeeAmount);
        signMsgVal = appendParam(signMsgVal, "orderTime", orderTime);
        signMsgVal = appendParam(signMsgVal, "productName", productName);
        signMsgVal = appendParam(signMsgVal, "productNum", productNum);
        signMsgVal = appendParam(signMsgVal, "ext1", ext1);
        signMsgVal = appendParam(signMsgVal, "ext2", ext2);
        signMsgVal = appendParam(signMsgVal, "payType", payType);
        signMsgVal = appendParam(signMsgVal, "pid", merchantId);
        signMsgVal = appendParam(signMsgVal, "sharingData", sharingData);
        signMsgVal = appendParam(signMsgVal, "sharingPayFlag", sharingPayFlag);

        Pkipair pki = new Pkipair();
        String signMsg = pki.signMsg(signMsgVal.toString().trim(), orderinfo.getPolicyagentid().longValue() + "");
        // post参数
        String signMsgValstr = "";
        signMsgValstr = appendParam(signMsgValstr, "inputCharset", inputCharset);
        signMsgValstr = appendParam(signMsgValstr, "bgUrl", bgUrl);
        signMsgValstr = appendParam(signMsgValstr, "version", version);
        signMsgValstr = appendParam(signMsgValstr, "language", language);
        signMsgValstr = appendParam(signMsgValstr, "signType", signType);
        signMsgValstr = appendParam(signMsgValstr, "signMsg", java.net.URLEncoder.encode(signMsg, "UTF-8"));
        signMsgValstr = appendParam(signMsgValstr, "payeeContactType", payeeContactType);
        signMsgValstr = appendParam(signMsgValstr, "payeeContact", payeeContact);
        signMsgValstr = appendParam(signMsgValstr, "orderId", orderId);
        signMsgValstr = appendParam(signMsgValstr, "orderAmount", orderAmount);
        signMsgValstr = appendParam(signMsgValstr, "payeeAmount", payeeAmount);
        signMsgValstr = appendParam(signMsgValstr, "orderTime", orderTime);
        signMsgValstr = appendParam(signMsgValstr, "productName", productName);
        signMsgValstr = appendParam(signMsgValstr, "productNum", productNum);
        signMsgValstr = appendParam(signMsgValstr, "ext1", ext1);
        signMsgValstr = appendParam(signMsgValstr, "ext2", ext2);
        signMsgValstr = appendParam(signMsgValstr, "payType", payType);
        signMsgValstr = appendParam(signMsgValstr, "pid", merchantId);
        signMsgValstr = appendParam(signMsgValstr, "sharingData", sharingData);
        signMsgValstr = appendParam(signMsgValstr, "sharingPayFlag", sharingPayFlag);

        String parameter = subway + signMsgValstr;
        // 写入支付记录
        Traderecord traderecord = new Traderecord();
        traderecord.setCode(orderTime);
        traderecord.setCreatetime(new Timestamp(System.currentTimeMillis()));
        traderecord.setCreateuser("Billpay");
        traderecord.setDescription(moneyShare);
        traderecord.setGoodsdesc(goodsDesc);
        traderecord.setGoodsname(productName);
        traderecord.setModifytime(new Timestamp(System.currentTimeMillis()));
        traderecord.setModifyuser("");
        traderecord.setOrdercode(ordernumber);
        traderecord.setPayname("快钱");
        traderecord.setPaytype(2);// 0支付宝 1财付通 2 快钱
        traderecord.setRetcode("");
        traderecord.setState(0);// 0等待支付1支付成功2支付失败
        traderecord.setTotalfee((int) Double.parseDouble(orderAmount));// 支付金额分为单位
        traderecord.setType(1);// 订单类型
        traderecord.setPaymothed("99bill");// 支付方式
        traderecord.setBankcode("");// 支付银行
        try {
            traderecord = Server.getInstance().getMemberService().createTraderecord(traderecord);
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("交易失败！");
            e.printStackTrace();
            return "交易失败";
        }
        System.out.println(parameter);
        return parameter;
    }

    //功能函数。将变量值不为空的参数组成字符串
    public static String appendParam(String returnStr, String paramId, String paramValue) {
        if (!returnStr.equals("")) {
            if (!paramValue.equals("")) {
                returnStr = returnStr + "&" + paramId + "=" + paramValue;
            }
        }
        else {
            if (!paramValue.equals("")) {
                returnStr = paramId + "=" + paramValue;
            }
        }
        return returnStr;
    }

    //功能函数。将变量值不为空的参数组成字符串。结束
    // 功能函数。将变量值不为空的参数组成字符串
    public static StringBuilder appendParam(StringBuilder returnStr, String paramId, String paramValue) {
        if (returnStr.length() > 0) {
            if (!paramValue.equals("")) {
                returnStr = returnStr.append("&" + paramId + "=" + paramValue);
            }
        }
        else {
            if (!paramValue.equals("")) {
                returnStr.append(paramId + "=" + paramValue);
            }
        }
        return returnStr;
    }

    DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();

    public String formatMoney(Double money) {
        format.applyPattern("###0.00");
        try {
            String result = format.format(money);
            return result;
        }
        catch (Exception e) {
            if (money != null) {
                return Double.toString(money);
            }
            else {
                return "0";
            }
        }
    }

    public String formatMoneyInt(Double money) {
        format.applyPattern("###0");
        try {
            String result = format.format(money);
            return result;
        }
        catch (Exception e) {
            if (money != null) {
                return Double.toString(money);
            }
            else {
                return "0";
            }
        }
    }

    /**
     * 查询供应商块钱帐号
     * @param agentid
     * @return
     */
    public String findSupplyAcount(long agentid) {
        String sql = "select BillAccount from GpAgentInfo with(nolock) where AgentId=" + agentid;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        String account = "";
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            account = map.get("BillAccount") == null ? "" : map.get("BillAccount").toString();
        }
        return account;
    }

    public double formatMoneyDouble(Double money) {
        format.applyPattern("###0.00");
        try {
            String result = format.format(money);
            return Double.parseDouble(result);
        }
        catch (Exception e) {
            if (money != null) {
                return money;
            }
            else {
                return 0d;
            }
        }
    }

    /**
     * 创建分润记录
     * @param orderid
     * @return
     */
    public String getRoyalty_parameters(long orderid, String OrderNumber, Orderinfo orderinfo, Double ServMoney,
            double SumPaymoney) {
        String returnnote = "";
        String returnMsg = "1,";
        String sql = "select OId,AgentId,Account,Money,status,Btype from GpShareProfit with(nolock) where Oid="
                + orderid;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        List<GpShareProfit> gplist = new ArrayList<GpShareProfit>();
        if (orderinfo != null) {
            //机票支付价格
            //            double Summoney = orderinfo.getTotalticketprice() + orderinfo.getTotalairportfee()
            //                    + orderinfo.getTotalfuelfee() + orderinfo.getTotalinsurprice() + orderinfo.getCurrplatfee()
            //                    + ServMoney;//票面价+机建费+燃油费+保险费+服务费+[采购自己添加的服务费]

            Double supplymoney = orderinfo.getTotalticketprice() + orderinfo.getTotalairportfee()
                    + orderinfo.getTotalfuelfee() + orderinfo.getGpSupplyServMoney()
                    + orderinfo.getGpSupplyInsurServMoney();//+当时分给供应商的服务费+当时分给供应商的保险费
            String supplynote = "供应服务费:" + orderinfo.getGpSupplyServMoney() + ",保险服务费:"
                    + orderinfo.getGpSupplyInsurServMoney();
            WriteLog.write("GpPay代扣款", "ordernumber:" + OrderNumber + "==>" + supplynote);
            GpShareProfit supplyprofit = new GpShareProfit();
            supplyprofit.setMoney(supplymoney);
            supplyprofit.setAgentId(orderinfo.getPolicyagentid());
            supplyprofit.setOId(orderid);
            supplyprofit.setStatus(0);
            supplyprofit.setAccount(findSupplyAcount(orderinfo.getPolicyagentid()));
            supplyprofit.setBtype(1);
            supplyprofit.setNote(supplynote);
            gplist.add(supplyprofit);

            Double caigoumoney = ServMoney - orderinfo.getGpPlatServFee();//当时平台应扣的服务费
            if (caigoumoney < 0) {
                caigoumoney = 0d;
            }
            String caigounote = "采购服务费:" + caigoumoney;
            GpShareProfit caigouprofit = new GpShareProfit();
            caigouprofit.setMoney(caigoumoney);
            caigouprofit.setAgentId(orderinfo.getBuyagentid());
            caigouprofit.setOId(orderid);
            caigouprofit.setStatus(0);
            caigouprofit.setBtype(2);
            caigouprofit.setAccount("");
            caigouprofit.setNote(caigounote);
            gplist.add(caigouprofit);

            Double platmoney = SumPaymoney - caigoumoney - supplymoney;//平台应得的钱
            platmoney = formatMoneyDouble(platmoney);
            String platnote = "";
            GpShareProfit platprofit = new GpShareProfit();
            platprofit.setMoney(platmoney);
            platprofit.setAgentId(46l);
            platprofit.setOId(orderid);
            platprofit.setStatus(0);
            platprofit.setBtype(3);
            platprofit.setAccount("");
            platprofit.setAccount(findPlatAcount(46l));
            platprofit.setNote(platnote);
            gplist.add(platprofit);
            WriteLog.write("GpPay代扣款", "ordernumber:" + OrderNumber + "==>平台利润：" + platmoney);
            if (platmoney < 0) {
                returnMsg = "0,";
                returnnote = "代理商:46平台赔钱" + formatMoney(platmoney);
                WriteLog.write("GpPay代扣款", "ordernumber:" + OrderNumber + "==>" + returnnote);
            }
            else {
                try {
                    String insertsql = "select 1;";
                    for (GpShareProfit sp : gplist) {
                        if (sp.getBtype() == 1 || sp.getBtype() == 3) {
                            if ("".equals(sp.getAccount())) {
                                returnMsg = "0,";
                                returnnote = "代理商:" + sp.getAgentId() + "非法快钱账户";
                                WriteLog.write("GpPay代扣款", "ordernumber:" + OrderNumber + "==>" + returnnote);
                                break;
                            }
                        }
                        if (sp.getMoney() > 0) {
                            insertsql += ";INSERT INTO dbo.GpShareProfit(OId,AgentId,Account,Money,Note,status,Btype) select "
                                    + sp.getOId() + "," + sp.getAgentId() + ",'" + sp.getAccount() + "',"
                                    + sp.getMoney() + ",'" + sp.getNote() + "',0," + sp.getBtype()
                                    + " WHERE NOT EXISTS (SELECT 1 FROM dbo.GpShareProfit WHERE OId=" + sp.getOId()
                                    + " AND AgentId=" + sp.getAgentId() + ");";
                        }
                    }
                    insertsql += ";INSERT INTO dbo.GpAgencyProfitRecord(OrderId ,DaiShouFee ,DaiShouServFee ,DaiShouState) SELECT "
                            + orderinfo.getId() + "," + SumPaymoney + "," + ServMoney
                            + ",0 WHERE NOT EXISTS(SELECT 1 FROM dbo.GpAgencyProfitRecord WITH(nolock) WHERE OrderId="
                            + orderid + ")";
                    if ("1,".equals(returnMsg)) {
                        try {
                            WriteLog.write("GpPay代扣款", "ordernumber:" + OrderNumber + "==>" + insertsql);
                            Server.getInstance().getSystemService().findMapResultBySql(insertsql, null);
                        }
                        catch (Exception e) {
                            returnMsg = "0,";
                            returnnote = "插入gp分润记录异常";
                            e.printStackTrace();
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        if ("1,".equals(returnMsg)) {
            String platAccount = "";
            String note = "";
            Double Money = 0d;
            Double supplyMoney = 0d;
            //1^hyccservice@126.com^100^0^test
            for (GpShareProfit sp : gplist) {
                if (sp.getBtype() == 3 || sp.getBtype() == 2) {
                    if (sp.getBtype() == 3) {//平台
                        platAccount = sp.getAccount();
                    }
                    Money += sp.getMoney();
                    note += sp.getBtype() + "[" + formatMoney(sp.getMoney()) + "]-";
                }
                if (sp.getBtype() == 1) {//供应
                    supplyMoney = sp.getMoney();
                }
            }
            Money = Money * 100;
            supplyMoney = supplyMoney * 100;
            if (Money > 0) {
                returnMsg += "" + "1^" + platAccount + "^" + formatMoneyInt(Money) + "^0^" + note + OrderNumber;
            }
            returnMsg += "," + formatMoneyInt(supplyMoney);
        }
        returnMsg += "," + returnnote;
        return returnMsg;
    }
}