package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.callback.WriteLog;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.LoginRequest;

public class LoginBeforeMethod {

    //线程数量
    private final int threadNum = 50;

    //睡眠时间
    private final long threadTimes = 1000L;

    private int userNum = 1;

    /**
     * 取账号登陆 刷新db 
     */
    public void loginAll() {
        
        long minId = 0;
        int j =0;
        for (int i = 0; i < 10000; i++) {
            DBAccount db = new DBAccount();          
            List<Customeruser> users = db.getDBCanUseAccount(userNum, 15, 1, minId);
            j+=users.size();
            System.out.println("取出的账号数："+users.size()+"总共的账号数："+j);
            for (Customeruser customeruser : users) {
                if (customeruser.getId()>minId) {
                    minId = customeruser.getId();
                }
                WriteLog.write("当前登陆的ID", ""+customeruser.getId()+"");
            }
            WriteLog.write("取出的最后一个登陆的ID", ""+minId);
            if(users!=null&&!users.isEmpty()){              
                login(users);
            }else{
                break;
            }
            try {
                Thread.sleep(5000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
          
        }
        
        WriteLog.write("当前登陆的最后一个ID", ""+minId);
        
        
    }

    /**
     * 
     * @param users
     */
    public void login(List<Customeruser> users) {

        for (int i = 0; i < users.size(); i++) {

            if (i != 0 && i % threadNum == 0) {
                try {
                    Thread.sleep(threadTimes);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(!users.get(i).equals("")){                
                new LoginAndFreshEach(users.get(i)).start();
            }

        }

    }

    /**
     * 返回单个用户登陆结果
     * @param user
     * @return
     */
    public void loginBefor(Customeruser user) {

        LoginBeforePhone loginBeforePhone = new LoginBeforePhone(user);
        String accountParameters = loginBeforePhone.getAccountParameters();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("datatypeflag", "1012");
        parameters.put("accountPhone", accountParameters);
        RepServerBean repServerBean = RepServerUtil.getRepServer(user, false);
        String url ="";
        try {
           for (int i = 0; i < 3; i++) {
               url = repServerBean.getUrl();
               if(url!=null&&!"".equals(url)){
                   break;
               }
               
               try {
                   Thread.sleep(threadTimes);
               }
               catch (InterruptedException e) {
                   e.printStackTrace();
               }  
               
           }
           try {
               Thread.sleep(threadTimes);
           }
           catch (InterruptedException e) {
               e.printStackTrace();
           }
           
        }
        catch (Exception e) {
            
            WriteLog.write("登陆请求返回rep异常",""+e );
        }
        System.out.println(url);
        String loginedResult ="";
        try {
            
           WriteLog.write("登陆请求地址参数",url +" "+parameters);
           for (int i = 0; i < 15; i++) {
             if(url!=null&&!"".equals(url)){
                 
               loginedResult = RequestUtils.sendPost("http://localhost:9016/Reptile/traininit", parameters);   
               
               try {
                   Thread.sleep(threadTimes);
               }
               catch (InterruptedException e) {
                   e.printStackTrace();
               }
            }
            if(loginedResult!=null&&!"".equals(loginedResult)){
                
                WriteLog.write("登陆返回的数据", ""+loginedResult);
                break;
            }           
        }
            
        }
        catch (Exception e) {
            WriteLog.write("登陆请求返回数据异常",""+e );

        }        
        accountParameters = "&accountPhone=" + loginBeforePhone.getAccountParameters();
     
        System.out.println(loginedResult);
        TongchengSupplyMethod tongchengSupplyMethod = new TongchengSupplyMethod();
        
        if(loginedResult!=null&&!"".equals(loginedResult)&&loginedResult.contains("登录成功")){
            //tongchengSupplyMethod.freshPhone(accountParameters, loginedResult, user);           
            WriteLog.write("登陆返回所有数据", "参数："+accountParameters+"登陆结果："+loginedResult+"用户："+user);
        }else{
            WriteLog.write("登陆失败的所有数据", "参数："+accountParameters+"登陆结果："+loginedResult+"用户："+user);
        }

    }

    public static void main(String[] args) {

        LoginBeforeMethod loginBeforeMethod = new LoginBeforeMethod();
        loginBeforeMethod.loginAll();

    }

    public class LoginAndFreshEach extends Thread {
        private Customeruser customeruser;

        public LoginAndFreshEach(Customeruser customeruser) {
            this.customeruser = customeruser;
        }

        public void run() {
            
            loginBefor(customeruser);
        }
    }

}
