package com.ccservice.b2b2c.atom.servlet.chongdong;

import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class WormholeUtil {
    public static boolean checkTrainOrderIsWormhole(long orderid) {
        boolean falg = false;
        String sql = "EXEC [dbo].[sp_TrainOrderIsWormhole_select] @orderid=" + orderid;
        List list = new ArrayList();
        try {
            list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e) {
            WriteLog.write("判断订单是否使用虫洞接口_Error", sql);
            ExceptionUtil.writelogByException("判断订单是否使用虫洞接口_Error", e);
            e.printStackTrace();
        }
        if (list.size() > 0) {
            falg = true;
        }
        return falg;
    }

    public static boolean checkTrainOrderIsWormhole(String qunarnumber) {
        boolean falg = false;
        String sql = "EXEC [dbo].[sp_TrainOrderIsWormhole_select2] @orderid='" + qunarnumber + "'";
        List list = new ArrayList();
        try {
            list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e) {
            WriteLog.write("判断订单是否使用虫洞接口_Error", sql);
            ExceptionUtil.writelogByException("判断订单是否使用虫洞接口_Error", e);
            e.printStackTrace();
        }
        if (list.size() > 0) {
            falg = true;
        }
        return falg;
    }
}
