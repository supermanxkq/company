package com.ccservice.b2b2c.atom.service.jltour;

import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.huamin.WriteLog;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.b2b2c.atom.hotel.cache.CacheHotelData;
import com.ccservice.b2b2c.base.hmhotelprice.JLPriceResult;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service.jltour.util.JlTourUtil;

/**
 * 深捷旅JSON接口数据
 * @author WH
 */

public class JlTourHotelJsonData {

    /**
     * 酒店基本信息，用于同步深捷旅基础信息到本地
     * @param hotelIds 深捷旅的酒店ID，支持多个查询，用“/”隔开，如 1/2/3/4/5，每次请求最多查询20个
     * @param hotelMap key：深捷旅的酒店ID，用于通过深捷旅酒店ID取本地酒店，如为空则实时查询本地酒店
     * @param errorflag 出现错误，0：默认；1：出错了，用于多个查询后深捷旅抛异常，拆分ID，单个请求。非本方法内调用此接口均设为0。
     */
    @SuppressWarnings("unchecked")
    public void getHotelInfo(String hotelIds, Map<String, Hotel> hotelMap, int errorflag) throws Exception {
        if (ElongHotelInterfaceUtil.StringIsNull(hotelIds)) {
            throw new Exception("酒店ID为空!");
        }
        hotelIds = hotelIds.trim();
        if ("/".equals(hotelIds)) {
            throw new Exception("酒店ID错误!");
        }
        if (hotelMap == null) {
            hotelMap = new HashMap<String, Hotel>();
        }
        if (hotelIds.endsWith("/")) {
            hotelIds = hotelIds.substring(0, hotelIds.length() - 1);
        }
        //封装请求
        JSONObject obj = new JSONObject();
        obj.put("hotelIds", hotelIds);
        obj.put("queryType", "hotelinfo");
        //POST
        String ret = JlTourUtil.getMiddleData(obj);
        //解析
        int success = 8;
        JSONObject jl = new JSONObject();
        try {
            jl = JSONObject.fromObject(ret);
            success = jl.getInt("success");
        }
        catch (Exception e) {
            success = 8;
            jl = new JSONObject();
        }
        //1：成功；8：失败
        if (success == 8) {
            //单个请求
            String[] idArray = hotelIds.split("/");
            if (errorflag == 0 && idArray.length > 1) {
                for (String id : idArray) {
                    if (ElongHotelInterfaceUtil.StringIsNull(id)) {
                        continue;
                    }
                    Map<String, Hotel> tempMap = new HashMap<String, Hotel>();
                    tempMap.put(id, hotelMap.get(id));
                    //重新请求
                    getHotelInfo(id, tempMap, 1);
                }
                return;
            }
            else if (idArray.length > 1) {
                throw new Exception("请求参数错误.");
            }
            else {
                String hotelid = idArray[0];
                //本地酒店
                Hotel old = hotelMap.get(hotelid);
                //为空
                if (old == null) {
                    String sql = "where C_HOTELCODE = '" + hotelid + "' and C_SOURCETYPE = 6";
                    List<Hotel> localList = Server.getInstance().getHotelService().findAllHotel(sql, "", 1, 0);
                    if (localList != null && localList.size() > 0) {
                        old = localList.get(0);
                    }
                }
                if (old != null && old.getId() > 0 && jl.containsKey("data")
                        && (jl.getJSONArray("data") == null || jl.getJSONArray("data").size() == 0)) {
                    if (old.getState() == null || old.getState().intValue() != 0) {
                        old.setState(0);//禁用
                        Server.getInstance().getHotelService().updateHotelIgnoreNull(old);
                        System.out.println(old.getName() + "，酒店信息未查到，禁用。");
                    }
                }
                else {
                    //日志
                    WriteLog.write("深捷旅酒店JSON接口-基础信息", ret);
                }
                return;
            }
        }
        JSONArray data = new JSONArray();
        try {
            data = jl.getJSONArray("data");
        }
        catch (Exception e) {
            WriteLog.write("深捷旅酒店JSON接口-基础信息", ret);
            return;
        }
        int size = data.size();
        Map<Integer, City> cityMap = new HashMap<Integer, City>();//key：深捷旅城市ID
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < size; i++) {
            try {
                JSONObject hotel = data.getJSONObject(i);
                String hotelid = hotel.getString("hotelid");
                String updatetime = hotel.getString("updatetime");//最后更新时间
                //本地酒店
                Hotel old = hotelMap.get(hotelid);
                //为空
                if (old == null) {
                    String sql = "where C_HOTELCODE = '" + hotelid + "' and C_SOURCETYPE = 6";
                    List<Hotel> localList = Server.getInstance().getHotelService().findAllHotel(sql, "", 1, 0);
                    if (localList != null && localList.size() > 0) {
                        old = localList.get(0);
                    }
                }
                if (old == null) {
                    old = new Hotel();
                }
                //房型
                JSONArray rooms = hotel.containsKey("rooms") ? hotel.getJSONArray("rooms") : new JSONArray();
                if (rooms == null) {
                    rooms = new JSONArray();
                }
                //时间一致，数据未变化，不再向下进行操作
                String oldtime = old.getElModifytime() == null ? "" : old.getElModifytime();
                if (oldtime.equals(updatetime)) {
                    System.out.println("=====酒店信息无变化，不更新，深捷旅酒店：" + old.getName() + "=====");
                }
                else {
                    String hotelcd = hotel.getString("hotelcd");
                    String namechn = hotel.getString("namechn").trim();
                    String nameeng = hotel.getString("nameeng");
                    int star = JlTourUtil.getStar(hotel.getInt("star"));
                    //判断是不是国内酒店
                    boolean isChina = JlTourUtil.isChina(hotel.getInt("country"));
                    //非中国
                    if (!isChina) {
                        //房型ID存缓存，用于深捷旅推送价格时，直接跳过国际酒店房型
                        if (rooms.size() > 0) {
                            for (int j = 0; j < rooms.size(); j++) {
                                try {
                                    JSONObject room = rooms.getJSONObject(j);
                                    String jlRoomId = room.getString("roomtypeid");//房型编号
                                    String jlRoomName = room.getString("namechn").trim();//客房中文名称
                                    new CacheHotelData().checkJlTourInternationalRoom(jlRoomId, namechn + "["
                                            + jlRoomName + "]");
                                }
                                catch (Exception e) {
                                }
                            }
                        }
                        //存在，可能性小
                        if (old != null && old.getId() > 0) {
                            old.setHotelcode2(hotelcd);
                            old.setName(namechn);
                            old.setEnname(nameeng);
                            old.setState(0);//禁用
                            Server.getInstance().getHotelService().updateHotelIgnoreNull(old);
                            System.out.println(namechn + "，国际酒店，禁用。");
                        }
                        else {
                            System.out.println(namechn + "，国际酒店，跳过。");
                        }
                        continue;
                    }
                    //int state = hotel.getInt("state");//省份
                    int city = hotel.getInt("city");//城市
                    //int zone = hotel.getInt("zone");//行政区
                    //int bd = hotel.getInt("bd");//商业区
                    String floor = hotel.getString("floor");//层高
                    //String website = hotel.getString("website");//酒店网址
                    String addresschn = hotel.getString("addresschn");//中文地址
                    //String adresseng = hotel.getString("adresseng");//英文地址
                    String interiornotes = hotel.getString("interiornotes");//特别提示
                    String centraltel = hotel.getString("centraltel");//电话
                    String fax = hotel.getString("fax");//传真
                    String postcode = hotel.getString("postcode");//邮编
                    //String email = hotel.getString("email");
                    //String language = hotel.getString("language");//语言类型
                    //String themetype = hotel.getString("themetype");//酒店主题
                    String introducechn = hotel.getString("introducechn");//酒店中文介绍
                    //String summarychn = hotel.getString("summarychn");//中文摘要
                    //String allowcreditcard = hotel.getString("allowcreditcard");//能处理的信用卡
                    String facilities = hotel.getString("facilities");//酒店设施
                    //String remark = hotel.getString("remark");//备注
                    //String keynames = hotel.getString("keynames");//关键字
                    String jingdu = hotel.getString("jingdu");//精度
                    String weidu = hotel.getString("weidu");//纬度
                    int active = hotel.getInt("active");//是否生效，1：生效；8：禁用
                    //String outeriornotes = hotel.getString("outeriornotes");//外部备注
                    //String createtime = hotel.getString("createtime");//创建时间
                    //封装酒店信息
                    old.setHotelcode(hotelid);
                    old.setHotelcode2(hotelcd);
                    old.setName(namechn);
                    old.setEnname(nameeng);
                    old.setStar(star);
                    //城市
                    City local = cityMap.get(city);
                    if (local == null) {
                        String citysql = "where C_JLCODE = '" + city + "'";
                        List<City> cityList = Server.getInstance().getHotelService().findAllCity(citysql, "", 1, 0);
                        if (cityList != null && cityList.size() > 0) {
                            local = cityList.get(0);
                            cityMap.put(city, local);
                        }
                    }
                    if (local != null) {
                        old.setCityid(local.getId());
                        old.setProvinceid(local.getProvinceid());
                    }
                    old.setMainfloor(floor);
                    old.setAddress(addresschn);
                    old.setMarkettell(centraltel);
                    old.setAvailPolicy(interiornotes);
                    old.setFax1(fax);
                    old.setPostcode(postcode);
                    old.setDescription(introducechn);
                    //酒店设施
                    if (!ElongHotelInterfaceUtil.StringIsNull(facilities) && facilities.contains(",")) {
                        String serviceitem = "";
                        String[] items = facilities.split(",");
                        for (int j = 0; j < items.length; j++) {
                            String item = items[j].trim();
                            String facilitiy = JlTourUtil.getFacilitiy(item);
                            if (!ElongHotelInterfaceUtil.StringIsNull(facilitiy)) {
                                serviceitem += facilitiy + ",";
                            }
                        }
                        if (serviceitem.endsWith(",")) {
                            serviceitem = serviceitem.substring(0, serviceitem.length() - 1);
                            old.setServiceitem(serviceitem);
                        }
                    }
                    try {
                        old.setLng(Double.parseDouble(jingdu));
                        old.setLat(Double.parseDouble(weidu));
                    }
                    catch (Exception e) {
                    }
                    if (active == 1 && (old.getHcontrol() == null || old.getHcontrol().longValue() != 1)
                            && rooms.size() > 0) {//生效、非人工禁用、包含房型
                        old.setState(3);
                    }
                    else {
                        old.setState(0);
                    }
                    old.setCountryid(168l);
                    old.setSourcetype(6l);
                    old.setType(1);
                    old.setPaytype(2l);
                    old.setLastupdatetime(format.format(new Date()));
                    old.setElModifytime(updatetime);
                    //新增
                    if (old.getId() == 0) {
                        old = Server.getInstance().getHotelService().createHotel(old);
                        System.out.println("-----新增深捷旅酒店-----" + old.getName() + "-----");
                    }
                    else {
                        Server.getInstance().getHotelService().updateHotelIgnoreNull(old);
                        System.out.println("~~~~~更新深捷旅酒店~~~~~" + old.getName() + "~~~~~");
                    }
                }
                if (old.getId() > 0 && rooms.size() > 0) {
                    getRoomInfo(rooms, old, format);
                }
            }
            catch (Exception e) {
                WriteLog.write("深捷旅酒店JSON接口-基础信息", ElongHotelInterfaceUtil.errormsg(e) + "###" + ret);
            }
        }
    }

    /**
     * 房型基本信息，用于同步深捷旅基础信息到本地
     */
    @SuppressWarnings("unchecked")
    private void getRoomInfo(JSONArray rooms, Hotel hotel, SimpleDateFormat format) {
        long hotelid = hotel.getId();
        String sql = "where C_HOTELID = " + hotelid;
        List<Roomtype> localList = Server.getInstance().getHotelService().findAllRoomtype(sql, "", -1, 0);
        Map<String, Roomtype> localMap = new HashMap<String, Roomtype>();//key：深捷旅房型ID
        for (Roomtype r : localList) {
            localMap.put(r.getRoomcode(), r);
        }
        Map<String, Roomtype> existsMap = new HashMap<String, Roomtype>();//深捷旅本地存在的房型，key：深捷旅房型ID
        for (int j = 0; j < rooms.size(); j++) {
            String ret = "";
            try {
                JSONObject room = rooms.getJSONObject(j);
                ret = room.toString();
                String roomtypeid = room.getString("roomtypeid");//房型编号
                String namechn = room.getString("namechn").trim();//客房中文名称
                String updatetime = room.getString("updatetime");//最后修改时间
                Roomtype old = localMap.get(roomtypeid);
                if (old == null) {
                    old = new Roomtype();
                }
                else {
                    existsMap.put(roomtypeid, old);
                }
                //时间一致，数据未变化，不再向下进行操作
                String oldtime = old.getModifytime() == null ? "" : old.getModifytime();
                if (oldtime.equals(updatetime)) {
                    System.out.println("房型信息无变化，不更新，深捷旅房型：" + namechn);
                    continue;
                }
                String acreages = room.getString("acreages");//客房面积
                int bedtype = JlTourUtil.getBed(room.getString("bedtype"));//床型
                String bedsize = room.getString("bedsize");//床尺寸
                if (!ElongHotelInterfaceUtil.StringIsNull(bedsize) && bedsize.trim().length() > 50) {
                    bedsize = bedsize.trim().substring(0, 50);
                }
                String allowaddbed = room.getString("allowaddbed");//是否允许加床，1：是；8：否
                String allowaddbedqty = room.getString("allowaddbedqty");//加床数量
                String allowaddbedsize = room.getString("allowaddbedsize");//加床尺寸
                String nosm = room.getString("nosm");//该房型有无无烟房，1：是；8：否
                String floordistribution = room.getString("floordistribution");//房型分布在多少层
                //String nettype = room.getString("nettype");//1：宽带；2：拨号；8：无
                String remark = room.getString("remark");//房型设施
                String remark2 = room.getString("remark2");//信息备注
                int active = room.getInt("active");//是否生效，1：生效；8：禁用
                //封装房型信息
                old.setName(namechn);
                old.setRoomcode(roomtypeid);
                old.setHotelid(hotelid);
                old.setBed(bedtype);
                old.setBedsize(bedsize);
                old.setAreadesc(acreages);
                old.setAddflag(allowaddbed);
                old.setAddbednum(allowaddbedqty);
                old.setAddbedsize(allowaddbedsize);
                old.setNosm(nosm);
                old.setLayer(floordistribution);
                old.setRoomset(remark);
                old.setNote(remark2);
                if (active == 1) {
                    old.setState(1);
                }
                else {
                    old.setState(0);
                }
                old.setWideband(0);
                old.setLanguage(0);
                old.setModifytime(updatetime);
                old.setLastupdatetime(format.format(new Date(System.currentTimeMillis())));
                //新增
                if (old.getId() == 0) {
                    old = Server.getInstance().getHotelService().createRoomtype(old);
                    System.out.println("新增深捷旅房型-----" + namechn);
                }
                else {
                    Server.getInstance().getHotelService().updateRoomtypeIgnoreNull(old);
                    System.out.println("更新深捷旅房型~~~~~" + namechn);
                }
            }
            catch (Exception e) {
                WriteLog.write("深捷旅酒店JSON接口-基础信息", ElongHotelInterfaceUtil.errormsg(e) + "+++" + ret);
            }
        }
        //删除多余房型
        if (localMap.size() > 0) {
            //移除本地有的
            if (existsMap.size() > 0) {
                for (String jlRoomId : existsMap.keySet()) {
                    if (localMap.containsKey(jlRoomId)) {
                        localMap.remove(jlRoomId);
                    }
                }
            }
            if (localMap.size() > 0) {
                String jlRoomIds = "";
                for (String jlRoomId : localMap.keySet()) {
                    jlRoomIds += "'" + jlRoomId + "',";
                }
                if (jlRoomIds.endsWith(",")) {
                    jlRoomIds = jlRoomIds.substring(0, jlRoomIds.length() - 1);
                    String delSql = "delete from T_ROOMTYPE where C_HOTELID = " + hotelid + " and C_ROOMCODE in ("
                            + jlRoomIds + ")";
                    Server.getInstance().getSystemService().findMapResultBySql(delSql, null);
                    System.out.println("=====删除深捷旅多余房型=====");
                }
            }
        }
    }

    /**
     * 酒店房型价格
     * @param type 查询类型
     *          |--> hotelpriceall ：申请+即时确认；
     *          |--> hotelpricecomfirm ：只要即时确认，不填默认置为hotelpriceall；
     *          |--> UpdateChange：特殊，用于深捷旅变价通知时，异步更新淘宝数据
     * @param hotelIds深 捷旅酒店ID，多个ID用斜杠分开，例如：1/2/3/4/5，如果按酒店查询，则查询的是整个酒店的指定时段的价格数据.
     * @param roomIds 深捷旅房型ID，多个ID用斜杠分开，例如：1/2/3/4，如果按房型查询，则查询的是指定房型的价格数据，如果hotelIds和roomIds均有值，则优先取roomIds数据，忽略hotelIds中的数据.
     * @param checkInDate checkOutDate 入离店时间，时间范围最多一个月，如果超过一个月，则捷旅自动截取.
     */
    public List<JLPriceResult> getHotelPrice(String type, String hotelIds, String roomIds, String checkInDate,
            String checkOutDate) {
        String ret = "";
        String oldType = type;//原类型
        List<JLPriceResult> list = new ArrayList<JLPriceResult>();
        try {
            //验证数据
            if (ElongHotelInterfaceUtil.StringIsNull(type) || "UpdateChange".equals(type)) {
                type = "hotelpriceall";
            }
            //房型ID不为空，优先取房型ID
            if (!ElongHotelInterfaceUtil.StringIsNull(roomIds)) {
                hotelIds = "";
            }
            if (ElongHotelInterfaceUtil.StringIsNull(hotelIds) && ElongHotelInterfaceUtil.StringIsNull(roomIds)) {
                throw new Exception("酒店、房型ID不能同时为空!");
            }
            if (ElongHotelInterfaceUtil.StringIsNull(checkInDate) || ElongHotelInterfaceUtil.StringIsNull(checkOutDate)) {
                throw new Exception("价格日期为空!");
            }
            //封装请求
            JSONObject obj = new JSONObject();
            obj.put("queryType", type);
            if (!ElongHotelInterfaceUtil.StringIsNull(hotelIds)) {
                obj.put("hotelIds", hotelIds);
            }
            if (!ElongHotelInterfaceUtil.StringIsNull(roomIds)) {
                obj.put("roomtypeids", roomIds);
            }
            obj.put("checkInDate", checkInDate);
            obj.put("checkOutDate", checkOutDate);
            //POST
            ret = JlTourUtil.getMiddleData(obj);
            //解析
            if (ElongHotelInterfaceUtil.StringIsNull(ret)) {
                throw new Exception("深捷旅返回数据为空!");
            }
            JSONObject jl = JSONObject.fromObject(ret);
            //1：成功；8：失败
            int success = jl.getInt("success");
            if (success == 8) {
                String msg = jl.getString("msg");
                //服务器时间早入深捷旅时间--->{"data":[],"msg":"只能查询30天之内的价格/入住日期要大于当前日期/退房日期要大于入住日期！","success":8}
                if (!ElongHotelInterfaceUtil.StringIsNull(msg) && msg.contains("入住日期要大于当前日期")) {
                    //加一天，重新请求
                    String newCheckInDate = ElongHotelInterfaceUtil.getAddDate(checkInDate, 1);
                    int days = ElongHotelInterfaceUtil.getSubDays(newCheckInDate, checkOutDate);
                    if (days > 0) {
                        return getHotelPrice(oldType, hotelIds, roomIds, newCheckInDate, checkOutDate);
                    }
                }
                throw new Exception(msg);
            }
            JSONArray data = jl.getJSONArray("data");
            data = data == null ? new JSONArray() : data;
            if ("UpdateChange".equals(oldType)) {
                try {
                    JSONObject taoBaoObj = new JSONObject();
                    taoBaoObj.put("jlRoomId", roomIds);
                    taoBaoObj.put("jlDatas", data.toString());
                    new SyncUpdateTaoBaoData(taoBaoObj.toString()).start();
                }
                catch (Exception e) {
                    WriteLog.write("异步更新淘宝数据", ElongHotelInterfaceUtil.errormsg(e));
                }
            }
            int size = data.size();
            for (int i = 0; i < size; i++) {
                JSONObject room = data.getJSONObject(i);
                int hotelId = room.getInt("hotelId");//深捷旅酒店ID
                String hotelCd = room.getString("hotelCd");
                int roomtypeId = room.getInt("roomtypeId");//房型ID
                String roomtypeName = room.getString("roomtypeName");//房型名称
                if (hotelId <= 0 || roomtypeId <= 0 || ElongHotelInterfaceUtil.StringIsNull(roomtypeName)
                        || !room.containsKey("roomPriceDetail")) {
                    continue;
                }
                //客户类型、市场：11 所有市场 12 中国大陆市场 13 日本市场 14 香港市场 15 俄罗斯市场 16 澳门市场，先看不适用市场，为空再看适用市场
                String noacceptcustomer = room.containsKey("noacceptcustomer") ? room.getString("noacceptcustomer")
                        : "";
                //String acceptcustomer = room.containsKey("acceptcustomer") ? room.getString("acceptcustomer") : "";
                if ("11".equals(noacceptcustomer)) {
                    continue;
                }
                //价格明细
                JSONArray roomPriceDetail = room.getJSONArray("roomPriceDetail");
                int priceSize = roomPriceDetail.size();
                for (int j = 0; j < priceSize; j++) {
                    JLPriceResult rs = new JLPriceResult();
                    //解析价格
                    JSONObject price = roomPriceDetail.getJSONObject(j);
                    //11:现付，12:预付
                    String pricingtype = price.getString("pricingtype");
                    //货币
                    String currency = price.getString("currency");
                    //keyid
                    String keyid = price.containsKey("keyid") ? price.getString("keyid") : "";
                    if (!"12".equals(pricingtype) || !"RMB".equals(currency)
                            || ElongHotelInterfaceUtil.StringIsNull(keyid)) {
                        continue;
                    }
                    //配额类型
                    String allotmenttype = price.getString("allotmenttype");
                    //供应商ID
                    String supplierid = price.getString("supplierid");
                    //日期，"night": "2014-10-01 12:00:00"
                    String night = price.getString("night").split(" ")[0];
                    //同行标准价
                    double preeprice = price.getDouble("preeprice");
                    if (preeprice <= 0) {
                        continue;
                    }
                    //套餐名称
                    String ratetype = price.getString("ratetype");
                    String ratetypename = price.getString("ratetypename");
                    //早餐
                    int includebreakfastqty2 = price.getInt("includebreakfastqty2");
                    if (includebreakfastqty2 >= 34 && includebreakfastqty2 <= 45) {
                        includebreakfastqty2 = 34;
                    }
                    //当前可售房间数量，大于0表示可即时确认；等于0表示需要等待确认；小于0表示满房 
                    int qtyable = price.getInt("qtyable");
                    //上网类型，1：宽带；2：拔号；3: wi-fi；8: 无网络
                    //int internetprice = price.containsKey("internetprice") ? price.getInt("internetprice") : 8;
                    //是否免费带宽，如收费则上为网价格，<0: 收费，金额未定 =0:免费 =1: 收费，金额未定 >1:上网费用
                    String netcharge = price.containsKey("netcharge") ? price.getString("netcharge") : "0";
                    //最后修改时间，每次更新都以最后修改时间参考
                    String lastupdatepricetime = price.getString("lastupdatepricetime");
                    //ADD
                    rs.setCurrency(currency);
                    rs.setSupplierid(supplierid);
                    rs.setRatetype(ratetypename);
                    rs.setRatetypeid(ratetype);
                    rs.setAllotmenttype(allotmenttype);
                    rs.setJlroomtype(Integer.toString(roomtypeId));
                    //预订条款类型:提前订房、指定日期前、连住晚数、指定时间段，11：提前预订 12：指定日期前订 13：连住晚数 14：指定时间段能订
                    int termtype = price.containsKey("termtype") ? price.getInt("termtype") : 0;
                    //提前预订
                    if (termtype == 11) {
                        String advancedays = price.containsKey("advancedays") ? price.getString("advancedays") : "0";
                        rs.setLeadTime(advancedays);
                    }
                    //指定日期前订
                    else if (termtype == 12) {
                        //指定日期
                        //String appointeddate = price.containsKey("appointeddate") ? price.getString("appointeddate") : "";
                        continue;
                    }
                    //连住晚数
                    else if (termtype == 13) {
                        //连住晚数
                        String continuousdays = price.containsKey("continuousdays") ? price.getString("continuousdays")
                                : "1";
                        rs.setMinDay(continuousdays);
                    }
                    //指定时间段能订
                    else if (termtype == 14) {
                        //指定开始日期、指定结束日期
                        //String beginday = price.containsKey("beginday") ? price.getString("beginday") : "";
                        //String endday = price.containsKey("endday") ? price.getString("endday") : "";
                        continue;
                    }
                    rs.setHotelcd(hotelCd);
                    rs.setHotelid(String.valueOf(hotelId));
                    rs.setHotelName(price.getString("namechn"));
                    rs.setJlkeyid(keyid);
                    rs.setJltime(lastupdatepricetime);
                    rs.setPricetypr(pricingtype);
                    rs.setNetfee(netcharge);
                    rs.setStayDate(night);
                    rs.setFangliang(String.valueOf(qtyable));
                    rs.setAllot(qtyable < 0 ? "16" : "");
                    rs.setRoomtype(roomtypeName.trim());
                    rs.setPprice(String.valueOf(preeprice));
                    rs.setBreakfast(String.valueOf(includebreakfastqty2));
                    /**
                     * 备注：深捷旅在JSON取消规则上作了逻辑修改，与mysql同步时不一样
                     * 先看voidabletype是不是13即订即保，如果不是就看Dayselect和Timeselect字段，表示入住前多久可以取消。
                     * Dayselect int类型  如 1，Timeselect string类型 如 18:00，表示入住前1天的18:00后就不能取消了
                     * Dayselect和Timeselect为空不限制，据行业行规是当天18:00后不能取消
                     */
                    //默认
                    String voidabletype = "13";
                    String canceldesc = "此房即订即保，一但预订不可取消和修改";
                    try {
                        //取消修改条款，11：提前天数  12：指定时段 13：即订即保  14：无限制
                        int Jlvoidabletype = price.containsKey("voidabletype") ? price.getInt("voidabletype") : 13;
                        //非即订即保
                        if (Jlvoidabletype != 13) {
                            voidabletype = "12";//设为指定时段
                            String restype = price.containsKey("restype") ? price.getString("restype") : "";//11：入住前；12：确认后
                            //入住前，其他直接设为不可取消和修改
                            if ("11".equals(restype)) {
                                String day = price.containsKey("dayselect") ? price.getString("dayselect") : "";//入住前 多少天
                                String time = price.containsKey("timeselect") ? price.getString("timeselect") : "";//入住前多少天几点前
                                String noeditorcancel = price.containsKey("noeditorcancel") ? price
                                        .getString("noeditorcancel") : "";//11：不可修改；12：不可取消
                                String noedit = price.containsKey("noedit") ? price.getString("noedit") : "";//不可修改内容
                                String cashscaletype = price.containsKey("cashscaletype") ? price
                                        .getString("cashscaletype") : "";//担保金额类型 11：首日：12：全额
                                //取消规则拼写
                                if (ElongHotelInterfaceUtil.StringIsNull(day) || "0".equals(day.trim())) {
                                    day = "0";
                                    canceldesc = "入住当天";
                                }
                                else {
                                    canceldesc = "入住前" + Integer.parseInt(day.trim()) + "天";
                                }
                                if (!ElongHotelInterfaceUtil.StringIsNull(time)) {
                                    canceldesc += time.trim() + "后";
                                }
                                else {
                                    time = "0:00";
                                }
                                /*
                                if ("11".equals(noeditorcancel)) {
                                    canceldesc += "不可修改，";
                                }
                                else if ("12".equals(noeditorcancel)) {
                                    canceldesc += "不可取消，";
                                }
                                else {
                                    canceldesc += "不可取消和修改，";
                                }
                                */
                                canceldesc += "不可取消和修改，";
                                if ("11".equals(cashscaletype)) {
                                    canceldesc += "担保金额为首日房费";
                                }
                                else {
                                    canceldesc += "担保金额为全额房费";
                                }
                                rs.setDayselect(day.trim());
                                rs.setTimeselect(time.trim());
                                rs.setNoeditorcancel(noeditorcancel);
                                rs.setNoedit(noedit);
                                rs.setGuaranteeamounttype(cashscaletype);
                            }
                        }
                    }
                    catch (Exception e) {
                        voidabletype = "13";
                        canceldesc = "此房即订即保，一但预订不可取消和修改";
                    }
                    rs.setVoidabletype(voidabletype);
                    rs.setCanceldesc(canceldesc);
                    //ADD
                    list.add(rs);
                }
            }
        }
        catch (Exception e) {
            WriteLog.write("深捷旅酒店JSON接口-价格信息", ElongHotelInterfaceUtil.errormsg(e) + "###" + ret);
        }
        return list;
    }

    /**
     * 深捷旅屏蔽酒店
     */
    public void getShieldHotel() {
        //封装请求
        JSONObject obj = new JSONObject();
        obj.put("queryType", "checkshieldid");
        //POST
        String ret = "";
        try {
            ret = JlTourUtil.getMiddleData(obj);
        }
        catch (Exception e) {
        }
        //解析
        if (ElongHotelInterfaceUtil.StringIsNull(ret)) {
            return;
        }
        JSONObject jl = JSONObject.fromObject(ret);
        //1：成功；8：失败
        int success = jl.getInt("success");
        //成功
        if (success == 1 && jl.containsKey("data")) {
            String jlIds = "";
            JSONArray shields = jl.getJSONArray("data");
            for (int i = 0; i < shields.size(); i++) {
                JSONObject shield = shields.getJSONObject(i);
                int jlId = shield.getInt("hotelid");
                jlIds += "'" + jlId + "',";
            }
            if (jlIds.endsWith(",")) {
                jlIds = jlIds.substring(0, jlIds.length() - 1);
                //禁用酒店
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String sql = "update T_HOTEL set C_STATE = 0 , C_LASTUPDATETIME = '" + time
                        + "' where C_SOURCETYPE = 6 and C_HOTELCODE in (" + jlIds + ")";
                Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                System.out.println("=====禁用深捷旅屏蔽酒店=====");
            }
        }
    }
}