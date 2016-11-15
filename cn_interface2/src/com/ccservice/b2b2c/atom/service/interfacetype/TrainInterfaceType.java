package com.ccservice.b2b2c.atom.service.interfacetype;

import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.hotel.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;

/**
 * B2B接口类型 1-HTHY 2-QUNAR 3-TONGCHENG 
 * @time 2015年3月17日 上午10:17:31
 * @author fiend
 */
public class TrainInterfaceType implements ITrainInterfaceTypeService {

    @SuppressWarnings("rawtypes")
    @Override
    public int getTrainInterfaceType(long trainorderid) {
        try {
            long C_AGENTID = 0;
            int r1 = (int) (Math.random() * 10000);
            String sql = "SELECT TOP 1 ISNULL(C_INTERFACETYPE, 0) C_INTERFACETYPE, ISNULL(C_AGENTID, 0) C_AGENTID "
                    + "FROM T_TRAINORDER WITH (NOLOCK) WHERE ID=" + trainorderid;
            WriteLog.write("接口用户判断", r1 + "--->" + sql);
            List list1 = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list1 != null && list1.size() > 0) {
                Map map = (Map) list1.get(0);
                //接口类型
                String C_INTERFACETYPE = map.get("C_INTERFACETYPE").toString();
                if (!"0".equals(C_INTERFACETYPE)) {
                    return Integer.valueOf(C_INTERFACETYPE);
                }
                else {
                    C_AGENTID = Long.parseLong(map.get("C_AGENTID").toString());
                }
            }

            String sql1 = "SELECT C_INTERFACETYPE FROM T_INTERFACEACCOUNT WITH (NOLOCK) WHERE C_AGENTID = " + C_AGENTID;
            WriteLog.write("接口用户判断", r1 + "--->" + sql1);
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql1, null);
            WriteLog.write("接口用户判断", r1 + "--->" + list.size());
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                WriteLog.write("接口用户判断", r1 + "--->" + map.get("C_INTERFACETYPE").toString());
                return Integer.valueOf(map.get("C_INTERFACETYPE").toString());
            }
            else {
                return 1;
            }
        }
        catch (Exception e) {
            return 1;
        }
    }

}
