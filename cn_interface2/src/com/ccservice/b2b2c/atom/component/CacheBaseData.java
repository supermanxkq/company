package com.ccservice.b2b2c.atom.component;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.aircompany.Aircompany;
import com.ccservice.b2b2c.base.cabin.Cabin;
import com.ccservice.b2b2c.base.cityairport.Cityairport;
import com.ccservice.b2b2c.base.flightmodel.Flightmodel;

public class CacheBaseData {

    private BaseCache BaseDataCache;

    private static CacheBaseData instance;

    private static Object lock = new Object();

    public CacheBaseData() {
        //这个根据配置文件来，初始BaseCache而已;  
        //缓存时间设置为1个月
        BaseDataCache = new BaseCache("basedatainfo", 2592000);
    }

    /**
     * 缓存航空公司名称
     * @param strKey Key值
     * @return 航空公司
     * @throws MalformedURLException
     */
    public Aircompany getAirCompanyInfo(String strKey) throws MalformedURLException {
        try {
            Aircompany aircompany = new Aircompany();
            aircompany = (Aircompany) BaseDataCache.get(strKey);
            return aircompany;

        }
        catch (Exception e) {
            System.out.println("getBasedata>>AirCompany[" + strKey + "]>>" + e.getMessage());
            //从接口查询航班
            List<Aircompany> list = Server.getInstance().getAirService().findAllAircompany("", "", -1, 0);
            //将航空公司存入缓存
            Aircompany aircompany = new Aircompany();
            for (int i = 0; i < list.size(); i++) {
                BaseDataCache.put(list.get(i).getAircomcode(), list.get(i));
                if (list.get(i).getAircomcode().equals(strKey)) {
                    aircompany = list.get(i);
                }
            }
            return aircompany;
        }
    }

    public Cityairport getCityAirport(String strKey) throws MalformedURLException {
        try {
            Cityairport cityairport = new Cityairport();
            cityairport = (Cityairport) BaseDataCache.get(strKey);
            return cityairport;

        }
        catch (Exception e) {
            System.out.println("getBasedata>>cityairport[" + strKey + "]>>" + e.getMessage());
            //从接口查询机场信息
            List<Cityairport> list = Server.getInstance().getAirService().findAllCityairport("", "", -1, 0);
            //将机场信息存入缓存
            Cityairport cityairport = new Cityairport();
            for (int i = 0; i < list.size(); i++) {
                BaseDataCache.put(list.get(i).getAirportcode(), list.get(i));
                if (list.get(i).getAirportcode().equals(strKey)) {
                    cityairport = list.get(i);
                }
            }
            return cityairport;
        }
    }

    public List<Aircompany> getAirCompanyList(String strKey) throws MalformedURLException {
        try {
            List<Aircompany> list = new ArrayList<Aircompany>();
            list = (List<Aircompany>) BaseDataCache.get(strKey);
            return list;

        }
        catch (Exception e) {
            System.out.println("getBasedata>>AirCompanyList[" + strKey + "]>>" + e.getMessage());
            //从接口查询航班
            List<Aircompany> list = Server.getInstance().getAirService()
                    .findAllAircompany("where " + Aircompany.COL_countrycode + "='CN'", "", -1, 0);
            //将航空公司存入缓存
            BaseDataCache.put("ALLAirCompanyList", list);
            return list;
        }
    }

    /**
     * 查询所有机型信息
     * @param strKey 机型号
     * @return  机型list
     * @throws MalformedURLException
     */
    public List<Flightmodel> getFlightModel(String strKey) throws MalformedURLException {
        try {
            List<Flightmodel> list = new ArrayList<Flightmodel>();
            list = (List<Flightmodel>) BaseDataCache.get(strKey);
            return list;

        }
        catch (Exception e) {
            System.out.println("getBasedata>>Flightmodel[" + strKey + "]>>" + e.getMessage());
            //从接口查询航班
            List<Flightmodel> list = Server.getInstance().getAirService().findAllFlightmodel("", "", -1, 0);
            //将航空公司存入缓存
            BaseDataCache.put("ALLFlightModelList", list);
            return list;
        }
    }

    /**
     * 缓存舱位信息
     * @param strKey Key值
     * @return 航空公司
     * @throws MalformedURLException
     */
    public Cabin getCabinInfo(String strKey, String strAirCompanycode, String strCabinCode)
            throws MalformedURLException {
        try {
            Cabin cabin = new Cabin();
            cabin = (Cabin) BaseDataCache.get(strKey);
            return cabin;

        }
        catch (Exception e) {
            System.out.println("getBasedata>>Cabin[" + strKey + "]>>" + e.getMessage());
            //从接口查询航班
            List<Cabin> list = Server
                    .getInstance()
                    .getAirService()
                    .findAllCabin(
                            "where C_AIRCOMPANYCODE='" + strAirCompanycode + "' and C_CABINCODE='" + strCabinCode + "'",
                            "", -1, 0);
            //将航空公司存入缓存
            Cabin cabin = new Cabin();
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    BaseDataCache.put(list.get(i).getAircompanycode() + "_" + list.get(i).getCabincode(), list.get(i));
                    if (strKey.equals(list.get(i).getAircompanycode() + "_" + list.get(i).getCabincode())) {
                        cabin = list.get(i);
                    }
                }
            }
            return cabin;
        }
    }

}
