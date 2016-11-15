package com.ccservice.b2b2c.atom.service;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.dom4j.DocumentHelper;
import org.jdom.input.SAXBuilder;

import com.ccservice.huamin.WriteLog;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.atom.hotel.JLHotel;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.region.Region;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.base.province.Province;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.b2b2c.base.hmhotelprice.JLOrder;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.hmhotelprice.JLOrderItems;
import com.ccservice.b2b2c.atom.component.SendPostandGet2;
import com.ccservice.b2b2c.base.hmhotelprice.Hmhotelprice;
import com.ccservice.b2b2c.base.hmhotelprice.JLPriceResult;
import com.ccservice.b2b2c.base.hotelgooddata.HotelGoodData;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service.jltour.JlTourHotelJsonData;

public class JLHotelService implements IJLHotelService {

    /********************深捷旅新接口、JSON接口开始********************/

    public void newShieldHotel() {
        new JlTourHotelJsonData().getShieldHotel();
    }

    public void newUpdateHotelInfo(String hotelIds, Map<String, Hotel> hotelMap, int errorflag) throws Exception {
        new JlTourHotelJsonData().getHotelInfo(hotelIds, hotelMap, errorflag);
    }

    public List<JLPriceResult> newUpdateHotelPrice(String type, String hotelIds, String roomIds, String checkInDate,
            String checkOutDate) {
        return new JlTourHotelJsonData().getHotelPrice(type, hotelIds, roomIds, checkInDate, checkOutDate);
    }

    /********************深捷旅新接口、JSON接口结束********************/

