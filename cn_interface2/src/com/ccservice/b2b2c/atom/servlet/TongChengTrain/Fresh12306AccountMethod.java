package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.account.dubbo.util.DubboConsumer;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class Fresh12306AccountMethod extends TongchengSupplyMethod {
    /**
     * 刷新账号乘客数量
     * @param reqobj 下单结果
     * @param customeruser 下单账号
     */
    public void refreshAccountPassenger(JSONObject repobj, Customeruser customeruser) {
        try {
            String accountName = customeruser.getLoginname();
            //乘客数量
            int accountPassengerCount = repobj.getIntValue("accountPassengerCount");
            //非客人账号、乘客数量大于0
            if (!customeruser.isCustomerAccount() && accountPassengerCount > 0
                    && !ElongHotelInterfaceUtil.StringIsNull(accountName)) {
                //刷新乘客
                JSONObject data = AccountSystemParam("RefreshAccount");
                //请求JSON
                data.put("name", accountName);
                data.put("count", accountPassengerCount);
                data.put("normal_passengers", repobj.getJSONArray("normal_passengers"));
                //Dubbo
                DubboConsumer.getInstance().getDubboAccount().refresh12306Account(data);
                /*
                //请求参数
                String param = "param=" + data.toJSONString();
                //请求地址
                String url = GetAccountSystemUrl();
                //地址非空
                if (!ElongHotelInterfaceUtil.StringIsNull(url)) {
                    RequestUtil.post(url, param, "UTF-8", new HashMap<String, String>(), 5000);
                }
                */
            }
        }
        catch (Exception e) {

        }
    }
}
