import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class test {
    public static void main(String[] args) {
        JSONArray returntickets = new JSONArray();
        JSONObject o1 = new JSONObject();
        o1.put("ticket_no", "E0171981081120001");
        o1.put("passengername", "%E9%A9%AC%E6%80%A1%E9%B8%BF");//马怡鸿
        o1.put("passporttypeseid", 1);
        o1.put("passportseno", "142601199312176316");
        o1.put("returnsuccess", true);
        o1.put("returnmoney", 26);
        o1.put("returntime", "2016-06-17 17:09:26");
        o1.put("returnfailid", "");
        o1.put("returnfailmsg", "");
        returntickets.add(o1);
        long currentTime = System.currentTimeMillis();
        String timestamp = String.valueOf(currentTime / 1000);
        try {
            String signs = ElongHotelInterfaceUtil.MD5("92l2w9s745is8djyh0hbpyfg8v812pvw");
            String key = ElongHotelInterfaceUtil.MD5("meituan0" + timestamp + "14657391460001E01719810826true" + signs);
            System.out.println(key);
            JSONObject obj = new JSONObject();
            obj.put("returntype", 0);
            obj.put("apiorderid", "14657391460001");
            obj.put("sign", key);
            obj.put("trainorderid", "E017198108");
            obj.put("reqtoken", "1466154566441");
            obj.put("returntickets", returntickets);
            obj.put("returnstate", true);
            obj.put("returnmoney", 26);
            obj.put("timestamp", timestamp);
            obj.put("returnmsg", "");
            System.out.println(obj.toString());
            String ret = SendPostandGet.submitPost("http://i.meituan.com/uts/train/agentht/returnticketnotify",
                    "data=" + obj.toString(), "utf-8").toString();
            System.out.println(ret);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
