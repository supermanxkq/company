/**
 * 
 */
package com.ccservice.b2b2c.atom.component.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.client.util.StringUtil;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 回调给同程常旅客的工具类
 * @time 2015年10月14日 下午3:43:42
 * @author chendong
 */
public class CallBackPassengerUtil {
    public static void main(String[] args) {
        Customeruser customeruser = new Customeruser();
        customeruser.setLoginname("loginname1");
        customeruser.setIsenable(1);
        List<Trainpassenger> trainPassengers = new ArrayList<Trainpassenger>();
        Trainpassenger p1 = new Trainpassenger();
        p1.setName("许道玉");
        p1.setIdnumber("362323196811086218");
        p1.setIdtype(1);
        Trainticket trainticket = new Trainticket();
        trainticket.setTickettype(1);
        List<Trainticket> traintickets = new ArrayList<Trainticket>();
        traintickets.add(trainticket);
        p1.setTraintickets(traintickets);
        //放入乘客
        trainPassengers.add(p1);
        int operationtypeid = 1;//操作类型 ID 1:新增，2:删除，3:修改
        callBackTongcheng(customeruser, trainPassengers, operationtypeid);
    }

    /**
     *
     * 同步同程常旅客的公共方法
     * 调用自己的servlet然后再请求同程
     * @time 2015年10月14日 下午3:44:40
     * @author chendong
     * @param operationtypeid  操作类型 ID 1:新增，2:删除，3:修改
     */
    public static void callBackTongcheng(Customeruser customeruser, List<Trainpassenger> trainPassengers,
            int operationtypeid) {
        //        #4.21. 下单账号状况及常旅变更回调推送接口        #自己的回调地址
        String TongchengCallBackPassengerUrl = PropertyUtil.getValue("TongchengCallBackPassengerUrl",
                "train.tongcheng.properties");
        JSONObject accounts = new JSONObject();
        JSONArray accountsArray = new JSONArray();
        JSONObject jsonAccount = new JSONObject();
        JSONArray jsonArrayPassengers = new JSONArray();
        for (int i = 0; i < trainPassengers.size(); i++) {
            Trainpassenger trainpassenger = trainPassengers.get(i);
            JSONObject jsonObjectPassenger = new JSONObject();
            String passengersename = StringUtil.geturlencode(nullObject(trainpassenger.getName()));
            jsonObjectPassenger.put("passengersename", passengersename);
            jsonObjectPassenger.put("passportseno", nullObject(trainpassenger.getIdnumber()));
            jsonObjectPassenger.put("passporttypeseid", getIdtype(trainpassenger.getIdtype()));//证件类型 ID 与名称对应关系: 1:二代身份证，2:一代身份证，C:港澳通行证，G:台湾通 行证，B:护照
            jsonObjectPassenger.put("passporttypeseid", getIdtype(trainpassenger.getIdtype()));//证件类型 ID 与名称对应关系: 1:二代身份证，2:一代身份证，C:港澳通行证，G:台湾通 行证，B:护照
            String passporttypeseidname = StringUtil.geturlencode(nullObject(getIdtypestr(trainpassenger.getIdtype())));
            jsonObjectPassenger.put("passporttypeseidname", passporttypeseidname);//证件类型 ID 与名称对应关系: 1:二代身份证，2:一代身份证，C:港澳通行证，G:台湾通 行证，B:护照
            jsonObjectPassenger.put("passengertypeid", gettickettype(trainpassenger));//旅客类型 ID 与旅客类型名称对应关系：1:成人票，2:儿童票，3:学生票，4:残军票
            String passengertypename = StringUtil.geturlencode(gettickettypestr(trainpassenger));
            jsonObjectPassenger.put("passengertypename", passengertypename);//旅客类型 ID 与旅客类型名称对应关系：1:成人票，2:儿童票，3:学生票，4:残军票
            jsonObjectPassenger.put("operationtypeid", operationtypeid);//操作类型 ID 1:新增，2:删除，3:修改
            String operationtypename = getoperationtypeidName(operationtypeid);
            operationtypename = StringUtil.geturlencode(operationtypename);
            jsonObjectPassenger.put("operationtypename", operationtypename);//操作类型 ID 1:新增，2:删除，3:修改
            String operationtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "";
            jsonObjectPassenger.put("operationtime", operationtime);//操作时间，格式：yyyyMMddHHmmss（非空）例：20151001093518
            if ("315".equals(customeruser.getLogpassword())) {
                jsonObjectPassenger.put("identitystatusid", "315");
                jsonObjectPassenger.put("identitystatusmsg", StringUtil.geturlencode("身份信息涉嫌被他人冒用"));
            }
            else {
                jsonObjectPassenger.put("identitystatusid", "100");
                jsonObjectPassenger.put("identitystatusmsg", StringUtil.geturlencode("正常"));
            }

            jsonArrayPassengers.add(jsonObjectPassenger);
        }
        jsonAccount.put("passengers", jsonArrayPassengers);
        jsonAccount.put("accountname", customeruser.getId() + "");

        //        accounts 示例
        //        {"accounts":[{"accountname":"23aa","accountstatusid":"2","accountstatusname":"可用", "passengers":
        //        [{"passengersename":"张三","passportseno":"362323230950435300","passporttypeseid":"1",
        //        "passporttypeseidname":"二代身份证","passengertypeid":"1","passengertypename":"成人票","operationtypeid":"1",
        //        "operationtypename":"新增","operationtime":"20151001093518"}]
        //            }]
        //            }
        String accountstatusid = "2";//账户状态ID 与名称对应关系:1:不可用，2:可用
        String accountstatusname = "可用";//账户状态ID 与名称对应关系:1:不可用，2:可用
        if (customeruser.getIsenable() == 1) {

        }
        else {
            accountstatusid = "1";
            accountstatusname = "不可用";
        }
        jsonAccount.put("accountstatusid", accountstatusid);
        accountstatusname = StringUtil.geturlencode(accountstatusname);
        jsonAccount.put("accountstatusname", accountstatusname);
        accountsArray.add(jsonAccount);
        accounts.put("accounts", accountsArray);

        String backjson = "backjson=" + accounts.toJSONString();
        //        System.out.println(TongchengCallBackPassengerUrl);
        //        System.out.println(backjson);
        WriteLog.write("CallBackPassengerUtil", backjson + "=>" + TongchengCallBackPassengerUrl);
        String resultString = SendPostandGet.submitPost(TongchengCallBackPassengerUrl, backjson, "utf-8").toString();
        WriteLog.write("CallBackPassengerUtil", "结果>" + resultString + "->" + TongchengCallBackPassengerUrl);
        //        System.out.println("结果>" + resultString + "->" + TongchengCallBackPassengerUrl);
    }

