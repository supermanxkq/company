/**
 * 
 */
package test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

import client.BoangServiceImplServiceStub;
import client.BoangServiceImplServiceStub.PassengerInfoBean;

import com.ccservice.b2b2c.atom.server.Server;
import com.insurance.SunshineInsurance;

/**
 * 
 * @time 2016年6月6日 下午1:18:08
 * @author chendong
 */
public class SunshineInsuranceTest {
    public static void main(String[] args) {
        SunshineInsurance si = new SunshineInsurance();
        //        si.test();
    }

    /**
     *  说明：出保测试 
     * @time 2014年9月10日 下午5:01:33
     * @author yinshubin
     */
    public void test() {
        //        userId = Server.getInstance().getSunshineInsuranceEaccount().getUsername();
        //        sign = Server.getInstance().getSunshineInsuranceEaccount().getKeystr();
        //        clientType = Server.getInstance().getSunshineInsuranceEaccount().getEdesc();
        //        callbackurl = Server.getInstance().getSunshineInsuranceEaccount().getNourl();
        try {
//            BoangServiceImplServiceStub stub = new BoangServiceImplServiceStub();
//            OMElement header1 = AXIOMUtil.stringToOM("<authHeader><userName>" + userId + "</userName><passwd>" + sign
//                    + "</passwd></authHeader>");
//            stub._getServiceClient().addHeader(header1);
//            BoangServiceImplServiceStub.SubmitInsureReqE submitInsureReq = new BoangServiceImplServiceStub.SubmitInsureReqE();
//            BoangServiceImplServiceStub.SubmitInsureReq SubmitInsureReq = new BoangServiceImplServiceStub.SubmitInsureReq();
//            BoangServiceImplServiceStub.SendInfoBean SendInfoBean = new BoangServiceImplServiceStub.SendInfoBean();
//            BoangServiceImplServiceStub.CallBackInfoBean CallBackInfoBean = new BoangServiceImplServiceStub.CallBackInfoBean();
//            BoangServiceImplServiceStub.OrderInfoBean OrderInfoBean = new BoangServiceImplServiceStub.OrderInfoBean();
//            BoangServiceImplServiceStub.InsuranceInfoBean InsuranceInfoBean = new BoangServiceImplServiceStub.InsuranceInfoBean();
//            OrderInfoBean.setAircode("Aircode");
//            // 航班起飞时间，也即保险生效时间
//            OrderInfoBean.setStartDate("2014-09-15 12:00:00");
//            // 贵公司关联订单号
//            OrderInfoBean.setOrderId("201409151200");
//
//            // 客户端类型为1，表示接口
//            if (null == clientType || clientType.length() <= 0) {
//                clientType = "1";
//            }
//            CallBackInfoBean.setClientType(clientType);
//            if ("1".equals(clientType)) {
//                CallBackInfoBean.setCallback(callbackurl);
//                CallBackInfoBean.setUserId(userId);
//                CallBackInfoBean.setMerchantsign(sign);
//            }
//
//            // 产品编号:阳光人寿交通工具综合保险B款保险，保费20，保额605000，有效期7天，限购2份（即保险有效期内限购两份）
//            InsuranceInfoBean.setProductcode("BA-QP000640");
//
//            PassengerInfoBean passengerArray[] = new PassengerInfoBean[1];
//            BoangServiceImplServiceStub.PassengerInfoBean PassengerInfoBean = new BoangServiceImplServiceStub.PassengerInfoBean();
//            // 乘客证件号码
//            PassengerInfoBean.setInsuredidno("371426198910018883");
//            // 乘客证件类型：0: 身份证11: 户口薄12: 驾驶证13: 军官证14: 士兵证17: 港澳通行证18: 台湾通行证99: 其他51: 护照61: 港台同胞证
//            PassengerInfoBean.setInsuredidtype("10");
//            // 乘客生日
//            PassengerInfoBean.setInsuredbirthday("1989-10-01");
//            // 乘客手机号码：用于接收保单成功的短信，短信由阳光保险公司发送和保监会发送
//            PassengerInfoBean.setInsuredmobile("13918913183");
//            // 乘客姓名，注意转码，否则会乱码
//            PassengerInfoBean.setInsuredname(java.net.URLEncoder.encode("陈栋", "UTF-8"));
//            // 必填,只能选择1,2，否则会在阳光投保失败
//            PassengerInfoBean.setInsurednum("1");
//            // passengerInfo可以多个，目前网站限制9份，因为一般售票网站最多能一单出9张票，所以，建议此接口提交时最多9份，最少1份
//            passengerArray[0] = PassengerInfoBean;
//
//            SendInfoBean.setOrderInfoBean(OrderInfoBean);
//            SendInfoBean.setCallBackInfoBean(CallBackInfoBean);
//            SendInfoBean.setInsuranceInfoBean(InsuranceInfoBean);
//            SendInfoBean.setPassengerInfoList(passengerArray);
//            SubmitInsureReq.setArg0(SendInfoBean);
//            submitInsureReq.setSubmitInsureReq(SubmitInsureReq);
//
//            BoangServiceImplServiceStub.SubmitInsureReqResponseE SubmitInsureReqResponseE = stub
//                    .submitInsureReq(submitInsureReq);
//            BoangServiceImplServiceStub.SyncResponse SyncResponse = SubmitInsureReqResponseE
//                    .getSubmitInsureReqResponse().get_return();
//            System.out.println(SyncResponse.getCode());
//            System.out.println(SyncResponse.getDesc());
//            System.out.println(SyncResponse.getSuccess());
        }
        catch (Exception e) {
            System.out.println("接口异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *  说明：退保测试 
     * @time 2014年9月10日 下午5:01:33
     * @author yinshubin
     */
    public void refundTest() {
        try {
            BoangServiceImplServiceStub stub = new BoangServiceImplServiceStub();
            //            OMElement header1 = AXIOMUtil.stringToOM("<authHeader><userName>" + userId + "</userName><passwd>" + sign
            //                    + "</passwd></authHeader>");
            //            stub._getServiceClient().addHeader(header1);
            BoangServiceImplServiceStub.CancelInfoBean cancelInfoBean = new BoangServiceImplServiceStub.CancelInfoBean();
            BoangServiceImplServiceStub.CancelInsurance cancelInsurance = new BoangServiceImplServiceStub.CancelInsurance();
            BoangServiceImplServiceStub.CancelInsuranceE cancelInsuranceE = new BoangServiceImplServiceStub.CancelInsuranceE();
            BoangServiceImplServiceStub.SyncResponse syncResponse = new BoangServiceImplServiceStub.SyncResponse();
            // 退保原因控制在100字以内
            cancelInfoBean.setCancelReason(java.net.URLEncoder.encode("客人退票", "UTF-8"));
            // 客户端类型：网站接口
            cancelInfoBean.setClientType("1");
            // 用户名
            //            cancelInfoBean.setUserId(userId);
            // 保单号，必填，作为退保的唯一标识，且每次只能设置一个保单号
            cancelInfoBean.setPolicyno("BA201409111018060352");
            cancelInsurance.setArg0(cancelInfoBean);
            cancelInsuranceE.setCancelInsurance(cancelInsurance);
            BoangServiceImplServiceStub.CancelInsuranceResponseE cancelInsuranceResponseE = stub
                    .cancelInsurance(cancelInsuranceE);
            syncResponse = cancelInsuranceResponseE.getCancelInsuranceResponse().get_return();
            System.out.println(syncResponse.getCode());
            System.out.println(syncResponse.getDesc());
            System.out.println(syncResponse.getSuccess());
        }
        catch (Exception e) {
            System.out.println("接口异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

}
