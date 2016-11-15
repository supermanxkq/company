package com.ccservice.b2b2c.atom.train.idmongo.Thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.component.util.CallBackPassengerUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.train.idmongo.IDModel;
import com.ccservice.b2b2c.atom.train.idmongo.MongoLogic;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 向idmongo添加乘客线程
 * @author fiend
 *
 */
public class MyThreadTrainIdMongoInsertPassenger extends Thread {
    private IDModel idmodel;

    //12306账号
    private String loginName;

    private Trainpassenger trainpassenger;

    //重复添加乘客最大次数
    private final int insertMaxSum = 5;

    public MyThreadTrainIdMongoInsertPassenger(IDModel idmodel, String loginName) {
        this.idmodel = idmodel;
        this.idmodel.set_version(getMongoVersion());
        this.loginName = loginName;
        getTrainPassenger(idmodel);
        getCus(loginName);
    }

    /**
     * 哎 自己生成个customeruser吧
     * 
     * @param loginName
     * @time 2015年11月17日 下午4:21:43
     * @author fiend
     */
    @SuppressWarnings("rawtypes")
    private void getCus(String loginName) {
        Customeruser customeruser = new Customeruser();
        List list = Server
                .getInstance()
                .getSystemService()
                .findMapResultBySql(
                        "SELECT TOP 1 ID FROM T_CUSTOMERUSER  WITH (NOLOCK) WHERE C_LOGINNAME='" + loginName + "'",
                        null);
        if (list != null) {
            Map map = (Map) list.get(0);
            customeruser.setId(Long.valueOf(map.get("ID").toString()));
            customeruser.setLoginname(loginName);
            this.customeruser = customeruser;
        }
    }

    /**
     * 哎 自己生成个乘客吧
     * 
     * @param idmodel
     * @time 2015年11月17日 下午4:21:23
     * @author fiend
     */
    private void getTrainPassenger(IDModel idmodel) {
        Trainpassenger trainpassenger = new Trainpassenger();
        trainpassenger.setName(idmodel.GetRealName());
        String idnumber = new MongoLogic().GetStringFromLong(idmodel.GetIDNumber());
        trainpassenger.setIdnumber(idnumber);
        trainpassenger.setIdtype(1);
        Trainticket trainticket = new Trainticket();
        trainticket.setTickettype(1);
        List<Trainticket> traintickets = new ArrayList<Trainticket>();
        traintickets.add(trainticket);
        trainpassenger.setTraintickets(traintickets);
        this.trainpassenger = trainpassenger;
    }

    public MyThreadTrainIdMongoInsertPassenger(Trainpassenger trainpassenger, Customeruser user) {
        long idNumber = new MongoLogic().GetLongFromString(trainpassenger.getIdnumber());
        this.idmodel = new IDModel(idNumber, trainpassenger.getName(), trainpassenger.getIdtype(), user.getLoginname());
        this.idmodel.set_version(getMongoVersion());//增加冒用版本
        this.customeruser = user;
        this.loginName = user.getLoginname();
        this.trainpassenger = trainpassenger;
    }

    @Override
    public void run() {
        InsertPassenger2IdMongo();
        try {
            CallbackTongchengBindPassenger(trainpassenger, loginName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    Customeruser customeruser;

    /**
     * 将能成功绑定的乘客同步到同程
     * 
     * @param idmodel
     * @param loginName
     * @time 2015年11月4日 下午4:24:37
     * @author WCL
     */
    public void CallbackTongchengBindPassenger(Trainpassenger trainpassenger, String loginName) {
        String isBespeak = PropertyUtil.getValue("isBespeak", "Train.properties");
        if (isBespeak == null || !"1".equals(isBespeak)) {
            int ticketType = trainpassenger.getTraintickets().get(0).getTickettype();
            WriteLog.write("CallBackPassengerUtil", "passengerName:" + trainpassenger.getName() + ":passengerNumber:"
                    + trainpassenger.getIdnumber() + ":passengerNumberType:" + trainpassenger.getIdtype()
                    + ":ticketType:" + ticketType + ":loginName:" + loginName);
            customeruser.setIsenable(1);
            List<Trainpassenger> trainPassengers = new ArrayList<Trainpassenger>();
            Trainpassenger p1 = new Trainpassenger();
            p1.setName(trainpassenger.getName());
            p1.setIdnumber(trainpassenger.getIdnumber());
            p1.setIdtype(trainpassenger.getIdtype());
            Trainticket trainticket = new Trainticket();
            trainticket.setTickettype(ticketType);
            List<Trainticket> traintickets = new ArrayList<Trainticket>();
            traintickets.add(trainticket);
            p1.setTraintickets(traintickets);
            //放入乘客
            trainPassengers.add(p1);
            int operationtypeid = 1;//操作类型 ID 1:新增，2:删除，3:修改

            CallBackPassengerUtil.callBackTongcheng(customeruser, trainPassengers, operationtypeid);
        }
    }

    /**
     * 向idmongo添加乘客
     * @author fiend
     */
    private void InsertPassenger2IdMongo() {
        MongoLogic mongoLogic = new MongoLogic();
        for (int i = 0; i < this.insertMaxSum; i++) {
            try {
                WriteLog.write("MyThreadTrainIdMongoInsertPassenger",
                        this.loginName + "--->" + this.idmodel.GetRealName() + ":" + this.idmodel.GetIDNumber() + ":"
                                + this.idmodel.GetIDType());
                mongoLogic.AddId(this.loginName, this.idmodel);
                break;
            }
            catch (Exception e) {
                WriteLog.write("MyThreadTrainIdMongoInsertPassenger_ERROR",
                        this.loginName + "--->" + this.idmodel.GetRealName() + ":" + this.idmodel.GetIDNumber() + ":"
                                + this.idmodel.GetIDType());
                ExceptionUtil.writelogByException("MyThreadTrainIdMongoInsertPassenger_ERROR", e);
            }
        }
    }

    /**
     * 获取当前mongo的Version
     * 
     * @return
     * @time 2015年12月29日 上午11:06:46
     * @author fiend
     */
    private int getMongoVersion() {
        String mongoVersionStr = PropertyUtil.getValue("MongoVersion", "Train.properties");
        try {
            return Integer.valueOf(mongoVersionStr);
        }
        catch (NumberFormatException e) {
            ExceptionUtil.writelogByException("ERROR_getMongoVersion", e);
            return -1;
        }

    }
}
