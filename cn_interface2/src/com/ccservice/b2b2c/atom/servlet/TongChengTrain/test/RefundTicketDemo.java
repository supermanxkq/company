package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class RefundTicketDemo {

    public static void main(String[] args) throws Exception {
        //new NumRefund(4, "2015122521001004340075982526").start();
    }

}

class NumRefund extends Thread {

    private int type;

    private String nums;

    private int threadNum = 5;

    public NumRefund(int type, String nums) {
        this.type = type;
        this.nums = nums;
    }

    public void run() {
        if (type == 3) {
            tc(nums);
        }
        else if (type == 4) {
            tn(nums);
        }
    }

    private void tc(String nums) {
        String[] array = nums.split("@");
        ExecutorService pool = Executors.newFixedThreadPool(threadNum);
        String url = "http://121.199.25.199:9026/cn_interface/AliRefundPrice.jsp?payTradeNos=";
        for (int i = 0; i < array.length; i++) {
            String num = array[i];
            pool.execute(new ReqRefund(i, num, url + num));
        }
        pool.shutdown();
    }

    private void tn(String nums) {
        String[] array = nums.split("@");
        ExecutorService pool = Executors.newFixedThreadPool(threadNum);
        String url = "http://120.26.100.206:9001/cn_interface/AliRefundPrice.jsp?payTradeNos=";
        for (int i = 0; i < array.length; i++) {
            String num = array[i];
            pool.execute(new ReqRefund(i, num, url + num));
        }
        pool.shutdown();
    }

}

class ReqRefund extends Thread {

    private int i;

    private String num;

    private String url;

    public ReqRefund(int i, String num, String url) {
        this.i = i;
        this.num = num;
        this.url = url;
    }

    public void run() {
        String result = i + "--->" + ElongHotelInterfaceUtil.getCurrentTime() + "--->"
                + SendPostandGet.submitGet(url, "UTF-8") + "--->" + ElongHotelInterfaceUtil.getCurrentTime() + "--->"
                + num;
        System.out.println(result);
    }
}