    public void getHotelSingle(String hotelcode) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String totalurl = Server.getInstance().getJLUrl() + "?method=getHotelSingle&hotelcode=" + hotelcode;
        System.out.println(totalurl);
        String str = SendPostandGet2.doGet(totalurl, "utf-8");
        SAXBuilder sax = new SAXBuilder();
        Document document;
        try {
            document = sax.build(new StringReader(str));
            Element root = document.getRootElement();
            List<Element> hoteldatas = root.getChildren("hoteldata");
            for (Element hoteldata : hoteldatas) {
                Hotel ht = new Hotel();
                String hotelid = hoteldata.getChildText("hotelid");
                ht.setHotelcode(hotelid);
                String hotelcd = hoteldata.getChildText("hotelcd");
                ht.setHotelcode2(hotelcd);
                String namechn = hoteldata.getChildText("namechn");

                ht.setName(namechn);
                String nameeng = hoteldata.getChildText("nameeng");
                ht.setEnname(nameeng);
                String star = hoteldata.getChildText("star");
                if ("55".equals(star)) {
                    ht.setStar(5);
                }
                else if ("50".equals(star)) {
                    ht.setStar(16);
                }
                else if ("45".equals(star)) {
                    ht.setStar(4);
                }
                else if ("40".equals(star)) {
                    ht.setStar(13);
                }
                else if ("35".equals(star)) {
                    ht.setStar(3);
                }
                else if ("30".equals(star)) {
                    ht.setStar(10);
                }
                else if ("20".equals(star)) {
                    ht.setStar(2);
                }
                else {
                    ht.setStar(0);
                }
                String zone = hoteldata.getChildText("zone");
                String bd = hoteldata.getChildText("bd");
                //行政区
                String where = " where C_TYPE=2 and  C_JLCODE ='" + zone + "'";
                List<Region> region1 = Server.getInstance().getHotelService().findAllRegion(where, "", -1, 0);
                if (region1.size() > 0) {
                    ht.setRegionid1(region1.get(0).getId());
                }
                //商业区
                String wheres = " where C_TYPE=1 and  C_JLCODE ='" + zone + "'";
                List<Region> region2 = Server.getInstance().getHotelService().findAllRegion(wheres, "", -1, 0);
                if (region2.size() > 0) {
                    ht.setRegionid2(region2.get(0).getId());
                }
                //城市
                String cityt = hoteldata.getChildText("city");
                String wherec = " where c_jlcode='" + cityt + "'";
                List<City> cityes = Server.getInstance().getHotelService().findAllCity(wherec, "", -1, 0);
                if (cityes.size() > 0) {
                    ht.setCityid(cityes.get(0).getId());
                }
                //省
                String state = hoteldata.getChildText("state");
                String wherep = " where C_JLCODE='" + state + "'";
                List<Province> province = Server.getInstance().getHotelService().findAllProvince(wherep, "", -1, 0);
                if (province.size() > 0) {
                    ht.setProvinceid(province.get(0).getId());
                }

                String floor = hoteldata.getChildText("floor");
                if (!"null".equals(floor)) {
                    ht.setMainfloor(floor);
                }
                String website = hoteldata.getChildText("website");
                String addresschn = hoteldata.getChildText("addresschn");
                ht.setAddress(addresschn);
                String adresseng = hoteldata.getChildText("adresseng");
                String centraltel = hoteldata.getChildText("centraltel");
                ht.setMarkettell(centraltel);
                String interiornotes = hoteldata.getChildText("interiornotes");
                ht.setAvailPolicy(interiornotes);
                String fax = hoteldata.getChildText("fax");
                ht.setFax1(fax);
                String postcode = hoteldata.getChildText("postcode");
                ht.setPostcode(postcode);
                String email = hoteldata.getChildText("email");
                String language = hoteldata.getChildText("language");
                String themetype = hoteldata.getChildText("themetype");
                String acceptcustom = hoteldata.getChildText("acceptcustom");
                //  ht.setAcceptForeign(acceptcustom);
                String introducechn = hoteldata.getChildText("introducechn");
                String summarychn = hoteldata.getChildText("summarychn");
                ht.setDescription(summarychn);
                String allowcreditcard = hoteldata.getChildText("allowcreditcard");
                String facilities = hoteldata.getChildText("facilities");
                String serviceitem = "";
                //11,12,13,14,15,16,17,18,19,20,22,23,24,25,26,27,21
                if (facilities.contains(",")) {
                    String[] items = facilities.split(",");
                    for (int i = 0; i < items.length; i++) {
                        String ite = items[i];
                        if ("11".equals(ite)) {
                            serviceitem += "停车场,";
                        }
                        else if ("12".equals(ite)) {
                            serviceitem += "会议室,";
                        }
                        else if ("13".equals(ite)) {
                            serviceitem += "游泳池,";
                        }
                        else if ("14".equals(ite)) {
                            serviceitem += "健身房,";
                        }
                        else if ("15".equals(ite)) {
                            serviceitem += "洗衣服务,";
                        }
                        else if ("16".equals(ite)) {
                            serviceitem += "中餐厅,";
                        }
                        else if ("17".equals(ite)) {
                            serviceitem += "西餐厅,";
                        }
                        else if ("18".equals(ite)) {
                            serviceitem += "宴会厅,";
                        }
                        else if ("19".equals(ite)) {
                            serviceitem += "租车服务,";
                        }
                        else if ("20".equals(ite)) {
                            serviceitem += "外币兑换,";
                        }
                        else if ("21".equals(ite)) {
                            serviceitem += "咖啡厅,";
                        }
                        else if ("22".equals(ite)) {
                            serviceitem += "ATM机,";
                        }
                        else if ("23".equals(ite)) {
                            serviceitem += "酒吧,";
                        }
                        else if ("24".equals(ite)) {
                            serviceitem += "叫醒服务,";
                        }
                        else if ("25".equals(ite)) {
                            serviceitem += "网球场,";
                        }
                        else if ("26".equals(ite)) {
                            serviceitem += "歌舞厅,";
                        }
                        else if ("27".equals(ite)) {
                            serviceitem += "美容美发,";
                        }
                        else if ("30".equals(ite)) {
                            serviceitem += "前台贵重物品保险柜,";
                        }
                        else if ("31".equals(ite)) {
                            serviceitem += "送餐服务,";
                        }
                        else if ("32".equals(ite)) {
                            serviceitem += "礼宾司服务,";
                        }
                        else if ("33".equals(ite)) {
                            serviceitem += "商务中心,";
                        }
                        else if ("34".equals(ite)) {
                            serviceitem += "旅游服务,";
                        }
                    }
                    try {
                        serviceitem = serviceitem.substring(0, serviceitem.lastIndexOf(","));
                    }
                    catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
                ht.setServiceitem(serviceitem);
                String facilitiesdisabled = hoteldata.getChildText("facilitiesdisabled");
                String remark = hoteldata.getChildText("remark");
                String keynames = hoteldata.getChildText("keynames");
                String jingdu = hoteldata.getChildText("jingdu");

                if (jingdu != null && !"".equals(jingdu) && !"null".equals(jingdu) && !" ".equals(jingdu)) {
                    try {
                        ht.setLng(Double.parseDouble(jingdu));
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                String weidu = hoteldata.getChildText("weidu");
                if (weidu != null && !"".equals(weidu) && !"null".equals(weidu) && !" ".equals(weidu)) {
                    try {
                        ht.setLat(Double.parseDouble(weidu));
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                String upString = hoteldata.getChildText("upString");
                String active = hoteldata.getChildText("active");
                if (active.equals("1")) {
                    ht.setState(3);
                }
                else {
                    ht.setState(0);
                }
                String outeriornotes = hoteldata.getChildText("outeriornotes");
                String createtime = hoteldata.getChildText("Createtime");
                String pricechange = hoteldata.getChildText("pricechange");
                String begintime = hoteldata.getChildText("begintime");
                String endtime = hoteldata.getChildText("endtime");
                String supplierminor = hoteldata.getChildText("supplierminor");
                String updatetime = hoteldata.getChildText("updatetime");
                ht.setCountryid(168l);
                ht.setSourcetype(6l);
                ht.setType(1);
                ht.setPaytype(2l);
                ht.setLastupdatetime(sdf.format(new Date(System.currentTimeMillis())));
                ht.setElModifytime(updatetime);
                String whereh = " where C_HOTELCODE='" + hotelid + "' and c_sourcetype=6";
                List<Hotel> hotel = Server.getInstance().getHotelService().findAllHotel(whereh, "", -1, 0);
                if (hotel.size() > 0) {
                    ht.setId(hotel.get(0).getId());
                    if (hotel.get(0).getElModifytime() != null && hotel.get(0).getElModifytime().equals(updatetime)) {
                        System.out.println("酒店更新最后时间点相同，不用更新……");
                    }
                    else {
                        if (hotel.get(0).getHcontrol() != null && hotel.get(0).getHcontrol().longValue() == 1) {
                            ht.setState(null);//如果人工关闭，择供应商不在更新该酒店状态
                        }
                        int vv = Server.getInstance().getHotelService().updateHotelIgnoreNull(ht);
                        System.out.println("更新成功");
                    }
                }
                else {
                    //新增
                    try {
                        Server.getInstance().getHotelService().createHotel(ht);
                        System.out.println("新增");
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 正式酒店数据入库
     */
    public void getHotels() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<City> cityes = Server.getInstance().getHotelService()
                .findAllCity("where c_jlcode is not null ", " order by id desc", -1, 0);
        int temp = cityes.size();
        for (City city : cityes) {
            System.out.println("当前剩余城市数量：" + temp--);
            String totalurl = Server.getInstance().getJLUrl() + "?method=getHotel&cityid=" + city.getJlcode();
            System.out.println(totalurl);
            String str = SendPostandGet2.doGet(totalurl, "utf-8");
            SAXBuilder sax = new SAXBuilder();
            Document document;
            try {
                document = sax.build(new StringReader(str));
                Element root = document.getRootElement();
                List<Element> hoteldatas = root.getChildren("hoteldata");
                for (Element hoteldata : hoteldatas) {
                    Hotel ht = new Hotel();
                    String hotelid = hoteldata.getChildText("hotelid");
                    ht.setHotelcode(hotelid);
                    String hotelcd = hoteldata.getChildText("hotelcd");
                    ht.setHotelcode2(hotelcd);
                    String namechn = hoteldata.getChildText("namechn");

                    ht.setName(namechn);
                    String nameeng = hoteldata.getChildText("nameeng");
                    ht.setEnname(nameeng);
                    String star = hoteldata.getChildText("star");
                    if ("55".equals(star)) {
                        ht.setStar(5);
                    }
                    else if ("50".equals(star)) {
                        ht.setStar(16);
                    }
                    else if ("45".equals(star)) {
                        ht.setStar(4);
                    }
                    else if ("40".equals(star)) {
                        ht.setStar(13);
                    }
                    else if ("35".equals(star)) {
                        ht.setStar(3);
                    }
                    else if ("30".equals(star)) {
                        ht.setStar(10);
                    }
                    else if ("20".equals(star)) {
                        ht.setStar(2);
                    }
                    else {
                        ht.setStar(0);
                    }
                    String zone = hoteldata.getChildText("zone");
                    String bd = hoteldata.getChildText("bd");
                    //行政区
                    String where = " where C_TYPE=2 and  C_JLCODE ='" + zone + "'";
                    List<Region> region1 = Server.getInstance().getHotelService().findAllRegion(where, "", -1, 0);
                    if (region1.size() > 0) {
                        ht.setRegionid1(region1.get(0).getId());
                    }
                    //商业区
                    String wheres = " where C_TYPE=1 and  C_JLCODE ='" + zone + "'";
                    List<Region> region2 = Server.getInstance().getHotelService().findAllRegion(wheres, "", -1, 0);
                    if (region2.size() > 0) {
                        ht.setRegionid2(region2.get(0).getId());
                    }
                    //城市
                    String cityt = hoteldata.getChildText("city");
                    String wherec = " where c_jlcode='" + city + "'";
                    if (cityes.size() > 0) {
                        ht.setCityid(city.getId());
                    }
                    //省
                    String state = hoteldata.getChildText("state");
                    String wherep = " where C_JLCODE='" + state + "'";
                    List<Province> province = Server.getInstance().getHotelService().findAllProvince(wherep, "", -1, 0);
                    if (province.size() > 0) {
                        ht.setProvinceid(province.get(0).getId());
                    }

                    String floor = hoteldata.getChildText("floor");
                    if (!"null".equals(floor)) {
                        ht.setMainfloor(floor);
                    }
                    String website = hoteldata.getChildText("website");
                    String addresschn = hoteldata.getChildText("addresschn");
                    ht.setAddress(addresschn);
                    String adresseng = hoteldata.getChildText("adresseng");
                    String centraltel = hoteldata.getChildText("centraltel");
                    ht.setMarkettell(centraltel);
                    String interiornotes = hoteldata.getChildText("interiornotes");
                    ht.setAvailPolicy(interiornotes);
                    String fax = hoteldata.getChildText("fax");
                    ht.setFax1(fax);
                    String postcode = hoteldata.getChildText("postcode");
                    ht.setPostcode(postcode);
                    String email = hoteldata.getChildText("email");
                    String language = hoteldata.getChildText("language");
                    String themetype = hoteldata.getChildText("themetype");
                    String acceptcustom = hoteldata.getChildText("acceptcustom");
                    //  ht.setAcceptForeign(acceptcustom);
                    String introducechn = hoteldata.getChildText("introducechn");
                    String summarychn = hoteldata.getChildText("summarychn");
                    ht.setDescription(summarychn);
                    String allowcreditcard = hoteldata.getChildText("allowcreditcard");
                    String facilities = hoteldata.getChildText("facilities");
                    String serviceitem = "";
                    //11,12,13,14,15,16,17,18,19,20,22,23,24,25,26,27,21
                    if (facilities.contains(",")) {
                        String[] items = facilities.split(",");
                        for (int i = 0; i < items.length; i++) {
                            String ite = items[i];
                            if ("11".equals(ite)) {
                                serviceitem += "停车场,";
                            }
                            else if ("12".equals(ite)) {
                                serviceitem += "会议室,";
                            }
                            else if ("13".equals(ite)) {
                                serviceitem += "游泳池,";
                            }
                            else if ("14".equals(ite)) {
                                serviceitem += "健身房,";
                            }
                            else if ("15".equals(ite)) {
                                serviceitem += "洗衣服务,";
                            }
                            else if ("16".equals(ite)) {
                                serviceitem += "中餐厅,";
                            }
                            else if ("17".equals(ite)) {
                                serviceitem += "西餐厅,";
                            }
                            else if ("18".equals(ite)) {
                                serviceitem += "宴会厅,";
                            }
                            else if ("19".equals(ite)) {
                                serviceitem += "租车服务,";
                            }
                            else if ("20".equals(ite)) {
                                serviceitem += "外币兑换,";
                            }
                            else if ("21".equals(ite)) {
                                serviceitem += "咖啡厅,";
                            }
                            else if ("22".equals(ite)) {
                                serviceitem += "ATM机,";
                            }
                            else if ("23".equals(ite)) {
                                serviceitem += "酒吧,";
                            }
                            else if ("24".equals(ite)) {
                                serviceitem += "叫醒服务,";
                            }
                            else if ("25".equals(ite)) {
                                serviceitem += "网球场,";
                            }
                            else if ("26".equals(ite)) {
                                serviceitem += "歌舞厅,";
                            }
                            else if ("27".equals(ite)) {
                                serviceitem += "美容美发,";
                            }
                            else if ("30".equals(ite)) {
                                serviceitem += "前台贵重物品保险柜,";
                            }
                            else if ("31".equals(ite)) {
                                serviceitem += "送餐服务,";
                            }
                            else if ("32".equals(ite)) {
                                serviceitem += "礼宾司服务,";
                            }
                            else if ("33".equals(ite)) {
                                serviceitem += "商务中心,";
                            }
                            else if ("34".equals(ite)) {
                                serviceitem += "旅游服务,";
                            }
                        }
                        try {
                            serviceitem = serviceitem.substring(0, serviceitem.lastIndexOf(","));
                        }
                        catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                    ht.setServiceitem(serviceitem);
                    String facilitiesdisabled = hoteldata.getChildText("facilitiesdisabled");
                    String remark = hoteldata.getChildText("remark");
                    String keynames = hoteldata.getChildText("keynames");
                    String jingdu = hoteldata.getChildText("jingdu");

                    if (jingdu != null && !"".equals(jingdu) && !"null".equals(jingdu) && !" ".equals(jingdu)) {
                        ht.setLng(Double.parseDouble(jingdu));
                    }

                    String weidu = hoteldata.getChildText("weidu");
                    if (weidu != null && !"".equals(weidu) && !"null".equals(weidu) && !" ".equals(weidu)) {
                        ht.setLat(Double.parseDouble(weidu));
                    }

                    String upString = hoteldata.getChildText("upString");
                    String active = hoteldata.getChildText("active");
                    if (active.equals("1")) {
                        ht.setState(3);
                    }
                    else {
                        ht.setState(0);
                    }
                    String outeriornotes = hoteldata.getChildText("outeriornotes");
                    String createtime = hoteldata.getChildText("Createtime");
                    String pricechange = hoteldata.getChildText("pricechange");
                    String begintime = hoteldata.getChildText("begintime");
                    String endtime = hoteldata.getChildText("endtime");
                    String supplierminor = hoteldata.getChildText("supplierminor");
                    String updatetime = hoteldata.getChildText("updatetime");
                    ht.setCountryid(168l);
                    ht.setSourcetype(6l);
                    ht.setType(1);
                    ht.setPaytype(2l);
                    ht.setLastupdatetime(sdf.format(new Date(System.currentTimeMillis())));
                    ht.setElModifytime(updatetime);
                    String whereh = " where C_HOTELCODE='" + hotelid + "' and c_sourcetype=6";
                    List<Hotel> hotel = Server.getInstance().getHotelService().findAllHotel(whereh, "", -1, 0);
                    if (hotel.size() > 0) {
                        ht.setId(hotel.get(0).getId());
                        if (hotel.get(0).getElModifytime() != null && hotel.get(0).getElModifytime().equals(updatetime)) {
                            System.out.println("酒店更新最后时间点相同，不用更新……");
                        }
                        else {
                            if (hotel.get(0).getHcontrol() != null && hotel.get(0).getHcontrol().longValue() == 1) {
                                ht.setState(null);//如果人工关闭，择供应商不在更新该酒店状态
                            }
                            int vv = Server.getInstance().getHotelService().updateHotelIgnoreNull(ht);
                            System.out.println("更新成功");
                        }
                    }
                    else {
                        //新增
                        try {
                            Server.getInstance().getHotelService().createHotel(ht);
                            System.out.println("新增");
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
            catch (JDOMException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getRoomType(long hotelid, String hotelcode) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String totalurl = Server.getInstance().getJLUrl() + "?method=getRoomType&hotelid=" + hotelcode;
        System.out.println(totalurl);
        String str = SendPostandGet2.doGet(totalurl, "utf-8");
        SAXBuilder sax = new SAXBuilder();
        List<Roomtype> roomtypet = new ArrayList<Roomtype>();
        Document document;
        try {
            document = sax.build(new StringReader(str));
            Element root = document.getRootElement();
            List<Element> roomdatas = root.getChildren("roomtypedata");
            for (Element roomdata : roomdatas) {
                Roomtype rt = new Roomtype();
                rt.setLanguage(0);
                String roomtypeid = roomdata.getChildText("roomtypeid");
                rt.setRoomcode(roomtypeid);
                String namechn = roomdata.getChildText("namechn");
                String hid = roomdata.getChildText("hotelid");
                //String basetype = roomdata.getChildText("basetype");
                String acreages = roomdata.getChildText("acreage");
                if (!"null".equals(acreages)) {
                    rt.setAreadesc(acreages);
                }
                //String roomqty = roomdata.getChildText("roomqty");
                //String bedqty = roomdata.getChildText("bedqty");
                String bedtype = roomdata.getChildText("bedtype");
                if ("single".equals(bedtype)) {
                    rt.setBed(1);
                }
                else if ("big".equals(bedtype) || "bigsing".equals(bedtype)) {
                    rt.setBed(2);
                }
                else if ("bigdou".equals(bedtype)) {
                    rt.setBed(4);
                }
                else if ("double".equals(bedtype) || "sindou".equals(bedtype)) {
                    rt.setBed(3);
                }
                String bedsize = roomdata.getChildText("bedsize");
                if (!"null".equals(bedsize)) {
                    rt.setBedsize(bedsize);
                }
                String allowaddbed = roomdata.getChildText("allowaddbed");
                if (!"null".equals(allowaddbed)) {
                    rt.setAddflag(allowaddbed);
                }
                String allowaddbedqty = roomdata.getChildText("allowaddbedqty");
                if (!"null".equals(allowaddbedqty)) {
                    rt.setAddbednum(allowaddbedqty);
                }
                String allowaddbedsize = roomdata.getChildText("allowaddbedsize");
                if (!"null".equals(allowaddbedsize)) {
                    rt.setAddbedsize(allowaddbedsize);
                }
                String nosm = roomdata.getChildText("nosm");
                if (!"null".equals(nosm)) {
                    rt.setNosm(nosm);
                }
                String floordistribution = roomdata.getChildText("floordistribution");
                if (!"null".equals(floordistribution)) {
                    rt.setLayer(floordistribution);
                }
                String nettype = roomdata.getChildText("nettype");
                //String roomfacilities = roomdata.getChildText("roomfacilities");
                String remark = roomdata.getChildText("remark");
                String remark2 = roomdata.getChildText("remark2");
                String updatetime = roomdata.getChildText("updatetime");
                rt.setModifytime(updatetime);
                rt.setLastupdatetime(sdf.format(new Date(System.currentTimeMillis())));
                if (!"null".equals(remark)) {
                    rt.setRoomset(remark);
                }
                if (!"null".equals(remark2)) {
                    rt.setNote(remark2);
                }
                rt.setWideband(0);
                String active = roomdata.getChildText("active");
                if ("1".equals(active)) {
                    rt.setState(1);
                }
                else {
                    rt.setState(0);
                }
                rt.setName(namechn);
                String wheres = "where c_roomcode='" + roomtypeid + "' and c_hotelid=" + hotelid;
                List<Roomtype> rtype = Server.getInstance().getHotelService().findAllRoomtype(wheres, "", -1, 0);
                if (rtype.size() > 0) {
                    rt.setId(rtype.get(0).getId());
                    if (rtype.get(0).getModifytime() != null && rtype.get(0).getModifytime().equals(updatetime)) {
                        System.out.println("房型更新，最后时间点相同，不用更新……");
                    }
                    else {
                        int rooty = Server.getInstance().getHotelService().updateRoomtypeIgnoreNull(rt);
                        if (rooty > 0) {
                            System.out.println(namechn + "   " + "更新成功");
                        }
                        else {
                            System.out.println("失败");
                        }
                    }
                }
                else {
                    rt.setHotelid(hotelid);
                    Roomtype roomt = Server.getInstance().getHotelService().createRoomtype(rt);
                    if (roomt != null) {
                        System.out.println(roomt.getName() + "保存成功");
                    }
                    else {
                        //System.out.println(hotel.get(0).getName()+"  "+roomt.getName()+"保存失败"); 
                    }
                }
            }

        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void getUpHotel(long time) {
        // TODO Auto-generated method stub
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String totalurl = Server.getInstance().getJLUrl() + "?method=getUpdateHotel&time=" + time;
        System.out.println(totalurl);
        String str = SendPostandGet2.doGet(totalurl, "utf-8");
        SAXBuilder sax = new SAXBuilder();
        List<Hotel> hotels = new ArrayList<Hotel>();
        Document document;
        try {
            document = sax.build(new StringReader(str));
            Element root = document.getRootElement();
            List<Element> hoteldatas = root.getChildren("hoteldata");
            for (Element hoteldata : hoteldatas) {
                Hotel ht = new Hotel();
                String hotelid = hoteldata.getChildText("hotelid");
                ht.setHotelcode(hotelid);
                String hotelcd = hoteldata.getChildText("hotelcd");
                ht.setHotelcode2(hotelcd);
                String namechn = hoteldata.getChildText("namechn");

                ht.setName(namechn);
                String nameeng = hoteldata.getChildText("nameeng");
                ht.setEnname(nameeng);
                String star = hoteldata.getChildText("star");
                if ("55".equals(star)) {
                    ht.setStar(5);
                }
                else if ("50".equals(star)) {
                    ht.setStar(16);
                }
                else if ("45".equals(star)) {
                    ht.setStar(4);
                }
                else if ("40".equals(star)) {
                    ht.setStar(13);
                }
                else if ("35".equals(star)) {
                    ht.setStar(3);
                }
                else if ("30".equals(star)) {
                    ht.setStar(10);
                }
                else if ("20".equals(star)) {
                    ht.setStar(2);
                }
                else {
                    ht.setStar(0);
                }
                String zone = hoteldata.getChildText("zone");
                String bd = hoteldata.getChildText("bd");
                //行政区
                String where = " where C_TYPE=2 and  C_JLCODE ='" + zone + "'";
                List<Region> region1 = Server.getInstance().getHotelService().findAllRegion(where, "", -1, 0);
                if (region1.size() > 0) {
                    ht.setRegionid1(region1.get(0).getId());
                }
                //商业区
                String wheres = " where C_TYPE=1 and  C_JLCODE ='" + zone + "'";
                List<Region> region2 = Server.getInstance().getHotelService().findAllRegion(wheres, "", -1, 0);
                if (region2.size() > 0) {
                    ht.setRegionid2(region2.get(0).getId());
                }
                //城市
                String cityt = hoteldata.getChildText("city");
                List<City> cityes = Server.getInstance().getHotelService()
                        .findAllCity("where c_jlcode='" + cityt + "'", "", -1, 0);
                if (cityes.size() > 0) {
                    ht.setCityid(cityes.get(0).getId());
                }
                //省
                String state = hoteldata.getChildText("state");
                String wherep = " where C_JLCODE='" + state + "'";
                List<Province> province = Server.getInstance().getHotelService().findAllProvince(wherep, "", -1, 0);
                if (province.size() > 0) {
                    ht.setProvinceid(province.get(0).getId());
                }

                String floor = hoteldata.getChildText("floor");
                if (!"null".equals(floor)) {
                    ht.setMainfloor(floor);
                }
                String website = hoteldata.getChildText("website");
                String addresschn = hoteldata.getChildText("addresschn");
                ht.setAddress(addresschn);
                String adresseng = hoteldata.getChildText("adresseng");
                String centraltel = hoteldata.getChildText("centraltel");
                ht.setMarkettell(centraltel);
                String interiornotes = hoteldata.getChildText("interiornotes");
                ht.setAvailPolicy(interiornotes);
                String fax = hoteldata.getChildText("fax");
                ht.setFax1(fax);
                String postcode = hoteldata.getChildText("postcode");
                ht.setPostcode(postcode);
                String email = hoteldata.getChildText("email");
                String language = hoteldata.getChildText("language");
                String themetype = hoteldata.getChildText("themetype");
                String acceptcustom = hoteldata.getChildText("acceptcustom");
                //  ht.setAcceptForeign(acceptcustom);
                String introducechn = hoteldata.getChildText("introducechn");
                String summarychn = hoteldata.getChildText("summarychn");
                ht.setDescription(summarychn);
                String allowcreditcard = hoteldata.getChildText("allowcreditcard");
                String facilities = hoteldata.getChildText("facilities");
                String serviceitem = "";
                //11,12,13,14,15,16,17,18,19,20,22,23,24,25,26,27,21
                if (facilities.contains(",")) {
                    String[] items = facilities.split(",");
                    for (int i = 0; i < items.length; i++) {
                        String ite = items[i];
                        if ("11".equals(ite)) {
                            serviceitem += "停车场,";
                        }
                        else if ("12".equals(ite)) {
                            serviceitem += "会议室,";
                        }
                        else if ("13".equals(ite)) {
                            serviceitem += "游泳池,";
                        }
                        else if ("14".equals(ite)) {
                            serviceitem += "健身房,";
                        }
                        else if ("15".equals(ite)) {
                            serviceitem += "洗衣服务,";
                        }
                        else if ("16".equals(ite)) {
                            serviceitem += "中餐厅,";
                        }
                        else if ("17".equals(ite)) {
                            serviceitem += "西餐厅,";
                        }
                        else if ("18".equals(ite)) {
                            serviceitem += "宴会厅,";
                        }
                        else if ("19".equals(ite)) {
                            serviceitem += "租车服务,";
                        }
                        else if ("20".equals(ite)) {
                            serviceitem += "外币兑换,";
                        }
                        else if ("21".equals(ite)) {
                            serviceitem += "咖啡厅,";
                        }
                        else if ("22".equals(ite)) {
                            serviceitem += "ATM机,";
                        }
                        else if ("23".equals(ite)) {
                            serviceitem += "酒吧,";
                        }
                        else if ("24".equals(ite)) {
                            serviceitem += "叫醒服务,";
                        }
                        else if ("25".equals(ite)) {
                            serviceitem += "网球场,";
                        }
                        else if ("26".equals(ite)) {
                            serviceitem += "歌舞厅,";
                        }
                        else if ("27".equals(ite)) {
                            serviceitem += "美容美发,";
                        }
                        else if ("30".equals(ite)) {
                            serviceitem += "前台贵重物品保险柜,";
                        }
                        else if ("31".equals(ite)) {
                            serviceitem += "送餐服务,";
                        }
                        else if ("32".equals(ite)) {
                            serviceitem += "礼宾司服务,";
                        }
                        else if ("33".equals(ite)) {
                            serviceitem += "商务中心,";
                        }
                        else if ("34".equals(ite)) {
                            serviceitem += "旅游服务,";
                        }
                    }
                    try {
                        serviceitem = serviceitem.substring(0, serviceitem.lastIndexOf(","));
                    }
                    catch (RuntimeException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                ht.setServiceitem(serviceitem);
                String facilitiesdisabled = hoteldata.getChildText("facilitiesdisabled");
                String remark = hoteldata.getChildText("remark");
                String keynames = hoteldata.getChildText("keynames");
                String jingdu = hoteldata.getChildText("jingdu");

                if (jingdu != null && !"".equals(jingdu) && !"null".equals(jingdu) && !" ".equals(jingdu)) {
                    ht.setLng(Double.parseDouble(jingdu));
                }

                String weidu = hoteldata.getChildText("weidu");
                if (weidu != null && !"".equals(weidu) && !"null".equals(weidu) && !" ".equals(weidu)) {
                    ht.setLat(Double.parseDouble(weidu));
                }

                String upString = hoteldata.getChildText("upString");
                String active = hoteldata.getChildText("active");
                if (active.equals("1")) {
                    ht.setState(3);
                }
                else {
                    ht.setState(0);
                }
                String outeriornotes = hoteldata.getChildText("outeriornotes");
                String createtime = hoteldata.getChildText("Createtime");
                String pricechange = hoteldata.getChildText("pricechange");
                String begintime = hoteldata.getChildText("begintime");
                String endtime = hoteldata.getChildText("endtime");
                String supplierminor = hoteldata.getChildText("supplierminor");
                String updatetime = hoteldata.getChildText("updatetime");
                ht.setCountryid(168l);
                ht.setSourcetype(6l);
                ht.setType(1);
                ht.setPaytype(2l);
                ht.setLastupdatetime(sdf.format(new Date(System.currentTimeMillis())));
                ht.setElModifytime(updatetime);
                String whereh = " where C_HOTELCODE='" + hotelid + "' and c_sourcetype=6";
                List<Hotel> hotel = Server.getInstance().getHotelService().findAllHotel(whereh, "", -1, 0);
                if (hotel.size() > 0) {
                    ht.setId(hotel.get(0).getId());
                    if (hotel.get(0).getElModifytime() != null && hotel.get(0).getElModifytime().equals(updatetime)) {
                        System.out.println("酒店更新最后时间点相同，不用更新……");
                    }
                    else {
                        if (hotel.get(0).getHcontrol() != null && hotel.get(0).getHcontrol().longValue() == 1) {
                            ht.setState(null);//如果人工关闭，择供应商不在更新该酒店状态
                        }
                        int vv = Server.getInstance().getHotelService().updateHotelIgnoreNull(ht);
                        System.out.println("更新成功");
                    }
                }
                else {
                    //新增
                    try {
                        Server.getInstance().getHotelService().createHotel(ht);
                        System.out.println("新增");
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
        catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void getUpRoomType(long time) {
        // TODO Auto-generated method stub
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String totalurl = Server.getInstance().getJLUrl() + "?method=getUpRoomType&time=" + time;
        System.out.println(totalurl);
        String str = SendPostandGet2.doGet(totalurl, "utf-8");
        SAXBuilder sax = new SAXBuilder();
        List<Roomtype> roomtype = new ArrayList<Roomtype>();
        Document document;
        try {
            document = sax.build(new StringReader(str));
            Element root = document.getRootElement();
            List<Element> roomdatas = root.getChildren("roomtypedata");
            for (Element roomdata : roomdatas) {
                Roomtype rt = new Roomtype();
                rt.setLanguage(0);
                String roomtypeid = roomdata.getChildText("roomtypeid");
                rt.setRoomcode(roomtypeid);
                String namechn = roomdata.getChildText("namechn");
                String hid = roomdata.getChildText("hotelid");
                if (!"null".equals(hid)) {
                    List<Hotel> hoteles = Server.getInstance().getHotelService()
                            .findAllHotel("where c_sourcetype=6 and  c_hotelcode='" + hid + "'", "", -1, 0);
                    if (hoteles.size() == 1) {
                        //String basetype = roomdata.getChildText("basetype");
                        String acreages = roomdata.getChildText("acreage");
                        if (!"null".equals(acreages)) {
                            rt.setAreadesc(acreages);
                        }
                        //String roomqty = roomdata.getChildText("roomqty");
                        //String bedqty = roomdata.getChildText("bedqty");
                        String bedtype = roomdata.getChildText("bedtype");
                        if ("single".equals(bedtype)) {
                            rt.setBed(1);
                        }
                        else if ("big".equals(bedtype) || "bigsing".equals(bedtype)) {
                            rt.setBed(2);
                        }
                        else if ("bigdou".equals(bedtype)) {
                            rt.setBed(4);
                        }
                        else if ("double".equals(bedtype) || "sindou".equals(bedtype)) {
                            rt.setBed(3);
                        }
                        String bedsize = roomdata.getChildText("bedsize");
                        if (!"null".equals(bedsize)) {
                            rt.setBedsize(bedsize);
                        }
                        String allowaddbed = roomdata.getChildText("allowaddbed");
                        if (!"null".equals(allowaddbed)) {
                            rt.setAddflag(allowaddbed);
                        }
                        String allowaddbedqty = roomdata.getChildText("allowaddbedqty");
                        if (!"null".equals(allowaddbedqty)) {
                            rt.setAddbednum(allowaddbedqty);
                        }
                        String allowaddbedsize = roomdata.getChildText("allowaddbedsize");
                        if (!"null".equals(allowaddbedsize)) {
                            rt.setAddbedsize(allowaddbedsize);
                        }
                        String nosm = roomdata.getChildText("nosm");
                        if (!"null".equals(nosm)) {
                            rt.setNosm(nosm);
                        }
                        String floordistribution = roomdata.getChildText("floordistribution");
                        if (!"null".equals(floordistribution)) {
                            rt.setLayer(floordistribution);
                        }
                        String nettype = roomdata.getChildText("nettype");
                        //String roomfacilities = roomdata.getChildText("roomfacilities");
                        String remark = roomdata.getChildText("remark");
                        String remark2 = roomdata.getChildText("remark2");
                        String updatetime = roomdata.getChildText("updatetime");
                        if (!"null".equals(remark)) {
                            rt.setRoomset(remark);
                        }
                        if (!"null".equals(remark2)) {
                            rt.setNote(remark2);
                        }
                        rt.setWideband(0);
                        String active = roomdata.getChildText("active");
                        if ("1".equals(active)) {
                            rt.setState(1);
                        }
                        else {
                            rt.setState(0);
                        }
                        rt.setName(namechn);
                        rt.setModifytime(updatetime);
                        rt.setLastupdatetime(sdf.format(new Date(System.currentTimeMillis())));
                        String wheres = "where c_roomcode='" + roomtypeid + "'";
                        List<Roomtype> rtype = Server.getInstance().getHotelService()
                                .findAllRoomtype(wheres, "", -1, 0);
                        if (rtype.size() > 0) {
                            rt.setId(rtype.get(0).getId());
                            rt.setHotelid(hoteles.get(0).getId());
                            if (rtype.get(0).getModifytime() != null && rtype.get(0).getModifytime().equals(updatetime)) {
                                System.out.println("房型更新，最后时间点相同，不用更新……");
                            }
                            else {
                                int rooty = Server.getInstance().getHotelService().updateRoomtypeIgnoreNull(rt);
                                if (rooty > 0) {
                                    System.out.println(hoteles.get(0).getName() + "  " + namechn + "   "
                                            + hoteles.get(0).getSourcetype() + "更新成功");
                                }
                                else {
                                    System.out.println("失败");
                                }
                            }
                        }
                        else {
                            rt.setHotelid(hoteles.get(0).getId());
                            roomtype.add(rt);
                            Roomtype roomt = Server.getInstance().getHotelService().createRoomtype(rt);
                            if (roomt != null) {
                                System.out.println(hoteles.get(0).getName() + "  " + roomt.getName() + "保存成功");
                            }
                            else {
                                //System.out.println(hotel.get(0).getName()+"  "+roomt.getName()+"保存失败"); 
                            }
                        }
                    }
                    else if (hoteles.size() == 0) {//审核是否添加酒店问题
                        getHotelSingle(hid);
                        List<Hotel> hoteltes = Server.getInstance().getHotelService()
                                .findAllHotel("where c_sourcetype=6 and  c_hotelcode='" + hid + "'", "", -1, 0);
                        if (hoteles.size() == 1) {
                            getRoomType(hoteles.get(0).getId(), hid);
                        }
                    }
                    else {
                        WriteLog.write("捷旅更新房型问题记录", "供应商酒店id：" + hid + ",该酒店数据库没有，或者酒店数量不为1。");
                    }
                }

            }
        }
        catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void getZone() {
        String totalurl = Server.getInstance().getJLUrl() + "?method=getZone";
        System.out.println(totalurl);
        String str = SendPostandGet2.doGet(totalurl, "utf-8");
        SAXBuilder sax = new SAXBuilder();
        Document document;
        int o = 0;
        try {
            document = sax.build(new StringReader(str));
            Element root = document.getRootElement();
            List<Element> zonedatas = root.getChildren("zonedata");
            for (Element zonedata : zonedatas) {
                String zoneid = zonedata.getChildText("zoneid");
                String cityid = zonedata.getChildText("cityid");
                String name = zonedata.getChildText("name");
                String active = zonedata.getChildText("active");
                List<City> cityes = Server.getInstance().getHotelService()
                        .findAllCity("where C_JLCODE='" + cityid + "'", "", -1, 0);
                if (cityes.size() == 1) {
                    List<Region> regions = Server
                            .getInstance()
                            .getHotelService()
                            .findAllRegion(
                                    "where c_cityid=" + cityes.get(0).getId() + " and (c_name='" + name
                                            + "' or c_name='" + name.replaceAll("区", "县") + "' or c_name='"
                                            + name.replaceAll("区", "县") + "') ", "", -1, 0);
                    if (regions.size() == 1) {
                        regions.get(0).setJlcode(zoneid);
                        Server.getInstance().getHotelService().updateRegionIgnoreNull(regions.get(0));
                        System.out.println("更新区域……");
                    }
                    else {
                        Region region = new Region();
                        region.setName(name);
                        region.setCityid(cityes.get(0).getId());
                        region.setType("2");
                        region.setLanguage(0);
                        region.setCountryid(168l);
                        region.setJlcode(zoneid);
                        Server.getInstance().getHotelService().createRegion(region);
                        System.out.println("创建区域……");
                    }
                }
                else {
                    WriteLog.write("捷旅数据更新", zoneid + ":" + name + ":未找到城市");
                }
            }
        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void getBizZone() {
        String totalurl = Server.getInstance().getJLUrl() + "?method=getBizZone";
        System.out.println(totalurl);
        String str = SendPostandGet2.doGet(totalurl, "utf-8");
        SAXBuilder sax = new SAXBuilder();
        Document document;
        int o = 0;
        try {
            document = sax.build(new StringReader(str));
            Element root = document.getRootElement();
            List<Element> zonedatas = root.getChildren("bizzonedata");
            for (Element zonedata : zonedatas) {
                String bizzoneid = zonedata.getChildText("bizzoneid");
                String cityid = zonedata.getChildText("cityid");
                String zoneid = zonedata.getChildText("zoneid");
                String name = zonedata.getChildText("description");
                String active = zonedata.getChildText("active");
                List<City> cityes = Server.getInstance().getHotelService()
                        .findAllCity("where C_JLCODE='" + cityid + "'", "", -1, 0);
                if (cityes.size() == 1) {
                    List<Region> regions = Server
                            .getInstance()
                            .getHotelService()
                            .findAllRegion("where c_cityid=" + cityes.get(0).getId() + " and c_name='" + name + "'",
                                    "", -1, 0);
                    if (regions.size() == 1) {
                        regions.get(0).setJlcode(zoneid);
                        regions.get(0).setType("1");
                        Server.getInstance().getHotelService().updateRegionIgnoreNull(regions.get(0));
                        System.out.println("更新区域……");
                    }
                    else if (regions.size() > 1) {
                        Server.getInstance()
                                .getSystemService()
                                .findMapResultBySql(
                                        "delete from t_region where c_cityid=" + cityes.get(0).getId()
                                                + " and c_name='" + name + "' and c_jlcode is not null", null);
                        System.out.println("------删除创建区域……");
                        Region region = new Region();
                        region.setName(name);
                        region.setCityid(cityes.get(0).getId());
                        region.setType("1");
                        region.setLanguage(0);
                        region.setCountryid(168l);
                        region.setJlcode(zoneid);
                        Server.getInstance().getHotelService().createRegion(region);
                        System.out.println("创建区域……");
                    }
                    else {
                        Region region = new Region();
                        region.setName(name);
                        region.setCityid(cityes.get(0).getId());
                        region.setType("1");
                        region.setLanguage(0);
                        region.setCountryid(168l);
                        region.setJlcode(zoneid);
                        Server.getInstance().getHotelService().createRegion(region);
                        System.out.println("创建区域……");
                    }
                }
                else {
                    WriteLog.write("捷旅数据更新", zoneid + ":" + name + ":未找到城市");
                }
            }
        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void getCity() {
        String totalurl = Server.getInstance().getJLUrl() + "?method=getCity";
        System.out.println(totalurl);
        String str = SendPostandGet2.doGet(totalurl, "utf-8");
        SAXBuilder sax = new SAXBuilder();
        List<Roomtype> roomtype = new ArrayList<Roomtype>();
        Document document;
        int o = 0;
        try {
            document = sax.build(new StringReader(str));
            Element root = document.getRootElement();
            List<Element> citydatas = root.getChildren("citydata");
            for (Element citydata : citydatas) {
                City c = new City();
                String cityid = citydata.getChildText("cityid");
                String cityname = citydata.getChildText("name");
                String enname = citydata.getChildText("citypin");
                String stateid = citydata.getChildText("stateid");
                cityname = cityname.replace(cityname.charAt(0), ' ').trim();
                String where = " where C_NAME='" + cityname + "'";
                List<City> city = Server.getInstance().getHotelService().findAllCity(where, "", -1, 0);
                if (city.size() > 0) {
                    o++;
                    System.out.println("城市名称：" + city.get(0).getName());
                    c.setId(city.get(0).getId());
                    c.setLanguage(0);
                    c.setJlcode(cityid);
                    c.setType(1l);
                    if (c.getEnname() == null) {
                        c.setEnname(enname);
                    }
                    if (c.getSname() == null) {
                        c.setSname(enname);
                    }
                    if (c.getProvinceid() == null) {
                        List<Province> provinces = Server.getInstance().getHotelService()
                                .findAllProvince(" where C_JLCODE='" + stateid + "'", "", -1, 0);
                        if (provinces.size() == 1) {
                            c.setProvinceid(provinces.get(0).getId());
                        }
                    }
                    Server.getInstance().getHotelService().updateCityIgnoreNull(c);
                    System.out.println("更新-------城市id：" + cityid + "城市名称：" + cityname);
                }
                else {
                    c.setJlcode(cityid);
                    c.setName(cityname);
                    c.setEnname(enname);
                    c.setSname(enname);
                    c.setLanguage(0);
                    c.setType(1l);
                    List<Province> provinces = Server.getInstance().getHotelService()
                            .findAllProvince(" where C_JLCODE='" + stateid + "'", "", -1, 0);
                    if (provinces.size() == 1) {
                        c.setProvinceid(provinces.get(0).getId());
                    }
                    Server.getInstance().getHotelService().createCity(c);
                    System.out.println("添加-------城市id：" + cityid + "城市名称：" + cityname);
                }
            }
            System.out.println(o);
        }
        catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public List<HotelGoodData> getGDHotelPrice(String hotelid, String startDate, String endDate) {
        String totalurl = Server.getInstance().getJLUrl() + "?method=getHotelPrice&hotelid=" + hotelid + "&startDate="
                + startDate + "&endDate=" + endDate;
        System.out.println(totalurl);
        String str = SendPostandGet2.doGet(totalurl, "utf-8");
        SAXBuilder sax = new SAXBuilder();
        List<HotelGoodData> jprs = new ArrayList<HotelGoodData>();
        Document document;
        try {
            document = sax.build(new StringReader(str));
            Element root = document.getRootElement();
            List<Element> prices = root.getChildren("pricedata");
            label: for (Element price : prices) {
                HotelGoodData jpr = new HotelGoodData();
                String keyid = price.getChildText("keyid");
                //String hotelid = price.getChildText("hotelid");
                String hotelcd = price.getChildText("hotelcd");
                String lastupdatepricetime = price.getChildText("lastupdatepricetime");
                String namechn = price.getChildText("namechn");
                String roomtypename = price.getChildText("roomtypename");
                String preeprice = price.getChildText("preeprice");
                if (Double.parseDouble(preeprice) <= 0) {
                    continue label;
                }
                String includebreakfastqty2 = price.getChildText("includebreakfastqty2");

                if (includebreakfastqty2.equals("10")) {
                    includebreakfastqty2 = "0";
                }
                else if (includebreakfastqty2.equals("11") || includebreakfastqty2.equals("12")
                        || includebreakfastqty2.equals("13")) {
                    includebreakfastqty2 = "1";
                }
                else if (includebreakfastqty2.equals("21") || includebreakfastqty2.equals("22")
                        || includebreakfastqty2.equals("23")) {
                    includebreakfastqty2 = "2";
                }
                else if (includebreakfastqty2.equals("31") || includebreakfastqty2.equals("32")
                        || includebreakfastqty2.equals("33")) {
                    includebreakfastqty2 = "3";
                }
                else if ("34".equals(includebreakfastqty2)) {
                    includebreakfastqty2 = "6";//含早
                }

                String night = price.getChildText("night");

                String ratetypename = price.getChildText("ratetypename");
                String allotmenttype = price.getChildText("allotmenttype");
                String ratetype = price.getChildText("ratetype");
                String roomtypeid = price.getChildText("roomtypeid");

                jpr.setRatetype(ratetypename);
                jpr.setRatetypeid(ratetype);
                jpr.setAllotmenttype(allotmenttype);
                jpr.setJlroomtypeid(roomtypeid);

                String roomstatus = price.getChildText("roomstatus");
                String advancedays = price.getChildText("advancedays");
                if (advancedays != null && !"".equals(advancedays) && !"null".equals(advancedays)) {
                    jpr.setBeforeday(Long.parseLong(advancedays));
                }
                else {
                    jpr.setBeforeday(0l);
                }

                String continuousdays = price.getChildText("continuousdays");
                if (continuousdays != null && !"".equals(continuousdays) && !"null".equals(continuousdays)) {
                    jpr.setMinday(Long.parseLong(continuousdays));
                }
                else {
                    jpr.setMinday(0l);
                }
                String netcharge = price.getChildText("netcharge");

                String pricingtype = price.getChildText("pricingtype");
                jpr.setContractid(hotelcd);
                jpr.setJlkeyid(keyid);
                jpr.setJltime(lastupdatepricetime);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String day = null;
                try {
                    day = sdf.format((sdf.parse(night).getTime()));
                }
                catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                jpr.setDatenum(day);

                if ("16".equals(roomstatus)) {
                    jpr.setYuliunum(-1l);
                    jpr.setRoomstatus(1l);
                }
                jpr.setRoomstatus(0l);
                if ((int) (Double.parseDouble(preeprice)) <= 0) {
                    jpr.setRoomstatus(1l);
                }
                String qtyable = price.getChildText("qtyable");
                jpr.setYuliunum(Long.parseLong(qtyable));
                jpr.setHotelname(namechn);
                jpr.setBaseprice((long) (Double.parseDouble(preeprice)));

                jpr.setBfcount(Long.parseLong(includebreakfastqty2));
                String where = " where c_hotelcode='" + hotelid + "' and C_SOURCETYPE=6";
                List<Hotel> hotel = Server.getInstance().getHotelService().findAllHotel(where, "", -1, 0);
                String wheres = "  where C_HOTELID='" + hotel.get(0).getId() + "' and C_ROOMCODE = '" + roomtypeid
                        + "'";
                List<Roomtype> room = Server.getInstance().getHotelService().findAllRoomtype(wheres, "", -1, 0);
                if (hotel.size() > 0) {
                    jpr.setHotelid(hotel.get(0).getId());
                    jpr.setCityid(String.valueOf(hotel.get(0).getCityid()));
                    if (room.size() > 0) {
                        jpr.setRoomtypeid(room.get(0).getId());
                        jpr.setRoomtypename(room.get(0).getName());
                        if (room.get(0).getQunarname() != null && !"".equals(room.get(0).getQunarname())
                                && !"null".equals(room.get(0).getQunarname())) {
                            jpr.setQunarName(room.get(0).getQunarname());
                        }

                        jprs.add(jpr);
                    }
                }
            }
            return jprs;
        }
        catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jprs;

    }

    @Override
    public List<JLPriceResult> getHotelPrice(String hotelid, String startDate, String endDate) {
        // TODO Auto-generated method stub
        String totalurl = Server.getInstance().getJLUrl() + "?method=getHotelPrice&hotelid=" + hotelid + "&startDate="
                + startDate + "&endDate=" + endDate;
        String str = SendPostandGet2.doGet(totalurl, "utf-8");
        SAXBuilder sax = new SAXBuilder();
        List<JLPriceResult> jprs = new ArrayList<JLPriceResult>();
        Document document;
        try {
            document = sax.build(new StringReader(str));
            Element root = document.getRootElement();
            List<Element> prices = root.getChildren("pricedata");
            for (Element price : prices) {
                JLPriceResult jpr = new JLPriceResult();
                String keyid = price.getChildText("keyid");
                String hotelids = price.getChildText("hotelid");
                /*String city = price.getChildText("city");
                String where=" where C_JLCODE='"+city+"'";
                List<City> citys = Server.getInstance().getHotelService().findAllCity(where, "", -1, 0);
                jpr.setCityid(String.valueOf(citys.get(0).getId()));*/
                String hotelcd = price.getChildText("hotelcd");
                String lastupdatepricetime = price.getChildText("lastupdatepricetime");
                String namechn = price.getChildText("namechn");
                String roomtypenamec = price.getChildText("roomtypename");
                String preeprice = price.getChildText("preeprice");
                String includebreakfastqty2 = price.getChildText("includebreakfastqty2");
                String currency = price.getChildText("currency");
                jpr.setCurrency(currency);
                /*if(includebreakfastqty2.equals("10")){
                    includebreakfastqty2="0";
                }else if (includebreakfastqty2.equals("11")||includebreakfastqty2.equals("12")||includebreakfastqty2.equals("13")||includebreakfastqty2.equals("34")) {
                    includebreakfastqty2="1";
                }else if (includebreakfastqty2.equals("21")||includebreakfastqty2.equals("22")||includebreakfastqty2.equals("23")) {
                    includebreakfastqty2="2";
                } else if (includebreakfastqty2.equals("31")||includebreakfastqty2.equals("32")||includebreakfastqty2.equals("33")){
                    includebreakfastqty2="3";
                }*/

                String night = price.getChildText("night");
                String supplierid = price.getChildText("supplierid");
                String ratetypename = price.getChildText("ratetypename");
                String allotmenttype = price.getChildText("allotmenttype");
                String ratetype = price.getChildText("ratetype");
                String roomtypeids = price.getChildText("roomtypeid");
                jpr.setSupplierid(supplierid);
                jpr.setRatetype(ratetypename);
                jpr.setRatetypeid(ratetype);
                jpr.setAllotmenttype(allotmenttype);
                jpr.setJlroomtype(roomtypeids);

                String roomstatus = price.getChildText("roomstatus");
                String advancedays = price.getChildText("advancedays");
                jpr.setLeadTime(advancedays);
                String continuousdays = price.getChildText("continuousdays");
                jpr.setMinDay(continuousdays);
                String netcharge = price.getChildText("netcharge");

                String pricingtype = price.getChildText("pricingtype");
                jpr.setHotelcd(hotelcd);
                jpr.setJlkeyid(keyid);
                jpr.setJltime(lastupdatepricetime);
                jpr.setPricetypr(pricingtype);
                jpr.setNetfee(netcharge);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String day = null;
                try {
                    day = sdf.format((sdf.parse(night).getTime()));
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                jpr.setStayDate(day);
                jpr.setAllot(roomstatus);

                String qtyable = price.getChildText("qtyable");
                jpr.setFangliang(qtyable);
                jpr.setHotelid(hotelids);
                jpr.setHotelName(namechn);
                jpr.setRoomtype(roomtypenamec);
                jpr.setPprice(preeprice);
                jpr.setBreakfast(includebreakfastqty2);

                //取消规则
                String voidabletype = price.getChildText("voidabletype");//int 取消修改条款类型 11：提前天数；12：指定时段；13：即订即保：14：无限制
                String advancedays2 = price.getChildText("advancedays2");//int 提前天数
                String dayselect = price.getChildText("dayselect");//入住前/确认后 多少天
                String timeselect = price.getChildText("timeselect");//入住前/确认后 多少天 几点前
                String noeditorcancel = price.getChildText("noeditorcancel");//11：不可修改；12：不可取消
                String noedit = price.getChildText("noedit");//不可修改内容
                String guaranteeamounttype = price.getChildText("guaranteeamounttype");//担保金额类型>>11：首日：12：全额
                //拼接消规则
                String canceldesc = "";
                if (voidabletype != null && !"".equals(voidabletype.trim())
                        && !"null".equals(voidabletype.trim().toLowerCase())) {
                    int tempvoidabletype = Integer.parseInt(voidabletype);
                    if (tempvoidabletype == 13) {
                        jpr.setVoidabletype("13");
                        canceldesc = "此房即订即保，一但预订不可取消和修改";
                    }
                    else {
                        jpr.setVoidabletype("11");
                        if (dayselect == null || "".equals(dayselect.trim())
                                || "null".equals(dayselect.trim().toLowerCase()) || timeselect == null
                                || "".equals(timeselect.trim()) || "null".equals(timeselect.trim().toLowerCase())) {
                            if (tempvoidabletype == 11) {
                                if (advancedays2 == null || "".equals(advancedays2.trim())
                                        || "null".equals(advancedays2.trim().toLowerCase())) {
                                    jpr.setVoidabletype("13");
                                    canceldesc = "此房即订即保，一但预订不可取消和修改";
                                }
                                else if ("0".equals(advancedays2.trim())) {
                                    canceldesc = "入住当天不可取消和修改";
                                    jpr.setDayselect(advancedays2.trim());
                                    jpr.setTimeselect("0:00");
                                }
                                else {
                                    canceldesc = "入住前" + Integer.parseInt(advancedays2.trim()) + "天不可取消和修改";
                                    jpr.setDayselect(advancedays2.trim());
                                    jpr.setTimeselect("0:00");
                                }
                            }
                            else {
                                jpr.setVoidabletype("13");
                                canceldesc = "此房即订即保，一但预订不可取消和修改";
                            }
                        }
                        else {
                            if ("0".equals(dayselect.trim())) {
                                canceldesc = "入住当天";
                            }
                            else {
                                canceldesc = "入住前" + Integer.parseInt(dayselect.trim()) + "天";
                            }
                            canceldesc += timeselect.trim() + "后";
                            if ("11".equals(noeditorcancel)) {
                                canceldesc += "不可修改，";
                            }
                            else if ("12".equals(noeditorcancel)) {
                                canceldesc += "不可取消，";
                            }
                            else {
                                canceldesc += "不可取消和修改，";
                            }
                            if ("11".equals(guaranteeamounttype)) {
                                canceldesc += "担保金额为首日房费";
                            }
                            else {
                                canceldesc += "担保金额为全额房费";
                            }
                            jpr.setDayselect(dayselect.trim());
                            jpr.setTimeselect(timeselect.trim());
                            jpr.setNoeditorcancel(noeditorcancel);
                            jpr.setNoedit(noedit);
                            jpr.setGuaranteeamounttype(guaranteeamounttype);
                        }
                    }
                }
                jpr.setCanceldesc(canceldesc);

                jprs.add(jpr);
            }
            return jprs;
        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return jprs;
    }

    @Override
    public List<JLPriceResult> getHotelPriceByRoom(String hotelid, String rateid, String roomtypeid, String startDate,
            String endDate) {
        String totalurl = Server.getInstance().getJLUrl() + "?method=getHotelPriceByRoom&hotelid=" + hotelid
                + "&startDate=" + startDate + "&endDate=" + endDate + "&roomtypeid=" + roomtypeid + "&rateid=" + rateid;
        String str = SendPostandGet2.doGet(totalurl, "utf-8");
        SAXBuilder sax = new SAXBuilder();
        List<JLPriceResult> jprs = new ArrayList<JLPriceResult>();
        Document document;
        try {
            document = sax.build(new StringReader(str));
            Element root = document.getRootElement();
            List<Element> prices = root.getChildren("pricedata");
            for (Element price : prices) {
                JLPriceResult jpr = new JLPriceResult();
                String keyid = price.getChildText("keyid");
                String hotelids = price.getChildText("hotelid");
                String hotelcd = price.getChildText("hotelcd");
                String lastupdatepricetime = price.getChildText("lastupdatepricetime");
                String namechn = price.getChildText("namechn");
                String roomtypenamec = price.getChildText("roomtypename");
                String preeprice = price.getChildText("preeprice");
                String includebreakfastqty2 = price.getChildText("includebreakfastqty2");
                if (roomtypeid == null || "".equals(roomtypeid) || "null".equals(roomtypeid)) {
                    if (!ElongHotelInterfaceUtil.StringIsNull(includebreakfastqty2)
                            && !"null".equalsIgnoreCase(includebreakfastqty2.trim())) {
                        if (includebreakfastqty2.equals("10")) {
                            includebreakfastqty2 = "0";
                        }
                        else if (includebreakfastqty2.equals("11") || includebreakfastqty2.equals("12")
                                || includebreakfastqty2.equals("13")) {
                            includebreakfastqty2 = "1";
                        }
                        else if (includebreakfastqty2.equals("21") || includebreakfastqty2.equals("22")
                                || includebreakfastqty2.equals("23")) {
                            includebreakfastqty2 = "2";
                        }
                        else if (includebreakfastqty2.equals("31") || includebreakfastqty2.equals("32")
                                || includebreakfastqty2.equals("33")) {
                            includebreakfastqty2 = "3";
                        }
                        else if ("34".equals(includebreakfastqty2)) {
                            includebreakfastqty2 = "6";//含早
                        }
                    }
                }
                String night = price.getChildText("night");
                String supplierid = price.getChildText("supplierid");
                String ratetypename = price.getChildText("ratetypename");
                String allotmenttype = price.getChildText("allotmenttype");
                String ratetype = price.getChildText("ratetype");
                String roomtypeids = price.getChildText("roomtypeid");
                jpr.setSupplierid(supplierid);
                jpr.setRatetype(ratetypename);
                jpr.setRatetypeid(ratetype);
                jpr.setAllotmenttype(allotmenttype);
                jpr.setJlroomtype(roomtypeids);

                String roomstatus = price.getChildText("roomstatus");
                String advancedays = price.getChildText("advancedays");
                jpr.setLeadTime(advancedays);
                String continuousdays = price.getChildText("continuousdays");
                jpr.setMinDay(continuousdays);
                String netcharge = price.getChildText("netcharge");

                String pricingtype = price.getChildText("pricingtype");
                jpr.setHotelcd(hotelcd);
                jpr.setJlkeyid(keyid);
                jpr.setJltime(lastupdatepricetime);
                jpr.setPricetypr(pricingtype);
                jpr.setNetfee(netcharge);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String day = null;
                try {
                    day = sdf.format((sdf.parse(night).getTime()));
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                jpr.setStayDate(day);
                jpr.setAllot(roomstatus);

                String qtyable = price.getChildText("qtyable");
                jpr.setFangliang(qtyable);
                jpr.setHotelid(hotelids);
                jpr.setHotelName(namechn);
                jpr.setRoomtype(roomtypenamec);
                jpr.setPprice(preeprice);
                jpr.setBreakfast(includebreakfastqty2);

                //取消规则
                String voidabletype = price.getChildText("voidabletype");//int 取消修改条款类型 11：提前天数；12：指定时段；13：即订即保：14：无限制
                String advancedays2 = price.getChildText("advancedays2");//int 提前天数
                String dayselect = price.getChildText("dayselect");//入住前/确认后 多少天
                String timeselect = price.getChildText("timeselect");//入住前/确认后 多少天 几点前
                String noeditorcancel = price.getChildText("noeditorcancel");//11：不可修改；12：不可取消
                String noedit = price.getChildText("noedit");//不可修改内容
                String guaranteeamounttype = price.getChildText("guaranteeamounttype");//担保金额类型>>11：首日：12：全额
                //拼接消规则
                String canceldesc = "";
                if (voidabletype != null && !"".equals(voidabletype.trim())
                        && !"null".equals(voidabletype.trim().toLowerCase())) {
                    int tempvoidabletype = Integer.parseInt(voidabletype);
                    if (tempvoidabletype == 13) {
                        jpr.setVoidabletype("13");
                        canceldesc = "此房即订即保，一但预订不可取消和修改";
                    }
                    else {
                        jpr.setVoidabletype("11");
                        if (dayselect == null || "".equals(dayselect.trim())
                                || "null".equals(dayselect.trim().toLowerCase()) || timeselect == null
                                || "".equals(timeselect.trim()) || "null".equals(timeselect.trim().toLowerCase())) {
                            if (tempvoidabletype == 11) {
                                if (advancedays2 == null || "".equals(advancedays2.trim())
                                        || "null".equals(advancedays2.trim().toLowerCase())) {
                                    jpr.setVoidabletype("13");
                                    canceldesc = "此房即订即保，一但预订不可取消和修改";
                                }
                                else if ("0".equals(advancedays2.trim())) {
                                    canceldesc = "入住当天不可取消和修改";
                                    jpr.setDayselect(advancedays2.trim());
                                    jpr.setTimeselect("0:00");
                                }
                                else {
                                    canceldesc = "入住前" + Integer.parseInt(advancedays2.trim()) + "天不可取消和修改";
                                    jpr.setDayselect(advancedays2.trim());
                                    jpr.setTimeselect("0:00");
                                }
                            }
                            else {
                                jpr.setVoidabletype("13");
                                canceldesc = "此房即订即保，一但预订不可取消和修改";
                            }
                        }
                        else {
                            if ("0".equals(dayselect.trim())) {
                                canceldesc = "入住当天";
                            }
                            else {
                                canceldesc = "入住前" + Integer.parseInt(dayselect.trim()) + "天";
                            }
                            canceldesc += timeselect.trim() + "后";
                            if ("11".equals(noeditorcancel)) {
                                canceldesc += "不可修改，";
                            }
                            else if ("12".equals(noeditorcancel)) {
                                canceldesc += "不可取消，";
                            }
                            else {
                                canceldesc += "不可取消和修改，";
                            }
                            if ("11".equals(guaranteeamounttype)) {
                                canceldesc += "担保金额为首日房费";
                            }
                            else {
                                canceldesc += "担保金额为全额房费";
                            }
                            jpr.setDayselect(dayselect.trim());
                            jpr.setTimeselect(timeselect.trim());
                            jpr.setNoeditorcancel(noeditorcancel);
                            jpr.setNoedit(noedit);
                            jpr.setGuaranteeamounttype(guaranteeamounttype);
                        }
                    }
                }
                jpr.setCanceldesc(canceldesc);

                jprs.add(jpr);
            }
            return jprs;
        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return jprs;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<JLPriceResult> getUpdateHotelPrice(String hotelidd, String startDate, String endDate, long time,
            String citycode) {
        String totalurl = Server.getInstance().getJLUrl() + "?method=getUpdataHotelPrice&startDate=" + startDate
                + "&endDate=" + endDate + "&time=" + time + "&citycode=" + citycode;
        System.out.println(totalurl);
        String str = SendPostandGet2.doGet(totalurl, "utf-8");
        SAXBuilder sax = new SAXBuilder();
        List<JLPriceResult> jprs = new ArrayList<JLPriceResult>();
        Document document;
        try {
            document = sax.build(new StringReader(str));
            Element root = document.getRootElement();
            List<Element> prices = root.getChildren("pricedata");
            for (Element price : prices) {

                JLPriceResult jpr = new JLPriceResult();
                String keyid = price.getChildText("keyid");
                String hotelids = price.getChildText("hotelid");
                /*String city = price.getChildText("city");
                String where=" where C_JLCODE='"+city+"'";
                List<City> citys = Server.getInstance().getHotelService().findAllCity(where, "", -1, 0);
                jpr.setCityid(String.valueOf(citys.get(0).getId()));*/
                String hotelcd = price.getChildText("hotelcd");
                String lastupdatepricetime = price.getChildText("lastupdatepricetime");
                String namechn = price.getChildText("namechn");
                String roomtypenamec = price.getChildText("roomtypename");
                String preeprice = price.getChildText("preeprice");
                String includebreakfastqty2 = price.getChildText("includebreakfastqty2");
                String currency = price.getChildText("currency");
                jpr.setCurrency(currency);
                /*if(includebreakfastqty2.equals("10")){
                    includebreakfastqty2="0";
                }else if (includebreakfastqty2.equals("11")||includebreakfastqty2.equals("12")||includebreakfastqty2.equals("13")||includebreakfastqty2.equals("34")) {
                    includebreakfastqty2="1";
                }else if (includebreakfastqty2.equals("21")||includebreakfastqty2.equals("22")||includebreakfastqty2.equals("23")) {
                    includebreakfastqty2="2";
                } else if (includebreakfastqty2.equals("31")||includebreakfastqty2.equals("32")||includebreakfastqty2.equals("33")){
                    includebreakfastqty2="3";
                }*/

                String night = price.getChildText("night");
                String supplierid = price.getChildText("supplierid");
                String ratetypename = price.getChildText("ratetypename");
                String allotmenttype = price.getChildText("allotmenttype");
                String ratetype = price.getChildText("ratetype");
                String roomtypeids = price.getChildText("roomtypeid");
                jpr.setSupplierid(supplierid);
                jpr.setRatetype(ratetypename);
                jpr.setRatetypeid(ratetype);
                jpr.setAllotmenttype(allotmenttype);
                jpr.setJlroomtype(roomtypeids);

                String roomstatus = price.getChildText("roomstatus");
                String advancedays = price.getChildText("advancedays");
                jpr.setLeadTime(advancedays);
                String continuousdays = price.getChildText("continuousdays");
                jpr.setMinDay(continuousdays);
                String netcharge = price.getChildText("netcharge");

                String pricingtype = price.getChildText("pricingtype");
                jpr.setHotelcd(hotelcd);
                jpr.setJlkeyid(keyid);
                jpr.setJltime(lastupdatepricetime);
                jpr.setPricetypr(pricingtype);
                jpr.setNetfee(netcharge);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String day = null;
                try {
                    day = sdf.format((sdf.parse(night).getTime()));
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                jpr.setStayDate(day);
                jpr.setAllot(roomstatus);

                String qtyable = price.getChildText("qtyable");
                jpr.setFangliang(qtyable);
                jpr.setHotelid(hotelids);
                jpr.setHotelName(namechn);
                jpr.setRoomtype(roomtypenamec);
                jpr.setPprice(preeprice);
                jpr.setBreakfast(includebreakfastqty2);

                //取消规则
                String voidabletype = price.getChildText("voidabletype");//int 取消修改条款类型 11：提前天数；12：指定时段；13：即订即保：14：无限制
                String advancedays2 = price.getChildText("advancedays2");//int 提前天数
                String dayselect = price.getChildText("dayselect");//入住前/确认后 多少天
                String timeselect = price.getChildText("timeselect");//入住前/确认后 多少天 几点前
                String noeditorcancel = price.getChildText("noeditorcancel");//11：不可修改；12：不可取消
                String noedit = price.getChildText("noedit");//不可修改内容
                String guaranteeamounttype = price.getChildText("guaranteeamounttype");//担保金额类型>>11：首日：12：全额
                //拼接消规则
                String canceldesc = "";
                if (voidabletype != null && !"".equals(voidabletype.trim())
                        && !"null".equals(voidabletype.trim().toLowerCase())) {
                    int tempvoidabletype = Integer.parseInt(voidabletype);
                    if (tempvoidabletype == 13) {
                        jpr.setVoidabletype("13");
                        canceldesc = "此房即订即保，一但预订不可取消和修改";
                    }
                    else {
                        jpr.setVoidabletype("11");
                        if (dayselect == null || "".equals(dayselect.trim())
                                || "null".equals(dayselect.trim().toLowerCase()) || timeselect == null
                                || "".equals(timeselect.trim()) || "null".equals(timeselect.trim().toLowerCase())) {
                            if (tempvoidabletype == 11) {
                                if (advancedays2 == null || "".equals(advancedays2.trim())
                                        || "null".equals(advancedays2.trim().toLowerCase())) {
                                    jpr.setVoidabletype("13");
                                    canceldesc = "此房即订即保，一但预订不可取消和修改";
                                }
                                else if ("0".equals(advancedays2.trim())) {
                                    canceldesc = "入住当天不可取消和修改";
                                    jpr.setDayselect(advancedays2.trim());
                                    jpr.setTimeselect("0:00");
                                }
                                else {
                                    canceldesc = "入住前" + Integer.parseInt(advancedays2.trim()) + "天不可取消和修改";
                                    jpr.setDayselect(advancedays2.trim());
                                    jpr.setTimeselect("0:00");
                                }
                            }
                            else {
                                jpr.setVoidabletype("13");
                                canceldesc = "此房即订即保，一但预订不可取消和修改";
                            }
                        }
                        else {
                            if ("0".equals(dayselect.trim())) {
                                canceldesc = "入住当天";
                            }
                            else {
                                canceldesc = "入住前" + Integer.parseInt(dayselect.trim()) + "天";
                            }
                            canceldesc += timeselect.trim() + "后";
                            if ("11".equals(noeditorcancel)) {
                                canceldesc += "不可修改，";
                            }
                            else if ("12".equals(noeditorcancel)) {
                                canceldesc += "不可取消，";
                            }
                            else {
                                canceldesc += "不可取消和修改，";
                            }
                            if ("11".equals(guaranteeamounttype)) {
                                canceldesc += "担保金额为首日房费";
                            }
                            else {
                                canceldesc += "担保金额为全额房费";
                            }
                            jpr.setDayselect(dayselect.trim());
                            jpr.setTimeselect(timeselect.trim());
                            jpr.setNoeditorcancel(noeditorcancel);
                            jpr.setNoedit(noedit);
                            jpr.setGuaranteeamounttype(guaranteeamounttype);
                        }
                    }
                }
                jpr.setCanceldesc(canceldesc);

                jprs.add(jpr);

            }
            return jprs;
        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return jprs;
    }

    @Override
    public List<JLPriceResult> getDelHotel(long time) {
        // TODO Auto-generated method stub
        String totalurl = Server.getInstance().getJLUrl() + "?method=getDelHotelPrice&time=" + time;
        System.out.println(totalurl);
        String str = SendPostandGet2.doGet(totalurl, "utf-8");
        SAXBuilder sax = new SAXBuilder();
        List<JLPriceResult> jprs = new ArrayList<JLPriceResult>();
        Document document;

        try {
            document = sax.build(new StringReader(str));
            Element root = document.getRootElement();
            List<Element> prices = root.getChildren("pricedata");
            for (Element price : prices) {
                JLPriceResult jpr = new JLPriceResult();

                String keyid = price.getChildText("keyid");
                jpr.setJlkeyid(keyid);

                jprs.add(jpr);
            }
            return jprs;
        }
        catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * huc 新增订单
     * 
     * @param order
     */
    public Hotelorder newOrder(JLOrder order) {
        JLHotel jh = new JLHotel();
        StringBuffer sbXml = new StringBuffer();
        sbXml.append("<order>");
        sbXml.append("<customercd>" + PropertyUtil.getValue("jlUserCd") + "</customercd>");
        sbXml.append("<authno>" + PropertyUtil.getValue("jlAuthNo") + "</authno>");
        sbXml.append("<businesstype>Neworder</businesstype>");
        sbXml.append("<hotelid>").append(order.getHotelid()).append("</hotelid>");
        sbXml.append("<hotelcd>").append(order.getHotelcd()).append("</hotelcd>");
        sbXml.append("<hotelname>").append(order.getHotelname()).append("</hotelname>");
        sbXml.append("<roomtypeid>").append(order.getRoomtypeid()).append("</roomtypeid>");
        sbXml.append("<roomtypename>").append(order.getRoomtypename()).append("</roomtypename>");
        sbXml.append("<supplierid>").append(order.getSupplierid()).append("</supplierid>");
        sbXml.append("<pricingtype>").append(order.getPricingtype()).append("</pricingtype>");
        sbXml.append("<allotmenttype>").append(order.getAllotmenttype()).append("</allotmenttype>");
        sbXml.append("<ratetype>").append(order.getRatetype()).append("</ratetype>");
        sbXml.append("<checkindate>").append(order.getCheckindate()).append("</checkindate>");
        sbXml.append("<checkoutdate>").append(order.getCheckoutdate()).append("</checkoutdate>");
        sbXml.append("<days>").append(order.getDays()).append("</days>");

        sbXml.append("<currency>").append(order.getCurrency()).append("</currency>");
        sbXml.append("<roomqty>").append(order.getRoomqty()).append("</roomqty>");
        sbXml.append("<totalamount>").append(order.getTotalamount()).append("</totalamount>");
        sbXml.append("<checkinpersons>").append(order.getCheckinpersons()).append("</checkinpersons>");
        sbXml.append("<settlementtype>").append(order.getSettlementtype()).append("</settlementtype>");
        sbXml.append("<hotelremark>").append(order.getHotelremark()).append("</hotelremark>");
        sbXml.append("<arrivaltime>").append(order.getArrivaltime()).append("</arrivaltime>");
        //sbXml.append("<latestarrivaltime>").append(order.getLatestarrivaltime()).append("</latestarrivaltime>");
        sbXml.append("<latestarrivaltime>23:00</latestarrivaltime>");
        sbXml.append("<arrivaltraffic>").append(order.getArrivaltraffic()).append("</arrivaltraffic>");
        sbXml.append("<flight>").append(order.getFlight() == null ? "" : order.getFlight()).append("</flight>");
        sbXml.append("<customerordercd>").append(order.getCustomerordercd()).append("</customerordercd>");

        sbXml.append("<contact>").append(order.getContact()).append("</contact>");
        sbXml.append("<contacttitle>").append(order.getContacttitle()).append("</contacttitle>");
        sbXml.append("<contactmobile>").append(order.getContactmobile()).append("</contactmobile>");
        sbXml.append("<contacttele>").append(order.getContacttele()).append("</contacttele>");
        sbXml.append("<contactfax>").append(order.getContactfax()).append("</contactfax>");
        sbXml.append("<contactemail>").append(order.getContactemail()).append("</contactemail>");
        sbXml.append("<specialrequest>").append(order.getSpecialrequest()).append("</specialrequest>");

        List<JLOrderItems> orderItems = order.getOrderItems();
        for (JLOrderItems orderItem : orderItems) {
            sbXml.append("<orderitems>");
            sbXml.append("<allotmenttype>").append(orderItem.getAllotmenttype()).append("</allotmenttype>");
            sbXml.append("<ratetype>").append(orderItem.getRatetype()).append("</ratetype>");
            sbXml.append("<night>").append(orderItem.getNight()).append("</night>");
            sbXml.append("<currency>").append(orderItem.getCurrency()).append("</currency>");
            sbXml.append("<preeprice>").append(orderItem.getPreeprice()).append("</preeprice>");
            sbXml.append("<facepaytype></facepaytype>");
            sbXml.append("<faceprice></faceprice>");
            sbXml.append("<includebreakfastqty2>").append(orderItem.getIncludebreakfastqty2())
                    .append("</includebreakfastqty2>");
            sbXml.append("<roomqty>").append(orderItem.getRoomqty()).append("</roomqty>");
            sbXml.append("<totalamount>").append(orderItem.getTotalamount()).append("</totalamount>");
            sbXml.append("<checkinpersons>").append(orderItem.getCheckinpersons()).append("</checkinpersons>");
            sbXml.append("<termall>").append(orderItem.getTermall()).append("</termall>");
            sbXml.append("<specialrequest>").append(orderItem.getSpecialrequest()).append("</specialrequest>");
            sbXml.append("</orderitems>");
        }
        sbXml.append("</order>");
        WriteLog.write("捷旅下单日志", "请求xml:" + sbXml.toString());
        try {
            String resultXML = jh.postMiddleData(sbXml.toString());
            WriteLog.write("捷旅下单日志", "响应xml:" + resultXML.toString());
            org.dom4j.Document document = DocumentHelper.parseText(resultXML);
            org.dom4j.Element root = document.getRootElement();
            String result = root.elementText("result");
            if ("8".equals(result)) {
                String error = root.elementText("error");
                WriteLog.write("捷旅下单日志", "创建订单失败，失败原因:" + error);
            }
            else if ("1".equals(result)) {
                System.out.println("创建订单成功!!!!!");
                Hotelorder ho = new Hotelorder();
                String customercd = root.elementText("customercd");// 客户编号 √   如：SZ2048
                String authno = root.elementText("authno");// 授权码 √ 用于验证消息合法性
                String orderid = root.elementText("orderid");// 捷旅订单 ID
                String ordercd = root.elementText("ordercd");// 捷旅订单编号
                String customerordercd = root.elementText("customerordercd");// 同行订单编号
                String hotelcd = root.elementText("hotelcd");// 酒店编码 √ 捷旅酒店编码
                ho.setWaicode(ordercd);
                ho.setOrderid(customerordercd);
                String where = " where c_hotelcode2='" + hotelcd + "'";
                List<Hotel> hotel = Server.getInstance().getHotelService().findAllHotel(where, "", -1, 0);
                if (hotel.size() > 0) {
                    ho.setHotelid(hotel.get(0).getId());
                }
                String hotelname = root.elementText("hotelname");// 酒店中文名称 √
                ho.setName(hotelname);
                String businesstype = root.elementText("businesstype");// 业务类型 √  neworder

                String roomtypeid = root.elementText("roomtypeid");// 房型 id √  捷旅房型 id
                String wheres = " where C_ROOMCODE='" + roomtypeid + "' and C_HOTELID='" + hotel.get(0).getId() + "'";
                List<Roomtype> roomtype = Server.getInstance().getHotelService().findAllRoomtype(wheres, "", -1, 0);
                if (roomtype.size() > 0) {
                    ho.setRoomid(roomtype.get(0).getId());
                }
                String roomtypename = root.elementText("roomtypename");// 房型名称  √
                ho.setRoomtypename(roomtypename);
                String supplierid = root.elementText("supplierid");// 捷旅供应商 ID √ 来自于捷旅产品库表
                String pricingtype = root.elementText("pricingtype");// 现付/预付  √  11:现付  12:预付
                String allotmenttype = root.elementText("allotmenttype");// 配额类型:  √ 11  配额外  12 普通配额  13 买断配额 14 买断2 15 买断3 16 买断4
                String ratetype = root.elementText("ratetype");// 价格类型 √ 含单早价、含双早价、提前三天价等  编码对应的值由捷旅后台提供对应关系。
                String checkindate = root.elementText("checkindate");// 入住日期  √ 入住日期 不带时间的日期如  2011-11-11
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Timestamp intime = new Timestamp(sdf.parse(checkindate).getTime());
                ho.setPretime(intime);
                ho.setOrdersource(6l);
                String checkoutdate = root.elementText("checkoutdate");// 退房日期 √  退房日期  不带时间的日期如  2011-11-11
                Timestamp outtime = new Timestamp(sdf.parse(checkoutdate).getTime());
                ho.setLeavedate(outtime);
                String currency = root.elementText("currency");// 币种 √ RMB  人民币，目前只接受人民币报价
                String preeprice = root.elementText("preeprice");// 同行价 √  捷旅报价
                String facepaytype = root.elementText("facepaytype");// 现付类型  √ 11  固定售价、14  加高返佣
                String faceprice = root.elementText("faceprice");// 面付价 √  客户前台现付价，如果是加高返佣，  加高可由同行指定。
                String includebreakfastqty2 = root.elementText("includebreakfastqty2");// 含早情况，该数据原来于捷旅价格库 √ 10 不含、11 1份中早、12 1份西早、13 1份自助、21 2份中早、22 2份西早、23 2份自助、31 3份中早、32 3份西早、33 3份自助、34床位早
                //ho.setBfcount(Long.parseLong(includebreakfastqty2));
                String roomqty = root.elementText("roomqty");// 房间数量 √
                ho.setPrerooms(Integer.parseInt(roomqty));
                String totalamount = root.elementText("totalamount");// 订单总金额  √
                ho.setPrice(totalamount);
                String checkinpersons = root.elementText("checkinpersons");// 入住人姓名  √  如：李明，李兵，名字用豆号分隔开
                String settlementtype = root.elementText("settlementtype");// 结算类型  √  month:月结 hmonth:半月结  week:周结 order:单结
                String hotelremark = root.elementText("hotelremark");// 客户备注 客人的特别要求
                String arrivaltime = root.elementText("arrivaltime");// 最上到达时间
                String latestarrivaltime = root.elementText("latestarrivaltime");// 最晚到达时间
                String arrivaltraffic = root.elementText("arrivaltraffic");// 客人到达交通工具
                String flight = root.elementText("flight");// 班次
                // String customerordercd = root.elementText("customerordercd");// 客户订单编号 对方系统的订单编号，用于绑定双方 订单
                String confirmno = root.elementText("confirmno");// 酒店确认号
                String Roomnos = root.elementText("Roomnos");// 酒店房号
                String orderstatus = root.elementText("orderstatus");// 订单状态  11 草稿单  12 待处理  13 待确认   14 已确认  18 已撤单
                //ho.setState(Integer.parseInt(orderstatus));
                try {
                    ho.setOutorderstate(Long.valueOf(orderstatus));
                }
                catch (RuntimeException e) {
                    e.printStackTrace();
                }
                String hotelconfirmfaxsent = root.elementText("hotelconfirmfaxsent");// 已发送酒店
                String hotelconfirmtel = root.elementText("hotelconfirmtel");// 酒店已口头确认
                String customerconfirmed = root.elementText("customerconfirmed");// 已书面确认客户
                String hotelconfirmfax = root.elementText("hotelconfirmfax");// 酒店已书面确认
                String Allotmentready = root.elementText("Allotmentready");// 已满足配额
                String startpay = root.elementText("startpay");// 已即时确认 订单操作步骤。
                String customerconfirmtype = root.elementText("customerconfirmtype");// 客户确认类型 √ 11:短信  12:传真 13:EMAIL  14:EBOOKING
                String contact = root.elementText("contact");// 联系人
                ho.setLinkname(contact);
                String contacttitle = root.elementText("contacttitle");// 联系人称呼
                String contactmobile = root.elementText("contactmobile");// 联系人手机
                ho.setLinkmobile(contactmobile);
                String contacttele = root.elementText("contacttele");// 联系人电话
                String contactfax = root.elementText("contactfax");// 联系人传真
                String contactemail = root.elementText("contactemail");// 联系人EMAIL
                ho.setLinkmail(contactemail);
                String termall = root.elementText("termall");// 条款字符串
                String specialrequest = root.elementText("specialrequest");// 特别要求  是

                System.out.println("客户编号：" + customercd + "，捷旅订单ID：" + orderid + "，捷旅订单编号：" + ordercd);
                return ho;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * huc 取消订单 customercd 客户编号 √ 如：SZ2048 businesstype 业务类型 √ cancelorder
     * ordercd 捷旅订单号 √ customerordercd 客户订单号 √ 来自客户 hotelremark 备注信息 √ 取消原因
     */
    public String cancleOrder(String ordercd, String customerordercd, String hotelremark) {
        String flag = "0,";
        JLHotel jh = new JLHotel();
        try {
            StringBuffer sbXml = new StringBuffer();
            sbXml.append("<order>");
            sbXml.append("<customercd>" + PropertyUtil.getValue("jlUserCd") + "</customercd>");
            sbXml.append("<authno>" + PropertyUtil.getValue("jlAuthNo") + "</authno>");
            sbXml.append("<businesstype>cancelorder</businesstype>");
            sbXml.append("<ordercd>").append(ordercd).append("</ordercd>");
            sbXml.append("<customerordercd>").append(customerordercd).append("</customerordercd>");
            sbXml.append("<hotelremark>").append(hotelremark).append("</hotelremark>");
            sbXml.append("</order>");
            WriteLog.write("捷旅订单日志", "取消订单请求xml:" + sbXml.toString());
            String resultXML = jh.postMiddleData(sbXml.toString());
            WriteLog.write("捷旅订单日志", "取消订单响应xml:" + resultXML);
            org.dom4j.Document document = DocumentHelper.parseText(resultXML);
            org.dom4j.Element root = document.getRootElement();
            String result = root.elementText("result");
            if ("8".equals(result)) {
                String error = root.elementText("error");
                WriteLog.write("捷旅订单日志", "取消订单失败,失败原因:" + error);
                flag = "0/" + error;
            }
            else if ("1".equals(result)) {
                String customercdResponse = root.elementText("customercd");// 客户编号  √  如：SZ2048
                String businesstypeResponse = root.elementText("businesstype");// 业务类型  √  cancelorder
                String ordercdResponse = root.elementText("ordercd");// 捷旅订单编号
                String customerordercdResponse = root.elementText("customerordercd");// 同行订单编号
                System.out.println("客户编号：" + customercdResponse + "，业务类型：" + businesstypeResponse + "，捷旅订单编号："
                        + ordercdResponse + "，同行订单编号：" + customerordercdResponse);
                flag = "1/申请成功";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public void checkInDb() {
        Calendar start = GregorianCalendar.getInstance();

        start.add(Calendar.DAY_OF_MONTH, 0);

        Calendar end = GregorianCalendar.getInstance();

        end.add(Calendar.DAY_OF_MONTH, 8);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String started = sdf.format(start.getTime());

        String ended = sdf.format(end.getTime());

        long time1 = System.currentTimeMillis();

        String where = "  where C_JLCODE is not null ";

        List<City> citys = Server.getInstance().getHotelService().findAllCity(where, "", -1, 0);
        int m = citys.size();
        for (City city : citys) {
            String wheres = " where   c_sourcetype=6 ";

            wheres += " and c_cityid=" + city.getId() + " and C_HMCODE is null ";

            List<Hotel> hotels = Server.getInstance().getHotelService().findAllHotel(wheres, "order by id", -1, 0);

            int k = hotels.size();
            m--;

            for (int j = 0; j < hotels.size(); j++) {
                System.out.println("城市数量：" + (m));
                System.out.println("城市名称：" + city.getName());
                System.out.println("酒店数量：" + (k--));
                long time3 = System.currentTimeMillis();

                System.out.println("酒店名称：" + hotels.get(j).getName());

                List<JLPriceResult> result = new JLHotelService().getHotelPrice(hotels.get(j).getHotelcode(), started,
                        ended);

                for (JLPriceResult priceResult : result) {
                    Hmhotelprice hmp = new Hmhotelprice();
                    if (priceResult.getLeadTime() != null && !"".equals(priceResult.getLeadTime())
                            && !"null".equals(priceResult.getLeadTime())) {
                        hmp.setAdvancedday(Long.parseLong(priceResult.getLeadTime()));
                    }
                    String includebreakfastqty2 = priceResult.getBreakfast();
                    //hmp.setJlbf(priceResult.getBreakfast());
                    if (includebreakfastqty2.equals("10")) {
                        includebreakfastqty2 = "0";
                    }
                    else if (includebreakfastqty2.equals("11") || includebreakfastqty2.equals("12")
                            || includebreakfastqty2.equals("13")) {
                        includebreakfastqty2 = "1";
                    }
                    else if (includebreakfastqty2.equals("21") || includebreakfastqty2.equals("22")
                            || includebreakfastqty2.equals("23")) {
                        includebreakfastqty2 = "2";
                    }
                    else if (includebreakfastqty2.equals("31") || includebreakfastqty2.equals("32")
                            || includebreakfastqty2.equals("33")) {
                        includebreakfastqty2 = "3";
                    }
                    else if ("34".equals(includebreakfastqty2)) {
                        includebreakfastqty2 = "6";//含早
                    }
                    hmp.setBf(Long.parseLong(includebreakfastqty2));

                    hmp.setAllotmenttype(priceResult.getAllotmenttype());

                    hmp.setCityid(String.valueOf(hotels.get(j).getCityid()));

                    hmp.setHotelid(hotels.get(j).getId());

                    //hmp.setJlf(priceResult.getFangliang());

                    //hmp.setJlft(priceResult.getAllot());

                    hmp.setJlkeyid(priceResult.getJlkeyid());

                    //hmp.setJlroomtypeid(priceResult.getJlroomtype());

                    hmp.setJltime(priceResult.getJltime());

                    if (priceResult.getMinDay() != null && !"".equals(priceResult.getMinDay())
                            && !"null".equals(priceResult.getMinDay())) {
                        hmp.setMinday(Long.parseLong(priceResult.getMinDay()));
                    }

                    hmp.setPrice(Double.parseDouble(priceResult.getPprice()));

                    hmp.setRatetype(priceResult.getRatetype());

                    //hmp.setRatetypeid(priceResult.getRatetypeid());

                    String wherex = " where C_ROOMCODE='" + priceResult.getJlroomtype() + "'";
                    List<Roomtype> rts = Server.getInstance().getHotelService().findAllRoomtype(wherex, "", -1, 0);
                    if (rts.size() > 0) {
                        hmp.setRoomtypeid(rts.get(0).getId());

                        SimpleDateFormat sdfx = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String now = sdfx.format(System.currentTimeMillis());
                        hmp.setUpdatetime(now);

                        hmp.setStatedate(priceResult.getStayDate());

                        hmp.setContractid(priceResult.getHotelcd());

                        //hmp.setCountryid("168");

                        //hmp.setCountryname("中国");

                        try {
                            Hmhotelprice hp = Server.getInstance().getHotelService().createHmhotelprice(hmp);
                            if (hp != null) {
                                System.out.println("插入成功");
                            }
                            else {
                                System.out.println("插入失败");
                            }
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    long time4 = System.currentTimeMillis();

                    System.out.println("用时：" + (time4 - time3) / 1000 + "s");
                }
            }
        }
        long time2 = System.currentTimeMillis();
        System.out.println("用时：" + (time2 - time1) / 1000 / 60);
    }

}
