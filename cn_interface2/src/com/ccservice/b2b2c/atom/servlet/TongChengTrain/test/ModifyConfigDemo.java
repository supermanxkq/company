package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import com.weixin.util.RequestUtil;

/**
 * 修改项目的内存配置
 */

public class ModifyConfigDemo {

    public static void main(String[] args) {
        String cn_interface = "/cn_interface/InitHashMap.jsp";
        String ticket_inter = "/ticket_inter/InitHashMap.jsp";
        String ip = "http://120.26.100.206:";
        //更新的值
        String key = "12306AccountUrl";
        String value = "http://10.169.8.130:45808/12306Account/Account.jsp";
        //String value = "http://121.41.114.170:45808/12306Account/Account.jsp";

        //更新项目
        List<String> ports = new ArrayList<String>();
        ports.add("39216");
        ports.add("39004");
        ports.add("39710");
        ports.add("39210");
        ports.add("39016");
        ports.add("39116");
        ports.add("9016");
        ports.add("59016");
        ports.add("59025");
        ports.add("49410");
        ports.add("9001");
        ports.add("9022");
        ports.add("9010");
        ports.add("59210");
        ports.add("49210");
        ports.add("58216");
        ports.add("58016");
        ports.add("49116");
        ports.add("49016");

        List<String> urls = new ArrayList<String>();
        for (String port : ports) {
            urls.add(ip + port + cn_interface + "?key=" + key + "&value=" + value);
            urls.add(ip + port + ticket_inter + "?key=" + key + "&value=" + value);
        }

        for (String url : urls) {
            //System.out.println(url + "-->" + RequestUtil.get(url, "UTF-8", new HashMap<String, String>(), 0));
        }
    }

}