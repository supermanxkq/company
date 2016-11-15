package com.ccservice.b2b2c.atom.servlet.job.thread;

import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.job.JobAlitripTraindetails;
import com.ccservice.b2b2c.base.trainno.TrainNo;

/**
 * 阿里旅行 
 * 2.3.5车次详细信息接口 
 * 定时任务
 * @time 2015年3月17日 上午11:22:06
 * @author chendong
 */
public class JobAlitripTraindetailsThread extends Thread {
    String base_chexing = "G,D,T,Z,K,C,S,Y,1,2,3,4,5,6,7,8,9";

    List<TrainNo> list;

    public JobAlitripTraindetailsThread(List<TrainNo> list) {
        super();
        this.list = list;
    }

    //     String base_chexing = "K50";
    @Override
    public void run() {
        execute();
    }

    public void main(String[] args) {
        execute();
    }

    private void execute() {
        for (int j = 0; j < list.size(); j++) {
            TrainNo trainno = list.get(j);
            List<TrainNo> list2 = Server
                    .getInstance()
                    .getTrainService()
                    .findAllTrainNo("WHERE C_STATION_TRAIN_CODE = '" + trainno.getStation_train_code() + "'",
                            " ORDER BY C_STATION_NO ", -1, 0);
            JobAlitripTraindetails.getonelinebytrainno(list2);
        }
    }

}
