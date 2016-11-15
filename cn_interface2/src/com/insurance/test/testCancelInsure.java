package com.insurance.test;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.IAtomService;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.service.IAirService;
import com.ccservice.b2b2c.base.service.ISystemService;

public class testCancelInsure {
    private static final String interface_url_hyx = "http://120.26.100.206:59025/cn_interface/service/";

    private static final String service_url_db = "http://121.40.174.4:49001/cn_service/service/";

    private static final String str_old_insure_endid = "1";

    public static void main(String[] args) {
        int r1 = (int) (Math.random() * 1000000);
        System.out.println(cancelOrderAplylist("BA201506141219257692", "", r1));
    }

    /**
     * 查询保险的cn_service地址
     * 
     * @return
     * @time 2015年5月25日 下午3:44:20
     * @author Auser
     */
    public static IAirService getAirService() {
        HessianProxyFactory factory = new HessianProxyFactory();
        try {
            return (IAirService) factory.create(IAirService.class, service_url_db + IAirService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 更新保险的cn_service地址
     * 
     * @return
     * @time 2015年5月25日 下午3:44:20
     * @author Auser
     */
    public static ISystemService getSystemService() {
        HessianProxyFactory factory = new HessianProxyFactory();
        try {
            return (ISystemService) factory.create(ISystemService.class,
                    service_url_db + ISystemService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
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
        try {
            return (IAtomService) factory.create(IAtomService.class,
                    interface_url_hyx + IAtomService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据投保时间确定新老保险
     * 获取对应的项目地址
     * @return
     * @time 2015年5月25日 下午2:40:35
     * @author fiend
     * @throws Exception 
     */
    public static IAtomService getInsrueAtomService(long insureid) throws Exception {
        //        if (insureid > Long.valueOf(str_old_insure_endid)) {
        WriteLog.write("Taobao_tuibao", insureid + ":火意险");
        return gethyxInsureAtomService();
        //        }
        //        else {
        //            WriteLog.write("Taobao_tuibao", insureid + ":航意险");
        //            return Server.getInstance().getAtomService();
        //        }
    }

    /**
     * 退保
     * @param policyno
     * @param cancelReason
     * @param r1
     * @return
     */
    public static JSONObject cancelOrderAplylist(String policyno, String cancelReason, int r1) {
        //TODO 退保需要修改

        JSONObject jsonObject = new JSONObject();
        long insereid = qorderids(policyno);
        if (0 == insereid) {
            jsonObject.put("success", false);
            jsonObject.put("code", 108);
            jsonObject.put("msg", "退保0接口参数校验错误");
        }
        else {

            Insuruser insuruser = getAirService().findInsuruser(insereid);

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
                        getAirService().updateInsuruser(insuruser);
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
     * 退保订单查询
     * @param policyno
     * @return
     */
    public static long qorderids(String policyno) {

        String sql = "SELECT ID FROM T_INSURUSER WHERE C_POLICYNO='" + policyno + "'";
        try {
            List list = getSystemService().findMapResultBySql(sql, null);
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

}
