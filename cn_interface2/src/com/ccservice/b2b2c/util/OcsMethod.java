package com.ccservice.b2b2c.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;

import com.ccservice.elong.inter.PropertyUtil;

public class OcsMethod {

    private static OcsMethod ocsMethod = new OcsMethod();

    private static MemcachedClient cache = null;

    //    final static String host = "xxxxxxxx.m.yyyyyyyyyy.ocs.aliyuncs.com";//控制台上的“内网地址”
    //    final static String port = "11211"; //默认端口 11211，不用改
    //    final static String username = "xxxxxxxxx";//控制台上的“访问账号”
    //    final static String password = "my_password";//邮件中提供的“密码”
    private static final int TIMEOUT = 60 * 60 * 24;

    static {
        String host = PropertyUtil.getValue("Ocs_host", "Train.properties");
        String port = PropertyUtil.getValue("Ocs_port", "Train.properties");
        String username = PropertyUtil.getValue("Ocs_username", "Train.properties");
        String password = PropertyUtil.getValue("Ocs_password", "Train.properties");

        try {
            AuthDescriptor ad = new AuthDescriptor(new String[] { "PLAIN" }, new PlainCallbackHandler(username,
                    password));

            cache = new MemcachedClient(new ConnectionFactoryBuilder().setProtocol(Protocol.BINARY)
                    .setAuthDescriptor(ad).build(), AddrUtil.getAddresses(host + ":" + port));
            System.out.println("OCS=====================开启成功");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**      
     * * 保护型构造方法，不允许实例化！     
     *  *     
     *   */
    protected OcsMethod() {
    }

    /**      
     * * 获取唯一实例.      
     * * @return      
     * */
    public static OcsMethod getInstance() {
        if (ocsMethod == null) {
            ocsMethod = new OcsMethod();
        }
        return ocsMethod;
    }

    /**
     * 向ocs中添加一条记录
     * @param key
     * @param value
     * @return
     */
    public boolean add(String key, String value) {
        return add(key, value, TIMEOUT);
    }

    /**
     * 向ocs中添加一条记录
     * @param key
     * @param value
     * @param timeout int类型的超时时间
     * @return
     */
    public boolean add(String key, String value, int timeout) {
        try {
            Future<Boolean> isSuccess = cache.set(key, timeout, value);
            return isSuccess.get();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从ocs中查询对应信息
     * @param key
     * @return
     */
    public String get(String key) {
        Object getObject = cache.get(key);
        return getObject == null ? "" : getObject.toString();
    }

    /**
     * 从ocs中移除对应信息
     * @param key
     * @return
     */
    public boolean remove(String key) {
        try {
            Future<Boolean> isSuccess = cache.delete(key);
            return isSuccess.get();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从ocs中替换对应信息
     * @param key
     * @param value
     * @return
     */
    public boolean replace(String key, String value) {
        return replace(key, value, TIMEOUT);
    }

    /**
     * 从ocs中替换对应信息
     * @param key
     * @param value
     * @param timeout int类型的超时时间
     * @return
     */
    public boolean replace(String key, String value, int timeout) {
        try {
            Future<Boolean> isSuccess = cache.replace(key, timeout, value);
            return isSuccess.get();
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        OcsMethod.getInstance().add("1", "2");
    }

}
