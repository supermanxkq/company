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

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TrainSearchThread;
import com.ccservice.b2b2c.base.customerpassenger.Customerpassenger;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.tenpay.util.MD5Util;

/**
 * mobileApp对常用旅客的操作接口
 * @time 2015年5月27日 下午7:33:38
 */
public class MobileAppPassagerOperation extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public String partnerid;// = "tongcheng_train";

    public String key;//= "x3z5nj8mnvl14nirtwlvhvuialo0akyt";

    //partnerid的对象集合
    Map<String, InterfaceAccount> interfaceAccountMap;

    //当前用户的集合
    Map<String, Customeruser> customeruserMap;

    //常用旅客的信息集合
    List<Customerpassenger> list;

    //当前用户的curphone:ID
    Map<String, String> idMap;

    public MobileAppPassagerMethod mobileapppassagermethod = new MobileAppPassagerMethod();

    public Customerpassenger customerpassenger = new Customerpassenger();

    public void init() throws ServletException {
        this.partnerid = this.getInitParameter("partnerid");
        this.key = this.getInitParameter("key");
        interfaceAccountMap = new HashMap<String, InterfaceAccount>();
        //        customeruserMap = new HashMap<String, Customeruser>();
        //        idMap = new HashMap<String, String>();
        //        list = new ArrayList<Customerpassenger>();
        super.init();
    }

    public MobileAppPassagerOperation() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset = UTF-8");
        response.setHeader("Content-Type", "text/html;charset = UTF-8");
        int r1 = new Random().nextInt(10000000);
        String result = "";
        PrintWriter out = response.getWriter();
        JSONObject obj = new JSONObject();
        String resultJSONString = "";
        try {
            //请求的数据
            String jsonStr = request.getParameter("jsonStr");
            WriteLog.write("移动客户端常用旅客", r1 + ":jsonStr:" + jsonStr);
            //对传过来的字符串进行非空的判断
            if (ElongHotelInterfaceUtil.StringIsNull(jsonStr)) {
                obj.put("success", "false");
                obj.put("code", "101");
                obj.put("msg", "传入的json为空对象");
                result = obj.toString();
                //初始化账号信息
                if ("1".equals(request.getParameter("initdata"))) {
                    new Thread(new TrainSearchThread(interfaceAccountMap, 2)).start();
                    interfaceAccountMap = new HashMap<String, InterfaceAccount>();
                    customeruserMap = new HashMap<String, Customeruser>();
                    resultJSONString = "OK";
                }
            }
            else {
                jsonStr = request.getParameter("jsonStr") == null ? "" : request.getParameter("jsonStr").trim();//去空格
                JSONObject jsonStrJSON = JSONObject.parseObject(jsonStr);
                //判断传过来的json字符串格式
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
                //判断partnerid中是否有非法的字符
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
                if ("".equals(keystr)) {
                    keystr.trim();
                }
                String keyString = partnerid + method + reqtime + MD5Util.MD5Encode(keystr, "UTF-8");
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
                else if (sign.equals(checkSign)) {
                    /**
                     * 常用旅客的添加
                     */
                    if ("add_method".equals(method)) {
                        if (!jsonStrJSON.containsKey("username") || !jsonStrJSON.containsKey("useridentitytype")
                                || !jsonStrJSON.containsKey("useridentity") || !jsonStrJSON.containsKey("usertype")) {
                            obj.put("success", false);
                            obj.put("code", "102");
                            obj.put("msg", "传入的json格式错误");
                            result = obj.toString();
                        }
                        String username = jsonStrJSON.getString("username");
                        String useridentitytype = jsonStrJSON.getString("useridentitytype");
                        String useridentity = jsonStrJSON.getString("useridentity");
                        String usertype = jsonStrJSON.getString("usertype");
                        if ("".equals(username) || "".equals(useridentitytype) || "".equals(useridentity)
                                || "".equals(usertype)) {
                            obj.put("success", false);
                            obj.put("code", "106");
                            obj.put("msg", "业务参数缺失");
                            result = obj.toString();
                        }
                        else {
                            //在添加之前首先查找当前账户下的常用旅客的是否存在
                            //查找当前账户的ID
                            long id = mobileapppassagermethod.getCustomeruserId(curphone);
                            list = mobileapppassagermethod.getCustomerPassenger(id);
                            if (list.size() > 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    customerpassenger = list.get(i);
                                    if (useridentity.equals(customerpassenger.getLivingcardnum())) {
                                        break;
                                    }

                                }
                                if (customerpassenger == null) {
                                    customerpassenger = mobileapppassagermethod.addPassger(username, useridentitytype,
                                            useridentity, usertype, curphone);
                                    if (customerpassenger != null) {
                                        obj.put("success", true);
                                        obj.put("code", "100");
                                        obj.put("msg", "处理或操作成功");
                                        result = obj.toString();
                                    }
                                }
                                else {
                                    obj.put("success", false);
                                    obj.put("code", "200");
                                    obj.put("msg", "此乘客已添加");
                                    result = obj.toString();

                                }

                            }
                            else {
                                customerpassenger = mobileapppassagermethod.addPassger(username, useridentitytype,
                                        useridentity, usertype, curphone);
                                if (customerpassenger != null) {
                                    obj.put("success", true);
                                    obj.put("code", "100");
                                    obj.put("msg", "处理或操作成功");
                                    result = obj.toString();
                                }

                            }

                        }

                    }
                    /**
                     * 删除常用旅客
                     */
                    else if ("delete_method".equals(method)) {
                        if (!jsonStrJSON.containsKey("ID")) {
                            obj.put("success", false);
                            obj.put("code", "102");
                            obj.put("msg", "传入的json格式错误");
                            result = obj.toString();
                        }
                        //要被删除常用旅客的ID
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
                            //删除数据库中的常用旅客
                            int n = mobileapppassagermethod.deletePassger(id);
                            //删除缓存中的常用旅客
                            if (n > 0) {
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
                     * 查看当前用户的常用旅客
                     */
                    else if ("select_method".equals(method)) {
                        //在数据库中查看
                        long id = mobileapppassagermethod.getCustomeruserId(curphone);
                        list = mobileapppassagermethod.getCustomerPassenger(id);
                        Customerpassenger customerpassenger = new Customerpassenger();
                        JSONArray jsa = new JSONArray();
                        if (list.size() > 0) {
                            for (int i = 0; i < list.size(); i++) {
                                JSONObject jso = new JSONObject();
                                customerpassenger = list.get(i);
                                String str = "";
                                if (customerpassenger.getState() == 0) {
                                    str = "待核验";
                                }
                                if (customerpassenger.getState() == -1) {
                                    str = "未通过";
                                }
                                if (customerpassenger.getState() == 1) {
                                    str = "通过";
                                }
                                jso.put("ID", customerpassenger.getId());
                                jso.put("username", customerpassenger.getUsername());
                                jso.put("useridentitytype", customerpassenger.getLivingcardtype());
                                jso.put("useridentity", customerpassenger.getLivingcardnum());
                                jso.put("usertype", customerpassenger.getTypestr());
                                jso.put("status", str);
                                jsa.add(jso);
                            }
                            obj.put("success", true);
                            obj.put("code", "100");
                            obj.put("msg", "处理或操作成功");
                            System.out.println("jsa：" + jsa.toString());
                            obj.put("data", jsa);
                            result = obj.toString();
                        }
                        else {
                            obj.put("success", false);
                            obj.put("code", "400");
                            obj.put("msg", "没有常用旅客");
                            result = obj.toString();
                        }

                    }
                    /**
                     * 修改常用旅客信息
                     */
                    else if ("update_method".equals(method)) {
                        if (!jsonStrJSON.containsKey("username") || !jsonStrJSON.containsKey("useridentitytype")
                                || !jsonStrJSON.containsKey("useridentity") || !jsonStrJSON.containsKey("usertype")
                                || !jsonStrJSON.containsKey("ID")) {
                            obj.put("success", false);
                            obj.put("code", "102");
                            obj.put("msg", "传入的json格式错误");
                            result = obj.toString();
                        }
                        String ID = jsonStrJSON.getString("ID");
                        String username = jsonStrJSON.getString("username");
                        String useridentitytype = jsonStrJSON.getString("useridentitytype");
                        String useridentity = jsonStrJSON.getString("useridentity");
                        String usertype = jsonStrJSON.getString("usertype");
                        if ("".equals(username) || "".equals(useridentitytype) || "".equals(useridentity)
                                || "".equals(usertype) || "".equals(ID)) {
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
                            Customerpassenger customerPassengernew = mobileapppassagermethod.updateCustomerPassenger(
                                    ID, username, useridentitytype, useridentity, usertype, curphone);

                            if (customerPassengernew != null) {
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
        catch (Exception e) {
            e.printStackTrace();
            obj.put("success", "false");
            obj.put("code", "108");
            obj.put("msg", "接口调用异常");
            result = obj.toString();
        }
        WriteLog.write("移动客户端常用旅客返回的结果", r1 + ":result:" + result);
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
