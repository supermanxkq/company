package com.ccservice.b2b2c.atom.hotel;

/**
 * 酒店工具类
 */
public class HotelUtil {
    /**
     * 酒店宽带 无、免费、收费
     */
    public static String getWeb(String desc, Integer wideband) {
        String type = "无";
        if (",宽带上网(免费)".equals(desc)) {
            type = "免费";
        }
        else if (",宽带上网(收费)".equals(desc)) {
            type = "收费";
        }
        else if ("宽带上网(免费)".equals(desc)) {
            type = "免费";
        }
        else if ("宽带上网(收费)".equals(desc)) {
            type = "收费";
        }
        else if ("免费无线上网在行政酒廊,宽带上网(免费)".equals(desc)) {
            type = "免费";
        }
        else if ("无线上网(免费)".equals(desc)) {
            type = "免费";
        }
        else if ("无线上网(免费),宽带上网(免费)".equals(desc)) {
            type = "免费";
        }
        else if ("无线上网(免费),宽带上网(收费)".equals(desc)) {
            type = "收费";
        }
        else if ("无线上网(免费),无线上网(免费)".equals(desc)) {
            type = "免费";
        }
        else if ("无线上网(收费)".equals(desc)) {
            type = "收费";
        }
        else if ("无线上网(收费),宽带上网(免费)".equals(desc)) {
            type = "收费";
        }
        else if ("无线上网(收费),宽带上网(收费)".equals(desc)) {
            type = "收费";
        }
        else if (wideband != null && wideband.intValue() == 1) {
            type = "免费";
        }
        else if (wideband != null && wideband.intValue() == 2) {
            type = "收费";
        }
        return type;
    }
}