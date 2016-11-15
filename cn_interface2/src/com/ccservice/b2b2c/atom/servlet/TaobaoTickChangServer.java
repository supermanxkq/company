package com.ccservice.b2b2c.atom.servlet;

import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 * 查询列车 车次详情
 * @author liangwei
 *
 */
public class TaobaoTickChangServer {
    /**
        * 查询列车 车次详情
        * @author liangwei
        *
        */
    public List<Train> getlistTrains(Map mp) {
        try {
            String fromstationString = mp.get("fromStation").toString();
            try {
                fromstationString = Train12306StationInfoUtil.getThreeByName(fromstationString);
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("Error_TaobaoTickChangServer_getlistTrains", e);
            }
            String toStationString = mp.get("toStation").toString();
            try {
                toStationString = Train12306StationInfoUtil.getThreeByName(toStationString);
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("Error_TaobaoTickChangServer_getlistTrains", e);
            }
            List<Train> lsList = Server.getInstance().getAtomService()
                    .getDGTrainList(fromstationString, toStationString, mp.get("departDate").toString());
            return lsList;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
