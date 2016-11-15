package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.encoding.XMLType;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.log4j.Logger;

import client.PayServiceStub;
import client.PayServiceStub.AppendParam;
import client.PayServiceStub.Coding;
import client.PayServiceStub.MakeMD5;
import client.PayServiceStub.MobilePay;

import com.ccservice.b2b2c.atom.pay.handle.PayHandle;
import com.ccservice.b2b2c.atom.pay.helper.Payhelper;

/**
 * @author hanmenghui
 *  地球通宝支付。
 *
 */
@SuppressWarnings("serial")
public class Globalvillagepay extends PaySupport implements Pay {
    public Globalvillagepay(HttpServletRequest request, HttpServletResponse response, Payhelper payhelper) {
        super(request, response, payhelper);
    }

    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private String MCode = ""; //"doniel"; //买家帐号

    private String PayPassword = ""; //"aB123456";//买家密码

    public void pay(float factorage) throws Exception {
        logger.error("地球村支付开始");
        // New 一个WebService对像
        int i = 0;
        PayServiceStub payService;
        try {
            //地球通宝账号
            MCode = new String(request.getParameter("MCode").getBytes("ISO8859-1"), "UTF-8");
            //地球通宝支付密码
            PayPassword = new String(request.getParameter("PayPassword").getBytes("ISO8859-1"), "UTF-8");
            String VerCode = new String(request.getParameter("VerCode").getBytes("ISO8859-1"), "UTF-8");
            String GUID = new String(request.getParameter("GUID").getBytes("ISO8859-1"), "UTF-8");
            payService = new PayServiceStub();
            PayServiceStub.MobilePay mp = new MobilePay();
            String orderprice = payhelper.getOrderprice() + factorage + "";

            String url = "http://external.dqc100.com:8090/InterfaceHost/SookShop.asmx";
            //在浏览器中打开url，可以找到SOAPAction: "http://www.chinsoft.com.cn/SendMQ"
            String namespace = "http://www.dqc100.com/";
            String actionUri = "AccountBalanceTransfer"; //Action路径
            String op = "AccountBalanceTransfer"; //要调用的方法名
            String strPayPwd = "";
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new java.net.URL(url));
            call.setUseSOAPAction(true);
            call.setSOAPActionURI(namespace + "DesEncrypt"); // action uri
            call.setOperationName(new QName(namespace, "DesEncrypt"));// 设置要调用哪个方法
            // 设置参数名称，具体参照从浏览器中看到的
            call.addParameter(new QName(namespace, "strEncrypt"), XMLType.XSD_STRING, ParameterMode.IN);
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING); // 要返回的数据类型
            //String sendTime = "2011-07-14 13:05:32";
            Object[] params = new Object[] { PayPassword };
            strPayPwd = (String) call.invoke(params); //方法执行后的返回值
            System.out.println(strPayPwd);
            call.clearOperation();
            //加密结束 支付开始

