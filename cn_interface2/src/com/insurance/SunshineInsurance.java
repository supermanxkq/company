package com.insurance;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import client.BoangServiceImplServiceStub;
import client.BoangServiceImplServiceStub.PassengerInfoBean;
import client.TrafficPolicyStub;

import com.ccservice.b2b2c.atom.component.PublicComponent;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.util.Insurances;

/**
 * 阳光保险实现方法
 * 测试类已经 移动到 SunshineInsuranceTest
 * 
 * 
 * 
 * @time 2014年9月9日 上午10:00:50
 * @author chendong
 */
public class SunshineInsurance extends PublicComponent implements IInsuranceBook {
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    /**
     * 
     * @param userId
     * @param sign
     * @param clientType
     * @param callbackurl
     * @param productcode
     */
    public SunshineInsurance(String userId, String sign, String clientType, String callbackurl, String productcode) {
        super();
        this.userId = userId;
        this.sign = sign;
        this.clientType = clientType;
        this.callbackurl = callbackurl;
        Productcode = productcode;
    }

    public SunshineInsurance() {
        super();
    }

    /**
     * @see 客户id
     */
    //    private static String userId = "1000021";
    private String userId;

    /**
     * @see 密钥
     */
    //    private static String sign = "e10adc3949ba59abbe56e057f20f883e";
    private String sign;

    /**
     * @see 客户端类型
     */
    //    private static String clientType = "1";
    private String clientType;

    /**
     * @see 客户回调地址
     */
    //    private static String callbackurl = "http://211.103.207.133:8080/cn_home/sunshinecallback";
    private String callbackurl;

    /**
     * 产品代码
     */
    private String Productcode;

    @Override
    public List<Insurances> orderAplylist(String jyNo, Customeruser user, List list, String begintime, String[] fltno)
            throws Exception {

        return null;
    }

    @Override
    public DataHandler PolicyReprint(Insurorder order) throws Exception {
        return null;
    }

