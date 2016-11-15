/**
 * 
 */
package com.ccservice.b2b2c.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 此工具类是发送rep的异常信息到指定的url
 * 该url可以记录rep的信息包括错误但不限于错误的其他信息
 * @time 2015年10月30日 上午10:29:03
 * @author chendong
 */
public class RepLogUtil extends Thread {

    public static void main(String[] args) {

        RepLogUtil reputil = new RepLogUtil("L300", "10.251.247.161", "121.43.155.68", "1", 0);
        reputil.start();

    }

    String RepNo;

    String RepNip;

    String RepWip;

    String Contentstr;

    int Type;

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        super.run();
        getString();
    }

    public RepLogUtil(String repNo, String repNip, String repWip, String contentstr, int type) {
        super();
        this.RepNo = repNo;
        this.RepNip = repNip;
        this.RepWip = repWip;
        this.Contentstr = contentstr;
        this.Type = type;
    }

    public RepLogUtil(RepServerBean repServerBean, String contentstr, int type) {
        super();
        this.RepNo = repServerBean.getName();
        this.RepNip = repServerBean.getUrl();
        this.RepWip = "";
        this.Contentstr = contentstr;
        this.Type = type;
    }

    /**
     * 根据url获取对应的rep信息
     * @time 2015-10-30 10:42:44
     * @author zhaohongbo
     * */
    public void getString() {
        String repLogSendUrl = PropertyUtil.getValue("repLogSendUrl", "train.log.properties");
        String contentstr = Contentstr;
        try {
            contentstr = URLEncoder.encode(contentstr, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
        }
        String paramContent = "RepNo=" + RepNo + "&RepNip=" + RepNip + "&RepWip=" + RepWip + "&Contentstr="
                + contentstr + "&Type=" + Type;
        int r1 = (int) (Math.random() * 100000);
        WriteLog.write("占座REP失败回传", r1 + "--->" + repLogSendUrl + "?" + paramContent);
        String result = SendPostandGet.submitPost(repLogSendUrl, paramContent, "UTF-8").toString();
        WriteLog.write("占座REP失败回传", r1 + "--->" + result);
    }
}
