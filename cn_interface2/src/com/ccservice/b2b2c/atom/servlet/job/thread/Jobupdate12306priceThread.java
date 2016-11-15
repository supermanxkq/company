package com.ccservice.b2b2c.atom.servlet.job.thread;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.atom.train.data.ZhuanfaUtil;
import com.ccservice.b2b2c.base.trainno.TrainNo;
import com.ccservice.b2b2c.util.TrainUtil;

public class Jobupdate12306priceThread extends Thread {
    String train_no;//列车号

    String station_train_code;//车次

    int updatetype;//0更新12306的价格信息(老) 1更新12306的价格信息(新)(12306价格数据)  2更新上下铺的价格（58数据）

    public Jobupdate12306priceThread(String train_no, String station_train_code, int updatetype) {
        super();
        this.train_no = train_no;
        this.station_train_code = station_train_code;
        this.updatetype = updatetype;
    }

    public Jobupdate12306priceThread(String station_train_code, int updatetype) {
        super();
        this.updatetype = updatetype;
        this.station_train_code = station_train_code;
    }

    @Override
    public void run() {
        if (this.updatetype == 0) {
            update12306pricefor(train_no, station_train_code);
        }
        else if (this.updatetype == 1 || this.updatetype == 2) {
            System.out.println(station_train_code);
            String sql_string = "SELECT top 1 c_train_no trainno FROM T_TRAINNO with(nolock) where c_station_train_code='"
                    + station_train_code + "' and C_STATION_NO='01'";
            List trainnoList = Server.getInstance().getSystemService().findMapResultBySql(sql_string, null);
            if (trainnoList.size() > 0) {
                if (this.updatetype == 1) {//更新12306的价格信息
                    Map map = (Map) trainnoList.get(0);
                    String trainno = map.get("trainno").toString().trim();
                    //                String train_date = TimeUtil.gettodaydatebyfrontandback(1, 10);
                    //                String liecheshike = TrainUtil.getliecheshike(trainno, train_date, 10);
                    //                List<TrainNo> trainnos = TrainUtil.jiexi12306_checichaxun_shuju(liecheshike, trainno);
                    //                chuli_checichaxun(trainnos);
                    //                System.out.println(liecheshike);

                    update12306pricefor(trainno, station_train_code);
                }
                else if (this.updatetype == 2) {//2更新上下铺的价格（58数据）

                }
            }
            else {
                try {
                    String sqlinsert = "update T_trainprice set c_mtime='" + new Timestamp(System.currentTimeMillis())
                            + "' where C_MCCKEY like '" + station_train_code + "_%'";
                    //+ key + "','" + value                   + "','" + new Timestamp(System.currentTimeMillis()) + "')";
                    Server.getInstance().getSystemService().excuteEaccountBySql(sqlinsert);
                }
                catch (Exception e) {
                }
            }
        }
    }

    /**
     * 处理查询到的车次信息
     * 
     * @param trainnos
     * @time 2015年5月5日 下午8:58:48
     * @author chendong
     */
    private void chuli_checichaxun(List<TrainNo> trainnos) {
        //#TODO 更新数据的时候以后再写因为这里有 里程的问题，看下这个方法怎么写 2015年5月5日21:00:35 chendong
        //        String delete_sql = "DELETE FROM T_TRAINNO WHERE C_STATION_TRAIN_CODE like '%" + station_train_code + "%'";
        //        Server.getInstance().getSystemService().excuteAdvertisementBySql(delete_sql);
        //        for (int j = 0; j < trainnos.size(); j++) {
        //            try {
        //                Server.getInstance().getTrainService().createTrainNo(trainnos.get(j));
        //            }
        //            catch (SQLException e) {
        //                e.printStackTrace();
        //            }
        //        }
    }

    /**
     * 根据车次和车号更新这个车次所有的出发到达站的价格
     * 
     * @param list1
     * @param train_no 列车号 ：380000T19706
     * @param station_train_code 车次
     * @time 2015年3月3日 上午9:52:52
     * @author chendong
     */
    private void update12306pricefor(String train_no, String station_train_code) {
        String where_sql = "where C_STATION_TRAIN_CODE='" + station_train_code + "' ";
        List list1 = Server.getInstance().getTrainService().findAllTrainNo(where_sql, " ORDER BY C_STATION_NO ", -1, 0);
        // 创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int z1 = 0; z1 < list1.size(); z1++) {
            TrainNo trainno_z1 = (TrainNo) list1.get(z1);
            for (int z2 = z1 + 1; z2 < list1.size(); z2++) {
                TrainNo trainno_z2 = (TrainNo) list1.get(z2);
                String s_no = trainno_z1.getStation_no().trim();
                String e_no = trainno_z2.getStation_no().trim();
                try {
                    // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
                    Thread t1 = null;
                    int updatetype = 1;//0更新12306的价格信息(老) 1更新12306的价格信息(新)  2更新上下铺的价格
                    // 将线程放入池中进行执行
                    t1 = new Jobupdate12306priceThread_update12306pricefor(train_no, station_train_code, s_no, e_no);
                    pool.execute(t1);

                    //                    update12306price(train_no, station_train_code, s_no, e_no);
                }
                catch (Exception e) {
                    System.out.println("error:" + train_no + ":" + station_train_code);
                }
            }
        }
        pool.shutdown();
    }

    public void deletepricefromdb(String mcckey) {
        try {
            String sqldelete = "delete from T_trainprice where C_MCCKEY='" + mcckey + "'";
            Server.getInstance().getSystemService().excuteEaccountBySql(sqldelete);
        }
        catch (Exception e) {
        }
    }

}
