package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.text.*;
import java.util.*;
import com.weixin.util.RequestUtil;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class WHTestDemo {

    //加消费者
    private static final int addType = 1;

    //减消费者
    private static final int subType = 0;

    //查看消费者
    private static final int showType = 10;

    private HessianProxyFactory factory = new HessianProxyFactory();

    private String url = "http://120.26.100.206:9026/cn_service/service/";

    public static void main(String[] args) throws Exception {
        统计客人账号订单();
        //        删除TrainAccountSrc表数据();
        //        淘宝托管未登录(5); //正常，次数5
        //        淘宝托管未登录(1); //忙碌，次数1
        //        空铁下单消费者加减(19810, showType, 60);//慎用
    }

    /**
     * 更新下单，消费者加、减
     * @param port tomcat端口
     * @param oldNum 原消费者数量，type为1时有用
     * @param type 业务类型>>0：消费者数量减10，1：消费者数量加1，10：查看消费者数量
     */
    private static void 空铁下单消费者加减(int port, int type, int oldNum) {
        //URL
        String url = "http://120.26.223.234:" + port + "/ticket_inter/mq/MqTrainCreateOrder.jsp?type=" + type;
        //减
        if (type == 0) {
            while (true) {
                //请求
                String html = RequestUtil.get(url, AccountSystem.UTF8, AccountSystem.NullMap, 0);
                //打印
                System.out.println(ElongHotelInterfaceUtil.getCurrentTime() + "-->" + html);
                //中断
                if (html.contains("下单消费者数量:0")) {
                    break;
                }
            }
        }
        //加
        else if (type == 1) {
            for (int i = 0; i < oldNum; i++) {
                System.out.println(RequestUtil.get(url, AccountSystem.UTF8, AccountSystem.NullMap, 0));
            }
        }
        //查看
        else if (type == 10) {
            System.out.println(RequestUtil.get(url, AccountSystem.UTF8, AccountSystem.NullMap, 0));
        }
    }

    /**
     * 删除TrainAccountSrc表数据
     */
    private static void 删除TrainAccountSrc表数据() throws Exception {
        WHTestDemo demo = new WHTestDemo();
        //当前
        String current = ElongHotelInterfaceUtil.getCurrentDate();
        //历史
        String history = ElongHotelInterfaceUtil.getAddDate(current, -70);
        //4906366
        while (true) {
            String sql = "delete from TrainAccountSrc where ID in (select top 2000 ID from TrainAccountSrc with(nolock) where CreateTime < '"
                    + current + "' and AccountSrc = 57)";
            int delete = demo.getSystemService().excuteAdvertisementBySql(sql);
            System.out.println(ElongHotelInterfaceUtil.getCurrentTime() + "-->" + delete);
            if (delete <= 0) {
                break;
            }
            else {
                Thread.sleep(1000);
            }
        }
        while (true) {
            String sql = "delete from TrainAccountSrc where ID in (select top 2000 ID from TrainAccountSrc with(nolock) where CreateTime < '"
                    + history + "')";
            int delete = demo.getSystemService().excuteAdvertisementBySql(sql);
            System.out.println(ElongHotelInterfaceUtil.getCurrentTime() + "-->" + delete);
            if (delete <= 0) {
                break;
            }
            else {
                Thread.sleep(1000);
            }
        }
    }

    /**
     * @param count 判断cookie是否在线次数，按以下方式赋值。要修改.4数据库数据，否则重启tomcat数据存在问题
     * 
     * 如果大量淘宝Ali服务器处于忙碌中，.4执行更新次数SQL，C_VALUE值设为1，然后执行main方法
     * 如果忙碌状态恢复正常，将C_VALUE值设为为5，然后执行main方法
     * -------------------------------------------------------------------------
     * 忙碌时>>更新次数SQL>>update T_SYSCONFIG set C_VALUE = '1' where C_NAME = 'TaoBao_NoLoginTryCount'
     * 正常时>>更新次数SQL>>update T_SYSCONFIG set C_VALUE = '5' where C_NAME = 'TaoBao_NoLoginTryCount'
     */
    private static void 淘宝托管未登录(int count) {
        WHTestDemo demo = new WHTestDemo();
        //下单消费者>>cn_interface
        for (int i = 1; i <= 8; i++) {
            demo.updateData("http://120.26.223.234:19" + i + "10/cn_interface/InitRepServerUtilData.jsp", count);
        }
        System.out.println("-----华丽的分隔线-----");
        //审核消费者>>ticket_inter
        demo.updateData("http://120.26.100.206:19510/ticket_inter/InitRepServerUtilData.jsp", count);
    }

    /**
     * 更新内存
     */
    private void updateData(String url, int count) {
        //尝试次数
        System.out.println(url
                + " >>内存数据为>> "
                + RequestUtil
                        .get(url + "?key=TaoBao_NoLoginTryCount&value=" + count, AccountSystem.UTF8,
                                AccountSystem.NullMap, 0).replaceAll("<center>", "").replaceAll("<br/></center>", "")
                        .replaceAll("<br/>", " / "));
    }

    @SuppressWarnings("rawtypes")
    private static void 统计客人账号订单() throws Exception {
        WHTestDemo demo = new WHTestDemo();
        //周几
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        week = week == 1 ? 7 : week - 1;//星期日：1；星期六：7
        //格式
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        //今天
        String today = ElongHotelInterfaceUtil.getCurrentDate();
        //天数
        int days = week == 1 ? 3 : 1;//周一跑3天（上周五、六、七的），其他跑1天（头天的）
        //开始
        String start = ElongHotelInterfaceUtil.getAddDate(today, -days);
        //备注>>可自定days和start，如days直接设为7，日期直接设为2016-02-01，表示从1号开始跑7天的数据
        //循环
        for (int i = 0; i < days; i++) {
            String current = ElongHotelInterfaceUtil.getAddDate(start, i);
            String tomorrow = ElongHotelInterfaceUtil.getAddDate(current, 1);
            //SQL
            String 途牛所有 = "select COUNT(*) TOTAL from T_TRAINORDER with(nolock) where C_CREATETIME > '" + current
                    + "' and C_CREATETIME < '" + tomorrow + "' and ordertype = 3";
            String 途牛成功 = "select COUNT(*) TOTAL from T_TRAINORDER with(nolock) where C_CREATETIME > '" + current
                    + "' and C_CREATETIME < '" + tomorrow + "' and C_ORDERSTATUS = 3 and ordertype = 3";
            String 淘宝所有 = "select COUNT(*) TOTAL from T_TRAINORDER with(nolock) where C_CREATETIME > '" + current
                    + "' and C_CREATETIME < '" + tomorrow + "' and ordertype = 4";
            String 淘宝成功 = "select COUNT(*) TOTAL from T_TRAINORDER with(nolock) where C_CREATETIME > '" + current
                    + "' and C_CREATETIME < '" + tomorrow + "' and C_ORDERSTATUS = 3 and ordertype = 4";
            //查询数据
            List 途牛成功集合 = demo.getSystemService().findMapResultBySql(途牛成功, null);
            List 途牛所有集合 = demo.getSystemService().findMapResultBySql(途牛所有, null);
            List 淘宝成功集合 = demo.getSystemService().findMapResultBySql(淘宝成功, null);
            List 淘宝所有集合 = demo.getSystemService().findMapResultBySql(淘宝所有, null);
            //转MAP
            Map 途牛成功Map = (Map) 途牛成功集合.get(0);
            Map 途牛所有Map = (Map) 途牛所有集合.get(0);
            Map 淘宝成功Map = (Map) 淘宝成功集合.get(0);
            Map 淘宝所有Map = (Map) 淘宝所有集合.get(0);
            //统计数据
            double 途牛成功个数 = Double.parseDouble(途牛成功Map.get("TOTAL").toString());
            double 途牛所有个数 = Double.parseDouble(途牛所有Map.get("TOTAL").toString());
            double 淘宝成功个数 = Double.parseDouble(淘宝成功Map.get("TOTAL").toString());
            double 淘宝所有个数 = Double.parseDouble(淘宝所有Map.get("TOTAL").toString());
            //输出统计
            System.out.println("途牛成功率>>" + current + ">>" + decimalFormat.format((途牛成功个数 / 途牛所有个数) * 100));
            System.out.println("淘宝成功率>>" + current + ">>" + decimalFormat.format((淘宝成功个数 / 淘宝所有个数) * 100));
            if (i != days - 1) {
                System.out.println("-----华丽的分隔线-----");
            }
        }
    }

    private ISystemService getSystemService() throws Exception {
        return (ISystemService) factory.create(ISystemService.class, url + ISystemService.class.getSimpleName());
    }
}