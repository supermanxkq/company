package com.ccservice.b2b2c.atom.servlet.TongChengTrain;
import com.ccservice.b2b2c.base.customeruser.Customeruser;

public class LoginBeforePhone {
    Customeruser user;  //用户
  
    String accountParameters;  //accountPhone 参数
    
    String loginedResult;   //结果

    public LoginBeforePhone() {

    }
    
    public LoginBeforePhone( Customeruser user) {
        super();
        this.user = user; 
        this.accountParameters = setAccountParameters(user);
    }
    
    public Customeruser getUser() {
        return user;
    }
      
    public void setUser(Customeruser user) {
        this.user = user;
    }
    
      
    public void setAccountParameters() {
        TongchengSupplyMethod tongchengSupplyMethod = new TongchengSupplyMethod();       
        this.accountParameters =tongchengSupplyMethod.CommonAccountPhone(user);      
      
    }
    
    
    public String setAccountParameters(Customeruser user) {
        TongchengSupplyMethod tongchengSupplyMethod = new TongchengSupplyMethod();       
        return tongchengSupplyMethod.CommonAccountPhone(user);      
      
    }
  
    public String getAccountParameters() {
        return accountParameters;
    }
  
    
    
    public String getLoginedResult() {
        return loginedResult;
    }

    public void setLoginedResult(String loginedResult) {
        this.loginedResult = loginedResult;
    }
    
    
    
    

 
}
