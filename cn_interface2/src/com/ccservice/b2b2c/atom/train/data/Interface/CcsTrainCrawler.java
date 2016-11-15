package com.ccservice.b2b2c.atom.train.data.Interface;

import java.util.List;

import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.train.Train;

/**
 * 火车票抓取interface
 * 
 * @time 2014年12月2日 下午11:07:48
 * @author chend
 */
public interface CcsTrainCrawler {
    /**
     * String startcity, String endcity, String time param
     * 
     * @param param
     *            抓取网页时传入的参数，一般有出发城市三字码、到达城市三字码、出发时间......
     * @return 网页代码
     */
    String getHtml(String startcity, String endcity, String time, FlightSearch param);

    /**
     * 
     * @param startcity
     *            出发三字码
     * @param endcity
     *            到达三字码
     * @param time
     *            出发时间 格式2014-12-14
     * @param param
     * @param purposecodes
     *            订票类型
     * @return
     * @author luoqingxin
     */
    String getHtml(String startcity, String endcity, String time, FlightSearch param, String purposecodes);

    /**
     * 
     * @param html
     * @param startcity
     *            出发三字码
     * @param endcity
     *            到达三字码
     * @param time
     *            出发时间 格式2014-12-14
     * @param param
     * @return
     * @time 2014年12月14日 下午1:45:56
     * @author chendong
     */
    List<Train> process(String html, String startcity, String endcity, String time, FlightSearch param);

    /**
     * 
     * @param html
     * @param startcity
     *            出发三字码
     * @param endcity
     *            到达三字码
     * @param time
     *            出发时间 格式2014-12-14
     * @param param
     * @param purposecodes
     *            订票类型
     * @author luoqingxin
     */
    List<Train> process(String html, String startcity, String endcity, String time, FlightSearch param,
            String purposecodes);
}
