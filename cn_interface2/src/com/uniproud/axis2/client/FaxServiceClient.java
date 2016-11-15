package com.uniproud.axis2.client;
/**
 * ��ͨ�������ռ�����Ϣ
 * GuestCompany uniproud
 * author shqv
 */

import java.util.Iterator;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

public class FaxServiceClient {//64.187.11.116
//    private static EndpointReference targetEPR = new EndpointReference("http://202.100.80.24:8081/services/BnetAuthentication");
    private OMElement result = null;
    private static EndpointReference targetEPR = new EndpointReference("http://60.10.59.126/servlet/services/FaxService");
    public static OMElement getEchoOMElement(String xml){
        //�̶���ʽ
        OMFactory fac = OMAbstractFactory.getOMFactory();
        //��һ�����Ϊ�̶���ʽ�����Ը��
//        OMNamespace omNs = fac.createOMNamespace("bnetesb", "BnetAuthentication");//
        OMNamespace omNs = fac.createOMNamespace("http://axis2.fax.uniproud.com", "FaxToServer");//
        OMElement method = fac.createOMElement("FaxToServer", omNs);
        OMElement value = fac.createOMElement("Text", omNs);
        value.addChild(fac.createOMText(value, xml));
        method.addChild(value);
//        System.out.println("method=="+method);
        return method;
    }

