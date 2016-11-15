package com.ccservice.b2b2c.atom.pay.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        DateFormat f = new SimpleDateFormat("MM月dd日");
        try {
            f.parse("2011-08-02");
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(f.format(new Date(System.currentTimeMillis())));

    }

}
