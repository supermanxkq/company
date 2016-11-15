package com.tenpay.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;



public class MeiTuanSendUtils {

    public String callService(String url, String req){
        StringBuffer responseMessage = null;
        InputStream in = null;
        BufferedReader br = null;
        try {
            responseMessage = new StringBuffer();
            HttpClient client = HttpClientUtils.getHTTPSClient();
            HttpPost post = new HttpPost(url);
            StringEntity reqEntity = new StringEntity(req, "UTF-8");
            reqEntity.setContentType("application/json;charset=UTF-8");
            post.setEntity(reqEntity);
            HttpResponse response = client.execute(post);
            int charCount = -1;
            in = response.getEntity().getContent();
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while ((charCount = br.read()) != -1) {
                responseMessage.append((char) charCount);
            }
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return responseMessage.toString();
    }
    
    
   
    public static void main(String[] args) {
        String s;
        String url="http://test.i.meituan.com/uts/train02/104/resignTicketNotify";
        String req ="{\"toStationCode\":\"XFB\",\"fromStationCode\":\"SUB\",\"reqToken\":\"5b3491ca3124efd856d4a85a58b4f8f5\",\"trainCode\":\"6021\",\"returnFact\":0,\"order12306Serial\":\"\",\"resignId\":\"149\",\"startTime\":\"13:49\",\"orderAmount\":1,\"toStationName\":\"香坊\",\"arriveTime\":\"13:59\",\"trainDate\":\"2016-09-28\",\"fromStationName\":\"孙家\",\"success\":true,\"tickets\":[{\"ticketNo\":\"E5988764822020095\",\"ticketId\":5082731,\"seatType\":\"1\",\"seatNo\":\"095号\",\"coachNo\":\"02车厢\",\"ticketType\":\"1\",\"passengerName\":\"袁怀秀\",\"certificateNo\":\"511324197501101927\",\"ticketPrice\":1}],\"orderId\":\"14714273920001\"}";
        try {
            s = new MeiTuanSendUtils().callService(url, req);
            System.out.println(s);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        
    }
//    
//    public static void main(String[] args) {
//        ClassLoader classLoader = MeiTuanSendUtils.class.getClassLoader();
//        URL resource = classLoader.getResource(" org/apache/http/message/BasicLineParser");
//        System.out.println(resource);
//    }
    
}
