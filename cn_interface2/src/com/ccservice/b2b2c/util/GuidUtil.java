package com.ccservice.b2b2c.util;

import java.util.UUID;

public class GuidUtil {
    private static UUID getGUID() {
        UUID uuid = UUID.randomUUID();
        return uuid;
    }

    /**
     * 获得一个去掉"-"符号的UUID
     * @return
     */
    public static String getUuid() {
        String s = getGUID().toString();
        return s.replace("-", "");
    }
}
