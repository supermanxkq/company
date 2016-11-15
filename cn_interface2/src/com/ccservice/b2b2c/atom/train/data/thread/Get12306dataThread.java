package com.ccservice.b2b2c.atom.train.data.thread;

import java.util.concurrent.Callable;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.util.HttpUtils;

public class Get12306dataThread implements Callable<String> {
    String search_url;

    Long l1;

    public Get12306dataThread(String search_url, long l1) {
        super();
        this.search_url = search_url;
        this.l1 = l1;
    }

    @Override
    public String call() throws Exception {
        String json;
        int searchcount = 0;
        Long l2 = System.currentTimeMillis();
        do {
            if (searchcount > 0) {
                Thread.sleep(1000L);
            }
            json = HttpUtils.Get_https(search_url, 3000);
            searchcount++;
        }
        while (json.length() == 0 && searchcount < 5);
        WriteLog.write("Wrapper_12306_Get12306dataThread", l1 + ":searchcount:" + searchcount + ":json:" + json
                + ":search_url:" + search_url + " 耗时:" + (System.currentTimeMillis() - l2));

        return json;
    }

    public static void main(String[] args) {
        String search_url = "https://kyfw.12306.cn/otn/lcxxcx/query?purpose_codes=ADULT&queryDate=2015-06-04&from_station=SNH&to_station=CSQ";
        String json = HttpUtils.Get_https(search_url, 4800);
        System.out.println(json);
    }

}
