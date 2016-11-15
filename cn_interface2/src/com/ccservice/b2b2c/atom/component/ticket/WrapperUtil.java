package com.ccservice.b2b2c.atom.component.ticket;

import java.io.IOException;

import org.apache.commons.lang.ArrayUtils;

import com.ccservice.b2b2c.atom.component.ticket.api.DaMaCommon;
import com.ccservice.b2b2c.atom.component.ticket.api.DaMaResult;
import com.ccservice.b2b2c.atom.component.ticket.api.RuoKuaiDaMaApi;
import com.ccservice.b2b2c.atom.component.ticket.api.UUAPI;
import com.ccservice.b2b2c.atom.component.ticket.api.UUAPI.UUDLL;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 网页抓取的工具类
 * 
 * @time 2015年6月11日 下午5:12:50
 * @author chendong
 */
public class WrapperUtil {
    public static void main(String[] args) {
        DaMaCommon dmc = getcheckcodebydama("D:\\CA_img\\1435119026067.jpg", 0);
        System.out.println();
    }

    /**
     * 错码报错 
     * @param daMaCommon
     * @time 2015年1月13日 下午4:44:35
     * @author fiend
     */
    public static void errorCodeCommon(DaMaCommon daMaCommon) {
        if (daMaCommon == null) {
            return;
        }
        try {
            if (daMaCommon.getTpye() == DaMaCommon.RUOKUAI) {
                RuoKuaiDaMaApi.error(daMaCommon.getId());
            }
            else if (daMaCommon.getTpye() == DaMaCommon.UUYUN) {
                UUDLL.INSTANCE.uu_reportError(Integer.valueOf(daMaCommon.getId()));
            }
            //            else if (daMaCommon.getTpye() == DaMaCommon.UUYUNLINUX) {
            //                UUYunLinuxApi.error(SOFTID, SOFTKEY, USERNAME, PASSWORD, daMaCommon.getId());
            //            }
            //            else if (daMaCommon.getTpye() == DaMaCommon.DAMA2) {
            //                dama2Error(SOFTID, SOFTKEY, USERNAME, PASSWORD, daMaCommon.getId());
            //            }
            //            else if (daMaCommon.getTpye() == DaMaCommon.EASY) {
            //                EasyAPI.error(SOFTID, SOFTKEY, USERNAME, PASSWORD, daMaCommon.getId());
            //            }
            //            else if (daMaCommon.getTpye() == DaMaCommon.HTHYCODE) {
            //                HTHYAPI.error(SOFTID, SOFTKEY, USERNAME, PASSWORD, daMaCommon);
            //            }
            //            else if (daMaCommon.getTpye() == DaMaCommon.TaoBao) {
            //                TaobaoAPI.error(daMaCommon.getId());
            //            }
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println(daMaCommon.getId());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(daMaCommon.getId());
        }
    }

    /**
     * 打码
     * 国航的打码方法
     * 
     * @param picturepath
     * @param count
     * @return
     * @time 2015年6月24日 下午12:45:02
     * @author chendong
     */
    public static DaMaCommon getcheckcodebydama(String picturepath, int count) {
        DaMaCommon dmc = new DaMaCommon();
        //使用哪一家的打码 0 uuyuan 1
        String damafromSupplier = PropertyUtil.getValue("damafromSupplier", "air.properties");
        if ("0".equals(damafromSupplier)) {
            dmc = WrapperUtil.uuYundama(picturepath, 1004, 0);
        }
        else if ("1".equals(damafromSupplier)) {
            DaMaResult result = RuoKuaiDaMaApi.localPrint(picturepath, "3040");
            dmc.setId(result.getId());
            dmc.setResult(result.getResult());
        }
        return dmc;
    }

    /**
     *  优优云打码
     * 
     * @param picturepath
     * @param codeType 1004(国航的验证码类型)
     * @param count 打码多少次
     * @time 2015年6月24日 上午11:43:36
     * @author chendong
     */
    public static DaMaCommon uuYundama(String picturepath, int codeType, int count) {
        String resultString = "-1";
        DaMaCommon dmc = new DaMaCommon();
        try {
            String SOFTID = PropertyUtil.getValue("uuyun_SOFTID", "air.properties");//"99669";
            String SOFTKEY = PropertyUtil.getValue("uuyun_SOFTKEY", "air.properties");//"95975182099644d0b42503a3ff437609";
            String DLLVerifyKey = PropertyUtil.getValue("uuyun_DLLVerifyKey", "air.properties");//"72CDFDEB-452D-44A0-A62B-2897D0B5F46E";
            String DLLPATH = PropertyUtil.getValue("uuyun_DLLPATH", "air.properties");
            String USERNAME = PropertyUtil.getValue("uuyun_USERNAME", "air.properties");//"hyccservicecom";
            String PASSWORD = PropertyUtil.getValue("uuyun_PASSWORD", "air.properties");//"HANGtian126";
            UUAPI.SOFTID = Integer.valueOf(SOFTID);//ti.getSOFTID();
            UUAPI.SOFTKEY = SOFTKEY;
            UUAPI.DLLVerifyKey = DLLVerifyKey;
            UUAPI.DLLPATH = DLLPATH;
            UUAPI.USERNAME = USERNAME;
            UUAPI.PASSWORD = PASSWORD;
            if (UUAPI.checkAPI()) {
                String[] result = UUAPI.easyDecaptcha(picturepath, codeType);//1004
                System.out.println(ArrayUtils.toString(result));
                dmc.setId(result[0]);
                dmc.setTpye(DaMaCommon.UUYUN);
                dmc.setResult(result[1]);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return dmc;
    }
}
