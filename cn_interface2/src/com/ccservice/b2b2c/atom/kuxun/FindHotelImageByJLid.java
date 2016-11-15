package com.ccservice.b2b2c.atom.kuxun;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;

/**
 * 
 * 
 *
 */
public class FindHotelImageByJLid extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String hotelid = request.getParameter("jlhotelid");
        JSONArray arry = new JSONArray();
        if (hotelid != null && !"".equals(hotelid)) {
            String sql = "select top 10 I.* from T_HOTELIMAGE I  left join T_HOTELALL A on A.ID=I.C_ZSHOTELID "
                    + " left join T_HOTEL  H on  H.C_ZSHOTELID=A.ID   "
                    + "WHERE I.C_ISNEWFLAG IS NOT NULL AND H.C_HOTELCODE='" + hotelid + "'";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    Map map = (Map) list.get(i);
                    String path = map.get("C_PATH") != null ? map.get("C_PATH").toString() : "";
                    String desc = map.get("C_DESCRIPTION") != null ? map.get("C_DESCRIPTION").toString() : "";
                    String jlcode = hotelid;
                    JSONObject obj = new JSONObject();
                    obj.put("id", jlcode);
                    obj.put("path", path);
                    obj.put("desc", desc);
                    arry.add(i, obj);
                }
            }
        }
        response.setCharacterEncoding("GBK");
        PrintWriter out = response.getWriter();
        WriteLog.write("JLIMAGE", "hotelid:" + hotelid);
        out.print(arry.toString());
        out.flush();
        out.close();
    }
}
