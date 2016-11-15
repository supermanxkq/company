package com.ccservice.b2b2c.util.activemqpool;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

import com.ccservice.elong.inter.PropertyUtil;

/** 
 * 链接工厂管理类 
 * 自己工厂定义成了单例模式，连接池是静态块进行初始化，具体实现自己看着办 
 */
@SuppressWarnings("deprecation")
public class MQPooledConnectionFactory {
    private static ActiveMQConnectionFactory connectionFactory;

    /**
     * mq地址
     */
    private static final String URL_STRING = PropertyUtil.getValue("activeMQ_url", "Train.properties");

    /**
     * 默认链接池大小
     */
    private static int maximumActive = 10;

    /** 
     * 获得自己创建的链接工厂，这个工厂只初始化一次 
     */
    public static ActiveMQConnectionFactory getMyActiveMQConnectionFactory() {
        if (null == connectionFactory) {
            connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD, URL_STRING);
        }
        return connectionFactory;
    }

    private static PooledConnectionFactory pooledConnectionFactory;

    /**
     * 构建链接池
     */
    static {
        try {
            // 需要创建一个链接工厂然后设置到连接池中  
            ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(
                    ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, URL_STRING);
            // 如果将消息工厂作为属性设置则会有类型不匹配的错误，虽然Spring配置文件中是这么配置的，这里必须在初始化的时候设置进去  
            pooledConnectionFactory = new PooledConnectionFactory(activeMQConnectionFactory);
            // 链接最大活跃数，为了在测试中区别我们使用的到底是不是一个对象和看是否能控制连接数(实际上是会话数)，我们在这里设置为1  
            try {
                //从配置文件读取链接池大小,如果出现异常，使用默认的大小
                maximumActive = Integer.valueOf(PropertyUtil.getValue("maximumActive", "Train.properties"));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            pooledConnectionFactory.setMaximumActive(maximumActive);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 
     * 获得链接池工厂 
     */
    public static PooledConnectionFactory getPooledConnectionFactory() {
        return pooledConnectionFactory;
    }

    /** 
     * 对象回收销毁时停止链接 
     */
    @Override
    protected void finalize() throws Throwable {
        pooledConnectionFactory.stop();
        super.finalize();
    }
}