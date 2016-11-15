package com.insurance;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.component.PublicComponent;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.util.Insurances;
import com.ccservice.b2b2c.atom.server.Server;

import client.BoangServiceImplServiceStub;
import client.TrafficPolicyStub;
import client.BoangServiceImplServiceStub.PassengerInfoBean;

/**
 * 新华保险的接口方法
 * 
 * @author cd
 *
 */
public class Newchinalife extends PublicComponent implements IInsuranceBook {
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    /**
     * @see 客户id
     */
    //    private static String userId = "1000021";
    private static String userId;

    /**
     * @see 密钥
     */
    //    private static String sign = "e10adc3949ba59abbe56e057f20f883e";
    private static String sign;

    /**
     * @see 客户端类型
     */
    //    private static String clientType = "1";
    private static String clientType;

    /**
     * @see 客户回调地址
     */
    //    private static String callbackurl = "http://211.103.207.133:8080/cn_home/sunshinecallback";
    private static String callbackurl;

    //此数据已放到数据库
    //	String PosCode="IBJ00003";//正式的
    //	String SellFormType="EUN02006";//正式的
    //	String PosCode="";//假的
    //	String SellFormType="";//假的

    /**
     * 创建订单
     * 
     * @param ainame
     *            投保人姓名
     * @param aimobile
     *            投保人电话
     * @param aiaddress
     *            投保人地址 可以为空
     * @param aiidcard
     *            证件类型:1 身份证 2 护照 3 军人证 4 港台同胞证 5 其它证件
     * @param aiidnumber
     *            证件号
     * @param aieffectivedate
     *            生效日期
     * @param aiemail
     *            电子邮件
     * @param aisex
     *            投保人性别 -1保密 1 男 0 女
     * @param aiflightnumber
     *            航班号
     * @return
     */
    private void savePolicyInfo(String ainame, String aimobile, String aiaddress, String aiidcard, String aiidnumber,
            String aieffectivedate, String aiemail, String aisex, String aiflightnumber) {
    }

    @Override
    public List<Insurances> orderAplylist(String jyNo, Customeruser user, List list, String begintime, String[] fltno)
            throws Exception {
        return null;
    }

