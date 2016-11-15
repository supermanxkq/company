/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2006 All Rights Reserved.
 */
package com.alipay.client.util;

import java.io.InputStream;
import java.util.List;

import org.nuxeo.common.xmap.XMap;

/**
 * XMap工具类，用来解析xml文件 
 * @author stone.zhangjl
 * @version $Id: XMapUtil.java,v 1.1 2013/11/18 07:52:02 hanmh Exp $
 */
public class XMapUtil {
    private static final XMap xmap;

    static {
        xmap = new XMap();
    }

    /**
     * 注册Object。
     * 
     * @param clazz
     */
    public static void register(Class<?> clazz) {
        if (clazz != null) {
            xmap.register(clazz);
        }
    }

    /**
     * 解析xml到Object
     * @param is
     * @return
     * @throws Exception
     */
    public static Object load(InputStream is) throws Exception {
        Object obj = null;
        try {
            obj = xmap.load(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return obj;
    }

    /**
     * Object到XML。
     * 
     * @param obj
     * @param encoding
     * @param outputsFields
     * @return
     * @throws PaygwException 
     */
    public static String asXml(Object obj, String encoding, List<String> outputsFields)
                                                                                       throws Exception {

        return xmap.asXmlString(obj, encoding, outputsFields);
    }
}