package com.ccservice.b2b2c.atom.servlet;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.IAtomService;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 淘宝投保类
 * @author liangwei
 *
 */
public class TaobaoTrainInsure {
    private static TaobaoTrainInsure tbti = null;

    private TaobaoTrainInsure() {

    }

    public static TaobaoTrainInsure getTaobaoTrainInsure() {
        if (tbti == null) {

            tbti = new TaobaoTrainInsure();
        }
        return tbti;
    }

    /**
     * 投保
     * @return
     */
    //    public JSONObject toubao(Trainorder trainorder) {
    //        JSONObject suc = new JSONObject();
    //        suc.put("success", false);
    //        suc.put("dbsuccess", false);
    //        List<Map> lsList = new ArrayList<Map>();
    //        try {
    //            for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
    //                for (Trainticket trainticket : trainpassenger.getTraintickets()) {
    //                    if (trainticket.getInsurorigprice() > 0)//票里的保险价钱大于0说明客户需要投保
    //                    {
    //                        Map mpMap = new HashMap<String, String>();
    //                        int r1 = new Random().nextInt(10000000);
    //                        JSONObject json = insure(trainpassenger.getIdtype() + "", trainpassenger.getIdnumber(),
    //                                trainticket.getDeparttime(), trainpassenger.getBirthday(),
    //                                trainorder.getQunarOrdernumber(), trainpassenger.getName(), trainticket.getTrainno(),
    //                                trainorder.getContacttel(), getagentid("TAOBAO"), r1);
    //                        if (Boolean.parseBoolean(json.getString("success")) == true) {
    //                            mpMap.put("orderid", trainpassenger.getOrderid());
    //                            mpMap.put("idnumber", trainpassenger.getIdnumber());
    //                            mpMap.put("id", trainpassenger.getId());
    //                            mpMap.put("Interfaceticketno", trainticket.getInterfaceticketno());
    //                            lsList.add(mpMap);
    //                        }
    //
    //                    }
    //                }
    //            }
    //            if (lsList.size() > 0) {
    //                int q = 0;
    //                JSONArray jsonArray = new JSONArray();
    //                for (int y = 0; y < 8; y++) {
    //                    for (Iterator it = lsList.iterator(); it.hasNext();) {
    //                        Map mp = (Map) it.next();
    //                        String pc = qpolicyno(Long.valueOf(mp.get("orderid").toString()), mp.get("idnumber").toString());
    //                        if (pc.contains("@") && "1".equals(pc.split("@")[1])) {
    //
    //                            Boolean x = uorderisu(mp.get("id").toString(), pc.split("@")[0]);
    //                            if (x) {
    //                                q++;
    //                                JSONObject jsonObject = new JSONObject();
    //                                jsonObject.put("success", true);
    //                                jsonObject.put("dbsuccess", x);
    //                                jsonObject.put("baodanhao", pc.split("@")[0]);
    //                                jsonObject.put("Interfaceticketno", mp.get("Interfaceticketno"));
    //                                jsonArray.add(jsonObject);
    //                                it.remove();
    //
    //                            }
    //                        }
    //                        //TODO 加投保失败
    //                        //投保失败
    //                        if (pc.contains("@") && "2".equals(pc.split("@")[1])) {
    //                            q++;
    //                            JSONObject jsonObject = new JSONObject();
    //                            jsonObject.put("success", false);
    //                            jsonObject.put("dbsuccess", true);
    //                            jsonObject.put("baodanhao", "");
    //                            jsonObject.put("Interfaceticketno", mp.get("Interfaceticketno"));
    //                            jsonArray.add(jsonObject);
    //                            it.remove();
    //                        }
    //                        if (lsList.size() == 0) {
    //                            if (jsonArray.size() > 0) {
    //                                suc.put("array", jsonArray);
    //                                suc.put("success", true);
    //                                WriteLog.write("淘宝保险接口_投保", "jsonArray" + jsonArray.toJSONString());
    //                                return suc;
    //                            }
    //                            else {
    //                                suc.put("success", false);
    //                                return suc;
    //                            }
    //                        }
    //                        else if (jsonArray.size() > 0 && q > 0 && y > 6) {
    //                            suc.put("array", jsonArray);
    //                            suc.put("success", true);
    //                            WriteLog.write("淘宝保险接口_投保",
    //                                    "jsonArray" + jsonArray.toJSONString() + "失败的数量" + lsList.size());
    //                            return suc;
    //                        }
    //                        else if (y == 7) {
    //                            WriteLog.write("淘宝保险接口_投保超时无法获取保单号", "trainorder" + mp.get("orderid").toString() + "::"
    //                                    + mp.get("idnumber").toString());
    //                            suc.put("success", false);
    //                            return suc;
    //
    //                        }
    //                    }
    //                    if (y == 7) {
    //                        WriteLog.write("淘宝保险接口_投保超时", "超时");
    //                        suc.put("success", false);
    //                        return suc;
    //
    //                    }
    //                    Thread.currentThread().sleep(50000);
    //
    //                }
    //            }
    //        }
    //        catch (Exception e) {
    //            e.printStackTrace();
    //            WriteLog.write("淘宝保险接口_投保", "trainorder" + trainorder.getId() + "" + e.toString() + ":" + e.getMessage());
    //            return suc;
    //        }
    //        return suc;
    //    }

