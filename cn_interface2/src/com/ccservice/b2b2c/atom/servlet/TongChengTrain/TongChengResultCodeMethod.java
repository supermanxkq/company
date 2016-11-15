package com.ccservice.b2b2c.atom.servlet.TongChengTrain;


import com.alibaba.fastjson.JSONObject;
import com.callback.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.util.OcsMethod;

public class TongChengResultCodeMethod {
    
    private static String keyCancelOrder="keyCancelOrder";//公共拼接参数    用于区分     防止重复         取消订单用
    
    private static String keyCancelChange="keyCancelChange";//公共拼接参数    用于区分     防止重复         取消改签
    
    /**
     * @param type    1    取消订单        2  取消改签
     * @param  orderid  订单号  
     * @result  key    放入ocs的key
     */
    public static String getkey(String orderid,int type){
        String key="";
        if(type==1){
            key=keyCancelOrder+orderid;
        }else if(type==2){
            key=keyCancelChange+orderid;
        }
        return key;
    }
    
    /**
     * 检测当前请求是否存在于ocs中
     * @param key 标识
     * @param  orderid  订单号  
     */
    public static Integer isYorM(String orderid,int type){
        Integer result=0;
        String key=getkey(orderid,type);
        String getS= OcsMethod.getInstance().get(key);//从ocs中获取信息    
        WriteLog.write("ocs步骤","ocs步骤1key"+ key);
//        String getS= MemCached.getInstance().get(key).toString();//从ocs中获取信息    
        if(ElongHotelInterfaceUtil.StringIsNull(getS)){
            //当前ocs中没有此数据       需插入   
            boolean rr =addocs(key);
            if(rr){
                result=0;//第一次   0
            }
        }else{
          //当前ocs中有此数据         判断是否超时      1  超时将它移除      建立新的此订单缓存     2 不超时   返回正在处理 
            JSONObject jj=JSONObject.parseObject(getS);
            long nowtime=System.currentTimeMillis();//当前时间
            long deltaT=(nowtime-jj.getLongValue("createtime"))/1000;//时间差  单位（秒）    当前请求和   ocs中 储存时间     
            if(deltaT<=20){
                result=2;//当前正在处理    中断程序    返回同步结果   
            }else{
                boolean rr= replaceocs(key);
                if(rr){
                    result=1;//超时   替换 继续程序    
                }
            }
        }
        WriteLog.write("ocs步骤", "ocs步骤4最终结果 "+result);
        return result;
    }
    
    public static boolean removeocs(String orderid,int type){
        boolean result=false;
        String key=getkey(orderid,type);
        result=deleteocs(key);
        return result;
    }
    
    /**
     * 向ocs中插入   数据
     * @param key 标识
     * @param  orderid  订单号  
     */
    public static boolean addocs(String key){
        long createtime=System.currentTimeMillis();
        JSONObject jss=new JSONObject();
        jss.put("createtime", createtime);
        boolean result= OcsMethod.getInstance().add(key, jss.toJSONString(), 60*60*24);
//        boolean result= MemCached.getInstance().add(key, jss, new Date());
        return result;
    }
    
    /**
     * 向ocs中替换   数据
     * @param key 标识
     * @param  orderid  订单号  
     */
    public static boolean replaceocs(String key){
        long createtime=System.currentTimeMillis();
        JSONObject jss=new JSONObject();
        jss.put("createtime", createtime);
        boolean result= OcsMethod.getInstance().replace(key, jss.toJSONString(), 60*60*24);//MemCached.getInstance().
//        boolean result= MemCached.getInstance().replace(key, jss, new Date());
        return result;
    } 
    
    /**
     * 删除   数据
     * @param key 标识
     * @param  orderid  订单号  
     */
    public static boolean deleteocs(String key){
        boolean result= OcsMethod.getInstance().remove(key);
//        boolean result= MemCached.getInstance().delete(key);
        return result;
    }
    
    
    public static String getmsg(String orderid){
        String result="";
        Integer iii= TongChengResultCodeMethod.isYorM(orderid, 1);
        if(iii==2){
            result="we can go go go !";
            return result;
        }
        try {
            Thread.sleep(15000);
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        result="you win!";
        TongChengResultCodeMethod.removeocs(orderid, 1);
        return result;
    }
    
    public static void main(String[] args) {
        String url = "http://120.26.100.206:19362/cn_interface/seshiocs";
//        String url = "http://localhost:1888/cn_interface/seshiocs";
        System.out.println("请求发送时间："+System.currentTimeMillis()+"：");
        String result = SendPostandGet.submitPost(url,"orderid=T3163652131qwea1B", "utf-8").toString();
        System.out.println(System.currentTimeMillis()+"结果："+result);
//        String orderid="T3163652131651A21B23ss";
//        System.out.println("请求发送时间："+System.currentTimeMillis()+"：");
//        String result =getmsg(orderid);
//        System.out.println(System.currentTimeMillis()+"结果："+result);
    }
}
