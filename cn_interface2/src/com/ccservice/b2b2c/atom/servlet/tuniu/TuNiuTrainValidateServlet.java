package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.servlet.account.method.TrainAccountOperateMethod;
import com.ccservice.b2b2c.atom.servlet.tuniu.method.TuNiuServletUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.TrainUtil;

/**
* @ClassName: TuNiuTrainValidateServlet
* @Description: 途牛身份验证
* @author RRRRRR
* @date 2016年10月31日 上午10:24:46
 */
@WebServlet("/TuNiuTrainValidateServlet")
public class TuNiuTrainValidateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final TuNiuServletUtil tuNiuServletUtil = new TuNiuServletUtil();

    private static final String logName = "TuNiuTrainValidateServlet";

    public TuNiuTrainValidateServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        //结果
        String result = "";
        //输出
        PrintWriter out = null;
        final AsyncContext ctx = request.startAsync();
        //随机数据
        int random = new Random().nextInt(9000000) + 1000000;
        //操作
        try {
            out = response.getWriter();
            //请求参数
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer buf = new StringBuffer(1024);
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }
            //请求参数
            String param = buf.toString();
            if (param == null || "".equals(param)) {
                tuNiuServletUtil.respByParamError(ctx, logName);
                return;
            }
            //记录日志
            WriteLog.write(logName, random + "req-->" + param);
            //处理方法
            result = new TrainAccountOperateMethod().TuNiuTrainValidate(param, logName, random).toString();
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(logName + "Exception", e, String.valueOf(random));
        }
        finally {
            if (out != null) {
                //日志
                WriteLog.write(logName, random + "-->res-->" + result);
                //输出
                out.print(result);
                out.flush();
                out.close();
            }
        }
    }

    public static void main(String[] args) {
        JSONObject jso1 = new JSONObject();
        JSONArray jsa1 = new JSONArray();
        JSONObject jsob1 = new JSONObject();
        //        String("name");
        //        identityType = jsonarray.getJSONObject(i).getString("identityType");
        //        identityCard = jsonarray.getJSONObject(i).getString("identityCard");
        jsob1.put("name", "任少楠");
        jsob1.put("identityType", "1");
        jsob1.put("identityCard", "411224199212155611");
        jsa1.add(jsob1);
        jso1.put("data", jsa1);
        String result = new TrainAccountOperateMethod().TuNiuTrainValidate(jso1.toJSONString(), "aa", 65132).toString();
        System.out.println(result);
    }
}
