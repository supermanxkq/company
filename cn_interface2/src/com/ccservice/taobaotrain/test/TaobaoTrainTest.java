package com.ccservice.taobaotrain.test;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import TrainInterfaceMethod.TrainInterfaceMethod;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.SendPostandGet2;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.TrainStudentInfo;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

public class TaobaoTrainTest {
    public static void main1(String[] args) {
        Trainorder trainorder = new Trainorder();
        List<Trainpassenger> trainplist = new ArrayList<Trainpassenger>();
        List<Trainticket> Trainticklist = new ArrayList<Trainticket>();
        Trainticket trainticket = new Trainticket();
        trainticket.setStatus(Trainticket.WAITISSUE);
        trainticket.setCosttime("TEST");
        trainticket.setArrivaltime("TEST");// 新增

        trainticket.setDeparttime("TEST");
        trainticket.setDeparture("TEST");
        trainticket.setArrival("TEST");// 到达

        trainticket.setPrice(0f);
        trainticket.setPayprice(0f);
        // 淘寶票的id
        trainticket.setInterfaceticketno("TEST");
        trainticket.setTrainno("TEST");
        trainticket.setSeattype("TEST");
        trainticket.setTickettype(1);
        trainticket.setInsurorigprice(0f);
        trainticket.setInsurenum(1);
        trainticket.setRealinsureno("111111");
        Trainpassenger trainpassenger = new Trainpassenger();
        trainpassenger.setBirthday("TEST");
        trainpassenger.setIdnumber("TEST");
        trainpassenger.setIdtype(1);
        trainpassenger.setName("TEST");
        Trainticklist.add(trainticket);
        trainpassenger.setTraintickets(Trainticklist);
        try {
            //解析淘宝学生票信息
            if (3 == trainticket.getTickettype()) {
                List<TrainStudentInfo> trainstudentinfos = new ArrayList<TrainStudentInfo>();
                TrainStudentInfo trainstudentinfo = new TrainStudentInfo();
                trainstudentinfo.setClasses("TEST");
                trainstudentinfo.setDepartment("TEST");
                trainstudentinfo.setEductionalsystem("TEST");
                trainstudentinfo.setEntranceyear("TEST");
                trainstudentinfo.setFromcity("TEST");
                trainstudentinfo.setSchoolname("TEST");
                trainstudentinfo.setSchoolprovince("TEST");
                trainstudentinfo.setStudentcard("TEST");
                trainstudentinfo.setStudentno("TEST");
                trainstudentinfo.setTocity("TEST");
                trainstudentinfo.setSchoolnamecode("");
                trainstudentinfo.setSchoolprovincecode("");
                trainstudentinfo.setFromcitycode("");
                trainstudentinfo.setTocitycode("");
                trainstudentinfo.setArg1(0l);
                trainstudentinfo.setArg2("");
                trainstudentinfo.setArg3(0l);
                trainstudentinfos.add(trainstudentinfo);
                trainpassenger.setTrainstudentinfos(trainstudentinfos);
            }
        }
        catch (Exception e) {
        }
        trainplist.add(trainpassenger);

        trainorder.setPassengers(trainplist);
        trainorder.setOrderprice(0f);
        trainorder.setContacttel("TEST");
        trainorder.setAgentcontact("TEST");//卖家ID
        trainorder.setAgentcontacttel("TEST");
        trainorder.setContactuser("TEST");
        trainorder.setQunarOrdernumber("TEST");
        trainorder.setOrderstatus(1);
        trainorder.setState12306(1);
        trainorder.setAgentid(0);
        trainorder.setInterfacetype(TrainInterfaceMethod.TAOBAO);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        trainorder.setCreatetime(ts);
        trainorder.setCreateuser("淘宝网");
        trainorder.setTaobaosendid("test");
        trainorder.setInsureadreess("adress");
        trainorder = Server.getInstance().getTrainService().createTrainorder(trainorder);
        trainorder = Server.getInstance().getTrainService().findTrainorder(trainorder.getId());
        System.out.println(trainorder.getInsureadreess());
        System.out.println(trainorder.getTaobaosendid());
        System.out.println(trainorder.getPassengers().get(0).getTraintickets().get(0).getRealinsureno());
    }

