package com.ccservice.b2b2c.atom.service;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.callback.PropertyUtil;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.server.PhoneChangeWebMb;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.service.ISystemService;

public class PhoneChangeWebThread extends Thread {

    private final int TONGCHENG_TYPE = 1;

    private final int KONGTIE_TYPE = 2;


    public volatile boolean isrun = false;
    public void run() {


        while (!isrun) {

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
            String date = df.format(new Date());
            int size = PhoneChangeWebMb.hb.containsKey(date) ? PhoneChangeWebMb.hb.get(date).size() : 0;
            for (Iterator<String> it = PhoneChangeWebMb.hb.keySet().iterator(); it.hasNext();) {

                String key = (String) it.next();

                if (!key.equals(date)) {

                    it.remove();
                }
                System.out.println(key);
            }
            if (size > 100) {
                String sql = " [TrainOrderPhoneMethod_UpdateAll]  @type=" + 0;
                System.out.println(sql);
                getSystemService(1).findMapResultByProcedure(sql);//同程切换web端
                getSystemService(2).findMapResultByProcedure(sql);//空铁切换web端
                String ssql = " [TrainOrderModelRecord_insert] @STARTTIME='" + date + "',@OPERATOR='" + "自动切换WEB端"
                        + "',@RECORD='" + "全部" + "--->切换成了--->" + "WEB端" + "'";

                getSystemService(1).findMapResultByProcedure(ssql);//同程切换web端
                getSystemService(2).findMapResultByProcedure(ssql);//空铁切换web端
                System.out.println("切换成功了");
            }
            try {
                this.sleep(10000);

            }
            catch (InterruptedException e) {

                e.printStackTrace();
            }
        }

    }

    /**
     * 获取对应系统的ITrainService
     * @param systemdburlString
     * @return
     */
    private ISystemService getSystemServiceOldDB(String systemdburlString) {
        System.out.println(systemdburlString);
        HessianProxyFactory factory = new HessianProxyFactory();
        try {
            return (ISystemService) factory.create(ISystemService.class,
                    systemdburlString + ISystemService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取订单对应系统的ITrainService
     * @param systemType
     * @return
     */
    public ISystemService getSystemService(int systemType) {
        return getSystemServiceOldDB(getSystemDBUrl(systemType));
    }

    /**
     * 查到对应DB的service地址
     * @param systemType
     * @return
     */
    private String getSystemDBUrl(int systemType) {
        String systemdburlString = "";
        if (systemType == this.TONGCHENG_TYPE) {
            systemdburlString = PropertyUtil.getValue("TongCheng_Service_Url", "Train.properties");
        }
        if (systemType == this.KONGTIE_TYPE) {
            systemdburlString = PropertyUtil.getValue("KongTie_Service_Url", "Train.properties");
        }
        return systemdburlString;
    }

}
