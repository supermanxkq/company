/**
 * 
 */
package com.ccservice.b2b2c.atom.taobao.train;

import com.ccservice.elong.inter.PropertyUtil;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;

/**
 * 
 * @time 2015年10月27日 下午5:10:03
 * @author chendong
 */
public class TaobaoAccountSession {
    public static void main(String[] args) {
        String url = PropertyUtil.getValue("url", "Train.taobao.properties");
        String appkey = PropertyUtil.getValue("appkey", "Train.taobao.properties");
        String appSecret = PropertyUtil.getValue("appSecret", "Train.taobao.properties");
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
    }
}