            call = (Call) service.createCall();
            call.setTargetEndpointAddress(new java.net.URL(url));
            //call.setUsername("xuan"); // 用户名（如果需要验证）
            //call.setPassword("123456"); // 密码
            call.setUseSOAPAction(true);
            call.setSOAPActionURI(namespace + actionUri); // action uri
            call.setOperationName(new QName(namespace, op));// 设置要调用哪个方法
            // 设置参数名称，具体参照从浏览器中看到的
            call.removeAllParameters();
            call.addParameter(new QName(namespace, "VerCode"), XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter(new QName(namespace, "Guid"), XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter(new QName(namespace, "FromAccountName"), XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter(new QName(namespace, "ToAccountName"), XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter(new QName(namespace, "strPayPwd"), XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter(new QName(namespace, "toSum"), XMLType.XSD_DECIMAL, ParameterMode.IN);
            call.addParameter(new QName(namespace, "toRemark"), XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter(new QName(namespace, "accreditCode"), XMLType.XSD_STRING, ParameterMode.IN);
            //call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING); // 要返回的数据类型
            //String sendTime = "2011-07-14 13:05:32";
            System.out.println("支付了" + orderprice + "元！");
            params = new Object[] { VerCode, GUID, MCode, "DQC100", strPayPwd, orderprice, "机票转帐", "www.dqcpay.com" };
            String result = (String) call.invoke(params); //方法执行后的返回值
            System.out.println("支付成功");

            PayHandle payhandle = (PayHandle) Class.forName(
                    PayHandle.class.getPackage().getName() + "." + payhelper.getHandleName()).newInstance();
            payhandle.orderHandle(payhelper.getOrdernumber(), "", payhelper.getOrderprice(), 4, "");

            i = 1;
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("支付失败");
            e.printStackTrace();
        }
        try {

            //			PrintWriter out = response.getWriter();
            //			response.setContentType("text/plain; charset=GBK");
            if (i == 1) {
                response.sendRedirect("paysuccess.jsp");
                //out.print("<html><body style='text-align: center;'>您的订单已支付成功！</body></html>");
            }
            else {
                response.sendRedirect("payfiled.jsp");
                //out.print("<html><body style='text-align: center;'>您的订单支付失败！</body></html>");
            }
            //			out.flush();
            //			out.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String GetData(Payhelper payhelper) {
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();
        format.applyPattern("###0.00");
        try {
            // New 一个Websevrice 服务对像
            PayServiceStub payService = new PayServiceStub();

            // 基本信息，可从配置文件中获取
            String strKey = "51Mcsoft"; // 加密与解密的密钥
            String MerID = "BLD.DQC100.COM"; // 总商户名，由地球通宝后台添加
            String MD5Key = "4EC14F7F7A89470B01191F1FC09FB93B"; // 站点的MD5Key由地球
            // MerID和MD5Key
            // 是正式可用的

            // 订单列表
            String strOrderList = "";

            String OrderNo = payhelper.getOrdernumber();// 订单编号
            String ProductName = payhelper.getOrdername();// 产品名称

            String SMCode = "DQC100";// 卖家账号
            String Seller = "地球村";// 卖家名称

            String OrderPrice = format.format(payhelper.getOrderprice());// 订单金额（实际支付金额）

            strOrderList += "<OrderList>";
            strOrderList += "<OrderNo>" + OrderNo + "</OrderNo>";
            strOrderList += "<ProductName>" + ProductName + "</ProductName>";
            strOrderList += "<SMCode>" + SMCode + "</SMCode>";
            strOrderList += "<Seller>" + Seller + "</Seller>";
            strOrderList += "<OrderPrice>" + OrderPrice + "</OrderPrice>";
            strOrderList += "</OrderList>";

            // 生成签名串
            String signMsg = "";
            String signMsgVal = "";
            String Guarantee = "0";

            String Remark = "";

            PayServiceStub.AppendParam parm = new AppendParam();

            parm.setParamID("MerID");

            parm.setParamValue(MerID);

            parm.setReturnStr(signMsgVal);

            signMsgVal = payService.appendParam(parm).getAppendParamResult();

            System.out.println("                                 " + signMsgVal);

            parm.setParamID("MD5Key");

            parm.setParamValue(MD5Key);

            parm.setReturnStr(signMsgVal);

            signMsgVal = payService.appendParam(parm).getAppendParamResult();

            System.out.println("                                 " + signMsgVal);

            parm.setParamID("Guarantee");

            parm.setParamValue(Guarantee);

            parm.setReturnStr(signMsgVal);

            signMsgVal = payService.appendParam(parm).getAppendParamResult();

            System.out.println("                                 " + signMsgVal);

            parm.setParamID("MCode");

            parm.setParamValue(MCode);

            parm.setReturnStr(signMsgVal);

            signMsgVal = payService.appendParam(parm).getAppendParamResult();

            System.out.println("                                 " + signMsgVal);

            parm.setParamID("PayPassword");

            parm.setParamValue(PayPassword);

            parm.setReturnStr(signMsgVal);

            signMsgVal = payService.appendParam(parm).getAppendParamResult();

            System.out.println("                                 " + signMsgVal);

            parm.setParamID("Remark");

            parm.setParamValue(Remark);

            parm.setReturnStr(signMsgVal);

            signMsgVal = payService.appendParam(parm).getAppendParamResult();

            System.out.println("                                 " + signMsgVal);

            parm.setParamID("OrderList");

            parm.setParamValue(strOrderList);

            parm.setReturnStr(signMsgVal);

            signMsgVal = payService.appendParam(parm).getAppendParamResult();

            System.out.println("signMsgVal：" + signMsgVal);

            PayServiceStub.MakeMD5 makeMD5 = new MakeMD5();

            makeMD5.setSignMsgVal(signMsgVal.toString());

            makeMD5.setStrKey(strKey);// 这个不能写掉

            signMsg = payService.makeMD5(makeMD5).getMakeMD5Result().toUpperCase();

            System.out.println("signMsg:" + signMsg);

            // 生成XML串并加密、编码
            StringBuffer strXML = new StringBuffer();
            strXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
            strXML.append("<Result><PostData>");
            strXML.append("<BaseInfo>");
            strXML.append("<MerID>" + MerID + "</MerID>");
            strXML.append("<Guarantee>" + Guarantee + "</Guarantee>");
            strXML.append("<MCode>" + MCode + "</MCode>");
            strXML.append("<PayPassword>" + PayPassword + "</PayPassword>");
            strXML.append("<Remark>" + Remark + "</Remark>");
            strXML.append("<signMsg>" + signMsg + "</signMsg>");
            strXML.append("</BaseInfo>");
            strXML.append(strOrderList);// 订单列表
            strXML.append("</PostData></Result>");

            System.out.println("strXML:" + strXML);

            PayServiceStub.Coding coding = new Coding();

            coding.setStrKey(strKey);

            coding.setStrXML(strXML.toString());

            PayServiceStub.CodingResponse res = payService.coding(coding);
            String postData = res.getCodingResult();

            System.out.println("pstdata::" + postData);

            // String strPostData = PayService.Coding(strXML.oString(), strKey);
            // // 成功PostData加密串

            return postData;

        }
        catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void pay(long agentid) throws Exception {
        // TODO Auto-generated method stub

    }

}
