package com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread;

import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;

public class UpdatekeyThread extends Thread {
    int i;

    List<String> clist;

    public UpdatekeyThread(List<String> clist, int i) {
        this.clist = clist;
        this.i = i;
    }

    @Override
    public void run() {
        for (int j = 0; j < clist.size(); j++) {
            String liechecode = clist.get(j);
            String[] liechecodes = liechecode.split("_");
            if (liechecodes.length == 4) {
                try {
                    String newmcckey = liechecodes[0] + "_" + liechecodes[1] + "_" + liechecodes[2];
                    String sql = "update T_TRAINPRICE set C_MCCKEY='" + newmcckey + "' where C_MCCKEY='" + liechecode
                            + "'";
                    System.out.println(i + "————" + j + "————" + newmcckey);
                    Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                }
                catch (Exception e) {
                }
            }
        }
    }

}