    /**
     * 同步投保-火意险
     * 
     * @param trainorder
     * @return
     * @time 2015年5月18日 下午4:37:39
     * @author fiend
     */
    public Trainorder insuranceSynchronous(Trainorder trainorder) {
        if (1 == 1) {
            return trainorder;
        }
        try {
            for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
                for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                    if (trainticket.getInsurorigprice() > 0
                            && (trainticket.getRealinsureno() == null || "".equals(trainticket.getRealinsureno())))//票里的保险价钱大于0说明客户需要投保
                    {
                        int r1 = new Random().nextInt(10000000);
                        Insuruser insuruser = insureTrain(trainpassenger.getIdtype() + "",
                                trainpassenger.getIdnumber(), trainticket.getDeparttime(),
                                trainpassenger.getBirthday(), trainorder.getQunarOrdernumber(),
                                trainpassenger.getName(), trainticket.getTrainno(), trainorder.getContacttel(),
                                getagentid("TAOBAO"), r1, trainorder.getId(), trainorder.getInsureadreess(),
                                trainorder.getOrdernumber(), trainorder.getCreatetime(),
                                trainorder.getQunarOrdernumber(), "0");
                        if (insuruser != null && insuruser.getPolicyno() != null && !"".equals(insuruser.getPolicyno())) {
                            trainticket.setRealinsureno(insuruser.getPolicyno());
                            Server.getInstance().getTrainService().updateTrainticket(trainticket);
                            WriteLog.write("Taobao_toubao", trainorder.getId() + ":" + trainpassenger.getName() + ":"
                                    + insuruser.getPolicyno());
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return trainorder;
    }

    /**
     * 同步改签投保-火意险
     * 
     * @param trainorder
     * @param trainpassenger
     * @param trainticket
     * @return
     * @time 2015年5月18日 下午6:24:55
     * @author fiend
     */
    public Trainticket insuranceSynchronousGaiQian(Trainorder trainorder, Trainpassenger trainpassenger,
            Trainticket trainticket) {
        try {
            int r1 = new Random().nextInt(10000000);
            Insuruser insuruser = insureTrain(trainpassenger.getIdtype() + "", trainpassenger.getIdnumber(),
                    trainticket.getTtcdeparttime(), trainpassenger.getBirthday(), trainorder.getQunarOrdernumber(),
                    trainpassenger.getName(), trainticket.getTrainno(), trainorder.getContacttel(),
                    getagentid("TAOBAO"), r1, trainorder.getId(), trainorder.getInsureadreess(),
                    trainorder.getOrdernumber(), trainorder.getCreatetime(), trainorder.getQunarOrdernumber(), "1");
            if (insuruser != null && insuruser.getPolicyno() != null && !"".equals(insuruser.getPolicyno())) {
                trainticket.setRealinsureno(insuruser.getPolicyno());
                Server.getInstance().getTrainService().updateTrainticket(trainticket);
                WriteLog.write("Taobao_GQtoubao",
                        trainorder.getId() + ":" + trainpassenger.getName() + ":" + insuruser.getPolicyno());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return trainticket;
    }

    //    public JSONObject GaiQiantoubao(Trainorder trainorder, List<String> tick) {
    //        JSONObject suc = new JSONObject();
    //        suc.put("success", false);
    //        suc.put("dbsuccess", false);
    //        List<Map> lsList = new ArrayList<Map>();
    //        try {
    //            for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
    //                for (Trainticket trainticket : trainpassenger.getTraintickets()) {
    //
    //                    if (trainticket.getInsurorigprice() > 0)//票里的保险价钱大于0说明客户需要投保
    //                    {
    //                        for (String t : tick) {
    //                            if (t.equals(trainticket.getInterfaceticketno())) {
    //                                Map mpMap = new HashMap<String, String>();
    //                                int r1 = new Random().nextInt(10000000);
    //                                JSONObject json = insure(trainpassenger.getIdtype() + "", trainpassenger.getIdnumber(),
    //                                        trainticket.getDeparttime(), trainpassenger.getBirthday(),
    //                                        trainorder.getQunarOrdernumber(), trainpassenger.getName(),
    //                                        trainticket.getTrainno(), trainorder.getContacttel(), getagentid("TAOBAO"), r1);
    //                                if (Boolean.parseBoolean(json.getString("success")) == true) {
    //                                    mpMap.put("orderid", trainpassenger.getOrderid());
    //                                    mpMap.put("idnumber", trainpassenger.getIdnumber());
    //                                    mpMap.put("id", trainpassenger.getId());
    //                                    mpMap.put("Interfaceticketno", t);
    //                                    lsList.add(mpMap);
    //                                }
    //                            }
    //                        }
    //                    }
    //                }
    //            }
    //            if (lsList.size() > 0) {
    //                JSONArray jsonArray = new JSONArray();
    //                for (int y = 0; y < 8; y++) {
    //                    for (Iterator it = lsList.iterator(); it.hasNext();) {
    //                        Map mp = (Map) it.next();
    //                        String pc = qpolicyno(Long.valueOf(mp.get("orderid").toString()), mp.get("idnumber").toString());
    //                        System.out.println(pc);
    //                        if (pc.contains("@") && "1".equals(pc.split("@")[1])) {
    //
    //                            Boolean x = uorderisu(mp.get("id").toString(), pc.split("@")[0]);
    //                            if (x) {
    //                                JSONObject jsonObject = new JSONObject();
    //                                jsonObject.put("success", true);
    //                                jsonObject.put("dbsuccess", x);
    //                                jsonObject.put("baodanhao", pc.split("@")[0]);
    //                                jsonObject.put("Interfaceticketno", mp.get("Interfaceticketno"));
    //                                jsonArray.add(jsonObject);
    //                                it.remove();
    //
    //                            }
    //                        }
    //                        //TODO 加投保失败
    //                        //投保失败
    //                        if (pc.contains("@") && "2".equals(pc.split("@")[1])) {
    //                            JSONObject jsonObject = new JSONObject();
    //                            jsonObject.put("success", false);
    //                            jsonObject.put("dbsuccess", true);
    //                            jsonObject.put("baodanhao", "");
    //                            jsonObject.put("Interfaceticketno", mp.get("Interfaceticketno"));
    //                            jsonArray.add(jsonObject);
    //                            it.remove();
    //                        }
    //                        if (lsList.size() == 0) {
    //                            if (jsonArray.size() > 0) {
    //                                suc.put("array", jsonArray);
    //                                suc.put("success", true);
    //                                return suc;
    //                            }
    //                            else {
    //                                suc.put("success", false);
    //                                return suc;
    //                            }
    //                        }
    //                        else if (lsList.size() > 0 && jsonArray.size() > 0 && y > 5) {
    //                            suc.put("array", jsonArray);
    //                            suc.put("success", true);
    //                            return suc;
    //                        }
    //
    //                    }
    //                    if (y == 7) {
    //                        WriteLog.write("淘宝保险接口_投保超时无法获取保单号", "trainorder" + trainorder + "超时");
    //                        suc.put("success", false);
    //                        return suc;
    //
    //                    }
    //                    Thread.currentThread().sleep(50000);
    //
    //                }
    //            }
    //        }
    //        catch (Exception e) {
    //            e.printStackTrace();
    //            WriteLog.write("淘宝保险接口_投保", "trainorder" + trainorder.getId() + "" + e.toString() + ":" + e.getMessage());
    //            return suc;
    //        }
    //        return suc;
    //    }

    /**
     * 淘宝改签 换新保
     * @param trainorderid
     * @param msg
     * @param json
     * @return
     */
    public JSONObject GaiQianTui(int trainorderid, String msg, JSONArray json) {
        JSONObject jsons = new JSONObject();
        jsons.put("success", false);
        JSONArray jsonArray = new JSONArray();
        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderid);
        try {
            for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
                for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                    for (int i = 0; i < json.size(); i++) {
                        if (json.getJSONObject(i).getString("subbizorderid").equals(trainticket.getInterfaceticketno())) {
                            if (trainticket.getInsurorigprice() > 0 && trainticket.getRealinsureno() != null)//票里的保险价钱大于0说明客户有保险
                            {
                                int r1 = new Random().nextInt(10000000);
                                JSONObject jsonObject = cancelOrderAplylist(trainticket.getRealinsureno(), msg, r1);//退保
                                if (jsonObject.getBoolean("success"))//退保成功在买保险
                                {
                                    trainticket = insuranceSynchronousGaiQian(trainorder, trainpassenger, trainticket);
                                    JSONObject jsono = new JSONObject();
                                    jsono.put("success", true);
                                    jsono.put("dbsuccess", true);
                                    jsono.put("baodanhao", trainticket.getRealinsureno());
                                    jsono.put("Interfaceticketno", trainticket.getInterfaceticketno());
                                    jsonArray.add(jsono);
                                    //                                    lsLists.add(trainticket.getInterfaceticketno());

                                }
                                else {
                                    JSONObject jsono = new JSONObject();
                                    jsono.put("success", false);
                                    jsono.put("dbsuccess", false);
                                    jsono.put("baodanhao", "");
                                    jsono.put("Interfaceticketno", trainticket.getInterfaceticketno());
                                    jsonArray.add(jsono);
                                    WriteLog.write("淘宝保险接口_改签", "退保失败trainorder" + trainorder.getId() + "票ID"
                                            + trainticket.getId() + " Insureno" + trainticket.getRealinsureno()
                                            + "退保成功");
                                }
                            }
                        }
                    }
                }
            }
            //            if (lsLists.size() > 0) {
            //                JSONObject jsono = GaiQiantoubao(trainorder, lsLists);
            //                if (jsono.getBooleanValue("success")) {
            //                    jsons.put("jArray", jsono.getJSONArray("array"));
            //                    jsons.put("success", true);
            //                }
            //                else {
            //                    jsons.put("success", false);
            //                }
            //                return jsons;
            //            }
            //            else {
            //                jsons.put("success", false);
            //                return jsons;
            //            }
            jsons.put("jArray", jsonArray);
            jsons.put("success", true);
            return jsons;
        }
        catch (Exception e) {
            WriteLog.write("淘宝保险接口_投保", "trainorder" + trainorder.getId() + "" + e.toString() + ":" + e.getMessage());
            jsons.put("success", false);
            return jsons;
        }
    }

    /**
     * 退保
     * @return
     */
    public boolean tuibao(int trainorderid, String msg, String Insureno) {

        if (Insureno == null || Insureno.equals("")) {
            return false;
        }
        JSONObject jsonObject = new JSONObject();
        try {

            int r1 = new Random().nextInt(10000000);
            jsonObject = cancelOrderAplylist(Insureno, msg, r1);
            if (jsonObject.getBoolean("success")) {
                return uorderisu1("", Insureno);

            }

            WriteLog.write("淘宝保险接口_退保", "trainorder" + trainorderid + " Insureno" + Insureno + "jsonObject"
                    + jsonObject);
            return false;
        }
        catch (Exception e) {
            WriteLog.write("淘宝保险接口_退保", "Insureno" + Insureno + "" + e.toString() + ":" + e.getMessage());
            return false;

        }

    }

    /**
     * 退保订单查询
     * @param policyno
     * @return
     */
    public long qorderids(String policyno) {

        String sql = "SELECT ID FROM T_INSURUSER WHERE C_POLICYNO='" + policyno + "'";
        try {
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                return Long.valueOf(map.get("ID").toString());
            }
            return 0l;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0l;
        }

    }

    /**
     * 通过事务来处理保险逻辑
     * @param id
     * @param idnumber
     * @return
     */
    //    public String qpolicyno(long id, String idnumber) {
    //        try {
    //            String strSP = "[dbo].[sp_Taobao_Trainorder_Insuruser] " + "@orderid = " + id + "," + "@code = N'"
    //                    + idnumber + "'";
    //            List list = Server.getInstance().getSystemService().findMapResultByProcedure(strSP);
    //            if (list.size() > 0) {
    //                Map map = (Map) list.get(0);
    //                return (map.get("datainfo").toString() != null && !"".equals(map.get("datainfo").toString())) ? map
    //                        .get("datainfo").toString() : "@0";
    //            }
    //            else {
    //                return "@0";
    //            }
    //        }
    //        catch (Exception e) {
    //            e.printStackTrace();
    //            return "@0";
    //
    //        }
    //    }

    //    public String qpolicyno(long id, String idnumber) {
    //        try {
    //
    //            String sql = "SELECT TOP 1 C_POLICYNO,C_INSURSTATUS,ID FROM T_INSURUSER  with (nolock) WHERE C_ORDERID="
    //                    + id + " and C_CODE='" + idnumber + "' and C_REMARK is null";
    //            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
    //            if (list.size() > 0) {
    //                Map map = (Map) list.get(0);
    //                String policyno = map.get("C_POLICYNO") == null ? "" : map.get("C_POLICYNO").toString();
    //                return policyno + "@" + map.get("C_INSURSTATUS").toString();
    //            }
    //            return "";
    //
    //        }
    //        catch (Exception e) {
    //            return "";
    //
    //        }
    //    }

    /**
     * 退保
     * @param policyno
     * @param cancelReason
     * @param r1
     * @return
     */
    public JSONObject cancelOrderAplylist(String policyno, String cancelReason, int r1) {
        //TODO 退保需要修改

        JSONObject jsonObject = new JSONObject();
        long insereid = qorderids(policyno);
        if (0 == insereid) {
            jsonObject.put("success", false);
            jsonObject.put("code", 108);
            jsonObject.put("msg", "退保0接口参数校验错误");
        }
        else {

            Insuruser insuruser = Server.getInstance().getAirService().findInsuruser(insereid);

            //找到订单对应的保险
            try {
                String result = "-1";

                //当保险状态是1:投保成功的话才调用退保或者取消订单的接口
                if (insuruser.getInsurstatus() == 1) {
                    result = getInsrueAtomService(insereid).cancelInsuruser(insuruser);

                    WriteLog.write("淘宝保险接口_退保_boang", r1 + ":" + result);
                }

                if (result.contains("3,")) {
                    jsonObject.put("success", true);
                    jsonObject.put("code", 100);
                    jsonObject.put("msg", "成功");
                    //4退保成功
                    insuruser.setInsurstatus(3);
                    try {
                        Server.getInstance().getAirService().updateInsuruser(insuruser);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        WriteLog.write("淘宝保险接口_退保", r1 + ":数据库更新异常");
                    }
                }
                else {
                    jsonObject.put("success", false);
                    jsonObject.put("code", 108);
                    jsonObject.put("msg", "退保失败");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                jsonObject.put("code", 105);
                jsonObject.put("success", false);
                jsonObject.put("msg", "接口处理异常");
            }
        }
        WriteLog.write("淘宝保险接口_退保", r1 + ":result:" + jsonObject.toString());
        return jsonObject;
    }

    /**
     * 获取保险代理商ID
     * @param userid
     * @return
     */
    private long getagentid(String userid) {
        String sql = "SELECT C_AGENTID,C_KEY FROM T_HTHYINSUREINFO WHERE C_USERNAME = '" + userid + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            return Long.valueOf(map.get("C_AGENTID").toString());
        }
        return 0;
    }

    /**
     * 投保
     * @param idtype
     * @param idno
     * @param traindate
     * @param birthday
     * @param trainordernumber
     * @param name
     * @param tradeno
     * @param mobile
     * @param agentid
     * @param r1
     * @return
     */
    //    public JSONObject insure(String idtype, String idno, String traindate, String birthday, String trainordernumber,
    //            String name, String tradeno, String mobile, long agentid, int r1) {
    //        WriteLog.write("淘宝保险接口_投保", r1 + ":idtype:" + idtype + ":idno:" + idno + ":traindate:" + traindate
    //                + ":birthday:" + birthday + ":trainordernumber:" + trainordernumber + ":name:" + name + ":tradeno:"
    //                + tradeno + ":mobile:" + mobile + ":agentid:" + agentid);
    //        JSONObject obj = new JSONObject();
    //        long orderid = qorderid(trainordernumber);
    //        if (orderid == 0l) {
    //            obj.put("code", 106);
    //            obj.put("success", false);
    //            obj.put("msg", "非法无效参数");
    //        }
    //        else {
    //            try {
    //                Insuruser insur = new Insuruser();
    //                insur.setAgentid(agentid);
    //                insur.setCodetype(Long.valueOf(idtype));
    //                insur.setCode(idno);
    //                insur.setFlytime(formatStringToTime(traindate, "yyyy-MM-dd HH:mm"));
    //                insur.setOrdernum(trainordernumber);
    //                insur.setOrderid(orderid);
    //                try {
    //                    if (birthday == null || "".equals(birthday)) {
    //                        birthday = idno.substring(6, 10) + "-" + idno.substring(10, 12) + "-" + idno.substring(12, 14);
    //                    }
    //                }
    //                catch (Exception e) {
    //                    e.printStackTrace();
    //                }
    //                insur.setBirthday(formatStringToTime(birthday, "yyyy-MM-dd"));
    //                insur.setName(name);
    //                insur.setFlyno(tradeno);
    //                insur.setMobile(mobile);
    //                insur = Server.getInstance().getAirService().createInsuruser(insur);
    //                WriteLog.write("淘宝保险接口_投保", "信息保存完成");
    //                List insurlist = new ArrayList();
    //                insurlist.add(insur);
    //                List<Insuruser> listinsurance = new ArrayList<Insuruser>();
    //                listinsurance = Server.getInstance().getAtomService().newOrderAplylist(null, insurlist);
    //                WriteLog.write("淘宝保险接口_投保", "投保成功数量" + listinsurance.size());
    //                if (listinsurance.size() > 0) {
    //                    for (int i = 0; i < listinsurance.size(); i++) {
    //
    //                        obj.put("insunumber", listinsurance.get(i));
    //                    }
    //
    //                    obj.put("code", 100);
    //                    obj.put("success", true);
    //                    obj.put("msg", "投保请求已接受");
    //                }
    //                else {
    //                    obj.put("code", 105);
    //                    obj.put("success", false);
    //                    obj.put("msg", "接口处理异常");
    //                }
    //            }
    //            catch (Exception e) {
    //                e.printStackTrace();
    //                obj.put("code", 105);
    //                obj.put("success", false);
    //                obj.put("msg", "接口处理异常1" + e.getMessage() + "            " + e.toString());
    //            }
    //        }
    //        WriteLog.write("淘宝保险接口_投保", r1 + ":result:" + obj.toString());
    //        return obj;
    //    }

    /**
     * 火意险
     * 
     * @param idtype
     * @param idno
     * @param traindate
     * @param birthday
     * @param trainordernumber
     * @param name
     * @param tradeno
     * @param mobile
     * @param agentid
     * @param r1
     * @return
     * @time 2015年5月18日 下午4:40:53
     * @author fiend
     */
    @SuppressWarnings("unchecked")
    public Insuruser insureTrain(String idtype, String idno, String traindate, String birthday,
            String trainordernumber, String name, String tradeno, String mobile, long agentid, int r1, long id,
            String adds, String ordernum, Timestamp createdate, String qunarordernum, String flag) {
        WriteLog.write("淘宝保险接口_投保", r1 + ":idtype:" + idtype + ":idno:" + idno + ":traindate:" + traindate
                + ":birthday:" + birthday + ":trainordernumber:" + trainordernumber + ":name:" + name + ":tradeno:"
                + tradeno + ":mobile:" + mobile + ":agentid:" + agentid);
        Insuruser insuruser = new Insuruser();
        long orderid = qorderid(trainordernumber);
        if (orderid == 0l) {
            return insuruser;
        }
        else {
            try {
                insuruser.setAgentid(agentid);
                insuruser.setCodetype(Long.valueOf(idtype));
                insuruser.setCode(idno);
                insuruser.setFlytime(sunshineTimestamp(traindate, "yyyy-MM-dd HH:mm"));
                insuruser.setOrdernum(trainordernumber);
                insuruser.setOrderid(orderid);
                //TODO point 投保时间 无论是否成功 都需要set
                insuruser.setBegininsuretime(new Timestamp(System.currentTimeMillis()));
                //起保时间  ？？？？？？=================
                //TODO 起保时间
                //insuruser.setBegintime(Begintime);
                try {
                    if (birthday == null || "".equals(birthday)) {
                        if (idno.length() == 18) {
                            birthday = idno.substring(6, 10) + "-" + idno.substring(10, 12) + "-"
                                    + idno.substring(12, 14);
                            if (!isAdult(birthday)) {
                                birthday = "1984-06-01";
                                WriteLog.write("生成保险生日日期", "未成年转换保险生日--->" + orderid);
                            }
                        }
                        else {
                            birthday = "1984-06-01";
                            WriteLog.write("生成保险生日日期", "护照转换保险生日--->" + orderid);
                        }
                    }
                }
                catch (Exception e) {
                    ExceptionUtil.writelogByException("生成保险生日日期_ERROR", e);
                }
                try {
                    insuruser.setBirthday(formatStringToTime(birthday, "yyyy-MM-dd"));
                }
                catch (Exception e) {
                    ExceptionUtil.writelogByException("赋值保险生日日期_ERROR", e);
                }
                insuruser.setName(name);
                insuruser.setFlyno(tradeno);
                insuruser.setMobile(mobile);
                insuruser.setEmail("baoxian@clbao.com");
                insuruser.setInsurstatus(0);
                List<Insuruser> insurlist = new ArrayList<Insuruser>();
                insuruser = Server.getInstance().getAirService().createInsuruser(insuruser);
                insurlist.add(insuruser);
                List<Insuruser> listinsurance = new ArrayList<Insuruser>();
                //                listinsurance = Server.getInstance().getAtomService().newOrderAplylist(null, insurlist);
                listinsurance = gethyxInsureAtomService().newOrderAplylist(null, insurlist);
                if (listinsurance.size() > 0) {
                    WriteLog.write("淘宝保险接口_投保", r1 + ":投保成功");
                    insuruser.setPolicyno(listinsurance.get(0).getPolicyno());
                    insuruser.setInsurstatus(listinsurance.get(0).getInsurstatus());
                    insuruser.setRemark(listinsurance.get(0).getRemark());
                }
                WriteLog.write("淘宝保险接口_投保", r1 + ":信息保存完成");
                Server.getInstance().getAirService().updateInsuruser(insuruser);
                if (adds != null && adds.contains("^^^")) {
                    //火车票保险邮寄地址
                    try {
                        String s2 = adds.replace("^^^", "-");
                        String[] ss = s2.split("-");
                        String sql = "";
                        //创建火车票保险邮寄信息
                        if (flag.equals("0")) {
                            sql = "INSERT INTO TrainInsureMailingAddress(OrderID,Name,Province,City,Distric,"
                                    + "DetailedAddress,ZipCode,Phone,Arg1,Arg2,Arg3,OrderNum,DepartTime,InsuranceNum,PrintStatus,CreateTime,AgentId,InsuranceStatus,InsurUserId,QunarOrderNumber) VALUES("
                                    + orderid
                                    + ",'"
                                    + name
                                    + "','"
                                    + ss[1]
                                    + "','"
                                    + ss[2]
                                    + "','"
                                    + ss[3]
                                    + "','"
                                    + ss[4]
                                    + "',"
                                    + ss[5]
                                    + ","
                                    + ss[6]
                                    + ",'age1',111,'age3','"
                                    + ordernum
                                    + "','"
                                    + formatStringToTime(traindate, "yyyy-MM-dd HH:mm")
                                    + "',1,1,'"
                                    + createdate
                                    + "','"
                                    + agentid
                                    + "',"
                                    + insuruser.getInsurstatus()
                                    + ",'"
                                    + insuruser.getId()
                                    + "','" + qunarordernum + "')";
                        }
                        //火车票改签,修改发车时间
                        else {
                            sql = "UPDATE TrainInsureMailingAddress SET DepartTime='"
                                    + formatStringToTime(traindate, "yyyy-MM-dd HH:mm") + "' WHERE InsurUserId="
                                    + insuruser.getId();
                        }
                        WriteLog.write("UPDATEandINSERT_TrainInsureMailingAddress", sql);
                        Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    }
                    catch (Exception e) {
                        WriteLog.write("UPDATEandINSERT_TrainInsureMailingAddress_error", trainordernumber);
                        ExceptionUtil.writelogByException("UPDATEandINSERT_TrainInsureMailingAddress_error", e);
                    }
                }
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("投保_ERROR", e);
            }
        }
        WriteLog.write("淘宝保险接口_投保", r1 + ":id:" + insuruser.getId() + ":policyno:" + insuruser.getPolicyno()
                + ":insurstatus:" + insuruser.getInsurstatus());
        return insuruser;
    }

    /**
     * 获取火意险的cn_interface地址
     * 
     * @return
     * @time 2015年5月25日 下午3:44:20
     * @author Auser
     */
    public static IAtomService gethyxInsureAtomService() {
        HessianProxyFactory factory = new HessianProxyFactory();
        String search_12306yupiao_service_url = PropertyUtil.getValue("hyxInsure_cn_interface_url", "Train.properties");
        try {
            return (IAtomService) factory.create(IAtomService.class, search_12306yupiao_service_url
                    + IAtomService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 时间转换 
     * @param date
     * @param format
     * @return
     * @time 2014年10月9日 下午6:45:34
     * @author yinshubin
     */
    public Timestamp formatStringToTime(String date, String format) {
        try {
            SimpleDateFormat simplefromat = new SimpleDateFormat(format);
            return new Timestamp(simplefromat.parse(date).getTime());

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public long qorderid(String trainordernumber) {
        String sql = "SELECT ID FROM T_TRAINORDER WITH (NOLOCK) WHERE C_QUNARORDERNUMBER='" + trainordernumber + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            return Long.valueOf(map.get("ID").toString());
        }
        return 0l;
    }

    /**
     * 修改票表里的保单号
     * @param pid   乘客id
     * @param insid 保单号
     * @return
     */
    public boolean uorderisu(String pid, String insid) {
        if (pid != null && insid != null) {
            String up = "update T_TRAINTICKET set C_REALINSURENO='" + insid + "' where  c_trainpid= " + pid;
            Server.getInstance().getSystemService().excuteEaccountBySql(up);
            return true;
        }
        return false;
    }

    /**
     * 退票退保更新
     * @param insid
     * @param oldinsid
     * @return
     */
    public boolean uorderisu1(String insid, String oldinsid) {

        if (oldinsid != null && insid != null) {
            String up = "update T_TRAINTICKET set C_REALINSURENO='" + insid + "' where  C_REALINSURENO= '" + oldinsid
                    + "'";
            Server.getInstance().getSystemService().excuteEaccountBySql(up);
            return true;
        }
        return false;
    }

    /**
     * 改签退通过淘宝票ID查询保单号
     * @return
     */
    public JSONObject qtickinface(String interfaces) {
        JSONObject jsonObject = new JSONObject();
        String sql = "SELECT c_trainpid,C_REALINSURENO FROM T_TRAINTICKET WITH (NOLOCK) WHERE C_INTERFACETICKETNO='"
                + interfaces + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            jsonObject.put("c_insureno", map.get("C_REALINSURENO"));
            jsonObject.put("c_trainpid", map.get("c_trainpid"));
            return jsonObject;
        }
        return null;
    }

    /**
     * 根据投保时间确定新老保险
     * 获取对应的项目地址
     * @return
     * @time 2015年5月25日 下午2:40:35
     * @author fiend
     * @throws Exception 
     */
    public IAtomService getInsrueAtomService(long insureid) throws Exception {
        String str_old_insure_endid = PropertyUtil.getValue("str_old_insure_endid", "Train.properties");
        if (insureid > Long.valueOf(str_old_insure_endid)) {
            WriteLog.write("Taobao_tuibao", insureid + ":火意险");
            return gethyxInsureAtomService();
        }
        else {
            WriteLog.write("Taobao_tuibao", insureid + ":航意险");
            return Server.getInstance().getAtomService();
        }
    }

    public static void main(String arg[]) {
        //        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(16494);
        //        Trainticket trainticket = trainorder.getPassengers().get(0).getTraintickets().get(0);
        //        testcancelinsure(trainorder, trainticket);
        //        testGQinsure();
        //        testinsure();
    }

    /**
     * 测试取消保险
     * @param trainorder
     * @param trainticket
     * @author fiend 
     */
    public static void testcancelinsure(Trainorder trainorder, Trainticket trainticket) {

        Map<String, Object> mp = new HashMap<String, Object>();

        //        String sql = "update T_TRAINTICKET set C_STATUS=" + Trainticket.REFUNDED + " where ID=" + trainticket.getId();
        //        Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        String refundfee = "0";
        boolean b = false;
        for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
            for (Trainticket tticket : trainpassenger.getTraintickets()) {
                if (tticket.getId() == trainticket.getId()) {
                    trainticket = tticket;
                }
            }
        }
        if (trainticket.getRealinsureno() != null) {
            try {
                b = TaobaoTrainInsure.getTaobaoTrainInsure().tuibao(Integer.parseInt(trainorder.getId() + ""), "客户退票",
                        trainticket.getRealinsureno());
                System.out.println(b);
                if (b) {

                    WriteLog.write("淘宝退票", "退票退保成功" + Integer.parseInt(trainorder.getId() + "") + "   Insureno  "
                            + trainticket.getRealinsureno());
                }
                else {
                    WriteLog.write("淘宝退票", "退票退保失败" + Integer.parseInt(trainorder.getId() + "") + "   Insureno  "
                            + trainticket.getRealinsureno());
                }
            }

            catch (Exception e) {
                WriteLog.write("淘宝退票", "退票退保失败" + Integer.parseInt(trainorder.getId() + "") + "   Insureno  "
                        + trainticket.getRealinsureno());
            }
        }
        if (trainticket.getTcnewprice() != null && trainticket.getTcnewprice().floatValue() > 0) {

            int n = (int) ((trainticket.getTcnewprice() - trainticket.getProcedure()) * 100);
            if (b) {
                int y = (int) (trainticket.getInsurorigprice() * 100);
                n = n + y;
            }
            refundfee = n + "";

        }
        else {
            int n = (int) ((trainticket.getPrice() - trainticket.getProcedure()) * 100);
            if (b) {
                int y = (int) (trainticket.getInsurorigprice() * 100);
                n = n + y;
            }
            refundfee = n + "";
        }

        mp.put("refund_fee", refundfee);
        mp.put("agree_return", true);
        mp.put("refuse_return_reason", "no");
        mp.put("main_order_id", trainorder.getQunarOrdernumber());//
        mp.put("sub_biz_order_id", trainticket.getInterfaceticketno());
        mp.put("buyerid", trainorder.getTaobaosendid());
        System.out.println(mp.toString());
    }

    /**
     * 测试投保
     * @author fiend
     */
    public static void testinsure() {

        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(16494);
        trainorder = TaobaoTrainInsure.getTaobaoTrainInsure().insuranceSynchronous(trainorder);
        Server.getInstance().getTrainService().updateTrainorder(trainorder);
        try {
            Map mp = new HashMap();
            // mp.put("alipayTradeNo", trainorder.getSupplytradeno());
            mp.put("main_order_id", trainorder.getQunarOrdernumber());
            mp.put("status", true);
            mp.put("failMsg", "no");
            StringBuffer sb = new StringBuffer();
            int num = 0;
            for (int i = 0; i < trainorder.getPassengers().size(); i++) {
                Trainpassenger tp = trainorder.getPassengers().get(i);
                for (int y = 0; y < tp.getTraintickets().size(); y++) {
                    Trainticket tk = tp.getTraintickets().get(y);
                    if (y == 0) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        mp.put("depDate", df.format(df.parse(tk.getDeparttime())));
                    }

                    num = num + 1;
                    // 火车票订单id，单价，坐席，座位号，车次，乘车人姓名，证件名称，证件号码，保单号，保单价格
                    int x = (int) (tk.getPrice() * 100);
                    sb.append(tk.getInterfaceticketno() + ";");
                    sb.append(x + ";");
                    sb.append(TaobaoHotelInterfaceUtil.CackBackSuccessseao(tk.getSeattype(), tk.getSeatno()) + ";");
                    if (tk.getSeatno() == null) {
                        sb.append("no" + ";");
                    }
                    else {

                        sb.append(tk.getSeattype() + "_" + tk.getCoach() + "_" + tk.getSeatno().toString() + ";");
                    }
                    sb.append(tk.getTrainno() + ";");
                    sb.append(tp.getName().toString() + ";");
                    String certTypeValue = tp.getIdtypestr().toString();
                    sb.append(IDnumberType(certTypeValue) + ";");
                    sb.append(tp.getIdnumber() + ";");
                    if (tk.getRealinsureno() == null || "".equals(tk.getRealinsureno())) {
                        sb.append("0;");
                        sb.append("0");
                    }
                    else {
                        sb.append(tk.getRealinsureno() + ";");
                        sb.append((int) (tk.getInsurorigprice() * 100));
                    }
                    if (trainorder.getPassengers().size() > i + 1) {
                        sb.append(",");
                    }

                }
            }
            mp.put("tickets", sb);

            if (trainorder.getExtnumber() == null) {
                mp.put("ticket12306Id", "no");
            }
            else {
                mp.put("ticket12306Id", trainorder.getExtnumber().toString());
            }
            mp.put("ticketNum", num);// 火车票
            System.out.println(mp.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 测试改签保险
     * @author fiend
     */
    public static void testGQinsure() {
        JSONObject jsons = new JSONObject();
        jsons.put("success", false);
        JSONArray jsonArray = new JSONArray();
        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(16494);
        try {
            for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
                for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                    if (trainticket.getInsurorigprice() > 0 && trainticket.getRealinsureno() != null)//票里的保险价钱大于0说明客户有保险
                    {
                        int r1 = new Random().nextInt(10000000);
                        JSONObject jsonObject = TaobaoTrainInsure.getTaobaoTrainInsure().cancelOrderAplylist(
                                trainticket.getRealinsureno(), "error", r1);//退保
                        if (jsonObject.getBoolean("success"))//退保成功在买保险
                        {
                            trainticket = TaobaoTrainInsure.getTaobaoTrainInsure().insuranceSynchronousGaiQian(
                                    trainorder, trainpassenger, trainticket);
                            JSONObject jsono = new JSONObject();
                            jsono.put("success", true);
                            jsono.put("dbsuccess", true);
                            jsono.put("baodanhao", trainticket.getRealinsureno());
                            jsono.put("Interfaceticketno", trainticket.getInterfaceticketno());
                            jsonArray.add(jsono);
                            //                                    lsLists.add(trainticket.getInterfaceticketno());

                        }
                        else {
                            JSONObject jsono = new JSONObject();
                            jsono.put("success", false);
                            jsono.put("dbsuccess", false);
                            jsono.put("baodanhao", "");
                            jsono.put("Interfaceticketno", trainticket.getInterfaceticketno());
                            jsonArray.add(jsono);
                            WriteLog.write("淘宝保险接口_改签",
                                    "退保失败trainorder" + trainorder.getId() + "票ID" + trainticket.getId() + " Insureno"
                                            + trainticket.getRealinsureno() + "退保成功");
                        }
                    }
                }
            }
            jsons.put("jArray", jsonArray);
            jsons.put("success", true);
            System.out.println(jsons.toJSONString());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(jsons.toJSONString());
        }

    }

    /**
     * 测试使用证件类型
     * @param val
     * @return
     * @author fiend
     */
    public static String IDnumberType(String val) {

        if (val.equals("二代身份证")) {
            return "0";
        }
        else if (val.equals("一代身份证")) {
            return "0";
        }
        else if (val.equals("护照")) {
            return "1";
        }
        else if (val.equals("港澳通行证")) {
            return "4";
        }
        else if (val.equals("台湾通行证")) {
            return "5";
        }

        return "-1";
    }

    //    private static boolean isAdult(String brithday) {
    //        try {
    //            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //            Calendar c = Calendar.getInstance();
    //            c.setTime(sdf.parse(brithday));
    //            Date d = c.getTime();
    //            int age = (int) ((new Date().getTime() - d.getTime()) / 1000 / 60 / 60 / 24 / 365);
    //            return age >= 18;
    //        }
    //        catch (ParseException e) {
    //            return true;
    //        }
    //    }

    /**
     * 判断是否是成人
     * 
     * @param brithday
     *            生日 yyyy-MM-dd
     * @return 年龄
     * @author fiend
     */
    public static boolean isAdult(String brithday) {
        try {
            int brithday_year = Integer.valueOf(brithday.split("-")[0]);
            int brithday_month = Integer.valueOf(brithday.split("-")[1]);
            int brithday_day = Integer.valueOf(brithday.split("-")[2]);
            Calendar now = Calendar.getInstance();
            if ((now.get(Calendar.YEAR) - brithday_year) > 18) {
                return true;
            }
            if ((now.get(Calendar.YEAR) - brithday_year) == 18) {
                if (((now.get(Calendar.MONTH) + 1) - brithday_month) > 0) {
                    return true;
                }
                if (((now.get(Calendar.MONTH) + 1) - brithday_month) == 0) {
                    if (now.get(Calendar.DAY_OF_MONTH) > brithday_day) {
                        return true;
                    }
                }
            }
            return false;
        }
        catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取一个可以投保的发车时间  如果实际时间不满足 就+1个小时
     * @param traindate
     * @param format
     * @return
     * @author fiend
     */
    private Timestamp sunshineTimestamp(String traindate, String format) {
        try {
            if (isNeedFalsityTrainDate(traindate, format)) {
                return falsityTrainDate(traindate, format);
            }
            else {
                return formatStringToTime(traindate, format);
            }
        }
        catch (ParseException e) {
            return formatStringToTime(traindate, format);
        }
    }

    /**
     * 发车时间-系统当前时间 是否大于63分钟
     * @param traindate
     * @param format
     * @return
     * @author fiend
     */
    private boolean isNeedFalsityTrainDate(String traindate, String format) {
        try {
            SimpleDateFormat simplefromat = new SimpleDateFormat(format);
            return (simplefromat.parse(traindate).getTime() - System.currentTimeMillis()) < (63 * 60 * 1000);
        }
        catch (ParseException e) {
            return false;
        }
    }

    /**
     * 获取虚假发车时间 原发车时间+1小时
     * @param traindate
     * @param format
     * @return
     * @author fiend
     * @throws ParseException 
     */
    private Timestamp falsityTrainDate(String traindate, String format) throws ParseException {
        SimpleDateFormat simplefromat = new SimpleDateFormat(format);
        return new Timestamp(simplefromat.parse(traindate).getTime() + (60 * 60 * 1000));
    }
}
