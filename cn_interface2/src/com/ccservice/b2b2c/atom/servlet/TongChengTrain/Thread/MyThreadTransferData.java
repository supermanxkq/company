package com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread;

import java.net.URLEncoder;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 把抢票数据推向trainorder_bespeak项目
 * @time 2015年10月28日 21:10:05
 * @author QingXin
 **/
public class MyThreadTransferData extends Thread {

    JSONObject json;

    public MyThreadTransferData(JSONObject json) {
        this.json = json;
    }

    public void run() {
        try {
            Thread.sleep(200l);
            transferData(this.json);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void transferData(JSONObject json) {
        try {
            WriteLog.write("3.13_抢票接口", "json:" + json);
            String URL = PropertyUtil.getValue("TrainorderBespeakServelt_URL", "Train.properties");
            String resultString = "";
            for (int i = 1; i <= 5; i++) {
                String prm = URLEncoder.encode(json.toJSONString(), "UTF-8");
                String paramContent = "jsonStr=" + prm;
                WriteLog.write("Q_向trainorder_bespeak项目传递数据", "qorderid:" + json.getString("qorderid") + ";jsonStr="
                        + json.toJSONString());
                resultString = SendPostandGet.submitPost(URL, paramContent, "UTF-8").toString();
                if (resultString.equals("success")) {
                    WriteLog.write("Q_向trainorder_bespeak项目传递数据", "qorderid:" + json.getString("qorderid")
                            + ";向新接口传递数据  第" + i + "传送:" + resultString + " 成功！");
                    break;
                }
                WriteLog.write("Q_向trainorder_bespeak项目传递数据", "qorderid:" + json.getString("qorderid") + ";向新接口传递数据  第"
                        + i + "传送:" + resultString + " 失败！");
            }
            WriteLog.write("Q_向trainorder_bespeak项目传递数据", "qorderid:" + json.getString("qorderid") + ";result:"
                    + resultString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
