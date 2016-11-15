package com.ccservice.b2b2c.atom.servlet.job;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSONArray;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;

public class JobTongCheng3151 implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            String sql = "exec [sp_AccountWait_select] ";//存储过程  中间状态998防并发
            List list1 = null;
            try {
                list1 = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            }
            catch (Exception e) { 
                e.printStackTrace();
            }
            if (list1.size() > 0) {
                for (int i = 0; i < list1.size(); i++) {
                    Map map = (Map) list1.get(i);
                    String OrderId = map.get("OrderId").toString();
                    Trainorder trainorder = Server.getInstance().getTrainService()
                            .findTrainorder(Long.valueOf(OrderId));
                    trainorder.setOrderstatus(Trainorder.CANCLED);
                    trainorder.setIsquestionorder(Trainorder.NOQUESTION);
                    Server.getInstance().getTrainService().updateTrainorder(trainorder);
                    createTrainorderrc(trainorder.getId(), "该订单等待账号超时-已据单", "3151", 1);
                    String sqll="exec  [sp_accountWait_update] @OrderId="+OrderId;
                    Server.getInstance()
                            .getSystemService().findMapResultBySql(sqll, null);
                    for (int j = 0; j < 5; j++) {
                        if (returnTongcheng3151(trainorder)) {
                            createTrainorderrc(trainorder.getId(), "该订单等待账号超时-已据单,回调成功！", "3151", 1);
                            break;
                        }  
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 同程3151回调
     * @time 2016年3月11日 下午4:19:17
     * @author QingXin
     **/
    public boolean returnTongcheng3151(Trainorder trainorder) {
        boolean is = false;
        try {
            JSONObject jso = new JSONObject();
            jso.put("method", "train_order_callback");
            jso.put("trainorderid", trainorder.getId());
            jso.put("unmatchedpasslist", new JSONArray());
            jso.put("ordersuccess", false);
            jso.put("tongchengorderid", trainorder.getQunarOrdernumber());
            jso.put("resultJsonstr",
                    URLEncoder.encode(trainorder.getPassengers().get(0).getName()
                            + "冒用，未通过身份核验，请持本人身份证件原件到就近铁路客运车站办理身份核验", "UTF-8"));
            jso.put("agentid", trainorder.getAgentid());
            String url = "http://tctraincallback.tc.hangtian123.net/cn_interface/tcTrainCallBack";
            WriteLog.write("t同程火车票接口_3151超时回调", "orderId:" + trainorder.getId() + "; URL:" + url + "?" + jso.toString());
            String result = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();
            WriteLog.write("t同程火车票接口_3151超时回调", "orderId:" + trainorder.getId() + ";result:" + result);
            if (result.equalsIgnoreCase("success")) {
                is = true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }

    protected void createTrainorderrc(Long trainorderId, String content, String createuser, int status) {
        try {
            Trainorderrc rc = new Trainorderrc();
            rc.setOrderid(trainorderId);
            rc.setContent(content);
            rc.setStatus(status);
            rc.setCreateuser(createuser);
            rc.setYwtype(1);
            Server.getInstance().getTrainService().createTrainorderrc(rc);
        }
        catch (Exception e) {
            WriteLog.write("操作记录失败", trainorderId + ":content:" + content);
        }
    }
}
