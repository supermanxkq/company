package com.ccservice.b2b2c.atom.service.travelskyhotel.util;

import java.util.Map;
import org.dom4j.Node;
import java.util.List;
import java.util.HashMap;
import org.dom4j.Element;
import org.dom4j.Document;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.BufferedReader;
import org.dom4j.io.SAXReader;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import com.ccservice.huamin.WriteLog;
import org.apache.commons.httpclient.Header;
import com.ccservice.b2b2c.atom.server.Server;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 中航信接口工具类
 * @author WH
 */

public class TravelskyHelper {
    /**
     * 航信价格计划名提取提前和连住天数
     * @param RatePlanName * 价格计划名(暑期特惠房/连住3晚及以上/提前3天预订/双早/单早/现付/默认航旅通-金色世纪联合价格计划 等等)
     * @param type 1：提前天数；2：连住天数
     */
    public static int RatePlanNameToDays(String RatePlanName, int type) {
        int days = type == 2 ? 1 : 0;//连住最少一天
        try {
            if (type == 1 && RatePlanName.contains("提前") && RatePlanName.contains("天")) {
                //提前1天预订含双早、提前3天预订、提前21天预订
                RatePlanName = RatePlanName.substring(RatePlanName.indexOf("提前") + 2);
                RatePlanName = RatePlanName.substring(0, RatePlanName.indexOf("天"));
                days = Integer.parseInt(RatePlanName.trim());
            }
            else if (type == 2 && RatePlanName.contains("连住") && RatePlanName.contains("晚")) {
                //连住2晚及以上、连住2晚及以上含双早、连住2晚送1晚/无限制
                RatePlanName = RatePlanName.substring(RatePlanName.indexOf("连住") + 2);
                RatePlanName = RatePlanName.substring(0, RatePlanName.indexOf("晚"));
                days = Integer.parseInt(RatePlanName.trim());
            }
        }
        catch (Exception e) {
        }
        return days;
    }

    /**
     * 航信宽带转换
     */
    public static int InternetToInt(String net) {
        int web = 0;//无
        if ("Y".equals(net)) {
            web = 2;//免费
        }
        else if ("C".equals(net)) {
            web = 3;//收费   
        }
        return web;
    }

    /**
     * 航信床型转换为去哪儿、用于比价上去哪儿
     * -1：其他；0：大床；1：双床；2：大/双床；3：三床；4：一单一双；5：单人床；6：上下铺；7：通铺；8：榻榻米；9：水床；10：圆床；11：拼床
     */
    public static int BedTypeToQunar(String BedType) {
        int bed = -1;
        if (ElongHotelInterfaceUtil.StringIsNull(BedType)) {
            bed = -1;
        }
        else if ("大床".equals(BedType)) {
            bed = 0;
        }
        else if ("双床".equals(BedType)) {
            bed = 1;
        }
        else if ("大/双".equals(BedType) || BedType.contains("大床") || BedType.contains("双床")) {
            bed = 2;
        }
        else if ("三床".equals(BedType)) {
            bed = 3;
        }
        else if ("一单一双".equals(BedType)) {
            bed = 4;
        }
        else if ("单人床".equals(BedType) || (BedType.contains("单人床"))) {
            bed = 5;
        }
        else if ("上下铺".equals(BedType)) {
            bed = 6;
        }
        else if ("通铺".equals(BedType)) {
            bed = 7;
        }
        else if ("榻榻米".equals(BedType)) {
            bed = 8;
        }
        else if ("水床".equals(BedType)) {
            bed = 9;
        }
        else if ("圆床".equals(BedType)) {
            bed = 10;
        }
        else if ("拼床".equals(BedType)) {
            bed = 11;
        }
        return bed;
    }

    /**
     * 航信床型转换为本地
     */
    public static int BedTypeToLocal(String BedType) {
        int bed = 0;
        if (ElongHotelInterfaceUtil.StringIsNull(BedType)) {
            bed = 0;
        }
        else if ("单人床".equals(BedType)) {
            bed = 1;
        }
        else if ("大床".equals(BedType)) {
            bed = 2;
        }
        else if ("双床".equals(BedType)) {
            bed = 3;
        }
        else if ("大/双".equals(BedType) || BedType.contains("大床") || BedType.contains("双床")) {
            bed = 4;
        }
        return bed;
    }

    /**
     * 航信酒店星级换成本地格式  <Rank>酒店星级 表示1-5星级：1S、2S、3S、4S、5S； 表示1-5准星级：1A、2A、3A、4A、5A String类型</Rank>
     */
    public static int RankToLocal(String Rank) {
        int local = 0;
        if ("1S".equals(Rank) || "2S".equals(Rank) || "3S".equals(Rank) || "4S".equals(Rank) || "5S".equals(Rank)) {
            local = Integer.parseInt(Rank.substring(0, 1));
        }
        else if ("3A".equals(Rank) || "4A".equals(Rank) || "5A".equals(Rank)) {
            local = (Integer.parseInt(Rank.substring(0, 1)) * 3) + 1;
        }
        return local;
    }

