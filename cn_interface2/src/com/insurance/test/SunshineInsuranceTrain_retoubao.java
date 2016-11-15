package com.insurance.test;

import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.insuruser.Insuruser;

public class SunshineInsuranceTrain_retoubao {
    public static void main(String[] args) {
        String polis = "BA201505261324120425,340423,12970,PARKMYUNGJUN|BA201505261448540538,319694,11414,胡绍会|BA201505261455050549,266695,7604,曾飞扬|BA201505261711210789,299947,9939,杨仕环|BA201505261741170829,375638,14620,彭志超|BA201505261752020851,254028,6904,李晓霞|BA201505261923190963,376514,14701,彭惠|BA201505262003501013,355889,13448,王永森|BA201505262211491199,234107,5689,温宗达|BA201505262223131211,276313,8263,戴英|BA201505270709101340,336075,12685,常宁|BA201505270830441427,253882,6891,常昊琳|BA201505270913001480,324638,11844,吴旻峰|BA201505271027121602,325340,11904,张昕宇|BA201505271127331710,284180,8840,梁田|BA201505271138581722,374970,14572,林秀英|BA201505271156181752,277241,8337,刘博心|BA201505271341111942,242485,6102,吕颖玉|BA201505271554402130,343186,13134,赵天红|BA201505271604212145,331787,12372,罗咏心|BA201505271621112176,351418,13292,陈婉怡|BA201505271952262263,284315,8852,程小艾|BA201505271952492265,335786,12674,林昊文|BA201505272006172289,321763,11598,徐乐|BA201505272020312325,371442,14331,张帆|BA201505272029282340,302015,10091,刘佳丽|BA201505272033542348,254218,6922,田建福|BA201505272115002430,321030,11541,王聪明|BA201505272125572463,337928,12809,周志成|BA201505272133122474,285328,8926,刘燕宇|BA201505280708242671,309733,10658,韩易成|BA201505280733392773,284068,8830,程小艾|BA201505280835302824,356775,13514,周蔓|BA201505280916052904,290547,9301,李德保|BA201505281011302991,277242,8338,李攀|BA201505281717013428,292238,9419,肖清清|BA201505281802053475,295500,9656,李瑶|BA201505281806163482,325677,11935,刘雨蒙|BA201505282002103665,331785,12371,戴英|BA201505290716374149,298710,9856,郑易茗|BA201505290754394247,330460,12297,罗咏心|BA201505291001194490,338474,12860,郑莉妹|BA201505291021044543,326404,12003,张昕宇|BA201505291043084590,306715,10451,彭红梅|BA201505291107354714,351174,13286,程学锋|BA201505291131004830,344067,13156,张乔阳|BA201505291146094922,358397,13607,武天屹|BA201505291158264962,378242,14823,周裕民|BA201505291617245590,377564,14769,施代佳|BA201505291641155652,311079,10746,姬毓|BA201505291808175940,360052,13728,吴涛|BA201505292000546270,360638,13768,王菲菲|BA201505292141406538,350953,13282,程学锋";
        //        String polis = "BA201505261210340281,374969,14571,郑作科|BA201505261309070407,370700,14274,代好古";

        String[] poliss = polis.split("[|]");

        for (int i = 0; i < poliss.length; i++) {
            main1(poliss[i]);
        }
    }

    public static void main1(String C_POLICYNO) {
        //        String sql = "where C_POLICYNO='BA201505252019429519'";
        String[] poliss_name = C_POLICYNO.split(",");
        C_POLICYNO = poliss_name[0];
        String name = poliss_name[3];
        String trainticketid = poliss_name[1];
        String inuserid = poliss_name[2];
        String sql = "where C_POLICYNO='" + C_POLICYNO + "'";
        //        List list = Server.getInstance().getSystemService().findMapResultSortBySql(sql, "order by id asc", null);
        List list = Server.getInstance().getAirService().findAllInsuruser(sql, "order by id asc", -1, 0);
        //        if (list.size() > 1) {
        //            for (int i = 1; i < list.size(); i++) {
        //        Insuruser insuruser = (Insuruser) list.get(i);
        Insuruser insuruser = Server.getInstance().getAirService().findInsuruser(Long.parseLong(inuserid));
        System.out.println(insuruser);
        System.out.println(insuruser.getFlytime());
        if (insuruser.getName().equals(name)) {
            List listnew = new ArrayList<Insuruser>();
            listnew.add(insuruser);
            try {
                List newlist = Server.getInstance().getAtomService().saveTrainOrderAplylist(null, listnew, 1);
                Insuruser insurusernew = (Insuruser) newlist.get(0);
                String sql_new = "";
                int count_ = 0;
                if (insurusernew.getInsurstatus() == 1) {
                    sql_new = "update T_INSURUSER set C_POLICYNO='" + insurusernew.getPolicyno() + "' where id="
                            + insuruser.getId() + ";update T_TRAINTICKET set C_REALINSURENO='"
                            + insurusernew.getPolicyno() + "' where id=" + trainticketid;
                    count_ = Server.getInstance().getSystemService().excuteGiftBySql(sql_new);
                }
                else {
                    sql_new = insurusernew.getRemark();
                }
                String temp_s = insuruser.getId() + ":" + count_ + ":" + sql_new;
                System.out.println(temp_s);
                WriteLog.write("chongxintoubao", temp_s);
            }
            catch (Exception e) {
            }
        }
        //            }
        //        }

    }
}
