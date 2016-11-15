package com.ccservice.b2b2c.atom.servlet.QunarTrain;

import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.train.Train;

public class QunarSupplyMethod extends TongchengSupplyMethod {
    /**
     * 
     * 获取qunar在我们系统中的agentid
     * @return
     * @time 2014年12月11日 下午12:00:47
     * @author chendong
     */
    public String getqunaragentid() {
        return getSysconfigString("qcyg_agentid");
    }

    /**
     * 根据出发到达时间获取列车信息
     * train.getStart_station_name() 始发站名字
     * train.getSfz()始发站三字码
     * train.getEnd_station_name()终点站名字
     * train.getZdz()终点站名字三字码
     * train.getRun_time_minute()运行分钟数
     * @param fromcity 出发三字码
     * @param tocity   到达三字码
     * @param date  时间  格式2014-12-11
     * @param traincode 车次
     * @time 2015年7月10日15:14:16
     * @author luoqingxin
     **/
    public static Train getTrainbycache(String fromcity, String tocity, String date, String traincode) {
        FlightSearch flightSearch = new FlightSearch();
        List<Train> list = Server.getInstance().getAtomService()
                .getDGTrainListcache(fromcity, tocity, date, flightSearch);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTraincode().equals(traincode)) {
                return list.get(i);
            }
        }
        Train train = new Train();
        return train;
    }

    /**
     *证件类型 12306转换代码本地
     * @param idtype
     * @return
     * @time 2014年12月24日 上午11:21:59
     * @author wzc
     */
    public static int getIdtype12306tolocal(String idtype) {
        if (idtype.equals("1")) {
            return 1;
        }
        else if (idtype.equals("B")) {
            return 3;
        }
        else if (idtype.equals("C")) {
            return 4;
        }
        else if (idtype.equals("G")) {
            return 5;
        }
        else {
            return 0;
        }
    }
}