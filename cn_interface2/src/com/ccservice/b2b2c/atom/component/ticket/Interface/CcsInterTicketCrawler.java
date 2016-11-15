package com.ccservice.b2b2c.atom.component.ticket.Interface;

import com.ccservice.b2b2c.base.flightinfo.FlightSearch;
import com.ccservice.b2b2c.base.interticket.AllRouteBean;

public interface CcsInterTicketCrawler {
    /**
     * @param param 抓取网页时传入的参数，一般有出发城市三字码、到达城市三字码、出发时间......
     * @return      网页代码
     */
    String getHtml(FlightSearch param, String url, String cookie);

    /**
     * @param html   抓取的网页
     * @param param  抓取网页的链接的参数
     * @return       封装的结果bean，包含航班list和process状态
     */
    AllRouteBean process(String html, FlightSearch param);
}
