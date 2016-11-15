package com.ccservice.b2b2c.atom.service12306;

import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.atom.train.TrainHelper;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 改签退，获取最佳车次+座席+日期
 * @author WH
 * @version 2.0
 * @remark 2016年3月18日，新逻辑调整
 */

public class SelectBestTrainForReturnTicket {

    //时间格式化
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    //时分格式化
    private SimpleDateFormat shiFenFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * 根据原发车时间判断是否走改签退
     * @param oldDepartTime 原发车时间，Trainticket类departtime字段
     * @return true:可走改签退；false:不能走改签退
     */
    public boolean GoChangeRefund(String oldDepartTime) {
        //结果
        boolean result = false;
        //判断
        try {
            /**
            |-->只有如下两种可能：
            |-->24小时及以内(20%)改到24小时以外(10%)，如当前13点，发车时间为第二天的12点，改到第二天13点以后(时间为发车时间当天且之后)；
            |-->24小时至48小时，发车时间在第三天(10%)改到48小时以外(5%)，如当前13点，发车时间为第三天12点，改到第三天13点以后(时间为发车时间当天且之后)；
            */
            //发车
            String departDay = oldDepartTime.split(" ")[0];
            //当天
            String currentDay = ElongHotelInterfaceUtil.getCurrentDate();
            //天数
            int subDays = ElongHotelInterfaceUtil.getSubDays(currentDay, departDay);
            //第2、3天[0开始]
            if (subDays == 1 || subDays == 2) {
                //春运时间
                String[] springDateTimes = PropertyUtil.getValue("SpringDateTime").split("@");
                //改签到春运期间
                if (ElongHotelInterfaceUtil.getSubDays(springDateTimes[0], departDay) >= 0
                        && ElongHotelInterfaceUtil.getSubDays(departDay, springDateTimes[1]) >= 0) {
                    return result;
                }
                //发车时间
                long trainTime = shiFenFormat.parse(oldDepartTime).getTime();
                //发车时间-当前时间
                long timesub = trainTime - System.currentTimeMillis();
                //24小时及以内、48小时及以内
                if ((subDays == 1 && timesub <= 24 * 60 * 60 * 1000)
                        || (subDays == 2 && timesub <= 48 * 60 * 60 * 1000)) {
                    result = true;
                }
            }
        }
        catch (Exception e) {

        }
        //返回
        return result;
    }

    /**
     * 获取改签最佳车次>>新逻辑
     * @time 2016年3月17日 下午1:16:03
     * @version 2.0
     * @param from_station_name 出发站
     * @param to_station_name 到达站
     * @param oldDepartTime 原发车时间
     * @param oldPrice 原票价
     * @param oldFee 原手续费
     * @备注
     * 1、手续费>>(0, 24H]为20%、(24H, 48H]为10%、(48H, 15D]为5%、(15D, 60D]为0%；
     * 2、改签到当天退票手续费为最高的20%，故新日期非当天；
     * 3、如果改签车次发车时间<=原发车时间，则新手续费>=原手续费，帮 改签车次发车时间>原发车时间；
     * 4、改签新规定>>48小时以上改签到15天以上，再退票还收5%手续费；
     * 5、可改签的情况
     *      |-->A、24小时及以内(20%)改到24小时以外(10%)，如当前13点，发车时间为第二天的12点，改到第二天13点以后(时间为发车时间当天且之后)；
     *      |-->B、24小时至48小时，发车时间在第二天，距发车时间范围是(24H, 24H + 24H - 7H = 41H)，改到第二天当天，手续费还是10%，舍弃；
     *      |-->C、24小时至48小时，发车时间在第三天(10%)改到48小时以外(5%)，如当前13点，发车时间为第三天12点，改到第三天13点以后(时间为发车时间当天且之后)；
     *      |-->D、综合分析，只有A、C项符合
     * 6、如果新手续费>=原手续费，直接舍弃     
     */
    public Train getBest(String from_station_name, String to_station_name, String oldDepartTime, float oldPrice,
            float oldFee) throws Exception {
        //不改签退
        if (!GoChangeRefund(oldDepartTime)) {
            return new Train();
        }
        //发车日期，都在原发车当天
        String endDate = oldDepartTime.split(" ")[0];
        String startDate = oldDepartTime.split(" ")[0];
        //改签退预留时间，单位：分钟
        int reservedTime = Integer.parseInt(PropertyUtil.getValue("reservedTime"));
        //最低退票费
        float minReturnFee = Float.parseFloat(PropertyUtil.getValue("minReturnFee"));
        //多少天以后，退票免费，当前15天
        int noFeeDayStart = Integer.parseInt(PropertyUtil.getValue("noFeeDayStart"));
        //春运时间
        String[] springDateTimes = PropertyUtil.getValue("SpringDateTime").split("@");
        //火车票退票手续费规则
        String[] trainRefundFees = PropertyUtil.getValue("TrainRefundFee").split("@");
        //时间误差，单位：分钟
        int timeWuCha = Integer.parseInt(PropertyUtil.getValue("SelectTrainTimeWuCha"));
        //手续费小数舍弃规则
        String[] trainRefundFeeDecimals = PropertyUtil.getValue("TrainRefundFeeDecimal").split("@");
        //春运期间，改签票退票手续费
        float springChangeReturnFee = Float.parseFloat(PropertyUtil.getValue("SpringChangeReturnFee"));
        //最低手续费车次
        Train best = getLowest(from_station_name, to_station_name, startDate, endDate, oldPrice, oldFee,
                trainRefundFees, trainRefundFeeDecimals, springDateTimes, springChangeReturnFee, noFeeDayStart,
                minReturnFee, oldDepartTime, timeWuCha, reservedTime);
        //取最高价格+座席
        String[] maxData = getMaxByTrain(best, oldPrice).split("@");
        best.setSeattypeval(maxData[1]);
        best.setDistance(Float.parseFloat(maxData[0]));//暂存最高价
        //返回
        return best;
    }

