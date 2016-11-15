package com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread;

import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.component.util.CallBackPassengerUtil;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

public class MyThreadCallBackTongcheng315Passenger extends Thread {

    Customeruser user;

    Trainpassenger trainpassenger;

    int type;

    public MyThreadCallBackTongcheng315Passenger(Customeruser user, Trainpassenger trainpassenger, int type) {

        this.user = user;
        this.trainpassenger = trainpassenger;
        this.type = type;
    }

    @Override
    public void run() {
        checkAccountMethod(user, trainpassenger, type);
    }

    /**
     * 将不可用的帐号同步推送给同程 OR 将315的乘客同步推送给同程
     * 
     * @param user 
     * @param trainpassenger
     * @time 2015年11月17日 下午5:49:28
     * @author w.c.l
     */
    public void checkAccountMethod(Customeruser user, Trainpassenger trainpassenger, int type) {

        Customeruser temp = new Customeruser();
        temp.setId(user.getId());
        temp.setIsenable(user.getIsenable());
        if (type == 315) {
            temp.setIsenable(1);
            temp.setLogpassword("315");
        }
        else {
            temp.setLogpassword("100");
        }

        List<Trainpassenger> trainpassengers = new ArrayList<Trainpassenger>();
        trainpassengers.add(trainpassenger);
        WriteLog.write("TongchengSupplyMethod_checkAccountMethod", "user:" + user.getId() + "type:" + type);
        CallBackPassengerUtil.callBackTongcheng(temp, trainpassengers, 2);
    }
}
