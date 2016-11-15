package com.uniproud.axis2.client;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ServiceXMLAnalysis {
  public static Namespace ns = null;
  /**
   * 用来解析XML的类
   * @param xmlDoc
   * @return Element
   */
  public Element xmlElements(String xmlDoc) {
    Element root = null;
    try {
      //创建一个新的字符串
      StringReader read = new StringReader(xmlDoc);
      //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
      InputSource source = new InputSource(read);
      //创建一个新的SAXBuilder
      SAXBuilder saxBuilder = new SAXBuilder();

      //通过输入源构造一个Document
      Document doc = saxBuilder.build(source);
      //取的根元素
      root = doc.getRootElement();
//      System.out.println(root.getName()); //输出根元素的名称（测试）
      ns = root.getNamespace();
      //得到根元素所有子元素的集合
      System.out.println(root);
    }
    catch (JDOMException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return root;
  }

  //得到
  public int getErrorFlag(Element bodyElent) {
    if (bodyElent == null) {
      return -10;
    }
    String errorFlag = "";
    String returnMessage = "";
    Element headerElent = bodyElent.getChild("Header"); //得到Header节点
    // get ErrorFlag
    errorFlag = headerElent.getChildText("ErrorFlag");
    errorFlag = errorFlag == null ? "" : errorFlag.trim();
        // get ReturnMessage
    returnMessage = headerElent.getChildText("ReturnMessage");
    returnMessage = returnMessage == null ? "" : returnMessage.trim();
    System.out.println("调用是否成功:" + errorFlag);
    System.out.println("调用结果信息:" + returnMessage);
    return Integer.parseInt(errorFlag);
  }

//得到XML反馈的XML的BODY中的Element
  public Element getBody(String xmlStr) {
    if (xmlStr == null || xmlStr.equals(""))
      return null;
    ServiceXMLAnalysis doc = new ServiceXMLAnalysis();
    Element bodyElent = null;
    try {
      Element uniproudSoap = null;
      uniproudSoap = doc.xmlElements(xmlStr); //开始解析xml字符串
      bodyElent = uniproudSoap.getChild("Body", ns); //得到body 节点
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return bodyElent;
  }

  //发送普通传真的反馈XML解析
  public static void getSendFaxToBack(String xmlStr) {
    if (xmlStr == null || xmlStr.equals(""))
      return;
    ServiceXMLAnalysis doc = new ServiceXMLAnalysis();
    try {
      Element bodyElent = doc.getBody(xmlStr);
      System.out.println("bodyElent==" + bodyElent);

      int errorFlag = doc.getErrorFlag(bodyElent);
      if (errorFlag != 0)
        return;
      //得到SendFaxToServerResult节点
      Element ResultElement = bodyElent.getChild("SendFaxToServerResponse").
          getChild("SendFaxToServerResult");
      String jobNo = ""; //作业号
      int JobResult = 0; //作业状态
      int TotalNum = 0; //递交份数
      int ValidNum = 0; //合格份数
      jobNo = ResultElement.getChildText("JobNo");
      JobResult = Integer.parseInt(ResultElement.getChildText("JobResult"));
      TotalNum = Integer.parseInt(ResultElement.getChildText("TotalNum"));
      ValidNum = Integer.parseInt(ResultElement.getChildText("ValidNum"));
      System.out.println("作业号:" + jobNo);
      System.out.println("作业状态:" + JobResult);
      System.out.println("递交份数:" + TotalNum);
      System.out.println("合格份数:" + ValidNum);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  //添加发送人发送的反馈XML解析
  public static void getSendPHFaxToBack(String xmlStr) {
    if (xmlStr == null || xmlStr.equals(""))
      return;
    ServiceXMLAnalysis doc = new ServiceXMLAnalysis();
    try {
      Element bodyElent = doc.getBody(xmlStr);
      int errorFlag = doc.getErrorFlag(bodyElent);
      if (errorFlag != 0)
        return;
      //得到SendPHFaxToServerResult节点
      Element ResultElement = bodyElent.getChild("SendPHFaxToServerResponse").
          getChild("SendPHFaxToServerResult");
      String jobNo = ""; //作业号
      int JobResult = 0; //作业状态
      int TotalNum = 0; //递交份数
      int ValidNum = 0; //合格份数
      jobNo = ResultElement.getChildText("JobNo");
      JobResult = Integer.parseInt(ResultElement.getChildText("JobResult"));
      TotalNum = Integer.parseInt(ResultElement.getChildText("TotalNum"));
      ValidNum = Integer.parseInt(ResultElement.getChildText("ValidNum"));
      System.out.println("作业号:" + jobNo);
      System.out.println("作业状态:" + JobResult);
      System.out.println("递交份数:" + TotalNum);
      System.out.println("合格份数:" + ValidNum);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  //查询所有未获取的清单的XML解析
  public static void getQueryForSendBack(String xmlStr) {
    if (xmlStr == null || xmlStr.equals(""))
      return;
    ServiceXMLAnalysis doc = new ServiceXMLAnalysis();
    try {
      Element bodyElent = doc.getBody(xmlStr);
      int errorFlag = doc.getErrorFlag(bodyElent);
      if (errorFlag != 0)
        return;
      Element ResultElement = bodyElent.getChild(
          "QueryResultForSendTaskResponse").
          getChild("QueryResultForSendTaskResult");
      int SendFaxResultNum = Integer.parseInt(ResultElement.getChildText(
          "SendFaxResultNum"));
      System.out.println("查询到的任务个数:" + SendFaxResultNum);
      List SendFaxResultList = ResultElement.getChild("SendFaxResultList").
          getChildren("SendFaxResult");
      Element et = null;
      int ClientTaskID = 0;
      int ServerTaskID = 0;
      int result = 0;
      String startTime = "";
      int CostTime = 0;
      int NumberOfPages = 0;
      int SendTimes = 0;
      double BillingFee = 0;
      String DidNumber = "";
      for (int i = 0; i < SendFaxResultList.size(); i++) {
        et = (Element) SendFaxResultList.get(i);
        ClientTaskID = Integer.parseInt(et.getChildText("ClientTaskID"));
        ServerTaskID = Integer.parseInt(et.getChildText("ServerTaskID"));
        result = Integer.parseInt(et.getChildText("result"));
        startTime = et.getChildText("startTime");
        CostTime = Integer.parseInt(et.getChildText("CostTime"));
        NumberOfPages = Integer.parseInt(et.getChildText("NumberOfPages"));
        SendTimes = Integer.parseInt(et.getChildText("SendTimes"));
        BillingFee = Double.parseDouble(et.getChildText("BillingFee"));
        DidNumber = et.getChildText("DidNumber");
        System.out.println("-------------------------------------" + i +
                           "--------------------------------------");
        System.out.println("客户端任务ID:" + ClientTaskID);
        System.out.println("服务端任务ID:" + ServerTaskID);
        System.out.println("发送结果:" + result);
        System.out.println("发送开始时间:" + startTime);
        System.out.println("发送时长.单位秒:" + CostTime);
        System.out.println("发送页数:" + NumberOfPages);
        System.out.println("发送次数:" + SendTimes);
        System.out.println("发送费用:" + BillingFee);
        System.out.println("Didumber:" + DidNumber);
        System.out.println("--------------------------------------------------------------------------------");
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  //接收传真
  public static void getQueryForRecvBack(String xmlStr) {
    if (xmlStr == null || xmlStr.equals(""))
      return;
    ServiceXMLAnalysis doc = new ServiceXMLAnalysis();
    try {
      Element bodyElent = doc.getBody(xmlStr);
      int errorFlag = doc.getErrorFlag(bodyElent);
      if (errorFlag != 0 && errorFlag != -15)
        return;
      Element ResultElement = bodyElent.getChild(
          "QueryResultForRecvTaskResponse").getChild(
          "QueryResultForRecvTaskResult");
      Element RecvFaxResult = ResultElement.getChild("RecvFaxResult");
      int ServerTaskID = 0;
      int result = 0;
      String startTime = "";
      int CostTime = 0;
      int NumOfPages = 0;
      String RecvFileName = "";
      int SendTimes = 0;
      double BillingFee = 0;
      String DidNumber = "";
      ServerTaskID = Integer.parseInt(RecvFaxResult.getChildText("ServerTaskID"));
      result = Integer.parseInt(RecvFaxResult.getChildText("Result"));
      startTime = RecvFaxResult.getChildText("StartTime");
      CostTime = Integer.parseInt(RecvFaxResult.getChildText("CostTime"));
      NumOfPages = Integer.parseInt(RecvFaxResult.getChildText("NumOfPages"));
      RecvFileName = RecvFaxResult.getChildText("RecvFileName");
      BillingFee = Double.parseDouble(RecvFaxResult.getChildText("BillingFee"));
      DidNumber = RecvFaxResult.getChildText("DidNumber");
      System.out.println(
          "-------------------------------------------------------------------");
      System.out.println("服务端任务ID:" + ServerTaskID);
      System.out.println("接收结果:" + result);
      System.out.println("接收开始时间:" + startTime);
      System.out.println("接收时长.单位秒:" + CostTime);
      System.out.println("接收页数:" + NumOfPages);
      System.out.println("接收传真文件名称:" + RecvFileName);
      System.out.println("接收费用:" + BillingFee);
      System.out.println("Didumber:" + DidNumber);
      System.out.println(
          "-------------------------------------------------------------------");
      String FileName = ResultElement.getChild("Document").getAttribute(
          "FileName").getValue();
      String EncodingType = ResultElement.getChild("Document").getAttribute(
          "EncodingType").getValue();
      String DocumentExtension = ResultElement.getChild("Document").
          getAttribute("DocumentExtension").getValue();
      String DocumentText = ResultElement.getChild("Document").getText();
      DocumentText = DocumentText == null ? "" : DocumentText.trim();
      Base64 base64 = new Base64();
      boolean ifok = base64.tranfile(DocumentText, "c://" + FileName);
      if (ifok) {
        System.out.println("文件存放在---" + "c://" + FileName);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  //接收传真返回ZIP
  public static void getQueryForRecvZIPBack(String xmlStr) {
    if (xmlStr == null || xmlStr.equals(""))
      return;
    ServiceXMLAnalysis doc = new ServiceXMLAnalysis();
    try {
      Element bodyElent = doc.getBody(xmlStr);
      int errorFlag = doc.getErrorFlag(bodyElent);
      if (errorFlag != 0 && errorFlag != -15 && errorFlag != -16)
        return;
      Element ResultElement = bodyElent.getChild(
          "QueryResultForRecvTaskZipResponse").getChild(
          "QueryResultForRecvTaskZipResult");
      int RecvFaxResultNum = Integer.parseInt(ResultElement.getChildText(
          "RecvFaxResultNum"));
      System.out.println("接收到的" + RecvFaxResultNum + "个传真！");
      List RecvFaxResult = ResultElement.getChild("RecvFaxResultList").
          getChildren("RecvFaxResult");
      Element et = null;
      int ServerTaskID = 0;
      int Result = 0;
      String StartTime = "";
      int CostTime = 0;
      int NumOfPages = 0;
      String RecvFileName = "";
      double BillingFee = 0;
      String DidNumber = "";
      for (int i = 0; i < RecvFaxResult.size(); i++) {
        et = (Element) RecvFaxResult.get(i);
        ServerTaskID = Integer.parseInt(et.getChildText("ServerTaskID"));
        Result = Integer.parseInt(et.getChildText("Result"));
        StartTime = et.getChildText("StartTime");
        CostTime = Integer.parseInt(et.getChildText("CostTime"));
        NumOfPages = Integer.parseInt(et.getChildText("NumOfPages"));
        BillingFee = Double.parseDouble(et.getChildText("BillingFee"));
        RecvFileName = et.getChildText("RecvFileName");
        DidNumber = et.getChildText("DidNumber");
        System.out.println("------------------------" + i +
                           "---------------------------------");
        System.out.println("服务端任务ID:" + ServerTaskID);
        System.out.println("接收结果:" + Result);
        System.out.println("接收开始时间:" + StartTime);
        System.out.println("接收时长.单位秒:" + CostTime);
        System.out.println("接收页数:" + NumOfPages);
        System.out.println("接收传真文件名称:" + RecvFileName);
        System.out.println("接收费用:" + BillingFee);
        System.out.println("Didumber:" + DidNumber);
        System.out.println(
            "--------------------------------------------------------------");
      }
      if (errorFlag == 15) {
        System.out.println("传真文件已删除");
      }
      else if(RecvFaxResultNum > 0){
        String FileName = ResultElement.getChild("Document").getAttribute(
            "FileName").getValue();
        String EncodingType = ResultElement.getChild("Document").getAttribute(
            "EncodingType").getValue();
        String DocumentExtension = ResultElement.getChild("Document").
            getAttribute("DocumentExtension").getValue();
        String DocumentText = ResultElement.getChild("Document").getText();
        DocumentText = DocumentText == null ? "" : DocumentText.trim();
        Base64 base64 = new Base64();
        boolean ifok = base64.tranfile(DocumentText, "c://" + FileName);
        if (ifok) {
          System.out.println("文件存放在---" + "c://" + FileName);
        }

      }

    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

//删除发送传真文件
  public static void getDeleteFileForSendTask(String xmlStr) {
    if (xmlStr == null || xmlStr.equals(""))
      return;
    ServiceXMLAnalysis doc = new ServiceXMLAnalysis();
    try {
      Element bodyElent = doc.getBody(xmlStr);
      int errorFlag = doc.getErrorFlag(bodyElent);
      if (errorFlag != 0)
        return;
      Element ResultElement = bodyElent.getChild(
          "DeleteFileForSendTaskResponse").getChild(
          "DeleteFileForSendTaskResult");
      int DeleteFaxNum = Integer.parseInt(ResultElement.getChildText(
          "DeleteFaxNum"));
      System.out.println("成功删除了" + DeleteFaxNum + "个文件！");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  //删除接收传真文件
  public static void getDeleteFileForRecvTask(String xmlStr) {
    if (xmlStr == null || xmlStr.equals(""))
      return;
    ServiceXMLAnalysis doc = new ServiceXMLAnalysis();
    try {
      Element bodyElent = doc.getBody(xmlStr);
      int errorFlag = doc.getErrorFlag(bodyElent);
      if (errorFlag != 0)
        return;
      Element ResultElement = bodyElent.getChild(
          "DeleteFileForRecvTaskResponse").getChild(
          "DeleteFileForRecvTaskResult");
      int DeleteFaxNum = Integer.parseInt(ResultElement.getChildText(
          "DeleteFaxNum"));
      System.out.println("成功删除了" + DeleteFaxNum + "个文件！");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  //获取用户信息
  public static void getGetUserInfo(String xmlStr) {
    if (xmlStr == null || xmlStr.equals(""))
      return;
    ServiceXMLAnalysis doc = new ServiceXMLAnalysis();
    try {
      Element bodyElent = doc.getBody(xmlStr);
      int errorFlag = doc.getErrorFlag(bodyElent);
      if (errorFlag != 0)
        return;
      Element ResultElement = bodyElent.getChild("GetUserInfoResponse").
          getChild("GetUserInfoResult").getChild("UserInfo");
      String UserName = ResultElement.getChildText("UserName");
      System.out.println("用户名：" + UserName);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  //增加用户信息
  public static void getAddUserInfo(String xmlStr) {
    if (xmlStr == null || xmlStr.equals(""))
      return;
    ServiceXMLAnalysis doc = new ServiceXMLAnalysis();
    try {
      Element bodyElent = doc.getBody(xmlStr);
      int errorFlag = doc.getErrorFlag(bodyElent);
      if (errorFlag != 0)
        return;
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  //修改密码
  public static void getChangePassword(String xmlStr) {
    if (xmlStr == null || xmlStr.equals(""))
      return;
    ServiceXMLAnalysis doc = new ServiceXMLAnalysis();
    try {
      Element bodyElent = doc.getBody(xmlStr);
      int errorFlag = doc.getErrorFlag(bodyElent);
      if (errorFlag != 0)
        return;
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  //修改用户信息
  public static void getModifyUserInfoBack(String xmlStr) {
    if (xmlStr == null || xmlStr.equals(""))
      return;
    ServiceXMLAnalysis doc = new ServiceXMLAnalysis();
    try {
      Element bodyElent = doc.getBody(xmlStr);
      int errorFlag = doc.getErrorFlag(bodyElent);
      if (errorFlag != 0)
        return;
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}