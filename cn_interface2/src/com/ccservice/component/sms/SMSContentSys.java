package com.ccservice.component.sms;

/**
 * 
 * @author wzc
 * 系统短信模板
 *
 */
public class SMSContentSys {

    public static String getSmsAccountclockcontent(String appendstr) {
        String content = "";
        content = "您好，短信条数不足" + appendstr + "，请尽快充值。";
        return content;
    }
}
