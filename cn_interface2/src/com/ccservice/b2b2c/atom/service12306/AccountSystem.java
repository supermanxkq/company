package com.ccservice.b2b2c.atom.service12306;

import java.util.Map;
import java.util.HashMap;
import java.sql.Timestamp;

/**
 * 账号系统
 * @author WH
 */

public class AccountSystem {

    public static final String UTF8 = "UTF-8";//UTF-8

    public static final int OneFree = 1;//释放次数，1

    public static final int TwoFree = 2;//释放次数，2

    public static final int ZeroCancel = 0;//取消次数，0

    public static final int OneCancel = 1;//取消次数，1

    public static final int ThreeCancel = 3;//取消次数，3

    public static final String NullName = "";//获取账号，空账号名

    public static final Timestamp NullDepartTime = null;//发车时间，空

    public static final boolean waitWhenNoAccount = true;//获取账号，无账号等待

    public static final boolean checkPassenger = true;//释放的是身份验证

    public static final Map<String, String> NullMap = new HashMap<String, String>();//获取账号，备用字段

    /***********账号类型START***********/

    public static final int OrderAccount = 1;//下单账号

    public static final int PassengerAccount = 2;//身份验证账号

    public static final int LoginNameAccount = 3;//账号名获取

    public static final int CustomerAccount = 4;//客户账号

    /***********账号类型END***********/

    /***********释放类型START***********/

    public static final int FreeNoCare = 1;//NoCare

    public static final int FreeCurrent = 2;//仅当天使用

    public static final int FreeDepart = 3;//发车时间后才可使用

    public static final int FreeOther = 4;//分配给其他业务(暂未用)

    public static final int FreePassengerFull = 5;//账号乘客已满

    public static final int FreeNoLogin = 6;//账号未登录

    public static final int FreeNoCheck = 7;//账号待核验

    public static final int FreeBespeakBindingPassenger = 8;//约票绑定乘客释放(约票专用，其他业务勿使)

    /***********释放类型END***********/

}