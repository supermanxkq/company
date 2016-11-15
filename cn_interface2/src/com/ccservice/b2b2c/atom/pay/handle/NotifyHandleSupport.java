package com.ccservice.b2b2c.atom.pay.handle;

import java.util.List;

import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;

public abstract class NotifyHandleSupport extends HttpServlet {

    Log logger = LogFactory.getLog(NotifyHandleSupport.class);

    /**
     * 支付成功后的后续订单处理
     * @param extra_common_param  支付回传信息(包含订单好和处理类信息)
     * @param trade_no   交易号
     * @param payprice 支付金额
     * @param paytype  支付方式
     */
    protected void orderHandle(String extra_common_param, String trade_no, float payprice, int paytype,
            String selleremail) {
        String[] infos = extra_common_param.split("Fg");// 支付时传入参数规范
        String ordernumber = infos[0];// 获取订单号
        String handleName = infos[1];// 获取handle类名
        PayHandle payhandle = null;
        try {
            payhandle = (PayHandle) Class.forName(PayHandle.class.getPackage().getName() + "." + handleName)
                    .newInstance();
            payhandle.orderHandle(ordernumber, trade_no, payprice, paytype, selleremail);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.info("orderHandle异常", e.fillInStackTrace());
        }
    }

    /**
     * 根据sysconfig的name获得value
     * 这里有缓存
     * @param name
     * @return
     */
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

}
