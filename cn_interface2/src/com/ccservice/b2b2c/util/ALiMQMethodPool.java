package com.ccservice.b2b2c.util;

import java.util.Enumeration;
import java.util.Vector;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * aliMQ池
 * 
 * @time 2016年8月15日 上午10:37:03
 * @author fiend
 */
public class ALiMQMethodPool {

    private static ALiMQMethodPool aLiMQMethodPool = new ALiMQMethodPool();

    private final int numSize = 50; // 对象池的大小     

    private final int maxSize = 150; // 对象池最大的大小     

    private Vector<AliMQMethod> aliMQMethods = null; //存放对象池中对象

    private final String producerId = PropertyUtil.getValue("ProducerId", "Train.properties");

    private final String accessKey = PropertyUtil.getValue("AccessKey", "Train.properties");

    private final String secretKey = PropertyUtil.getValue("SecretKey", "Train.properties");

    private final String tagName = PropertyUtil.getValue("tagName", "Train.properties");

    private final String keyOrderId = PropertyUtil.getValue("keyOrderId", "Train.properties");

    /**
     * 
     * 私有构造
     */
    private ALiMQMethodPool() {
        //在构造内创建一个连接池
        createPool();
    }

    /**
     * 获取本类唯一对象
     * 
     * @return
     * @time 2016年8月15日 上午10:36:11
     * @author fiend
     */
    public static ALiMQMethodPool getinstance() {
        if (aLiMQMethodPool == null) {
            aLiMQMethodPool = new ALiMQMethodPool();
        }
        return aLiMQMethodPool;
    }

    /**
     * 创建一个对象池
     * 
     * @time 2016年8月15日 上午10:34:09
     * @author fiend
     */
    private synchronized void createPool() {
        // 确保对象池没有创建。如果创建了，保存对象的向量 objects 不会为空     
        if (aliMQMethods != null) {
            return; // 如果己经创建，则返回     
        }

        // 创建保存对象的向量 , 初始时有 0 个元素     
        aliMQMethods = new Vector<AliMQMethod>();
        // 根据 numObjects 中设置的值，循环创建指定数目的对象     
        for (int i = 0; i < numSize; i++) {
            if (aliMQMethods.size() == 0 && this.aliMQMethods.size() < this.maxSize) {
                aliMQMethods.addElement(AliMQMethod.create(producerId, accessKey, secretKey));
            }
        }
    }

    /**
     * 如果对象池数量不足，创建一个
     * 
     * @time 2016年8月15日 上午10:23:07
     * @author fiend
     */
    private synchronized void createOne() {
        if (this.aliMQMethods.size() < this.maxSize) {
            aliMQMethods.addElement(AliMQMethod.create(producerId, accessKey, secretKey));
        }
    }

    /**
     * 获取可用对象
     * 
     * @return
     * @time 2016年8月15日 上午10:33:30
     * @author fiend
     */
    private synchronized AliMQMethod getAliMQMethod() {
        // 确保对象池己被创建     
        if (aliMQMethods == null) {
            return null; // 对象池还没创建，则返回 null     
        }

        AliMQMethod aliMQMethod = getFreeAliMQMethod(); // 获得一个可用的对象     

        // 如果目前没有可以使用的对象，即所有的对象都在使用中     
        while (aliMQMethod == null) {
            wait(250);
            aliMQMethod = getFreeAliMQMethod(); // 重新再试，直到获得可用的对象，如果     
            // getFreeObject() 返回的为 null，则表明创建一批对象后也不可获得可用对象     
        }

        return aliMQMethod;// 返回获得的可用的对象     
    }

    /**
     * 本函数从对象池对象 aliMQMethods 中返回一个可用的的对象，如果   
     * 当前没有可用的对象，则创建几个对象，并放入对象池中。   
     * 如果创建后，所有的对象都在使用中，则返回 null  
     * 
     * @return
     * @time 2016年8月15日 上午10:34:18
     * @author fiend
     */
    private AliMQMethod getFreeAliMQMethod() {

        // 从对象池中获得一个可用的对象     
        AliMQMethod aliMQMethod = findFreeAliMQMethod();

        if (aliMQMethod == null) {
            createOne(); //如果目前对象池中没有可用的对象，创建一些对象     
            // 重新从池中查找是否有可用对象     
            aliMQMethod = findFreeAliMQMethod();

            // 如果创建对象后仍获得不到可用的对象，则返回 null     
            if (aliMQMethod == null) {
                return null;
            }
        }

        return aliMQMethod;
    }

