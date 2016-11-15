package com.ccservice.b2b2c.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 作者 guozhengju:
 * @version 创建时间：2015年7月29日 下午2:34:45 类说明
 */
public class BaiDuMapApi {

	public BaiDuMapApi() {
	}

	/**
	 * 根据邮寄地址匹配线下售票点
	 * 
	 * @param address
	 * 
	 * @return
	 */
	public String getAgentidByInsuraddress(List list, String address) {
		BaiDuMap bdm = new BaiDuMap();
		// address = "北京,北京市,昌平区,沙河镇";
		List<Map> lists = new ArrayList<Map>();
		String agentId = "1";
		boolean flag = true;
		Map<String, Double> map1 = new HashMap<String, Double>();

		String[] ss = address.split(",");

		if (list.size() > 0) {
			if (list.size() == 1) {
				Map map = (Map) list.get(0);
				agentId = map.get("ID").toString();
			} else {
				for (int i = 0; i < list.size(); i++) {
					Map map = (Map) list.get(i);
					String add = map.get("ProvinceName").toString()
							+ map.get("CityName").toString()
							+ map.get("RegionName").toString()
							+ map.get("TownName").toString();

					map1.put(map.get("ID").toString(),
							bdm.getDistance(ss[0] + ss[1] + ss[2] + ss[3], add));

				}
				agentId = getAgentIdByMinDistance(map1);
			}
		} else {
			agentId = "1";
		}
		return agentId;
	}

	/**
	 * list冒泡排序
	 * 
	 * @param list
	 * @return
	 */

	public String getAgentIdByMinDistance(Map<String, Double> map_Data) {

		List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String, Double>>(
				map_Data.entrySet());

		Collections.sort(list_Data,
				new Comparator<Map.Entry<String, Double>>() {
					public int compare(Map.Entry<String, Double> o1,
							Map.Entry<String, Double> o2) {
						if ((o2.getValue() - o1.getValue()) < 0)
							return 1;
						else if ((o2.getValue() - o1.getValue()) == 0)
							return 0;
						else
							return -1;
					}
				});
		return list_Data.get(0).getKey();
	}

}
