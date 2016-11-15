package com.uniproud.axis2.client;

/**
 * 生成测试用的客户端XML字符串
 * GuestCompany uniproud
 * author shqv
 */

public class ToServerXML {
  public ToServerXML() {}
    public static String getSendSmsToClientXML() {
    String xml = "";
    return  xml;
    }
  /**
   * 普通无收件人信息的传真
   * @return
   */
  public static String getSendFaxToClientXML(String userID,String password,String faxnum,String filename) {
    String documentExtension = filename.substring(filename.lastIndexOf(".") + 1,
    		filename.length());
    Base64 base64 = new Base64();
    String document = base64.getEncodedText(filename);
    System.out.println("documentExtension===" + documentExtension);
    String xml =
        "<?xml version=\"1.0\" encoding=\"gb2312\" ?>" +
        "<FaxInfo xmlns=\"http://www.uniproud.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.uniproud.com fax_schema.xsd\">" +
        "<SchemaVersion>1.1</SchemaVersion>" +
        "<Login>" +
        "<UserID>"+userID+"</UserID>" +//用户名
        "<Password>"+password+"</Password>" +//密码
        "</Login>" +
        "<FaxOptions>" +
        "<Resolution>2</Resolution>" +//传真精度.0-低,1-中,2-高
        "<Priority>0</Priority>" +//优先级.1-低,2-高
        "<FeedBackMode>0</FeedBackMode>" +//反馈方式.0-无,1-Email反馈,2-传真,3-短信反馈
        "<FaxToneDetectTimeout>17</FaxToneDetectTimeout>" +//传真音检测时长
        "<PromptVoiceChoice>-1</PromptVoiceChoice>" +//提示音.-1-群发提示音,0-默认提示音,>1自定义
        "<FaxSidFlag>0</FaxSidFlag>" +//是否启用FaxSID.0-否,1-是
        "<FaxSid>aafdasfdas</FaxSid>" +//个性化传真标识FaxSID.FaxSidFlag为1时有效.
        "<StartSecond1>-1</StartSecond1>" +//工作时段1起始时间,单位秒.-1表示无限制
        "<EndSecond1>-1</EndSecond1>" +//工作时段1截止时间,单位秒.-1表示无限制
        "<StartSecond2>-1</StartSecond2>" +//工作时段2起始时间,单位秒.-1表示无限制.
         "<EndSecond2>-1</EndSecond2>" +//工作时段2截止时间,单位秒.-1表示无限制
        "</FaxOptions>" +//
        "<SendTaskList>" +//
        "<TotalNum>1</TotalNum> " + //传真个数.最大值1000.如果个数太多,请分开传输.
        "<SendTask>" +//
        "<ClientTaskID>1</ClientTaskID>" +//客户端TaskID,标志唯一一条传真记录.用于查询发送结果
        "<FaxNumber>"+faxnum+"</FaxNumber>" +//传真号码.
        "</SendTask>" +
        "</SendTaskList>" +
        "<DocumentList>" +
        "<FileNum>2</FileNum>" +
        "<Document ContentType=\"application/msword\" FileName=\""+filename+"\" EncodingType=\"base64\"  DocumentExtension=\"" +
        documentExtension + "\">" +
        document +
        "</Document>" +
        "</DocumentList>" +
        "</FaxInfo> ";
    return xml;
  }

