package com.ccservice.b2b2c.atom.server;

import java.util.Date;

import com.ccservice.b2b2c.util.OcsMethod;
import com.ccservice.elong.inter.PropertyUtil;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

/** * 使用memcached的缓存实用类. * * @author 铁木箱子 * */
public class MemCached {
    protected static MemCached memCached = new MemCached();

    static Integer memCacheFrom;//0自己memcache,1阿里ocs

    // 创建全局的唯一实例     
    protected static MemCachedClient mcc = new MemCachedClient();

    // 设置与缓存服务器的连接池     
    static {
        // 服务器列表和其权重         
        String[] servers = { PropertyUtil.getValue("mcc_serverString", "Train.properties") };
        Integer[] weights = { 3 };
        // 获取socke连接池的实例对象        
        SockIOPool pool = SockIOPool.getInstance();
        // 设置服务器信息         
        pool.setServers(servers);
        pool.setWeights(weights);
        // 设置初始连接数、最小和最大连接数以及最大处理时间        
        pool.setInitConn(50);
        pool.setMinConn(25);
        pool.setMaxConn(1200);
        //        pool.setMaxIdle(1000 * 60 * 60 * 6);
        pool.setMaxIdle(1000 * 60 * 60 * 24);
        //设置主线程睡眠时间，每3秒苏醒一次，维持连接池大小    
        //maintSleep 千万不要设置成30，访问量一大就出问题，单位是毫秒，推荐30000毫秒。
        pool.setMaintSleep(30000);
        // 设置TCP的参数，连接超时等        
        pool.setNagle(false);
        //连接建立后的超时时间    
        pool.setSocketTO(3000);
        //连接建立时的超时时间  
        pool.setSocketConnectTO(0);
        // 初始化连接池        
        pool.initialize();
        // 压缩设置，超过指定大小（单位为K）的数据都会被压缩 
        mcc.setCompressEnable(true);
        mcc.setCompressThreshold(64 * 1024);

    }

    /**      
     * * 保护型构造方法，不允许实例化！     
     *  *     
     *   */
    protected MemCached() {
    }

    /**      
     * * 获取唯一实例.      
     * * @return      
     * */
    public static MemCached getInstance() {
        if (memCached == null) {
            memCached = new MemCached();

        }
        if (memCacheFrom == null) {
            try {
                memCacheFrom = Integer.parseInt(PropertyUtil.getValue("memCacheFrom", "Train.properties"));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return memCached;
    }

    /**      
     * * 添加一个指定的值到缓存中.      
     * * @param key      
     * * @param value      
     * * @return      
     * */
    public boolean add(String key, Object value) {
        if (memCacheFrom == 1) {
            return OcsMethod.getInstance().add(key, value.toString());
        }
        else {
            return mcc.add(key, value);
        }
    }

    public boolean add(String key, Object value, Date expiry) {
        if (memCacheFrom == 1) {
            Long timeout = expiry.getTime() / 1000;
            return OcsMethod.getInstance().add(key, value.toString(), timeout.intValue());
        }
        else {
            return mcc.add(key, value, expiry);
        }
    }

    public boolean replace(String key, Object value) {
        if (memCacheFrom == 1) {
            return OcsMethod.getInstance().replace(key, value.toString());
        }
        else {
            return mcc.replace(key, value);
        }
    }

    public boolean replace(String key, Object value, Date expiry) {
        if (memCacheFrom == 1) {
            Long timeout = expiry.getTime() / 1000;
            return OcsMethod.getInstance().replace(key, value.toString(), timeout.intValue());
        }
        else {
            return mcc.replace(key, value, expiry);
        }
    }

    public boolean delete(String key) {
        if (memCacheFrom == 1) {
            return OcsMethod.getInstance().remove(key);
        }
        else {
            return mcc.delete(key);
        }
    }

    /**      
     * *
     *  根据指定的关键字获取对象.      
     *  * @param key      
     *  * @return      
     *  */
    public Object get(String key) {
        //        System.out.println("memCacheFrom:" + memCacheFrom);
        if (memCacheFrom != null && memCacheFrom == 1) {
            return OcsMethod.getInstance().get(key);
        }
        else {
            return mcc.get(key);
        }
    }
}
