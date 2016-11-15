package com.test;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.server.Server;

public class TomcatStatusMem {
    private static Hashtable<Integer, TomcatStatusMethod> tomcatStatusMethods = new Hashtable<Integer, TomcatStatusMethod>();

    public static void init() {
        String sql = "[sp_TomcatStatusMethod_Select]";
        try {
            List list = Server.getInstance().getSystemService().findMapResultByProcedure(sql);
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                tomcatStatusMethods.put(i, new TomcatStatusMethod(map.get("TomcatUrl").toString(), map
                        .get("TomcatName").toString()));
            }
        }
        catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            new MyThreadTomcatStatusAll().start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Hashtable<Integer, TomcatStatusMethod> getTomcatStatusMethods() {
        return tomcatStatusMethods;
    }

    public static void setTomcatStatusMethods(Hashtable<Integer, TomcatStatusMethod> tomcatStatusMethods) {
        TomcatStatusMem.tomcatStatusMethods = tomcatStatusMethods;
    }

}
