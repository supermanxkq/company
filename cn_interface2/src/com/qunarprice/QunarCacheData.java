package com.qunarprice;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.hotel.cache.CacheHotelData;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class QunarCacheData {

    /**
     * @param QunarHotelId 去哪儿酒店ID
     * @param payType 支付类型  1：现付；2：预付
     */
    public static String getPriceByQunarHotelId(String QunarHotelId, String payType) {
        String ret = "";
        //取缓存
        Map<String, JSONArray> map = new HashMap<String, JSONArray>();
        map = new CacheHotelData().getQunarHotelPrice(QunarHotelId, map, 0);
        if (map.size() > 0) {
            //禁用代理
            String DisableAgents = ReptileGetQunarHotelPrice.getFileValue(3);
            List<String> DisableAgentList = new ArrayList<String>();
            if (!ElongHotelInterfaceUtil.StringIsNull(DisableAgents)) {
                String[] DisableAgentArray = DisableAgents.split("@");
                for (String agent : DisableAgentArray) {
                    if (ElongHotelInterfaceUtil.StringIsNull(agent)) {
                        continue;
                    }
                    DisableAgentList.add(agent.trim());
                }
            }
            //返回JSON
            JSONArray retarray = new JSONArray();
            //现付
            JSONArray xfarray = new JSONArray();
            Map<String, Integer> xfpriceflag = new HashMap<String, Integer>();//<去哪儿房型名称,价格>
            Map<String, Integer> xfstatusflag = new HashMap<String, Integer>();//<去哪儿房型名称,房态>
            Map<String, Integer> xfjsonflag = new HashMap<String, Integer>();//<去哪儿房型名称,JSONObject在JSONArray第几个>
            //预付
            JSONArray yfarray = new JSONArray();
            Map<String, Integer> yfpriceflag = new HashMap<String, Integer>();//<去哪儿房型名称,价格>
            Map<String, Integer> yfstatusflag = new HashMap<String, Integer>();//<去哪儿房型名称,房态>
            Map<String, Integer> yfjsonflag = new HashMap<String, Integer>();//<去哪儿房型名称,JSONObject在JSONArray第几个>
            //循环
            for (String roomName : map.keySet()) {
                JSONArray array = map.get(roomName);
                if (array == null || array.size() == 0) {
                    continue;
                }
                int size = array.size();
                for (int i = 0; i < size; i++) {
                    try {
                        //替换原有JSONObject
                        boolean replaceflag = false;
                        JSONObject rateobj = new JSONObject();
                        //解析数据
                        JSONObject obj = array.getJSONObject(i);
                        String bedName = obj.getString("bedName");
                        String agentId = obj.getString("agentId");
                        String agentName = obj.getString("agentName");
                        String paytype = obj.getString("paytype");//0：现付；1：预付
                        Double price = obj.getDouble("price");//真实价格
                        Double priceratio = 0d;//obj.getDouble("priceratio");//价格比例
                        Double fanyong = obj.getDouble("fanyong");//返佣
                        String roombf = obj.getString("roombf");//套餐，如提前，暂不取
                        int roomstatus = obj.getInt("roomstatus");//房态 1 开房；-1 关房；-2 休息中
                        String hashcode = obj.getString("hashcode");//暂不取
                        int danbaoflag = obj.getInt("danbaoflag");//0：不担保；1：担保
                        String tempbf = obj.getString("bfstr");//早餐
                        String webstr = obj.getString("webstr");//宽带
                        //判断
                        if (DisableAgentList.contains(agentName)
                                || (agentName.endsWith("团购") && DisableAgentList.contains("团购"))) {
                            continue;
                        }
                        int sealprice = price.intValue();
                        //B2B及B2C现付查询程序中卖价是真实价格+返佣，故作相减修改
                        if ("0".equals(paytype) && fanyong != null && fanyong.doubleValue() > 0) {
                            fanyong = (double) fanyong.intValue();
                            sealprice = (int) ElongHotelInterfaceUtil.subtract(sealprice, fanyong);
                        }
                        else {
                            fanyong = 0d;
                        }
                        //封装数据
                        rateobj.put("bedName", bedName);
                        rateobj.put("agentid", agentId);
                        rateobj.put("agentname", agentName);
                        rateobj.put("roomname", roomName);
                        rateobj.put("paytype", paytype);
                        rateobj.put("rPrice", sealprice);//计算后价格
                        rateobj.put("rPriceratio", priceratio);//比例为0
                        rateobj.put("roomstatus", roomstatus);
                        //比较价格，取最低的
                        if ("1".equals(paytype)) {//预付
                            if (yfpriceflag.containsKey(roomName)) {
                                int oldprice = yfpriceflag.get(roomName);
                                int oldstatus = yfstatusflag.get(roomName);
                                //已有房态开房
                                if (oldstatus == 1) {
                                    if (roomstatus == 1) {//现有开房
                                        //已有价格小一些
                                        if (oldprice <= sealprice)
                                            continue;
                                        replaceflag = true;
                                    }
                                    else {
                                        continue;//满房不覆盖开房
                                    }
                                }
                                else {
                                    if (roomstatus == 1) {//现有开房
                                        replaceflag = true;//无条件覆盖
                                    }
                                    else {
                                        //已有价格小一些
                                        if (oldprice <= sealprice)
                                            continue;
                                        replaceflag = true;
                                    }
                                }
                            }
                            yfpriceflag.put(roomName, sealprice);
                            yfstatusflag.put(roomName, roomstatus);
                        }
                        else {
                            if (xfpriceflag.containsKey(roomName)) {
                                int oldprice = xfpriceflag.get(roomName);
                                int oldstatus = xfstatusflag.get(roomName);
                                //已有房态开房
                                if (oldstatus == 1) {
                                    if (roomstatus == 1) {//现有开房
                                        //已有价格小一些
                                        if (oldprice <= sealprice)
                                            continue;
                                        replaceflag = true;
                                    }
                                    else {
                                        continue;//满房不覆盖开房
                                    }
                                }
                                else {
                                    if (roomstatus == 1) {//现有开房
                                        replaceflag = true;//无条件覆盖
                                    }
                                    else {
                                        //已有价格小一些
                                        if (oldprice <= sealprice)
                                            continue;
                                        replaceflag = true;
                                    }
                                }
                            }
                            xfpriceflag.put(roomName, sealprice);
                            xfstatusflag.put(roomName, roomstatus);
                        }
                        rateobj.put("roombf", roombf);
                        rateobj.put("url", "");
                        rateobj.put("fanyong", fanyong);
                        rateobj.put("hashcode", hashcode);
                        rateobj.put("danbaoflag", danbaoflag);
                        rateobj.put("agentRoom", "");
                        //早餐
                        String bfstr = "无早";
                        if ("单早".equals(tempbf) || "双早".equals(tempbf) || "三早".equals(tempbf) || "四早".equals(tempbf)) {
                            bfstr = tempbf;
                        }
                        else if ("单份早餐".equals(tempbf) || "一份早餐".equals(tempbf)) {
                            bfstr = "单早";
                        }
                        else if ("两份早餐".equals(tempbf) || "双份早餐".equals(tempbf)) {
                            bfstr = "双早";
                        }
                        else if ("含早餐".equals(tempbf) || "含早".equals(tempbf) || "美式自助早餐".equals(tempbf)
                                || "欧式早餐".equals(tempbf) || "自助早餐".equals(tempbf)) {
                            bfstr = "含早";
                        }
                        rateobj.put("bfstr", bfstr);
                        rateobj.put("webstr", webstr);
                        //替换原有JSONObject
                        if ("1".equals(paytype)) {//预付
                            if (replaceflag) {
                                yfarray.set(yfjsonflag.get(roomName), rateobj);
                            }
                            else {
                                int idx = yfarray.size();
                                yfarray.add(idx, rateobj);
                                yfjsonflag.put(roomName, idx);
                            }
                        }
                        else {
                            if (replaceflag) {
                                xfarray.set(xfjsonflag.get(roomName), rateobj);
                            }
                            else {
                                int idx = xfarray.size();
                                xfarray.add(idx, rateobj);
                                xfjsonflag.put(roomName, idx);
                            }
                        }
                        if (yfarray.size() > 0) {
                            retarray.addAll(yfarray);//优先显示预付
                        }
                        if (xfarray.size() > 0) {
                            retarray.addAll(xfarray);
                        }
                        if (retarray.size() > 0) {
                            ret = retarray.toString();
                        }
                    }
                    catch (Exception e) {
                    }
                }
            }
        }
        return ret;
    }
}