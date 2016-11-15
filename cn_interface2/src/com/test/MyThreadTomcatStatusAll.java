package com.test;

import java.util.Set;

public class MyThreadTomcatStatusAll extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                Set<Integer> set = TomcatStatusMem.getTomcatStatusMethods().keySet();
                for (Integer i : set) {
                    new MyThreadTomcatStatus(i, TomcatStatusMem.getTomcatStatusMethods().get(i)).start();
                }
            }
            catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
