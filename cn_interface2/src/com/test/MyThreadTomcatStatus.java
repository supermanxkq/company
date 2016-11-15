package com.test;

import java.util.Date;

import com.callback.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;

public class MyThreadTomcatStatus extends Thread {
    private int tomcatNo;

    private TomcatStatusMethod tomcatStatusMethod;

    public MyThreadTomcatStatus(int tomcatNo, TomcatStatusMethod tomcatStatusMethod) {
        this.tomcatNo = tomcatNo;
        this.tomcatStatusMethod = tomcatStatusMethod;
    }

    public void run() {
        String result = "-1";
        try {
            result = SendPostandGet.submitGet(this.tomcatStatusMethod.getTomcatUrl(), "UTF-8");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        WriteLog.write("tomcat_自测",
                this.tomcatStatusMethod.getTomcatName() + "--->" + this.tomcatStatusMethod.getTomcatUrl() + "--->"
                        + result);
        if (result != null && "success".equals(result)) {
            TomcatStatusMem.getTomcatStatusMethods().get(this.tomcatNo).setTomcatStatus(true);
            TomcatStatusMem.getTomcatStatusMethods().get(this.tomcatNo).setErrTimeDate(null);
        }
        else {
            if (TomcatStatusMem.getTomcatStatusMethods().get(this.tomcatNo).isTomcatStatus()) {
                TomcatStatusMem.getTomcatStatusMethods().get(this.tomcatNo).setTomcatStatus(false);
                TomcatStatusMem.getTomcatStatusMethods().get(this.tomcatNo).setErrTimeDate(new Date());
            }

        }
    }
}
