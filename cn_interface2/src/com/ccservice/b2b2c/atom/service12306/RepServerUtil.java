package com.ccservice.b2b2c.atom.service12306;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import com.weixin.util.RequestUtil;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.account.dubbo.util.DubboConsumer;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;

/**
 * REP工具类
 * @author WH
 * @time 2016年1月21日 上午11:24:02
 * @version 1.0
 */

public class RepServerUtil {

    /**
     * 未登录判断超时
     */
    private static final String NoLoginTimeOut = "TaoBao_NoLoginTimeOut";

    /**
     * 未登录次数KEY
     */
    private static final String NoLoginTryCount = "TaoBao_NoLoginTryCount";

    /**
     * REP相关数据配置
     */
    public static Map<String, Integer> repData = new HashMap<String, Integer>();

    /**
     * 取配置表
     */
    @SuppressWarnings("unchecked")
    private int getConfig(String key) {
        //结果
        int result = 0;
        //捕捉
        try {
            List<Sysconfig> configs = Server.getInstance().getSystemService()
                    .findAllSysconfig("WHERE C_NAME = '" + key + "'", "", -1, 0);
            //查询成功
            if (configs != null && configs.size() > 0) {
                result = Integer.parseInt(configs.get(0).getValue());
            }
        }
        catch (Exception e) {
        }
        //返回
        return result;
    }

    /**
     * 获取淘宝托管专用REP，REP类型固定为2>>新逻辑，判断是否有可用的REP
     */
    public static RepServerBean getTaoBaoTuoGuanRepServer(Customeruser user, boolean isDama) {
        //结果
        RepServerBean result = new RepServerBean();
        //判断
        if (user != null && !ElongHotelInterfaceUtil.StringIsNull(user.getCardnunber())) {
            //Cookie
            String cookie = user.getCardnunber();
            //登录参数
            String param = "datatypeflag=3&cookie=" + cookie;
            //走个人信息
            JSONObject callBackJson = new JSONObject();
            callBackJson.put("newMethod", true);
            callBackJson.put("keyWords", "快速注册");
            //拼接回调参数
            param += "&callBackJson=" + callBackJson;
            //REP超时，单位：毫秒
            if (!repData.containsKey(NoLoginTimeOut)) {
                //取数据
                int config = new RepServerUtil().getConfig(NoLoginTimeOut);
                //内存赋值
                repData.put(NoLoginTimeOut, config > 0 ? config : 5000);
            }
            //尝试次数
            if (!repData.containsKey(NoLoginTryCount)) {
                //取数据
                int config = new RepServerUtil().getConfig(NoLoginTryCount);
                //内存赋值
                repData.put(NoLoginTryCount, config > 0 ? config : 5);
            }
            //超时时间
            int timeout = repData.get(NoLoginTimeOut);
            //尝试次数(-1>>没有可用最终还要拿一次)
            int tryCount = repData.get(NoLoginTryCount) - 1;
            //公共方法
            TongchengSupplyMethod supplyMethod = new TongchengSupplyMethod();
            //请求头
            Map<String, String> header = new HashMap<String, String>();
            //循环尝试
            for (int i = 0; i < tryCount; i++) {
                try {
                    //取REP
                    RepServerBean rep = getTaoBaoTuoGuanRepServer(isDama);
                    //类型正确
                    if (rep.getType() == 1) {
                        //REP
                        String url = rep.getUrl();
                        //参数
                        String data = param + supplyMethod.JoinCommonAccountInfo(user, rep);
                        //请求REP
                        String repResult = RequestUtil.post(url, data, "utf-8", header, timeout);
                        //结果为空
                        if (ElongHotelInterfaceUtil.StringIsNull(repResult)) {
                            continue;
                        }
                        //非用户未登录
                        if (!Account12306Util.accountNoLogin(repResult, user)) {
                            //结果
                            result = rep;
                            //中断
                            break;
                        }
                    }
                }
                catch (Exception e) {
                }
            }
        }
        //返回
        return ElongHotelInterfaceUtil.StringIsNull(result.getUrl()) ? getTaoBaoTuoGuanRepServer(isDama) : result;
    }

    /**
     * 切换REP>>淘宝托管，先用类型为1的，如果未登录切到类型为2的重试
     */
    public static boolean changeRepServer(Customeruser user) {
        return getType(user) == 1;
    }

    /**
     * 获取淘宝托管专用REP，REP类型固定为2
     */
    private static RepServerBean getTaoBaoTuoGuanRepServer(boolean isDama) {
        //类型
        int tuoGuanType = 2;
        //获取
        RepServerBean rep = getRepServer(tuoGuanType, isDama, new HashMap<String, String>());
        //重置
        rep.setType(rep.getType() == tuoGuanType ? 1 : rep.getType());
        //返回
        return rep;
    }

    /**
     * 用同一个REP>>后续业务用登录绑定的REP
     */
    public static boolean UseSameRep(Customeruser user) {
        return user != null && !ElongHotelInterfaceUtil.StringIsNull(user.getMemberemail());
    }

    /**
     * 获取REP：非PC端登录12306账号后才能用的业务；
     * 可调用此方法的业务例如：途牛客人账号走手机端验证；
     * 使用完必须调用[freeRepServer(RepServerBean rep)方法]释放
     */
    public static RepServerBean dontNeedLoginRepServer() {
        Map<String, String> backup = new HashMap<String, String>();
        backup.put("special", "true");
        return getRepServer(getType(new Customeruser()), false, backup);
    }