    /**
     * @param from_station_name 出发站
     * @param to_station_name 到达站
     * @param noFeeDayStart 多少天以后，退票免费，当前15天
     * @param springChangeReturnFee 春运期间，改签票退票手续费
     * @param springDateTimes 春运时间
     * @param trainRefundFeeDecimals 手续费小数舍弃规则
     * @param trainRefundFees 火车票退票手续费规则
     * @param oldDepartTime 原出发时间
     */
    private Train getLowest(String from_station_name, String to_station_name, String startDate, String endDate,
            float oldPrice, float oldFee, String[] trainRefundFees, String[] trainRefundFeeDecimals,
            String[] springDateTimes, float springChangeReturnFee, int noFeeDayStart, float minReturnFee,
            String oldDepartTime, int timeWuCha, int reservedTime) {
        //最优
        Train best = new Train();
        //查询
        try {
            best = getTrain(from_station_name, to_station_name, startDate, endDate, oldPrice, oldFee, trainRefundFees,
                    trainRefundFeeDecimals, springDateTimes, springChangeReturnFee, noFeeDayStart, minReturnFee,
                    oldDepartTime, timeWuCha, reservedTime);
        }
        catch (Exception e) {
            best = new Train();
        }
        //返回
        return best;
    }

    /**
     * 获取日期范围最低手续费的那个train，包括endDate
     */
    private Train getTrain(String from_station_name, String to_station_name, String startDate, String endDate,
            float oldPrice, float oldFee, String[] trainRefundFees, String[] trainRefundFeeDecimals,
            String[] springDateTimes, float springChangeReturnFee, int noFeeDayStart, float minReturnFee,
            String oldDepartTime, int timeWuCha, int reservedTime) throws Exception {
        //余票
        TrainHelper helper = new TrainHelper();
        //key: 最低手续费
        Map<Float, Train> TrainFeeMap = new HashMap<Float, Train>();
        //天数
        int days = ElongHotelInterfaceUtil.getSubDays(startDate, endDate);
        //循环
        for (int i = 0; i <= days; i++) {
            //日期
            String date = ElongHotelInterfaceUtil.getAddDate(startDate, i);
            //对应车次
            List<Train> list = helper.queryTrain(from_station_name, to_station_name, date);
            //无车次结果
            if (list == null || list.size() == 0) {
                continue;
            }
            //最低手续费车次
            Train train = getDayMinTrain(list, oldPrice, date, trainRefundFees, trainRefundFeeDecimals,
                    springDateTimes, springChangeReturnFee, noFeeDayStart, minReturnFee, timeWuCha, oldDepartTime,
                    reservedTime);
            //最低手续费
            Float minFee = train.getQtxb_price();
            //最高价>>手续费由20%-->10%、10%-->5%，价格越高，差价就越低，新手续费(票差价*原费率 + 新票价*新费率)就越低
            float maxPrice = train.getDistance();
            //有票
            if (maxPrice > 0 && minFee != null && minFee.floatValue() > 0) {
                //覆盖标识
                boolean putflag = false;
                //已存在，取价格高的，价格一样时，取日期靠后的
                if (TrainFeeMap.containsKey(minFee)) {
                    //原车次
                    Train oldTrain = TrainFeeMap.get(minFee);
                    //当前最高价>=原最高价
                    putflag = maxPrice >= Float.parseFloat(getMaxByTrain(oldTrain, oldPrice).split("@")[0]);
                }
                else {
                    putflag = true;
                }
                if (putflag) {
                    //发车日期
                    train.setStartdate(date);
                    //存在时，日期靠后的覆盖
                    TrainFeeMap.put(minFee, train);
                }
            }
        }
        if (TrainFeeMap.size() > 0) {
            //取最低价格车次
            float currentMin = -1f;
            for (float minFee : TrainFeeMap.keySet()) {
                if (minFee < currentMin || currentMin == -1f) {
                    currentMin = minFee;
                }
            }
            if (currentMin > 0) {
                //与当前车票手续费比较
                String[] oldDepartTimes = oldDepartTime.split(" ");
                //程序计算原手续费，用于比较，不相等，不进行改签
                float tempOldFee = getRefundFee(oldPrice, oldDepartTimes[0], oldDepartTimes[1], trainRefundFees,
                        trainRefundFeeDecimals, springDateTimes, springChangeReturnFee, noFeeDayStart, minReturnFee,
                        timeWuCha, 1, "");
                //原手续费大于现手续费
                if (oldFee > currentMin && oldFee == tempOldFee) {
                    return TrainFeeMap.get(currentMin);
                }
            }
        }
        return new Train();
    }

