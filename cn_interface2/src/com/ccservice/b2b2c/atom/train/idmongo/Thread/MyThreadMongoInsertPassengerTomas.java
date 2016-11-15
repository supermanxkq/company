package com.ccservice.b2b2c.atom.train.idmongo.Thread;

import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.train.idmongo.MongoLogic;
import com.ccservice.b2b2c.atom.train.idmongo.mem.MongoInsertPassengerTomasMem;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class MyThreadMongoInsertPassengerTomas extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                //是否启动
                if (MongoInsertPassengerTomasMem.isStart) {
                    //添加1天乘客数据到Mongo中
                    addEachDay();
                }
            }
            catch (Exception e1) {
                ExceptionUtil.writelogByException("MyThreadMongoInsertPassengerTomas_Exception", e1);
            }
            try {
                //睡眠5秒，怕死
                Thread.sleep(5000L);
            }
            catch (Exception e) {
                //就吃这个异常，我喜欢*_*
            }
        }
    }

    /**
     * 添加1天乘客数据到Mongo中
     * 
     * @throws Exception
     * @time 2016年11月11日 下午6:56:08
     * @author fiend
     */
    @SuppressWarnings("rawtypes")
    private void addEachDay() throws Exception {
        //查询1天的乘客数据
        List list = Server
                .getInstance()
                .getSystemService()
                .findMapResultByProcedure(
                        " [sp_Trainpassenger_SelectNameIDNumberByTomas] @daychange="
                                + MongoInsertPassengerTomasMem.daychange);
        MongoLogic mongoLogic = new MongoLogic();
        for (int i = 0; i < list.size(); i++) {
            Map map = (Map) list.get(i);
            //插入Mongo
            mongoLogic.AddPassengerTomasIDString(map.get("C_IDNUMBER").toString(), map.get("C_NAME").toString());
        }
        //内存中日期+1，并且记录LOG
        MongoInsertPassengerTomasMem.addDaychange();
    }
}
