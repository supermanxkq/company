package com.ccservice.b2b2c.atom.service.jltour;

import java.net.URLEncoder;
import com.ccservice.b2b2c.atom.qunar.PHUtil;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 异步更新淘宝数据 
 * @author WH
 */

public class SyncUpdateTaoBaoData extends Thread {
    private String json;

    public SyncUpdateTaoBaoData(String json) {
        this.json = json;
    }

    public void run() {
        String url = PropertyUtil.getValue("TaoBaoUpdateUrl");
        if (!ElongHotelInterfaceUtil.StringIsNull(json) && !ElongHotelInterfaceUtil.StringIsNull(url)) {
            try {
                PHUtil.submitPost(url, URLEncoder.encode(json, "utf-8"));
            }
            catch (Exception e) {
            }
        }
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}