package com.ccservice.b2b2c.atom.ticketorder;

import java.sql.Timestamp;
import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customercredit.Customercredit;
import com.ccservice.b2b2c.base.customerpassenger.Customerpassenger;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.passenger.Passenger;
import com.ccservice.b2b2c.base.segmentinfo.Segmentinfo;

public class CreateTicketOrder {

    /**
     * 创建机票订单接口
     */

    /**
     * @author 孙斌
     * @createtime:2011-11-28
     * @param listsegment
     *            航程list
     * @param listpassenger
     *            乘机人list
     * @param orderinfo
     *            订单对象
     * @return 订单号=成功 -1=失败
     */
    public String CreateOrderByIF(List<Segmentinfo> listsegment, List<Passenger> listpassenger, Orderinfo orderinfo) {
        // 订单生成成功或者失败
        String strreturn = "-1";
        try {
            // 插入订单数据表
            orderinfo = Server.getInstance().getAirService().createOrderinfo(orderinfo);
            // 保存乘机人
            for (Passenger passenger : listpassenger) {
                passenger.getEmployeeid();
                passenger.setOrderid(orderinfo.getId());
                passenger.setState(0);
                Server.getInstance().getAirService().createPassenger(passenger);
            }
            // 保存航程信息
            for (Segmentinfo segment : listsegment) {
                segment.setOrderid(orderinfo.getId());
                Server.getInstance().getAirService().createSegmentinfo(segment);
            }
            strreturn = String.valueOf(orderinfo.getId());

            // 保存常用旅客,姓名和证件号已经存在的乘机人不保存
            for (Passenger passenger : listpassenger) {
                String where = " where 1=1 and " + Customerpassenger.COL_customeruserid + " = "
                        + orderinfo.getCustomeruserid() + " and " + Customerpassenger.COL_username + " = '"
                        + passenger.getName() + "'";

                List<Customerpassenger> list = Server.getInstance().getMemberService()
                        .findAllCustomerpassenger(where, "", -1, 0);
                // 如果是否保存为是，并且没有重复的常旅客，则保存
                if (passenger.getIssave() == 1 && list.size() == 0) {
                    Customerpassenger customerpassenger = new Customerpassenger();
                    // 姓名
                    customerpassenger.setUsername(passenger.getName());
                    // 性别
                    if ((passenger.getSex() == null ? 1 : passenger.getSex()) == 1) {
                        customerpassenger.setSex("男");
                    }
                    else {
                        customerpassenger.setSex("女");
                    }
                    // 乘机人类型
                    customerpassenger.setType(passenger.getPtype());
                    // 创建时间
                    customerpassenger.setCreatetime(new Timestamp(System.currentTimeMillis()));
                    // 会员id
                    customerpassenger.setCustomeruserid(orderinfo.getCustomeruserid());
                    // 证件类型
                    customerpassenger.setLivingcardtype(passenger.getIdtype() + "");
                    // 证件号码
                    customerpassenger.setLivingcardnum(passenger.getIdnumber());
                    // 证件有效期
                    customerpassenger.setLivingperiod(passenger.getCardvaliddate());
                    // 里程卡类型
                    customerpassenger.setWorkperiod(passenger.getMileagecardtype() + "");
                    // 里程卡值
                    customerpassenger.setWorknumber(passenger.getMileagecardnumber());
                    // 出生日期
                    customerpassenger.setEntrytime(passenger.getBirthday());

                    customerpassenger = Server.getInstance().getMemberService()
                            .createCustomerpassenger(customerpassenger);
                    // 保存证件信息
                    Customercredit credit = new Customercredit();
                    credit.setCreditnumber(passenger.getIdnumber());
                    credit.setPassportvalidity(passenger.getCardvaliddate());
                    credit.setCredittypeid(passenger.getIdtype());
                    credit.setCreatetime(new Timestamp(System.currentTimeMillis()));
                    credit.setType(0);
                    credit.setRefid(customerpassenger.getId());
                    Server.getInstance().getMemberService().createCustomercredit(credit);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("创建本地订单异常:" + ex.getMessage());
            strreturn = "-1";
        }
        return strreturn;
    }
}
