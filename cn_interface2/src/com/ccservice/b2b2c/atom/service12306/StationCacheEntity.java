/** @Title: StationCacheEntity.java	   <BR>
* @Package com.ccservice.b2b2c.atom.service12306   <BR>
* @Description: TODO(用一句话描述该文件做什么)<BR>
* @author   Anki   <BR>
* @date 2016年1月6日 下午3:59:16  <BR>
* @version V1.0     
*/
package com.ccservice.b2b2c.atom.service12306;

import java.util.Date;
import java.util.Map;


/**
* @ClassName: StationCacheEntity <BR>
* @Description: TODO(存储三字码)<BR>
* @author  Anki <BR>
* @date 2016年1月6日 下午3:59:16 <BR> <BR>
*/
public class StationCacheEntity {

    public StationCacheEntity(Map<String, String> stationInfoMap, Date dateTime) {
        this.stationInfoMap = stationInfoMap;
        this.dateTime = dateTime;
    }

    private Date dateTime;

    private Map<String, String> stationInfoMap;

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getStationInfoMap(String name) {
        long lastTime = dateTime.getTime();
        long currentTime = new Date().getTime();
        String threeCode = this.stationInfoMap.get(name);
        if ((currentTime - lastTime) >= (1000 * 45) || threeCode == null) {
            this.stationInfoMap = Train12306StationInfoUtil.loadAllStation();
            this.dateTime = new Date();
            threeCode = this.stationInfoMap.get(name);
        }
        if (threeCode == null) {
            threeCode = name;
        }
        return threeCode;
    }

    public void setStationInfoMap(Map<String, String> stationInfoMap) {
        this.stationInfoMap = stationInfoMap;
    }
}
