package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.b2b2c.util.PropertyWatchDog;

/**
 * 淘宝启动长连接类
 * @author liangwei
 *
 */
public class TaobaoTarinMonitor extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private String appScret;

    private String agentId;

    private String apiUrl;

    private String appKey;

    private String sessionKey;

    public TaobaoHotelInterfaceUtil tbiu;

    public void init() throws ServletException {
        String statue = getInitParameter("TaobaoMonitor");

        tbiu = new TaobaoHotelInterfaceUtil();
        tbiu.appkey = getInitParameter("appKey");
        tbiu.url = this.getInitParameter("apiUrl");
        tbiu.appSecret = this.getInitParameter("appScret");
        tbiu.agentid = Long.parseLong(this.getInitParameter("agentId"));
        tbiu.sessionKey = this.getInitParameter("sessionKey");
        tbiu.jiantingurl = this.getInitParameter("jiantingurl");
        tbiu.settbiu(tbiu);
        int max = 100;
        int min = 10;
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        //tbiu.traintuipaotest();
        //String xx = "{'train_agent_order_get_response':{'mailing':false,'is_success':true,'request_id':'9wy9ljau0tap','relation_name':'梁伟','company_name':'no','address':'no address','main_order_id':193528712001223,'total_price':2400,'telephone':'18514281458','tickets':{'to_agent_ticket_info':[{'birthday':'1982-11-22','train_num':'6233','tag':1,'certificate_num':'110106198211222726','passenger_type':0,'to_station':'哈尔滨东','insurance_price':100,'certificate_type':'0','insurance_unit_price':2000,'seat':1,'ticket_price':100,'from_time':'2015-06-24 10:39:00','sub_order_id':'193528712011223','from_station':'哈尔滨','passenger_name':'吴枫','to_time':'2015-06-24 10:54:00'},{'birthday':'1955-02-28','train_num':'6233','tag':1,'certificate_num':'110106195502282768','passenger_type':0,'to_station':'哈尔滨东','insurance_price':100,'certificate_type':'0','insurance_unit_price':2000,'seat':1,'ticket_price':100,'from_time':'2015-06-24 10:39:00','sub_order_id':'193528712021223','from_station':'哈尔滨','passenger_name':'杨秀敏','to_time':'2015-06-24 10:54:00'},{'birthday':'1955-01-13','train_num':'6233','tag':1,'certificate_num':'110106195501132733','passenger_type':0,'to_station':'哈尔滨东','insurance_price':100,'certificate_type':'0','insurance_unit_price':2000,'seat':1,'ticket_price':100,'from_time':'2015-06-24 10:39:00','sub_order_id':'193528712031223','from_station':'哈尔滨','passenger_name':'吴宏祥','to_time':'2015-06-24 10:54:00'}]},'order_status':1,'latest_issue_time':'2015-05-08 16:52:55'}}";
        // tbiu.Traintest(xx, "175992312");
        //tbiu.TaobaoOrderChanginId(1006188095312l);测试改签
        try {
            tbiu.TaoBaoShou();//s授权
            tbiu.taobaoOuTtick();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            String path = TaobaoTarinMonitor.class.getResource("/PropertyValue.xml").toString();
            if (path != null && path.contains("file:/")) {
                path = path.replaceFirst("file:/", "");
            }
            PropertyWatchDog dog = new PropertyWatchDog(path);
            dog.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //tbiu.AnalysisOuTtick("{'description':'book-tbtestauto1942','extra':'','main_biz_order_id':193510814441223"
        //     + random.nextInt()
        //    + ",'msg_type':'1','sub_biz_order_id':0,'time_stamp':'2014-11-25 16:00:53','user_id':175992312}");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=utf-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        String param = "";
        try {
            param = req.getParameter("jsonStr");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        WriteLog.write("TaobaoTarinMonitor_t12306_4.5", param);
        //        System.out.println(param);
        if (param != null && param.length() > 0) {
            try {
                String msg = tbiu.AnalysisOuTtick(param);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        out.print("success");
        out.flush();
        out.close();

    }

}
