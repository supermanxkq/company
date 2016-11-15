package com.insurance;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

import com.ccservice.b2b2c.atom.component.SendPostandGet2;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.CCSPostMethod;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.util.Insurances;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 *     http://wecare.sinosig.com/common/new_customerservice/html/baodanfuwu/dzbd_index.html
         我们的正式环境： 接口调用地址:
         联通的：http://114.251.229.211:7002/ifp/SyncInterface
         电信的：http://219.143.230.134:7002/ifp/SyncInterface
        这2个任选一个就行
        用户名：G5RH4T1HJY6UKUK41UJ3TG236ED
        密码：1THTH1Y3KM5L6IOU4KI6L412F1
        密钥：f15r8gth2yjny6jy56
 * 
 * @time 2015年5月13日 下午5:41:34
 * @author chendong
 */
public class SunshineInsuranceTrain extends InsuranceSupplyMethod implements IInsuranceBook {
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private String eMailPath = "baoxian@clbao.com";

    /**
     * @see 请求地址
     */
    //    private static String urlStr = "http://1.202.235.81:7001/ifp-R201505060A/SyncInterface";

    //    private static String urlStr = "http://localhost:9004/cn_home/servletTest";

    private String urlStr;

    /**
     * @see 用户名
     */
    //    private static String username = "boang";

    private String username;

    /**
     * @see 密码
     */
    //    private static String password = "boangtest";

    private String password;

    /**
     * 合作渠道的接口标识
     */
    //    private static String interfaceFlag = "BAYG";

    private String interfaceFlag;

    /**
     * @see 密钥  key
     */
    //    private static String key = "ldlhpwefhorehjgoer";

    private String key;//

    /**
     * 产品编号
     */
    private String productCode;

    private double oremium;//保费

    private double amount;//保额

    int insurperiod;//保险期间 (天)

    /**
     * 
     * @param eMailPath 接收保险电子邮箱地址  baoxian@clbao.com
     * @param urlStr 调保险地址的url
     * @param username 保险接口用户名 
     * @param password 保险接口密码
     * @param interfaceFlag  合作渠道的接口标识 BAYG 
     * @param key 密钥  key ldlhpwefhorehjgoer
     * @param productCode 产品编号 
    * <ul>
    * <li> <code>QP010901[20元 阳光 火车意外险高端款 火车意外伤害80W 医疗5000(200免赔，80%赔付) 限购2份,保3天 [火车票]</code>
    * <li> <code>QP000103[30元阳光航空意外险(舱到舱) 飞机意外伤害320W，医疗5000元(200免赔，70%赔付) 限购一份 保单次</code>
    * </ul>
     * @param insurperiod2 
     * @param amount2 
     * @param oremium2 
     *  
     *  
     */
    public SunshineInsuranceTrain(String eMailPath, String urlStr, String username, String password,
            String interfaceFlag, String key, String productCode, double oremium, double amount, int insurperiod) {
        super();
        if (eMailPath != null || !"".equals(eMailPath)) {
            this.eMailPath = eMailPath;
        }
        this.urlStr = urlStr;
        this.username = username;
        this.password = password;
        this.interfaceFlag = interfaceFlag;
        this.key = key;
        this.productCode = productCode;
        this.oremium = oremium;
        this.amount = amount;
        this.insurperiod = insurperiod;
    }

    public SunshineInsuranceTrain() {
        super();
    }

    /* (non-Javadoc)
     * @see com.insurance.IInsuranceBook#newOrderAplylist(java.lang.String[], java.util.List)
     */
    @Override
    public List newOrderAplylist(String[] jyNo, List list) {
        List listResult = saveTrainOrderAplylist(jyNo, list, 1);
        return listResult;
    }

    private String sendPostandget(String urlStr, String string, Map<String, String> paramMap) {
        String response = "-1";
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            String value = entry.getValue();

            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        //        response = SendPostandGet.submitPost(urlStr, sb.toString(), "GBK").toString();
        response = SendPostandGet2.doPost(urlStr, paramMap, "GBK");//  (urlStr, sb.toString(), "GBK").toString();
        return response;
    }

