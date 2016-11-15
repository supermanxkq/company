package com.ccservice.b2b2c.atom.servlet.wanda;

import com.alibaba.fastjson.JSONObject;

public class WanDaCode {

    public static JSONObject limaohuantaizi(JSONObject json,Integer type){
        String msg="";
        if(type==1){
            msg=json.getString("msg");
        }else if(type==2){
            msg=json.getString("returnmsg");
        }
        if(msg.contains("密码输入错误")){
            json.put("msg", msg);
            json.put("code", 901);
        }else if(msg.contains("户名不存在")){
            json.put("msg", msg);
            json.put("code", 902);
        }else if(msg.contains("该邮箱不存在")){
            json.put("msg", msg);
            json.put("code", 903);
        }else if(msg.contains("您的手机号码尚未进行核验，目前暂无法用于登录")){
            json.put("msg", msg);
            json.put("code", 904);
        }
        return json;
    }
    
    public static void main(String[] args) {
        JSONObject jj=new JSONObject();
        jj.put("code", 999);
        jj.put("msg", "取消订单失败>>>您的手机号码尚未进行核验，目前暂无法用于登录，请您先使用用户名或邮箱登录，然后选择手机核验，核验通过后即可使用手机号码登录功能，谢谢。");
        jj.put("returnmsg", "该邮箱不存在。");
        System.out.println(limaohuantaizi(jj,1).toJSONString());
    }
}
