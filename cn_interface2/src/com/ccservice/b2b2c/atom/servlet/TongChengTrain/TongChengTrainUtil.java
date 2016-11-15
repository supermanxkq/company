package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

/**
 * @author WH
 */

public class TongChengTrainUtil {

    //同程证件类型转本地，-1表示证件错误
    public static int tongChengIdTypeToLocal(String type) {
        //1:二代身份证，2:一代身份证，C:港澳通行证，G:台湾通行证，B:护照
        if ("1".equals(type)) {
            return 1;
        }
        else if ("C".equals(type)) {
            return 4;
        }
        else if ("G".equals(type)) {
            return 5;
        }
        else if ("B".equals(type)) {
            return 3;
        }
        else {
            return -1;
        }
    }

    //本地证件转同程
    public static String localIdTypeToTongCheng(int type) {
        //1: 二代身份证; 3: 护照; 4: 港澳通行证; 5: 台湾通行证;
        if (type == 1) {
            return "1";
        }
        else if (type == 4) {
            return "C";
        }
        else if (type == 5) {
            return "G";
        }
        else if (type == 3) {
            return "B";
        }
        else {
            return "-1";
        }
    }

}