    /**
     * 查找对象池中所有的对象，查找一个可用的对象，   
     * 如果没有可用的对象，返回 null 
     * 
     * @return
     * @time 2016年8月15日 上午10:34:26
     * @author fiend
     */
    @SuppressWarnings("rawtypes")
    private AliMQMethod findFreeAliMQMethod() {

        AliMQMethod obj = null;
        AliMQMethod pObj = null;

        // 获得对象池向量中所有的对象     
        Enumeration enumerate = aliMQMethods.elements();

        // 遍历所有的对象，看是否有可用的对象     
        while (enumerate.hasMoreElements()) {
            pObj = (AliMQMethod) enumerate.nextElement();

            // 如果此对象不忙，则获得它的对象并把它设为忙     
            if (!pObj.isBusy()) {
                obj = pObj;
                pObj.setBusy(true);
                break;
            }
        }
        return obj;
        // 返回找到到的可用对象     
    }

    /**
     * 此函数返回一个对象到对象池中，并把此对象置为空闲。   
     * 所有使用对象池获得的对象均应在不使用此对象时返回它。  
     * 
     * @param aliMQMethod
     * @time 2016年8月15日 上午10:34:34
     * @author fiend
     */

    @SuppressWarnings("rawtypes")
    private void returnAliMQMethod(AliMQMethod aliMQMethod) {

        // 确保对象池存在，如果对象没有创建（不存在），直接返回     
        if (aliMQMethod == null) {
            return;
        }

        AliMQMethod pObj = null;

        Enumeration enumerate = aliMQMethods.elements();

        // 遍历对象池中的所有对象，找到这个要返回的对象对象     
        while (enumerate.hasMoreElements()) {
            pObj = (AliMQMethod) enumerate.nextElement();

            // 先找到对象池中的要返回的对象对象     
            if (aliMQMethod.getKey().equals(pObj.getKey())) {
                // 找到了 , 设置此对象为空闲状态     
                pObj.setBusy(false);
                break;
            }
        }
    }

    /**
     * 关闭对象池中所有的对象，并清空对象池。
     * 
     * @time 2016年8月15日 上午10:34:42
     * @author fiend
     */
    @SuppressWarnings("rawtypes")
    private synchronized void closeObjectPool() {

        // 确保对象池存在，如果不存在，返回     
        if (aliMQMethods == null) {
            return;
        }

        AliMQMethod aliMQMethod = null;

        Enumeration enumerate = aliMQMethods.elements();

        while (enumerate.hasMoreElements()) {

            aliMQMethod = (AliMQMethod) enumerate.nextElement();

            // 如果忙，等 5 秒     
            if (aliMQMethod.isBusy()) {
                wait(5000); // 等 5 秒     
            }

            // 从对象池向量中删除它     
            aliMQMethods.removeElement(aliMQMethod);
        }

        // 置对象池为空     
        aliMQMethods = null;
    }

    /**
     * 使程序等待给定的毫秒数   
     * 
     * @param mSeconds
     * @time 2016年8月15日 上午10:34:49
     * @author fiend
     */
    private void wait(int mSeconds) {
        try {
            Thread.sleep(mSeconds);
        }
        catch (InterruptedException e) {
        }
    }

    /**
     * 通过连接池发送mq消息
     * 
     * @param sendBody
     * @param topicName
     * @throws Exception
     * @time 2016年8月15日 上午10:43:49
     * @author fiend
     */
    public void sendMQ(String sendBody, String topicName) throws Exception {
        AliMQMethod aliMQMethod = getAliMQMethod();
        WriteLog.write("ALiMQMethodPool_sendMQ", aliMQMethod.getKey() + "--->" + sendBody);
        aliMQMethod.sendMessage(sendBody, topicName, producerId, accessKey, secretKey, tagName, keyOrderId);
        returnAliMQMethod(aliMQMethod);
    }
}
