package com.ccservice.b2b2c.atom.service.interfacetype;

/**
 * 火车票接口类型service
 * @time 2015年3月12日 上午10:00:58
 * @author fiend
 */
public interface ITrainInterfaceTypeService {
    /**
     * 查询订单的接口类型归属 
     * @param trainorderid
     * @return
     * @time 2015年3月12日 上午10:03:18
     * @author fiend
     */
    public int getTrainInterfaceType(long trainorderid);
}