  /**
   * 收件人信息的传真
   * @return
   */
  public static String getSendPHFaxToClientXML(String filename) {
    String xml = "";
    String documentExtension = filename.substring(filename.lastIndexOf(".") + 1,
    		filename.length());
    Base64 base64 = new Base64();
    String document = base64.getEncodedText(filename);
    System.out.println("documentExtension===" + documentExtension);
    xml = "<?xml version=\"1.0\" encoding=\"gb2312\" ?>" +
        "<FaxInfo xmlns=\"http://www.uniproud.com\"                     " +
        " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"        " +
        " xsi:schemaLocation=\"http://www.uniproud.com/fax_schema.xsd\">" +
        "<SchemaVersion>1.1</SchemaVersion>                            " +
        "<Login>" +
        "<UserID>70270281</UserID> " + //用户帐号
        "<Password>000000</Password>" + //用户密码
        "</Login>" + //
        "<FaxOptions>" +//
        "<FromName>John Doe</FromName>" +//发件人.
        "<Resolution>high</Resolution>" +//传真精度.1-低,2-中,3-高
        "<Priority>0</Priority>  " +//优先级.1-低,2-高
        "<FeedBackMode>0</FeedBackMode> " +//反馈方式.0-无,1-Email反馈,2-传真,3-短信反馈
        "<FaxToneDetectTimeout>17</FaxToneDetectTimeout>" +//传真音检测时长
        "<PromptVoiceChoice>-1</PromptVoiceChoice>" +//提示音.-1-群发提示音,0-默认提示音,>1自定义
        "<FaxSidFlag>0</FaxSidFlag>  " +//是否启用FaxSID.0-否,1-是
        "<FaxSid>aafdasfdas</FaxSid> " +//个性化传真标识FaxSID.FaxSidFlag为1时有效.
        "<StartSecond1>-1</StartSecond1> " +//工作时段1起始时间,单位秒.-1表示无限制.
        "<EndSecond1>-1</EndSecond1>  " +//工作时段1截止时间,单位秒.-1表示无限制.
        "<StartSecond2>-1</StartSecond2>" +//工作时段2起始时间,单位秒.-1表示无限制.
        "<EndSecond2>-1</EndSecond2>" +//工作时段2截止时间,单位秒.-1表示无限制
        "  </FaxOptions> " +//
        "<SendTaskList> " +//
        "<TotalNum>2</TotalNum>" +//传真个数.最大值100.如果个数太多,请分开传输.
        "<SendTask>" +//
        "<ClientTaskID>1</ClientTaskID>" +//客户端TaskID,标志唯一一条传真记录.用于查询发送结果
        "<FaxNumber>01068179411</FaxNumber> " +//传真号码
        "<GuestName>韩方园</GuestName>  " +//收件人名称.
        "<GuestCompany>航天华有</GuestCompany>" +//收件人公司名称.
        "</SendTask> " +//
        "</SendTaskList>" +//
        "<DocumentList>" +//包括多个Document注:所有文件最大不超过3M
        "<FileNum>1</FileNum> " +//
        "<Document FileName=\"" + filename + "\" " +//
        " ContentType=\"application/msword\"" +//
        " EncodingType=\"base64\"  " +//
        "DocumentExtension=\"" + documentExtension + "\"> " +
         document+
        "</Document> " +
        "</DocumentList> " +
        "</FaxInfo>";
    return xml;
  }

  /**
   * 查询所有未获取的清单
   * @return
   */
  public static String getQueryResultForSendTaskXML() {
    String xml =
        "<?xml version=\"1.0\" encoding=\"gb2312\"?> " +
        "<FaxInfo xmlns=\"http://www.uniproud.com\" " +
        " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
        " xsi:schemaLocation=\"http://www.uniproud.com/fax_schema.xsd\">" +
        "<QueryResultForSendTask> " +
        "<Login>" +
        "<UserID>00000001</UserID>" +
        "<Password>uniproud</Password>" +
         "</Login> " +
        "<FaxClientIDListFilter></FaxClientIDListFilter> " +//根据客户端TaskID查询,多个以,号相隔
        "<JobNoListFilter></JobNoListFilter>" +//根据作业号查询,多个以,号相隔
        "<StartTimeFilter></StartTimeFilter>" +//查询开始时间.格式为YYYY-MM-DD HH:MM:SS
        "<EndTimeFilter></EndTimeFilter>" +//查询截止时间.格式为:YYYY-MM-DD HH:MM:SS
        "<AllSubSendTask>1</AllSubSendTask> " +
        "</QueryResultForSendTask>" +//
        "</FaxInfo>";
    return xml;
  }
  /**
   * 接收传真
   * @return
   */
  public static String getQueryResultForRecvTaskXML() {
    String xml =
        "<?xml version=\"1.0\" encoding=\"gb2312\"?>" +
        "<FaxInfo xmlns=\"http://www.uniproud.com\"  " +
        " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  " +
        " xsi:schemaLocation=\"http://www.uniproud.com/fax_schema.xsd\">" +
        "<QueryResultForRecvTask>    " +
        "<Login>                       " +
        "<UserID>00000001</UserID> " +//用户帐号
        "<Password>uniproud</Password> " +//用户密码
        "</Login> " +
        "<FaxNumbersListFilter></FaxNumbersListFilter>  " +//根据传真号码查询,多个以,号相隔
        "<StartTimeFilter></StartTimeFilter> " +//查询开始时间.格式为:YYYY-MM-DD HH:MM:SS
        "<EndTimeFilter></EndTimeFilter> " +//查询截止时间.格式为YYYY-MM-DD HH:MM:SS
        "<AllSubRecvTask>1</AllSubRecvTask> " +
        "</QueryResultForRecvTask>  " +
        "</FaxInfo>";

    return xml;
  }

