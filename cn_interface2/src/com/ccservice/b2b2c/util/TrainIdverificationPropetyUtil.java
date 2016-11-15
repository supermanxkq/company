package com.ccservice.b2b2c.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 
 * @author wzc
 * 系统配置
 *
 */
public class TrainIdverificationPropetyUtil {

    private static Map<String, String> mapvalues = new HashMap<String, String>();

    /**
     * 初始化数据
     */
    public static void initData(String path) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(path);
            Element root = document.getRootElement();
            List<Element> elementlist = root.elements("Item");
            for (int i = 0; i < elementlist.size(); i++) {
                Element item = elementlist.get(i);
                String name = item.elementText("name");
                String value = item.elementText("value");
                mapvalues.put(name, value);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取value
     * @param key
     * @return
     */
    public static String getValue(String key) {
        if (mapvalues.containsKey(key)) {
            return mapvalues.get(key);
        }
        return "";
    }

    public static void main(String[] args) {
    }
}
