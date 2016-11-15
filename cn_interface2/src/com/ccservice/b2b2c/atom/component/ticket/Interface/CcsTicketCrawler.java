package com.ccservice.b2b2c.atom.component.ticket.Interface;

import java.util.List;

import com.ccservice.b2b2c.base.flightinfo.FlightInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;

public interface CcsTicketCrawler {
    /**
     * @param param 抓取网页时传入的参数，一般有出发城市三字码、到达城市三字码、出发时间......
     * @return      网页代码
     */
    String getHtml(FlightSearch param);

    /**
     * @param html   抓取的网页
     * @param param  抓取网页的链接的参数
     * @return       封装的结果bean，包含航班list和process状态
     */
    List<FlightInfo> process(String html, FlightSearch param);
}
