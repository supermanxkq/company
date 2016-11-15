package com.ccservice.b2b2c.atom.train;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.callback.PropertyUtil;
import com.ccservice.b2b2c.atom.server.Server;

public class TrainOrderInterfaceInfoMethod {
    /**
     * 获取易订行调用空铁系统使用的用户和KEY
     * 
     * @param agentid
     * @return
     * @time 2016年7月6日 上午10:15:16
     * @author fiend
     */
    public static JSONObject getInterfaceUser(long agentid) {
        JSONObject jsonObject = new JSONObject();
        boolean isFind = false;
        try {
            //去DB中查找配置的用户和KEY
            String sql = "select KongTieUser,KongTieKey from T_INTERFACEACCOUNT WITH (NOLOCK) WHERE C_AGENTID="
                    + agentid;
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                if (map.containsKey("KongTieUser") && !"".equals(map.get("KongTieUser").toString())
                        && map.containsKey("KongTieKey") && !"".equals(map.get("KongTieKey").toString())) {
                    jsonObject.put("partnerid", map.get("KongTieUser").toString());
                    jsonObject.put("partnerid_key", map.get("KongTieKey").toString());
                    isFind = true;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //如果DB中没有配置的用户和KEY，用配置文件的默认数据
        if (!isFind) {
            String username = PropertyUtil.getValue("partnerid", "Train.properties");
            String userkey = PropertyUtil.getValue("partnerid_key", "Train.properties");
            jsonObject.put("partnerid", username);
            jsonObject.put("partnerid_key", userkey);
        }
        return jsonObject;
    }
}
