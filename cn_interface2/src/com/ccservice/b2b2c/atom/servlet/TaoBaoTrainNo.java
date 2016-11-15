package com.ccservice.b2b2c.atom.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.service12306.TimeUtil;

/**
 * Servlet implementation class TaoBaoTrainNo
 */
public class TaoBaoTrainNo extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TaoBaoTrainNo() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String path = "D:/traindetails/" + TimeUtil.gettodaydate(1).replace(" ", "").replace("-", "").replace(":", "")
                + "/" + "traindetails.gz";
        /* String paths = "D:/traindetails/"
                 + TimeUtil.gettodaydatebyfrontandback(1, -1).replace(" ", "").replace("-", "").replace(":", "") + "/"
                 + "traindetails.txt";*/
        try {
            File file = new File(path);
            String filename = file.getName();
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            response.addHeader("Content-Disposition", "attachment;filename="
                    + new String(filename.replaceAll(" ", "").getBytes("utf-8"), "iso8859-1"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            os.write(buffer);// 输出文件
            os.flush();
            os.close();
        }
        catch (Exception e) {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-type", "text/html;charset=UTF-8");
            response.getWriter().write("文件不存在");
        }

    }

}
