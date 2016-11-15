package HuoNiuPackage;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.db.DBHelper;
import com.ccservice.b2b2c.util.db.DBHelperAccount;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;

/**
 * Servlet implementation class HuoNiuPackage
 */
public class HuoNiuPackageServerlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public HuoNiuPackageServerlet() {
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
        String json = request.getParameter("json");
        JSONObject obj = new JSONObject();
        obj.put("success", false);
        obj.put("msg", "我错了。。。。");
        String logname = "火牛数据统计接口";
        String r1 = System.currentTimeMillis() + "" + (int) (Math.random() * 10000);
        try {
            if (json != null && !"".equals(json)) {
                JSONObject jsonjie = JSONObject.parseObject(json);
                String FireBullToken = jsonjie.getString("FireBullToken");

                int Type = jsonjie.containsKey("Type") ? jsonjie.getIntValue("Type") : -1;
                int orderId = jsonjie.containsKey("orderId") ? jsonjie.getIntValue("orderId") : 0;
                if (Type == 3) {
                    logname = "火牛数据统计接口_占座成功";
                }
                WriteLog.write(logname, r1 + "--->" + getIpAddr(request) + "--->" + json);
                int Status = jsonjie.containsKey("Status") ? jsonjie.getIntValue("Status") : -1;
                int CoyptoTime = jsonjie.containsKey("CoyptoTime") ? jsonjie.getIntValue("CoyptoTime") : -1;
                if (Type < 0 || Status < 0 || CoyptoTime < 0) {
                }
                else {
                    String sql = "EXEC [sp_TrainOrderPhoneCoypto_INSERT] @Type=" + Type + ", @Status  =" + Status
                            + ", @CoyptoTime  =" + CoyptoTime + ", @orderId  =" + orderId+ ",@FireBullToken ='" + FireBullToken + "'";
                    try {
                        DBHelperAccount.insertSql(sql);
                        obj.put("success", true);
                        obj.remove("msg");
                    }
                    catch (Exception e) {
                        WriteLog.write(logname + "_ERROR", r1 + "--->" + sql);
                        ExceptionUtil.writelogByException(logname + "_ERROR", e);
                    }
                }
            }
        }
        catch (Exception e) {
            WriteLog.write(logname + "_ERROR", r1);
            ExceptionUtil.writelogByException(logname + "_ERROR", e);
        }
        PrintWriter out = response.getWriter();
        out.print(obj.toString());
        out.flush();
    }


        /**
     * 
     * 
     * @param showover
     * @return
     * @time 2015年12月16日 下午7:07:13
     * @author Administrator
     */
    private String getjsonobject_series_realTimeOne(int showover) {
        DataTable myDt = null;
        String sql = "exec [TrainOrderGetStatusHN]";
        try {
            myDt = DBHelper.GetDataTable(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonobject = new JSONObject();
        JSONArray jsonobject_series = new JSONArray();
        JSONArray jsonobject_series_data1 = new JSONArray();
        List<DataRow> dataRows = myDt.GetRow();
        if (dataRows.size() > 0) {
            for (int i = 0; i < dataRows.size(); i++) {
                DataRow datarow = dataRows.get(i);
                int Status = Integer.parseInt(datarow.GetColumnString("total").toString());
                String Date = datarow.GetColumnString("timeKey").toString();
                JSONObject totalItem = new JSONObject();
                totalItem.put("x", Date);

                totalItem.put("y", Status);

                jsonobject_series_data1.add(totalItem);
            }
        }
        jsonobject_series.add(jsonobject_series_data1);
        jsonobject.put("series", jsonobject_series);
        return JSONObject.toJSONString(jsonobject);

    }

    private String getjsonobject_series_realTimeTow(int showover) {
        DataTable myDt = null;
        String sql = "exec [TrainOrderGetStatusHN]";
        try {
            myDt = DBHelper.GetDataTable(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonobject = new JSONObject();
        JSONArray jsonobject_series = new JSONArray();
        JSONArray jsonobject_series_data1 = new JSONArray();
        List<DataRow> dataRows = myDt.GetRow();
        if (dataRows.size() > 0) {
            for (int i = 0; i < dataRows.size(); i++) {
                DataRow datarow = dataRows.get(i);
                int Status = Integer.parseInt(datarow.GetColumnString("total").toString());
                String Date = datarow.GetColumnString("timeKey").toString();
                JSONObject totalItem = new JSONObject();
                totalItem.put("x", Date);

                totalItem.put("y", Status);

                jsonobject_series_data1.add(totalItem);
            }
        }
        jsonobject_series.add(jsonobject_series_data1);
        jsonobject.put("series", jsonobject_series);
        return JSONObject.toJSONString(jsonobject);

    }

    private String getjsonobject_series_realTimeThree(int showover) {
        DataTable myDt = null;
        String sql = "exec [TrainOrderGetStatusHN]";
        try {
            myDt = DBHelper.GetDataTable(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonobject = new JSONObject();
        JSONArray jsonobject_series = new JSONArray();
        JSONArray jsonobject_series_data1 = new JSONArray();
        List<DataRow> dataRows = myDt.GetRow();
        if (dataRows.size() > 0) {
            for (int i = 0; i < dataRows.size(); i++) {
                DataRow datarow = dataRows.get(i);
                int Status = Integer.parseInt(datarow.GetColumnString("total").toString());
                String Date = datarow.GetColumnString("timeKey").toString();
                JSONObject totalItem = new JSONObject();
                totalItem.put("x", Date);

                totalItem.put("y", Status);

                jsonobject_series_data1.add(totalItem);
            }
        }
        jsonobject_series.add(jsonobject_series_data1);
        jsonobject.put("series", jsonobject_series);
        return JSONObject.toJSONString(jsonobject);

    }

    private String getjsonobject_series_realTimeFour(int showover) {
        DataTable myDt = null;
        String sql = "exec [TrainOrderGetStatusHN]";
        try {
            myDt = DBHelper.GetDataTable(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonobject = new JSONObject();
        JSONArray jsonobject_series = new JSONArray();
        JSONArray jsonobject_series_data1 = new JSONArray();
        List<DataRow> dataRows = myDt.GetRow();
        if (dataRows.size() > 0) {
            for (int i = 0; i < dataRows.size(); i++) {
                DataRow datarow = dataRows.get(i);
                int Status = Integer.parseInt(datarow.GetColumnString("total").toString());
                String Date = datarow.GetColumnString("timeKey").toString();
                JSONObject totalItem = new JSONObject();
                totalItem.put("x", Date);

                totalItem.put("y", Status);

                jsonobject_series_data1.add(totalItem);
            }
        }
        jsonobject_series.add(jsonobject_series_data1);
        jsonobject.put("series", jsonobject_series);
        return JSONObject.toJSONString(jsonobject);

    }

    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
