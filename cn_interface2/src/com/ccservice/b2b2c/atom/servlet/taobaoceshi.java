package com.ccservice.b2b2c.atom.servlet;

import com.taobao.api.internal.tmc.Message;
import com.taobao.api.internal.tmc.MessageHandler;
import com.taobao.api.internal.tmc.MessageStatus;
import com.taobao.api.internal.tmc.TmcClient;
import com.taobao.api.response.TrainAgentOrderGetResponse;
import com.taobao.api.internal.toplink.LinkException;

public class taobaoceshi {

    public static void main(String[] args) throws LinkException {

        TrainAgentOrderGetResponse rr = new TrainAgentOrderGetResponse();

        String url = "ws://mc.api.tbsandbox.com/";
        String appkey = "690047";
        String appSecret = "4cc8b1ae51a91e9bf3bdf6bd99277ff7";
        TmcClient client = new TmcClient(url, appkey, appSecret);
        System.out.println("xxx1");
        client.setMessageHandler(new MessageHandler() {
            public void onMessage(Message message, MessageStatus status) {
                try {
                    System.out.println("xxx");
                    System.out.println(message.getContent());

                }
                catch (Exception e) {

                    status.fail();// 回滚

                }
            }
        });
        client.connect();

    }

}