    /**
     * 阳光保险    出保
     */
    @Override
    public List newOrderAplylist(String[] jyNo, List list) {
        userId = Server.getInstance().getSunshineInsuranceEaccount().getUsername();
        sign = Server.getInstance().getSunshineInsuranceEaccount().getKeystr();
        clientType = Server.getInstance().getSunshineInsuranceEaccount().getEdesc();
        callbackurl = Server.getInstance().getSunshineInsuranceEaccount().getNourl();
        WriteLog.write("sunshineInsurance", userId + ":" + sign + ":" + clientType + ":" + callbackurl);
        try {
            for (int i = 0; i < list.size(); i++) {
                Insuruser insuruser = (Insuruser) list.get(i);
                this.Productcode = "BA-QP000640";
                insuruser = SunshineInsuranceSubmitInsureReq(insuruser);
            }
        }
        catch (Exception e) {
            WriteLog.write("sunshineInsurance", "接口异常:" + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 新华保险投保
     * @time 2016年6月6日 下午12:50:05
     * @author chendong
     * @param insuruser 
     * @throws XMLStreamException 
     * @throws AxisFault 
     */
    public Insuruser SunshineInsuranceSubmitInsureReq(Insuruser insuruser) throws XMLStreamException, AxisFault {
        BoangServiceImplServiceStub stub = new BoangServiceImplServiceStub();
        OMElement header1 = AXIOMUtil.stringToOM("<authHeader><userName>" + this.userId + "</userName><passwd>" + sign
                + "</passwd></authHeader>");
        stub._getServiceClient().addHeader(header1);
        BoangServiceImplServiceStub.SubmitInsureReqE submitInsureReqE = new BoangServiceImplServiceStub.SubmitInsureReqE();
        BoangServiceImplServiceStub.SubmitInsureReq submitInsureReq = new BoangServiceImplServiceStub.SubmitInsureReq();
        BoangServiceImplServiceStub.SendInfoBean SendInfoBean = new BoangServiceImplServiceStub.SendInfoBean();
        BoangServiceImplServiceStub.SyncResponse syncResponse = new BoangServiceImplServiceStub.SyncResponse();
        BoangServiceImplServiceStub.InsuranceInfoBean InsuranceInfoBean = new BoangServiceImplServiceStub.InsuranceInfoBean();
        BoangServiceImplServiceStub.CallBackInfoBean CallBackInfoBean = new BoangServiceImplServiceStub.CallBackInfoBean();
        BoangServiceImplServiceStub.PassengerInfoBean PassengerInfoBean = new BoangServiceImplServiceStub.PassengerInfoBean();
        BoangServiceImplServiceStub.OrderInfoBean OrderInfoBean = new BoangServiceImplServiceStub.OrderInfoBean();
        OrderInfoBean.setAircode(insuruser.getFlyno());
        // 航班起飞时间，也即保险生效时间
        OrderInfoBean.setStartDate(changeDate(insuruser.getFlytime()));
        // 贵公司关联订单号
        OrderInfoBean.setOrderId(insuruser.getId() + "");

        // 客户端类型为1，表示接口
        if (null == this.clientType || this.clientType.length() <= 0) {
            this.clientType = "1";
        }
        CallBackInfoBean.setClientType(this.clientType);
        if ("1".equals(this.clientType)) {
            CallBackInfoBean.setCallback(this.callbackurl);
            CallBackInfoBean.setUserId(this.userId);
            CallBackInfoBean.setMerchantsign(this.sign);
        }
        //"BA-QP000640" 产品编号:阳光人寿交通工具综合保险B款保险，保费20，保额605000，有效期7天，限购2份（即保险有效期内限购两份）
        InsuranceInfoBean.setProductcode(this.Productcode);
        PassengerInfoBean passengerArray[] = new PassengerInfoBean[1];
        // 乘客证件号码
        PassengerInfoBean.setInsuredidno(insuruser.getCode());
        // 乘客证件类型：0: 身份证11: 户口薄12: 驾驶证13: 军官证14: 士兵证17: 港澳通行证18: 台湾通行证99: 其他51: 护照61: 港台同胞证
        PassengerInfoBean.setInsuredidtype(getcodetype(insuruser.getCodetype()));
        SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
        String birthday = "NULL";
        try {
            birthday = yyyy_MM_dd.format(new Date(insuruser.getBirthday().getTime()));
        }
        catch (Exception e) {
        }
        // 乘客生日
        PassengerInfoBean.setInsuredbirthday(birthday);
        // 乘客手机号码：用于接收保单成功的短信，短信由阳光保险公司发送和保监会发送
        if (insuruser.getMobile() != null && !"".equals(insuruser.getMobile())) {
            PassengerInfoBean.setInsuredmobile(insuruser.getMobile());
        }
        else {
            PassengerInfoBean.setInsuredmobile("13439311111");
        }
        // 乘客姓名，注意转码，否则会乱码
        try {
            PassengerInfoBean.setInsuredname(java.net.URLEncoder.encode(insuruser.getName(), "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 必填,只能选择1,2，否则会在阳光投保失败
        PassengerInfoBean.setInsurednum("1");
        // passengerInfo可以多个，目前网站限制9份，因为一般售票网站最多能一单出9张票，所以，建议此接口提交时最多9份，最少1份
        passengerArray[0] = PassengerInfoBean;
        SendInfoBean.setOrderInfoBean(OrderInfoBean);
        SendInfoBean.setCallBackInfoBean(CallBackInfoBean);
        SendInfoBean.setInsuranceInfoBean(InsuranceInfoBean);
        SendInfoBean.setPassengerInfoList(passengerArray);
        submitInsureReq.setArg0(SendInfoBean);
        submitInsureReqE.setSubmitInsureReq(submitInsureReq);
        //            submitInsureReq.setSubmitInsureReq(param);
        BoangServiceImplServiceStub.SubmitInsureReqResponseE submitInsureReqResponseE = null;
        try {
            submitInsureReqResponseE = stub.submitInsureReq(submitInsureReqE);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        syncResponse = submitInsureReqResponseE.getSubmitInsureReqResponse().get_return();
        WriteLog.write("sunshineInsurance", insuruser.getId() + ":Productcode:" + this.Productcode);
        WriteLog.write("sunshineInsurance", insuruser.getId() + ":Code:" + insuruser.getCode() + ":Codetype:"
                + insuruser.getCodetype() + ":birthday:" + birthday + ":Mobile:" + insuruser.getMobile() + ":Name:"
                + insuruser.getName());
        if (!syncResponse.getSuccess()) {
            insuruser.setInsurstatus(2);
            insuruser.setRemark(insuruser.getRemark() == null ? "" : insuruser.getRemark() + "|"
                    + syncResponse.getDesc());
            WriteLog.write("sunshineInsurance", insuruser.getId() + ":出保失败:" + syncResponse.getDesc());
        }
        else {
            WriteLog.write("sunshineInsurance", insuruser.getId() + ":出保中:" + syncResponse.getDesc());
            insuruser.setInsurstatus(5);//投保中
            //TODO 因为是异步获取投保结果，所以存为0
        }
        return insuruser;
    }

    @Override
    public List saveTrainOrderAplylist(String[] jyNo, List list, int type) {
        String PosCode = getSysconfigString("newchinaPosCode");
        String SellFormType = getSysconfigString("newchinaSellFormType");
        List<Insuruser> insurs = new ArrayList<Insuruser>();
        String ProductCode = "66368079";
        if (type == 1) {
            ProductCode = "66368080";
        }
        else if (type == 2) {
            ProductCode = "66368079";
        }
        else if (type == 3) {
            ProductCode = "66368082";
        }
        else if (type == 4) {
            ProductCode = "66368081";
        }
        //ProductCode="66368074";
        List listResult = new ArrayList();
        String DepartureCity = "";
        String ArrivalCity = "";
        List<String> policynos = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            Insuruser insuruser = (Insuruser) list.get(i);
            try {
                String TransID = getTransID(insuruser);
                insuruser.setExtorderno(TransID);
                insuruser.setPaystatus(1);
                TrafficPolicyStub stub = new TrafficPolicyStub();
                TrafficPolicyStub.SavePolicyInfo savePolicyInfo = new TrafficPolicyStub.SavePolicyInfo();
                String CardType = getcodetype(insuruser.getCodetype());
                String flytime = insuruser.getFlytime().toString().substring(0, 10).replaceAll("-", "");
                if (ischild(insuruser.getBirthday().getTime())) {
                    if (type == 2) {
                        ProductCode = "66368080";
                    }
                    if (type == 4) {
                        ProductCode = "66368082";
                    }
                }
                String requestxml = "<?xml version=\"1.0\" encoding=\"GBK\"?><ApplyRequest><TransData><TransID>"
                        + TransID + "</TransID>" + "<MerchantNo>hthy</MerchantNo><TransDate>" + flytime
                        + "</TransDate><TransTime>000000</TransTime><OrderID>" + insuruser.getOrdernum() + "</OrderID>"
                        + "<PosCode>" + PosCode + "</PosCode><SellFormType>" + SellFormType
                        + "</SellFormType><ProductCode>" + ProductCode + "</ProductCode><EffectDate>" + flytime
                        + "</EffectDate>" + "<EffectTime>000000</EffectTime><ExpiryDate>" + flytime
                        + "</ExpiryDate><ExpiryTime>000000</ExpiryTime><OwnerName>" + insuruser.getName()
                        + "</OwnerName><CardType>" + CardType + "</CardType>" + "<CardNo>" + insuruser.getCode()
                        + "</CardNo><BirthDate>" + insuruser.getBirthday().toString().substring(0, 10)
                        + "</BirthDate><Mobile>" + insuruser.getMobile()
                        + "</Mobile><NeedSMS>T</NeedSMS><Email></Email>" + "<InsuranceNum>1</InsuranceNum><FlightNo>"
                        + insuruser.getFlyno() + "</FlightNo><DepartureCity>" + DepartureCity
                        + "</DepartureCity><DepartureDate>" + flytime + "</DepartureDate>"
                        + "<DepartureTime>000000</DepartureTime><ArrivalCity>" + ArrivalCity
                        + "</ArrivalCity><ArrivalDate>" + flytime + "</ArrivalDate><ArrivalTime>000000</ArrivalTime>"
                        + "<Extend1 /></TransData><TransSignature /></ApplyRequest>";
                WriteLog.write("新华保险", "0:" + requestxml);
                savePolicyInfo.setIn0(requestxml);
                TrafficPolicyStub.SavePolicyInfoResponse response = stub.savePolicyInfo(savePolicyInfo);
                String xml = response.getOut();
                WriteLog.write("新华保险", "1:" + xml);
                String ResultStatus = getXMLMessageNodeValue(xml, "ResultStatus");
                String ResultMsg = getXMLMessageNodeValue(xml, "ResultMsg");
                System.out.println(ResultMsg);
                String InsuranceNo = getXMLMessageNodeValue(xml, "InsuranceNo");
                if ("00".equals(ResultStatus)) {
                    insuruser.setPolicyno(InsuranceNo);
                    insuruser.setInsurstatus(1);
                }
                else {
                    insuruser.setRemark(insuruser.getRemark() == null ? "" : insuruser.getRemark() + ResultMsg);
                }

            }
            catch (AxisFault e) {
                e.printStackTrace();
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            listResult.add(insuruser);
        }
        return listResult;
    }

    /**
     * 退保
     */
    @Override
    public String cancelOrderAplylist(Insuruser insur) {
        this.userId = Server.getInstance().getSunshineInsuranceEaccount().getUsername();
        this.sign = Server.getInstance().getSunshineInsuranceEaccount().getKeystr();
        this.clientType = Server.getInstance().getSunshineInsuranceEaccount().getEdesc();
        this.callbackurl = Server.getInstance().getSunshineInsuranceEaccount().getNourl();
        try {
            insur = SunshineInsuranceCancelInsurance(insur);

        }
        catch (Exception e) {
            WriteLog.write("sunshineInsurance", insur.getId() + "接口异常：" + e.getMessage());
            e.printStackTrace();
        }
        return insur.getInsurstatus() + "," + insur.getRemark();
    }

    /**
     * 阳光保险取消投保
     * @param insur
     * @time 2016年6月6日 下午1:11:14
     * @author chendong
     * @throws XMLStreamException 
     * @throws RemoteException 
     */
    private Insuruser SunshineInsuranceCancelInsurance(Insuruser insur) throws XMLStreamException, RemoteException {
        BoangServiceImplServiceStub stub = new BoangServiceImplServiceStub();
        OMElement header1 = AXIOMUtil.stringToOM("<authHeader><userName>" + userId + "</userName><passwd>" + sign
                + "</passwd></authHeader>");
        stub._getServiceClient().addHeader(header1);
        BoangServiceImplServiceStub.CancelInfoBean cancelInfoBean = new BoangServiceImplServiceStub.CancelInfoBean();
        BoangServiceImplServiceStub.CancelInsurance cancelInsurance = new BoangServiceImplServiceStub.CancelInsurance();
        BoangServiceImplServiceStub.CancelInsuranceE cancelInsuranceE = new BoangServiceImplServiceStub.CancelInsuranceE();
        BoangServiceImplServiceStub.SyncResponse syncResponse = new BoangServiceImplServiceStub.SyncResponse();

        try {// 退保原因控制在100字以内
            cancelInfoBean.setCancelReason(java.net.URLEncoder.encode("客人退票", "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 客户端类型：网站接口
        cancelInfoBean.setClientType(this.clientType);
        // 用户名
        cancelInfoBean.setUserId(userId);
        // 保单号，必填，作为退保的唯一标识，且每次只能设置一个保单号
        cancelInfoBean.setPolicyno(insur.getPolicyno());
        cancelInsurance.setArg0(cancelInfoBean);
        cancelInsuranceE.setCancelInsurance(cancelInsurance);
        BoangServiceImplServiceStub.CancelInsuranceResponseE cancelInsuranceResponseE = stub
                .cancelInsurance(cancelInsuranceE);
        syncResponse = cancelInsuranceResponseE.getCancelInsuranceResponse().get_return();
        if (!syncResponse.getSuccess()) {
            insur.setRemark(insur.getRemark() == null ? "" : insur.getRemark() + syncResponse.getDesc());
            WriteLog.write("sunshineInsurance", insur.getId() + "退保失败" + syncResponse.getDesc());
        }
        else {
            insur.setInsurstatus(4);
            WriteLog.write("sunshineInsurance", insur.getId() + "退保成功");
        }
        return insur;
    }

    /**
     *  将时间戳改为字符串 
     * @param timestamp
     * @return
     * @time 2014年9月9日 下午3:29:31
     * @author yinshubin
     */
    public String changeDate(Timestamp timestamp) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(timestamp);
    }

    /**
     *  数据库证件类型转换成保险接口证件类型
     * @param timestamp
     * @return
     * @time 2014年9月9日 下午3:29:31
     * @author yinshubin
     */
    private String getcodetype(long codetype) {
        //        乘客证件类型：10: 身份证11: 户口薄12: 驾驶证13: 军官证14: 士兵证17: 港澳通行证18: 台湾通行证99: 其他51: 护照61: 港台同胞证
        String CardType = "99";
        if (codetype == 3) {//护照
            CardType = "51";
        }
        else if (codetype == 1) {//身份证
            CardType = "10";
        }
        else if (codetype == 4) {//港澳通行证
            CardType = "17";
        }
        else if (codetype == 5) {//台湾通行证
            CardType = "18";
        }
        else if (codetype == 6) {//台胞证
            CardType = "99";
        }
        else if (codetype == 7) {//回乡证
            CardType = "99";
        }
        else if (codetype == 8) {//军官证
            CardType = "13";
        }
        else {
            CardType = "99";
        }
        return CardType;
    }

    /**
     * 从报文中截取指定名称的节点值（不包含节点本身）
     * 
     * @param message
     *            报文文本
     * @param nodeName
     *            节点名
     * @return 节点值
     */
    private String getXMLMessageNodeValue(String message, String nodeName) {
        String nodeXML = getXMLMessageByNodeName(message, nodeName);
        int startIdx = nodeXML.indexOf('>') + 1;
        int endIdx = nodeXML.lastIndexOf('<');
        if (startIdx == -1 || endIdx == -1) {
            throw new RuntimeException("找不到对应的节点名：" + nodeName);
        }
        else {
            return nodeXML.substring(startIdx, endIdx);
        }
    }

    /**
     * 从报文中截取指定名称的节点内容（包含节点本身）
     * 
     * @param message
     *            报文文本
     * @param nodeName
     *            节点名
     * @return 节点内容
     */
    private String getXMLMessageByNodeName(String message, String nodeName) {
        String openNode = "<" + nodeName + ">";
        String closeNode = "</" + nodeName + ">";
        int startIdx = message.indexOf(openNode);
        int endIdx = message.indexOf(closeNode) + closeNode.length();
        if (startIdx == -1 || endIdx == -1) {
            throw new RuntimeException("找不到对应的节点名：" + nodeName);
        }
        else {
            return message.substring(startIdx, endIdx);
        }
    }

    /**
     * 此方法获得新华保险的交易流水号事先分配好的放到T_B2BSEQUENCE的xinhuaTransID中
     * @return
     */
    public String getTransID(Insuruser insuruser) {
        if (insuruser.getExtorderno() == null) {
            String sqlisstar = "SELECT C_VALUE AS XINHUATRANSID FROM T_B2BSEQUENCE WHERE C_NAME='xinhuaTransID'";
            // 1 表示正在更新 0表示更新完了
            List isstar = Server.getInstance().getSystemService().findMapResultBySql(sqlisstar, null);
            if (isstar.size() > 0) {
                Map map = (Map) isstar.get(0);
                String XINHUATRANSID = map.get("XINHUATRANSID").toString();
                try {
                    sqlisstar = "UPDATE T_B2BSEQUENCE SET C_VALUE = '" + (Long.parseLong(XINHUATRANSID) + 1)
                            + "' WHERE C_NAME='xinhuaTransID'";
                    Server.getInstance().getSystemService().excuteSysconfigBySql(sqlisstar);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
                return "0" + XINHUATRANSID;
            }
            else {
                return "-1";
            }
        }
        else {
            return insuruser.getExtorderno();
        }
    }

    /**
     *判断是否消失18岁，如果小于返回true
     * @param time
     * @return
     */
    public boolean ischild(long time) {
        long weichengnian = 18 * 1000 * 60 * 60 * 24 * 365L;
        if (System.currentTimeMillis() - time > weichengnian) {
            return false;
        }
        else {
            return true;
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getCallbackurl() {
        return callbackurl;
    }

    public void setCallbackurl(String callbackurl) {
        this.callbackurl = callbackurl;
    }

    public String getProductcode() {
        return Productcode;
    }

    public void setProductcode(String productcode) {
        Productcode = productcode;
    }

}
