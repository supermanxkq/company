package HuoNiuPackage;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.db.DBHelper;
import com.ccservice.b2b2c.util.db.DBHelperAccount;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;


/**
 * Servlet implementation class HuoNiuPackage
 */
public class HuoNiulineServerlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HuoNiulineServerlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        String json = request.getParameter("json");
        String result = "-1";
        // 1每天2每月3每小时4每分钟的
        int showover = Integer.parseInt(request.getParameter("showover"));
        if (showover == 1) {
            result = getjsonobject_series_realTimeOne();
            //            result = "{\"series\":[[{\"x\":\"2016-04-07 18:53\",\"y\":5},{\"x\":\"2016-04-07 18:54\",\"y\":17},{\"x\":\"2016-04-07 18:55\",\"y\":10},{\"x\":\"2016-04-07 18:56\",\"y\":14},{\"x\":\"2016-04-07 18:57\",\"y\":5},{\"x\":\"2016-04-07 18:58\",\"y\":11},{\"x\":\"2016-04-07 18:59\",\"y\":13},{\"x\":\"2016-04-07 19:00\",\"y\":6},{\"x\":\"2016-04-07 19:01\",\"y\":7},{\"x\":\"2016-04-07 19:02\",\"y\":17},{\"x\":\"2016-04-07 19:03\",\"y\":10},{\"x\":\"2016-04-07 19:04\",\"y\":20},{\"x\":\"2016-04-07 19:05\",\"y\":5},{\"x\":\"2016-04-07 19:06\",\"y\":7},{\"x\":\"2016-04-07 19:07\",\"y\":10},{\"x\":\"2016-04-07 19:08\",\"y\":7},{\"x\":\"2016-04-07 19:09\",\"y\":2},{\"x\":\"2016-04-07 19:10\",\"y\":3},{\"x\":\"2016-04-07 19:11\",\"y\":6},{\"x\":\"2016-04-07 19:12\",\"y\":8},{\"x\":\"2016-04-07 19:13\",\"y\":5},{\"x\":\"2016-04-07 19:14\",\"y\":2},{\"x\":\"2016-04-07 19:15\",\"y\":10},{\"x\":\"2016-04-07 19:16\",\"y\":5},{\"x\":\"2016-04-07 19:17\",\"y\":8},{\"x\":\"2016-04-07 19:18\",\"y\":25},{\"x\":\"2016-04-07 19:19\",\"y\":11},{\"x\":\"2016-04-07 19:20\",\"y\":7},{\"x\":\"2016-04-07 19:21\",\"y\":8},{\"x\":\"2016-04-07 19:22\",\"y\":10},{\"x\":\"2016-04-07 19:23\",\"y\":6},{\"x\":\"2016-04-07 19:24\",\"y\":8},{\"x\":\"2016-04-07 19:25\",\"y\":8},{\"x\":\"2016-04-07 19:26\",\"y\":5},{\"x\":\"2016-04-07 19:27\",\"y\":4},{\"x\":\"2016-04-07 19:28\",\"y\":2},{\"x\":\"2016-04-07 19:29\",\"y\":4},{\"x\":\"2016-04-07 19:30\",\"y\":2},{\"x\":\"2016-04-07 19:31\",\"y\":7},{\"x\":\"2016-04-07 19:32\",\"y\":7},{\"x\":\"2016-04-07 19:33\",\"y\":6},{\"x\":\"2016-04-07 19:34\",\"y\":3},{\"x\":\"2016-04-07 19:35\",\"y\":6},{\"x\":\"2016-04-07 19:36\",\"y\":4},{\"x\":\"2016-04-07 19:37\",\"y\":6},{\"x\":\"2016-04-07 19:38\",\"y\":3},{\"x\":\"2016-04-07 19:39\",\"y\":6},{\"x\":\"2016-04-07 19:40\",\"y\":3},{\"x\":\"2016-04-07 19:41\",\"y\":3},{\"x\":\"2016-04-07 19:42\",\"y\":5},{\"x\":\"2016-04-07 19:43\",\"y\":0},{\"x\":\"2016-04-07 19:44\",\"y\":7},{\"x\":\"2016-04-07 19:45\",\"y\":10},{\"x\":\"2016-04-07 19:46\",\"y\":1},{\"x\":\"2016-04-07 19:47\",\"y\":1},{\"x\":\"2016-04-07 19:48\",\"y\":4},{\"x\":\"2016-04-07 19:49\",\"y\":1},{\"x\":\"2016-04-07 19:50\",\"y\":3},{\"x\":\"2016-04-07 19:51\",\"y\":7},{\"x\":\"2016-04-07 19:52\",\"y\":9},{\"x\":\"2016-04-07 19:53\",\"y\":3}],[{\"x\":\"2016-04-07 18:53\",\"y\":3},{\"x\":\"2016-04-07 18:54\",\"y\":7},{\"x\":\"2016-04-07 18:55\",\"y\":4},{\"x\":\"2016-04-07 18:56\",\"y\":4},{\"x\":\"2016-04-07 18:57\",\"y\":1},{\"x\":\"2016-04-07 18:58\",\"y\":3},{\"x\":\"2016-04-07 18:59\",\"y\":5},{\"x\":\"2016-04-07 19:00\",\"y\":2},{\"x\":\"2016-04-07 19:01\",\"y\":2},{\"x\":\"2016-04-07 19:02\",\"y\":6},{\"x\":\"2016-04-07 19:03\",\"y\":1},{\"x\":\"2016-04-07 19:04\",\"y\":4},{\"x\":\"2016-04-07 19:05\",\"y\":2},{\"x\":\"2016-04-07 19:06\",\"y\":1},{\"x\":\"2016-04-07 19:07\",\"y\":3},{\"x\":\"2016-04-07 19:08\",\"y\":2},{\"x\":\"2016-04-07 19:09\",\"y\":2},{\"x\":\"2016-04-07 19:10\",\"y\":1},{\"x\":\"2016-04-07 19:11\",\"y\":2},{\"x\":\"2016-04-07 19:12\",\"y\":1},{\"x\":\"2016-04-07 19:13\",\"y\":0},{\"x\":\"2016-04-07 19:14\",\"y\":0},{\"x\":\"2016-04-07 19:15\",\"y\":1},{\"x\":\"2016-04-07 19:16\",\"y\":2},{\"x\":\"2016-04-07 19:17\",\"y\":2},{\"x\":\"2016-04-07 19:18\",\"y\":6},{\"x\":\"2016-04-07 19:19\",\"y\":4},{\"x\":\"2016-04-07 19:20\",\"y\":3},{\"x\":\"2016-04-07 19:21\",\"y\":2},{\"x\":\"2016-04-07 19:22\",\"y\":6},{\"x\":\"2016-04-07 19:23\",\"y\":0},{\"x\":\"2016-04-07 19:24\",\"y\":2},{\"x\":\"2016-04-07 19:25\",\"y\":1},{\"x\":\"2016-04-07 19:26\",\"y\":1},{\"x\":\"2016-04-07 19:27\",\"y\":3},{\"x\":\"2016-04-07 19:28\",\"y\":1},{\"x\":\"2016-04-07 19:29\",\"y\":0},{\"x\":\"2016-04-07 19:30\",\"y\":0},{\"x\":\"2016-04-07 19:31\",\"y\":1},{\"x\":\"2016-04-07 19:32\",\"y\":0},{\"x\":\"2016-04-07 19:33\",\"y\":2},{\"x\":\"2016-04-07 19:34\",\"y\":1},{\"x\":\"2016-04-07 19:35\",\"y\":0},{\"x\":\"2016-04-07 19:36\",\"y\":1},{\"x\":\"2016-04-07 19:37\",\"y\":0},{\"x\":\"2016-04-07 19:38\",\"y\":1},{\"x\":\"2016-04-07 19:39\",\"y\":4},{\"x\":\"2016-04-07 19:40\",\"y\":2},{\"x\":\"2016-04-07 19:41\",\"y\":2},{\"x\":\"2016-04-07 19:42\",\"y\":1},{\"x\":\"2016-04-07 19:43\",\"y\":0},{\"x\":\"2016-04-07 19:44\",\"y\":3},{\"x\":\"2016-04-07 19:45\",\"y\":3},{\"x\":\"2016-04-07 19:46\",\"y\":0},{\"x\":\"2016-04-07 19:47\",\"y\":0},{\"x\":\"2016-04-07 19:48\",\"y\":1},{\"x\":\"2016-04-07 19:49\",\"y\":0},{\"x\":\"2016-04-07 19:50\",\"y\":2},{\"x\":\"2016-04-07 19:51\",\"y\":2},{\"x\":\"2016-04-07 19:52\",\"y\":3},{\"x\":\"2016-04-07 19:53\",\"y\":0}]],\"status\":{\"sumNewTicketNumber\":\"5885\",\"sumRefuse\":\"2285\",\"Otherreasons\":\"2\",\"sumOrderSuccess\":\"1689\",\"sumNowOrderingNumber\":\"1429\",\"sumAppointments\":\"0\",\"Notickets\":\"91\",\"avgRate\":0,\"Booked\":\"6\",\"sumBespeakTotal\":\"5293\",\"sumOrderNeedTo\":\"6892\",\"CancelType0\":0}}";
        }
        else if (showover == 2) {
            result = getjsonobject_series_realTimeTow();
        }
        else if (showover == 3) {
            result = getjsonobject_series_realTimeThree();
        }
        else {

            result = getjsonobject_series_realTimeFour();
        }

        PrintWriter out = response.getWriter();
        out.print(result);
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
    private String getjsonobject_series_realTimeOne() {
        DataTable myDt = null;

        String sql = "exec [TrainOrderGetStatusHN]";
        DataTable avg = null;
        String sqlavg = "select isnull(avg(CoyptoTime),0) as AVG from TrainOrderPhoneCoypto with(nolock) where Type=0 and Status=1 and  Date between DATEADD(HH,-1,CONVERT(varchar(16),getdate(),120)) and getdate() ";

        try {
            avg = DBHelperAccount.GetDataTable(sqlavg);
            myDt = DBHelperAccount.GetDataTable(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonobject = new JSONObject();
        JSONArray jsonobject_series = new JSONArray();
        JSONArray jsonobject_series_data1 = new JSONArray();
        JSONArray jsonobject_series_data2 = new JSONArray();
        List<DataRow> dataRows = myDt.GetRow();
        if (dataRows.size() > 0) {
            for (int i = 0; i < dataRows.size(); i++) {
                DataRow datarow = dataRows.get(i);
                int total = Integer.parseInt(datarow.GetColumnString("total"));
                int success = Integer.parseInt(datarow.GetColumnString("success"));
                String Date = datarow.GetColumnString("timeKey").toString();
                String DateCOUNT = datarow.GetColumnString("timeKey").toString();
                JSONObject totalItem = new JSONObject();
                JSONObject successItem = new JSONObject();
                totalItem.put("x", DateCOUNT);
                totalItem.put("y", total);
                successItem.put("x", Date);
                successItem.put("y", success);
                jsonobject_series_data1.add(totalItem);
                jsonobject_series_data2.add(successItem);
            }
        }
        List<DataRow> dataRowavg = avg.GetRow();
        DataRow datarowavg = dataRowavg.get(0);
        int Statusavg = Integer.parseInt(datarowavg.GetColumnString("AVG").toString());

        jsonobject_series.add(jsonobject_series_data1);
        jsonobject_series.add(jsonobject_series_data2);
        jsonobject.put("series", jsonobject_series);
        JSONObject jsonobject_status2 = new JSONObject();
        jsonobject_status2.put("avgtime", Statusavg);
        jsonobject.put("status", jsonobject_status2);
        return JSONObject.toJSONString(jsonobject);

    }

    private String getjsonobject_series_realTimeTow() {
        DataTable myDt = null;
        String sql = "exec [TrainOrderGetStatusHNTow]";
        DataTable avg = null;
        String sqlavg = "select isnull(avg(CoyptoTime),0) as AVG from TrainOrderPhoneCoypto with(nolock) where Type=1 and Status=1 and  Date between DATEADD(HH,-1,CONVERT(varchar(16),getdate(),120)) and getdate() ";

        try {
            avg = DBHelperAccount.GetDataTable(sqlavg);
            myDt = DBHelperAccount.GetDataTable(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonobject = new JSONObject();
        JSONArray jsonobject_series = new JSONArray();
        JSONArray jsonobject_series_data1 = new JSONArray();
        JSONArray jsonobject_series_data2 = new JSONArray();
        List<DataRow> dataRows = myDt.GetRow();
        if (dataRows.size() > 0) {
            for (int i = 0; i < dataRows.size(); i++) {
                DataRow datarow = dataRows.get(i);
                int total = Integer.parseInt(datarow.GetColumnString("total"));
                int success = Integer.parseInt(datarow.GetColumnString("success"));
                String Date = datarow.GetColumnString("timeKey").toString();
                String DateCOUNT = datarow.GetColumnString("timeKey").toString();
                JSONObject totalItem = new JSONObject();
                JSONObject successItem = new JSONObject();
                totalItem.put("x", DateCOUNT);
                totalItem.put("y", total);
                successItem.put("x", Date);
                successItem.put("y", success);
                jsonobject_series_data1.add(totalItem);
                jsonobject_series_data2.add(successItem);
            }
        }
        List<DataRow> dataRowavg = avg.GetRow();
        DataRow datarowavg = dataRowavg.get(0);
        int Statusavg = Integer.parseInt(datarowavg.GetColumnString("AVG").toString());

        jsonobject_series.add(jsonobject_series_data1);
        jsonobject_series.add(jsonobject_series_data2);
        jsonobject.put("series", jsonobject_series);
        JSONObject jsonobject_status2 = new JSONObject();
        jsonobject_status2.put("avgtime", Statusavg);
        jsonobject.put("status", jsonobject_status2);
        return JSONObject.toJSONString(jsonobject);

    }

    private String getjsonobject_series_realTimeThree() {
        DataTable myDt = null;
        String sql = "exec [TrainOrderGetStatusHNThree]";
        DataTable avg = null;
        String sqlavg = "select isnull(avg(CoyptoTime),0) as AVG from TrainOrderPhoneCoypto with(nolock) where Type=2 and Status=1 and  Date between DATEADD(HH,-1,CONVERT(varchar(16),getdate(),120)) and getdate() ";

        try {
            avg = DBHelperAccount.GetDataTable(sqlavg);
            myDt = DBHelperAccount.GetDataTable(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonobject = new JSONObject();
        JSONArray jsonobject_series = new JSONArray();
        JSONArray jsonobject_series_data1 = new JSONArray();
        JSONArray jsonobject_series_data2 = new JSONArray();
        List<DataRow> dataRows = myDt.GetRow();
        if (dataRows.size() > 0) {
            for (int i = 0; i < dataRows.size(); i++) {
                DataRow datarow = dataRows.get(i);
                int total = Integer.parseInt(datarow.GetColumnString("total"));
                int success = Integer.parseInt(datarow.GetColumnString("success"));
                String Date = datarow.GetColumnString("timeKey").toString();
                String DateCOUNT = datarow.GetColumnString("timeKey").toString();
                JSONObject totalItem = new JSONObject();
                JSONObject successItem = new JSONObject();
                totalItem.put("x", DateCOUNT);
                totalItem.put("y", total);
                successItem.put("x", Date);
                successItem.put("y", success);
                jsonobject_series_data1.add(totalItem);
                jsonobject_series_data2.add(successItem);
            }
        }
        List<DataRow> dataRowavg = avg.GetRow();
        DataRow datarowavg = dataRowavg.get(0);
        int Statusavg = Integer.parseInt(datarowavg.GetColumnString("AVG").toString());

        jsonobject_series.add(jsonobject_series_data1);
        jsonobject_series.add(jsonobject_series_data2);
        jsonobject.put("series", jsonobject_series);
        JSONObject jsonobject_status2 = new JSONObject();
        jsonobject_status2.put("avgtime", Statusavg);
        jsonobject.put("status", jsonobject_status2);
        return JSONObject.toJSONString(jsonobject);


    }

    private String getjsonobject_series_realTimeFour() {
        DataTable myDt = null;
        String sql = "exec [TrainOrderGetStatusHNFour]";
        DataTable avg = null;
        DataTable count = null;
        String sqlavg = "select isnull(avg(CoyptoTime),0) as AVG from TrainOrderPhoneCoypto with(nolock) where Type=3 and Status=1 and  Date between DATEADD(HH,-1,CONVERT(varchar(16),getdate(),120)) and getdate() ";
        String sqlcount = "select isnull(count(*),0) as count from TrainOrderPhoneCoypto with(nolock) where Type=3 and Status=1 and  Date >CONVERT(varchar(100), GETDATE(), 23) ";

        try {
            avg = DBHelperAccount.GetDataTable(sqlavg);
            myDt = DBHelperAccount.GetDataTable(sql);
            count = DBHelperAccount.GetDataTable(sqlcount);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonobject = new JSONObject();
        JSONArray jsonobject_series = new JSONArray();
        JSONArray jsonobject_series_data1 = new JSONArray();
        JSONArray jsonobject_series_data2 = new JSONArray();
        List<DataRow> dataRows = myDt.GetRow();
        if (dataRows.size() > 0) {
            for (int i = 0; i < dataRows.size(); i++) {
                DataRow datarow = dataRows.get(i);
                int total = Integer.parseInt(datarow.GetColumnString("total"));
                int success = Integer.parseInt(datarow.GetColumnString("success"));
                String Date = datarow.GetColumnString("timeKey").toString();
                String DateCOUNT = datarow.GetColumnString("timeKey").toString();
                JSONObject totalItem = new JSONObject();
                JSONObject successItem = new JSONObject();
                totalItem.put("x", DateCOUNT);
                totalItem.put("y", total);
                successItem.put("x", Date);
                successItem.put("y", success);
                jsonobject_series_data1.add(totalItem);
                jsonobject_series_data2.add(successItem);
            }
        }
        List<DataRow> dataRowavg = avg.GetRow();
        List<DataRow> dataRowcount = count.GetRow();
        DataRow datarowcount = dataRowcount.get(0);
        DataRow datarowavg = dataRowavg.get(0);
        int Statusavg = Integer.parseInt(datarowavg.GetColumnString("AVG").toString());
        int COUNT = Integer.parseInt(datarowcount.GetColumnString("count").toString());
        jsonobject_series.add(jsonobject_series_data1);
        jsonobject_series.add(jsonobject_series_data2);
        jsonobject.put("series", jsonobject_series);
        JSONObject jsonobject_status2 = new JSONObject();
        jsonobject_status2.put("avgtime", Statusavg);
        jsonobject_status2.put("countorder", COUNT);
        jsonobject.put("status", jsonobject_status2);
        return JSONObject.toJSONString(jsonobject);

}
}
