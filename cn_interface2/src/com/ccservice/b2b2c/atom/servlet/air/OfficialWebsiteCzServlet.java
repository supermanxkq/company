package com.ccservice.b2b2c.atom.servlet.air;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.ITicketSearchService;
import com.ccservice.b2b2c.base.flightinfo.FlightInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 航空公司官网对外提供的接口
 * 
 * @time 2015年6月10日 下午3:28:45
 * @author chendong
 */
public class OfficialWebsiteCzServlet extends HttpServlet {
    Log logger = LogFactory.getLog(this.getClass());

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1433921543368L;

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
        String jsonString = request.getParameter("json");
        System.out.println("coming----->" + jsonString);
        //        jsonString = URLDecoder.decode(jsonString, "UTF-8");
        JSONObject json = new JSONObject();
        PrintWriter out = null;
        out = response.getWriter();
        try {
            // 出发城市三字码
            String s_depcitycode = "";
            // 出发城市名称
            String s_depcityname = "";
            // 到达城市三字码
            String s_arrcitycode = "";
            // 到达城市名称
            String s_arrcityname = "";
            // 出发日期
            String s_startdate = "";
            // 返程日期
            String s_backdate = "";
            // 航程类型 1单程 2往返 3联程
            int s_traveltype = 1;
            // 航空公司代码
            String s_aircompanycode = "";
            //是否用携程的数据，为1的话才用
            String typeflag = "0";
            FlightSearch flightSearch = new FlightSearch(s_depcitycode, s_arrcitycode, "", s_startdate, "",
                    s_traveltype + "", s_backdate, s_aircompanycode, typeflag, s_depcityname, s_arrcityname, 0, 2,
                    60000L);// 航班查询条件赋值
            List<FlightInfo> list = AVOpen(flightSearch);

        }
        catch (Exception e) {

        }
        finally {
            out.print(json.toJSONString());
            out.flush();
            out.close();
        }
    }

    public List<FlightInfo> AVOpen(FlightSearch flightSearch) {
        List<FlightInfo> list = new ArrayList<FlightInfo>();
        ITicketSearchService iTicketSearchService = Server.getInstance().getTicketSearchService();
        try {
            //            String urlAtom = getSysconfigString("searchurl_interface");
            //            String urlAtom = "http://localhost:18080/cn_interface/service/";
            String urlAtom = PropertyUtil.getValue("searchurl_interface", "air.properties");
            HessianProxyFactory factory = new HessianProxyFactory();
            iTicketSearchService = (ITicketSearchService) factory.create(ITicketSearchService.class, urlAtom
                    + ITicketSearchService.class.getSimpleName());
            list = iTicketSearchService.findAllFlightinfo(flightSearch);
        }
        catch (MalformedURLException e) {
            logger.error("ticket_LIST:AVOpen", e.fillInStackTrace());
        }
        catch (Exception e) {
            logger.error("ticket_LIST:AVOpen", e.fillInStackTrace());
        }
        return list;
    }

}