    /**
     * 
     * @time 2015年10月28日 下午3:36:53
     * @author chendong
     */
    private static String nullObject(Object object) {
        if (object == null) {
            return "";
        }
        else {
            return object.toString();
        }
    }

    /**
     * 
     * @param trainpassenger
     * @return
     * @time 2015年10月20日 下午12:23:24
     * @author chendong
     */
    private static String gettickettypestr(Trainpassenger trainpassenger) {
        String tickettype = "1";
        try {
            tickettype = trainpassenger.getTraintickets().get(0).getTickettypestr() + "票";//1:成人票，2:儿童票，3:学生票，4:残军票
        }
        catch (Exception e) {
        }
        return tickettype;
    }

    /**
     * 
     * @param trainpassenger
     * @return 1:成人票，2:儿童票，3:学生票，4:残军票
     * @time 2015年10月20日 下午12:16:19
     * @author chendong
     */
    private static String gettickettype(Trainpassenger trainpassenger) {
        String tickettype = "1";
        try {
            if (trainpassenger != null && trainpassenger.getTraintickets() != null
                    && trainpassenger.getTraintickets().size() > 0
                    && trainpassenger.getTraintickets().get(0).getTickettype() != 0) {
                //                int i_tickettype = 
                tickettype = "" + trainpassenger.getTraintickets().get(0).getTickettype();//1:成人票，2:儿童票，3:学生票，4:残军票
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return tickettype;
    }

    public static String getoperationtypeidName(int idtype) {
        switch (idtype) {
        case 1:
            return "新增";
        case 2:
            return "删除";
        case 3:
            return "修改";
        }
        return "修改";
    }

    public static String getIdtype(int idtype) {
        switch (idtype) {
        case 1:
            return "1";
        case 2:
            return "2";
        case 3:
            return "B";
        case 4:
            return "C";
        case 5:
            return "G";
        }
        return "1";
    }

    public static String getIdtypestr(int idtype) {
        switch (idtype) {
        case 1:
            return "二代身份证";
        case 2:
            return "一代身份证";
        case 3:
            return "护照";
        case 4:
            return "港澳通行证";
        case 5:
            return "台湾通行证";
        }
        return "";
    }
}
