package com.ccservice.b2b2c.atom.train.idmongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

/**
* MongoHelper
* @author 
*
*/
public class MongoHelper {

    private static String HOST = "120.26.211.188";

    private static int PORT = 27017;

    private static String DataSource = "train";

    private static MongoClient mg = null;

    private static DB db = null;

    private final static MongoHelper instance = new MongoHelper();

    /**
    * MongoDB操作辅助类实体
    * @return
    * @throws Exception
    */
    public static MongoHelper getInstance() throws Exception {
        return instance;
    }

    static {
        try {

            MongoCredential credential = MongoCredential.createScramSha1Credential("sa", "admin",
                    "as111213sa@bj".toCharArray());

            ServerAddress serverAdd = new ServerAddress(HOST, PORT);
            List<MongoCredential> creList = new ArrayList<MongoCredential>();
            creList.add(credential);
            mg = new MongoClient(serverAdd, creList);

            //mg = new MongoClient(HOST, PORT);
            //mg = new Mongo(HOST, PORT);

            db = mg.getDB(DataSource);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    * 获取
    * @param collection
    */
    public static DBCollection getCollection(String collection) {
        return db.getCollection(collection);
    }

    /**
    * ----------------------------------分割线--------------------------------------
    */

    private DBObject map2Obj(Map map) {
        return new BasicDBObject(map);
    }

    /**
    * 插入
    * @param collection
    * @param map
    */
    public void insert(String collection, Map<String, Object> map) {
        try {
            DBObject dbObject = new BasicDBObject(map);
            getCollection(collection).insert(dbObject);
        }
        catch (MongoException e) {
        }
    }

    /**
    * 插入
    * @param collection
    * @param list
    */
    public void insertBatch(String collection, List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        try {
            List<DBObject> listDB = new ArrayList<DBObject>();
            for (int i = 0; i < list.size(); i++) {
                DBObject dbObject = map2Obj(list.get(i));
                listDB.add(dbObject);
            }
            getCollection(collection).insert(listDB);
        }
        catch (MongoException e) {
        }
    }

    /**
    * 删除
    * @param collection
    * @param map
    */
    public void delete(String collection, Map<String, Object> map) {
        DBObject obj = map2Obj(map);
        getCollection(collection).remove(obj);
    }

    /**
     * 删除
     * @param collection
     * @param obj
     */
    public void delete(String collection, DBObject obj) {
        getCollection(collection).remove(obj);
    }

    /**
      * 全部删除
      * @param collection
      * @param map
      */
    public void deleteAll(String collection) {
        List<DBObject> rs = findAll(collection);
        if (rs != null && !rs.isEmpty()) {
            for (int i = 0; i < rs.size(); i++) {
                getCollection(collection).remove(rs.get(i));
            }
        }
    }

    /**
    * 删除
    * @param collection
    * @param list
    */
    public void deleteBatch(String collection, List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            getCollection(collection).remove(map2Obj(list.get(i)));
        }
    }

    /**
    * 计数
    * @param collection
    * @param map
    */
    public long getCount(String collection, Map<String, Object> map) {
        return getCollection(collection).getCount(map2Obj(map));
    }

    /**
    * 计数
    * @param collection
    * @param map
    */
    public long getCount(String collection) {
        return getCollection(collection).find().count();
    }

    /**
    * 修改数据
    * @param collection
    * @param setFields
    * @param whereFields
    */
    public void update(String collection, Map<String, Object> setFields, Map<String, Object> whereFields) {
        DBObject obj1 = map2Obj(setFields);
        DBObject obj2 = map2Obj(whereFields);
        getCollection(collection).updateMulti(obj1, obj2);
    }

    /**
    * 根据ID查找数据
    * @param collection
    * @param _id
    */
    public DBObject findById(String collection, String _id) {
        DBObject obj = new BasicDBObject();
        obj.put("_id", _id);
        return getCollection(collection).findOne(obj);
    }

    /**
    * 获取所有结果
    * @param collection
    */
    public List<DBObject> findAll(String collection) {
        return getCollection(collection).find().toArray();
    }

    /**
    * 获取一个符合条件的结果
    * @param map
    * @param collection
    */
    public DBObject findOne(String collection, Map<String, Object> map) {
        DBCollection coll = getCollection(collection);
        return coll.findOne(map2Obj(map));
    }

    /**
     * 获取一个符合条件的结果
     * @param collection
     * @param key
     * @param value
     */
    public DBObject findOne(String collection, String key, Object value) {
        DBCollection coll = getCollection(collection);
        return coll.findOne(new BasicDBObject(key, value));
    }

    /**
    * 获取所有符合条件的结果，返回List<DBObject>
    * @param <DBObject>
    * @param map
    * @param collection
    * @throws Exception
    */
    public List<DBObject> find(String collection, Map<String, Object> map) throws Exception {
        DBCollection coll = getCollection(collection);
        DBCursor c = coll.find(map2Obj(map));
        if (c != null)
            return c.toArray();
        else
            return null;
    }
}