  /**
   * 接收ZIP返回信息
   * @return
   */
  public static String getQueryResultForRecvTaskZipXML() {
    String xml =
        "<?xml version=\"1.0\" encoding=\"gb2312\"?>                               " +
        "<FaxInfo xmlns=\"http://www.uniproud.com\"                     " +
        " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"        " +
        " xsi:schemaLocation=\"http://www.uniproud.com/fax_schema.xsd\">" +
        "<QueryResultForRecvTaskZip>                                           " +
        "<Login>                                                               " +
        "<UserID>00000001</UserID>" +//用户帐号
        "<Password>1</Password>" +//用户密码
        "</Login>" +
        "<FaxNumbersListFilter></FaxNumbersListFilter>   " +//根据传真号码查询,多个以,号相隔
        "<StartTimeFilter></StartTimeFilter>  " +//查询开始时间.格式为YYYY-MM-DD HH:MM:SS
        "<EndTimeFilter></EndTimeFilter>" +//查询截止时间.格式为YYYY-MM-DD HH:MM:SS
        "<AllSubRecvTask>1</AllSubRecvTask> " +
        "</QueryResultForRecvTaskZip> " +
         "</FaxInfo>";

    return xml;
  }
  /**
   * 删除发送传真文件
   * @return
   */
  public static String getDeleteFileForSendTask() {
        String xml =
            "<?xml version=\"1.0\" encoding=\"gb2312\"?>                               " +
            "<FaxInfo xmlns=\"http://www.uniproud.com\"                     " +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"        " +
            " xsi:schemaLocation=\"http://www.uniproud.com/fax_schema.xsd\">" +
            "<DeleteFileForSendTask>                                           " +
            "<Login>                                                               " +
            "<UserID>shqv4</UserID>" +//用户帐号
            "<Password>111111</Password>" +//用户密码
            "</Login>" +
            "<JobNoListFilter>080124000173</JobNoListFilter>   " +//根据传真号码查询,多个以,号相隔
            "<StartTimeFilter></StartTimeFilter>  " +//查询开始时间.格式为YYYY-MM-DD HH:MM:SS
            "<EndTimeFilter></EndTimeFilter>" +//查询截止时间.格式为YYYY-MM-DD HH:MM:SS
            "</DeleteFileForSendTask> " +
             "</FaxInfo>";

    return xml;
  }

