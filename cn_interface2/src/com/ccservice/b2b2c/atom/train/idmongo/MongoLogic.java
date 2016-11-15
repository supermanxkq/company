package com.ccservice.b2b2c.atom.train.idmongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mongodb.DBObject;

public class MongoLogic {

    private String collection = "CustomerUser";

    private String collectionTomas = "PassengerTomas";

    /**
     * 判断列表中是否已经包含相同的身份证号
     * 
     * @param idList
     * @param id
     * @return
     */
    public boolean CheckIdList(List<IDModel> idList, long id) {
        boolean result = false;
        for (int i = 0; i < idList.size(); i++) {
            if (idList.get(i).GetIDNumber() == id) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 判断列表中是否已经包含相同的身份证号,有则返回index，否则返回-1
     * 
     * @param idList
     * @param id
     * @return
     */
    public int GetIdIndex(List<IDModel> idList, long id) {
        int result = -1;
        for (int i = 0; i < idList.size(); i++) {
            if (idList.get(i).GetIDNumber() == id) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * 将String型字符串转化成long型
     * 
     * @param idStr
     * @return
     */
    public long GetLongFromString(String idStr) {
        long result = 0;
        idStr = idStr.toLowerCase();
        if (idStr.length() == 18) {
            if (idStr.endsWith("x")) {
                idStr = idStr.replaceAll("x", "10");
            }
            try {
                result = Long.parseLong(idStr);
            }
            catch (Exception ex) {
                result = 0;
            }
        }
        return result;
    }

    /**
     * 将long型身份证号转化成字符串型
     * 
     * @param id
     * @return
     */
    public String GetStringFromLong(long id) {
        String result = id + "";
        if (result.length() == 19) {
            result = result.substring(0, 17) + "x";
        }
        return result;
    }

    /**
     * 添加一个身份证（新账号添加成功，身份核验成功，调用此方法）（可重复）
     * 
     * @param loginName
     * @param idModel
     * @throws Exception
     */
    public void AddId(String loginName, IDModel idModel) throws Exception {
        Map<String, Object> query = new HashMap<String, Object>();

        if (idModel.GetSupplyAccount() == null || "".equals(idModel.GetSupplyAccount())) {
            idModel.SetSupplyAccount(loginName);
        }

        if (idModel.GetIDType() == 1 && idModel.GetIDNumber() > 0 && !"".equals(idModel.GetRealName())
                && !"".equals(idModel.GetSupplyAccount())) {
            query.put("IDNumber", idModel.GetIDNumber());
            query.put("SupplyAccount", idModel.GetSupplyAccount());
            query.put("version", idModel.get_version());

            DBObject result = MongoHelper.getInstance().findOne("CustomerUser", query);
            if (result == null) {
                query.put("RealName", idModel.GetRealName());
                query.put("IDType", idModel.GetIDType());
                MongoHelper.getInstance().insert(collection, query);
            }
        }
    }

    /**
     * 删除一个身份证（清除一个过期身份证、或判断某身份证已不存在之后调用）
     * 
     * @param loginName
     * @param idModel
     * @throws Exception
     */
    public void DelID(String loginName, long id) throws Exception {
        Map<String, Object> query = new HashMap<String, Object>();

        query.put("IDNumber", id);
        query.put("SupplyAccount", loginName);

        MongoHelper.getInstance().delete(collection, query);

    }

    /**
     * 删除一个12306账户（某12306账户被封禁或被找回时调用，注意超过30常旅不必调用此方法，必须是完全无法使用时才可调用）
     * 
     * @param loginName
     * @throws Exception 
     */
    public void DelAccount(String loginName) throws Exception {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put("SupplyAccount", loginName);
        MongoHelper.getInstance().delete(collection, query);
    }

    /**
     * 重新刷新一个账号下的所有身份证信息（维持账号登录状态，获取常旅列表后调用此方法）
     * 
     * @param loginName
     * @param idList
     * @throws Exception
     */
    public void RefreshMongoByCustomerUser(String loginName, List<IDModel> idList) throws Exception {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put("SupplyAccount", loginName);
        List<DBObject> havedUsers = MongoHelper.getInstance().find(collection, query);

        if (havedUsers != null && havedUsers.size() > 0) {
            for (int i = 0; i < havedUsers.size(); i++) {
                DBObject userItem = havedUsers.get(i);
                int idIndex = GetIdIndex(idList, Long.valueOf(String.valueOf(userItem.get("IDNumber"))));

                if (idIndex == -1)// 在参数中不存在的身份证将被删除
                {
                    MongoHelper.getInstance().delete(collection, userItem);
                }
                else // 已存在的身份证无需重新插入
                {
                    idList.remove(idIndex);
                }
            }
        }

        for (int i = 0; i < idList.size(); i++) // 将新身份证插入MongoDB
        {
            AddId(loginName, idList.get(i));
        }
    }

    public List<DBObject> FindMongoByCustomerUser(String loginName) throws Exception {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put("SupplyAccount", loginName);
        List<DBObject> havedUsers = MongoHelper.getInstance().find(collection, query);
        if (havedUsers != null && havedUsers.size() > 0) {
            //            for (int i = 0; i < havedUsers.size(); i++) {
            //                DBObject userItem = havedUsers.get(i);
            //                int idIndex = GetIdIndex(idList, Long.valueOf(String.valueOf(userItem.get("IDNumber"))));
            //
            //                if (idIndex == -1)// 在参数中不存在的身份证将被删除
            //                {
            //                    MongoHelper.getInstance().delete(collection, userItem);
            //                }
            //                else // 已存在的身份证无需重新插入
            //                {
            //                    idList.remove(idIndex);
            //                }
            //            }
            return havedUsers;
        }
        return new ArrayList<DBObject>();

    }

    /**
     * 返回推荐用户集（key为unCover度，为0表示全覆盖，即本账号包含所有需要的身份证号；
     * value为同一uncover度下所有loginName的ArrayList）
     * 
     * @param idList
     *            (只需要给IDNumber赋值)
     * @return
     * @throws Exception
     */
    public HashMap<Integer, ArrayList<String>> GetCustomUser(List<IDModel> idList) throws Exception {
        HashMap<Integer, ArrayList<String>> result = new HashMap<Integer, ArrayList<String>>();
        HashMap<String, Integer> temp = new HashMap<String, Integer>();

        for (int i = 0; i < idList.size(); i++) {
            Map<String, Object> query = new HashMap<String, Object>();
            query.put("IDNumber", idList.get(i).GetIDNumber());
            List<DBObject> users = MongoHelper.getInstance().find(collection, query);

            for (int j = 0; j < users.size(); j++) {
                String loginName = String.valueOf(users.get(j).get("SupplyAccount"));
                int cover = temp.containsKey(loginName) ? (temp.get(loginName) + 1) : 1;
                temp.put(loginName, cover);
            }
        }

        for (Entry<String, Integer> item : temp.entrySet()) {
            int unCover = idList.size() - item.getValue();
            if (!result.containsKey(unCover)) {
                result.put(unCover, new ArrayList<String>());
            }

            result.get(unCover).add(item.getKey());
        }

        return result;
    }

    /**
     * 添加一个托管账号的乘客
     * 
     * @param IDNumber(long)
     * @param name
     * @throws Exception
     * @time 2016年11月11日 下午6:18:25
     * @author fiend
     */
    public void AddPassengerTomas(long IDNumber, String name) throws Exception {
        Map<String, Object> query = new HashMap<String, Object>();

        if (IDNumber > 0 && name != null && !"".equals(name)) {
            query.put("IDNumber", IDNumber);
            query.put("Name", name);
            DBObject result = MongoHelper.getInstance().findOne(collectionTomas, query);
            if (result == null) {
                MongoHelper.getInstance().insert(collectionTomas, query);
            }
        }
    }

    /**
     * 添加一个托管账号的乘客
     * 
     * @param IDNumber(String)
     * @param name
     * @throws Exception
     * @time 2016年11月11日 下午6:18:25
     * @author fiend
     */
    public void AddPassengerTomasIDString(String IDNumberStr, String name) throws Exception {
        AddPassengerTomas(GetLongFromString(IDNumberStr), name);
    }

    /**
     * 通过证件号获取一个托管账号的乘客姓名
     * 
     * @param IDNumber(long)
     * @return
     * @throws Exception
     * @time 2016年11月11日 下午6:24:38
     * @author fiend
     */
    @SuppressWarnings("deprecation")
    public String GetPassengerTomasName(long IDNumber) throws Exception {
        String name = "";
        Map<String, Object> query = new HashMap<String, Object>();

        if (IDNumber > 0) {
            query.put("IDNumber", IDNumber);
            DBObject findResult = MongoHelper.getInstance().findOne(collectionTomas, query);
            name = findResult != null && findResult.containsKey("Name") ? findResult.get("Name").toString() : "";
        }
        return name;
    }

    /**
     * 通过证件号获取一个托管账号的乘客姓名
     * 
     * @param IDNumberStr(String)
     * @return
     * @throws Exception
     * @time 2016年11月11日 下午6:24:38
     * @author fiend
     */
    public String GetPassengerTomasNameByIDNumberString(String IDNumberStr) throws Exception {
        return GetPassengerTomasName(GetLongFromString(IDNumberStr));
    }

    /**
     * 通过证件号获取一个托管账号的乘客姓名
     * 
     * @param IDNumberStr
     * @return
     * @throws Exception
     * @time 2016年11月11日 下午6:24:38
     * @author fiend
     */
    public void DelPassengerTomasNameByIDNumberString(String IDNumberStr) throws Exception {
        DelPassengerTomasName(GetLongFromString(IDNumberStr));
    }

    /**
     * 通过证件号获取一个托管账号的乘客姓名
     * 
     * @param IDNumber(long)
     * @return
     * @throws Exception
     * @time 2016年11月11日 下午6:24:38
     * @author fiend
     */
    public void DelPassengerTomasName(long IDNumber) throws Exception {
        Map<String, Object> query = new HashMap<String, Object>();
        if (IDNumber > 0) {
            query.put("IDNumber", IDNumber);
            MongoHelper.getInstance().delete(collectionTomas, query);
        }
    }

    public static void main(String[] args) {
        try {
            //            new MongoLogic().AddPassengerTomasIDString("15010219800410303X", "白宝宏");
            //            System.out.println(new MongoLogic().GetPassengerTomasNameByIDNumberString("15010219800410303X"));
            //            new MongoLogic().DelPassengerTomasNameByIDNumberString("15010219800410303X");
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