    /**
     * 获取每天的最低手续费的那个train
     * @param oldPrice 原票价格
     * @param oldDepartTime 原发车时间
     */
    private Train getDayMinTrain(List<Train> list, float oldPrice, String date, String[] trainRefundFees,
            String[] trainRefundFeeDecimals, String[] springDateTimes, float springChangeReturnFee, int noFeeDayStart,
            float minReturnFee, int timeWuCha, String oldDepartTime, int reservedTime) throws Exception {
        Train ret = new Train();
        //当前最小手续费
        float currentMinFee = -1f;
        //当前最小发车时间
        long currentMinTime = -1l;
        //与当前车票手续费比较
        String[] oldDepartTimes = oldDepartTime.split(" ");
        //循环当前日期所有车次
        for (Train train : list) {
            if (ElongHotelInterfaceUtil.StringIsNull(train.getMemo())) {
                //车次最高价
                float maxPrice = Float.parseFloat(getMaxByTrain(train, oldPrice).split("@")[0]);
                //价格错误、高改
                if (maxPrice <= 0 || maxPrice > oldPrice) {
                    continue;
                }
                //发车时间
                long minTime = shiFenFormat.parse(date + " " + train.getStarttime()).getTime();
                //分界时间>>发车日期的当前时、分(N*24小时) + 预留时间
                long boundaryTime = shiFenFormat.parse(date + " " + timeFormat.format(new Date())).getTime()
                        + reservedTime * 60 * 1000;
                //要在N*24小时 + 1小时之后
                if (minTime < boundaryTime) {
                    continue;
                }
                //差价
                float chaPrice = ElongHotelInterfaceUtil.floatSubtract(oldPrice, maxPrice);
                //新车手续费
                float newFee = getRefundFee(maxPrice, date, train.getStarttime(), trainRefundFees,
                        trainRefundFeeDecimals, springDateTimes, springChangeReturnFee, noFeeDayStart, minReturnFee,
                        timeWuCha, 2, "");
                //差价手续费，平改暂为0，按原车票发车时间
                float chaFee = chaPrice == 0 ? 0 : getRefundFee(chaPrice, oldDepartTimes[0], oldDepartTimes[1],
                        trainRefundFees, trainRefundFeeDecimals, springDateTimes, springChangeReturnFee, noFeeDayStart,
                        minReturnFee, timeWuCha, 1, "");
                //手续费正确
                if (newFee > 0 && (chaFee > 0 || chaPrice == 0)) {
                    //新手续费
                    float minFee = ElongHotelInterfaceUtil.floatAdd(newFee, chaFee);
                    //手续费小的，相同时取发车晚的
                    if (currentMinFee == -1f || minFee < currentMinFee
                            || (minFee == currentMinFee && minTime > currentMinTime)) {
                        ret = train;
                        currentMinFee = minFee;
                        currentMinTime = minTime;
                        ret.setDistance(maxPrice);//暂存最高价格
                        ret.setQtxb_price(currentMinFee);//暂存最低手续费
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 计算退票手续费
     * @ramek 当前最低手续费2元，票价低于2元的，手续费=票价
     * @param price 票价
     * @param departDate 发车日期
     * @param departTime 发车时间
     * @param refundType 1:直接退，2:改签退
     * @param transDate 交易时间，用于计算该时间到发车时间的手续费
     */
    private float getRefundFee(float price, String departDate, String departTime, String[] TrainRefundFees,
            String[] TrainRefundFeeDecimals, String[] SpringDateTimes, float SpringChangeReturnFee, int noFeeDayStart,
            float minReturnFee, int timeWuCha, int refundType, String transDate) throws Exception {
        if (price <= 0) {
            throw new Exception("Price Is Error.");
        }
        float retFee = -1f;
        //判断春运期间
        int SprintStartDays = ElongHotelInterfaceUtil.getSubDays(SpringDateTimes[0], departDate);
        int SprintEndDays = ElongHotelInterfaceUtil.getSubDays(departDate, SpringDateTimes[1]);
        //改签到春运期间
        if (refundType == 2 && SprintStartDays >= 0 && SprintEndDays >= 0) {
            retFee = SpringChangeReturnFee;
        }
        else {
            //不要手续费时间
            long noFeeTime = noFeeDayStart * 24 * 60 * 60 * 1000;
            //发车时间
            String newDepartTime = departDate + " " + departTime;
            //发车时间-当前时间
            long timesub = 0;
            //交易时间为空
            if (ElongHotelInterfaceUtil.StringIsNull(transDate)) {
                timesub = shiFenFormat.parse(newDepartTime).getTime() - System.currentTimeMillis() - timeWuCha * 60
                        * 1000;
            }
            else {
                timesub = shiFenFormat.parse(newDepartTime).getTime() - shiFenFormat.parse(transDate).getTime()
                        - timeWuCha * 60 * 1000;
            }
            //不收手续费
            if (timesub >= noFeeTime) {
                return 0f;
            }
            //48H-15D-5@24H-48H-10@2H-24H-20
            for (String fee : TrainRefundFees) {
                String[] fees = fee.split("-");
                String start = fees[0];
                String end = fees[1];
                //类型
                String startType = start.substring(start.length() - 1);
                String endType = end.substring(end.length() - 1);
                //值
                long startValue = Long.parseLong(start.substring(0, start.length() - 1));
                long endValue = Long.parseLong(end.substring(0, end.length() - 1));
                //时间转Long
                long startLong = 0;
                long endLong = 0;
                //小时
                if ("H".equalsIgnoreCase(startType)) {
                    startLong = startValue * 60 * 60 * 1000;
                }
                //天
                else if ("D".equalsIgnoreCase(startType)) {
                    startLong = startValue * 24 * 60 * 60 * 1000;
                }
                else {
                    throw new Exception("TrainRefundFee Is Error.");
                }
                //小时
                if ("H".equalsIgnoreCase(endType)) {
                    endLong = endValue * 60 * 60 * 1000;
                }
                //天
                else if ("D".equalsIgnoreCase(endType)) {
                    endLong = endValue * 24 * 60 * 60 * 1000;
                }
                else {
                    throw new Exception("TrainRefundFee Is Error.");
                }
                if (timesub >= startLong && timesub < endLong) {
                    //手续费
                    retFee = Float.parseFloat(fees[2]);
                    break;
                }
            }
        }
        if (retFee < 0) {
            return retFee;
        }
        if (retFee == 0) {
            throw new Exception("TrainRefundFee Is Error.");
        }
        //百分比
        retFee = ElongHotelInterfaceUtil.floatMultiply(retFee, 0.01f);
        //手续费
        retFee = ElongHotelInterfaceUtil.floatMultiply(price, retFee);
        //比较最低手续
        if (retFee <= minReturnFee) {
            //票价小于最低手续费
            if (price < minReturnFee) {
                return price;
            }
            else {
                return minReturnFee;
            }
        }
        //取小数
        String strFee = String.valueOf(retFee);
        int idx = strFee.indexOf(".");
        if (idx > 0) {
            //手续费小数
            float xiaoShuFloat = Float.parseFloat("0" + strFee.substring(idx));
            //小数>0
            if (xiaoShuFloat > 0) {
                //手续费取整
                retFee = ElongHotelInterfaceUtil.floatSubtract(retFee, xiaoShuFloat);
                //0-0.25-0@0.25-0.75-0.5@0.75-1-1
                for (String decimal : TrainRefundFeeDecimals) {
                    String[] decimals = decimal.split("-");
                    float start = Float.parseFloat(decimals[0]);
                    float end = Float.parseFloat(decimals[1]);
                    if (xiaoShuFloat >= start && xiaoShuFloat < end) {
                        xiaoShuFloat = Float.parseFloat(decimals[2]);
                        retFee = ElongHotelInterfaceUtil.floatAdd(retFee, xiaoShuFloat);
                        break;
                    }
                }
            }
        }
        return retFee;
    }

    /**
     * 获取每个train里的有票且不高于原票价格的最高价格
     * @param oldPrice 原票价格
     * @remark P:特等座，M:一等座，O:二等座，F:动卧，E:特等软座，9:商务座，8：二等软座，7：一等软座，6:高级软卧，4:软卧，3:硬卧，2:软座，1:硬座
     */
    private String getMaxByTrain(Train train, float oldPrice) {
        int swzyp = StringToInt(train.getSwzyp());//商务座
        int tdzyp = StringToInt(train.getTdzyp());//特等座
        int rz1yp = StringToInt(train.getRz1yp());//一等座
        int rz2yp = StringToInt(train.getRz2yp());//二等座
        int gwyp = StringToInt(train.getGwyp());//高级软卧
        int rwyp = StringToInt(train.getRwyp());//软卧
        int ywyp = StringToInt(train.getYwyp());//硬卧
        int rzyp = StringToInt(train.getRzyp());//软座
        int yzyp = StringToInt(train.getYzyp());//硬座
        int wzyp = StringToInt(train.getWzyp());//无座
        //最高价
        float maxPrice = 0f;
        //对应座席
        String seatType = "-1";
        //商务座
        if (swzyp > 0 && RightPrice(train.getSwzprice(), oldPrice)) {
            seatType = "9";
            maxPrice = train.getSwzprice();
        }
        //特等座
        else if (tdzyp > 0 && RightPrice(train.getTdzprice(), oldPrice)) {
            seatType = "P";
            maxPrice = train.getTdzprice();
        }
        //一等座
        else if (rz1yp > 0 && RightPrice(train.getRz1price(), oldPrice)) {
            seatType = "M";
            maxPrice = train.getRz1price();
        }
        //二等座
        else if ((wzyp > 0 || rz2yp > 0) && RightPrice(train.getRz2price(), oldPrice)) {
            seatType = "O";
            maxPrice = train.getRz2price();
        }
        //高级软卧
        else if (gwyp > 0 && RightPrice(train.getGwsprice(), oldPrice)) {
            seatType = "6";
            maxPrice = train.getGwsprice();
        }
        //软卧
        else if (rwyp > 0 && RightPrice(train.getRwsprice(), oldPrice)) {
            seatType = "4";
            maxPrice = train.getRwsprice();
        }
        //硬卧
        else if (ywyp > 0 && RightPrice(train.getYwsprice(), oldPrice)) {
            seatType = "3";
            maxPrice = train.getYwsprice();
        }
        //软座
        else if (rzyp > 0 && RightPrice(train.getRzprice(), oldPrice)) {
            seatType = "2";
            maxPrice = train.getRzprice();
        }
        //硬座、无座
        else if ((wzyp > 0 || yzyp > 0) && RightPrice(train.getYzprice(), oldPrice)) {
            seatType = "1";
            maxPrice = train.getYzprice();
        }
        return maxPrice + "@" + seatType;
    }

    private int StringToInt(String str) {
        try {
            return Integer.parseInt(str);
        }
        catch (Exception e) {
            return 0;
        }
    }

    /**
     * 正确价格
     * @param newPrice 新票价格
     * @param oldPrice 原票价格
     */
    private boolean RightPrice(Float newPrice, float oldPrice) {
        return newPrice != null && newPrice > 0 && newPrice <= oldPrice;
    }

}