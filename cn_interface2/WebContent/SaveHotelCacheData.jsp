<%@page pageEncoding="utf-8"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="net.sf.json.JSONObject"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.cache.CacheHotelData"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%>
<%
    try {
        //请求数据
        BufferedReader br = new BufferedReader(new InputStreamReader(
                request.getInputStream(), "UTF-8"));
        String line = "";
        StringBuffer buf = new StringBuffer(1024);
        while ((line = br.readLine()) != null) {
            buf.append(line);
        }
        String json = URLDecoder.decode(buf.toString(), "utf-8");
        //解析数据
        JSONObject obj = JSONObject.fromObject(json);
        String qunarId = obj.getString("qid");
        JSONArray data = obj.getJSONArray("data");
        //循环数据
        Map<String, JSONArray> roomMap = new HashMap<String, JSONArray>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject room = data.getJSONObject(i);
            String roomName = room.getString("roomName");
            JSONArray roomArray = room.getJSONArray("roomArray");//缓存Map
            roomMap.put(roomName, roomArray);
        }
        //缓存数据
        if (roomMap.size() > 0) {
            new CacheHotelData()
                    .getQunarHotelPrice(qunarId, roomMap, 1);//强制更新
        }
    }
    catch (Exception e) {
    }
%>