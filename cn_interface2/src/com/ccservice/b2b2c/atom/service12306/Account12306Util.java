package com.ccservice.b2b2c.atom.service12306;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.sql.Timestamp;
import com.weixin.util.RequestUtil;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.account.dubbo.util.DubboConsumer;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 12306账号工具类
 * @author WH
 * @time 2016年6月13日 下午3:22:37
 * @version 1.0
 */

public class Account12306Util {

    /**
     * 根据REP结果判断用户未登录
     * @param user 12306账号
     * @param RepResult REP交互结果
     * @remark 用同一个REP并被封时，视为未登录
     */
    public static boolean accountNoLogin(String RepResult, Customeruser user) {
        //结果为空
        boolean isNull = ElongHotelInterfaceUtil.StringIsNull(RepResult);
        //用户未登录
        if (!isNull && (RepResult.contains("用户未登录") || RepResult.contains("该用户已在其他地点登录，本次登录已失效"))) {
            return true;
        }
        //用同一个REP
        if (RepServerUtil.UseSameRep(user)) {
            //REP被封
            if (!isNull && (RepResult.contains("网络繁忙") || RepResult.contains("您的操作频率过快"))) {
                return true;
            }
            //REP未绑定成功
            if (GetRepId(user, false) <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取12306账号>>注意参数，不要乱传，不清楚的及时问！！！
     * @author WH
     * @param type 1:获取下单账号；2:获取身份验证账号；3:账号名获取；4:客人账号
     * @param name 12306账号名，type为3、4时有效，其他type可为空
     * @param waitWhenNoAccount 无账号的时候是否等待，type为1、2时有效
     * @param backup 备用字段
     * @备注 backup备用字段存在password且value非空表示取客户账号的cookie，此时name非空；type无效(原方法可不修改type)，统一走取客户账号
     */
    @SuppressWarnings("rawtypes")
    public static Customeruser get12306Account(int type, String name, boolean waitWhenNoAccount,
            Map<String, String> backup) {
        Customeruser user = new Customeruser();
        //为空时设置
        if (backup == null) {
            backup = AccountSystem.NullMap;
        }
        if (ElongHotelInterfaceUtil.StringIsNull(name)) {
            if (type == AccountSystem.LoginNameAccount) {
                return user;
            }
            else {
                name = "";
            }
        }
        //来自账号系统
        user.setFromAccountSystem(true);
        //请求JSON
        JSONObject data = AccountSystemParam("Get");
        //备用字段
        for (String key : backup.keySet()) {
            data.put(key, backup.get(key));
        }
        data.put("type", type);
        data.put("name", name);
        data.put("waitWhenNoAccount", waitWhenNoAccount);
        //请求参数
        String param = "param=" + data.toJSONString();
        //请求地址
        String url = GetAccountSystemUrl();
        //记录时间
        int PKId = 0;
        //记录处理
        try {
            if ("1".equals(PropertyUtil.getValue("SaveGetAccountTime", "Train.properties"))) {
                //记录时间
                List list = Server.getInstance().getSystemService()
                        .findMapResultByProcedure(" [sp_TrainOrderGetAccountTime_insert]");
                //保存成功
                if (list != null && list.size() > 0) {
                    Map map = (Map) list.get(0);
                    PKId = Integer.valueOf(map.get("PKId").toString());
                }
                //记录日志
                WriteLog.write("TrainOrderGetAccountTime", param + "-->" + PKId);
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("TrainOrderGetAccountTime_Exception", e);
        }
        //POST请求
        String ret = RequestUtil.post(url, param, "UTF-8", new HashMap<String, String>(), 10 * 60 * 1000);
        //删除时间
        try {
            if (PKId > 0) {
                //删除操作
                Server.getInstance().getSystemService()
                        .findMapResultByProcedure(" [sp_TrainOrderGetAccountTime_Delete] @PKId = " + PKId);
                //记录日志
                WriteLog.write("TrainOrderGetAccountTime", "Delete-->" + PKId);
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("TrainOrderGetAccountTime_Exception", e);
        }
        //日志
        WriteLog.write("Account12306Util_Get12306Account", param + "-->" + ret);
        //解析返回结果
        if (!ElongHotelInterfaceUtil.StringIsNull(ret)) {
            //PARSE
            JSONObject obj = JSONObject.parseObject(ret);
            //SET
            user.setId(obj.getLongValue("id"));
            user.setState(obj.getInteger("state"));
            user.setLoginname(obj.getString("name"));
            user.setNationality(obj.getString("msg"));
            user.setLogpassword(obj.getString("pass"));
            user.setCardnunber(obj.getString("cookie"));
            user.setIsenable(obj.getInteger("isenable"));
            user.setDontRetryLogin(obj.getBooleanValue("dontRetryLogin"));
            user.setMemberemail(obj.getString("repurl"));//登录的REP信息，非空时为JSON格式
            user.setMemberfax(obj.getString("syncReserveResult"));//通过账号名预约，同步返回的预约结果
            user.setChinaaddress(obj.getString("enableAccounts"));//falsely_enable，不可用账号集合
        }
        return user;
    }

    /**
     * 释放12306账号>>注意参数，不要乱传，不清楚的及时问！！！
     * @author WH
     * @param user 12306账号，必填
     * @param freeType 释放类型 1:NoCare；2:仅当天使用；3:发车时间后才可使用；4:分配给其他业务(暂未用)；其他详见AccountSystem类
     *          |-->传AccountSystem里未定义的类型，将直接赋值给相关账号
     * @param cancalCount 取消次数，用于取消时释放账号，其他业务必须传0
     * @param freeCount 释放次数，1或2次
     * @param departTime 发车时间，freeType为3时有效，其他请设为空
     * @param isCheckPassenger 身份验证标识
     */
    public static void free12306Account(Customeruser user, int freeType, int freeCount, int cancalCount,
            Timestamp departTime, boolean isCheckPassenger) {
        //账号为空
        if (user == null || !user.isFromAccountSystem()) {
            return;
        }
        //账号名称
        String name = user.getLoginname();
        //判断数据
        if (ElongHotelInterfaceUtil.StringIsNull(name) || freeCount < AccountSystem.OneFree) {
            return;
        }
        if (cancalCount < AccountSystem.ZeroCancel || cancalCount > AccountSystem.ThreeCancel) {
            return;
        }
        if (freeType == AccountSystem.FreeDepart && departTime == AccountSystem.NullDepartTime) {
            return;
        }
        //RepId
        long repId = GetRepId(user, true);
        //不释放REP、客人账号
        if (repId <= 0 && user.isCustomerAccount()) {
            return;
        }
        //请求JSON
        JSONObject data = AccountSystemParam("Free");
        data.put("name", name);
        data.put("freeType", freeType);
        data.put("freeCount", freeCount);
        data.put("repId", repId);//释放REP用
        data.put("cancalCount", cancalCount);
        data.put("isCheckPassenger", isCheckPassenger);
        data.put("isCustomerAccount", user.isCustomerAccount());
        data.put("departTime", departTime == AccountSystem.NullDepartTime ? new Timestamp(System.currentTimeMillis())
                : departTime);
        //Dubbo
        try {
            DubboConsumer.getInstance().getDubboAccount().free12306Account(data);
        }
        catch (Exception e) {

        }
        /*
        //请求参数
        String param = "param=" + data.toJSONString();
        //请求地址
        String url = GetAccountSystemUrl();
        //POST请求
        RequestUtil.post(url, param, "UTF-8", new HashMap<String, String>(), 0);
        */
    }

    /**
     * 获取服务器ID
     * @param checkFree 校验是否释放
     */
    private static long GetRepId(Customeruser user, boolean checkFree) {
        //结果
        long repId = 0;
        //用同一个REP
        if (RepServerUtil.UseSameRep(user)) {
            //解析
            JSONObject object = JSONObject.parseObject(user.getMemberemail());
            //不校验释放
            if (!checkFree) {
                repId = object.getLongValue("id");
            }
            //校验释放，判断标识
            else {
                if (object.getBooleanValue("free")) {
                    repId = object.getLongValue("id");
                }
            }
        }
        //返回结果
        return repId;
    }

    /**
     * 账号系统地址
     * @author WH
     */
    private static String GetAccountSystemUrl() {
        return PropertyUtil.getValue("12306AccountUrl", "Train.properties");
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