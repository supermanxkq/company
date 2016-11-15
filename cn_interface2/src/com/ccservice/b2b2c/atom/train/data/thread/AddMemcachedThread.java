package com.ccservice.b2b2c.atom.train.data.thread;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.server.MemCached;
import com.ccservice.b2b2c.atom.servlet.TrainSearch;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 异步处理12306缓存价格信息
 * 
 * @time 2015年5月11日 下午1:18:45
 * @author chendong
 */
public class AddMemcachedThread extends Thread {
    public static void main(String[] args) {
        String mcckey = "chendongtest201507261653";
        String value = "chendongtest201507261653";
        Date date1 = new Date(1000 * 1 * 5);//40秒的缓存
        boolean addresult = MemCached.getInstance().add(mcckey, value, date1);
        TrainSearch.println(addresult);
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            TrainSearch.println(MemCached.getInstance().get(mcckey));

        }
    }

    String mcckey;

    String value;

    Date date;

    String time;

    int type;//1:存余票缓存,2:存价格信息到数据库和缓存

    public AddMemcachedThread(String mcckey, String value, Date date, int type, String time) {
        this.mcckey = mcckey;
        this.value = value;
        this.date = date;
        this.type = type;
        this.time = time;
    }

    @Override
    public void run() {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        if (type == 1) {//1:存余票缓存
            Date date1 = new Date(1000 * 1 * 40);//40秒的缓存
            boolean addresult = MemCached.getInstance().add(mcckey, value, date1);
            TrainSearch.println(addresult);
        }
        else if (type == 2) {//2:存价格信息到数据库和缓存
            MemCached.getInstance().delete(mcckey);
            boolean addresult = MemCached.getInstance().add(mcckey, value, date);
            //             TrainSearch.println(mcckey + "========add===" + addresult);
            if (value.indexOf("3\":\"-2\"") < 0 && value.indexOf("\"1\":\"-2\"") < 0
                    && value.indexOf("\"6\":\"-2\"") < 0 && value.indexOf("\"4\":\"-2\"") < 0) {
                if (this.date == null) {
                    try {
                        Date d1 = df.parse(df.format(new Date()));
                        Date d2 = df.parse("24:00:00");
                        long diff = d2.getTime() - d1.getTime();
                        long day = diff / (24 * 60 * 60 * 1000);
                        long hour = (diff / (60 * 60 * 1000) - day * 24);
                        long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
                        long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                        this.date = new Date(1000 * s * min * hour);//缓存到凌晨00:00释放掉
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    MemCached.getInstance().add(mcckey, value, date);
                }
                catch (Exception e) {

                }
                setpricefromdb(mcckey, value, time);
            }
            else {
                try {
                    //                    getSystemService().findMapResultBySql("delete from T_TRAINPRICE where C_MCCKEY='" + mcckey + "'",
                    //                            null);
                }
                catch (Exception e) {
                }
            }
        }

    }

    /**
     * 修改数据库里的价格
     * 
     * @param key
     * @param value
     * @param time
     * @time 2015年7月28日 上午11:13:47
     * @author chendong
     */
    public void setpricefromdb(String mcckey, String value, String time) {
        //如果值的长度小于10或者value是-1就什么都不操作
        if (value.length() < 10 || "-1".equals(value)) {
            return;
        }
        //判断数据库里是否有数据
        int count_trainprice = getSystemService().countAdvertisementBySql(
                "select count(*) from T_TRAINPRICE with(nolock) where C_MCCKEY='" + mcckey + "' AND QueryDate='" + time
                        + "'");
        String sqlinsert = "";
        if (count_trainprice == 0) {
            sqlinsert =
            //                    "delete from T_TRAINPRICE where C_MCCKEY='" + mcckey + "' AND QueryDate='" + time + "';"+
            "insert into T_TRAINPRICE(C_MCCKEY,C_PRICE,QueryDate) values ('" + mcckey + "','" + value + "','" + time
                    + "')";
        }
        else {
            sqlinsert = "update T_TRAINPRICE set C_PRICE=" + value + ",c_mtime='"
                    + (new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date()) + "',QueryDate='"
                    + time + "' where C_MCCKEY='" + mcckey + "' AND QueryDate='" + time + "'";
        }
        try {
            getSystemService().excuteGiftBySql(sqlinsert);
        }
        catch (Exception e) {
        }
    }

    public static ISystemService getSystemService() {
        HessianProxyFactory factory = new HessianProxyFactory();
        String search_12306yupiao_service_url = PropertyUtil.getValue("search_12306yupiao_service_url",
                "Train.properties");
        try {
            return (ISystemService) factory.create(ISystemService.class, search_12306yupiao_service_url
                    + ISystemService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
