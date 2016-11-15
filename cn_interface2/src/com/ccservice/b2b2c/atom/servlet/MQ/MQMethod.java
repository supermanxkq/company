package com.ccservice.b2b2c.atom.servlet.MQ;

/**
 * ActiveMQ 类型
 * @time 2015年1月27日 下午2:34:55
 * @author fiend
 */
public class MQMethod {
    public final static int QUERY = 2;

    /**
     * 订单支付
     */
    public final static int ORDERPAY = 3;

    public final static String ORDERPAY_NAME = "PayMQ_TrainPay";

    /**
     * 淘宝订单支付
     */
    public final static String OrderPay_TaoBaoName = "PayMQ_TaoBaoTrainPay";

    /**
     * 获取支付链接
     */
    public final static int ORDERGETURL = 4;

    public final static String ORDERGETURL_NAME = "PayMQ_TrainGetURL";

    /**
     * 更新交易号
     */
    public final static int PayNumberUPDATE = 5;

    public final static String PayNumberUPDATE_NAME = "MQ_TrainNumberUpdate";

    /**
     * 改签支付获取支付链接
     */
    public final static int GQORDERURL = 6;

    public final static String GQORDERGETURL_NAME = "PayMQ_GQTrainurl";

    /**
     * 改签更新交易号
     */
    public final static int GQPayNumberUpdate = 7;

    public final static String GQPayNumberUPDATE_NAME = "PayMQ_GQTrainPay";

    /**
     * 支付池改签支付
     */
    public final static String GQPay_NAME = "PayMQ_TrainGetURLGQ";
}
