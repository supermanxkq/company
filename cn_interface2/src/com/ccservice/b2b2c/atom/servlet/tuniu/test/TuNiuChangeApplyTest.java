package com.ccservice.b2b2c.atom.servlet.tuniu.test;

import java.util.UUID;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.SignUtil;

/**
 * 
 * 途牛请求改签测试
 */
public class TuNiuChangeApplyTest {

    public String account = "tuniu_basetest";

    public String key = "ix7xk7exkt4c7nd2u62254n51k2vnuzm";

    JSONObject jsonObject = new JSONObject();

    public static void main(String[] args) {
        TuNiuChangeApplyTest test = new TuNiuChangeApplyTest();
        System.out.println(test.qingqiugaiqian());
    }

    private String qingqiugaiqian() {
        jsonObject.put("account", account);
        String resultString = "";
        String changeIdNumber = UUID.randomUUID().toString();
        executepool(0, "8.0", "31404", "E8360050601150022", "1", "", "二代身份证", "140622199210264251", "硬座", "1", "杨荣强",
                "1", "成人票", "T1605171024180879859", "93e4ae77-1a0e-43b2-b5a8-ab14ddf794c6", "E836005060",
                changeIdNumber, "K7804", "2016-06-06 09:05:00", "1", "true", "1",
                "{tuniuURL}/train /changeOrderFeedback", "2016-05-09 11:28:29");
        jsonObject.put("sign", SignUtil.generateSign(jsonObject.toString(), key));
        //        String jsonString = "{\"sign\":\"eac24a7dc468249c778656738db9b08f\",\"timestamp\":\"2016-05-07 08:09:12\",\"returnCode\":100,\"data\":\"yVqlEOdqkdHA6svYwvrBQzgRLzStvug4oQfuRKshlMoEeopipJ8nMzO-ZUgSvrsOwOrL2ML6wUM4s3ANv6BhXCurL52UM_nNpMAPSt-ELUEk_dUkiQrhYSge7CryByLqm5-59HEc30lrNG-p-WkJSDutB1VYufsEzqIii12ZPE6FXfQjxKhzc2_Rw0Kc7XFgspez_fE6SCJfn7bii4cqz6ztn_teW9fvlNGDczISPfOYDxZqlI9Ap1POqWbwMcS7m3lSvQFFewXLJYjgLIgblhiEe5cQ-_x9QTjT2ggWXOL_o2V_OFNYySobV9SJaFO-9gLGsE8lOW4neEfLqlXyIXSinwVyNCZAv9wD3UpY_bNQAohPsL0677X7bfmle5JtMGFNjRtuGe5kwEYvg-5GwvQOsv29je4MtvvJYWDSB8PjlYVLD49uC3ourUF_a3h8uVRzvEM4pv2g8daRBpQTk8zX8bSRg7kBVNcZd19KcpHOa-d5V0yYbmFcfK8glncdcyZ6TUUats_NPZhdz6guaMyfRKqnYZv4sjkNWMzhHuOuGaofFa6LQy4DZZ66SpBTIEaKJ9mQI4VRfmyiSjMcW0qgVC-yRb6ifhcYW0LJQ_hsVTIzSx6pAOkKcodCG1t5uVRzvEM4pv31z2Kl0lFap8Dn2fa6gAtZrffJk3IG5OyfYC07qzUyn5ul2qZ1nDFUxUKga3TR0x68KnIBu4MH8r9nfeG8g10YsgCMsN_LPXrk6D9gDXRz5tWZUzV0nWdgBq_Xzdol4kuL61fMiNSDjk3nOwiFknWSL3c7wqi0RLK8VSlyCb1LqZXai370bl9D2NxGimfXba1hC117pe42yCEJ45_uEKGsiJYp4W8LhPkZgw_HBS8zDnN907RPQIqUkPm35aNLYcCRSL0mZ-Q5IkRFPgihpU-K6q0JgaTk0spHKABMgrIC3dnM1LjT7MIDWHsggYSOi0fKES1VKqcjI5yizHYfOH59kCgZak-_IkSUmdZEsa1VR_sX0Ixni9-E8AZ7lGSnqbM\",\"account\":\"-1\",\"errorMsg\":\"处理或操作成功\"}";

        resultString = SendPostandGet.submitGet2(
                "http://120.26.83.131:9022/cn_interface/TuNiuTraintrainAccountChangeApplyServlet",
                jsonObject.toString(), "UTF-8");
        return resultString;
    }

    private void executepool(int reason, String price, String passengerId, String oldTicketNo, String zwCode,
            String cxin, String passportTypeName, String passportNo, String zwName, String piaoType,
            String passengerName, String passportTypeId, String piaoTypeName, String vendorOrderId, String orderId,
            String orderNumber, String changeId, String changeCheCi, String changeDateTime, String changeZwCode,
            String hasSeat, String oldZwCode, String callBackUrl, String timestamp) {
        JSONObject oldTicketInfos = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        oldTicketInfos.put("reason", reason);
        oldTicketInfos.put("price", price);
        oldTicketInfos.put("passengerId", passengerId);
        oldTicketInfos.put("oldTicketNo", oldTicketNo);
        oldTicketInfos.put("zwCode", zwCode);
        oldTicketInfos.put("cxin", cxin);
        oldTicketInfos.put("passportTypeName", passportTypeName);
        oldTicketInfos.put("passportNo", passportNo);
        oldTicketInfos.put("zwName", zwName);
        oldTicketInfos.put("piaoType", piaoType);
        oldTicketInfos.put("passengerName", passengerName);
        oldTicketInfos.put("passportTypeId", passportTypeId);
        oldTicketInfos.put("piaoTypeName", piaoTypeName);
        jsonArray.add(oldTicketInfos);
        data.put("oldTicketInfos", jsonArray);
        data.put("vendorOrderId", vendorOrderId);
        data.put("orderId", orderId);
        data.put("orderNumber", orderNumber);
        data.put("changeId", changeId);
        data.put("changeCheCi", changeCheCi);
        data.put("changeDateTime", changeDateTime);
        data.put("changeZwCode", changeZwCode);
        data.put("hasSeat", hasSeat);
        data.put("oldZwCode", oldZwCode);
        data.put("callBackUrl", callBackUrl);
        try {
            String datas = TuNiuDesUtil.encrypt(data.toString());
            jsonObject.put("data", datas);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        jsonObject.put("timestamp", timestamp);
    }
}
