package com.ccservice.b2b2c.atom.servlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.rebaterecord.Rebaterecord;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;

public class TrainchangeRefundMethod {

    public boolean refundChange(Trainorderchange trainorderchange, Trainticket trainticket, Trainorder trainorder,
            String serverinfo) {
        float changePayPrice = trainorderchange.getTcprocedure();
        float canRefundPrice = canRefundPrice(trainorder.getOrdernumber());
        if (canRefundPrice > changePayPrice) {
            createTrainorderrc(1, trainorder.getId(), "改签失败，补款" + "<span style='color:red;'>" + changePayPrice
                    + "</span>元", "确认改签接口", 1, 0);

            //用户名称
            String userName = "接口";
            //退款只用到ID和用户名称
            Customeruser user = new Customeruser();
            user.setMembername(userName);
            user.setId(1000l);

            Server.getInstance()
                    .getTrainService()
                    .ticketRefundChange(trainorderchange.getId(), trainorder.getId(), trainticket.getId(), user,
                            serverinfo);
        }
        else {
            createTrainorderrc(1, trainorder.getId(), "退款失败，退款金额" + changePayPrice + "超过可退金额限制！", "确认改签接口", 1, 0);
            return false;
        }
        return true;
    }

    /**
     * 可退金额
     */
    public float canRefundPrice(List<Rebaterecord> recordList) {
        float canRefundPrice = 0;
        if (recordList != null && recordList.size() > 0) {
            for (Rebaterecord r : recordList) {
                canRefundPrice = ElongHotelInterfaceUtil.floatAdd(canRefundPrice, r.getRebatemoney().floatValue());
            }
        }
        //转换
        canRefundPrice = -canRefundPrice;
        //返回结果
        return canRefundPrice < 0 ? 0 : canRefundPrice;
    }

    /**
     * 可退金额
     * @param orderNumber 系统订单号
     */
    public float canRefundPrice(String orderNumber) {
        float canRefundPrice = 0;
        //单号错误
        if (ElongHotelInterfaceUtil.StringIsNull(orderNumber)) {
            return canRefundPrice;
        }
        //计算金额
        return canRefundPrice(findAllRebaterecord(orderNumber));
    }

    /**
     * 查询交易记录
     */
    @SuppressWarnings({ "unchecked" })
    public List<Rebaterecord> findAllRebaterecord(String orderNumber) {
        List<Rebaterecord> result = new ArrayList<Rebaterecord>();
        //查交易
        if (!ElongHotelInterfaceUtil.StringIsNull(orderNumber)) {
            //查询
            String where = "where C_ORDERNUMBER = '" + orderNumber + "'";
            List<Rebaterecord> list = Server.getInstance().getMemberService().findAllRebaterecord(where, "", -1, 0);
            //非空
            if (list != null && list.size() > 0) {
                //排序
                Collections.sort(list, new Comparator<Rebaterecord>() {
                    public int compare(Rebaterecord a, Rebaterecord b) {
                        return a.getId() < b.getId() ? 1 : -1;//小的放后
                    }
                });
                //添加
                result.addAll(list);
            }
        }
        return result;
    }

    public void createTrainorderrc(int yewutype, long trainorderid, String content, String createurser, int status,
            long ticketid) {
        try {
            Trainorderrc rc = new Trainorderrc();
            rc.setOrderid(trainorderid);
            rc.setContent(content);
            rc.setStatus(status);// Trainticket.ISSUED
            rc.setCreateuser(createurser);// "12306"
            rc.setTicketid(ticketid);
            rc.setYwtype(yewutype);
            Server.getInstance().getTrainService().createTrainorderrc(rc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
