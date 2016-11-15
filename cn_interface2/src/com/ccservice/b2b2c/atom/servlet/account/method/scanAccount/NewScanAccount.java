package com.ccservice.b2b2c.atom.servlet.account.method.scanAccount;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;







import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.servlet.account.DesUtil;
import com.ccservice.b2b2c.atom.train.idmongo.MongoHelper;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.db.DBHelperAccount;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;
import com.mongodb.DBObject;
import com.sun.jmx.snmp.Timestamp;
import com.sun.org.apache.xml.internal.security.Init;
/**
 * 扫描账号(被封，删除mongo中的账号)
 * @param accountName
 * @param id 
 * @return  朱李旭
 */
public class NewScanAccount implements Callable<String> {
    
    private String accountName;
    
    private String collection = "CustomerUser";
    
    private int id;
    
    private String LOGNAME="扫描账号";
    
    private final Map<String, String> NullHeader = new HashMap<String, String>();//空请求头
    
    private static final int repTimeOut = Integer.parseInt("35") * 1000;//REP超时时间，单位：毫秒
    
    public NewScanAccount(String accountName,int id){
        super();
        this.accountName=accountName;
        this.id=id;
    }
    
    
    /**
     * 扫描账号(被封，删除mongo中的账号)
     * @param accountObject
     * @param isNeedDec 用户名是否需要解密
     * @return  朱李旭
     */
    public void  deleteMongoAccount(String accountName,boolean isNeedDec,int minid){
            try {
                accountName = DesUtil.decrypt(accountName, "A1B2C3D4E5F60708");
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException(LOGNAME, e);
            }
            WriteLog.write(LOGNAME, "accountName---->" + accountName + "minid-->" + id);
            System.out.println("扫描账号accountName---->" + accountName + "--->minid---->" + id);
            WriteLog.write(LOGNAME, "accountName---->" + accountName);
            int freeType = AccountSystem.FreeNoCare; //释放账号类型
            Customeruser customeruser = GetUserFromAccountSystem(accountName, isNeedDec);
            
            String cookie = customeruser.getCardnunber();//拿cookie
            WriteLog.write(LOGNAME, "accountName---->" + accountName + "--->cookie：" + cookie +"customeruser---->"+ customeruser);
            if (cookie == null || "".equals(cookie)) {
                freeUserToAccountSystem(customeruser, freeType);
                return;
            }
            String url = RepServerUtil.getRepServer(customeruser, false).getUrl();
              url = "http://103.37.149.129:9090/Reptile/traininit";
            //请求参数
            JSONObject callBackJson = new JSONObject();
            callBackJson.put("newMethod", true);
            callBackJson.put("keyWords", "快速注册");
            String param = "datatypeflag=3&cookie=" + cookie + "&callBackJson=" + callBackJson;
            WriteLog.write(LOGNAME, "accountName---->" + accountName + "--->rep请求的参数：" + param + "----->url:" + url);
            String res ="";
            try {
                res = SendPostandGet.post(url, param, "utf-8",NullHeader,repTimeOut).toString();
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("扫描账号--post请求---返回值异常", e);
                WriteLog.write(LOGNAME, "post---->" + res + "--->rep请求的参数：" + param + "----->url:" + url);
            }
            String msg="";
            //解析结果
            if (!ElongHotelInterfaceUtil.StringIsNull(res)) {
                try {
                    msg = JSONObject.parseObject(res).getString("msg");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    ExceptionUtil.writelogByException("扫描账号--res"+res+"json转换-----异常", e);
                }
            }
            if(msg.equals("用户未登录")){
                freeType= AccountSystem.FreeNoLogin;
                freeUserToAccountSystem(customeruser, freeType);
                return;
            }
            else if(msg.equals("待核验")){//删除mongo
                freeType=AccountSystem.FreeNoCheck;
                freeUserToAccountSystem(customeruser, freeType);
                try {
                    WriteLog.write(LOGNAME, "删除账号--(待核验)----->"+accountName+"被封");
                    DelAccount(accountName);
                    WriteLog.write(LOGNAME, "删除账号--(待核验)----->"+accountName+"被封");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    ExceptionUtil.writelogByException("扫描账号--删除账号--(待核验)-----异常", e);
                    WriteLog.write(LOGNAME, "删除账号--(待核验)----->失败"+accountName+"error"+e);
                }
                return;
            }
            else if(msg.equals("手机待核验")){//删除mongo
                freeType=33;
                freeUserToAccountSystem(customeruser, freeType);
                try {
                    WriteLog.write(LOGNAME, "删除账号---(手机待核验)---->"+accountName+"被封");
                    DelAccount(accountName);
                    WriteLog.write(LOGNAME, "删除账号---(手机待核验)---->"+accountName+"被封");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    ExceptionUtil.writelogByException("扫描账号--删除账号---(手机待核验)-----异常", e);
                    WriteLog.write(LOGNAME, "删除账号---(手机待核验)---->失败"+accountName+"error");
                }
                return;
            }
            else{
                freeUserToAccountSystem(customeruser, freeType);
                WriteLog.write(LOGNAME, "账号---->"+accountName+"其他返回值----->"+res+"msg----->"+msg);
                
            }
            
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
    
    /**
     * 账号系统拿账号
     * @param loginname
     * @param isNeedDec 用户名是否需要解密
     * @author zlx
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
                WriteLog.write("扫描账号","账号系统拿账号---------------->"+loginname);
            }
        }
        Customeruser c = t.GetUserFromAccountSystem(AccountSystem.LoginNameAccount, loginname,
                !AccountSystem.waitWhenNoAccount, AccountSystem.NullMap);
        return c;
    }
    
    /**
     * mongo删除一个12306账户（某12306账户被封禁或被找回时调用，注意超过30常旅不必调用此方法，必须是完全无法使用时才可调用）
     * 
     * @param loginName
     * @throws Exception 
     */
    public static void DelAccount(String loginName) throws Exception {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put("SupplyAccount", loginName);
        WriteLog.write("扫描账号","删除mongo账号---------------->"+loginName);
        MongoHelper.getInstance().delete("CustomerUser", query);
        WriteLog.write("扫描账号","删除mongo账号---------------->"+loginName);
    }
    
    /**
     * mongo中查询账号
     * @param accountName
     * @return
     */
    public static List<DBObject> FindMongoByCustomerUser(String loginName) throws Exception {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put("SupplyAccount", loginName);
        List<DBObject> havedUsers = MongoHelper.getInstance().find("CustomerUser", query);
        if (havedUsers != null && havedUsers.size() > 0) {
            return havedUsers;
        }
        return new ArrayList<DBObject>();

    }
    
    public static int ceshi(String logname){
        try {
            DelAccount(logname);
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
    
    public static void main(String[] args) {
//        int minid=10549878;
//        JSONArray jsonary=new JSONArray();
//        jsonary=SelectAccount(minid);
//        int jsonsize=jsonary.size();
//        for(int i=0;i<jsonsize;i++){
//            String accountName=null;
//            try {
//                accountName = DesUtil.decrypt(
//                        jsonary.getJSONObject(i).getString("accountName"), "A1B2C3D4E5F60708");
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//            int id = jsonary.getJSONObject(i).getIntValue("accountId");
//            if (jsonary.getJSONObject(i).containsKey("accountId")
//                    && jsonary.getJSONObject(i).getIntValue("accountId") > minid) {
//                minid = jsonary.getJSONObject(i).getIntValue("accountId");
//            }
//            List list=null;
//            try {
//                list=FindMongoByCustomerUser(accountName);
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//            System.out.println(list.size());
//            
////            int a=ceshi(logname);
////            if(a==1){
////                System.out.println("成功！");
////            }
////            try {
////                list=FindMongoByCustomerUser(logname);
////            }
////            catch (Exception e) {
////                e.printStackTrace();
////            }
////            System.out.println(list.size());
//            
//            new NewScanAccount(accountName, id).run();
//        }
////        JSONObject obj=new JSONObject();
////        obj.put("aa", "asfasf");
////        String saf=obj.getString("bb");
////        System.out.println(saf);
        Date date=new Date();
        System.out.println(date.getHours());
  
    }
    
    public static JSONArray SelectAccount(int minid) {
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
    @Override
    public String call() throws Exception {
        try {
            
            deleteMongoAccount(accountName, false, id);
        }
        catch (Exception e) {
            e.printStackTrace();
            WriteLog.write(LOGNAME, "扫描异常=====accountName---->" + accountName + "minid-->" + id);
        }
        return "扫描账号成功！";
    }

}
