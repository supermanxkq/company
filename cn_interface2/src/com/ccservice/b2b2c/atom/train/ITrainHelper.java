package com.ccservice.b2b2c.atom.train;

import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.sms.SmsSender;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

public interface ITrainHelper {

    /**
     * 
     * 余票和时刻
     * 
     * @param startcity
     * @param endcity
     * @param time
     * @return
     * @time 2014年12月1日 下午7:25:27
     * @author chendong
     */
    public List<Train> getYPSKUnionTrainList(String startcity, String endcity, String time);

    public List<Train> getTrainlistFromInterface(String urlstr, String time);

    /**
     * 火车列车时刻信息
     * 
     * @param startcity
     * @param edncity
     * @param time
     * @return
     */
    public List<Train> getSKTrainList(String startcity, String edncity, String time);

    /**
     * 火车余票信息
     * 
     * @param startcity
     * @param endcity
     * @param time
     * @return
     */
    public List<Train> getYPTrainList(String startcity, String endcity, String time);

    /**
     * 代购时刻表
     * 
     * @param startcity
     * @param endcity
     * @param time
     * @return
     */
    public List<Train> getDGTrainList(String startcity, String endcity, String time);

    /**
     * 列车详细信息
     * 
     * @param traincode
     * @param startcity
     * @param endcity
     * @param time
     * @return
     */
    public List<Map<String, String>> getTraininfo(String traincode, String time);

    /**
     * 获取火车票分润资料
     * @param train
     * @return
     */
    public Map<Customeragent, Float> getAgentroyalty(Train train);

    /**
     * @param train
     * 创建火车票分润记录
     */
    public void createTrainrebate(Train train);

    public List<Train> getYPTrainlistFromInterface(String urlstr);

    public String getNetworkOrder(Train train, Trainpassenger passenger);

    public List<Map<String, String>> getCityinfo(String cityName);

    public List<Map<String, String>> getcityProv(String cityProv);

    public boolean sendAlermsms(SmsSender smssender);

    /**
     * 从缓存中获取列车时刻信息
     * 
     * @param fromcity
     * @param tocity
     * @param date
     * @return
     * @time 2014年12月10日 下午5:09:00
     * @author chendong
     * @param param 
     */
    public List<Train> getDGTrainListcache(String fromcity, String tocity, String date, FlightSearch param);

}
