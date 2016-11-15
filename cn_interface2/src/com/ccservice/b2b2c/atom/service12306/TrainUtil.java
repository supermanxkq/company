package com.ccservice.b2b2c.atom.service12306;

import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;

public class TrainUtil {
    /**
     *  随机调取下单接口，获取12306-cookie所在IP
     * @return cookie所在IP
     * @time 2014年12月16日 下午12:07:53
     * @author fiend
     */
    public static String randomIp() {
        String canorderips = getSystemConfig("Reptile_traininit_strs");
        if (canorderips.equals("46") || canorderips.trim().equals("")) {
            return getSystemConfig("Reptile_traininit_url");
        }
        String[] iscanorderip = canorderips.split(",");
        int i = (int) (Math.random() * iscanorderip.length);
        return getSystemConfig("Reptile_traininit_url" + iscanorderip[i]);
    }

    public static String getSystemConfig(String name) {
        List<Sysconfig> configs = Server.getInstance().getSystemService()
                .findAllSysconfig("where c_name='" + name + "'", "", -1, 0);
        if (configs != null && configs.size() == 1) {
            Sysconfig config = configs.get(0);
            return config.getValue();
        }
        return "46";
    }

    public static String getseat_typesby_trainclasstype(String checi, int type) {
        if (checi.startsWith("Z")) {
            return "43";
        }
        if (checi.startsWith("T")) {
            return "14613";
        }
        if (checi.startsWith("K")) {
            return "1413";
        }
        if (checi.startsWith("G")) {
            if (type == 1) {
                return "OM9";//G640 G70 G426 G83
            }
            if (type == 2) {
                return "O9OM";//G856
            }
            if (type == 3) {
                return "O9MO";//G9647
            }
            return "OMP";//G94 
        }
        if (checi.startsWith("C")) {
            if (type == 1) {
                return "OM9";//G640 G70 G426 G83
            }
            if (type == 2) {
                return "O9OM";//G856
            }
            if (type == 3) {
                return "O9MO";//G9647
            }
            return "OMP";//G94 
        }
        if (checi.startsWith("D")) {
            if (type == 1 || type == 3) {
                return "OM9";
            }
            return "OMO";
        }
        if (checi.startsWith("Y") || checi.startsWith("L")) {
            return "11";
        }
        return "1413";
    }

}
