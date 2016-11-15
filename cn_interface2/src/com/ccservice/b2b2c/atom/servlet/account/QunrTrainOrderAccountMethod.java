package com.ccservice.b2b2c.atom.servlet.account;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.callback.WriteLog;
import com.ccservice.b2b2c.atom.train.idmongo.MongoLogic;
import com.ccservice.b2b2c.util.db.DBHelperAccount;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;
import com.mongodb.DBObject;

public class QunrTrainOrderAccountMethod {

    
    public static void main(String[] args) {
       new QunrTrainOrderAccountMethod().operation();
    }
   
     private void operation() {
         int minid = 11846103;
         while (true) {
         System.out.println(minid);
         WriteLog.write("sss最后的一个minid", minid+"");
         JSONArray jsonArray = getAccountByDb(minid);
         if(jsonArray.size()==0){
             try {
                 Thread.sleep(100000L);
             }
             catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
         for (int i = 0; i < jsonArray.size(); i++) {
             if (jsonArray.getJSONObject(i).containsKey("accountId")
                     && jsonArray.getJSONObject(i).getIntValue("accountId") > minid) {
                 minid = jsonArray.getJSONObject(i).getIntValue("accountId");
                 WriteLog.write("最后的一个minid", minid+"");    
             }
             
             getAccountByMongo(jsonArray.getJSONObject(i));
     
         }
         try {
             Thread.sleep(100L);
         }
         catch (InterruptedException e) {
             e.printStackTrace();
         }
     }
        
     }
    /**
     * 获取ID
     * 
     * @param minid 当前同步的最后一个ID
     * @return
     * @time 2016年3月29日 下午2:12:21
     * @author fiend
     */
    private JSONArray getAccountByDb(int minid) {
        JSONArray jsonArray = new JSONArray();
        DataTable dataTable = DBHelperAccount.GetDataTable("[T_CUSTOMERUSER_select] @minId=" + minid, null);
        for (DataRow dataRow : dataTable.GetRow()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accountId", dataRow.GetColumnInt("ID"));
            jsonObject.put("accountName", dataRow.GetColumnString("C_LOGINNAME"));
            jsonArray.add(jsonObject);
        }    
        return jsonArray;
    }
    
  /**
   * Mongo 拿用户
   * @param jsonObjectAccount
   */
    private void getAccountByMongo(JSONObject jsonObjectAccount) {
       
        try {
            int accountid = jsonObjectAccount.getIntValue("accountId");
            List<DBObject> list = new MongoLogic().FindMongoByCustomerUser(jsonObjectAccount.getString("accountName")); 
            for (DBObject dbObject : list) {
                JSONObject jsonObject = new JSONObject();
                JSONObject dbjson = new JSONObject();
                try {            
                   dbjson = JSONObject.parseObject(dbObject.toString());
                 }
                 catch (Exception e) {
                   e.printStackTrace();
                }
                long passportseno= dbjson.getLongValue("IDNumber");
                String passengersename =dbjson.getString("RealName");
                String passengertypeid =dbjson.getString("IDType");
               
                String s =  passengertypeid +","+passengersename+","+getPassengertypeid(passportseno)+","+accountid;
                write(s);
              
                
            }
          } catch (Exception e) {
            e.printStackTrace();
        }
       
    }
    
    /**
     * 转换身份证号
     * @param id
     * @return
     */
    public static String getPassengertypeid(long id){
        String tyId = String.valueOf(id);
        if(tyId.length()==19){
            tyId = tyId.substring(0,tyId.length()-2)+"X";
        }
        return tyId;
        
    }
    
    
    
    private void write(String s){
        String path = "D:/userlog/";
        File fileParentDir = new File(path);

        if (!fileParentDir.exists()) {
            fileParentDir.mkdirs();
        }
        String logFilePathName = path + "/" +"同步的数据.txt";

        PrintWriter printWriter;
        try {
            if(new File(logFilePathName).exists()){
                printWriter = new PrintWriter(new FileOutputStream(logFilePathName, true));
                printWriter.println(s);
                printWriter.flush();
            }
          
        }
        catch (FileNotFoundException e) {          
            e.printStackTrace();
        }    
    }
}
