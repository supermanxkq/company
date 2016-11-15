package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;

/**
 * 企信通短信发送状况通知
 * 
 * @author 贾建磊
 */
public class SmsReplyToNotification extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
    static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String state = "0";
        response.setContentType("text/plain; charset=utf-8");
        PrintWriter out = response.getWriter();
        String paramPart = request.getQueryString();
        if (paramPart != null && !paramPart.equals("")) {
            String mid = request.getParameter("mid");
            if (mid != null && !mid.equals("")) {
                state = "1";
                //                WriteLog.write("企信通短信发送状况通知", "发送状况报告从接口传出的数据:" + mid);
                String[] midArray = mid.split("\",\"");
                if (midArray != null && midArray.length > 0) {
                    for (int i = 0; i < midArray.length; i++) {
                        String midInfo = midArray[i];
                        if (midInfo != null && !midInfo.equals("")) {
                            if (i == 0) {
                                midInfo = midInfo.substring(2, midArray[0].length());
                            }
                            else if (i == midArray.length - 1) {
                                midInfo = midInfo.substring(0, midArray[0].length() - 2);
                            }
                            String[] midInfoArr = midInfo.split(",");
                            if (midInfoArr != null && midInfoArr.length == 2) {
                                WriteLog.write("企信通短信发送状况通知", "[批次编号：" + midInfoArr[0].trim() + "---------发送状态："
                                        + getSmsSendState(midInfoArr[1].trim()) + "]");
                            }
                        }
                    }
                }
            }
        }
        out.print("{\"status\":" + state + "}");//为接口返回状态值：0接收数据失败    1接收数据成功
        out.flush();
        out.close();
    }

    public String getSmsSendState(String state) {
        if (state.equals("DELIVRD")) {
            return "发送成功";
        }
        else if (state.equals("UNDELIV")) {
            return "发送失败";
        }
        return "";
    }
}