    /** 
     * 通过账号获取REP>>要登录操作的业务，如下单、改签、退票、审核、支付链接、取消等
     * @author WH
     * @param isDama 是否是打码
     * @param user 下单账号，目前用账号标识服务器类型>>1：淘宝客人账号订单、改签、退票；其他：默认
     */
    public static RepServerBean getRepServer(Customeruser user, boolean isDama) {
        /**新逻辑**/
        //用同一个REP
        if (UseSameRep(user)) {
            return parseToRepServer(user.getMemberemail(), isDama);
        }
        //走老逻辑
        else {
            return getRepServer(getType(user), isDama, new HashMap<String, String>());
        }
    }

    /**
     * 用同一个REP>>通过账号系统释放REP，单独释放REP走该方法（如下单完要等待出票不释放账号，但要释放REP）
     * @author WH
     */
    public static void freeRepServer(RepServerBean rep) {
        //要释放REP
        if (rep != null && rep.isNeedFreeRep() && rep.getId() > 0) {
            //JSON
            JSONObject data = AccountSystemParam("FreeRep");
            //RepId
            data.put("id", rep.getId());
            //Dubbo
            try {
                DubboConsumer.getInstance().getDubboAccount().freeRepServer(data);
            }
            catch (Exception e) {

            }
            /*
            //请求参数
            String param = "param=" + data.toJSONString();
            //请求地址
            String url = PropertyUtil.getValue("12306AccountUrl", "Train.properties");
            //POST请求
            RequestUtil.post(url, param, "UTF-8", new HashMap<String, String>(), 0);
            */
        }
    }

    /**
     * 用同一个REP>>获取账号绑定的REP，再通过账号系统释放REP，单独释放REP走该方法（如下单完要等待出票不释放账号，但要释放REP）
     * @param user 12206账号
     */
    public static void freeRepServerByAccount(Customeruser user) {
        //用同一个REP
        if (UseSameRep(user)) {
            //先取再释放
            freeRepServer(parseToRepServer(user.getMemberemail(), false));
        }
    }

    /**
     * 通过Customeruser判断服务器类型，暂针对淘宝客人账号有IP地址
     */
    private static int getType(Customeruser user) {
        //IP非空且用.分隔后是4位
        return user != null && user.getPostalcode() != null && user.getPostalcode().split("\\.").length == 4 ? 1 : 0;
    }

    /**
     * 获取REP
     * @author WH
     * @param backup 备用字段
     * @param isDama 是否是打码
     * @param type>>服务器类型>>1：淘宝客人账号订单、改签、退票；其他：默认
     */
    private static RepServerBean getRepServer(int type, boolean isDama, Map<String, String> backup) {
        //为空时设置
        if (backup == null) {
            backup = new HashMap<String, String>();
        }
        //JSON
        JSONObject data = AccountSystemParam("GetRep");
        //备用字段
        for (String key : backup.keySet()) {
            data.put(key, backup.get(key));
        }
        data.put("type", type);
        data.put("isDama", isDama);
        /*
        //请求参数
        String param = "param=" + data.toJSONString();
        //请求地址
        String url = GetRepSystemUrl();
        */
        //POST请求
        String ret = "";
        //多次尝试
        for (int i = 0; i < 3; i++) {
            //Dubbo
            try {
                ret = DubboConsumer.getInstance().getDubboRepServer().getRepServer(data);
            }
            catch (Exception e) {

            }
            //请求结果
            //ret = RequestUtil.post(url, param, "UTF-8", new HashMap<String, String>(), 0);
            //成功获取
            if (!ElongHotelInterfaceUtil.StringIsNull(ret) && ret.contains("id")) {
                break;
            }
        }
        //返回结果
        if (!ElongHotelInterfaceUtil.StringIsNull(ret) && ret.contains("url")) {
            return parseToRepServer(ret, isDama);
        }
        else {
            return new RepServerBean();
        }
    }

    /**
     * JSON转REP
     * @param repInfo 账号系统REP的JSON信息
     */
    private static RepServerBean parseToRepServer(String repInfo, boolean isDama) {
        //NEW
        RepServerBean rep = new RepServerBean();
        rep.setDama(isDama);
        rep.setFromRepSystem(true);
        //PARSE
        JSONObject obj = JSONObject.parseObject(repInfo);
        //SET
        rep.setId(obj.getLongValue("id"));
        rep.setName(obj.getString("name"));
        rep.setUrl(obj.getString("url"));
        rep.setType(obj.getIntValue("type"));
        rep.setServerIp(obj.getString("serverIp"));
        rep.setNeedFreeRep(obj.getBooleanValue("free"));
        rep.setServerPort(obj.getIntValue("serverPort"));
        rep.setServerPassword(obj.getString("serverPassword"));
        rep.setSpecial12306Ip(obj.getString("special12306Ip"));
        //返回
        return rep;
    }

    /**
     * REP系统地址
     */
    @SuppressWarnings("unused")
    private static String GetRepSystemUrl() {
        return PropertyUtil.getValue("12306RepUrl", "Train.properties");
    }

    /**
     * 账号系统公共参数
     * @author WH
     */
    private static JSONObject AccountSystemParam(String method) {
        JSONObject param = new JSONObject();
        try {
            //方法
            param.put("method", method);
            //时间
            long longtime = System.currentTimeMillis();
            String reqtime = String.valueOf(longtime);
            param.put("reqtime", reqtime);
            //标志
            param.put("sign", ElongHotelInterfaceUtil.MD5(ElongHotelInterfaceUtil.MD5(method) + reqtime));
        }
        catch (Exception e) {
        }
        return param;
    }

}