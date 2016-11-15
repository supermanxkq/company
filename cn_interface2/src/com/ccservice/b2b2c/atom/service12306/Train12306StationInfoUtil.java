package com.ccservice.b2b2c.atom.service12306;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;

/**
 * 获取12306车站集合 或 车站名称对应三字码
 */
public class Train12306StationInfoUtil {

    private static StationCacheEntity cacheEntity = new StationCacheEntity(loadAllStation(), new Date());

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 60; i++) {
            Thread.sleep(1000L);
            System.out.println(Train12306StationInfoUtil.getThreeByName("北京"));
        }
        /*  try {
                      System.out.println(Train12306StationInfoUtil.getThreeByName("北京"));
          }
          catch (Exception e) {
              e.printStackTrace();
          }
          System.out.println(loadAllStation() == null ? "null" : loadAllStation().size());
        */
        //System.out.println(getThreeByName("北京北"));

    }

    /**
     * @描述: TODO(根据车站名字获取三字码)  <BR>
     * @方法名: getThreeByName  <BR>
     * @创建人:  Anki  <BR>
     * @参数： @param name
     * @参数： @return  <BR>
     * @创建时间:2016年1月6日-下午4:15:12 <BR>
     * @return String    返回类型  <BR>
     */
    public static String getSZMByName(String name) {
        if (ElongHotelInterfaceUtil.StringIsNull(name)) {
            return name;
        }
        else {
            return cacheEntity.getStationInfoMap(name);
        }
    }

    /**
     * @描述: TODO(查询数据库中的所有车站的信息)<BR>
     * @方法名: loadAllStation  <BR>
     * @创建人:  Anki  <BR>
     * @参数： @return  <BR>
     * @创建时间:2016年1月6日-下午3:53:26 <BR>
     * @return Map<String,String>    返回类型  <BR>
     */
    public static Map<String, String> loadAllStation() {
        Map<String, String> stationInfo = null;
        String sql = "select SName,Scode from StationName with(nolock) ";
        DataTable dataTable = Train12306StationInfoUtilDBHelper.GetDataTable(sql);
        List<DataRow> dataRows = dataTable.GetRow();
        if (dataRows != null && dataRows.size() > 0) {
            stationInfo = new HashMap<String, String>();
            for (int i = 0; i < dataRows.size(); i++) {
                DataRow dataRow = dataRows.get(i);
                String key = dataRow.GetColumnString("SName");
                String value = dataRow.GetColumnString("Scode");
                stationInfo.put(key, value);
            }
        }
        return stationInfo;
    }

    /**
      * 获取车站名称对应三字码
      * 根据
      */
    public static String getThreeByName(String name) throws Exception {
        String threeCode = getSZMByName(name);
        //        String threeCode = "";
        //        threeCode = getThreeByNameFromDb(name);//缓存的
        //        //        threeCode = getThreeByNamewh(name);
        //        if (ElongHotelInterfaceUtil.StringIsNull(threeCode)) {
        //            //1、同步12306数据到TXT文件
        //            MyThreadSimpleCode.execute();
        //            //2、再次获取
        //            threeCode = getThreeByNamewh(name);
        //        }
        return threeCode;
    }

    /**
     * @param name
     * @return
     * @time 2015年8月25日 上午11:00:32
     * @author chendong
     */
    private static String getThreeByNameFromDb(String name) {
        String threeCode = "";
        if (name != null && !"".equals(name)) {
            String procedureSqlString = "sp_StationName_getThreeByNamefromdb @SName = '" + name + "'";
            List ListStationName = Server.getInstance().getSystemService().findMapResultByProcedure(procedureSqlString);
            if (ListStationName.size() > 0) {
                Map map = (Map) ListStationName.get(0);
                threeCode = map.get("Scode").toString();
            }
            else {
                threeCode = name;
            }
        }
        else {
            threeCode = name;
        }
        return threeCode;
    }

    /**
     * 
     * @param name
     * @return
     * @time 2015年8月25日 上午10:59:51
     * @author chendong
     * @throws Exception 
     */
    private static String getThreeByNamewh(String name) throws Exception {
        if (ElongHotelInterfaceUtil.StringIsNull(name)) {
            return name;
        }
        Map<String, String> station = allStation();
        if (station.containsKey(name)) {
            return station.get(name);
        }
        else {
            return name;
        }
    }

    /**
     * 加载所有车站
     * @return map<key,value> <--> 北京北=VAP, 北京东=BOP, 北京=BJP
     */
    public static Map<String, String> allStation() throws Exception {
        Map<String, String> map = new LinkedHashMap<String, String>();
        String filecontent = loadStation();
        if (!ElongHotelInterfaceUtil.StringIsNull(filecontent)) {
            String[] stations = filecontent.split("@");
            //bjb|北京北|VAP|beijingbei|bjb|0@bjd|北京东|BOP|beijingdong|bjd|1
            for (String station : stations) {
                String key;
                String value;
                try {
                    String[] temp = station.split("\\|");
                    key = temp[1];
                    value = temp[2];
                    if (map.containsKey(key)) {
                        throw new Exception("车站名称存在重复.");
                    }
                    map.put(key, value);
                }
                catch (Exception e) {
                }
            }
        }
        return map;
    }

    //加载车站文件
    private static String loadStation() {
        InputStream in = null;
        BufferedReader br = null;
        InputStreamReader isr = null;
        StringBuffer buf = new StringBuffer();
        try {
            in = Train12306StationInfoUtil.class.getClassLoader().getResourceAsStream("TrainStationNames.txt");
            isr = new InputStreamReader(in, "utf-8");
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            buf = new StringBuffer();
        }
        finally {
            try {
                if (br != null) {
                    br.close();
                }
            }
            catch (Exception e) {
            }
            try {
                if (isr != null) {
                    isr.close();
                }
            }
            catch (Exception e) {
            }
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception e) {
            }
        }
        return buf.toString();
    }

}