  /**
 * 删除发送传真文件
 * @return
 */
public static String getDeleteFileForRecvTask() {
      String xml =
          "<?xml version=\"1.0\" encoding=\"gb2312\"?>                               " +
          "<FaxInfo xmlns=\"http://www.uniproud.com\"                     " +
          " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"        " +
          " xsi:schemaLocation=\"http://www.uniproud.com/fax_schema.xsd\">" +
          "<DeleteFileForRecvTask>                                           " +
          "<Login>                                                               " +
          "<UserID>cxm</UserID>" +//用户帐号
          "<Password>111111</Password>" +//用户密码
          "</Login>" +
          "<FaxNumberListFilter></FaxNumberListFilter>   " +
          "<ServerTaskIDListFilter></ServerTaskIDListFilter>   " +//根据传真号码查询,多个以,号相隔
          "<StartTimeFilter></StartTimeFilter>  " +//查询开始时间.格式为YYYY-MM-DD HH:MM:SS
          "<EndTimeFilter></EndTimeFilter>" +//查询截止时间.格式为YYYY-MM-DD HH:MM:SS
          "</DeleteFileForRecvTask> " +
           "</FaxInfo>";

  return xml;
}

  /**
   * 获取用户信息
   * @return
   */
  public static String getGetUserInfoXML() {
    String xml =
        "<?xml version=\"1.0\" encoding=\"gb2312\"?> " +
        "<UserInfo xmlns=\"http://www.uniproud.com\"" +
        " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  " +
        " xsi:schemaLocation=\"http://www.uniproud.com/fax_schema.xsd\">" +
        "<GetUserInfo>" +
        "<Login>" +
        "<UserID>shqv</UserID>  " +//用户帐号
        "<Password>111111</Password> " +//用户密码
        "</Login>  " +
         "</GetUserInfo> " +
        "</UserInfo> ";
    return xml;
  }

  /**
   * 增加用户信息
   * @return
   */
  public static String getAddUserInfoXML() {
    String xml =
        "<?xml version=\"1.0\" encoding=\"gb2312 \"?>                        " +
        "<UserInfo xmlns=\"http //www.uniproud.com\"                      "+
    "  xmlns:xsi=\"http: //www.w3.org/2001/XMLSchema-instance\"        "+
    "  xsi:schemaLocation=\"http: //www.uniproud.com/fax_schema.xsd\"> "+
    "<AddUserInfo>                                                  "+
    "<Login>                                                        "+
    "<UserID>root</UserID>                                          "+
    "<Password>root</Password>                                    "+
    "</Login>                                                       "+
    "  <UserInfo>                                                   "+
    "      <UserId>string557</UserId>                                  "+
    "      <UserName>string33</UserName>                              "+
    "      <Password>string</Password>                              "+
    "     <DeptMgrNo>d0001</DeptMgrNo>                                  "+
    "      <MobilePhone>string</MobilePhone>                        "+
    "      <DidNumber>string</DidNumber>                            "+
    "      <MobilePhone>string</MobilePhone>                        "+
    "      <CommPhone>string</CommPhone>                            "+
    "      <CommFax>string</CommFax>                                "+
    "      <CommEmail>string1</CommEmail>                            "+
    "      <Address>string</Address>                                "+
    "      <ZipCode>string</ZipCode>                                "+
    "      <SendAuthLevel>1</SendAuthLevel>                         "+
    "<BEmail2Fax>1</BEmail2Fax>                                     "+
    "      <BFax2Email>1</BFax2Email>                               "+
    "      <BFax2SMS>1</BFax2SMS>                                   "+
    "      <UserAlias>string</UserAlias>                            "+
    "      <DetectFaxToneTime>1</DetectFaxToneTime>                 "+
    "      <bSendFax>string</bSendFax>                              "+
    "<IsShowAllRecvFax>string</IsShowAllRecvFax>                    "+
    "   </UserInfo>                                                 "+
    "</AddUserInfo>                                                 "+
    "</UserInfo>                                                    ";

    return xml;
  }

  /**
   * 修改密码
   * @return
   */
  public static String getChangePasswordXML() {
    String xml =
        "<?xml version=\"1.0\" encoding=\"gb2312\"?>" +
        "<UserInfo xmlns=\"http://www.uniproud.com\"" +
        " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
        " xsi:schemaLocation=\"http://www.uniproud.com/fax_schema.xsd\">" +
        "<ChangePassword>  " +
        "<Login>" +
        " <UserID>cxm</UserID> " +//用户帐号
        " <Password>111111</Password> " +//用户密码
        "</Login>" +
        "<NewPassword>111111</NewPassword>" +//新密码
        "</ChangePassword>" +
        "</UserInfo>";

    return xml;
  }

