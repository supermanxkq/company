package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.ccservice.b2b2c.atom.component.WriteLog;

public class WeblogFilter implements Filter {

    public void init(FilterConfig arg0) throws ServletException {

    }

    public void doFilter(ServletRequest arg0, ServletResponse response, FilterChain arg2) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) arg0;
        WriteLog.write("cn_interface_WeblogFilter", getBrowserIp(req));
    }

    /**
     * 不过滤的方法返回true
     * 
     * @param method
     * @return
     */
    public boolean noFilterMethod(String method) {
        method = method.substring(method.indexOf("!") + 1, method.lastIndexOf("."));
        if ("parsepnr".equals(method) || "pnrcreateorder".equals(method) || "CheckName".equals(method)) {
            return true;
        }
        return false;
    }

    public void destroy() {
        // TODO Auto-generated method stub
    }

    /**
     * 获取IP
     * @return
     */
    public String getBrowserIp(HttpServletRequest request) {
        String ipString = "";
        if (request.getHeader("X-real-ip") == null) {
            ipString = request.getRemoteAddr();
        }
        else {
            ipString = request.getHeader("X-real-ip");
        }
        return ipString;
    }

}
