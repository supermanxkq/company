package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.util.concurrent.Callable;

public class TestThread2Thread implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        Integer result = 1;
        for (int i = 0; i < 100; i++) {
            Thread.sleep(100L);
            System.out.println("TestThread2Thread:" + i);
            result = i;
        }
        return result;
    }
}
