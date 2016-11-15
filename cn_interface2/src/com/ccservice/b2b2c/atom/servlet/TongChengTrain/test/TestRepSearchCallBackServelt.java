package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class TestRepSearchCallBackServelt extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public TestRepSearchCallBackServelt() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    try {
            request.setCharacterEncoding("utf-8");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	    String ctxkey = request.getParameter("ctxkey");
        String jsonData = request.getParameter("jsonData");
        System.out.println(getBrowserIp(request)+"--->ctxkey---->"+ctxkey+"jsonData---->"+jsonData);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	
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
