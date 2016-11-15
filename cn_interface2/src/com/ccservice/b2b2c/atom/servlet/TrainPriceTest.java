package com.ccservice.b2b2c.atom.servlet;

import com.ccservice.b2b2c.atom.component.SendPostandGet;

public class TrainPriceTest {

    public static void main(String[] args) {
        String url = "http://localhost:9004/cn_interface/TrainPrice";
        String resultString = SendPostandGet.submitPost(url, "mcckey=1095_02_10&time=2015-08-01&price=111.1&zuoxi=硬卧下",
                "UTF-8").toString();
        System.out.println(resultString);
    }
}