    @Override
    public DataHandler PolicyReprint(Insurorder order) throws Exception {
        // TODO Auto-generated method stub
        return null;
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
     * 新华保险的航意险出单请求
     * 新华保险投保时候有交易流水号，一个人同一个行程只能投保一次，交易流水号如果使用过就不能使用，如果调用过保存交易流水号到insuranuser里面
     */
    public List newOrderAplylist(String[] jyNo, List list) {
        userId = Server.getInstance().getSunshineInsuranceEaccount().getUsername();
        sign = Server.getInstance().getSunshineInsuranceEaccount().getKeystr();
        clientType = Server.getInstance().getSunshineInsuranceEaccount().getEdesc();
        callbackurl = Server.getInstance().getSunshineInsuranceEaccount().getNourl();
        WriteLog.write("sunshineInsurance", userId + ":" + sign + ":" + clientType + ":" + callbackurl);
        try {
            for (int i = 0; i < list.size(); i++) {
                BoangServiceImplServiceStub stub = new BoangServiceImplServiceStub();
                OMElement header1 = AXIOMUtil.stringToOM("<authHeader><userName>" + userId + "</userName><passwd>"
                        + sign + "</passwd></authHeader>");
                stub._getServiceClient().addHeader(header1);
                BoangServiceImplServiceStub.SubmitInsureReqE submitInsureReqE = new BoangServiceImplServiceStub.SubmitInsureReqE();
                BoangServiceImplServiceStub.SubmitInsureReq submitInsureReq = new BoangServiceImplServiceStub.SubmitInsureReq();
                BoangServiceImplServiceStub.SendInfoBean SendInfoBean = new BoangServiceImplServiceStub.SendInfoBean();
                BoangServiceImplServiceStub.SyncResponse syncResponse = new BoangServiceImplServiceStub.SyncResponse();
                BoangServiceImplServiceStub.InsuranceInfoBean InsuranceInfoBean = new BoangServiceImplServiceStub.InsuranceInfoBean();
                BoangServiceImplServiceStub.CallBackInfoBean CallBackInfoBean = new BoangServiceImplServiceStub.CallBackInfoBean();
                BoangServiceImplServiceStub.PassengerInfoBean PassengerInfoBean = new BoangServiceImplServiceStub.PassengerInfoBean();
                BoangServiceImplServiceStub.OrderInfoBean OrderInfoBean = new BoangServiceImplServiceStub.OrderInfoBean();
                Insuruser insuruser = (Insuruser) list.get(i);
                OrderInfoBean.setAircode(insuruser.getFlyno());
                // 航班起飞时间，也即保险生效时间
                OrderInfoBean.setStartDate(changeDate(insuruser.getFlytime()));
                // 贵公司关联订单号
                OrderInfoBean.setOrderId(insuruser.getId() + "");

                // 客户端类型为1，表示接口
                if (null == clientType || clientType.length() <= 0) {
                    clientType = "1";
                }
                CallBackInfoBean.setClientType(clientType);
                if ("1".equals(clientType)) {
                    CallBackInfoBean.setCallback(callbackurl);
                    CallBackInfoBean.setUserId(userId);
                    CallBackInfoBean.setMerchantsign(sign);
                }

                // 产品编号:阳光人寿交通工具综合保险B款保险，保费20，保额605000，有效期7天，限购2份（即保险有效期内限购两份）
                InsuranceInfoBean.setProductcode("BA-QP000640");

                PassengerInfoBean passengerArray[] = new PassengerInfoBean[1];
                // 乘客证件号码
                PassengerInfoBean.setInsuredidno(insuruser.getCode());
                // 乘客证件类型：0: 身份证11: 户口薄12: 驾驶证13: 军官证14: 士兵证17: 港澳通行证18: 台湾通行证99: 其他51: 护照61: 港台同胞证
                PassengerInfoBean.setInsuredidtype(getcodetype(insuruser.getCodetype()));
                // 乘客生日
                PassengerInfoBean.setInsuredbirthday(changeDate(insuruser.getBirthday()).substring(0, 10));
                // 乘客手机号码：用于接收保单成功的短信，短信由阳光保险公司发送和保监会发送
                PassengerInfoBean.setInsuredmobile(insuruser.getMobile());
                // 乘客姓名，注意转码，否则会乱码
                PassengerInfoBean.setInsuredname(java.net.URLEncoder.encode(insuruser.getName(), "UTF-8"));
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
                BoangServiceImplServiceStub.SubmitInsureReqResponseE SubmitInsureReqResponseE = stub
                        .submitInsureReq(submitInsureReqE);
                syncResponse = SubmitInsureReqResponseE.getSubmitInsureReqResponse().get_return();
                WriteLog.write("sunshineInsurance", userId + ":" + sign + ":" + clientType + ":" + callbackurl);
                WriteLog.write(
                        "sunshineInsurance",
                        insuruser.getId() + ":" + insuruser.getCode() + ":" + insuruser.getCodetype() + ":"
                                + insuruser.getBirthday() + ":" + insuruser.getMobile() + ":" + insuruser.getName());
                if (!syncResponse.getSuccess()) {
                    insuruser.setInsurstatus(2);
                    insuruser.setRemark(insuruser.getRemark() == null ? "" : insuruser.getRemark()
                            + syncResponse.getDesc());
                    WriteLog.write("sunshineInsurance", insuruser.getId() + ":出保失败:" + syncResponse.getDesc());
                }
                else {
                    WriteLog.write("sunshineInsurance", insuruser.getId() + ":出保中");
                    //TODO 因为是异步获取投保结果，所以存为0
                    insuruser.setInsurstatus(0);
                }
            }
        }
        catch (Exception e) {
            WriteLog.write("sunshineInsurance", "接口异常:" + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        //		Newchinalife newchinalife = new Newchinalife();

        //		List list = new ArrayList();
        Insuruser ins = new Insuruser();
        //		ins.setFlytime(new Timestamp(113, 10, 25, 0, 0, 0, 0));
        //		ins.setCodetype(1l);
        //		ins.setOrdernum("B00002");
        //		ins.setName("陈栋");
        //		ins.setCode("412823198909298017");
        ins.setBirthday(new Timestamp(95, 8, 29, 0, 0, 0, 0));
        //		ins.setMobile("13522543333");
        //		ins.setFlyno("CA1331");
        //		list.add(ins);
        //		newchinalife.newOrderAplylist(null, list);

        //=========================================================================================

        //		Insuruser ins = new Insuruser();
        //		ins.setFlyno("CA1331");
        //		ins.setFlytime(new Timestamp(113, 10, 25, 0, 0, 0, 0));
        //		ins.setExtorderno("0200620131029015");
        //		ins.setPolicyno("662103150903");
        //		newchinalife.cancelOrderAplylist(ins);

        //		long xianzaishike = System.currentTimeMillis();
        //		long weichengnian = 18*1000*60*60*24*365L;
        //		Timestamp t =new Timestamp(xianzaishike);
        //		Timestamp t1 =new Timestamp(ins.getBirthday().getTime()+weichengnian);
        //		
        //		System.out.println(xianzaishike);
        //		System.out.println(xianzaishike);
        //		System.out.println(ins.getBirthday().getTime()+weichengnian);

        String xml = "<?xml version=\"1.0\" encoding=\"GBK\"?><ApplyResponse><TransData><TransID>0200600000000654</TransID><MerchantNo>hthy</MerchantNo><TransDate>20131130</TransDate><TransTime>000000</TransTime><OrderID>T13112913204310463</OrderID><ResultStatus>81</ResultStatus><ResultMsg>0200600000000654被保人年龄超出范围</ResultMsg><Insurances><InsuranceNo></InsuranceNo></Insurances><PosCode>IBJ00003</PosCode><SellFormType>EUN02006</SellFormType><ProductCode>66368079</ProductCode><EffectDate>20131130</EffectDate><EffectTime>000000</EffectTime><ExpiryDate>20131130</ExpiryDate><ExpiryTime>000000</ExpiryTime><OwnerName>李祥澜</OwnerName><CardType>01</CardType><CardNo>411329199905022827</CardNo><BirthDate>1999-05-02</BirthDate><Mobile>18237787031</Mobile><NeedSMS>T</NeedSMS><Email></Email><InsuranceNum>1</InsuranceNum><FlightNo>K1064</FlightNo><DepartureCity></DepartureCity><DepartureDate>20131130</DepartureDate><DepartureTime>000000</DepartureTime><ArrivalCity></ArrivalCity><ArrivalDate>20131130</ArrivalDate><ArrivalTime>000000</ArrivalTime><Extend1 /></TransData><TransSignature /></ApplyResponse>";
        String ResultStatus = new Newchinalife().getXMLMessageNodeValue(xml, "ResultStatus");
        String ResultMsg = new Newchinalife().getXMLMessageNodeValue(xml, "ResultMsg");
        System.out.println(ResultMsg);
        if ("00".equals(ResultStatus)) {
        }
        else {
            System.out.println(ResultMsg);
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

    /**
     * 新华保险的航意险退单请求
     */
    public String cancelOrderAplylist(Insuruser insuruser) {
        String PosCode = getSysconfigString("newchinaPosCode");
        String SellFormType = getSysconfigString("newchinaSellFormType");
        //动态的
        try {
            TrafficPolicyStub stub = new TrafficPolicyStub();
            TrafficPolicyStub.RefundpolicyInfo refundpolicyInfo = new TrafficPolicyStub.RefundpolicyInfo();

            String flytime = insuruser.getFlytime().toString().substring(0, 10).replaceAll("-", "");
            String TransID = getTransID(insuruser);
            String requestxml = "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "<CancelRequest><TransData><TransID>"
                    + TransID + "</TransID><ApprovalTransID>" + insuruser.getExtorderno()
                    + "</ApprovalTransID><SellFormType>" + SellFormType
                    + "</SellFormType><OriginalInsurances><OriginalInsuranceNo>" + insuruser.getPolicyno()
                    + "</OriginalInsuranceNo></OriginalInsurances><TransDate>" + flytime
                    + "</TransDate><TransTime>000000</TransTime><Extend1 /></TransData>";
            WriteLog.write("新华保险退保", "0:" + requestxml);
            refundpolicyInfo.setIn0(requestxml);
            TrafficPolicyStub.RefundpolicyInfoResponse response = stub.refundpolicyInfo(refundpolicyInfo);
            String xml = response.getOut();
            WriteLog.write("新华保险退保", "1:" + xml);
            String ResultStatus = getXMLMessageNodeValue(xml, "ResultStatus");
            String ResultMsg = getXMLMessageNodeValue(xml, "ResultMsg");
            System.out.println(ResultMsg);
            if ("00".equals(ResultStatus)) {
                insuruser.setInsurstatus(4);
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
        return insuruser.getInsurstatus() + "," + insuruser.getRemark();
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
     * 此方法回滚新华保险的交易流水号事先分配好的放到T_B2BSEQUENCE的xinhuaTransID中
     * @return
     */
    public String rollTransID1() {
        String sqlisstar = "SELECT C_VALUE AS XINHUATRANSID FROM T_B2BSEQUENCE WHERE C_NAME='xinhuaTransID'";
        // 1 表示正在更新 0表示更新完了
        List isstar = Server.getInstance().getSystemService().findMapResultBySql(sqlisstar, null);
        if (isstar.size() > 0) {
            Map map = (Map) isstar.get(0);
            String XINHUATRANSID = map.get("XINHUATRANSID").toString();
            try {
                sqlisstar = "UPDATE T_B2BSEQUENCE SET C_VALUE = '" + (Long.parseLong(XINHUATRANSID) - 1)
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

    /**
     *根据Insuruser对象的codetype转化为接口的证件类型
     * @param codetype
     * @return
     */
    private String getcodetype(long codetype) {
        //		01  身份证
        //		02  护照
        //		03  学生证
        //		04  军人证
        //		06  驾驶证
        //		07  回乡证
        //		08  台胞证
        //		10 港澳通行证
        //		11 国际海员证
        //		20 外国人永久居留证
        //		21 旅行证
        //		22 台湾通行证
        //		23 士兵证
        //		24 临时身份证
        //		25 户口簿
        //		26 警官证
        //		99 其它
        String CardType = "99";
        if (codetype == 3) {
            CardType = "02";
        }
        else if (codetype == 1) {
            CardType = "01";
        }
        else if (codetype == 4) {
            CardType = "10";
        }
        else if (codetype == 5) {
            CardType = "99";
        }
        else if (codetype == 6) {
            CardType = "08";
        }
        else if (codetype == 7) {
            CardType = "07";
        }
        else if (codetype == 8) {
            CardType = "04";
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
     * 火车票使用方法
     * @param jyNo
     * @param list Insuruser对象
     * @param type 1。易订行火车票5元（未成年人）2。易订行火车票5元（成年人）3 。易订行火车票20元（未成年人）4。易订行火车票20元（成年人）
     * @return
     */
    public List<Insuruser> saveTrainOrderAplylist(String[] jyNo, List list, int type) {
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
        //动态的

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
}
