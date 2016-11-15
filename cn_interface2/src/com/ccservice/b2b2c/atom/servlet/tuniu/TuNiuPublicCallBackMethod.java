package com.ccservice.b2b2c.atom.servlet.tuniu; 

import com.ccservice.elong.inter.PropertyUtil;

/**
 * 
 * 途牛异步回调公共方法
 * @author RRRRRR
 * @time 2016年10月31日 下午5:22:02
 */
public class TuNiuPublicCallBackMethod {
    
    public static final String tuNiuInterfaceV2 = PropertyUtil.getValue("tuNiuInterfaceV2",
            "Train.properties");
    
    public static final String tuNiuPartnerId= PropertyUtil.getValue("tuNiuPartnerId",
            "Train.properties");
    
}
