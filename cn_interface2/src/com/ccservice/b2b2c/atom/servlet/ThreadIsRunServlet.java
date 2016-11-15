package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.service.PhoneChangeWebThread;

public class ThreadIsRunServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ThreadIsRunServlet() {
        super();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        try {
            String s = request.getParameter("isrun");
            PrintWriter out = null;
            out = response.getWriter();
            new PhoneChangeWebThread().start();
            if (s.equals("true")) {
                new PhoneChangeWebThread().isrun = true;
                out.print("终止线程成功");
            }
            else {
                out.print("启动线程成功");
            }
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
