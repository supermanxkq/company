package com.insurance;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.activation.DataHandler;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet2;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.CCSPostMethod;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.util.Insurances;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 * 中国人寿
 * 火意险
 * 产品分好了：产品编码                   产品名称                    计划编码                         计划名称              保额(￥)   保费（￥）
           PICCSZXSLY  深圳人保寿I旅行    ILXHCYWXD   I旅行火车意外险D款      20
                              帐号：qicaibj 密码：76966b555b1d12d9f098114176ecdc2d
                              接口平台 测试地址：http://118.194.192.112:8888 
                
                验真地址 http://www.e-picclife.com/sia4/webflow/OutSystemPolicy-flow.jspf?execution=e1s1
 * @time 2015年8月27日 下午4:46:32
 * @author chendong
 */
public class ChinaLifeInsuranceTrain extends InsuranceSupplyMethod implements IInsuranceBook {
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    String maildizhi = "baoxian@clbao.com";

    //    String maildizhi = "249016428@qq.com";
    public static void main(String[] args) {
        try {
            String html = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <response><header><PartnerCode>qicaibj</PartnerCode><Key>76966b555b1d12d9f098114176ecdc2d</Key><TradeNo>288134318620355</TradeNo><AccDate>2015-08-27</AccDate><AccTime>18:33:13</AccTime><IsEncrypt>0</IsEncrypt><Channel>1</Channel><ReqType>E</ReqType><ProductCode>PICCSZXSLY</ProductCode><PlanCode>ILXHCYWXD</PlanCode><MessageId>201508275352434fd8464bc7af6ce9aee53e4d58</MessageId><BackCode>99999</BackCode></header><body><Proposal><PolicyNo>000057895323158</PolicyNo><PolicyUrl><![CDATA[http://e.picclife.com/picc/Baf/BafFile.jspx?_action=renderPdfFromCore&_oid=3019833458&_bmfClass=InsurancePolicy&_bmfProperty=EFile&_contentType=application/pdf]]></PolicyUrl><Msg>承保成功;</Msg><OrderId>20150827183312822</OrderId></Proposal></body></response>";

            Document document = DocumentHelper.parseText(html);
            //            main1();
            //            main2();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @time 2015年8月27日 下午6:39:30
     * @author chendong
     * @throws DocumentException 
     */
    private static void main2() throws DocumentException {
        String html = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <response><header><PartnerCode>qicaibj</PartnerCode><Key>76966b555b1d12d9f098114176ecdc2d</Key><TradeNo>288134318620355</TradeNo><AccDate>2015-08-27</AccDate><AccTime>18:33:13</AccTime><IsEncrypt>0</IsEncrypt><Channel>1</Channel><ReqType>E</ReqType><ProductCode>PICCSZXSLY</ProductCode><PlanCode>ILXHCYWXD</PlanCode><MessageId>201508275352434fd8464bc7af6ce9aee53e4d58</MessageId><BackCode>99999</BackCode></header><body><Proposal><PolicyNo>000057895323158</PolicyNo><PolicyUrl><![CDATA[http://e.picclife.com/picc/Baf/BafFile.jspx?_action=renderPdfFromCore&_oid=3019833458&_bmfClass=InsurancePolicy&_bmfProperty=EFile&_contentType=application/pdf]]></PolicyUrl><Msg>承保成功;</Msg><OrderId>20150827183312822</OrderId></Proposal></body></response>";
        Document document = DocumentHelper.parseText(html);
        Element root = document.getRootElement();
        List<Element> list = root.content();
        for (int i = 0; i < list.size(); i++) {
            Element element = list.get(i);
            if ("body".equals(element.getName())) {
                Element elementProposal = element.element("Proposal");
                String Msg = elementProposal.elementText("Msg");
                String PolicyNo = elementProposal.elementText("PolicyNo");
                String PolicyUrl = elementProposal.elementText("PolicyUrl");
                String OrderId = elementProposal.elementText("OrderId");
                System.out.println(Msg);
                System.out.println(PolicyNo);
                System.out.println(PolicyUrl);
                System.out.println(OrderId);
                break;
            }
        }
        //        Element Element_ORDER = root.element("response");
        //        Element body = Element_ORDER.element("body");
        //        if ("true".equals(RETURN)) {
        //            Element POLICYElement = Element_ORDER.element("POLICY");
        //            String policyno = POLICYElement.attributeValue("POLICYNO");
        //            String no = POLICYElement.attributeValue("NO");
        //        }
        //        else {
        //            String error = Element_ORDER.attributeValue("ERROR");
        //            if (error == null) {
        //                error = Element_ORDER.element("POLICY").element("ERROR").attributeValue("INFO");
        //            }
        //            WriteLog.write("ChinaLifeInsuranceTrain", l1 + "==================" + error);
        //        }

    }

    /**
     * 
     * @time 2015年8月27日 下午6:39:14
     * @author chendong
     */
    private static void main1() {

        //        String maildizhi = "zengqingquan@clbao.com";
        String maildizhi = "249016428@qq.com";

        String mobile = "13041234677";
        Insuruser insuruser = new Insuruser();
        Long l1 = System.currentTimeMillis();
        ChinaLifeInsuranceTrain CLIT = new ChinaLifeInsuranceTrain();

        String resultString = "";
        //TEST========================S
        String urlStr = "http://118.194.192.112:8888";//测试地址
        urlStr = "http://uis.instony.com:8888";//正式地址
        String username = "qicaibj";
        String password = "76966b555b1d12d9f098114176ecdc2d";
        int iszhengshi = 1;

        CLIT.setUrlStr(urlStr);
        CLIT.setUsername(username);
        CLIT.setPassword(password);

        //=======退保测试开始
        insuruser.setPolicyno("000057895323158");
        //        resultString = CLIT.cancelOrderAplylist(insuruser);//=====================>退保测试开始
        //        System.out.println(resultString);
        //=======退保测试结束
        //=======承保测试开始
        insuruser.setId(l1);
        insuruser.setMobile(mobile);
        insuruser.setName("陈栋");
        insuruser.setCodetype(1L);
        insuruser.setCode("412823198909298017");
        insuruser.setFlytime(new Timestamp(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 30)));
        insuruser.setFlyno("2549");
        List<Insuruser> insurusers = new ArrayList<Insuruser>();
        insurusers.add(insuruser);
        //投保
        insuruser = (Insuruser) CLIT.saveTrainOrderAplylist(null, insurusers, 1).get(0);
        System.out.println(insuruser.getRemark());
        //=======承保测试结束
    }

    /**
     * @see 请求地址
     */

    private String urlStr;

    /**
     * @see 用户名
     */
    //    private static String username = "qicaibj";

    private String username;

    /**
     * @see 密码
     */
    //    private static String password = "76966b555b1d12d9f098114176ecdc2d";

    private String password;

    /**
     * 合作渠道的接口标识
     */
    //    private static String interfaceFlag = "BAYG";

    //    private String interfaceFlag;

    /**
     * @see 密钥  key
     */
    //    private static String key = "ldlhpwefhorehjgoer";

    //    private String key;//

    @Override
    public List<Insurances> orderAplylist(String jyNo, Customeruser user, List list, String begintime, String[] fltno)
            throws Exception {

        return null;
    }

    @Override
    public DataHandler PolicyReprint(Insurorder order) throws Exception {
        return null;
    }

    /* (non-Javadoc)
     * @see com.insurance.IInsuranceBook#newOrderAplylist(java.lang.String[], java.util.List)
     */
    @Override
    public List newOrderAplylist(String[] jyNo, List list) {
        List listResult = saveTrainOrderAplylist(jyNo, list, 1);
        return listResult;
    }

    @Override
    public List saveTrainOrderAplylist(String[] jyNo, List list, int type) {
        Long l1 = System.currentTimeMillis();

        try {
            for (int i = 0; i < list.size(); i++) {
                Insuruser insuruser = (Insuruser) list.get(i);
                String data = getXmlInfo_INSURE(insuruser, 0);//承保拼xml
                System.out.println(data);
                WriteLog.write("ChinaLifeInsuranceTrain", l1 + ":urlStr:" + urlStr);
                WriteLog.write("ChinaLifeInsuranceTrain", l1 + ":data:" + data);
                String html = SendPostandGet2.submitPost(urlStr, data, "utf-8", new HashMap<String, String>())
                        .toString();
                WriteLog.write("ChinaLifeInsuranceTrain", l1 + ":html:" + html);
                try {
                    //<?xml version="1.0" encoding="UTF-8" ?> <response><header><PartnerCode>qicaibj</PartnerCode><Key>76966b555b1d12d9f098114176ecdc2d</Key><TradeNo>288134318620355</TradeNo><AccDate>2015-08-27</AccDate><AccTime>18:33:13</AccTime><IsEncrypt>0</IsEncrypt><Channel>1</Channel><ReqType>E</ReqType><ProductCode>PICCSZXSLY</ProductCode><PlanCode>ILXHCYWXD</PlanCode><MessageId>201508275352434fd8464bc7af6ce9aee53e4d58</MessageId><BackCode>99999</BackCode></header><body><Proposal><PolicyNo>000057895323158</PolicyNo><PolicyUrl><![CDATA[http://e.picclife.com/picc/Baf/BafFile.jspx?_action=renderPdfFromCore&_oid=3019833458&_bmfClass=InsurancePolicy&_bmfProperty=EFile&_contentType=application/pdf]]></PolicyUrl><Msg>承保成功;</Msg><OrderId>20150827183312822</OrderId></Proposal></body></response>
                    Document document = DocumentHelper.parseText(html);
                    Element root = document.getRootElement();
                    String Msg = "";
                    String PolicyNo = "";
                    String PolicyUrl = "";
                    String OrderId = "";
                    List<Element> listElement = root.content();
                    for (int j = 0; j < listElement.size(); j++) {
                        Element element = listElement.get(j);
                        if ("body".equals(element.getName())) {
                            Element elementProposal = element.element("Proposal");
                            Msg = elementProposal.elementText("Msg");
                            PolicyNo = elementProposal.elementText("PolicyNo");
                            PolicyUrl = elementProposal.elementText("PolicyUrl");
                            OrderId = elementProposal.elementText("OrderId");
                            //                            System.out.println(Msg);
                            //                            System.out.println(PolicyNo);
                            //                            System.out.println(PolicyUrl);
                            //                            System.out.println(OrderId);
                            break;
                        }
                    }

                    if (Msg != null && Msg.contains("承保成功")) {
                        insuruser.setInsurstatus(1);//投保成功
                        insuruser.setPolicyno(PolicyNo);
                        WriteLog.write("ChinaLifeInsuranceTrain", l1 + ":policyno:" + PolicyNo + ":OrderId:" + OrderId
                                + ":remark:" + insuruser.getRemark());
                    }
                    else {
                        insuruser.setInsurstatus(2);//投保失败
                        insuruser.setRemark(Msg);
                        WriteLog.write("ChinaLifeInsuranceTrain", l1 + "==================" + Msg);
                    }
                }
                catch (DocumentException e) {
                    //                    e.printStackTrace();
                    logger.error(html, e.fillInStackTrace());
                }
                catch (Exception e) {
                    logger.error(html, e.fillInStackTrace());
                    //                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            //            WriteLog.write("ChinaLifeInsuranceTrain_err", "接口异常:" + e.getMessage());
            //            e.printStackTrace();
            logger.error(JSONObject.toJSONString(list), e.fillInStackTrace());
        }
        return list;
    }

    /**
     * 退保
     */
    @Override
    public String cancelOrderAplylist(Insuruser insur) {
        Long l1 = System.currentTimeMillis();
        String data = getXmlInfo_SURRENDER(insur);
        Map<String, String> paramMap = new HashMap<String, String>();
        WriteLog.write("ChinaLifeInsuranceTrain_cancelOrderAplylist", l1 + ":paramMap:" + paramMap + ";urlStr:"
                + urlStr);
        String html = SendPostandGet2.submitPost(urlStr, data, "utf-8", new HashMap<String, String>()).toString();
        WriteLog.write("ChinaLifeInsuranceTrain_cancelOrderAplylist", l1 + ":" + html);
        try {
            Document document = DocumentHelper.parseText(html);
            Element root = document.getRootElement();
            String Msg = "";
            String PolicyNo = "";
            String OrderId = "";
            List<Element> listElement = root.content();
            for (int j = 0; j < listElement.size(); j++) {
                Element element = listElement.get(j);
                if ("body".equals(element.getName())) {
                    Element elementProposal = element.element("Proposal");
                    Msg = elementProposal.elementText("Msg");
                    if (Msg.contains("撤单成功")) {
                        PolicyNo = elementProposal.elementText("PolicyNo");
                        //                        PolicyUrl = elementProposal.elementText("PolicyUrl");
                        OrderId = elementProposal.elementText("OrderId");
                        //                            System.out.println(Msg);
                        //                            System.out.println(PolicyNo);
                        //                            System.out.println(PolicyUrl);
                        //                            System.out.println(OrderId);
                    }
                    break;
                }
            }
            WriteLog.write("ChinaLifeInsuranceTrain_cancelOrderAplylist", l1 + ":policyno:" + Msg);
            if (Msg.contains("撤单成功")) {
                insur.setInsurstatus(3);
            }
            else {
                insur.setInsurstatus(4);
                insur.setRemark(Msg);
            }
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return insur.getInsurstatus() + "," + insur.getRemark();
    }

    /**
     * 退保拼xml
     * 
     * @param insur
     * @return
     * @time 2015年5月14日 下午12:08:22
     * @author chendong
     */
    private String getXmlInfo_SURRENDER(Insuruser insur) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        sb.append("<request>");
        sb.append("<header>");
        sb.append("<PartnerCode>" + username + "</PartnerCode>");
        sb.append("<Key>" + password + "</Key>");
        sb.append("<TradeNo>" + insur.getRemark() + "</TradeNo>");
        String accDate = TimeUtil.gettodaydate(1);
        sb.append("<AccDate>" + accDate + "</AccDate>");//商户请求日期/UIS返回日期    Y   日期格式：yyyy-MM-dd
        String accTime = TimeUtil.gettodayTime(2);
        sb.append("<AccTime>" + accTime + "</AccTime>");
        sb.append("<IsEncrypt>0</IsEncrypt>");//是否做安全校验 Y   1 做（默认） 0.不做（相见安全校验）
        sb.append("<Channel>1</Channel>");//渠道  Y   默认为1   （待后期扩展）
        sb.append("<ReqType>C</ReqType>");//请求类型    Y   C撤单;D:电子保单下载;E承保
        sb.append("<ProductCode>PICCSZXSLY</ProductCode>");//产品编码   Y   产品编码   UIS提供
        sb.append("<PlanCode>ILXHCYWXD</PlanCode>");//产品计划编码    Y   产品计划编码  UIS提供
        sb.append("<MessageId></MessageId>");
        sb.append("<BackCode></BackCode>");
        sb.append("</header>");
        sb.append("<body>");
        sb.append("<PolicyInfo>");
        sb.append("<PolicyNo>" + insur.getPolicyno() + "</PolicyNo>");//保单号
        sb.append("</PolicyInfo>");
        sb.append("</body>");
        sb.append("</request>");
        return sb.toString();
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
     * 承保拼xml
     * 
     * @param insuruser
     * @return
     * @time 2015年5月13日 下午5:52:39
     * @author chendong
     */
    private String getXmlInfo_INSURE(Insuruser insuruser, int isencode) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        sb.append("<request>");
        sb.append("<header>");
        sb.append("<PartnerCode>" + username + "</PartnerCode>");
        sb.append("<Key>" + password + "</Key>");
        String orderid = insuruser.getId() + System.currentTimeMillis() + "" + new Random().nextInt(10000);
        insuruser.setRemark(orderid);
        sb.append("<TradeNo>" + orderid + "</TradeNo>");//   商户订单号  Y   商户系统内部的订单号,64个字符内,确保在商户系统唯一
        String accDate = TimeUtil.gettodaydate(1);
        sb.append("<AccDate>" + accDate + "</AccDate>");//商户请求日期/UIS返回日期    Y   日期格式：yyyy-MM-dd
        String accTime = TimeUtil.gettodayTime(2);
        sb.append("<AccTime>" + accTime + "</AccTime>");
        sb.append("<IsEncrypt>0</IsEncrypt>");//是否做安全校验 Y   1 做（默认） 0.不做（相见安全校验）
        sb.append("<Channel>1</Channel>");//渠道  Y   默认为1   （待后期扩展）
        sb.append("<ReqType>E</ReqType>");//请求类型    Y   C撤单;D:电子保单下载;E承保
        sb.append("<ProductCode>PICCSZXSLY</ProductCode>");//产品计划编码 Y   产品计划编码  UIS提供
        sb.append("<PlanCode>ILXHCYWXD</PlanCode>");//产品计划编码    Y   产品计划编码  UIS提供
        sb.append("<MessageId></MessageId>");
        sb.append("<BackCode></BackCode>");
        sb.append("</header>");
        sb.append("<body>");
        sb.append("<PolicyInfo>");//保单信息
        sb.append("<TotalPremium></TotalPremium>");//TotalPremium 总保费        单位元  精确到小数点后2位  0.00
        sb.append("<InsuredDate>" + changeDate(new Timestamp(System.currentTimeMillis())) + "</InsuredDate>");//投保日期      YYYY-MM-DD hh:mm:ss(时分秒没有就为00)
        String InsBeginDate = changeDate(insuruser.getFlytime());//航班起飞时间|发车时间
        sb.append("<InsBeginDate>" + InsBeginDate + "</InsBeginDate>");//保险起期      YYYY-MM-DD hh:mm:ss(时分秒没有就为00)
        sb.append("<InsEndDate></InsEndDate>");//保险止期        YYYY-MM-DD hh:mm:ss(时分秒没有就为00)
        //        sb.append("<!--航空意外险  -->");
        sb.append("<FlightNo>" + insuruser.getFlyno() + "</FlightNo>");//航班号        永成航延、航意
        sb.append("<TakeoffDate>" + InsBeginDate + "</TakeoffDate>");//起飞时间      yyyy-MM-dd HH:mm:ss
        sb.append("</PolicyInfo>");
        sb.append("<Holder>");//投保人信息
        String name = insuruser.getName();
        try {
            if (isencode == 1) {
                name = java.net.URLEncoder.encode(name, "utf-8");
            }
            sb.append("<HolderName>" + name + "</HolderName>");//投保人信息姓名
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String InsuredSex = getSexById(insuruser.getCode());
        sb.append("<HolderSex>" + InsuredSex + "</HolderSex>");//投保人性别
        String birthday = "";
        try {
            if (insuruser.getBirthday() != null) {
                birthday = insuruser.getBirthday().toString().substring(0, 10);
            }
            else if (insuruser.getCodetype() == 1) {
                birthday = insuruser.getCode().substring(6, 14);
            }
            birthday = birthday.substring(0, 4) + "-" + birthday.substring(4, 6) + "-" + birthday.substring(6, 8);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ChinaLifeInsuranceTrain_ERROR", e);
        }
        sb.append("<HolderBirthday>" + birthday + "</HolderBirthday>");//出生日期       格式为YYYY-MM-DD 
        String HolderCardType = codeTypeDB2Sunshine(insuruser.getCodetype());
        sb.append("<HolderCardType>" + HolderCardType + "</HolderCardType>");//证件类型      1：身份证 2：护照 3：其他
        sb.append("<HolderCardNo>" + insuruser.getCode() + "</HolderCardNo>");//证件号码       如果为身份证 必须与性别出生日期一致
        sb.append("<HolderMobile>" + insuruser.getMobile() + "</HolderMobile>");
        sb.append("<HolderPhone></HolderPhone>");
        String email = insuruser.getEmail() == null ? maildizhi : insuruser.getEmail();
        sb.append("<HolderEmail>" + email + "</HolderEmail>");
        sb.append("<HolderAddress></HolderAddress>");//联系地址
        sb.append("<HolderZip></HolderZip>");//邮编
        sb.append("</Holder>");
        sb.append("<Insureds>");
        sb.append("<Insured>");//被保人信息
        sb.append("<InsuredRelation>1</InsuredRelation>");//被保人是投保人     1：本人 2：配偶 3：父母  4：子女 5：劳务关系 6：其他        备注：InsuredRelation=1时投被保人信息需要一致性校验
        sb.append("<InsuredName>" + name + "</InsuredName>");//姓名       长度小于32个汉字

        sb.append("<InsuredSex>" + InsuredSex + "</InsuredSex>");
        sb.append("<InsuredBirthday>" + birthday + "</InsuredBirthday>");
        sb.append("<InsuredCardType>" + HolderCardType + "</InsuredCardType>");
        sb.append("<InsuredCardNo>" + insuruser.getCode() + "</InsuredCardNo>");
        sb.append("<InsuredMobile>" + insuruser.getMobile() + "</InsuredMobile>");
        sb.append("<InsuredPhone></InsuredPhone>");
        sb.append("<InsuredEmail>" + email + "</InsuredEmail>");
        sb.append("<InsuredAddress></InsuredAddress>");
        sb.append("<InsuredZip></InsuredZip>");
        sb.append("<PlanNo></PlanNo>");
        sb.append("<Amount></Amount>");
        sb.append("<Premium></Premium>");
        sb.append("</Insured>");
        sb.append("</Insureds>");
        sb.append("</body>");
        sb.append("</request>");
        return sb.toString();
    }

    public static String sendPostRequest(String url, String encoding, Map<String, String> paramMap) {
        String response = "-1";
        CCSPostMethod post = null;
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        post = new CCSPostMethod(url.toString());
        post.setFollowRedirects(false);
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            post.addParameter(entry.getKey(), entry.getValue());
        }
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
        try {
            httpClient.executeMethod(post);
            response = post.getResponseBodyAsString();
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
    *  证件类型      1：身份证 2：护照 3：其他
    * @param timestamp
    * @return  
    * @time 2015年6月24日 上午11:14:31
    * @author yinshubin
    */
    private String codeTypeDB2Sunshine(long codetypeDB) {
        String cardtypeSunshine = "3";
        if (codetypeDB == 3) {//护照
            cardtypeSunshine = "2";
        }
        else if (codetypeDB == 1) {//身份证
            cardtypeSunshine = "1";
        }
        else {
            cardtypeSunshine = "3";
        }
        return cardtypeSunshine;
    }

    public String getUrlStr() {
        return urlStr;
    }

    public void setUrlStr(String urlStr) {
        this.urlStr = urlStr;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