    org.apache.axis2.client.async.Callback callback = new org.apache.axis2.client.async.Callback() {
        public void onComplete(org.apache.axis2.client.async.AsyncResult asyncResult) {
          result = asyncResult.getResponseEnvelope();
        }
        public void onError(Exception e) {
            e.printStackTrace();
        }
    };
    //�첽����
    public String Asynchronous(String xmlStr,String methodName){
      String fileXml = "";
      ServiceClient sender = null;
      try {
        OMElement payload = getEchoOMElement(xmlStr);
        Options options = new Options();
        options.setTo(targetEPR);
        options.setAction(methodName); //���ʵķ�����

        options.setTimeOutInMilliSeconds(60000); //���ó�ʱʱ��
        //Blocking invocation
        sender = new ServiceClient();
        sender.setOptions(options);
        sender.sendReceiveNonBlocking(payload, callback);
        while (!callback.isComplete()) {
          Thread.sleep(1000);
        }
        OMElement _fileName = null; //�ļ���
//        System.out.println("��ӡ���" + result);
        for (Iterator _iterator = result.getChildElements(); _iterator.hasNext(); ) {
          OMElement _ele = (OMElement) _iterator.next();
          _ele = _ele.getFirstElement().getFirstElement();
          if (_ele.getLocalName().equalsIgnoreCase("return")) {
            _fileName = _ele;
            fileXml = _fileName.getText();
          }
        }
      }catch (AxisFault axisFault) {
        axisFault.printStackTrace();
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      finally {
        try {
          sender.cleanup();
        }
        catch (AxisFault axisFault) {
          //have to ignore this
        }
      }
      return fileXml;
    }
    /**
     * @param xml
     * @param methodName
     * @return
     */
    public String SendReceive(String xmlStr,String methodName){
         String fileXml = "";
         ServiceClient sender = null;
        try {
          OMElement payload = getEchoOMElement(xmlStr);
          Options options = new Options();
          options.setTo(targetEPR);
          options.setAction(methodName);//���ʵķ�����

          options.setTimeOutInMilliSeconds(5000000); //���ó�ʱʱ��
          //Blocking invocation
          sender = new ServiceClient();
          sender.setOptions(options);

        /**
         * Callback to handle the response
         * �첽���� begin
          Callback callback = new Callback() {
              public void onComplete(AsyncResult result) {
                  System.out.println(result.getResponseEnvelope());
              }

              public void onError(Exception e) {
                  e.printStackTrace();
              }
          };
          sender.sendReceiveNonBlocking(payload, callback);
          while (!callback.isComplete()) {
            Thread.sleep(1000);
          }
          **///end
         System.out.println("payload:"+payload);
         result = sender.sendReceive(payload);//��ʼ����XML
          OMElement _fileName = null;//�ļ���
//          System.out.println("��ӡ���"+result);
//          System.out.println("ͬ������:"+result);
          for (Iterator _iterator = result.getChildElements(); _iterator.hasNext();) {
                   OMElement _ele = (OMElement) _iterator.next();

            if (_ele.getLocalName().equalsIgnoreCase("return")) {
                   _fileName = _ele;
            }
            fileXml = _fileName.getText();
          }
        }catch(AxisFault axisFault){
          axisFault.printStackTrace();
        }catch(Exception ex){
          ex.printStackTrace();
        }finally {
            try {
                sender.cleanup();
            } catch (AxisFault axisFault) {
                //have to ignore this
            }
        }
        return fileXml;
      }

      public static void main(String[] args) {
        FaxServiceClient client = new FaxServiceClient();
//        String sendFaxToBack = client.SendReceive(ToServerXML.getSendSmsToClientXML(),"urn:sendSms");                   //
//        System.out.println("SendSms...xml��"+sendFaxToBack);                                            //


//         String sendFaxToBack = client.SendReceive("wweqwqewwq","urn:call");
/////////////////////////////////////////////��ͨ���ͣ����ռ�����Ϣ SendFaxToServer����///////////////////////////////////////////////
        String sendFaxToBack = client.SendReceive(ToServerXML.getSendFaxToClientXML("70270281","000000","01068179400","D://hotelfax/hotelfaxH21023.doc"),"urn:SendFaxToServer");                   
        System.out.println("��ͨ����SendFaxToServer�����ķ�!��Ϣxml��"+sendFaxToBack);                                            
        ServiceXMLAnalysis.getSendFaxToBack(sendFaxToBack);//������ͨ���ͷ�!��Ϣ                                                 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////����ռ��˷��� SendPHFaxToServer����/////////////////////////////////////////////////////////////
//        String sendPHFaxToBack = client.SendReceive(ToServerXML.getSendPHFaxToClientXML(),"urn:SendPHFaxToServer");                   //
//        ServiceXMLAnalysis.getSendPHFaxToBack(sendPHFaxToBack);//��ӷ����˷��ʹ��淴!��Ϣ����                                           //
//        System.out.println("����ռ��˷���SendPHFaxToServer�����ķ�!��Ϣxml��"+sendPHFaxToBack);                                         //
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////��ѯ����δ��ȡ���嵥 QueryResultForSendTask����///////////////////////////////////////////////////
//        String queryForSendBack = client.SendReceive(ToServerXML.getQueryResultForSendTaskXML(),"urn:QueryResultForSendTask");        //
//        System.out.println("queryForSendBack:"+queryForSendBack);
//        ServiceXMLAnalysis.getQueryForSendBack(queryForSendBack);//�����ѯ�����嵥��!��Ϣ                                               //
//        System.out.println("��ѯ����δ��ȡ���嵥QueryResultForSendTask�����ķ�!��Ϣxml��"+queryForSendBack);                               //
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////���մ��� QueryResultForRecvTask����//////////////////////////////////////////////////////////////
//        String queryForRecvBack = client.SendReceive(ToServerXML.getQueryResultForRecvTaskXML(),"urn:QueryResultForRecvTask");         //
//        ServiceXMLAnalysis.getQueryForRecvBack(queryForRecvBack);//������մ��淴!��Ϣ                                                  //
//        System.out.println("���մ���QueryResultForRecvTask�����ķ�!��Ϣxml��"+queryForRecvBack);                                         //
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////����ZIP������Ϣ(һ����ഫ10��) QueryResultForRecvTaskZip����///////////////////////////////////////
//        String queryForRecvZIPBack = client.SendReceive(ToServerXML.getQueryResultForRecvTaskZipXML(),"urn:QueryResultForRecvTaskZip"); //
//        ServiceXMLAnalysis.getQueryForRecvZIPBack(queryForRecvZIPBack);//�������ZIP������Ϣ                                              //
//        System.out.println("����ZIP������ϢQueryResultForRecvTaskZip�����ķ�!��Ϣxml��"+queryForRecvZIPBack);                             //
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////ɾ���ʹ����ļ� DeleteFileForSendTask����//////////////////////////////////////////////////////
//         String deleteFileForSendTaskBack = client.SendReceive(ToServerXML.getDeleteFileForSendTask(),"urn:DeleteFileForSendTask");  //
//         ServiceXMLAnalysis.getDeleteFileForSendTask(deleteFileForSendTaskBack);//ɾ���ʹ����ļ�                                     //
//         System.out.println("ɾ���ʹ����ļ�DeleteFileForSendTask�����ķ�!��Ϣxml��"+deleteFileForSendTaskBack);                       //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////ɾ����մ����ļ� DeleteFileForRecvTask����/////////////////////////////////////////////////////
//         String deleteFileForRecvTaskBack = client.SendReceive(ToServerXML.getDeleteFileForRecvTask(),"urn:DeleteFileForRecvTask"); //
//         ServiceXMLAnalysis.getDeleteFileForRecvTask(deleteFileForRecvTaskBack);//ɾ���ʹ����ļ�                                    //
//         System.out.println("ɾ���ʹ����ļ�DeleteFileForSendTask�����ķ�!��Ϣxml��"+deleteFileForRecvTaskBack);                      //
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////��Ӳ�����Ϣ AddDeptInfo����/////////////////////////////////////////////////////////////////
//            String getAddDeptInfoBack = client.SendReceive(ToServerXML.getAddDeptInfoXML(),"urn:AddDeptInfo");
//            System.out.println("��ȡ�û���ϢAddUserInfo�����ķ�!��Ϣxml��"+getAddDeptInfoBack);
//            ServiceXMLAnalysis.getAddUserInfo(getAddDeptInfoBack);//��ȡ�û���Ϣ                                                           //

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 ////////////////////////////////////////////�޸Ĳ�����Ϣ ModifyDeptInfo����/////////////////////////////////////////////////////////////////
//            String getModifyDeptInfoBack = client.SendReceive(ToServerXML.getModifyDeptInfoXML(),"urn:ModifyDeptInfo");
//            System.out.println("��ȡ�û���ϢModifyDeptInfo�����ķ�!��Ϣxml��"+getModifyDeptInfoBack);
//            ServiceXMLAnalysis.getAddUserInfo(getModifyDeptInfoBack);//��ȡ�û���Ϣ                                                           //

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////����û���Ϣ AddUserInfo����/////////////////////////////////////////////////////////////////
//        String getUserInfoBack = client.SendReceive(ToServerXML.getAddUserInfoXML(),"urn:AddUserInfo");
//        System.out.println("��ȡ�û���ϢAddUserInfo�����ķ�!��Ϣxml��"+getUserInfoBack);
//        ServiceXMLAnalysis.getAddUserInfo(getUserInfoBack);//��ȡ�û���Ϣ                                                           //
//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////��ȡ�û���Ϣ GetUserInfo����/////////////////////////////////////////////////////////////////
//        String getUserInfoBack = client.SendReceive(ToServerXML.getGetUserInfoXML(),"urn:GetUserInfo");
//        System.out.println("��ȡ�û���ϢGetUserInfo�����ķ�!��Ϣxml��"+getUserInfoBack);
//        ServiceXMLAnalysis.getGetUserInfo(getUserInfoBack);//��ȡ�û���Ϣ                                                           //
//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////�޸����� ChangePassword����/////////////////////////////////////////////////////////////////
//         String changePasswordBack = client.SendReceive(ToServerXML.getChangePasswordXML(),"urn:ChangePassword");                //
//         ServiceXMLAnalysis.getChangePassword(changePasswordBack);//��ȡ�û���Ϣ                                                   //
//         System.out.println("�޸�����ChangePassword�����ķ�!��Ϣxml��"+changePasswordBack);                                          //
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////�޸��û���Ϣ ModifyUserInfo����//////////////////////////////////////////////////////////////
//          String modifyUserInfoBack = client.SendReceive(ToServerXML.getModifyUserInfoXML(),"urn:ModifyUserInfo");                //
//          ServiceXMLAnalysis.getModifyUserInfoBack(modifyUserInfoBack);//�޸��û���Ϣ                                               //
//          System.out.println("�޸��û���ϢModifyUserInfo�����ķ�!��Ϣxml��"+modifyUserInfoBack);                                       //
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      }
}


















