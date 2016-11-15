package com.ccservice.rabbitmq.util; 

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class RabbitMQConnectsBean {
    
    private Connection connection;//   目前链接中先不放值
    
    private boolean use;//是否正在使用
    
    private String key;//key   确认唯一一个链接

    private Channel channel;// 通道
    
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
