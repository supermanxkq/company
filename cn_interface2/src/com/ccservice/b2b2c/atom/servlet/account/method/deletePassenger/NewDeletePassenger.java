package com.ccservice.b2b2c.atom.servlet.account.method.deletePassenger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.service12306.Account12306Util;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.servlet.account.DesUtil;
import com.ccservice.b2b2c.atom.train.idmongo.IDModel;
import com.ccservice.b2b2c.atom.train.idmongo.MongoHelper;
import com.ccservice.b2b2c.atom.train.idmongo.MongoLogic;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.util.db.DBHelperAccount;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;
import com.mongodb.DBObject;

public class NewDeletePassenger implements Callable<String> {

    public NewDeletePassenger() {
    }

    public NewDeletePassenger(String accountName, int id) {
        super();
        this.accountName = accountName;
        this.id = id;
    }

    @Override
    public String call() {
        try {
            deletePassenges(accountName, false, id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "删除成功!";
    }

    private static int count = 0;

    private final String[] two_isOpenClick = { "93", "95", "97", "99" };

    private final String[] other_isOpenClick = { "93", "98", "99", "91", "95", "97" };

    private String accountName;

    private int id;

    private static int pcounti = 0;

    /**
     * 判断mongo中是否有其他账号是否有此乘客
     * @param id
     * @param accountName
     */
    public boolean isExist(long id, String accountName) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put("IDNumber", id);
        List<DBObject> list = new ArrayList<DBObject>();
        try {
            list = MongoHelper.getInstance().find("CustomerUser", query);
        }
        catch (Exception e) {
        }
        WriteLog.write("删除常旅", "accountName---->" + accountName + "-->获取mongo账号list---->" + list.toString());
        int existNum = list.size();
        WriteLog.write("删除常旅", "accountName---->" + accountName + "-->existNum---->" + existNum);
        if (existNum > 0) {
            JSONArray pList = JSONArray.parseArray(list.toString());
            for (int i = 0; i < pList.size(); i++) {
                long IDNumber = pList.getJSONObject(i).getLong("IDNumber");
                String SupplyAccount = pList.getJSONObject(i).getString("SupplyAccount");
                WriteLog.write("删除常旅", "accountName---->" + accountName + "证件号---->" + IDNumber
                        + "-->SupplyAccount--->" + SupplyAccount);
                //是否账号是自己
                if (SupplyAccount != null && id == IDNumber && accountName.equals(SupplyAccount)) {
                    existNum--;
                    continue;
                }
                Customeruser customeruser = GetUserFromAccountSystem(SupplyAccount, false);
                long cid = customeruser.getId();
                if (cid == 1) {
                    WriteLog.write("删除常旅", "accountName---->" + accountName + "证件号---->" + IDNumber + "SupplyAccount:"
                            + SupplyAccount + "-->id--->" + id);
                    existNum--;
                    freeUserToAccountSystem(customeruser, AccountSystem.FreeNoCare);
                    continue;
                }
                int isenable = customeruser.getIsenable();
                WriteLog.write("删除常旅", "accountName---->" + accountName + "证件号---->" + IDNumber + "-->isenable--->"
                        + isenable);
                if (isenable != 1 && isenable != 3 && isenable != 4 && isenable != 8 && isenable != 61) { //账号不可用
                    existNum--;
                    freeUserToAccountSystem(customeruser, AccountSystem.FreeNoCare);
                    continue;
                }
                else {
                    //真正去核验是否账号被封
                    String cookie = customeruser.getCardnunber();//拿cookie
                    WriteLog.write("删除常旅", "accountName---->" + accountName + "证件号---->" + IDNumber + "-->cid-->" + cid
                            + "-->SupplyAccount:" + SupplyAccount + "-->cookie--->" + cookie);
                    if (cookie == null || "".equals(cookie)) {
                        freeUserToAccountSystem(customeruser, AccountSystem.FreeNoCare);
                    }
                    String url = RepServerUtil.getRepServer(customeruser, false).getUrl();
//                    String   url = "http://103.37.165.9:9090/Reptile/traininit";
                    String paramContent = "cookie=" + cookie + "&datatypeflag=1106";
                    String res = "";
                    try {
                        res = SendPostandGet.submitPost(url, paramContent, "utf-8").toString();
                    }
                    catch (Exception e) {
                        existNum--;
                        freeUserToAccountSystem(customeruser, AccountSystem.FreeNoCare);
                        continue;
                    }

                    WriteLog.write("删除常旅", "accountName---->" + accountName + "SupplyAccount:" + SupplyAccount
                            + "证件号---->" + IDNumber + "-->res--->" + res);
                    JSONObject Dres = new JSONObject();
                    try {
                        Dres = JSONObject.parseObject(res);
                    }
                    catch (Exception e) {
                    }
                    if (Dres == null || Dres.isEmpty()) {
                        existNum--;
                        freeUserToAccountSystem(customeruser, AccountSystem.FreeNoCare);
                        continue;
                    }
                    String flag = Dres.containsKey("flag") ? Dres.getString("flag") : "";
                    if (flag.equals("用户未登录")) {
                        customeruser.setDescription("未登录");
                        existNum--;
                        freeUserToAccountSystem(customeruser, AccountSystem.FreeNoLogin);
                        continue;
                    }
                    else if (flag.equals("手机待核验")) {
                        customeruser.setDescription("手机核验");
                        existNum--;
                        freeUserToAccountSystem(customeruser, 33);
                        continue;
                    }
                    else if (flag.equals("待核验")) {
                        customeruser.setDescription("您的身份信息未通过核验");
                        existNum--;
                        freeUserToAccountSystem(customeruser, 31);
                        continue;
                    }
                    else {
                        freeUserToAccountSystem(customeruser, AccountSystem.FreeNoCare);
                        break;

                    }
                }
            }
        }
        WriteLog.write("删除常旅", "accountName---->" + accountName + "-->existNum最终---->" + existNum);
        if (existNum > 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断时间是否可以删除
     * @param pObj 2015-09-04 00:00:00
     * @return
     */
    public boolean isCanDelete(String born_date) {
        SimpleDateFormat yms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -5); //得到当前时间前5天
        calendar.add(Calendar.MONTH, -6); //得到前6个月
        Date before6Time = calendar.getTime(); //当前时间的前6个月时间
        Date addTime = new Date();
        try {
            addTime = yms.parse(born_date);
        }
        catch (ParseException e) {
        }
        if (addTime.before(before6Time)) {
            return true;
        }
        return false;
    }

    /**
     * 核验状态
     * @param id_type
     * @param total_times
     * @return
     */
    public String checkType(String id_type, String total_times) {
        if ("1".equals(id_type)) {
            int a = two_isOpenClick.length;
            for (int d = 0; d < a; d++) {
                if (two_isOpenClick[d].equals(total_times)) {
                    return "已通过";
                }
            }
            if ("92".equals(total_times) || "98".equals(total_times)) {
                return "待核验";
            }
            else if ("91".equals(total_times)) {
                return "请报验";
            }
            else if ("94".equals(total_times)) {
                return "未通过";
            }
            return "未通过";
        }
        else {
            int a = other_isOpenClick.length;
            for (int d = 0; d < a; d++) {
                if (other_isOpenClick[d].equals(total_times)) {
                    return "已通过";
                }
            }
            return "未通过";
        }
    }

    public static void main(String[] args) {
		new NewDeletePassenger().deletePassenges("wuxueminfmusscnf", false, 000);
	}
    
    /**
     * 删除常旅
     * @param accountObject
     * @param isNeedDec 用户名是否需要解密
     * @return 
     */
    public void deletePassenges(String accountName, boolean isNeedDec, int minid) {
        try {
            accountName = DesUtil.decrypt(accountName, "A1B2C3D4E5F60708");
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        WriteLog.write("删除用户名", "accountName---->" + accountName + "minid-->" + id);
        System.out.println("accountName---->" + accountName + "--->minid---->" + id);
        System.out.println("计数：" + count++);
        WriteLog.write("删除常旅", "accountName---->" + accountName);
        int freeType = AccountSystem.FreeNoCare; //释放账号类型
        Customeruser customeruser = GetUserFromAccountSystem(accountName, isNeedDec);
        int isenable = customeruser.getIsenable();
        if (isenable != 1 && isenable != 3 && isenable != 4 && isenable != 8 && isenable != 61) { //账号不可用
            freeUserToAccountSystem(customeruser, freeType);
            return;
        }
        String cookie = customeruser.getCardnunber();//拿cookie
        WriteLog.write("删除常旅", "accountName---->" + accountName + "--->cookie：" + cookie);
        if (cookie == null || "".equals(cookie)) {
            freeUserToAccountSystem(customeruser, freeType);
            return;
        }
        String url = RepServerUtil.getRepServer(customeruser, false).getUrl();
//        String url ="http://103.37.165.9:9090/Reptile/traininit";
        String paramContent = "cookie=" + cookie + "&datatypeflag=1106";
        WriteLog.write("删除常旅", "accountName---->" + accountName + "--->rep请求的参数：" + paramContent + "----->url:" + url);
        String res = "";
        try {
            res = SendPostandGet.submitPost(url, paramContent, "utf-8").toString();
        }
        catch (Exception e) {
            freeUserToAccountSystem(customeruser, freeType);
            return;
        }
        WriteLog.write("删除常旅", "accountName---->" + accountName + "--->rep返回的结果：" + res);
        JSONObject Dres = new JSONObject();
        try {
            Dres = JSONObject.parseObject(res);
        }
        catch (Exception e) {
        }
        if (Dres == null || Dres.isEmpty()) {
            freeUserToAccountSystem(customeruser, freeType);
            return;
        }
        String flag = Dres.containsKey("flag") ? Dres.getString("flag") : "";
        //未登录
        boolean accountNoLogin = Account12306Util.accountNoLogin(res, customeruser);
        //释放账号
        if (accountNoLogin || "".equals(flag)) {
            customeruser.setDescription("未登录");
            if (accountNoLogin) {
                freeType = AccountSystem.FreeNoLogin;
            }
            freeUserToAccountSystem(customeruser, freeType);
            return;
        }
        else if (flag.equals("手机待核验")) {
            freeType = 33;
            customeruser.setDescription("手机核验");
            freeUserToAccountSystem(customeruser, freeType);
            return;

        }
        else if (flag.equals("待核验")) {
            freeType = 31;
            customeruser.setDescription("您的身份信息未通过核验");
            freeUserToAccountSystem(customeruser, freeType);
            return;
        }
        else {
            if (Dres == null || "-1".equals(Dres)) {
                freeType = AccountSystem.FreeNoCare;
                freeUserToAccountSystem(customeruser, freeType);
                return;
            }
            boolean success = Dres.containsKey("success") ? Dres.getBoolean("success") : false;

            JSONObject data = Dres.containsKey("data") ? Dres.getJSONObject("data") : new JSONObject();
            JSONArray passengers = new JSONArray();
            if (success) {
                try {
                    passengers = data.containsKey("datas") ? data.getJSONArray("datas") : new JSONArray();
                }
                catch (Exception e) {
                }
            }

            if (passengers == null || "".equals(passengers) || "[]".equals(passengers) || passengers.isEmpty()) {
                freeUserToAccountSystem(customeruser, freeType);
                return;
            }
            boolean isDelete = false; //是否进行了删除的操作
            for (int i = 0; i < passengers.size(); i++) {
                JSONObject passenger = passengers.getJSONObject(i);
                String isUserSelf = passenger.getString("isUserSelf");
                String total_times = passenger.getString("total_times");
                String born_date = passenger.getString("born_date");
                String id_type = passenger.getString("passenger_id_type_code");
                String passenger_id_no = passenger.getString("passenger_id_no");
                String passenger_type_name = passenger.getString("passenger_name");
                String checkType = checkType(id_type, total_times);
                passenger.put("cookie", cookie);
                if ("Y".equals(isUserSelf)) {
                    continue;
                }
                WriteLog.write("删除常旅", "accountName---->" + accountName + "-->核验类型--->" + checkType + "--->姓名："
                        + passenger_type_name + "证件号--->" + passenger_id_no + "---->添加的时间：" + born_date);
                if (checkType.equals("待核验") || checkType.equals("未通过")) { //可以直接删除
                    isDelete = true;
                    String deleTeRes = fromRepDelete(accountName, passenger.toString(), url);
                    if (deleTeRes.contains("用户未登录")) {
                        break;
                    }
                    continue;
                }
                //开启线程去转存乘客
                new MyThreadPassengerSaveDB(passenger).start();
                
                boolean isTimeCanDelete = isCanDelete(born_date); //时间上是否可以删除
                WriteLog.write("删除常旅", "accountName---->" + accountName + "-->核验类型--->" + checkType + "--->姓名："
                        + passenger_type_name + "证件号--->" + passenger_id_no + "---->时间是否允许删除：" + isTimeCanDelete);
                if (!isTimeCanDelete) {
                    continue;
                }
                long id = GetLongFromString(passenger_id_no);
                boolean isExist = isExist(id, accountName);
                //boolean isExist =true;

                WriteLog.write("删除常旅", "accountName---->" + accountName + "-->证件号--->" + passenger_id_no + "-->是否允许删除"
                        + isExist);
                if (isExist) { //可以删除
                    String deleTeRes = fromRepDelete(accountName, passenger.toString(), url);
                    if (deleTeRes.contains("用户未登录")) {
                        break;
                    }
                    else {
                        System.out.println("乘客删除数量：" + pcounti++);
                    }
                    isDelete = true;

                }
                else {
                }
            }
            if (isDelete) {
                for (int i = 0; i < 3; i++) {
                    paramContent = "cookie=" + cookie + "&datatypeflag=1106";
                    WriteLog.write("删除常旅", "accountName---->" + accountName + "isDelete-->" + isDelete
                            + "--->rep请求的参数：" + paramContent + "----->url:" + url);
                    res = SendPostandGet.submitPost(url, paramContent, "utf-8").toString();
                    WriteLog.write("删除常旅", "accountName---->" + accountName + "isDelete-->" + isDelete
                            + "--->rep返回的结果：" + res);
                    try {
                        Thread.sleep(4000l);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i == 2 && res.contains("获取信息成功")) {
                        break;
                    }
                }

                try {
                    Dres = JSONObject.parseObject(res);
                }
                catch (Exception e) {

                }
                if (Dres == null || Dres.isEmpty() || "-1".equals(Dres)) {
                    freeUserToAccountSystem(customeruser, freeType);
                    return;
                }
                try {
                    success = Dres.containsKey("success") ? Dres.getBoolean("success") : false;
                    data = Dres.containsKey("data") ? Dres.getJSONObject("data") : new JSONObject();
                }
                catch (Exception e) {
                    freeUserToAccountSystem(customeruser, freeType);
                    return;
                }
                passengers = new JSONArray();
                if (success) {
                    try {
                        passengers = data.containsKey("datas") ? data.getJSONArray("datas") : new JSONArray();
                    }
                    catch (Exception e) {

                    }
                }

                if (passengers == null || "".equals(passengers) || passengers.isEmpty()) {
                    freeUserToAccountSystem(customeruser, freeType);
                    return;
                }

                RefreshMongoByCustomerUser(accountName, passengers);
            }

            freeUserToAccountSystem(customeruser, AccountSystem.FreeNoCare);

        }

    }

    /**
     * 刷新mongo中的账号乘客
     * @param loginName
     * @param passengers
     */
    private void RefreshMongoByCustomerUser(String accountName, JSONArray passengers) {
        try {
            MongoLogic logic = new MongoLogic();
            List<IDModel> idList = new ArrayList<IDModel>();
            //解析12306数据，封装为Model，只取身份证
            for (int i = 0; i < passengers.size(); i++) {
                //JSON
                JSONObject passenger = passengers.getJSONObject(i);
                //Model
                IDModel idModel = new IDModel();
                //SET
                idModel.SetIDType(1);
                idModel.SetSupplyAccount(accountName);
                idModel.SetRealName(passenger.getString("passenger_name"));
                idModel.set_version(2);
                idModel.SetIDNumber(GetLongFromString(passenger.getString("passenger_id_no")));
                //ADD
                idList.add(idModel);
            }
            WriteLog.write("删除常旅", "accountName---->" + accountName + "--->刷新mongo中的乘客:" + idList.toString());
            logic.RefreshMongoByCustomerUser(accountName, idList);
        }
        catch (Exception e) {
            WriteLog.write("删除常旅", "accountName---->" + accountName + "--->刷新mongo中的乘客异常:" + e.getMessage());

        }
    }

    /**
     * 从rep 删除常旅
     * @param accountName
     * @param deletParam
     * @param url 
     * @return
     */
    public String fromRepDelete(String accountName, String deletParam, String url) {
        String paramContent = "passenger=" + deletParam + "&datatypeflag=1006";
        WriteLog.write("删除常旅", "accountName---->" + accountName + "--->删除常旅请求rep参数:" + paramContent + "--->url:" + url);
        WriteLog.write("删除的常旅", "accountName---->" + accountName + "--->删除常旅请求rep参数:" + paramContent + "--->url:" + url);
        String res = "";
        do {
            res = SendPostandGet.submitPost(url, paramContent, "utf-8").toString();
        }
        while (null == res || "".equals(res));
        WriteLog.write("删除常旅", "accountName---->" + accountName + "--->删除常旅请求rep结果:" + res);
        WriteLog.write("删除的常旅", "accountName---->" + accountName + "--->删除常旅请求rep结果:" + res);

        return res;
    }

    /**
     * 转换证件号
     * @param idStr
     * @return
     */
    private long GetLongFromString(String idStr) {
        long result = 0;
        idStr = idStr.toLowerCase();
        if (idStr.length() == 18) {
            if (idStr.endsWith("x")) {
                idStr = idStr.replaceAll("x", "10");
            }
            try {
                result = Long.parseLong(idStr);
            }
            catch (Exception ex) {
                result = 0;
            }
        }
        return result;
    }

    /**
     * 每次db拿200账号
     * @param minid
     * @author zyxu
     * @return
     */
    public JSONArray SelectAccount(int minid) {
        String selectSql = "[T_CUSTOMERUSER_select] @minId=" + minid;
        JSONArray jsonArray = new JSONArray();
        DataTable dataTable = DBHelperAccount.GetDataTable(selectSql);
        if (dataTable != null) {
            for (DataRow dataRow : dataTable.GetRow()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("accountId", dataRow.GetColumnInt("ID"));
                jsonObject.put("accountName", dataRow.GetColumnString("C_LOGINNAME"));
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * 账号系统拿账号
     * @param loginname
     * @param isNeedDec 用户名是否需要解密
     * @author zyxu
     * @return
     */
    public Customeruser GetUserFromAccountSystem(String loginname, boolean isNeedDec) {
        TongchengSupplyMethod t = new TongchengSupplyMethod();
        if (isNeedDec) {
            try {
                loginname = DesUtil.decrypt(loginname, "A1B2C3D4E5F60708");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        Customeruser c = t.GetUserFromAccountSystem(AccountSystem.LoginNameAccount, loginname,
                !AccountSystem.waitWhenNoAccount, AccountSystem.NullMap);
        return c;
    }

    /**
     * 释放账号
     * @param user
     */
    public void freeUserToAccountSystem(Customeruser user, int freeType) {
        TongchengSupplyMethod t = new TongchengSupplyMethod();
        t.freeCustomeruser(user, freeType, AccountSystem.OneFree, AccountSystem.ZeroCancel,
                AccountSystem.NullDepartTime);
    }

}
