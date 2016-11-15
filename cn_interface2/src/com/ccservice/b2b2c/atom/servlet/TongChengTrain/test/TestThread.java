package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;
public class TestThread extends Thread {
    String orderid;


    @Override
    public void run() {
        super.run();
        //        Long l1 = System.currentTimeMillis();
        //        try {
        //            Thread.sleep(200);
        //        }
        //        catch (InterruptedException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        
        //        for (int i = 0; i < 2; i++) {
            TestRepSearch.testTBRepSearch("114.215.240.84:8080");
            //            System.out.println(11111);

        //        }

        //        System.out.println(this.orderid + ":" + (System.currentTimeMillis() - l1));

    }
}
