package com.ccservice.b2b2c.atom.servlet.TongChengTrain; 

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 
 * 同程    关于    code  看他家心情 
 * 修改务必写入此方法       
 * @author RRRRRR
 * @time 2016年11月10日 下午1:18:51
 */
public class TongChengCodeSwitch {
    
    /**
     * 
     * @author RRRRRR
     * @time 2016年11月10日 下午2:57:28
     * @Description 同程新增code   变更到站不符合条件  1010
     * @param json
     * @return
     */
    public static JSONObject getNewCode(JSONObject json){
        try {
            String msg=json.containsKey("msg")?json.getString("msg"):"";
            String help_info=json.containsKey("help_info")?json.getString("help_info"):"";
            try {
                msg=URLDecoder.decode(msg, "utf-8");
                help_info=URLDecoder.decode(help_info, "utf-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(!ElongHotelInterfaceUtil.StringIsNull(msg)||!ElongHotelInterfaceUtil.StringIsNull(help_info)){
                if(msg.contains("您申请的新票到站不符合变更到站的条件")||help_info.contains("您申请的新票到站不符合变更到站的条件")){
                    json.put("code", "1010");
                    WriteLog.write("同程回调code转换", "转换后json:  "+json.toJSONString());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            WriteLog.write("同程回调code转换", "转换出神器:  "+json.toJSONString());
        }
        return json;
    }
}