    @Override
    public List saveTrainOrderAplylist(String[] jyNo, List list, int type) {
        try {
            for (int i = 0; i < list.size(); i++) {
                Insuruser insuruser = (Insuruser) list.get(i);
                this.productCode = "QP010901";
                insuruser = Buyinsur(insuruser);
            }
        }
        catch (Exception e) {
            WriteLog.write("sunshineInsurance", "接口异常:" + e.getMessage());
            e.printStackTrace();
        }
        return list;

    }

    /**
     * 
     * @param insuruser
     * @return
     * @time 2016年6月6日 下午2:08:18
     * @author chendong
     */
    public Insuruser Buyinsur(Insuruser insuruser) {
        Long l1 = System.currentTimeMillis();
        String data = getXmlInfo_INSURE(insuruser, 0);//承保拼xml
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("functionFlag", "INSURE");//功能标识：承保传入（INSURE）
        paramMap.put("interfaceFlag", interfaceFlag);
        paramMap.put("data", data);
        String sign = getSign(key, data, "SunshineInsuranceTrain", l1);
        paramMap.put("sign", sign);
        WriteLog.write("SunshineInsuranceTrain", l1 + ":this.productCode:" + this.productCode);
        WriteLog.write("SunshineInsuranceTrain", l1 + ":paramMap:" + paramMap);
        WriteLog.write("SunshineInsuranceTrain", l1 + ":urlStr:" + urlStr);
        String html = sendPostandget(urlStr, "GBK", paramMap);
        WriteLog.write("SunshineInsuranceTrain", l1 + ":html:" + html);
        try {
            Document document = DocumentHelper.parseText(html);
            Element root = document.getRootElement();
            Element Element_ORDER = root.element("ORDER");
            String RETURN = Element_ORDER.attributeValue("RETURN");
            if ("true".equals(RETURN)) {
                Element POLICYElement = Element_ORDER.element("POLICY");
                String policyno = POLICYElement.attributeValue("POLICYNO");
                String no = POLICYElement.attributeValue("NO");
                insuruser.setInsurstatus(1);//投保成功
                insuruser.setPolicyno(policyno);
                WriteLog.write("SunshineInsuranceTrain", l1 + ":policyno:" + policyno + ":no:" + no);
            }
            else {
                insuruser.setInsurstatus(2);//投保失败
                String error = Element_ORDER.attributeValue("ERROR");
                if (error == null) {
                    error = Element_ORDER.element("POLICY").element("ERROR").attributeValue("INFO");
                }
                insuruser.setRemark(error);
                WriteLog.write("SunshineInsuranceTrain", l1 + "==================" + error);
            }
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return insuruser;
    }

    /**
     * 退保
     */
    @Override
    public String cancelOrderAplylist(Insuruser insur) {
        Long l1 = System.currentTimeMillis();
        String data = getXmlInfo_SURRENDER(insur);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("functionFlag", "SURRENDER");//功能标识：承保传入（SURRENDER）
        paramMap.put("interfaceFlag", interfaceFlag);
        paramMap.put("data", data);
        paramMap.put("sign", getSign(key, data, "SunshineInsuranceTrain_cancelOrderAplylist", l1));
        WriteLog.write("SunshineInsuranceTrain_cancelOrderAplylist", l1 + ":paramMap:" + paramMap + ";urlStr:" + urlStr);
        String html = sendPostRequest(urlStr, "GBK", paramMap);
        WriteLog.write("SunshineInsuranceTrain_cancelOrderAplylist", l1 + ":" + html);
        try {
            Document document = DocumentHelper.parseText(html);
            Element root = document.getRootElement();
            Element Element_ORDER = root.element("ORDER");
            String RETURN = Element_ORDER.attributeValue("RETURN");
            if ("true".equals(RETURN)) {
                Element POLICYElement = Element_ORDER.element("POLICY");
                String policyno = POLICYElement.attributeValue("POLICYNO");
                WriteLog.write("SunshineInsuranceTrain_cancelOrderAplylist", l1 + ":policyno:" + policyno);
                insur.setInsurstatus(3);
            }
            else {
                String error = Element_ORDER.attributeValue("ERROR");
                if (error == null) {
                    try {
                        error = Element_ORDER.element("POLICY").element("ERROR").attributeValue("INFO");
                    }
                    catch (Exception e) {
                    }
                }
                WriteLog.write("SunshineInsuranceTrain_cancelOrderAplylist", l1 + "==================" + error);
                insur.setInsurstatus(4);
                insur.setRemark(error);
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
        sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
        sb.append("<INSURENCEINFO>");
        sb.append("<USERNAME>" + username + "</USERNAME>");//insur.getPolicyno()
        sb.append("<PASSWORD>" + password + "</PASSWORD>");//insur.getPolicyno()
        sb.append("<POLICYNO>" + insur.getPolicyno() + "</POLICYNO>");//保单号
        sb.append("</INSURENCEINFO>");
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
        sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
        sb.append("<INSURENCEINFO>");
        sb.append("<USERNAME>" + username + "</USERNAME>");
        sb.append("<PASSWORD>" + password + "</PASSWORD>");
        sb.append("<ORDER>");
        String orderid = insuruser.getId() + System.currentTimeMillis() + "" + new Random().nextInt(100);
        //        String orderid = System.currentTimeMillis() + "" + new Random().nextInt(10000000);//最长20个字符
        sb.append("<ORDERID>" + orderid + "</ORDERID>");
        sb.append("<POLICYINFO>");

        sb.append("<PRODUCTCODE>" + this.productCode + "</PRODUCTCODE>");//产品编码
        sb.append("<PLANCODE></PLANCODE>");
        sb.append("<INSURDATE>" + changeDate(new Timestamp(System.currentTimeMillis())) + "</INSURDATE>");//投保时间
        String AIRLINEDATE = changeDate(insuruser.getFlytime());//航班起飞时间|发车时间
        Timestamp flytime_temp = new Timestamp(System.currentTimeMillis());
        flytime_temp = insuruser.getFlytime();
        //        flytime_temp.setHours(flytime_temp.getHours());
        String INSURSTARTDATE = changeDate(flytime_temp);
        System.out.println("保险起期:" + INSURSTARTDATE);
        sb.append("<INSURSTARTDATE>" + INSURSTARTDATE + "</INSURSTARTDATE>");//保险起期
        flytime_temp.setTime(flytime_temp.getTime() - 1000);
        flytime_temp.setDate(flytime_temp.getDate() + insurperiod);

        String INSURENDDATE = changeDate(flytime_temp);
        System.out.println("保险止期:" + INSURENDDATE);
        sb.append("<INSURENDDATE>" + INSURENDDATE + "</INSURENDDATE>");//保险止期
        sb.append("<APPNTMOBILE>" + insuruser.getMobile() + "</APPNTMOBILE>");
        try {
            String name = insuruser.getName();
            if (isencode == 1) {
                name = java.net.URLEncoder.encode(name, "GBK");
            }
            sb.append("<APPNTNAME>" + name + "</APPNTNAME>");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        sb.append("<APPNTIDTYPE>" + codeTypeDB2Sunshine(insuruser.getCodetype()) + "</APPNTIDTYPE>");//乘客证件类型
        sb.append("<APPNTIDNO>" + insuruser.getCode() + "</APPNTIDNO>");//// 乘客证件号码
        String birthday = "";
        try {
            if (insuruser.getBirthday() != null) {
                birthday = insuruser.getBirthday().toString().substring(0, 10).replace("-", "");
            }
            else if (insuruser.getCodetype() == 1) {
                birthday = insuruser.getCode().substring(6, 14);
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("SunshineInsuranceTrain_ERROR", e);
        }
        sb.append("<APPNTBIRTHDAY>" + birthday + "</APPNTBIRTHDAY>");

        sb.append("<INSURPERIOD>" + insurperiod + "</INSURPERIOD>");//保险期间
        sb.append("<PERIODFLAG>D</PERIODFLAG>");//保险期间单位，年为Y，月为M，日为D，例：保险期间为一个月，则INSURPERIOD=’1’，PERIODFLAG = ’M’
        sb.append("<MULT>1</MULT>");//购买份数
        sb.append("<AGREEMENTNO>860114020001</AGREEMENTNO>");
        sb.append("<PREMIUM>" + this.oremium + "</PREMIUM>");
        sb.append("<AMOUNT>" + this.amount + "</AMOUNT>");//3205000.00

        sb.append("<BENEFMODE>0</BENEFMODE>");
        sb.append("<AIRLINEDATE>" + AIRLINEDATE + "</AIRLINEDATE>");//航班起飞时间|发车时间airlinedate
        sb.append("<AIRLINENO>" + insuruser.getFlyno() + "</AIRLINENO>");//airlineno
        sb.append("<INSUREDLIST>");
        sb.append("<INSURED>");
        try {
            String name = insuruser.getName();
            if (isencode == 1) {
                name = java.net.URLEncoder.encode(name, "GBK");
            }
            sb.append("<INSUREDNAME>" + name + "</INSUREDNAME>");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        sb.append("<RELATIONSHIP>10</RELATIONSHIP>");//这个值是投保人与被保人的关系
        sb.append("<INSUREDIDNO>" + insuruser.getCode() + "</INSUREDIDNO>");//// 乘客证件号码insuredidno
        sb.append("<INSUREDIDTYPE>" + codeTypeDB2Sunshine(insuruser.getCodetype()) + "</INSUREDIDTYPE>");//乘客证件类型
        sb.append("<INSUREDBIRTHDAY>" + birthday + "</INSUREDBIRTHDAY>");
        sb.append("<INSUREDMOBILE>" + insuruser.getMobile() + "</INSUREDMOBILE>");
        String email = insuruser.getEmail() == null ? eMailPath : insuruser.getEmail();
        sb.append("<INSUREDEMAIL>" + email + "</INSUREDEMAIL>");
        sb.append("</INSURED>");
        sb.append("</INSUREDLIST>");
        sb.append("</POLICYINFO>");
        sb.append("</ORDER>");
        sb.append("</INSURENCEINFO>");
        return sb.toString();
    }

    public String getSign(String key, String data, String log_name, Long l1) {
        String sign_String = key + data;
        //        WriteLog.write(log_name, l1 + ":sign:String:" + sign_String);
        System.out.println("md5zhiqian:" + sign_String);
        try {
            sign_String = SunshineInsuranceTrainMD5Util.getMD5ofStr(sign_String.getBytes("GBK")).toLowerCase();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //        System.out.println("md5zhihou:" + sign_String);
        WriteLog.write(log_name, l1 + ":sign:" + sign_String);
        return sign_String;
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
    *  数据库证件类型转换成保险接口证件类型
    * @param timestamp
    * @return
    * @time 2015年6月24日 上午11:14:31
    * @author yinshubin
    */
    private String codeTypeDB2Sunshine(long codetypeDB) {
        //        乘客证件类型：10: 身份证11: 户口薄12: 驾驶证13: 军官证14: 士兵证17: 港澳通行证18: 台湾通行证99: 其他51: 护照61: 港台同胞证
        String cardtypeSunshine = "10";
        if (codetypeDB == 3) {//护照
            cardtypeSunshine = "51";
        }
        else if (codetypeDB == 1) {//身份证
            cardtypeSunshine = "10";
        }
        else if (codetypeDB == 4) {//港澳通行证
            cardtypeSunshine = "17";
        }
        else if (codetypeDB == 5) {//台湾通行证
            cardtypeSunshine = "18";
        }
        else if (codetypeDB == 6) {//台胞证
            cardtypeSunshine = "99";
        }
        else if (codetypeDB == 7) {//回乡证
            cardtypeSunshine = "99";
        }
        else if (codetypeDB == 8) {//军官证
            cardtypeSunshine = "13";
        }
        else {
            cardtypeSunshine = "10";
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

    public String getInterfaceFlag() {
        return interfaceFlag;
    }

    public void setInterfaceFlag(String interfaceFlag) {
        this.interfaceFlag = interfaceFlag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @Override
    public List<Insurances> orderAplylist(String jyNo, Customeruser user, List list, String begintime, String[] fltno)
            throws Exception {

        return null;
    }

    @Override
    public DataHandler PolicyReprint(Insurorder order) throws Exception {
        return null;
    }
}
