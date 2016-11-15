package com.ccservice.b2b2c.atom.mobileApp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.ccservice.b2b2c.base.useraddress.Useraddress;
import com.tenpay.util.MD5Util;

/**
 * 手机客户端邮寄地址
 * @time 2015年6月4日 下午4:27:46
 * @author baiyushan
 */
public class AddressAppRequest extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public String partnerid;// = "tongcheng_train";

    public String key;//= "x3z5nj8mnvl14nirtwlvhvuialo0akyt";

    //partnerid的对象集合
    Map<String, InterfaceAccount> interfaceAccountMap;

    public MobileAppPassagerMethod mobileapppassagermethod = new MobileAppPassagerMethod();

    //servlet的初始化
    public void init() throws ServletException {
        interfaceAccountMap = new HashMap<String, InterfaceAccount>();
    }

    public AddressAppRequest() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=utf-8");
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        response.setHeader("Content-Type", "text/html;charset = UTF-8");
        int r1 = new Random().nextInt(1000000);
        PrintWriter out = response.getWriter();
        String result = "";
        JSONObject obj = new JSONObject();

        try {
            //请求数据
            String jsonStr = request.getParameter("jsonStr");
            //由于手机客户端经常乱码所以提早进行decode
            //            if ("".equals(jsonStr)) {
            //                URLDecoder.decode(jsonStr, "utf-8");
            //            }
            WriteLog.write("移动客户端的邮寄地址请求", r1 + "jsonStr:" + jsonStr);
            //对传过来的jsonStr字符串进行非空判断
            if (ElongHotelInterfaceUtil.StringIsNull(jsonStr)) {
                obj.put("success", "false");
                obj.put("code", "101");
                obj.put("msg", "传入的json为空对象");
                result = obj.toString();
            }
            else {
                //给传过来的json字符串去除空格
                jsonStr = request.getParameter("jsonStr") == null ? "" : request.getParameter("jsonStr").trim();
                //将json字符串json化
                JSONObject jsonStrJSON = JSONObject.parseObject(jsonStr);
                if (!jsonStrJSON.containsKey("partnerid") || !jsonStrJSON.containsKey("method")
                        || !jsonStrJSON.containsKey("reqtime") || !jsonStrJSON.containsKey("sign")
                        || !jsonStrJSON.containsKey("curphone")) {
                    obj.put("success", false);
                    obj.put("code", "103");
                    obj.put("msg", "传入的 json格式错误");
                    result = obj.toString();
                }
                //获取的partnerid
                String partnerid = jsonStrJSON.getString("partnerid");
                if (partnerid.contains("'")) {
                    obj.put("success", false);
                    obj.put("code", "104");
                    obj.put("msg", "错误的通用参数");
                    result = obj.toString();
                }
                if ("".contains("partnerid")) {
                    obj.put("success", false);
                    obj.put("code", "103");
                    obj.put("msg", "通用参数缺失");
                    result = obj.toString();
                }
                InterfaceAccount interfaceAccount = interfaceAccountMap.get(partnerid);
                //缓存中没有这个partnerid账户信息则从数据库获取
                if (interfaceAccount == null) {
                    interfaceAccount = getInterfaceAccountByLoginname(partnerid);
                    if (interfaceAccount != null && interfaceAccount.getKeystr() != null) {
                        interfaceAccountMap.put(partnerid, interfaceAccount);
                    }
                }
                String method = jsonStrJSON.getString("method");
                String reqtime = jsonStrJSON.getString("reqtime");
                String sign = jsonStrJSON.getString("sign");
                String curphone = jsonStrJSON.getString("curphone");
                String keystr = interfaceAccount.getKeystr();
                if (!"".equals(keystr)) {
                    keystr.trim();
                }
                String keyString = partnerid + reqtime + MD5Util.MD5Encode(keystr, "UTF-8");
                String checkSign = MD5Util.MD5Encode(keyString, "UTF-8");
                //传过来的签名为空
                if ("".equals(sign) || "".equals(method) || "".equals(reqtime) || "".equals(curphone)) {
                    obj.put("success", false);
                    obj.put("code", "103");
                    obj.put("msg", "通用参数缺失");
                    result = obj.toString();
                }
                if (curphone.contains("'")) {
                    obj.put("success", false);
                    obj.put("code", "104");
                    obj.put("msg", "错误的通用参数");
                    result = obj.toString();
                }
                //判断签名是否通过
                else if (sign.equals(checkSign)) {
                    /**
                     * 邮寄地址的添加
                     */
                    if ("add_method".equals(method)) {
                        if (!jsonStrJSON.containsKey("username") || !jsonStrJSON.containsKey("userphone")
                                || !jsonStrJSON.containsKey("postcode") || !jsonStrJSON.containsKey("address")) {
                            obj.put("success", false);
                            obj.put("code", "102");
                            obj.put("msg", "传入的json格式错误");
                            result = obj.toString();
                        }
                        String username = jsonStrJSON.getString("username");
                        String userphone = jsonStrJSON.getString("userphone");
                        String postcode = jsonStrJSON.getString("postcode");
                        String address = jsonStrJSON.getString("address");
                        if ("".equals(username) || "".equals(userphone) || "".equals(postcode) || "".equals(address)) {
                            obj.put("success", false);
                            obj.put("code", "106");
                            obj.put("msg", "业务参数缺失");
                            result = obj.toString();
                        }
                        else {
                            int num = mobileapppassagermethod.addUserAddress(username, userphone, postcode, address,
                                    curphone);
                            if (num != -1) {
                                obj.put("success", true);
                                obj.put("code", "100");
                                obj.put("msg", "处理或操作成功");
                                result = obj.toString();
                            }
                            else {
                                obj.put("success", false);
                                obj.put("code", "200");
                                obj.put("msg", "添加失败");
                                result = obj.toString();
                            }

                        }

                    }
                    /*
                     * 邮寄地址的删除
                     */
                    else if ("delete_method".equals(method)) {
                        if (!jsonStrJSON.containsKey("ID")) {
                            obj.put("success", false);
                            obj.put("code", "102");
                            obj.put("msg", "传入的json格式错误");
                            result = obj.toString();
                        }
                        //要被邮寄地址的ID
                        String ID = jsonStrJSON.getString("ID");
                        if ("".equals(ID)) {
                            obj.put("success", false);
                            obj.put("code", "106");
                            obj.put("msg", "业务参数缺失");
                            result = obj.toString();
                        }
                        if (ID.contains("'")) {
                            obj.put("success", false);
                            obj.put("code", "109");
                            obj.put("msg", "错误的业务参数");
                            result = obj.toString();
                        }
                        else {
                            long id = Long.parseLong(ID);
                            //删除操作
                            int deletenum = mobileapppassagermethod.deleteAddress(id);
                            if (deletenum != -1) {
                                obj.put("success", true);
                                obj.put("code", "100");
                                obj.put("msg", "处理或操作成功");
                                result = obj.toString();
                            }
                            else {
                                obj.put("success", true);
                                obj.put("code", "300");
                                obj.put("msg", "删除失败");
                                result = obj.toString();
                            }
                        }
                    }
                    /**
                     * 查看邮寄地址信息
                     */
                    else if ("select_method".equals(method)) {
                        Useraddress useraddress = new Useraddress();
                        List<Useraddress> list = new ArrayList<Useraddress>();
                        list = mobileapppassagermethod.selectAddress(curphone);
                        JSONArray jsa = new JSONArray();
                        if (list.size() > 0) {
                            for (int i = 0; i < list.size(); i++) {
                                JSONObject jso = new JSONObject();
                                useraddress = list.get(i);
                                jso.put("ID", useraddress.getId());
                                jso.put("username", useraddress.getName());
                                jso.put("userphone", useraddress.getTel());
                                jso.put("postcode", useraddress.getPostalcode());
                                jso.put("address", useraddress.getAddress());
                                jsa.add(jso);
                            }
                            obj.put("success", true);
                            obj.put("code", "100");
                            obj.put("msg", "处理或操作成功");
                            obj.put("data", jsa);
                            result = obj.toString();
                        }
                        else {
                            obj.put("success", false);
                            obj.put("code", "400");
                            obj.put("msg", "没有邮寄地址");
                            result = obj.toString();

                        }

                    }
                    else if ("update_method".equals(method)) {
                        if (!jsonStrJSON.containsKey("username") || !jsonStrJSON.containsKey("userphone")
                                || !jsonStrJSON.containsKey("postcode") || !jsonStrJSON.containsKey("address")
                                || !jsonStrJSON.containsKey("ID")) {
                            obj.put("success", false);
                            obj.put("code", "102");
                            obj.put("msg", "传入的json格式错误");
                            result = obj.toString();
                        }
                        String ID = jsonStrJSON.getString("ID");
                        String username = jsonStrJSON.getString("username");
                        String userphone = jsonStrJSON.getString("userphone");
                        String postcode = jsonStrJSON.getString("postcode");
                        String address = jsonStrJSON.getString("address");
                        if ("".equals(username) || "".equals(userphone) || "".equals(postcode) || "".equals(address)
                                || "".equals(ID)) {
                            obj.put("success", false);
                            obj.put("code", "106");
                            obj.put("msg", "业务参数缺失");
                            result = obj.toString();
                        }
                        if (ID.contains("'")) {
                            obj.put("success", false);
                            obj.put("code", "109");
                            obj.put("msg", "错误的业务参数");
                            result = obj.toString();
                        }
                        else {
                            //在数据库中修改
                            long id = Long.parseLong(ID);
                            int num = mobileapppassagermethod.updateUseraddress(id, username, userphone, postcode,
                                    address);
                            if (num != -1) {
                                obj.put("success", true);
                                obj.put("code", "100");
                                obj.put("msg", "处理或操作成功");
                                result = obj.toString();
                            }
                            else {
                                obj.put("success", false);
                                obj.put("code", "300");
                                obj.put("msg", "修改失败");
                                result = obj.toString();
                            }

                        }

                    }
                    else {
                        obj.put("success", false);
                        obj.put("code", "105");
                        obj.put("msg", "接口不存在");
                        result = obj.toString();
                    }
                }
                else {
                    obj.put("success", false);
                    obj.put("code", "107");
                    obj.put("msg", "sign加密错误");
                    result = obj.toString();
                }

            }

        }
        catch (JSONException e) {
            //            e.printStackTrace();

            obj.put("success", false);
            obj.put("code", "507");
            obj.put("msg", "请求数据与格式有误");
            result = obj.toString();
        }
        catch (Exception e) {
            //            e.printStackTrace();
            obj.put("success", "false");
            obj.put("code", "108");
            obj.put("msg", "接口调用异常");
            result = obj.toString();
        }
        WriteLog.write("移动客户端邮寄地址返回的结果", r1 + ":result:" + result);
        out.print(result);
        out.flush();
        out.close();
    }

    /**
     * 根据用户名查找key
     * 
     * @param loginname
     * @return
     * @time 2015年5月28日 上午11:26:56
     * @author baiyushan
     */
    @SuppressWarnings("unchecked")
    private InterfaceAccount getInterfaceAccountByLoginname(String loginname) {
        List<InterfaceAccount> list_interfaceAccount = new ArrayList<InterfaceAccount>();
        InterfaceAccount interfaceAccount = new InterfaceAccount();
        try {
            list_interfaceAccount = Server.getInstance().getMemberService()
                    .findAllInterfaceAccount("where C_USERNAME = '" + loginname + "'", null, -1, 0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (list_interfaceAccount.size() > 0) {
            interfaceAccount = list_interfaceAccount.get(0);
        }
        else {
            if ("tongcheng_train".equals(loginname)) {
                interfaceAccount.setUsername("tongcheng_train");
                interfaceAccount.setKeystr(this.key);
            }
            else {
                interfaceAccount.setUsername(loginname);
                interfaceAccount.setKeystr("-1");
            }
            interfaceAccount.setInterfacetype(TrainInterfaceMethod.TONGCHENG);
        }
        return interfaceAccount;
    }
}
