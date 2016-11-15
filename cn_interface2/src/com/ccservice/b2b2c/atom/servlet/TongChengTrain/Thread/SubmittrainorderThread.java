package com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread;

import java.util.concurrent.Callable;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.base.train.Trainorder;

/**
 * 同步的占座的线程的方法
 * 如果超时，异步转同步
 * @time 2015年1月11日 下午12:23:35
 * @author chendong
 */
public class SubmittrainorderThread extends TongchengSupplyMethod implements Callable<Trainorder> {
    Trainorder trainorder;

    JSONObject json;

    String from_station_code;

    String to_station_code;

    int r1;

    public SubmittrainorderThread(Trainorder trainorder, JSONObject json, int r1) {
        super();
        this.trainorder = trainorder;
        this.json = json;
        this.r1 = r1;
    }

    @Override
    public Trainorder call() throws Exception {
        this.trainorder = TCTrainOrdering(this.trainorder, r1, from_station_code, to_station_code);
        return this.trainorder;
    }

}