  /**
   * 修改用户信息
   * @return
   */
  public static String getModifyUserInfoXML() {
    String xml =
        "<?xml version=\"1.0\" encoding=\"gb2312\"?>" +
        "<UserInfo xmlns=\"http://www.uniproud.com\"                     " +
        " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"        " +
        " xsi:schemaLocation=\"http://www.uniproud.com/fax_schema.xsd\">" +
        "<ModifyUserInfo>" +
        "<Login>" +
        "<UserID>cxm</UserID>" +//用户帐号
        "<Password>111111</Password> " +//用户密码
        "</Login> " +
        "<UserInfo>  " +
        "<MobilePhone>string</MobilePhone> " +//手机号码
        "<CommPhone>string</CommPhone>" +//商务电话
        "<CommFax>string</CommFax>" +//商务传真
        "<CommEmail>string</CommEmail> " +//商务邮箱
        "<Address>string</Address> " +//地址
        "<ZipCode>string</ZipCode>  " +//邮政编码
        "<SendAuthLevel>1</SendAuthLevel>" +//发送传真权限.0-无,1-本地,2-国内,3-国际
        "<BFax2Email>1</BFax2Email>" +//邮件转传真功能0-不启用,1-启用
        "<BEmail2Fax>1</BEmail2Fax>" +//传真转邮件功能0-不启用,1-启用
        "<BFax2SMS>1</BFax2SMS> " +//传真转短信功能0-不启用,1-启用
        "<UserAlias>string</UserAlias>" +//用户别名
        "<PromptVoiceChoice> 1 </PromptVoiceChoice>" +//传真提示音号
        "<DetectFaxToneTime>1</DetectFaxToneTime> " +//传真音检测时长
        "</UserInfo>  " +//
        "</ModifyUserInfo>" +//
        "</UserInfo>";
    return xml;
  }

  /**
   * 增加部门信息
   * @return
   */
  public static String getAddDeptInfoXML() {
    String xml =
    "<?xml version=\"1.0\" encoding=\"gb2312\"?>                       "+
    "<DeptInfo xmlns=\"http://www.uniproud.com\"                     "+
    "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"       "+
    "  xsi:schemaLocation=\"http://www.uniproud.com/fax_schema.xsd\">"+
    "<AddDeptInfo>                                                 "+
    "<Login>                                                       "+
    "<UserID>root</UserID>                                         "+
    "<Password>root</Password>                                   "+
    "</Login>                                                      "+
    "  <DeptInfo>                                                  "+
    "      <DeptMgrNo>string</DeptMgrNo>                           "+
    "      <DeptMgrName>string</DeptMgrName>                       "+
    "      <Remark>string</Remark>                                 "+
    "   </DeptInfo>                                                "+
    "</AddDeptInfo>                                                "+
    "</DeptInfo>                                                   ";
    return xml;
  }
  /**
   * 修改部门信息
   * @return
   */
  public static String getModifyDeptInfoXML() {
    String xml =
        "<?xml version=\"1.0\" encoding=\"gb2312\"?>                       "+
        "<DeptInfo xmlns=\"http://www.uniproud.com\"                     "+
        "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"       "+
        "  xsi:schemaLocation=\"http://www.uniproud.com/fax_schema.xsd\">"+
        "<ModifyDeptInfo>                                                 "+
        "<Login>                                                       "+
        "<UserID>root</UserID>                                         "+
        "<Password>root</Password>                                   "+
        "</Login>                                                      "+
        "  <DeptInfo>                                                  "+
        "      <DeptMgrNo>string</DeptMgrNo>                           "+
        "      <DeptMgrName>vvvv</DeptMgrName>                       "+
        "      <Remark>stringcc</Remark>                                 "+
        "   </DeptInfo>                                                "+
        "</ModifyDeptInfo>                                                "+
        "</DeptInfo>                                                   ";

    return xml;
  }

}