    /**
     * 航信省份对应本地
     */
    public static long getProvinceId(String hxProvince) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("北京市", 101);
        map.put("上海市", 102);
        map.put("天津市", 103);
        map.put("重庆", 104);
        map.put("河北", 105);
        map.put("山西", 106);
        map.put("内蒙古自治区", 107);
        map.put("辽宁", 108);
        map.put("吉林", 109);
        map.put("黑龙江", 110);
        map.put("江苏", 111);
        map.put("浙江", 112);
        map.put("安徽", 113);
        map.put("福建", 114);
        map.put("江西", 115);
        map.put("山东", 116);
        map.put("河南", 117);
        map.put("湖北", 118);
        map.put("湖南", 119);
        map.put("广东", 120);
        map.put("广西壮族自治区", 121);
        map.put("海南", 122);
        map.put("四川", 123);
        map.put("贵州", 124);
        map.put("云南", 125);
        map.put("西藏自治区", 126);
        map.put("陕西", 127);
        map.put("甘肃", 128);
        map.put("宁夏回族自治区", 129);
        map.put("青海", 130);
        map.put("新疆维吾尔自治区", 131);
        map.put("香港", 132);
        map.put("澳门", 133);
        return map.get(hxProvince) == null ? 0 : map.get(hxProvince);
    }

    /**
     * 根据XML名称，获取Document、设置时间戳和用户信息
     */
    public static Document getDocument(String xmlName) throws Exception {
        String OfficeID = Server.getInstance().getTravelsky_OfficeID();
        Document doc = new SAXReader().read(TravelskyHelper.class.getResourceAsStream(xmlName + ".xml"));
        //时间戳
        Element ele = TravelskyHelper.getSingleNode(doc, "OTRequest/Header/TimeStamp");
        ele.setText(ElongHotelInterfaceUtil.getCurrentTime());
        //OfficeID
        ele = getSingleNode(doc, "OTRequest/IdentityInfo/OfficeID");
        ele.setText(OfficeID);
        //UserID
        ele = getSingleNode(doc, "OTRequest/IdentityInfo/UserID");
        ele.setText(Server.getInstance().getTravelsky_UserID());

        //Password
        ele = getSingleNode(doc, "OTRequest/IdentityInfo/Password");
        ele.setText(Server.getInstance().getTravelsky_Password());
        //OfficeCode
        ele = getSingleNode(doc, "OTRequest/Source/OfficeCode");
        ele.setText(OfficeID);
        //Return
        return doc;
    }

    /**
     * 通过Document获取XML单节点
     */
    public static Element getSingleNode(Document doc, String nodename) {
        return (Element) doc.selectSingleNode(nodename);
    }

    /**
     * 通过Document获取XML多节点
     */
    @SuppressWarnings("unchecked")
    public static List<Node> getNodes(Document doc, String nodename) {
        List<Node> nodes = doc.selectNodes(nodename);
        if (nodes == null) {
            nodes = new ArrayList<Node>();
        }
        return nodes;
    }

    /**
     * 航信系统级错误、业务级错误
     */
    public static String hxErrorCheck(Document doc, String node, String resXml) {
        try {
            //业务级错误
            Element ele = getSingleNode(doc, "OTResponse/" + node.trim() + "/Errors/Error");
            errorInfo(ele, resXml);
            //系统级错误
            ele = getSingleNode(doc, "OTResponse/Errors/Error");
            errorInfo(ele, resXml);
            return "ResultIsTrue";
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    private static void errorInfo(Element ele, String resXml) throws Exception {
        if (ele != null) {
            String errorDesc = "";
            if (ele.attribute("ErrorDesc") != null) {
                errorDesc = "HX错误描述：" + ele.attribute("ErrorDesc").getText();
            }
            if (ElongHotelInterfaceUtil.StringIsNull(errorDesc)) {
                if (ele.attribute("ErrorCode") != null) {
                    errorDesc = "HX错误编码：" + ele.attribute("ErrorCode").getText();
                }
            }
            if (ElongHotelInterfaceUtil.StringIsNull(errorDesc)) {
                errorDesc = "HX返回：" + resXml;
            }
            throw new Exception(errorDesc);
        }
    }

    /**
     * 请求中航信
     */
    public static Document postXML(String xml) {
        //response
        Document doc = null;
        //connection
        InputStream in = null;
        BufferedReader br = null;
        PostMethod postMethod = null;
        HttpClient httpClient = new HttpClient();
        try {
            postMethod = new PostMethod(Server.getInstance().getTravelsky_Address());
            postMethod.addRequestHeader("Connection", "Keep-Alive");
            postMethod.addRequestHeader("ContentType", "text/xml; charset=utf-8");
            postMethod.addRequestHeader("ContentLength", Integer.toString(xml.length()));
            postMethod.setQueryString(new NameValuePair[] { new NameValuePair("request", xml) });
            //状态编码
            if (httpClient.executeMethod(postMethod) == HttpStatus.SC_OK) {
                //获取响应消息体
                in = postMethod.getResponseBodyAsStream();
                //获取消息头Content-Encoding判断数据流是否gzip压缩过
                Header contentEncoding = postMethod.getResponseHeader("Content-Encoding");
                if (contentEncoding != null && "gzip".equalsIgnoreCase(contentEncoding.getValue())) {
                    br = new BufferedReader(new InputStreamReader(new GZIPInputStream(in), "UTF-8"));
                }
                else {
                    br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                }
                doc = new SAXReader().read(br);
            }
            else {
                WriteLog.write("中航信酒店", "HttpStatus：" + postMethod.getStatusLine().toString() + "；请求XML：" + xml);
            }
        }
        catch (Exception e) {
            WriteLog.write("中航信酒店", ElongHotelInterfaceUtil.errormsg(e) + "；请求XML：" + xml);
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (Exception e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                }
                catch (Exception e) {
                }
            }
            // 释放连接
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
        }
        //return
        return doc;
    }
}