    /*  public static void main(String[] args) {
          String url = "http://120.26.100.206:39216/tcTrainCallBack";
          JSONObject jso = new JSONObject();
          jso.put("agentid", 48);
          jso.put("trainorderid", 82631);
          jso.put("method", "train_order_callback");
          jso.put("returnmsg", "system error");
          try {
              System.out.println(url + "?" + jso.toString());
              String result = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();
              System.out.println(result);
          }
          catch (Exception e) {
              e.printStackTrace();
          }
      }*/

    public static void tuipiao() {
        Map<String, Object> mp = new HashMap<String, Object>();
        mp.put("refund_fee", 42500);
        mp.put("agree_return", true);
        mp.put("refuse_return_reason", "no");
        mp.put("main_order_id", "950269667416789");//
        mp.put("sub_biz_order_id", "950269667416789");
        mp.put("buyerid", "855118967");
        JSONObject jsonObj = JSONObject.fromObject(mp);
        try {
            String Taobao_TrainCallBack = "http://121.41.171.147:30002/cn_interface/TaoBaoRefundTicketCallback";
            String taobao_callbackstr = SendPostandGet.submitGet(Taobao_TrainCallBack + "?json=" + jsonObj, "UTF-8");

            System.out.println(taobao_callbackstr);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void gaiqian() {
        try {
            //            System.out
            //                    .println(SendPostandGet2.doGet(
            //                            "http://localhost:9016/cn_interface/TaoBaoMealCackBack"
            //                                    + "?json={\"cgja\":[{\"realseat\":1,\"subbizorderid\":193547798821223,\"chooseseat\":\""
            //                                    + URLEncoder.encode("硬座_02_无座", "UTF-8")
            //                                    + "\",\"changfee\":0},{\"realseat\":1,\"subbizorderid\":193547798831223,\"chooseseat\":\""
            //                                    + URLEncoder.encode("硬座_02_无座", "UTF-8")
            //                                    + "\",\"changfee\":0},{\"realseat\":1,\"subbizorderid\":193547798841223,\"chooseseat\":\""
            //                                    + URLEncoder.encode("硬座_02_无座", "UTF-8")
            //                                    + "\",\"changfee\":0},{\"realseat\":1,\"subbizorderid\":193547798851223,\"chooseseat\":\""
            //                                    + URLEncoder.encode("硬座_02_无座", "UTF-8")
            //                                    + "\",\"changfee\":0}],\"transactionid\":\"T1505211445093276116\",\"orderidme\":16477,\"orderid\":\"193547798811223\",\"applyid\":\"1006774010312\",\"errorcode\":\"0\",\"sellerid\":3662263834,\"mainbizorderid\":\"193547798811223\",\"jsonarry\":[]}"
            //                                    + "&statue=1", "UTF-8"));
            System.out
                    .println(SendPostandGet2.doGet(
                            "http://localhost:9016/cn_interface/TaoBaoMealCackBack"
                                    + "?json={\"cgja\":[{\"subbizorderid\":193531822551223,\"realseat\":1,\"chooseseat\":\""
                                    + URLEncoder.encode("硬座_01_054号", "UTF-8")
                                    + "\",\"changfee\":0},{\"subbizorderid\":193531822561223,\"realseat\":1,\"chooseseat\":\""
                                    + URLEncoder.encode("硬座_01_053号", "UTF-8")
                                    + "\",\"changfee\":0},{\"subbizorderid\":193531822571223,\"realseat\":1,\"chooseseat\":\""
                                    + URLEncoder.encode("硬座_01_043号", "UTF-8")
                                    + "\",\"changfee\":0},{\"subbizorderid\":193531822581223,\"realseat\":1,\"chooseseat\":\""
                                    + URLEncoder.encode("硬座_01_052号", "UTF-8")
                                    + "\",\"changfee\":0}],\"transactionid\":\"T1505211909237372891\",\"orderidme\":16479,\"errorcode\":\"0\",\"applyid\":\"1006774016312\",\"orderid\":\"193531822541223\",\"sellerid\":3662263834,\"mainbizorderid\":\"193531822541223\",\"jsonarry\":[]}"
                                    + "&statue=1", "UTF-8"));
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //        String json = "{\"train_agent_change_get_response\":{\"apply_id\":1006774010312,\"from_station_name\":\"哈尔滨\",\"from_time\":\"2015-06-27 11:54:00\",\"latest_change_time\":\"2015-05-21 19:30:10\",\"main_biz_order_id\":193547798811223,\"seat_name\":1,\"status\":4,\"tickets\":{\"change_ticket_info\":[{\"change_fee\":400,\"sub_biz_order_id\":193547798821223},{\"change_fee\":400,\"sub_biz_order_id\":193547798831223},{\"change_fee\":0,\"sub_biz_order_id\":193547798841223},{\"change_fee\":400,\"sub_biz_order_id\":193547798851223}]},\"to_station_name\":\"哈尔滨东\",\"to_time\":\"2015-06-27 12:09:00\",\"total_change_fee\":1200,\"train_num\":\"K7042\",\"request_id\":\"9wyd7xv0qs6k\"}}";
        //        JSONObject orderjson = new JSONObject();
        //        JSONArray tickjsons = new JSONArray();
        //        TaoBaoReqChange tbr = new TaoBaoReqChange();
        //        JSONObject retobj = new JSONObject();
        //        JSONObject jb = new JSONObject();
        //        jb = jb.parseObject(json);
        //        JSONObject taobaoorder = new JSONObject();
        //        taobaoorder = jb.getJSONObject("train_agent_change_get_response");
        //        JSONArray taobaotick = new JSONArray();
        //        taobaotick = taobaoorder.getJSONObject("tickets").getJSONArray("change_ticket_info");
        //        Trainform trainform = new Trainform();
        //        trainform.setQunarordernumber(taobaoorder.getString("main_biz_order_id"));
        //        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        //        System.out.println(orders.size());
    }

    /**
     * 淘宝退票
     * 
     * @time 2015年4月14日 下午4:59:27
     * @author fiend
     */

    private static void nonrefundTaobao(float tcnewprice, float price, float procedure, String qunarordernumber,
            String interfaceticketno, String agentcontact) {

        Map mp = new HashMap();
        String refundfee = "0";
        if (tcnewprice > 0) {
            int n = (int) (tcnewprice - procedure) * 100;
            refundfee = n + "";
        }
        else {
            int n = (int) (price - procedure) * 100;
            refundfee = n + "";
        }
        mp.put("refund_fee", refundfee);
        mp.put("agree_return", false);
        mp.put("refuse_return_reason", "1");
        mp.put("main_order_id", qunarordernumber);//
        mp.put("sub_biz_order_id", interfaceticketno);
        mp.put("buyerid", agentcontact);
        JSONObject jsonObj = JSONObject.fromObject(mp);
        String Taobao_TrainCallBack = "http://121.41.171.147:30002/cn_interface/TaoBaoRefundTicketCallback";
        String taobao_callbackstr = SendPostandGet.submitGet(Taobao_TrainCallBack + "?json=" + jsonObj, "UTF-8");
        // WriteLog.write("TrainCreateOrder_issueTAOBAO", this.trainorderid + ":" + taobao_callbackstr);
        System.out.println(taobao_callbackstr);
    }

    public static void main(String[] args) {
        tuipiao();
        //        NULL 62.50   NULL    1046419957219008    1046419957219008    198960890   NULL
        //        NULL    277.50  NULL    1058642902183770    1058642902183770        197627037
        //        NULL    43.50   NULL    1054716085980586    1054716085980586        1904288605
        //        NULL    285.50  NULL    1048895299900027    1048895299900027    2244742700  NULL
        //        nonrefundTaobao(0f, 285.50f, 0f, "1048895299900027", "1048895299900027", "2244742700");

    }
}
