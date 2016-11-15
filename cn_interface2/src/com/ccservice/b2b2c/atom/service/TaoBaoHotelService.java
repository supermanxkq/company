package com.ccservice.b2b2c.atom.service;

import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.taobao.api.*;
import com.taobao.api.request.*;
import com.taobao.api.response.*;
import com.ccservice.huamin.WriteLog;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.atom.hotel.TaobaoUtil;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.b2b2c.base.hmhotelprice.Hmhotelprice;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class TaoBaoHotelService implements ITaoBaoHotelSerice {

    public String hotelAdd(Hotel hotel, String imgpath, Long taobaoProvinceId, Long taobaoCityId, Long taobaoRegionId,
            String sessionkey) throws Exception {
        if (hotel == null || ElongHotelInterfaceUtil.StringIsNull(hotel.getName())) {
            throw new Exception("酒店名称为空!");
        }
        if (hotel.getType() == null || hotel.getType().intValue() != 1 || hotel.getSourcetype() == null
                || hotel.getSourcetype().longValue() != 6) {
            throw new Exception("酒店信息不全!");
        }
        if (taobaoProvinceId == null || taobaoProvinceId.longValue() <= 0 || taobaoCityId == null
                || taobaoCityId.longValue() <= 0) {
            throw new Exception("酒店淘宝省份或城市ID为空!");
        }
        //Req
        HotelAddRequest req = new HotelAddRequest();
        //封装
        req.setName(hotel.getName().trim());
        req.setDomestic(true);
        req.setCountry("China");
        req.setProvince(taobaoProvinceId);
        req.setCity(taobaoCityId);
        req.setDistrict(taobaoRegionId == null || taobaoRegionId.longValue() <= 0 ? 0l : taobaoRegionId.longValue());
        req.setAddress(ElongHotelInterfaceUtil.StringIsNull(hotel.getAddress())
                || hotel.getAddress().trim().length() > 60 ? "--" : hotel.getAddress().trim());
        req.setLevel(getLevel(hotel.getStar()));
        req.setOrientation("B");//酒店定位 可选值：T、B 代表旅游度假、商务出行
        req.setDesc(ElongHotelInterfaceUtil.StringIsNull(hotel.getDescription()) ? hotel.getName().trim() : hotel
                .getDescription().trim());
        req.setPic(TaobaoUtil.getFileItem(imgpath));
        req.setSiteParam(String.valueOf(hotel.getId()));//接入卖家数据主键
        //请求
        TaobaoClient client = TaobaoUtil.getClient();
        HotelAddResponse res = (HotelAddResponse) TaobaoUtil.getResponse(client, req, sessionkey);
        return res.getBody();
    }

    public String hotelGet(Long taobaoHotelId, boolean NeedRoom, boolean NeedHotelCheckStatus, String sessionkey)
            throws Exception {
        if (taobaoHotelId == null || taobaoHotelId.longValue() <= 0) {
            throw new Exception("淘宝酒店ID错误!");
        }
        HotelGetRequest req = new HotelGetRequest();
        req.setHid(taobaoHotelId.longValue());
        req.setNeedRoomType(NeedRoom);
        req.setCheckAudit(NeedHotelCheckStatus);
        TaobaoClient client = TaobaoUtil.getClient();
        HotelGetResponse res = (HotelGetResponse) TaobaoUtil.getResponse(client, req, sessionkey);
        return res.getBody();
    }

    public String hotelUpdate(Long taobaoHotelId, Hotel hotel, String imgpath, Long taobaoProvinceId,
            Long taobaoCityId, Long taobaoRegionId, String sessionkey) throws Exception {
        if (taobaoHotelId == null || taobaoHotelId.longValue() <= 0) {
            throw new Exception("淘宝酒店ID错误!");
        }
        if (hotel == null || ElongHotelInterfaceUtil.StringIsNull(hotel.getName())) {
            throw new Exception("酒店名称为空!");
        }
        if (hotel.getType() == null || hotel.getType().intValue() != 1 || hotel.getSourcetype() == null
                || hotel.getSourcetype().longValue() != 6) {
            throw new Exception("酒店信息不全!");
        }
        if (taobaoProvinceId == null || taobaoProvinceId.longValue() <= 0 || taobaoCityId == null
                || taobaoCityId.longValue() <= 0) {
            throw new Exception("酒店淘宝省份或城市ID为空!");
        }
        HotelUpdateRequest req = new HotelUpdateRequest();
        req.setHid(taobaoHotelId);
        req.setName(hotel.getName().trim());
        req.setProvince(taobaoProvinceId);
        req.setCity(taobaoCityId);
        req.setDistrict(taobaoRegionId == null || taobaoRegionId.longValue() <= 0 ? 0l : taobaoRegionId.longValue());
        req.setAddress(ElongHotelInterfaceUtil.StringIsNull(hotel.getAddress())
                || hotel.getAddress().trim().length() > 60 ? "--" : hotel.getAddress().trim());
        req.setLevel(getLevel(hotel.getStar()));
        req.setDesc(ElongHotelInterfaceUtil.StringIsNull(hotel.getDescription()) ? hotel.getName().trim() : hotel
                .getDescription().trim());
        req.setPic(TaobaoUtil.getFileItem(imgpath));
        TaobaoClient client = TaobaoUtil.getClient();
        HotelUpdateResponse res = (HotelUpdateResponse) TaobaoUtil.getResponse(client, req, sessionkey);
        return res.getBody();
    }

    public String roomAdd(String roomName, Long taobaoHotelId, String sessionkey) throws Exception {
        if (taobaoHotelId == null || taobaoHotelId.longValue() <= 0) {
            throw new Exception("淘宝酒店ID错误!");
        }
        if (ElongHotelInterfaceUtil.StringIsNull(roomName)) {
            throw new Exception("房型名称为空!");
        }
        HotelTypeAddRequest req = new HotelTypeAddRequest();
        req.setName(roomName.trim());
        req.setHid(taobaoHotelId.longValue());
        TaobaoClient client = TaobaoUtil.getClient();
        HotelTypeAddResponse res = (HotelTypeAddResponse) TaobaoUtil.getResponse(client, req, sessionkey);
        return res.getBody();
    }

    public String productAdd(Long taobaoHotelId, Long taobaoRoomId, Roomtype roomtype, String imgpath,
            List<Hmhotelprice> prices, String sessionkey) throws Exception {
        if (taobaoHotelId == null || taobaoHotelId.longValue() <= 0) {
            throw new Exception("淘宝酒店ID错误!");
        }
        if (taobaoRoomId == null || taobaoRoomId.longValue() <= 0) {
            throw new Exception("淘宝房型ID错误!");
        }
        if (roomtype == null || ElongHotelInterfaceUtil.StringIsNull(roomtype.getName())) {
            throw new Exception("房型名称为空!");
        }
        if (roomtype.getHotelid() == null || roomtype.getHotelid().longValue() <= 0) {
            throw new Exception("房型酒店ID为空!");
        }
        if (prices == null || prices.size() <= 0) {
            throw new Exception("商品价格为空!");
        }
        HotelRoomAddRequest req = new HotelRoomAddRequest();
        req.setHid(taobaoHotelId.longValue());
        req.setRid(taobaoRoomId.longValue());
        req.setTitle(roomtype.getName().trim());
        req.setBedType(getBed(roomtype.getBed()));
        req.setStorey(ElongHotelInterfaceUtil.StringIsNull(roomtype.getLayer())
                || roomtype.getLayer().trim().length() > 8 ? "" : roomtype.getLayer().trim());
        req.setBreakfast(getBf(prices.get(0).getBf()));
        req.setBbn(getWeb(roomtype.getWidedesc(), roomtype.getWideband()));
        req.setPaymentType("A");//全额支付
        req.setGuide("1、标有实价有房的酒店宝贝价格准确，可直接预订，未标有实价有房的酒店宝贝价格可供参考，预订以客服报价为准；"
                + "2、入住时间一般为14：00，退房时间为12：00，个别酒店例外，例如入住15：00，退房11：00；凭身份证、护照办理入住；酒店不接受未满18岁人士单独入住；"
                + "3、全额预付房费的酒店，订单一经确认，买家保证全段入住；任何主、客观原因导致无法抵达或不住酒店，买家均保证支付全额房款；"
                + "4、入住有问题，须及时联系客服协调；不联系客服擅自离去，房间空置房费照收。");
        req.setPic(TaobaoUtil.getFileItem(imgpath));
        String ratetypename = prices.get(0).getRatetype();
        if (!ElongHotelInterfaceUtil.StringIsNull(ratetypename)) {
            ratetypename = ratetypename.trim();
        }
        String desc = ElongHotelInterfaceUtil.StringIsNull(roomtype.getRoomdesc()) ? roomtype.getName() : roomtype
                .getRoomdesc().trim();
        if (ratetypename.contains("提前") || ratetypename.contains("连住")) {
            desc += " [ " + ratetypename.trim() + " ] ";
        }
        req.setDesc(desc);
        req.setReceiptType("B");//发票类型   A:酒店住宿发票,B:其他
        req.setReceiptOtherTypeDesc("旅行社发票");
        req.setHasReceipt(true);
        //房价信息
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start = null;//价格开始
        Date end = null;//价格结束
        Date today = sdf.parse(sdf.format(new Date()));//今天
        Map<String, Boolean> prods = new HashMap<String, Boolean>();//套餐数，淘宝暂不支持多套餐
        JSONArray RoomQuotas = new JSONArray();
        for (Hmhotelprice p : prices) {
            if (p.getHotelid() == null || p.getHotelid().longValue() != roomtype.getHotelid().longValue()) {
                break;
            }
            if (p.getRoomtypeid() == null || p.getRoomtypeid().longValue() != roomtype.getId()) {
                break;
            }
            if (ElongHotelInterfaceUtil.StringIsNull(p.getStatedate())) {
                break;
            }
            if (p.getPrice() == null || p.getPrice().doubleValue() <= 0) {
                break;
            }
            if (ElongHotelInterfaceUtil.StringIsNull(p.getProd())) {
                break;
            }
            Date currentdate = sdf.parse(p.getStatedate());
            //今天之前，略过
            if (currentdate.before(today)) {
                continue;
            }
            if (start == null || start.after(currentdate)) {
                start = currentdate;
            }
            if (end == null || end.before(currentdate)) {
                end = currentdate;
            }
            JSONObject obj = new JSONObject();
            obj.put("date", sdf.format(currentdate));
            obj.put("price", ElongHotelInterfaceUtil.multiply(p.getPrice().doubleValue(), 100));//存储的单位是分
            obj.put("num", getRoomNum(p));
            RoomQuotas.add(obj.toString());
            prods.put(p.getProd().trim(), true);
        }
        WriteLog.write("淘宝发布商品", sessionkey + "=====" + req.getTitle() + "=====" + RoomQuotas.toString());
        if (prods.size() != 1) {
            throw new Exception("商品价格错误!");
        }
        if (RoomQuotas.size() == 0) {
            throw new Exception("商品价格为空!");
        }
        else {
            int days = ElongHotelInterfaceUtil.getSubDays(sdf.format(start), sdf.format(end)) + 1;
            if (RoomQuotas.size() < days) {
                throw new Exception("商品价格不连续!");
            }
            if (RoomQuotas.size() > days) {
                throw new Exception("商品价格错误!");
            }
        }
        req.setRoomQuotas(RoomQuotas.toString());
        //退订规则  -- 全额支付类型必填
        JSONObject json = new JSONObject();
        json.put("t", 2);//t代表类别 1表示任意退;2表示不能退;3表示阶梯退
        req.setRefundPolicyInfo(json.toString());
        TaobaoClient client = TaobaoUtil.getClient();
        HotelRoomAddResponse res = (HotelRoomAddResponse) TaobaoUtil.getResponse(client, req, sessionkey);
        return res.getBody();
    }

    public String productsGet(String taobaoHotelIds, String taobaoRoomIds, String taobaoProdIds, long pageno,
            boolean needhotel, boolean needroom, String sessionkey) throws Exception {
        if (ElongHotelInterfaceUtil.StringIsNull(taobaoHotelIds) && ElongHotelInterfaceUtil.StringIsNull(taobaoRoomIds)
                && ElongHotelInterfaceUtil.StringIsNull(taobaoProdIds)) {
            throw new Exception("淘宝酒店、房型、商品ID至少传一项!");
        }
        if (!ElongHotelInterfaceUtil.StringIsNull(taobaoHotelIds) && taobaoHotelIds.split(",").length > 5) {
            throw new Exception("淘宝酒店ID一次不能超过5个!");
        }
        if (ElongHotelInterfaceUtil.StringIsNull(taobaoRoomIds) && taobaoRoomIds.split(",").length > 20) {
            throw new Exception("淘宝房型ID一次不能超过20个!");
        }
        if (ElongHotelInterfaceUtil.StringIsNull(taobaoProdIds) && taobaoProdIds.split(",").length > 20) {
            throw new Exception("淘宝商品ID一次不能超过20个!");
        }
        HotelRoomsSearchRequest req = new HotelRoomsSearchRequest();
        if (!ElongHotelInterfaceUtil.StringIsNull(taobaoHotelIds)) {
            req.setHids(taobaoHotelIds);
        }
        if (!ElongHotelInterfaceUtil.StringIsNull(taobaoRoomIds)) {
            req.setRids(taobaoRoomIds);
        }
        if (!ElongHotelInterfaceUtil.StringIsNull(taobaoProdIds)) {
            req.setGids(taobaoProdIds);
        }
        req.setPageNo(pageno <= 0 ? 1 : pageno);
        req.setNeedHotel(needhotel);
        req.setNeedRoomType(needroom);
        TaobaoClient client = TaobaoUtil.getClient();
        HotelRoomsSearchResponse res = (HotelRoomsSearchResponse) TaobaoUtil.getResponse(client, req, sessionkey);
        return res.getBody();
    }

    public String productUpdate(Long taobaoProdId, Roomtype roomtype, List<Hmhotelprice> prices, Long status,
            String sessionkey) throws Exception {
        if (taobaoProdId == null || taobaoProdId.longValue() <= 0) {
            throw new Exception("淘宝商品ID错误!");
        }
        int flag = 0;
        if (roomtype != null && !ElongHotelInterfaceUtil.StringIsNull(roomtype.getName())) {
            flag++;
        }
        if (prices != null && prices.size() > 0) {
            flag++;
        }
        if ((flag == 0 && status == null) || flag == 1) {//同时为空、房型价格只传一种
            throw new Exception("请求参数错误!");
        }
        HotelRoomUpdateRequest req = new HotelRoomUpdateRequest();
        req.setGid(taobaoProdId);
        //房型、房价信息
        if (flag == 2) {
            req.setTitle(roomtype.getName().trim());
            req.setBedType(getBed(roomtype.getBed()));
            req.setStorey(ElongHotelInterfaceUtil.StringIsNull(roomtype.getLayer())
                    || roomtype.getLayer().trim().length() > 8 ? "" : roomtype.getLayer().trim());
            req.setBreakfast(getBf(prices.get(0).getBf()));
            req.setBbn(getWeb(roomtype.getWidedesc(), roomtype.getWideband()));
            //套餐
            String ratetypename = prices.get(0).getRatetype();
            if (!ElongHotelInterfaceUtil.StringIsNull(ratetypename)) {
                ratetypename = ratetypename.trim();
            }
            String desc = ElongHotelInterfaceUtil.StringIsNull(roomtype.getRoomdesc()) ? roomtype.getName() : roomtype
                    .getRoomdesc().trim();
            if (ratetypename.contains("提前") || ratetypename.contains("连住")) {
                desc += " [ " + ratetypename.trim() + " ] ";
            }
            req.setDesc(desc);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = null;//价格开始
            Date end = null;//价格结束
            Date today = sdf.parse(sdf.format(new Date()));//今天
            Map<String, Boolean> prods = new HashMap<String, Boolean>();//套餐数，淘宝暂不支持多套餐
            JSONArray RoomQuotas = new JSONArray();
            for (Hmhotelprice p : prices) {
                if (p.getHotelid() == null || p.getHotelid().longValue() != roomtype.getHotelid().longValue()) {
                    break;
                }
                if (p.getRoomtypeid() == null || p.getRoomtypeid().longValue() != roomtype.getId()) {
                    break;
                }
                if (ElongHotelInterfaceUtil.StringIsNull(p.getStatedate())) {
                    break;
                }
                if (p.getPrice() == null || p.getPrice().doubleValue() <= 0) {
                    break;
                }
                if (ElongHotelInterfaceUtil.StringIsNull(p.getProd())) {
                    break;
                }
                Date currentdate = sdf.parse(p.getStatedate());
                //今天之前，略过
                if (currentdate.before(today)) {
                    continue;
                }
                if (start == null || start.after(currentdate)) {
                    start = currentdate;
                }
                if (end == null || end.before(currentdate)) {
                    end = currentdate;
                }
                JSONObject obj = new JSONObject();
                obj.put("date", sdf.format(currentdate));
                obj.put("price", ElongHotelInterfaceUtil.multiply(p.getPrice().doubleValue(), 100));//存储的单位是分
                obj.put("num", getRoomNum(p));
                RoomQuotas.add(obj.toString());
                prods.put(p.getProd().trim(), true);
            }
            if (prods.size() != 1) {
                throw new Exception("商品价格错误!");
            }
            if (RoomQuotas.size() == 0) {
                throw new Exception("商品价格为空!");
            }
            else {
                int days = ElongHotelInterfaceUtil.getSubDays(sdf.format(start), sdf.format(end)) + 1;
                if (RoomQuotas.size() < days) {
                    throw new Exception("商品价格不连续!");
                }
                if (RoomQuotas.size() > days) {
                    throw new Exception("商品价格错误!");
                }
            }
            req.setRoomQuotas(RoomQuotas.toString());
        }
        //商品状态 1：上架；2：下架；3：删除
        if (status != null) {
            if (status.longValue() == 1 || status.longValue() == 2 || status.longValue() == 3) {
                req.setStatus(status);
            }
            else {
                throw new Exception("商品状态错误!");
            }
        }
        TaobaoClient client = TaobaoUtil.getClient();
        HotelRoomUpdateResponse res = (HotelRoomUpdateResponse) TaobaoUtil.getResponse(client, req, sessionkey);
        return res.getBody();
    }

    public String productBatchUpdate(Map<Long, List<Hmhotelprice>> prodPrices, String sessionkey) throws Exception {
        if (prodPrices == null || prodPrices.size() == 0) {
            throw new Exception("商品价格为空!");
        }
        JSONArray json = new JSONArray();
        out: for (Long key : prodPrices.keySet()) {
            if (key == null || key.longValue() <= 0) {
                continue;
            }
            List<Hmhotelprice> prices = prodPrices.get(key);
            if (prices == null || prices.size() == 0) {
                continue;
            }
            JSONObject obj = new JSONObject();
            obj.put("gid", key.longValue());
            //价格信息
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = null;//价格开始
            Date end = null;//价格结束
            Date today = sdf.parse(sdf.format(new Date()));//今天
            Map<String, Boolean> prods = new HashMap<String, Boolean>();//套餐数，淘宝暂不支持多套餐
            JSONArray RoomQuotas = new JSONArray();
            for (Hmhotelprice p : prices) {
                if (p.getHotelid() == null || p.getHotelid().longValue() <= 0) {
                    continue out;
                }
                if (p.getRoomtypeid() == null || p.getRoomtypeid().longValue() <= 0) {
                    continue out;
                }
                if (ElongHotelInterfaceUtil.StringIsNull(p.getStatedate())) {
                    continue out;
                }
                if (p.getPrice() == null || p.getPrice().doubleValue() <= 0) {
                    continue out;
                }
                if (ElongHotelInterfaceUtil.StringIsNull(p.getProd())) {
                    continue out;
                }
                Date currentdate = sdf.parse(p.getStatedate());
                //今天之前，略过
                if (currentdate.before(today)) {
                    continue;
                }
                if (start == null || start.after(currentdate)) {
                    start = currentdate;
                }
                if (end == null || end.before(currentdate)) {
                    end = currentdate;
                }
                JSONObject price = new JSONObject();
                price.put("date", sdf.format(currentdate));
                price.put("price", ElongHotelInterfaceUtil.multiply(p.getPrice().doubleValue(), 100));//存储的单位是分
                price.put("num", getRoomNum(p));
                RoomQuotas.add(price.toString());
                prods.put(p.getProd().trim(), true);
            }
            if (prods.size() != 1) {
                continue out;
            }
            if (RoomQuotas.size() == 0) {
                continue out;
            }
            else {
                int days = ElongHotelInterfaceUtil.getSubDays(sdf.format(start), sdf.format(end));
                if ((days + 1) != RoomQuotas.size()) {
                    continue out;
                }
            }
            obj.put("roomQuota", RoomQuotas.toString());
            json.add(obj.toString());
        }
        if (json.size() == 0) {
            throw new Exception("商品价格错误!");
        }
        HotelRoomsUpdateRequest req = new HotelRoomsUpdateRequest();
        req.setGidRoomQuotaMap(json.toString());
        TaobaoClient client = TaobaoUtil.getClient();
        HotelRoomsUpdateResponse res = (HotelRoomsUpdateResponse) TaobaoUtil.getResponse(client, req, sessionkey);
        return res.getBody();
    }

    public String getStatusByModifyTime(Timestamp startModifyTime, long pageno, long pagesize, int type,
            String sessionkey) {
        try {
            if (startModifyTime == null) {
                throw new Exception("修改开始时间不能为空。");
            }
            if (pageno < 1) {
                pageno = 1;
            }
            if (pagesize < 1 || pagesize > 100) {
                pagesize = 100;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //酒店
            if (type == 1) {
                HotelSoldHotelsIncrementGetRequest req = new HotelSoldHotelsIncrementGetRequest();
                req.setStartModified(sdf.parse(sdf.format(startModifyTime)));
                req.setPageNo(pageno);
                req.setPageSize(pagesize);
                TaobaoClient client = TaobaoUtil.getClient();
                HotelSoldHotelsIncrementGetResponse res = (HotelSoldHotelsIncrementGetResponse) TaobaoUtil.getResponse(
                        client, req, sessionkey);
                return res.getBody();
            }
            else if (type == 2) {
                HotelSoldTypesIncrementGetRequest req = new HotelSoldTypesIncrementGetRequest();
                req.setStartModified(sdf.parse(sdf.format(startModifyTime)));
                req.setPageNo(pageno);
                req.setPageSize(pagesize);
                TaobaoClient client = TaobaoUtil.getClient();
                HotelSoldTypesIncrementGetResponse res = (HotelSoldTypesIncrementGetResponse) TaobaoUtil.getResponse(
                        client, req, sessionkey);
                return res.getBody();
            }
            else {
                throw new Exception("请求类型错误。");
            }
        }
        catch (Exception e) {
            String msg = e.getMessage();
            return ElongHotelInterfaceUtil.StringIsNull(msg) ? "出现异常。" : msg;
        }
    }

    //淘宝酒店级别
    private String getLevel(Integer localstar) {
        int star = localstar == null ? 0 : localstar.intValue();
        String level = "";
        if (star == 5) {
            level = "F";//五星级/豪华
        }
        else if (star == 4) {
            level = "E";//四星级/高档
        }
        else if (star == 3) {
            level = "D";//三星级/舒适
        }
        else if (star == 2) {
            level = "C";//二星级/以下
        }
        else {
            level = "B";//经济连锁
        }
        return level;
    }

    private String getBed(Integer localbed) {
        //床型 可选值：A,B,C,D,E,F,G,H,I。分别代表：A：单人床，B：大床，C：双床，D：双床/大床，E：子母床，F：上下床，G：圆形床，H：多床，I：其他床型
        String bed = "I";
        if (localbed != null) {
            if (localbed.intValue() == 1) {
                bed = "A";
            }
            else if (localbed.intValue() == 2) {
                bed = "B";
            }
            else if (localbed.intValue() == 3) {
                bed = "C";
            }
            else if (localbed.intValue() == 4) {
                bed = "D";
            }
        }
        return bed;
    }

    //早餐 A,B,C,D,E。分别代表： A：无早，B：单早，C：双早，D：三早，E：多早
    private String getBf(Long type) {
        String bf = "A";
        if (type != null) {
            if (type.longValue() == 1) {
                bf = "B";
            }
            else if (type.longValue() == 2) {
                bf = "C";
            }
            else if (type.longValue() == 3) {
                bf = "D";
            }
        }
        return bf;
    }

    private int getRoomNum(Hmhotelprice price) {
        //本地房量
        int num = price.getYuliuNum() == null ? 0 : price.getYuliuNum().intValue();
        //RoomStatus 0：开房； 1：关房
        if (price.getRoomstatus() != null && price.getRoomstatus() == 0 && num >= 0) {
            //即时确认
            if (num > 9) {
                num = 9;
            }
            //待确认
            else if (num == 0) {
                num = Integer.parseInt(PropertyUtil.getValue("TaoBaoDQRRoomNum"));
            }
            return num;
        }
        return 0;
    }

    //宽带A,B,C,D。分别代表： A：无宽带，B：免费宽带，C：收费宽带，D：部分收费宽带
    private String getWeb(String desc, Integer wideband) {
        String type = "无";
        if (",宽带上网(免费)".equals(desc)) {
            type = "免费";
        }
        else if (",宽带上网(收费)".equals(desc)) {
            type = "收费";
        }
        else if ("宽带上网(免费)".equals(desc)) {
            type = "免费";
        }
        else if ("宽带上网(收费)".equals(desc)) {
            type = "收费";
        }
        else if ("免费无线上网在行政酒廊,宽带上网(免费)".equals(desc)) {
            type = "免费";
        }
        else if ("无线上网(免费)".equals(desc)) {
            type = "免费";
        }
        else if ("无线上网(免费),宽带上网(免费)".equals(desc)) {
            type = "免费";
        }
        else if ("无线上网(免费),宽带上网(收费)".equals(desc)) {
            type = "收费";
        }
        else if ("无线上网(免费),无线上网(免费)".equals(desc)) {
            type = "免费";
        }
        else if ("无线上网(收费)".equals(desc)) {
            type = "收费";
        }
        else if ("无线上网(收费),宽带上网(免费)".equals(desc)) {
            type = "收费";
        }
        else if ("无线上网(收费),宽带上网(收费)".equals(desc)) {
            type = "收费";
        }
        else if (wideband != null && wideband.intValue() == 1) {
            type = "免费";
        }
        else if (wideband != null && wideband.intValue() == 2) {
            type = "收费";
        }
        String web = "A"; // A：无宽带，B：免费宽带，C：收费宽带，D：部分收费宽带
        if ("收费".equals(type)) {
            web = "C";
        }
        else if ("免费".equals(type)) {
            web = "B";
        }
        return web;
    }
}