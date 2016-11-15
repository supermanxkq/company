package com.yeepay.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

// 从配置文件中获得配置信息
public class Configuration {
	private static Object lock = new Object();
	private static Configuration config = null;
	private static ResourceBundle rb = null;
	private static final String CONFIG_FILE = "info";
	
	private static Map configMap = new HashMap(){Object get(String key){
		Configuration value = (Configuration)super.get(key);
		return ((value ==null) ? value : getInstance() );
		}
	};
	private static Map rbMap = new HashMap(){Object get(String key){
		ResourceBundle value = (ResourceBundle)super.get(key);
		return ((value ==null) ? value : ResourceBundle.getBundle(key) );
		}
	};
	private Configuration(String CONFIG_FILE) {
		rb = (ResourceBundle) rbMap.get(CONFIG_FILE);
		//ResourceBundle.getBundle(CONFIG_FILE);
	}
	public static Configuration getInstance(String key) {
		synchronized(lock) {
			Configuration config = (Configuration) configMap.get(key);
			if(null == config) {
				configMap.put(key, new Configuration());
			}
		}
		return (config);
	}
	
	private Configuration() {
		rb = ResourceBundle.getBundle(CONFIG_FILE);
	}
	public static Configuration getInstance() {
		synchronized(lock) {
			if(null == config) {
				config = new Configuration();
			}
		}
		return (config);
	}
	public String getValue(String key) {
		return (rb.getString(key));
	}
}